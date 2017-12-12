package com.ritesh.cryptotrader.app;

import com.ritesh.cryptotrader.manager.TradeManager;

public class App {

	public static void main(String[] args){
		TradeManager.getInstance().start();
	}
}
