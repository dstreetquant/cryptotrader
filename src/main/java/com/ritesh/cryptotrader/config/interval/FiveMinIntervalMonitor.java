package com.ritesh.cryptotrader.config.interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.cryptotrader.utils.DateTimeUtil;

public class FiveMinIntervalMonitor implements IntervalMonitorSubject{
	private static FiveMinIntervalMonitor fiveMinIntervalMonitor = null;
	
	//current date
	private String todaysDate = null; // "20171127"
	
	private static final String[] PERIODIC_TIME_ARR_FIVE_MIN = FiveMinIntervalArr.PERIODIC_TIME_ARR_FIVE_MIN;
	private static final String[] PRECISION_TIME_ARR_IN_SECONDS = {"58", "59"};
	private static List<String> periodicTimeListFiveMinRequired = null;
	private static List<String> periodicTimeListFiveMinUpdated = null;
	private static List<String> secPrecisionList = null;
	
	//thread
	private TimeTicker timeTicker = null;
	
	//time ticked notification observer list
	private List<IntervalMonitorObserver> observerList;
	
	//ticker start/stop flag
	private Boolean tickerStarted = null;
	private Lock tickerStartedLock = null;
	
	private Map<Integer, String> indexToTimeMap = null;
	private Map<String, Integer> timeToIndexMap = null;
	
	/**
	 * private constructor
	 */
	private FiveMinIntervalMonitor(){
		observerList = new ArrayList<IntervalMonitorObserver>();
		
		tickerStarted = false;
		tickerStartedLock = new ReentrantLock();
		
		indexToTimeMap = new HashMap<Integer, String>();
		timeToIndexMap = new HashMap<String, Integer>();
		
		createIndexToTimeMap();
		createTimeToIndexMap();
	}
	
	/**
	 * singleton instance provider
	 * @return FiveMinIntervalMonitor instance
	 */
	public static FiveMinIntervalMonitor getInstance(){
		if(fiveMinIntervalMonitor == null){
			fiveMinIntervalMonitor = new FiveMinIntervalMonitor();
		}
		
		return fiveMinIntervalMonitor;
	}
	
	@Override
	public String getTimeAtOffset(String time, Integer offset){
		String reqdTime = "";
		
		if(time.length() != 5){
			//chop time to HH:mm format
			reqdTime = time.substring(0, 5);
		} else{
			reqdTime = time;
		}
		
		//calculate time index
		Integer timeIdx = timeToIndexMap.get(reqdTime);
		if (timeIdx != null) {
			//proceed only in case of valid time
			int offsetTimeIdx = timeIdx + offset;
			String offsetReqdTime = indexToTimeMap.get(offsetTimeIdx) + ":" + "59";
			return offsetReqdTime;
		} else{
			return null;
		}
	}
	
	@Override
	public void register(IntervalMonitorObserver observer) {
		//add observer in observer list
		if(!observerList.contains(observer)){
			observerList.add(observer);
		} else{
			System.out.println("FiveMinIntervalMonitor.register(): WARNING: observer already added [" + observer + "]");
		}
	}

	@Override
	public void unregister(IntervalMonitorObserver observer) {
		//remove observer from observer list
		if (observerList.contains(observer)) {
			observerList.remove(observer);
		} else {
			System.out.println("FiveMinIntervalMonitor.register(): WARNING: observer already removed [" + observer + "]");
		}
	}
	
	@Override
	public void notifyTickerObservers(String time) {
		System.out.println("FiveMinIntervalMonitor.notifyTickerObservers(): DEBUG: Time Ticked: " + time);
		if(observerList == null){
			System.out.println("FiveMinIntervalMonitor.notifyTickerObservers(): ERROR: observerList is null");
			return;
		}
		
		//notify observers
		for(IntervalMonitorObserver observer : observerList){
			observer.notifyTimeTickedUpdate(time);
		}
	}
	
	@Override
	public void start(){
		System.out.println("FiveMinIntervalMonitor.start(): API called");
		
		//if ticker is already in started state - do not start again
		tickerStartedLock.lock();
		if(tickerStarted){
			System.out.println("FiveMinIntervalMonitor.start(): WARNING: Ticker already in started state");
			tickerStartedLock.unlock();
			return;
		} else{
			tickerStarted = true;
			tickerStartedLock.unlock();
		}
		
		//create list of times required for time ticker thread
		periodicTimeListFiveMinRequired = Arrays.asList(PERIODIC_TIME_ARR_FIVE_MIN);
		//create list of times updated by time ticker thread
		periodicTimeListFiveMinUpdated = new ArrayList<String>();
		//create list of second precision time strings in seconds
		secPrecisionList = Arrays.asList(PRECISION_TIME_ARR_IN_SECONDS);
		
		// set initial today's date
		todaysDate = DateTimeUtil.getInstance().getCurrentDateOnlyInString();
		
		//create and start time ticker thread
		System.out.println("FiveMinIntervalMonitor.start(): Starting time ticker thread...");
		timeTicker = new TimeTicker();
		Thread timeTickerThread = new Thread(timeTicker);
		timeTickerThread.start();
	}
	
	@Override
	public void stop(){
		System.out.println("FiveMinIntervalMonitor.stop(): API called");
		
		//if ticker is already in stopped state - do not stop again
		tickerStartedLock.lock();
		if (!tickerStarted) {
			System.out.println("FiveMinIntervalMonitor.start(): WARNING: Ticker already in stopped state");
			tickerStartedLock.unlock();
			return;
		} else {
			tickerStarted = false;
			tickerStartedLock.unlock();
		}
		
		//stop time ticker thread
		timeTicker.stopThread();
	}
	
	/**
	 * Time Ticker thread
	 * @author riteshg
	 *
	 */
	class TimeTicker implements Runnable{
		private boolean runFlag = true;
		
		@Override
		public void run() {
			while(runFlag){
				String date = DateTimeUtil.getInstance().getCurrentDateOnlyInString();
				String time = DateTimeUtil.getInstance().getCurrentTimeOnlyInString();
				String hrMinTime = time.substring(0, 5);
				String secTime = time.substring(6, time.length());
				
				if(!date.equals(todaysDate)){
					System.out.println("FiveMinIntervalMonitor.TimeTicker.run(): Date Changed from "
							+ "[" + todaysDate + "] to [" + date + "]");
					//Date has changed, reset Updated Time List and notify handler
					todaysDate = date;
					periodicTimeListFiveMinUpdated.clear();
					DateChangeHandler.getInstance().notifyDateChanged();
				}
				
				//check for periodic time data
				if(periodicTimeListFiveMinRequired.contains(hrMinTime) && 
						!periodicTimeListFiveMinUpdated.contains(hrMinTime) && secPrecisionList.contains(secTime)){
					System.out.println("\nFiveMinIntervalMonitor.TimeTicker.run(): Interval Time ticked: [" + time + "]");
					//Notify Ticker observers
					notifyTickerObservers(time.substring(0, 6) + "59");
					//Update time updated list
					periodicTimeListFiveMinUpdated.add(hrMinTime);
				}
				
				//sleep for one sec
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					System.out.println("FiveMinIntervalMonitor.TimeTicker.run(): InterruptedException while sleeping");
				}
			}
		}
		
		/**
		 * Stops this thread
		 */
		public void stopThread(){
			System.out.println("FiveMinIntervalMonitor.TimeTicker.run(): Stopping Time ticker thread....");
			runFlag = false;
		}
	}
	
	/**
	 * createIndexToTimeMap - private method to create index to time mapping for slices
	 * time does not inlcude second precision
	 */
	private void createIndexToTimeMap(){
		for(int i = 0; i < PERIODIC_TIME_ARR_FIVE_MIN.length; i++){
			indexToTimeMap.put(i, PERIODIC_TIME_ARR_FIVE_MIN[i]);
		}
	}
	
	/**
	 * createTimeToIndexMap - private method to create time to index mapping for slices
	 * time does not include second precision
	 */
	private void createTimeToIndexMap(){
		for(int i = 0; i < PERIODIC_TIME_ARR_FIVE_MIN.length; i++){
			timeToIndexMap.put(PERIODIC_TIME_ARR_FIVE_MIN[i], i);
		}
	}
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args){
		FiveMinIntervalMonitor monitor = FiveMinIntervalMonitor.getInstance();
		monitor.start();
	}
}
