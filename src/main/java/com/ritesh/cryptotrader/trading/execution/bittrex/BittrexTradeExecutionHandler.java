package com.ritesh.cryptotrader.trading.execution.bittrex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ritesh.cryptotrader.config.IExchangeSpecificConfig;
import com.ritesh.cryptotrader.config.exchange.BittrexSpecificConfig;
import com.ritesh.cryptotrader.trading.execution.ITradeExecutionHandler;
import com.ritesh.cryptotrader.trading.model.TradeExecuted;
import com.ritesh.cryptotrader.trading.model.TradeToExecute;
import com.ritesh.cryptotrader.trading.model.TradeType;
import com.ritesh.cryptotrader.utils.DateTimeUtil;
import com.ritesh.cryptotrader.utils.JacksonObjectMapper;

public class BittrexTradeExecutionHandler implements ITradeExecutionHandler{
	
	private final String USER_AGENT = "Mozilla/5.0";
	private IExchangeSpecificConfig bittrexConfig = null;
	
	public BittrexTradeExecutionHandler(){
		this.bittrexConfig = new BittrexSpecificConfig();
	}

	@Override
	public TradeExecuted executeTrade(TradeToExecute tradeToExecute) {
		String tradingUrl = bittrexConfig.getTradeUrl();
		String tradeType = null;
		if(tradeToExecute.getTradeType() == TradeType.LONG 
				|| tradeToExecute.getTradeType() == TradeType.SHORT_COVER){
			tradeType = "buylimit";
		} else if(tradeToExecute.getTradeType() == TradeType.SHORT 
				|| tradeToExecute.getTradeType() == TradeType.LONG_SQUAREOFF){
			tradeType = "selllimit";
		} else{
			System.out.println("BittrexTradeExecutionHandler.executeTrade(): ERROR: Invalid TradeType");
		}
		String apiKey = bittrexConfig.getAPIKey();
		String market = bittrexConfig.getCoinStringLiteral(tradeToExecute.getCoin());
		String qty = tradeToExecute.getQty().toString();
		String rate = null;
		return execute(tradeToExecute, tradingUrl, tradeType, apiKey, market, qty, rate);
	}
	
	/**
	 * Hit Exchange URL for placing the trade
	 * @param tradingUrl
	 * @param tradeType
	 * @param apiKey
	 * @param market
	 * @param qty
	 * @param rate
	 * @return TradeExecuted
	 */
	private TradeExecuted execute(TradeToExecute tradeToExecute, String tradingUrl, String tradeType, 
			String apiKey, String market, String qty, String rate){
		BittrexTradeExecResponse tradeExecResponse = null;
		
		if(tradingUrl != null && tradeType != null 
				&& apiKey != null && market != null && qty != null){
			String url = tradingUrl + "/" + tradeType + "?" + 
				"apikey=" + apiKey + "&market=" + market + "&quantity=" + qty;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			// add request header
			request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response;
			try {
				response = client.execute(request);
				System.out.println("BittrexTradeExecutionHandler.execute(): "
						+ "GET request to URL : " + url);
				System.out.println("BittrexTradeExecutionHandler.execute(): "
						+ "Response Code : " + response.getStatusLine().getStatusCode());
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				tradeExecResponse = JacksonObjectMapper.getObjectMapper()
						.readValue(result.toString().toLowerCase(), BittrexTradeExecResponse.class);
				
				System.out.println("DEBUG: " + tradeExecResponse);
			} catch (ClientProtocolException e) {
				System.out.println("BittrexTradeExecutionHandler.execute(): ERROR: ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("BittrexTradeExecutionHandler.execute(): ERROR: IOException");
				e.printStackTrace();
			}
		}
		
		if(tradeExecResponse == null){
			System.out.println("BittrexTradeExecutionHandler.execute(): ERROR: Trade NOT executed");
			return null;
		} else if(!tradeExecResponse.getSuccess()){
			System.out.println("BittrexTradeExecutionHandler.execute(): ERROR: Trade execution FAILED at Bittrex");
			return null;
		} else{
			return convertBittrexTradeExecResp(tradeToExecute, tradeExecResponse);
		}
	}
	
	/**
	 * converter from exchange result to Data Structure
	 * @param tradeToExecute
	 * @param tradeExecResponse
	 * @return TradeExecuted instance
	 */
	private TradeExecuted convertBittrexTradeExecResp(TradeToExecute tradeToExecute, BittrexTradeExecResponse tradeExecResponse){
		TradeExecuted tradeExecuted = new TradeExecuted();
		tradeExecuted.setCoin(tradeToExecute.getCoin());
		tradeExecuted.setOrderId(tradeExecResponse.getResult().getUuid());
		tradeExecuted.setTradeType(tradeToExecute.getTradeType());
		tradeExecuted.setQty(tradeToExecute.getQty());
		tradeExecuted.setDate(DateTimeUtil.getInstance().getCurrentDateOnlyInString());
		tradeExecuted.setTime(DateTimeUtil.getInstance().getCurrentTimeOnlyInString());
		tradeExecuted.setExchangeTimeStamp(null);
		
		return tradeExecuted;
	}
}
