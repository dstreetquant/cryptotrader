package com.ritesh.cryptotrader.trading.execution;

import com.ritesh.cryptotrader.trading.model.TradeExecuted;
import com.ritesh.cryptotrader.trading.model.TradeToExecute;

public interface ITradeExecutionHandler {

	public TradeExecuted executeTrade(TradeToExecute tradeToExecute);
}
