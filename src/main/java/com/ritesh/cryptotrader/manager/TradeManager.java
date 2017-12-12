package com.ritesh.cryptotrader.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.cryptotrader.config.CoinConfig;
import com.ritesh.cryptotrader.config.TimeIntervalTickerConfig;
import com.ritesh.cryptotrader.config.TradingStrategyConfig;
import com.ritesh.cryptotrader.config.constants.ForwardCheckConstants;
import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.config.interval.TimeTickerHandler;
import com.ritesh.cryptotrader.price.PriceManager;
import com.ritesh.cryptotrader.price.PriceTickerConsumer;
import com.ritesh.cryptotrader.price.model.PriceOHLCV;
import com.ritesh.cryptotrader.stat.CoinStat;
import com.ritesh.cryptotrader.trading.model.TradeExecuted;
import com.ritesh.cryptotrader.trading.model.TradeTracked;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class TradeManager {

	private static TradeManager tradeManagerInst = null;
	
	//coin stat
	private Map<CoinEnum, CoinStat> coinStatMap = null;
	
	//price
	private Map<CoinEnum, BlockingQueue<Object>> priceTickerQMap = null;
	private Map<CoinEnum, PriceTickerConsumer> priceTickerConsumerMap = null;
	private PriceManager priceManager = null;
	
	//time
	private Map<CoinEnum, Map<TimeIntervalTickerEnum, TimeTickerHandler>> timeTickerHandlerMap = null;
	
	private TradeManager(){
		//
	}
	
	/**
	 * Singleton instance of TradeManager
	 * @return
	 */
	public static TradeManager getInstance(){
		if(tradeManagerInst == null){
			tradeManagerInst = new TradeManager();
		}
		return tradeManagerInst;
	}
	
	/**
	 * Start
	 */
	public void start(){
		//Initialize Coin Stat Data Structure
		initializeCoinStatStructure();
		
		//Initialize price ticker Q map for each coin
		createPriceTickerQMap();
		//Initialize price ticker consumer map for each coin
		createPriceTickerConsumerMap();
		//Start price manager
		priceManager = new PriceManager(priceTickerQMap);
		priceManager.start();
		
		//start time ticker handler
		createTimeTickerHandlersMap();
	}
	
	/**
	 * Stop
	 */
	public void stop(){
		//NOTE: reverse order as in start
		
		//stop time ticker handlers
		for(CoinEnum coin : timeTickerHandlerMap.keySet()){
			for(TimeIntervalTickerEnum timeTicker : timeTickerHandlerMap.get(coin).keySet()){
				timeTickerHandlerMap.get(coin).get(timeTicker).stop();
			}
		}
		
		//Stop Price Manager and price ticker consumers
		priceManager.stop();
		for(CoinEnum coin : priceTickerConsumerMap.keySet()){
			priceTickerConsumerMap.get(coin).stop();
		}
		
	}
	
	/**
	 * Get Price Ticker Q Map (map of coin to ticker Q)
	 */
	public Map<CoinEnum, BlockingQueue<Object>> getPriceTickerQMap(){
		return priceTickerQMap;
	}
	
	/**
	 * Initialize price ticker Message Q for each coin
	 */
	private void createPriceTickerQMap() {
		if (CoinConfig.getCoinsInUse().length != 0) {
			priceTickerQMap = new HashMap<CoinEnum, BlockingQueue<Object>>(CoinConfig.getCoinsInUse().length);
			for (CoinEnum coin : CoinConfig.getCoinsInUse()) {
				BlockingQueue<Object> q = new ArrayBlockingQueue<Object>(20);
				priceTickerQMap.put(coin, q);
			}
		}
	}
	
	/**
	 * Initialize price ticker Consumer for each coin
	 */
	private void createPriceTickerConsumerMap(){
		if (CoinConfig.getCoinsInUse().length != 0) {
			priceTickerConsumerMap = new HashMap<CoinEnum, PriceTickerConsumer>(CoinConfig.getCoinsInUse().length);
			for (CoinEnum coin : CoinConfig.getCoinsInUse()) {
				PriceTickerConsumer priceTickerConsumer = new PriceTickerConsumer(priceTickerQMap.get(coin));
				priceTickerConsumer.start();
				priceTickerConsumerMap.put(coin, priceTickerConsumer);
			}
		}
	}
	
	/**
	 * Initialize time ticker Handlers for each coin for each interval time monitor
	 */
	private void createTimeTickerHandlersMap(){
		if (CoinConfig.getCoinsInUse().length != 0) {
			timeTickerHandlerMap = new HashMap<CoinEnum, Map<TimeIntervalTickerEnum, TimeTickerHandler>>(
					CoinConfig.getCoinsInUse().length);
			for (CoinEnum coin : CoinConfig.getCoinsInUse()) {
				List<TimeIntervalTickerEnum> timeTickerList = 
						TimeIntervalTickerConfig.getTimeIntervalTickersList(coin);
				Map<TimeIntervalTickerEnum, TimeTickerHandler> tickerHandlerMap =
						new HashMap<TimeIntervalTickerEnum, TimeTickerHandler>(timeTickerList.size());
				for(TimeIntervalTickerEnum timeTicker : timeTickerList){
					TimeTickerHandler timeTickerHandler = new TimeTickerHandler(coin, timeTicker);
					timeTickerHandler.start();
					tickerHandlerMap.put(timeTicker, timeTickerHandler);
				}
				timeTickerHandlerMap.put(coin, tickerHandlerMap);
			}
		}
	}
	
	/**
	 * Get Coin Stat Map (map of coin to its current state statistics)
	 */
	public Map<CoinEnum, CoinStat> getCoinStatMap(){
		return coinStatMap;
	}
	
	/**
	 * Initialize Coin Stat Structure for each coin
	 */
	private void initializeCoinStatStructure(){
		if (CoinConfig.getCoinsInUse().length != 0) {
			coinStatMap = new HashMap<CoinEnum, CoinStat>(CoinConfig.getCoinsInUse().length);
			for (CoinEnum coin : CoinConfig.getCoinsInUse()) {
				CoinStat coinStat = new CoinStat();
				
				//fill the initial values
				coinStat.setTradingActive(true);
				coinStat.setAllottedTradingQty(new BigDecimal(0));	//TODO: to be configured
				coinStat.setCurrTickerPrice(null);
				coinStat.setCurrTickerPriceLock(new ReentrantLock());
				
				List<TimeIntervalTickerEnum> timeIntervalTickersList =
						TimeIntervalTickerConfig.getTimeIntervalTickersList(coin);
				Map<TimeIntervalTickerEnum, PriceOHLCV> currOHLCVMap =
						new HashMap<TimeIntervalTickerEnum, PriceOHLCV>(timeIntervalTickersList.size());
				Map<TimeIntervalTickerEnum, ReentrantLock> currOHLCVLockMap =
						new HashMap<TimeIntervalTickerEnum, ReentrantLock>(timeIntervalTickersList.size());
				for(TimeIntervalTickerEnum timeTicker : timeIntervalTickersList){
					//initialize with null - null checked by price ticker consumer to create one
					currOHLCVMap.put(timeTicker, null);
					currOHLCVLockMap.put(timeTicker, new ReentrantLock());
				}
				coinStat.setCurrOHLCVMap(currOHLCVMap);
				coinStat.setCurrOHLCVLockMap(currOHLCVLockMap);
				
				List<Strategy> strategyList = TradingStrategyConfig.getTradingStrategiesList(coin);
				Map<Strategy, Boolean> backFillCompletedMap =
						new HashMap<Strategy, Boolean>(strategyList.size());
				Map<Strategy, List<PriceOHLCV>> pastOHLCVMap =
						new HashMap<Strategy, List<PriceOHLCV>>(strategyList.size());
				Map<Strategy, ReentrantLock> pastOHLCVLockMap =
						new HashMap<Strategy, ReentrantLock>(strategyList.size());
				for(Strategy strat : strategyList){
					backFillCompletedMap.put(strat, false);
					pastOHLCVMap.put(strat, new ArrayList<>(strat.getBackFillNum()));
					pastOHLCVLockMap.put(strat, new ReentrantLock());
				}
				coinStat.setBackfillCompletedMap(backFillCompletedMap);
				coinStat.setPastOHLCVMap(pastOHLCVMap);
				coinStat.setPastOHLCVLockMap(pastOHLCVLockMap);

				Map<Strategy, IStrategyHandler> strategyHandlersMap = 
						new HashMap<Strategy, IStrategyHandler>(strategyList.size());
				Map<Strategy, List<TradeTracked>> trackedTradesMap = 
						new HashMap<Strategy, List<TradeTracked>>(strategyList.size());
				Map<Strategy, List<TradeExecuted>> executedTradesMap = 
						new HashMap<Strategy, List<TradeExecuted>>(strategyList.size());
				for(Strategy strat : strategyList){
					IStrategyHandler strategyHandler = 
							TradingStrategyConfig.getStrategyHandlerInstance(strat);
					strategyHandlersMap.put(strat, strategyHandler);
					trackedTradesMap.put(strat, new ArrayList<>(strat.getForwardCheckNum()));
					executedTradesMap.put(strat, new ArrayList<>());
				}
				coinStat.setStrategyHandlersMap(strategyHandlersMap);
				coinStat.setTrackedTradesMap(trackedTradesMap);
				coinStat.setExecutedTradesMap(executedTradesMap);;
				
				coinStatMap.put(coin, coinStat);
			}
		}
	}
}
