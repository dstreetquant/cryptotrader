package com.ritesh.cryptotrader.trading.model;

import java.math.BigDecimal;

public class TradeTracked extends TradeExecuted{

	private BigDecimal targetPrice;
	private BigDecimal slPrice;
	private String squareOffTime;
	
	public TradeTracked(){
		super();
	}
	public BigDecimal getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(BigDecimal targetPrice) {
		this.targetPrice = targetPrice;
	}
	public BigDecimal getSlPrice() {
		return slPrice;
	}
	public void setSlPrice(BigDecimal slPrice) {
		this.slPrice = slPrice;
	}
	public String getSquareOffTime() {
		return squareOffTime;
	}
	public void setSquareOffTime(String squareOffTime) {
		this.squareOffTime = squareOffTime;
	}
	
	@Override
	public String toString() {
		return "TradeTracked [targetPrice=" + targetPrice + ", slPrice=" + slPrice + ", squareOffTime=" + squareOffTime
				+ "]";
	}
}
