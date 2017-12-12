package com.ritesh.cryptotrader.config;

public class PriceConfig {
	
	/*
	 * NOTE: Configure all Price and ticker specific configurations
	 */

	//Price fetch period
	private static final Integer PRICE_FETCH_PERIOD = 1000;
	
	/**
	 * Get Price Fetch Period
	 * @return Price Fetch Period
	 */
	public static Integer getPriceFetchPeriod(){
		return PRICE_FETCH_PERIOD;
	}
}
