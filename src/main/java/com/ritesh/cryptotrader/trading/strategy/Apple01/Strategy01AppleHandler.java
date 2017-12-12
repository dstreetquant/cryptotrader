package com.ritesh.cryptotrader.trading.strategy.Apple01;

import com.ritesh.cryptotrader.price.model.TickerPrice;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;

public class Strategy01AppleHandler implements IStrategyHandler{

	private Strategy strategy = null;
	
	public Strategy01AppleHandler(Strategy strategy){
		this.strategy = strategy;
	}

	@Override
	public void notifyTradeTimeTicked(String time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPriceTicked(TickerPrice tickerPrice) {
		// TODO Auto-generated method stub
		
	}
}
