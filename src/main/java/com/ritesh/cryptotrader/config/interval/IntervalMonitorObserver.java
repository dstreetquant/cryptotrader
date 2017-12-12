package com.ritesh.cryptotrader.config.interval;

public interface IntervalMonitorObserver {
	
	public void notifyTimeTickedUpdate(String time);
}
