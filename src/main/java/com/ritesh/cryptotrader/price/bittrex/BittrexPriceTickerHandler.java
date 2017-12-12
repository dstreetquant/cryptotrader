package com.ritesh.cryptotrader.price.bittrex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ritesh.cryptotrader.config.CoinConfig;
import com.ritesh.cryptotrader.config.IExchangeSpecificConfig;
import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.config.exchange.BittrexSpecificConfig;
import com.ritesh.cryptotrader.price.IPriceTickerHandler;
import com.ritesh.cryptotrader.price.model.TickerPrice;
import com.ritesh.cryptotrader.utils.DateTimeUtil;
import com.ritesh.cryptotrader.utils.JacksonObjectMapper;

public class BittrexPriceTickerHandler implements IPriceTickerHandler {

	private final String USER_AGENT = "Mozilla/5.0";
	private ExecutorService executor = null;
	private IExchangeSpecificConfig bittrexConfig = null;
	
	public BittrexPriceTickerHandler(ExecutorService executor){
		this.executor = executor;
		this.bittrexConfig = new BittrexSpecificConfig();
	}

	/**
	 * Get Ticker Price Map for all coins
	 */
	public Map<CoinEnum, TickerPrice> getTickedPrice() {
		Map<CoinEnum, TickerPrice> tickerMap = new HashMap<CoinEnum, TickerPrice>();
		
		//Fire the futures of price fetch
		List<Future<TickerPrice>> futList = new ArrayList<Future<TickerPrice>>();
		for(CoinEnum coin : CoinConfig.getCoinsInUse()){
			Future<TickerPrice> fut = 
					executor.submit(new BittrexExchangePriceCallable(coin));
			futList.add(fut);
		}
		
		for(Future<TickerPrice> fut : futList){
			try {
				TickerPrice tickerPrice = fut.get();
				tickerMap.put(tickerPrice.getCoin(), tickerPrice);
			} catch (InterruptedException e) {
				System.out.println("BittrexPriceTickerHandler.getTickedPrice(): ERROR: InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("BittrexPriceTickerHandler.getTickedPrice(): ERROR: ExecutionException");
				e.printStackTrace();
			}
		}
		
		return tickerMap;
	}
	
	/**
	 * Bittrex Price Fetcher Callable
	 * @author rghosh
	 */
	class BittrexExchangePriceCallable implements Callable<TickerPrice> {
		private CoinEnum coin = null;
		
		public BittrexExchangePriceCallable(CoinEnum coin){
			this.coin = coin;
		}
		
		/**
		 * Call method
		 */
		public TickerPrice call() throws Exception {
			return getExchangePrice(coin);
		}
		
		/**
		 * Hit Bittrex URL to get Ticker price of a coin
		 * @param coin
		 * @return BittrexTickerPrice
		 */
		private TickerPrice getExchangePrice(CoinEnum coin) {
			String coinStringLiteral = bittrexConfig.getCoinStringLiteral(coin);
			if(coinStringLiteral == null){
				return null;
			}
			
			String url = bittrexConfig.getTickerURL() + coinStringLiteral;
			BittrexTickerPrice tickerPrice = null;

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			// add request header
			request.addHeader("User-Agent", USER_AGENT);

			HttpResponse response;
			try {
				response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				//System.out.println("BittrexPriceTickerHandler.BittrexExchangePriceCallable.getExchangePrice(): "
						//+ "GET request to URL : " + url);
				//System.out.println("BittrexPriceTickerHandler.BittrexExchangePriceCallable.getExchangePrice(): "
						//+ "Response Code : " + response.getStatusLine().getStatusCode());
				if(statusCode != 200){
					System.out.println("BittrexPriceTickerHandler.BittrexExchangePriceCallable.getExchangePrice():"
							+ " ERROR: statusCode not 200, statuscode: " + statusCode);
					return null;
				}
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				tickerPrice = JacksonObjectMapper.getObjectMapper()
						.readValue(result.toString().toLowerCase(), BittrexTickerPrice.class);
				
				System.out.println("BittrexPriceTickerHandler.getExchangePrice(): DEBUG: coin[" + coin + "] Ticker: " + tickerPrice);
			} catch (ClientProtocolException e) {
				System.out.println("BittrexPriceTickerHandler.getExchangePrice(): ERROR: ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("BittrexPriceTickerHandler.getExchangePrice(): ERROR: IOException");
				e.printStackTrace();
			}
			
			return convertBittrexTickerPrice(coin, tickerPrice);
		}
		
		/**
		 * Convert bittrex ticker price to generic ticker price model
		 * @param coin
		 * @param bittrexPrice
		 * @return TickerPrice
		 */
		private TickerPrice convertBittrexTickerPrice(CoinEnum coin, BittrexTickerPrice bittrexPrice){
			TickerPrice tickerPrice = null;
			if(bittrexPrice != null && bittrexPrice.getSuccess()){
				tickerPrice = new TickerPrice();
				tickerPrice.setCoin(coin);
				tickerPrice.setId(coin.toString());
				tickerPrice.setDate(DateTimeUtil.getInstance().getCurrentDateOnlyInString());
				tickerPrice.setTime(DateTimeUtil.getInstance().getCurrentTimeOnlyInString());
				tickerPrice.setLastPrice(bittrexPrice.getResult().getLast());
				tickerPrice.setLowestAsk(bittrexPrice.getResult().getAsk());
				tickerPrice.setHighestBid(bittrexPrice.getResult().getBid());
				tickerPrice.setVolume(null);
			}
			return tickerPrice;
		}
	}
}
