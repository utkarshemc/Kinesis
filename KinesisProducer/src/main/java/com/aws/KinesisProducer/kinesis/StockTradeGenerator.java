package com.aws.KinesisProducer.kinesis;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.aws.KinesisProducer.model.StockPrice;
import com.aws.KinesisProducer.model.StockTrade;

@Component
public class StockTradeGenerator {
	
	  /** The ratio of the deviation from the mean price **/
    private static final double MAX_DEVIATION = 0.2; // ie 20%

    /** The number of shares is picked randomly between 1 and the MAX_QUANTITY **/
    private static final int MAX_QUANTITY = 10000;

    /** Probability of trade being a sell **/
    private static final double PROBABILITY_SELL = 0.4; // ie 40%

    private final Random random = new Random();
    private AtomicLong id = new AtomicLong(1);
    
    public StockTrade getRandomTrade(List<StockPrice> stockPrices) {
    	
    	 // pick a random stock
        StockPrice stockPrice = stockPrices.get(random.nextInt(stockPrices.size()));
        // pick a random deviation between -MAX_DEVIATION and +MAX_DEVIATION
        double deviation = (random.nextDouble() - 0.5) * 2.0 * MAX_DEVIATION;
        // set the price using the deviation and mean price
        double price = stockPrice.getStockPrice() * (1 + deviation);
        // round price to 2 decimal places
        price = Math.round(price * 100.0) / 100.0;

        // set the trade type to buy or sell depending on the probability of sell
        StockTrade.TradeType tradeType = StockTrade.TradeType.BUY;
        if (random.nextDouble() < PROBABILITY_SELL) {
            tradeType = StockTrade.TradeType.SELL;
        }

        // randomly pick a quantity of shares
        long quantity = random.nextInt(MAX_QUANTITY) + 1; // add 1 because nextInt() will return between 0 (inclusive)
                                                          // and MAX_QUANTITY (exclusive). we want at least 1 share.

        return new StockTrade(stockPrice.getStockName(), tradeType, price, quantity, id.getAndIncrement());
    }

}
