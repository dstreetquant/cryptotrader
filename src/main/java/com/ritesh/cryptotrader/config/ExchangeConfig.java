package com.ritesh.cryptotrader.config;

import java.util.concurrent.ExecutorService;

import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.ExchangeEnum;
import com.ritesh.cryptotrader.price.IPriceTickerHandler;
import com.ritesh.cryptotrader.price.bittrex.BittrexPriceTickerHandler;
import com.ritesh.cryptotrader.price.poloniex.PoloniexPriceTickerHandler;
import com.ritesh.cryptotrader.trading.execution.ITradeExecutionHandler;
import com.ritesh.cryptotrader.trading.execution.bittrex.BittrexTradeExecutionHandler;
import com.ritesh.cryptotrader.trading.execution.poloniex.PoloniexTradeExecutionHandler;

public class ExchangeConfig {
	
	/*
	 * NOTE: Configure all exchange specific informations here
	 */

	//Crypto exchange specific configurations
	private static final ExchangeEnum CRYPTO_EXCHANGE = ExchangeEnum.BITTREX;
	
	//Exchange Specific implementation
	private static final ExchangeSpecificConfigImpl ExchangeSpecificConfigImpl = new ExchangeSpecificConfigImpl();
	
	/**
	 * Get the exchange for trading
	 * @return Exchange Enum
	 */
	public static ExchangeEnum getExchange(){
		return CRYPTO_EXCHANGE;
	}
	
	/**
	 * Get Price Tick fetch handler for a specific exchange
	 * @param executor
	 * @return Price Ticker Handler
	 */
	public static IPriceTickerHandler getPriceTickerHandler(ExecutorService executor){
		IPriceTickerHandler priceTickerHandler = null;
		switch(CRYPTO_EXCHANGE){
		case POLONIEX:
			priceTickerHandler = new PoloniexPriceTickerHandler();
			break;
		case BITTREX:
			priceTickerHandler = new BittrexPriceTickerHandler(executor);
			break;
		default:
			System.out.println("ExchangeConfig.getPriceTickerHandler(): ERROR: Invalid Exchange: "
					+ ExchangeConfig.getExchange());
			break;
		}
		
		return priceTickerHandler;
	}
	
	/**
	 * Get Trade Execution handler for a specific exchange
	 * @param executor
	 * @return Trade Execution Handler
	 */
	public static ITradeExecutionHandler getTradeExecutionHandler(){
		ITradeExecutionHandler tradeExecHandler = null;
		switch(CRYPTO_EXCHANGE){
		case POLONIEX:
			tradeExecHandler = new PoloniexTradeExecutionHandler();
			break;
		case BITTREX:
			tradeExecHandler = new BittrexTradeExecutionHandler();
			break;
		default:
			System.out.println("ExchangeConfig.getTradeExecutionHandler(): ERROR: Invalid Exchange: "
					+ ExchangeConfig.getExchange());
			break;
		}
		
		return tradeExecHandler;
	}

	/**
	 * Get Price Ticker URL
	 * @return Price Ticker HTTP
	 */
	public static String getTickerURL() {
		return ExchangeSpecificConfigImpl.getTickerURL();
	}

	/**
	 * Get Coin String Literal for an exchange
	 * @param coin
	 * @return Coin String
	 */
	public static String getCoinStringLiteral(CoinEnum coin) {
		return ExchangeSpecificConfigImpl.getCoinStringLiteral(coin);
	}
}
