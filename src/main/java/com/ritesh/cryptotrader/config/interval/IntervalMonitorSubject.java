package com.ritesh.cryptotrader.config.interval;

public interface IntervalMonitorSubject {

	public void start();
	public void stop();
	
	public void register(IntervalMonitorObserver observer);
	public void unregister(IntervalMonitorObserver observer);
	public void notifyTickerObservers(String time);
	public String getTimeAtOffset(String time, Integer offset);
}
