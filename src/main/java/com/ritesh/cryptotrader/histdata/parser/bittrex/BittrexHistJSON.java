package com.ritesh.cryptotrader.histdata.parser.bittrex;

import java.util.Arrays;

public class BittrexHistJSON {

	private Boolean success;
	private String message;
	private BittrexHistOHLCV[] result;
	
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
	public BittrexHistOHLCV[] getResult() {
		return result;
	}
	public void setResult(BittrexHistOHLCV[] result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "BittrexHistJSON [success=" + success + ", message=" + message + ", result=" + Arrays.toString(result)
				+ "]";
	}
}
