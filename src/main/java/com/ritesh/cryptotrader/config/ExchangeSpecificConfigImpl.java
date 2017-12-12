package com.ritesh.cryptotrader.config;

import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.exchange.BittrexSpecificConfig;
import com.ritesh.cryptotrader.config.exchange.PoloniexSpecificConfig;

class ExchangeSpecificConfigImpl implements IExchangeSpecificConfig {

	// Exchanges
	private static final IExchangeSpecificConfig poloniexConfig = new PoloniexSpecificConfig();
	private static final IExchangeSpecificConfig bittrexConfig = new BittrexSpecificConfig();

	/**
	 * Get Price Ticker URL
	 * @return Price Ticker HTTP
	 */
	public String getTickerURL() {
		String tickerUrl = null;
		switch (ExchangeConfig.getExchange()) {
		case POLONIEX:
			tickerUrl = poloniexConfig.getTickerURL();
			break;
		case BITTREX:
			tickerUrl = bittrexConfig.getTickerURL();
			break;
		default:
			System.out.println("ExchangeSpecificConfigImpl.getTickerURL(): ERROR: Invalid Exchange: " + ExchangeConfig.getExchange());
			break;
		}
		return tickerUrl;
	}

	/**
	 * Get Coin String Literal for an exchange
	 * @param coin
	 * @return Coin String
	 */
	public String getCoinStringLiteral(CoinEnum coin) {
		String coinString = null;
		switch (ExchangeConfig.getExchange()) {
		case POLONIEX:
			coinString = poloniexConfig.getCoinStringLiteral(coin);
			break;
		case BITTREX:
			coinString = bittrexConfig.getCoinStringLiteral(coin);
			break;
		default:
			System.out.println(
					"ExchangeSpecificConfigImpl.getCoinStringLiteral(): ERROR: Invalid Exchange: " + ExchangeConfig.getExchange());
			break;
		}
		return coinString;
	}

	/**
	 * Get Trade URL
	 * @return Trade HTTP
	 */
	public String getTradeUrl() {
		String tradeUrlString = null;
		switch (ExchangeConfig.getExchange()) {
		case POLONIEX:
			tradeUrlString = poloniexConfig.getTradeUrl();
			break;
		case BITTREX:
			tradeUrlString = bittrexConfig.getTradeUrl();
			break;
		default:
			System.out.println(
					"ExchangeSpecificConfigImpl.getTradeUrl(): ERROR: Invalid Exchange: " + ExchangeConfig.getExchange());
			break;
		}
		return tradeUrlString;
	}

	/**
	 * Get API Key for exchange REST APIs
	 * @return API Key
	 */
	public String getAPIKey() {
		String apiKeyString = null;
		switch (ExchangeConfig.getExchange()) {
		case POLONIEX:
			apiKeyString = poloniexConfig.getAPIKey();
			break;
		case BITTREX:
			apiKeyString = bittrexConfig.getAPIKey();
			break;
		default:
			System.out.println(
					"ExchangeSpecificConfigImpl.getAPIKey(): ERROR: Invalid Exchange: " + ExchangeConfig.getExchange());
			break;
		}
		return apiKeyString;
	}
}
