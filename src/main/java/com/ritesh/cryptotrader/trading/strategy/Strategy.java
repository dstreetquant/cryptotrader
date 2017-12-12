package com.ritesh.cryptotrader.trading.strategy;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.config.enums.TradingStrategyEnum;

public class Strategy {

	private CoinEnum coin = null;
	private BigDecimal strategyTradingQty = null;
	private TradingStrategyEnum tradingStrategy = null;
	private TimeIntervalTickerEnum timeTicker = null;
	private Integer backFillNum = null;
	private Integer forwardCheckNum = null;
	private Integer maxNumPosition = null;
	private String strategyName = null;
	
	public Strategy(CoinEnum coin, BigDecimal strategyTradingQty, TradingStrategyEnum tradingStrategy, 
			TimeIntervalTickerEnum timeTicker, Integer backFillNum, Integer forwardCheckNum) {
		super();
		this.coin = coin;
		this.strategyTradingQty = strategyTradingQty;
		this.tradingStrategy = tradingStrategy;
		this.timeTicker = timeTicker;
		this.backFillNum = backFillNum;
		this.forwardCheckNum = forwardCheckNum;
		this.maxNumPosition = this.forwardCheckNum;
		this.strategyName = "<" + this.tradingStrategy + "><" + this.timeTicker + "><" + 
							this.backFillNum + "><" + this.forwardCheckNum + ">";
	}
	
	public CoinEnum getCoin() {
		return coin;
	}
	public void setCoin(CoinEnum coin) {
		this.coin = coin;
	}
	public BigDecimal getStrategyTradingQty() {
		return strategyTradingQty;
	}
	public void setStrategyTradingQty(BigDecimal strategyTradingQty) {
		this.strategyTradingQty = strategyTradingQty;
	}
	public TradingStrategyEnum getTradingStrategy() {
		return tradingStrategy;
	}
	public void setTradingStrategy(TradingStrategyEnum tradingStrategy) {
		this.tradingStrategy = tradingStrategy;
	}
	public TimeIntervalTickerEnum getTimeTicker() {
		return timeTicker;
	}
	public void setTimeTicker(TimeIntervalTickerEnum timeTicker) {
		this.timeTicker = timeTicker;
	}
	public Integer getBackFillNum() {
		return backFillNum;
	}
	public void setBackFillNum(Integer backFillNum) {
		this.backFillNum = backFillNum;
	}
	public Integer getForwardCheckNum() {
		return forwardCheckNum;
	}
	public void setForwardCheckNum(Integer forwardCheckNum) {
		this.forwardCheckNum = forwardCheckNum;
	}
	public Integer getMaxNumPosition() {
		return maxNumPosition;
	}
	public void setMaxNumPosition(Integer maxNumPosition) {
		this.maxNumPosition = maxNumPosition;
	}
	public String getStrategyName() {
		return strategyName;
	}
	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	@Override
	public String toString() {
		return "Strategy [coin=" + coin + ", strategyTradingQty=" + strategyTradingQty + ", tradingStrategy="
				+ tradingStrategy + ", timeTicker=" + timeTicker + ", backFillNum=" + backFillNum + ", forwardCheckNum="
				+ forwardCheckNum + ", maxNumPosition=" + maxNumPosition + ", strategyName=" + strategyName + "]";
	}
}
