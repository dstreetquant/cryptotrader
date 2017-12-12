package com.ritesh.cryptotrader.trading.strategy.TestMACrossOver02;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.ritesh.cryptotrader.config.ExchangeConfig;
import com.ritesh.cryptotrader.config.TimeIntervalTickerConfig;
import com.ritesh.cryptotrader.config.interval.IntervalMonitorSubject;
import com.ritesh.cryptotrader.manager.TradeManager;
import com.ritesh.cryptotrader.price.model.PriceOHLCV;
import com.ritesh.cryptotrader.price.model.TickerPrice;
import com.ritesh.cryptotrader.stat.CoinStat;
import com.ritesh.cryptotrader.trading.model.TradeExecuted;
import com.ritesh.cryptotrader.trading.model.TradeToExecute;
import com.ritesh.cryptotrader.trading.model.TradeTracked;
import com.ritesh.cryptotrader.trading.model.TradeType;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class Strategy02MACrossOverTestHandler implements IStrategyHandler{
	
	private String TARGET_PERC = "0.01";
	private String SL_PERC = "0.005";

	private Strategy strategy = null;
	private CoinStat coinStat = null;
	
	private BlockingQueue<String> timeTickerQ = null;
	private TimeTickerRunnable timeTickerRunnable = null;
	private static boolean timeTickerRunning = true;
	
	private BlockingQueue<TickerPrice> priceTickerQ = null;
	private PriceTickerRunnable priceTickerRunnable = null;
	private static boolean priceTickerRunning = true;
	
	public Strategy02MACrossOverTestHandler(Strategy strategy){
		this.strategy = strategy;
		this.coinStat = TradeManager.getInstance().getCoinStatMap().get(strategy.getCoin());
		
		this.timeTickerQ = new ArrayBlockingQueue<String>(10);
		this.timeTickerRunnable = new TimeTickerRunnable();
		Thread th = new Thread(timeTickerRunnable);
		th.start();
		
		this.priceTickerQ = new ArrayBlockingQueue<TickerPrice>(10);
		this.priceTickerRunnable = new PriceTickerRunnable();
		Thread th1 = new Thread(priceTickerRunnable);
		th1.start();
	}

	@Override
	public void notifyTradeTimeTicked(String time) {
		try {
			timeTickerQ.put(time);
		} catch (InterruptedException e) {
			System.out.println("Strategy02MACrossOverTestHandler.notifyTradeTimeTicked(): ERROR: "
					+ "InterruptedException for putting time into Q for Strategy: [" + strategy.getStrategyName() + "]");
		}
	}
	
	/**
	 * Thread to handle time notified
	 * @author rghosh
	 */
	class TimeTickerRunnable implements Runnable{

		@Override
		public void run() {
			while(timeTickerRunning){
				try {
					String time = timeTickerQ.take();
					
					if(coinStat.getBackfillCompletedMap().get(strategy)){
						//Backfill completed
						handleCurrentTrackedTrades(time);
						handleNewTrade(time);
					} else{
						System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.run(): "
								+ "WARNING: Backfill NOT COMPLETED still now !!!");
					}
				} catch (InterruptedException e) {
					System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.run(): ERROR: "
							+ "InterruptedException for getting time from Q for Strategy: [" + strategy.getStrategyName() + "]");
				}
			}
		}
		
		/**
		 * Square off if any existing trade at this time
		 * @param time
		 */
		private void handleCurrentTrackedTrades(String time){
			List<TradeTracked> trackedTradesList = coinStat.getTrackedTradesMap().get(strategy);
			TradeTracked tradeTrackedToRemove = null;
			for(TradeTracked tradeTracked : trackedTradesList){
				if(tradeTracked.getSquareOffTime().equals(time)){
					TradeExecuted tradeExecuted = null;
					if(tradeTracked.getTradeType() == TradeType.LONG){
						System.out.println(
								"Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleCurrentTrackedTrades(): LONG SQUAREOFF");
						tradeExecuted = placeTrade(TradeType.LONG_SQUAREOFF, null, tradeTracked.getQty());
					} else if(tradeTracked.getTradeType() == TradeType.SHORT){
						System.out.println(
								"Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleCurrentTrackedTrades(): SHORT COVER");
						tradeExecuted = placeTrade(TradeType.SHORT_COVER, null, tradeTracked.getQty());
					}
					if(tradeExecuted != null){
						//mark for removal from track list
						System.out.println(
								"Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleCurrentTrackedTrades(): Marking trade for removal");
						tradeTrackedToRemove = tradeTracked;
					}
				}
			}
			
			if(tradeTrackedToRemove != null){
				//remove from list
				System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleCurrentTrackedTrades(): Removing from trackedTradesList");
				trackedTradesList.remove(tradeTrackedToRemove);
			}
		}
		
		/**
		 * Take new position
		 * @param time
		 */
		private void handleNewTrade(String time) {
			coinStat.getPastOHLCVLockMap().get(strategy).lock();
			List<PriceOHLCV> priceOhlcvList = coinStat.getPastOHLCVMap().get(strategy);
			PyResult pyResult = getPyResult(priceOhlcvList);
			coinStat.getPastOHLCVLockMap().get(strategy).unlock();

			if (pyResult != null) {
				if (pyResult.getTradeType() == TradeType.LONG || pyResult.getTradeType() == TradeType.SHORT) {
					TradeExecuted tradeExecuted = placeTrade(pyResult.getTradeType(), pyResult.getEnterPrice(),
							strategy.getStrategyTradingQty());
					if (tradeExecuted != null) {
						TradeTracked tradeTracked = new TradeTracked();
						tradeTracked.setCoin(strategy.getCoin());
						tradeTracked.setOrderId(tradeExecuted.getOrderId());
						tradeTracked.setTradeType(tradeExecuted.getTradeType());
						tradeTracked.setQty(tradeExecuted.getQty());
						tradeTracked.setDate(tradeExecuted.getDate());
						tradeTracked.setTime(tradeExecuted.getTime());
						tradeTracked.setExchangeTimeStamp(tradeExecuted.getExchangeTimeStamp());

						tradeTracked.setTargetPrice(pyResult.getEnterPrice()
								.add(pyResult.getEnterPrice().multiply(new BigDecimal(TARGET_PERC))));
						tradeTracked.setSlPrice(pyResult.getEnterPrice()
								.subtract(pyResult.getEnterPrice().multiply(new BigDecimal(SL_PERC))));
						IntervalMonitorSubject intMonSub = TimeIntervalTickerConfig
								.getIntervalMonitorInstance(strategy.getTimeTicker());
						tradeTracked.setSquareOffTime(intMonSub.getTimeAtOffset(time, strategy.getForwardCheckNum()));
						System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): "
								+ "Adding trade to coinstat TrackedTradesMap: " + tradeTracked);
						coinStat.getTrackedTradesMap().get(strategy).add(tradeTracked);
					} else{
						System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
								+ "tradeExecuted is null !!!");
					}
				} else{
					System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
							+ " Invalid pyResult Trade Type: " + pyResult.getTradeType());
				}
			} else{
				System.out.println("Strategy02MACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
						+ " Py Result is null !!!");
			}
		}

		/**
		 * Get Strategy Result
		 * @param priceOhlcvList
		 * @return Python Result
		 */
		private PyResult getPyResult(List<PriceOHLCV> priceOhlcvList) {
			return null;
		}
	}

	@Override
	public void notifyPriceTicked(TickerPrice tickerPrice) {
		try {
			priceTickerQ.put(tickerPrice);
		} catch (InterruptedException e) {
			System.out.println("Strategy02MACrossOverTestHandler.notifyPriceTicked(): ERROR: "
					+ "InterruptedException for putting tickerPrice into Q for Strategy: [" + strategy.getStrategyName() + "]");
		}
	}
	
	/**
	 * Thread to handle price notified
	 * @author rghosh
	 */
	class PriceTickerRunnable implements Runnable{

		@Override
		public void run() {
			while(priceTickerRunning){
				try {
					TickerPrice tickerPrice = priceTickerQ.take();
					
					List<TradeTracked> trackedTradesList = coinStat.getTrackedTradesMap().get(strategy);
					TradeTracked tradeTrackedToRemove = null;
					for(TradeTracked tradeTracked : trackedTradesList){
						TradeExecuted tradeExecuted = null;
						if(tradeTracked.getTradeType() == TradeType.LONG && 
								tickerPrice.getLastPrice().compareTo(tradeTracked.getSlPrice()) < 0){
							//LONG - SL Hit
							System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): LONG - SL Hit");
							tradeExecuted = 
									placeTrade(TradeType.LONG_SQUAREOFF, tradeTracked.getSlPrice(), tradeTracked.getQty());
						} else if(tradeTracked.getTradeType() == TradeType.LONG && 
								tickerPrice.getLastPrice().compareTo(tradeTracked.getTargetPrice()) > 0){
							//LONG - Target Hit
							System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): LONG - Target Hit");
							tradeExecuted = 
									placeTrade(TradeType.LONG_SQUAREOFF, tradeTracked.getTargetPrice(), tradeTracked.getQty());
						} else if(tradeTracked.getTradeType() == TradeType.SHORT && 
								tickerPrice.getLastPrice().compareTo(tradeTracked.getSlPrice()) > 0){
							//SHORT - SL Hit
							System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): SHORT - SL Hit");
							tradeExecuted = 
									placeTrade(TradeType.SHORT_COVER, tradeTracked.getSlPrice(), tradeTracked.getQty());
						} else if(tradeTracked.getTradeType() == TradeType.SHORT && 
								tickerPrice.getLastPrice().compareTo(tradeTracked.getTargetPrice()) < 0){
							//SHORT - Target Hit
							System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): SHORT - Target Hit");
							tradeExecuted = 
									placeTrade(TradeType.SHORT_COVER, tradeTracked.getTargetPrice(), tradeTracked.getQty());
						}
						
						//mark for removal from track list
						if(tradeExecuted != null){
							System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): marking tracked trade for removal");
							tradeTrackedToRemove = tradeTracked;
						}
					}
					
					//remove from list
					if(tradeTrackedToRemove != null){
						System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): Removing from trackedTradesList");
						trackedTradesList.remove(tradeTrackedToRemove);
					}
				} catch (InterruptedException e) {
					System.out.println("Strategy02MACrossOverTestHandler.PriceTickerRunnable.run(): ERROR: "
							+ "InterruptedException for getting price from Q for Strategy: [" + strategy.getStrategyName() + "]");
				}
			}
		}
	}
	
	/**
	 * trade place routine
	 * @param tradeType
	 * @param price
	 * @param qty
	 * @return TradeExecuted entity
	 */
	private TradeExecuted placeTrade(TradeType tradeType, BigDecimal price, BigDecimal qty){
		TradeToExecute tradeToExecute = new TradeToExecute();
		tradeToExecute.setCoin(strategy.getCoin());
		tradeToExecute.setTradeType(tradeType);
		tradeToExecute.setPrice(price);
		tradeToExecute.setQty(qty);
		
		//Try for 3 times to place the order
		System.out.println("Strategy02MACrossOverTestHandler.placeTrade(): Executing Trade 1st time");
		TradeExecuted tradeExecuted = 
				ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
		if(tradeExecuted == null){
			System.out.println("Strategy02MACrossOverTestHandler.placeTrade(): Executing Trade 2nd time");
			tradeExecuted = 
					ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
			if(tradeExecuted == null){
				System.out.println("Strategy02MACrossOverTestHandler.placeTrade(): Executing Trade 3rd time");
				tradeExecuted = 
						ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
			}
		}
		if(tradeExecuted != null){
			System.out.println("Strategy02MACrossOverTestHandler.placeTrade(): Adding executed trade to coinstat ExecutedTradesMap");
			//Add to Book keeping map of the orders
			coinStat.getExecutedTradesMap().get(strategy).add(tradeExecuted);
		}
		return tradeExecuted;
	}
}
