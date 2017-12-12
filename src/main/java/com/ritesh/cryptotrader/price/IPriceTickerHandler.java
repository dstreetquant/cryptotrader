package com.ritesh.cryptotrader.price;

import java.util.Map;

import com.ritesh.cryptotrader.config.enums.CoinEnum;
import com.ritesh.cryptotrader.price.model.TickerPrice;

public interface IPriceTickerHandler {

	public Map<CoinEnum, TickerPrice> getTickedPrice();
}
