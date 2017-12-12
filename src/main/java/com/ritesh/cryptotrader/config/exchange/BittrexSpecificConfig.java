package com.ritesh.cryptotrader.config.exchange;

import com.ritesh.cryptotrader.config.IExchangeSpecificConfig;
import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class BittrexSpecificConfig implements IExchangeSpecificConfig{

	/*
	 * NOTE: Configure all Bittrex exchange specific configurations
	 */
	
	//TICKER http API
	private static final String BITTREX_TICKER_URL = "https://bittrex.com/api/v1.1/public/getticker?market=";
	
	//TRADE http API
	private static final String BITTREX_TRADE_URL = "https://bittrex.com/api/v1.1/market";
	
	//API Key
	private static final String BITTREX_API_KEY = "abcd1234";
	
	/**
	 * Get Price Ticker URL for the Bittrex
	 */
	public String getTickerURL() {
		return BITTREX_TICKER_URL;
	}
	
	/**
	 * Get exchange Coin name in string for Bittrex
	 * @param coin
	 * @return coin name string
	 */
	public String getCoinStringLiteral(CoinEnum coin){
		String coinStr = null;
		switch(coin){
		case USDT_BTC:
			coinStr = "USDT-BTC";
			break;
		case USDT_ETH:
			coinStr = "USDT-ETH";
			break;
		default:
			System.out.println("Bittrex.getCoinStringLiteral(): ERROR: coin does not exist: " + coin);
			coinStr = null;
			break;
		}
		return coinStr;
	}

	@Override
	public String getTradeUrl() {
		return BITTREX_TRADE_URL;
	}

	@Override
	public String getAPIKey() {
		return BITTREX_API_KEY;
	}
}
