package com.aws.KinesisProducer.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aws.KinesisProducer.kinesis.DataProducer;
import com.aws.KinesisProducer.kinesis.StockTradeGenerator;
import com.aws.KinesisProducer.model.StockPrice;
import com.aws.KinesisProducer.model.StockTrade;


@RestController
@Component
public class DataUploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(DataUploadController.class);
	
	@Autowired
	private DataProducer dataProducer;
	
	@Autowired
	private StockTradeGenerator stocktradeGenerator;
	
	@Value(value = "${aws_stream_name}")
	private String awsStreamName;


	@PostMapping("/uploadToStream")
	public ResponseEntity<String> dataUpload(@RequestBody List<StockPrice> stockPrices) {
		try {
		while(true)
		{
		StockTrade stockTrade = stocktradeGenerator.getRandomTrade(stockPrices);
		dataProducer.sendStockToKinesis(awsStreamName, stockTrade);
		Thread.sleep(10000);
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok("Data Uploaded to Kinesis");
	}


}
