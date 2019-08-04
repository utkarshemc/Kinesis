package com.aws.KinesisProducer.model;

public class StockPrice {
	
	private String stockName;
	
	private double stockPrice;

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public double getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(double stockPrice) {
		this.stockPrice = stockPrice;
	}

	@Override
	public String toString() {
		return "StockPrice [stockName=" + stockName + ", stockPrice=" + stockPrice + "]";
	}
	
	

}
