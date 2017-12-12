package com.ritesh.cryptotrader.price.bittrex;

public class BittrexTickerPrice {

	private Boolean success;
	private String message;
	private BittrexBidAsk result;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BittrexBidAsk getResult() {
		return result;
	}
	public void setResult(BittrexBidAsk result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "BittrexTickerPrice [success=" + success + ", message=" + message + ", result=" + result + "]";
	}
}
