package com.ritesh.cryptotrader.config;

import com.ritesh.cryptotrader.config.enums.CoinEnum;

public interface IExchangeSpecificConfig {

	public String getTickerURL();
	public String getCoinStringLiteral(CoinEnum coin);
	public String getTradeUrl();
	public String getAPIKey();
}
