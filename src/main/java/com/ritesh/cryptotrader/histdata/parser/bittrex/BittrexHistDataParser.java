package com.ritesh.cryptotrader.histdata.parser.bittrex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ritesh.cryptotrader.utils.JacksonObjectMapper;

public class BittrexHistDataParser {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String BTTREX_HIST_URL = "https://bittrex.com/Api/v2.0/pub/market/GetTicks?marketName=COIN_NAME&tickInterval=TIME_INTERVAL";
	//"USDT-BTC", "USDT-ETH", "BTC-ETH"
	private static final String COIN_NAME = "USDT-BTC";
	//"oneMin", "fiveMin", "thirtyMin", "hour" and "day"
	private static final String TIME_INTERVAL = "fiveMin";
	
	private static final String FILE_PATH = "C:\\Ritesh_Computer\\crypto\\bittrex\\hist_data";
	private static final String FILE_NAME = FILE_PATH + "\\" + COIN_NAME + ".csv";

	private static BittrexHistJSON getHistData(){
		String url = BTTREX_HIST_URL.replace("COIN_NAME", COIN_NAME).replace("TIME_INTERVAL", TIME_INTERVAL);
		BittrexHistJSON bittrexHistJSON = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response;
		try {
			response = client.execute(request);
			System.out.println("BittrexHistDataParser.getHistData(): "
					+ "GET request to URL : " + url);
			System.out.println("BittrexHistDataParser.getHistData(): "
					+ "Response Code : " + response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			bittrexHistJSON = JacksonObjectMapper.getObjectMapper()
					.readValue(result.toString().toLowerCase(), BittrexHistJSON.class);
			
			System.out.println("BittrexHistDataParser.getHistData(): Data Recieved");
			//System.out.println("DEBUG: " + bittrexHistJSON);
		} catch (ClientProtocolException e) {
			System.out.println("BittrexHistDataParser.getHistData(): ERROR: ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("BittrexHistDataParser.getHistData(): ERROR: IOException");
			e.printStackTrace();
		}
		
		return bittrexHistJSON;
	}
	
	private static List<BittrexHistData> convertHistData(BittrexHistJSON bittrexHistJSON){
		List<BittrexHistData> bittrexHistDataList = new ArrayList<BittrexHistData>();
		
		BittrexHistOHLCV[] bittrexHistOHLCVArr = bittrexHistJSON.getResult();
		for(BittrexHistOHLCV bittrexHistOHLCV : bittrexHistOHLCVArr){
			BittrexHistData bittrexHistData = new BittrexHistData();
			String[] dateTime = bittrexHistOHLCV.getT().split("t");
			bittrexHistData.setDate(dateTime[0]);
			bittrexHistData.setTime(dateTime[1]);
			bittrexHistData.setOpen(bittrexHistOHLCV.getO());
			bittrexHistData.setHigh(bittrexHistOHLCV.getH());
			bittrexHistData.setLow(bittrexHistOHLCV.getL());
			bittrexHistData.setClose(bittrexHistOHLCV.getC());
			bittrexHistData.setVol(bittrexHistOHLCV.getV());
			bittrexHistDataList.add(bittrexHistData);
		}
		
		return bittrexHistDataList;
	}
	
	private static void writeToFile(List<BittrexHistData> bittrexHistDataList){
		StringBuilder sb = new StringBuilder();
		sb.append("Date,Time,Open,High,Low,Close,Volume\n");
		for(BittrexHistData bittrexHistData : bittrexHistDataList){
			String st = bittrexHistData.getDate() + ","
					+ bittrexHistData.getTime() + ","
					+ bittrexHistData.getOpen() + ","
					+ bittrexHistData.getHigh() + ","
					+ bittrexHistData.getLow() + ","
					+ bittrexHistData.getClose() + ","
					+ bittrexHistData.getVol() + "\n";
			sb.append(st);
		}
		
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(FILE_NAME);
			bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			System.out.println("BittrexHistDataParser.writeToFile(): File Writting Done: " + FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		BittrexHistJSON bittrexHistJSON = BittrexHistDataParser.getHistData();
		List<BittrexHistData> bittrexHistDataList = BittrexHistDataParser.convertHistData(bittrexHistJSON);
		//System.out.println(bittrexHistDataList);
		BittrexHistDataParser.writeToFile(bittrexHistDataList);
		
	}
}
