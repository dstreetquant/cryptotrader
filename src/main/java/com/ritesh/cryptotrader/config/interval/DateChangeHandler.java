package com.ritesh.cryptotrader.config.interval;

public class DateChangeHandler {
	
	private static DateChangeHandler dateChangeHandlerInst = null;

	private DateChangeHandler(){
		//
	}
	
	/**
	 * Singleton Instance
	 */
	public static DateChangeHandler getInstance(){
		if(dateChangeHandlerInst == null){
			dateChangeHandlerInst = new DateChangeHandler();
		}
		return dateChangeHandlerInst;
	}
	
	/**
	 * Handles Date Change notification
	 */
	public void notifyDateChanged(){
		Thread th = new Thread(new DateChangeNotifHandlerRunnable());
		th.start();
	}
	
	/**
	 * Thread to handle date notifications
	 * @author rghosh
	 */
	class DateChangeNotifHandlerRunnable implements Runnable{
		
		@Override
		public void run() {
			
		}
	}
}
