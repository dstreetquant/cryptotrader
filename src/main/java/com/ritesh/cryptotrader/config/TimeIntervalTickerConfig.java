package com.ritesh.cryptotrader.config;

import java.util.Arrays;
import java.util.List;

import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.enums.TimeIntervalTickerEnum;
import com.ritesh.cryptotrader.config.interval.FiveMinIntervalMonitor;
import com.ritesh.cryptotrader.config.interval.IntervalMonitorSubject;

public class TimeIntervalTickerConfig {
	
	/*
	 * NOTE: Configure Time Intervals to monitor for the respective coins
	 */
	
	//USDT-BTC Time Tick Intervals
	private static final TimeIntervalTickerEnum[] USDT_BTC_TICKER_ARR = new TimeIntervalTickerEnum[]{
			//TimeIntervalTickerEnum.TIME_INTERVAL_30_SEC,
			TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN
	};
	//USDT-ETH Time Tick Intervals
	private static final TimeIntervalTickerEnum[] USDT_ETH_TICKER_ARR = new TimeIntervalTickerEnum[]{
			//TimeIntervalTickerEnum.TIME_INTERVAL_30_SEC,
			TimeIntervalTickerEnum.TIME_INTERVAL_5_MIN
	};
	
	private static List<TimeIntervalTickerEnum> USDT_BTC_TIME_TICKER_LIST = Arrays.asList(USDT_BTC_TICKER_ARR);
	private static List<TimeIntervalTickerEnum> USDT_ETH_TIME_TICKER_LIST = Arrays.asList(USDT_ETH_TICKER_ARR);
	
	/**
	 * Get time interval ticker list for a coin
	 * @param coin
	 * @return Time Interval Ticker List
	 */
	public static List<TimeIntervalTickerEnum> getTimeIntervalTickersList(CoinEnum coin){
		List<TimeIntervalTickerEnum> timeIntervalTickersList = null;
		
		switch(coin){
		case USDT_BTC:
			timeIntervalTickersList = USDT_BTC_TIME_TICKER_LIST;
			break;
		case USDT_ETH:
			timeIntervalTickersList = USDT_ETH_TIME_TICKER_LIST;
			break;
		default:
			System.out.println("TimeIntervalTickerConfig.getTimeIntervalTickersList(): ERROR: "
					+ "Invalid coin: " + coin);
			break;
		}
		
		return timeIntervalTickersList;
	}

	/**
	 * Get String name of TimeIntervalTickerEnum
	 * @param timeIntEnum
	 * @return TimeIntervalTicker String
	 */
	public static String getTimeIntervalTickerString(TimeIntervalTickerEnum timeIntEnum){
		String timeIntString = null;
		
		switch(timeIntEnum){
		case TIME_INTERVAL_30_SEC:
			timeIntString = "TIME_INTERVAL_30_SEC";
			break;
		case TIME_INTERVAL_1_MIN:
			timeIntString = "TIME_INTERVAL_1_MIN";
			break;
		case TIME_INTERVAL_5_MIN:
			timeIntString = "TIME_INTERVAL_5_MIN";
			break;
		default:
			System.out.println("TimeIntervalTickerConfig.getTimeIntervalTickerString(): ERROR: "
					+ "Invalid time Interval: " + timeIntEnum);
			break;
		}
		
		return timeIntString;
	}
	
	/**
	 * Get Time Interval Monitor Object Instance
	 * @param timeIntEnum
	 * @return IntervalMonitorSubject instance
	 */
	public static IntervalMonitorSubject getIntervalMonitorInstance(TimeIntervalTickerEnum timeIntEnum){
		IntervalMonitorSubject monitor = null;
		
		switch(timeIntEnum){
		case TIME_INTERVAL_30_SEC:
			monitor = null;
			break;
		case TIME_INTERVAL_1_MIN:
			monitor = null;
			break;
		case TIME_INTERVAL_5_MIN:
			monitor = FiveMinIntervalMonitor.getInstance();
			break;
		default:
			System.out.println("TimeIntervalTickerConfig.getIntervalMonitorInstance(): ERROR: "
					+ "Invalid time Interval: " + timeIntEnum);
			break;
		}
		
		return monitor;
	}
}
