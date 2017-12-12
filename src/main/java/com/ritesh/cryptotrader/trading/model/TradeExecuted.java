package com.ritesh.cryptotrader.trading.model;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class TradeExecuted {

	protected CoinEnum coin;
	protected String orderId;
	protected TradeType tradeType;
	protected BigDecimal qty;
	protected String date;
	protected String time;
	protected String exchangeTimeStamp;
	
	public CoinEnum getCoin() {
		return coin;
	}
	public void setCoin(CoinEnum coin) {
		this.coin = coin;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getExchangeTimeStamp() {
		return exchangeTimeStamp;
	}
	public void setExchangeTimeStamp(String exchangeTimeStamp) {
		this.exchangeTimeStamp = exchangeTimeStamp;
	}
	
	@Override
	public String toString() {
		return "TradeExecuted [coin=" + coin + ", orderId=" + orderId + ", tradeType=" + tradeType + ", qty=" + qty
				+ ", date=" + date + ", time=" + time + ", exchangeTimeStamp=" + exchangeTimeStamp + "]";
	}
}
