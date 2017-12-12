package com.ritesh.cryptotrader.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

	private static DateTimeUtil dateTimeUtilInst = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	
	private DateTimeUtil(){
		dateFormat.setTimeZone(timeZone);
	}
	
	/**
	 * Singleton Instance
	 */
	public static DateTimeUtil getInstance(){
		if(dateTimeUtilInst == null){
			dateTimeUtilInst = new DateTimeUtil();
		}
		return dateTimeUtilInst;
	}
	
	/**
	 * Get Date Format in use
	 */
	public SimpleDateFormat getDateFormat(){
		return dateFormat;
	}
	
	/**
	 * Get Time Zone in use
	 */
	public TimeZone getTimeZone(){
		return timeZone;
	}
	
	/**
	 * Get Current Date & Time
	 */
	public Date getCurrentDateTime(){
		return Calendar.getInstance(timeZone).getTime();
	}
	
	/**
	 * Get Current Date & Time In String
	 */
	public String getCurrentDateTimeInString(){
		return dateFormat.format(Calendar.getInstance(timeZone).getTime());
	}
	
	/**
	 * Get Current Date only in String
	 */
	public String getCurrentDateOnlyInString(){
		String dateTime = dateFormat.format(Calendar.getInstance(timeZone).getTime());
		String date = dateTime.substring(0, 10);
		return date;
	}
	
	/**
	 * Get Current Time only in String
	 */
	public String getCurrentTimeOnlyInString(){
		String dateTime = dateFormat.format(Calendar.getInstance(timeZone).getTime());
		String time = dateTime.substring(11);
		return time;
	}
	
	/**
	 * Convert String to Date format
	 */
	public Date convertStringToDate(String dateTimeString) throws ParseException{
		Date dateTimeDate = dateFormat.parse(dateTimeString);
		return dateTimeDate;
	}
	
	/**
	 * Convert Date to String format
	 */
	public String convertDateToString(Date dateTimeDate){
		String dateTimeString = dateFormat.format(dateTimeDate);
		return dateTimeString;
	}
	
	/**
	 * Check if the time now has crossed the time referred/compared
	 */
	public boolean isTimeOver(Date timeNow, Date timeRef){
		if(timeNow.compareTo(timeRef) >= 0){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * Test Main 
	 */
	public static void main(String[] args){
		String currDateTime = DateTimeUtil.getInstance().getCurrentDateTimeInString();
		System.out.println("[" + currDateTime + "]");
		String currDate = DateTimeUtil.getInstance().getCurrentDateOnlyInString();
		System.out.println("[" + currDate + "]");
		String currTime = DateTimeUtil.getInstance().getCurrentTimeOnlyInString();
		System.out.println("[" + currTime + "]");
	}
}
