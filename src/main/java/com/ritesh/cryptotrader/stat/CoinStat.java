package com.ritesh.cryptotrader.stat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.price.model.PriceOHLCV;
import com.ritesh.cryptotrader.price.model.TickerPrice;
import com.ritesh.cryptotrader.trading.model.TradeExecuted;
import com.ritesh.cryptotrader.trading.model.TradeTracked;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class CoinStat {

	//NOTE: Set initial values in TradeManager
	private boolean tradingActive;
	private BigDecimal allottedTradingQty;
	private TickerPrice currTickerPrice;
	private ReentrantLock currTickerPriceLock;
	private Map<TimeIntervalTickerEnum, PriceOHLCV> currOHLCVMap;
	private Map<TimeIntervalTickerEnum, ReentrantLock> currOHLCVLockMap;
	private Map<Strategy, Boolean> backfillCompletedMap;
	private Map<Strategy, List<PriceOHLCV>> pastOHLCVMap;
	private Map<Strategy, ReentrantLock> pastOHLCVLockMap;
	private Map<Strategy, IStrategyHandler> strategyHandlersMap;
	private Map<Strategy, List<TradeTracked>> trackedTradesMap;
	private Map<Strategy, List<TradeExecuted>> executedTradesMap;
	
	public boolean isTradingActive() {
		return tradingActive;
	}
	public void setTradingActive(boolean tradingActive) {
		this.tradingActive = tradingActive;
	}
	public BigDecimal getAllottedTradingQty() {
		return allottedTradingQty;
	}
	public void setAllottedTradingQty(BigDecimal allottedTradingQty) {
		this.allottedTradingQty = allottedTradingQty;
	}
	public TickerPrice getCurrTickerPrice() {
		return currTickerPrice;
	}
	public void setCurrTickerPrice(TickerPrice currTickerPrice) {
		this.currTickerPrice = currTickerPrice;
	}
	public ReentrantLock getCurrTickerPriceLock() {
		return currTickerPriceLock;
	}
	public void setCurrTickerPriceLock(ReentrantLock currTickerPriceLock) {
		this.currTickerPriceLock = currTickerPriceLock;
	}
	public Map<TimeIntervalTickerEnum, PriceOHLCV> getCurrOHLCVMap() {
		return currOHLCVMap;
	}
	public void setCurrOHLCVMap(Map<TimeIntervalTickerEnum, PriceOHLCV> currOHLCVMap) {
		this.currOHLCVMap = currOHLCVMap;
	}
	public Map<TimeIntervalTickerEnum, ReentrantLock> getCurrOHLCVLockMap() {
		return currOHLCVLockMap;
	}
	public void setCurrOHLCVLockMap(Map<TimeIntervalTickerEnum, ReentrantLock> currOHLCVLockMap) {
		this.currOHLCVLockMap = currOHLCVLockMap;
	}
	public Map<Strategy, Boolean> getBackfillCompletedMap() {
		return backfillCompletedMap;
	}
	public void setBackfillCompletedMap(Map<Strategy, Boolean> backfillCompletedMap) {
		this.backfillCompletedMap = backfillCompletedMap;
	}
	public Map<Strategy, List<PriceOHLCV>> getPastOHLCVMap() {
		return pastOHLCVMap;
	}
	public void setPastOHLCVMap(Map<Strategy, List<PriceOHLCV>> pastOHLCVMap) {
		this.pastOHLCVMap = pastOHLCVMap;
	}
	public Map<Strategy, ReentrantLock> getPastOHLCVLockMap() {
		return pastOHLCVLockMap;
	}
	public void setPastOHLCVLockMap(Map<Strategy, ReentrantLock> pastOHLCVLockMap) {
		this.pastOHLCVLockMap = pastOHLCVLockMap;
	}
	public Map<Strategy, IStrategyHandler> getStrategyHandlersMap() {
		return strategyHandlersMap;
	}
	public void setStrategyHandlersMap(Map<Strategy, IStrategyHandler> strategyHandlersMap) {
		this.strategyHandlersMap = strategyHandlersMap;
	}
	public Map<Strategy, List<TradeTracked>> getTrackedTradesMap() {
		return trackedTradesMap;
	}
	public void setTrackedTradesMap(Map<Strategy, List<TradeTracked>> trackedTradesMap) {
		this.trackedTradesMap = trackedTradesMap;
	}
	public Map<Strategy, List<TradeExecuted>> getExecutedTradesMap() {
		return executedTradesMap;
	}
	public void setExecutedTradesMap(Map<Strategy, List<TradeExecuted>> executedTradesMap) {
		this.executedTradesMap = executedTradesMap;
	}
	@Override
	public String toString() {
		return "CoinStat [tradingActive=" + tradingActive + ", allottedTradingQty=" + allottedTradingQty
				+ ", currTickerPrice=" + currTickerPrice + ", currTickerPriceLock=" + currTickerPriceLock
				+ ", currOHLCVMap=" + currOHLCVMap + ", currOHLCVLockMap=" + currOHLCVLockMap
				+ ", backfillCompletedMap=" + backfillCompletedMap + ", pastOHLCVMap=" + pastOHLCVMap
				+ ", pastOHLCVLockMap=" + pastOHLCVLockMap + ", strategyHandlersMap=" + strategyHandlersMap
				+ ", trackedTradesMap=" + trackedTradesMap + ", executedTradesMap=" + executedTradesMap + "]";
	}
}
