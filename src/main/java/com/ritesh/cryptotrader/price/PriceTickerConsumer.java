package com.ritesh.cryptotrader.price;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.manager.TradeManager;
import com.ritesh.cryptotrader.price.model.PriceOHLCV;
import com.ritesh.cryptotrader.price.model.TickerPrice;
import com.ritesh.cryptotrader.stat.CoinStat;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class PriceTickerConsumer {
	private BlockingQueue<Object> priceTickerConsumerQ = null;
	private PriceTickerConsumerRunnable priceTickerConsumerRunnable = null;
	private static boolean running = true;

	public PriceTickerConsumer(BlockingQueue<Object> priceTickerConsumerQ){
		this.priceTickerConsumerQ = priceTickerConsumerQ;
	}

	/**
	 * Start the Price Ticker Consumer
	 */
	public void start(){
		priceTickerConsumerRunnable = new PriceTickerConsumerRunnable();
		Thread th = new Thread(priceTickerConsumerRunnable);
		th.start();
	}
	
	/**
	 * Stop the Price Ticker Consumer
	 */
	public void stop(){
		running = false;
	}
	
	/**
	 * Thread to consume from price Ticker Q
	 * @author rghosh
	 */
	class PriceTickerConsumerRunnable implements Runnable{
		
		@Override
		public void run() {
			while(running){
				try {
					// listen for any price ticker command in Q
					Object command = priceTickerConsumerQ.take();
					if(command instanceof PriceTickerCommand){
						PriceTickerCommand priceTickerCommand = (PriceTickerCommand)command;
						if(priceTickerCommand.getCommand().equals(PriceConstants.PRICE_TICKER)){
							TickerPrice tickerPrice = priceTickerCommand.getTickerPrice();
							
							//STEP1: Update Current Ticker Price
							CoinStat coinStat = 
									TradeManager.getInstance().getCoinStatMap().get(tickerPrice.getCoin());
							coinStat.getCurrTickerPriceLock().lock();
							coinStat.setCurrTickerPrice(tickerPrice);
							coinStat.getCurrTickerPriceLock().unlock();
							
							//STEP2: Notify strategy for Target/SL functionality
							Map<Strategy, IStrategyHandler> strategyHandlersMap = 
									coinStat.getStrategyHandlersMap();
							for(Strategy strat : strategyHandlersMap.keySet()){
								strategyHandlersMap.get(strat).notifyPriceTicked(tickerPrice);
							}
							
							//STEP3: Update current OHLCV for all time intervals
							Map<TimeIntervalTickerEnum, ReentrantLock> currOHLCVLockMap =
									coinStat.getCurrOHLCVLockMap();
							Map<TimeIntervalTickerEnum, PriceOHLCV> currOHLCVMap =
									coinStat.getCurrOHLCVMap();
							for(TimeIntervalTickerEnum timeTicker : currOHLCVLockMap.keySet()){
								currOHLCVLockMap.get(timeTicker).lock();
								BigDecimal lastPrice = tickerPrice.getLastPrice();
								if (lastPrice != null) {
									PriceOHLCV ohlcv = currOHLCVMap.get(timeTicker);
									if (ohlcv == null) {
										ohlcv = new PriceOHLCV();
										ohlcv.setDate(tickerPrice.getDate());
										ohlcv.setTime(tickerPrice.getTime());
										ohlcv.setOpen(lastPrice);
										ohlcv.setHigh(lastPrice);
										ohlcv.setLow(lastPrice);
										ohlcv.setClose(lastPrice);
										ohlcv.setVol(tickerPrice.getVolume());
										currOHLCVMap.put(timeTicker, ohlcv);
									} else {
										ohlcv.setDate(tickerPrice.getDate());
										ohlcv.setTime(tickerPrice.getTime());

										// high
										if (lastPrice.compareTo(ohlcv.getHigh()) == 1) {
											ohlcv.setHigh(lastPrice);
										}
										// low
										if (lastPrice.compareTo(ohlcv.getLow()) == -1) {
											ohlcv.setLow(lastPrice);
										}
										// close
										ohlcv.setClose(lastPrice);
										// vol
										if (ohlcv.getVol() == null || ohlcv.getVol() == new BigDecimal("0")) {
											ohlcv.setVol(tickerPrice.getVolume());
										} else {
											ohlcv.setVol(ohlcv.getVol().add(tickerPrice.getVolume()));
										}
									}
								} else{
									System.out.println("PriceTickerConsumer.PriceTickerConsumerRunnable.run(): lastPrice is null");
								}
								currOHLCVLockMap.get(timeTicker).unlock();
							}
						} else{
							System.out.println("PriceTickerConsumer.PriceTickerConsumerRunnable.run(): "
									+ "ERROR: Not a command of PRICE_TICKER");
						}
					} else{
						System.out.println("PriceTickerConsumer.PriceTickerConsumerRunnable.run(): "
								+ "ERROR: Not a priceTickerCommand");
					}
				} catch (InterruptedException e) {
					System.out.println("PriceTickerConsumer.PriceTickerConsumerRunnable.run(): "
							+ "ERROR: InterruptedException in take from priceTickerConsumerQ");
				}
			}
		}
	}
}
