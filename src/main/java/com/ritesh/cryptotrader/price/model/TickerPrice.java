package com.ritesh.cryptotrader.price.model;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class TickerPrice {

	private CoinEnum coin;
	private String id;
	private String date;
	private String time;
	private BigDecimal lastPrice;
	private BigDecimal lowestAsk;
	private BigDecimal highestBid;
	private BigDecimal volume;
	
	public CoinEnum getCoin() {
		return coin;
	}
	public void setCoin(CoinEnum coin) {
		this.coin = coin;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}
	public BigDecimal getLowestAsk() {
		return lowestAsk;
	}
	public void setLowestAsk(BigDecimal lowestAsk) {
		this.lowestAsk = lowestAsk;
	}
	public BigDecimal getHighestBid() {
		return highestBid;
	}
	public void setHighestBid(BigDecimal highestBid) {
		this.highestBid = highestBid;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	
	@Override
	public String toString() {
		return "TickerPrice [coin=" + coin + ", id=" + id + ", date=" + date + ", time=" + time + ", lastPrice="
				+ lastPrice + ", lowestAsk=" + lowestAsk + ", highestBid=" + highestBid + ", volume=" + volume + "]";
	}
}
