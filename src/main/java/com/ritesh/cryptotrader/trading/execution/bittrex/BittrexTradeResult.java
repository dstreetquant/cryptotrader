package com.ritesh.cryptotrader.trading.execution.bittrex;

public class BittrexTradeResult {

	String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "BittrexTradeResult [uuid=" + uuid + "]";
	}
	
}
