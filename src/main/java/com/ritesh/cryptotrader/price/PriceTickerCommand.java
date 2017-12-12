package com.ritesh.cryptotrader.price;

import com.ritesh.cryptotrader.price.model.TickerPrice;

public class PriceTickerCommand {
	private String command;
	private TickerPrice tickerPrice;
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public TickerPrice getTickerPrice() {
		return tickerPrice;
	}
	public void setTickerPrice(TickerPrice tickerPrice) {
		this.tickerPrice = tickerPrice;
	}
	@Override
	public String toString() {
		return "PriceTickerCommand [command=" + command + ", tickerPrice=" + tickerPrice + "]";
	}
}
