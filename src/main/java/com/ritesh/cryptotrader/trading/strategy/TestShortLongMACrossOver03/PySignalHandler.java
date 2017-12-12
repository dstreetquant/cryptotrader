package com.ritesh.cryptotrader.trading.strategy.TestShortLongMACrossOver03;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ritesh.cryptotrader.utils.JacksonObjectMapper;

public class PySignalHandler {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String PY_SIGNAL_URL = "http://localhost:3000/shortlongmacrossover/signal";

	/**
	 * Post to Python strategy service and get result
	 * @param jsonString
	 * @return signal string
	 */
	private static String doPost(String jsonString){
		String signal = null;
		try {
			byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			
			URL obj = new URL(PY_SIGNAL_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json"); 
			con.setRequestProperty("charset", "utf-8");
			con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
			con.setUseCaches( false );
			
			// Send post request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			//wr.writeBytes(jsonString);
			wr.write(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if(responseCode != 200){
				System.out.println("PySignalHandler.doPost(): ERROR: Response not 200");
				System.out.println("\nSending 'POST' request to URL : " + PY_SIGNAL_URL);
				System.out.println("Post Data : " + jsonString);
				System.out.println("Response Code : " + responseCode);
				signal = null;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			signal = response.toString();
		} catch (MalformedURLException e) {
			System.out.println("PySignalHandler.doPost(): ERROR: MalformedURLException");
		} catch (IOException e) {
			System.out.println("PySignalHandler.doPost(): ERROR: IOException");
		}
		
		return signal;
	}
	
	/**
	 * Get Python Strategy Signal
	 * @param clPricesList
	 * @return Signal String
	 */
	public static String getSignal(List<BigDecimal> clPricesList){
		try {
			System.out.println("PySignalHandler.getSignal(): Close Price List: " + clPricesList);
			String jsonString = JacksonObjectMapper.getObjectMapper().writeValueAsString(clPricesList);
			JSONParser parser = new JSONParser();
			JSONArray jsonArr = (JSONArray) parser.parse(jsonString);
			String jsonPostData = "{\"data\":" + jsonArr.toString() + "}";
			String signal = doPost(jsonPostData);
			return signal;
		} catch (JsonProcessingException e) {
			System.out.println("PySignalHandler.getSignal(): ERROR: JsonProcessingException");
		} catch (ParseException e) {
			System.out.println("PySignalHandler.getSignal(): ERROR: ParseException");
		}
		return null;
	}
}
