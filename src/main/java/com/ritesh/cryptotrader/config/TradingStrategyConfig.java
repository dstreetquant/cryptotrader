package com.ritesh.cryptotrader.config;

import java.util.Arrays;
import java.util.List;

import com.ritesh.cryptotrader.config.constants.BackFillConstants;
import com.ritesh.cryptotrader.config.constants.ForwardCheckConstants;
import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.config.enums.TradingStrategyEnum;
import com.ritesh.cryptotrader.trading.strategy.IStrategyHandler;
import com.ritesh.cryptotrader.trading.strategy.Strategy;
import com.ritesh.cryptotrader.trading.strategy.Apple01.Strategy01AppleHandler;
import com.ritesh.cryptotrader.trading.strategy.TestMACrossOver02.Strategy02MACrossOverTestHandler;
import com.ritesh.cryptotrader.trading.strategy.TestShortLongMACrossOver03.Strategy03ShortLongMACrossOverTestHandler;

public class TradingStrategyConfig {
	
	/*
	 * NOTE: Configure Trading Strategies for the respective coins
	 */
	
	//USDT-BTC Trading Strategies
	private static final Strategy[] USDT_BTC_TRADE_STRATEGIES_ARR = new Strategy[]{
			new Strategy(CoinEnum.USDT_BTC, CoinConfig.getQtyPerStrategyToTrade(CoinEnum.USDT_BTC),
					TradingStrategyEnum.TRADING_STRATEGY_01_APPLE, 
					TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN, BackFillConstants.BACK_FILL_20, 
					ForwardCheckConstants.FORWARD_CHECK_3),
			new Strategy(CoinEnum.USDT_BTC, CoinConfig.getQtyPerStrategyToTrade(CoinEnum.USDT_BTC), 
					TradingStrategyEnum.TRADING_STRATEGY_TEST_03_SHORT_LONG_MA_CROSSOVER, 
					TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN, BackFillConstants.BACK_FILL_20,
					ForwardCheckConstants.FORWARD_CHECK_3)
	};
	//USDT-ETH Trading Strategies
	private static final Strategy[] USDT_ETH_TRADE_STRATEGIES_ARR = new Strategy[]{
			new Strategy(CoinEnum.USDT_ETH, CoinConfig.getQtyPerStrategyToTrade(CoinEnum.USDT_ETH),
					TradingStrategyEnum.TRADING_STRATEGY_01_APPLE, 
					TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN, BackFillConstants.BACK_FILL_20,
					ForwardCheckConstants.FORWARD_CHECK_3),
			new Strategy(CoinEnum.USDT_ETH, CoinConfig.getQtyPerStrategyToTrade(CoinEnum.USDT_ETH),
					TradingStrategyEnum.TRADING_STRATEGY_TEST_03_SHORT_LONG_MA_CROSSOVER, 
					TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN, BackFillConstants.BACK_FILL_20,
					ForwardCheckConstants.FORWARD_CHECK_3)
	};
	
	private static List<Strategy> USDT_BTC_TRADE_STRATEGIES_LIST = Arrays.asList(USDT_BTC_TRADE_STRATEGIES_ARR);
	private static List<Strategy> USDT_ETH_TRADE_STRATEGIES_LIST = Arrays.asList(USDT_ETH_TRADE_STRATEGIES_ARR);
	
	/**
	 * Get trading strategies list for a coin
	 * @param coin
	 * @return Trading Strategy List
	 */
	public static List<Strategy> getTradingStrategiesList(CoinEnum coin){
		List<Strategy> tradingStrategiesList = null;
		
		switch(coin){
		case USDT_BTC:
			tradingStrategiesList = USDT_BTC_TRADE_STRATEGIES_LIST;
			break;
		case USDT_ETH:
			tradingStrategiesList = USDT_ETH_TRADE_STRATEGIES_LIST;
			break;
		default:
			System.out.println("TradingStrategyConfig.getTradingStrategiesList(): ERROR: "
					+ "Invalid coin: " + coin);
			break;
		}
		
		return tradingStrategiesList;
	}
	
	/**
	 * Get Strategy Handler Instance for a Strategy
	 * @param strategy
	 * @return Strategy Handler instance
	 */
	public static IStrategyHandler getStrategyHandlerInstance(Strategy strategy){
		IStrategyHandler strategyHandler = null;
		
		switch(strategy.getTradingStrategy()){
		case TRADING_STRATEGY_01_APPLE:
			strategyHandler = new Strategy01AppleHandler(strategy);
			break;
		case TRADING_STRATEGY_TEST_02_MA_CROSSOVER:
			strategyHandler = new Strategy02MACrossOverTestHandler(strategy);
			break;
		case TRADING_STRATEGY_TEST_03_SHORT_LONG_MA_CROSSOVER:
			strategyHandler = new Strategy03ShortLongMACrossOverTestHandler(strategy);
			break;
		default:
			System.out.println("TradingStrategyConfig.getStrategyInstance(): ERROR: "
					+ "Invalid Trading Strategy: " + strategy.getTradingStrategy());
			break;
		}
		return strategyHandler;
	}

//	/**
//	 * Get String name of a Trading Strategy
//	 * @param tradingStrategyString
//	 * @return Trading Strategy String
//	 */
//	public static String getTradingStrategyString(TradingStrategyEnum tradingStrategyEnum){
//		String tradingStrategyString = null;
//		
//		switch(tradingStrategyEnum){
//		case TRADING_STRATEGY_01_APPLE:
//			tradingStrategyString = "TRADING_STRATEGY_01_APPLE";
//			break;
//		case TRADING_STRATEGY_TEST_02_MA_CROSSOVER:
//			tradingStrategyString = "TRADING_STRATEGY_TEST_02_MA_CROSSOVER";
//			break;
//		default:
//			System.out.println("TradingStrategyConfig.getTradingStrategyString(): ERROR: "
//					+ "Invalid Trading Strategy: " + tradingStrategyEnum);
//			break;
//		}
//		
//		return tradingStrategyString;
//	}
}
