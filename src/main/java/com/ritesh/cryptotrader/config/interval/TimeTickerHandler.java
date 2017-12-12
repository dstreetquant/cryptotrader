package com.ritesh.cryptotrader.config.interval;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.cryptotrader.config.TimeIntervalTickerConfig;
import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.manager.TradeManager;
import com.ritesh.cryptotrader.price.model.PriceOHLCV;
import com.ritesh.cryptotrader.stat.CoinStat;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class TimeTickerHandler implements IntervalMonitorObserver {

	private TimeIntervalTickerEnum timeTicker = null;
	private CoinEnum coin = null;
	private IntervalMonitorSubject monitor = null;
	private BlockingQueue<String> notifQ = null;
	private TimeTickerNotifyRunnable timeTickerNotifyRunnable = null;
	private static boolean running = true;
	
	public TimeTickerHandler(CoinEnum coin, TimeIntervalTickerEnum timeTicker){
		this.coin = coin;
		this.timeTicker = timeTicker;
		this.monitor = TimeIntervalTickerConfig.getIntervalMonitorInstance(timeTicker);
		this.notifQ = new ArrayBlockingQueue<String>(10);
	}
	
	/**
	 * Start the Time Ticker handler
	 */
	public void start(){
		timeTickerNotifyRunnable = new TimeTickerNotifyRunnable();
		Thread th = new Thread(timeTickerNotifyRunnable);
		th.start();
		
		//register and start the time interval monitor
		this.monitor.register(this);
		this.monitor.start();
	}
	
	/**
	 * Stop the time ticker handler
	 */
	public void stop(){
		running = false;
		this.monitor.unregister(this);
		this.monitor.stop();
	}
	
	@Override
	public void notifyTimeTickedUpdate(String time) {
		try {
			notifQ.put(time);
		} catch (InterruptedException e) {
			System.out.println("TimeTickerHandler.notifyTimeTickedUpdate(): ERROR: "
					+ "InterruptedException: [" + coin + "][" + timeTicker + "][" + time + "]");
		}
	}
	
	/**
	 * Thread to Handle time interval tick
	 * @author rghosh
	 */
	class TimeTickerNotifyRunnable implements Runnable{

		@Override
		public void run() {
			while(running){
				try {
					String time = notifQ.take();
					
					//STEP1: Update past OHLC data
					Map<CoinEnum, CoinStat> coinStatMap = 
							TradeManager.getInstance().getCoinStatMap();
					for(CoinEnum coin : coinStatMap.keySet()){
						CoinStat coinStat = coinStatMap.get(coin);
						Map<Strategy, List<PriceOHLCV>> pastOHLCVMap = coinStat.getPastOHLCVMap();
						Map<Strategy, ReentrantLock> pastOHLCVLockMap = coinStat.getPastOHLCVLockMap();
						Map<TimeIntervalTickerEnum, PriceOHLCV> currOHLCVMap = coinStat.getCurrOHLCVMap();
						Map<TimeIntervalTickerEnum, ReentrantLock> currOHLCVLockMap = coinStat.getCurrOHLCVLockMap();
						
						for(Strategy strat : pastOHLCVLockMap.keySet()){
							TimeIntervalTickerEnum stratTimeTicker = strat.getTimeTicker();
							if(timeTicker.equals(stratTimeTicker)){
								ReentrantLock pastOHLCVLock = pastOHLCVLockMap.get(strat);
								pastOHLCVLock.lock();
								List<PriceOHLCV> pastOHLCV = pastOHLCVMap.get(strat);
								pastOHLCVLock.unlock();
								
								ReentrantLock currOHLCVLock = currOHLCVLockMap.get(strat);
								currOHLCVLock.lock();
								//Get currOHLCV and reset as null
								PriceOHLCV currOHLCV = currOHLCVMap.get(timeTicker);
								currOHLCVMap.put(timeTicker, null);
								currOHLCVLock.unlock();
								
								if(coinStat.getBackfillCompletedMap().get(strat)){
									//Backfill already completed - remove first
									pastOHLCVLock.lock();
									pastOHLCV.remove(0);
									pastOHLCVLock.unlock();
								} else{
									//Backfill not done
									System.out.println("TimeTickerHandler.TimeTickerNotifyRunnable.run(): "
											+ "Yet to complete Backfill, pastOHLCV size: " + pastOHLCV.size());
								}
								
								//add to pastOHLCV list
								if(currOHLCV != null){
									pastOHLCVLock.lock();
									currOHLCV.setTime(time);
									pastOHLCV.add(currOHLCV);
									pastOHLCVLock.unlock();
								}
								if(pastOHLCV.size() == strat.getBackFillNum()){
									System.out.println("TimeTickerHandler.TimeTickerNotifyRunnable.run(): "
											+ "Backfill completed");
									coinStat.getBackfillCompletedMap().put(strat, true);
								}
								
								if(coinStat.getBackfillCompletedMap().get(strat)){
									//STEP2: Notify strategy to Order check and trade
									coinStat.getStrategyHandlersMap().get(strat).notifyTradeTimeTicked(time);
								}
							}
						}
					}
					
				} catch (InterruptedException e) {
					System.out.println("TimeTickerHandler.TimeTickerNotifyRunnable.run(): ERROR: "
							+ "InterruptedException: [" + coin + "][" + timeTicker + "]");
				}
			}
		}
	}
}
