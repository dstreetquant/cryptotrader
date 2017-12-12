package com.ritesh.cryptotrader.histdata.parser.bittrex;

import java.math.BigDecimal;

public class BittrexHistOHLCV {

	private BigDecimal O;
	private BigDecimal H;
	private BigDecimal L;
	private BigDecimal C;
	private BigDecimal V;
	private String T;
	private BigDecimal BV;
	
	public BigDecimal getO() {
		return O;
	}
	public void setO(BigDecimal o) {
		O = o;
	}
	public BigDecimal getH() {
		return H;
	}
	public void setH(BigDecimal h) {
		H = h;
	}
	public BigDecimal getL() {
		return L;
	}
	public void setL(BigDecimal l) {
		L = l;
	}
	public BigDecimal getC() {
		return C;
	}
	public void setC(BigDecimal c) {
		C = c;
	}
	public BigDecimal getV() {
		return V;
	}
	public void setV(BigDecimal v) {
		V = v;
	}
	public String getT() {
		return T;
	}
	public void setT(String t) {
		T = t;
	}
	public BigDecimal getBV() {
		return BV;
	}
	public void setBV(BigDecimal bV) {
		BV = bV;
	}

	@Override
	public String toString() {
		return "BittrexHistOHLCV [O=" + O + ", H=" + H + ", L=" + L + ", C=" + C + ", V=" + V + ", T=" + T + ", BV="
				+ BV + "]";
	}
}
