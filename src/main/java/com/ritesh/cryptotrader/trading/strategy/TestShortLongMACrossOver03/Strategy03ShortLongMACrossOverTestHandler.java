package com.ritesh.cryptotrader.trading.strategy.TestShortLongMACrossOver03;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.ritesh.cryptotrader.config.ExchangeConfig;
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

public class Strategy03ShortLongMACrossOverTestHandler implements IStrategyHandler{
	
	private Strategy strategy = null;
	private CoinStat coinStat = null;
	
	private BlockingQueue<String> timeTickerQ = null;
	private TimeTickerRunnable timeTickerRunnable = null;
	private static boolean timeTickerRunning = true;
	
	private BlockingQueue<TickerPrice> priceTickerQ = null;
	private PriceTickerRunnable priceTickerRunnable = null;
	private static boolean priceTickerRunning = true;
	
	private static final String SIGNAL_BUY_STRING = "BUY";
	private static final String SIGNAL_SELL_STRING = "SELL";
	
	public Strategy03ShortLongMACrossOverTestHandler(Strategy strategy){
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
			System.out.println("Strategy03ShortLongMACrossOverTestHandler.notifyTradeTimeTicked(): ERROR: "
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
						coinStat.getPastOHLCVLockMap().get(strategy).lock();
						List<PriceOHLCV> priceOhlcvList = coinStat.getPastOHLCVMap().get(strategy);
						PyResult pyResult = getPyResult(priceOhlcvList);
						coinStat.getPastOHLCVLockMap().get(strategy).unlock();
						
						if(pyResult != null){
							handleCurrentTrackedTrades(time, pyResult);
							handleNewTrade(time, pyResult);
						} else{
							System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.run(): ERROR: "
									+ "PyResult is null");
						}
					} else{
						System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.run(): "
								+ "WARNING: Backfill NOT COMPLETED still now !!!");
					}
				} catch (InterruptedException e) {
					System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.run(): ERROR: "
							+ "InterruptedException for getting time from Q for Strategy: [" + strategy.getStrategyName() + "]");
				}
			}
		}
		
		/**
		 * Square off if any existing trade at this time
		 * @param time
		 * @param pyResult
		 */
		private void handleCurrentTrackedTrades(String time, PyResult pyResult){
			List<TradeTracked> trackedTradesList = coinStat.getTrackedTradesMap().get(strategy);
			
			//Handle existing trades in track if any
			if(trackedTradesList.size() > 1){
				//More than one tracked trade - should not be
				System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleTrades(): ERROR: "
						+ " More than 1 Tracked Trades in List: " + trackedTradesList);
			} else if(trackedTradesList.size() == 1){
				//Only one tracked trade
				TradeTracked tradeTracked = trackedTradesList.get(0);
				//Only Long side supported
				if(tradeTracked.getTradeType() == TradeType.LONG 
						&& pyResult.getTradeType() == TradeType.LONG_SQUAREOFF){
					TradeExecuted tradeExecuted = placeTrade(TradeType.LONG_SQUAREOFF, null, tradeTracked.getQty());
					if(tradeExecuted != null){
						System.out
								.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleTrades(): "
										+ "Tracked Trade Squared off, removing from tracked list");
						trackedTradesList.remove(0);
					}
				}
			} else{
				//No tracked trade
			}
		}
		
		/**
		 * Take new position
		 * @param time
		 * @param pyResult
		 */
		private void handleNewTrade(String time, PyResult pyResult) {
			if (pyResult != null) {
				//Only Long Supported
				if (pyResult.getTradeType() == TradeType.LONG) {
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

						//no predetermined Target/SL/Squareoff Time
						tradeTracked.setTargetPrice(null);
						tradeTracked.setSlPrice(null);
						tradeTracked.setSquareOffTime(null);
						System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): "
								+ "Adding trade to coinstat TrackedTradesMap: " + tradeTracked);
						coinStat.getTrackedTradesMap().get(strategy).add(tradeTracked);
					} else{
						System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
								+ "tradeExecuted is null !!!");
					}
				} else{
					System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
							+ " Invalid pyResult Trade Type: " + pyResult.getTradeType());
				}
			} else{
				System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.handleNewTrade(): ERROR: "
						+ " Py Result is null !!!");
			}
		}

		/**
		 * Get Strategy Result
		 * @param priceOhlcvList
		 * @return Python Result
		 */
		private PyResult getPyResult(List<PriceOHLCV> priceOhlcvList) {
			PyResult pyResult = null;
			
			List<BigDecimal> clPricesList = new ArrayList<BigDecimal>();
			for(PriceOHLCV priceOHLCV : priceOhlcvList){
				clPricesList.add(priceOHLCV.getClose());
			}
			
			String signal = PySignalHandler.getSignal(clPricesList);
			if(signal != null){
				pyResult = new PyResult();
				pyResult.setEnterPrice(priceOhlcvList.get(priceOhlcvList.size()-1).getClose());
				pyResult.setSlPrice(null);
				pyResult.setTargetPrice(null);
				if(signal.equals(SIGNAL_BUY_STRING)){
					pyResult.tradeType = TradeType.LONG;
				} else if(signal.equals(SIGNAL_SELL_STRING)){
					pyResult.tradeType = TradeType.LONG_SQUAREOFF;
				} else{
					System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.getPyResult()"
							+ ": ERROR: Invalid Py Signal: " + signal);
				}
			} else{
				System.out.println("Strategy03ShortLongMACrossOverTestHandler.TimeTickerRunnable.getPyResult()"
						+ ": ERROR: Signal is null !!!");
			}
			
			return pyResult;
		}
	}

	@Override
	public void notifyPriceTicked(TickerPrice tickerPrice) {
		try {
			priceTickerQ.put(tickerPrice);
		} catch (InterruptedException e) {
			System.out.println("Strategy03ShortLongMACrossOverTestHandler.notifyPriceTicked(): ERROR: "
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
					//Nothing to do with the ticker Price - No interim squareoff in this strategy
				} catch (InterruptedException e) {
					System.out.println("Strategy03ShortLongMACrossOverTestHandler.PriceTickerRunnable.run(): ERROR: "
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
		System.out.println("Strategy03ShortLongMACrossOverTestHandler.placeTrade(): Executing Trade 1st time");
		TradeExecuted tradeExecuted = 
				ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
		if(tradeExecuted == null){
			System.out.println("Strategy03ShortLongMACrossOverTestHandler.placeTrade(): Executing Trade 2nd time");
			tradeExecuted = 
					ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
			if(tradeExecuted == null){
				System.out.println("Strategy03ShortLongMACrossOverTestHandler.placeTrade(): Executing Trade 3rd time");
				tradeExecuted = 
						ExchangeConfig.getTradeExecutionHandler().executeTrade(tradeToExecute);
			}
		}
		if(tradeExecuted != null){
			System.out.println("Strategy03ShortLongMACrossOverTestHandler.placeTrade(): Adding executed trade to coinstat ExecutedTradesMap");
			//Add to Book keeping map of the orders
			coinStat.getExecutedTradesMap().get(strategy).add(tradeExecuted);
		}
		return tradeExecuted;
	}
}
