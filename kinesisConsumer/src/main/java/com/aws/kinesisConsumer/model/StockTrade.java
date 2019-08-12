package com.aws.kinesisConsumer.model;


import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StockTrade {
	
	private final static ObjectMapper JSON = new ObjectMapper();
	
	
	 public enum TradeType {
	        BUY,
	        SELL
	    }
	 
	private String tickerSymbol;
	private TradeType tradeType;
	private double price;
	private long quantity;
	private long id;
	
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "StockTrade [tickerSymbol=" + tickerSymbol + ", tradeType=" + tradeType + ", price=" + price
				+ ", quantity=" + quantity + ", id=" + id + "]";
	}
	
	public StockTrade(String tickerSymbol, TradeType tradeType, double price, long quantity, long id) {
		super();
		this.tickerSymbol = tickerSymbol;
		this.tradeType = tradeType;
		this.price = price;
		this.quantity = quantity;
		this.id = id;
	}
	
	public StockTrade() {
		
	}
	public static StockTrade fromJsonAsBytes(byte[] bytes) {
        try {
            return JSON.readValue(bytes, StockTrade.class);
        } catch (IOException e) {
            return null;
        }
    }
	 public byte[] toJsonAsBytes() {
	        try {
	            return JSON.writeValueAsBytes(this);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

}
