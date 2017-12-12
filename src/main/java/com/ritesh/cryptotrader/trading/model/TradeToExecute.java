package com.ritesh.cryptotrader.trading.model;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class TradeToExecute {

	protected CoinEnum coin;
	protected TradeType tradeType;
	protected BigDecimal price;
	protected BigDecimal qty;
	
	public CoinEnum getCoin() {
		return coin;
	}
	public void setCoin(CoinEnum coin) {
		this.coin = coin;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	@Override
	public String toString() {
		return "TradeToExecute [coin=" + coin + ", tradeType=" + tradeType + ", price=" + price + ", qty=" + qty + "]";
	}
}
