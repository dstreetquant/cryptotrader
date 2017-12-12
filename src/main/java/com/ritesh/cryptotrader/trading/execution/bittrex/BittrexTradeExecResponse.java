package com.ritesh.cryptotrader.trading.execution.bittrex;

public class BittrexTradeExecResponse {

	private Boolean success;
	private String message;
	private BittrexTradeResult result;
	
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
	public BittrexTradeResult getResult() {
		return result;
	}
	public void setResult(BittrexTradeResult result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "BittrexTradeExecResponse [success=" + success + ", message=" + message + ", result=" + result + "]";
	}
}
