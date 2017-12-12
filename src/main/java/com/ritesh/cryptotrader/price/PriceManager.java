package com.ritesh.cryptotrader.price;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ritesh.cryptotrader.config.CoinConfig;
import com.ritesh.cryptotrader.config.ExchangeConfig;
import com.ritesh.cryptotrader.config.PriceConfig;
import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.price.model.TickerPrice;

public class PriceManager {

	private Map<CoinEnum, BlockingQueue<Object>> priceTickerQMap = null;
	private PriceFetcherRunnable priceFetcherRunnable = null;
	
	public PriceManager(Map<CoinEnum, BlockingQueue<Object>> priceTickerQMap){
		this.priceTickerQMap = priceTickerQMap;
	}
	
	/**
	 * start price manager
	 */
	public void start(){
		priceFetcherRunnable = new PriceFetcherRunnable();
		Thread priceFetcherThread = new Thread(priceFetcherRunnable);
		priceFetcherThread.start();
	}
	
	/**
	 * stop price manager
	 */
	public void stop(){
		priceFetcherRunnable.stop();
	}
	
	/**
	 * price manager thread
	 * @author rghosh
	 */
	class PriceFetcherRunnable implements Runnable {
		private boolean stopFlag = true;
		ExecutorService executor = Executors.newFixedThreadPool(CoinConfig.getCoinsInUse().length);
		
		@Override
		public void run() {
			while(stopFlag){
				
				Map<CoinEnum, TickerPrice> coinMap = 
						ExchangeConfig.getPriceTickerHandler(executor).getTickedPrice();
				if(coinMap != null){
					Set<CoinEnum> coinSet = coinMap.keySet();
					for(CoinEnum coin : coinSet){
						TickerPrice tickerPrice = coinMap.get(coin);
						
						if(tickerPrice != null){
							//send to price ticker Q
							PriceTickerCommand priceTickerCommand = new PriceTickerCommand();
							priceTickerCommand.setCommand(PriceConstants.PRICE_TICKER);
							priceTickerCommand.setTickerPrice(tickerPrice);
							try {
								priceTickerQMap.get(coin).put(priceTickerCommand);
							} catch (InterruptedException e) {
								System.out.println("PriceManager.PriceFetcherRunnable.run(): ERROR: "
										+ "in putting ticker price into priceTicker command Q for coin: " + coin);
							}
						} else{
							System.out.println("PriceManager.PriceFetcherRunnable.run(): ERROR: "
									+ "tickerPrice is null for coin: " + coin);
						}
					}
				} else{
					System.out.println("PriceManager.PriceFetcherRunnable.run(): ERROR: coinMap of Ticker Price is null");
				}
				
				try {
					Thread.sleep(PriceConfig.getPriceFetchPeriod());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stop(){
			stopFlag = false;
		}
	}
	
	/**
	 * Test Main
	 */
	public static void main(String[] args){
		PriceManager mgr = new PriceManager(null);
		mgr.start();
	}
}
