package com.ritesh.cryptotrader.config;

import java.math.BigDecimal;

import com.ritesh.cryptotrader.config.enums.CoinEnum;

public class CoinConfig {
	
	/*
	 * NOTE: Configure all coin related configurations here
	 */

	//Coins to be tracked and traded
	private static final CoinEnum[] COINS_ARR = new CoinEnum []{
			CoinEnum.USDT_BTC, CoinEnum.USDT_ETH
	};
	
	/**
	 * Get Coins in use
	 * @return Coin Array
	 */
	public static CoinEnum[] getCoinsInUse(){
		return COINS_ARR;
	}
	
	/**
	 * Get Total Quantity to trade for a coin
	 * @param coin
	 * @return Total Quantity
	 */
	public static BigDecimal getTotalQtyToTrade(CoinEnum coin){
		switch(coin){
		case USDT_BTC:
			return new BigDecimal("2");
		case USDT_ETH:
			return new BigDecimal("2");
		default:
			return null;
		}
	}
	
	/**
	 * Quantity per strategy to trade for a coin
	 * @param coin
	 * @return Quantity Per Strategy
	 */
	public static BigDecimal getQtyPerStrategyToTrade(CoinEnum coin){
		switch(coin){
		case USDT_BTC:
			return new BigDecimal("1");
		case USDT_ETH:
			return new BigDecimal("1");
		default:
			return null;
		}
	}
}
