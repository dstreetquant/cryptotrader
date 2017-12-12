package com.ritesh.cryptotrader.price.bittrex;

import java.math.BigDecimal;

public class BittrexBidAsk {

	private BigDecimal Bid;
	private BigDecimal Ask;
	private BigDecimal Last;
	
	public BigDecimal getBid() {
		return Bid;
	}
	public void setBid(BigDecimal bid) {
		Bid = bid;
	}
	public BigDecimal getAsk() {
		return Ask;
	}
	public void setAsk(BigDecimal ask) {
		Ask = ask;
	}
	public BigDecimal getLast() {
		return Last;
	}
	public void setLast(BigDecimal last) {
		Last = last;
	}
	@Override
	public String toString() {
		return "BittrexBidAsk [Bid=" + Bid + ", Ask=" + Ask + ", Last=" + Last + "]";
	}
}
