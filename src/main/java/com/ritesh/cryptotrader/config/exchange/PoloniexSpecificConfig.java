package com.ritesh.cryptotrader.config.exchange;

import com.ritesh.cryptotrader.config.IExchangeSpecificConfig;
import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class PoloniexSpecificConfig implements IExchangeSpecificConfig{

	/*
	 * NOTE: Configure all Poloniex exchange specific configurations
	 */
	
	//TICKER http API
	public static final String POLONIEX_TICKER = "https://poloniex.com/public?command=returnTicker";

	/**
	 * Get Price Ticker URL for the poloniex
	 */
	public String getTickerURL() {
		return POLONIEX_TICKER;
	}

	/**
	 * Get exchange Coin name in string for Poloniex
	 * @param coin
	 * @return coin name string
	 */
	public String getCoinStringLiteral(CoinEnum coin) {
		return null;
	}

	@Override
	public String getTradeUrl() {
		return null;
	}

	@Override
	public String getAPIKey() {
		return null;
	}
}
