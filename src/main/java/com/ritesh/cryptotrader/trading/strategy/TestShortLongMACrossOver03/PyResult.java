package com.ritesh.cryptotrader.trading.strategy.TestShortLongMACrossOver03;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.trading.model.TradeType;

class PyResult {

	protected TradeType tradeType;
	protected BigDecimal enterPrice;
	protected BigDecimal slPrice;
	protected BigDecimal targetPrice;
	
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public BigDecimal getEnterPrice() {
		return enterPrice;
	}
	public void setEnterPrice(BigDecimal enterPrice) {
		this.enterPrice = enterPrice;
	}
	public BigDecimal getSlPrice() {
		return slPrice;
	}
	public void setSlPrice(BigDecimal slPrice) {
		this.slPrice = slPrice;
	}
	public BigDecimal getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(BigDecimal targetPrice) {
		this.targetPrice = targetPrice;
	}

	@Override
	public String toString() {
		return "PyResult [tradeType=" + tradeType + ", enterPrice=" + enterPrice + ", slPrice=" + slPrice
				+ ", targetPrice=" + targetPrice + "]";
	}
}
