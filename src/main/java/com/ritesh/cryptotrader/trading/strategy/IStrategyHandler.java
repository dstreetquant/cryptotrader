package com.ritesh.cryptotrader.trading.strategy;

import com.ritesh.cryptotrader.price.model.TickerPrice;

public interface IStrategyHandler {

	public void notifyTradeTimeTicked(String time);
	
	public void notifyPriceTicked(TickerPrice tickerPrice);
}
