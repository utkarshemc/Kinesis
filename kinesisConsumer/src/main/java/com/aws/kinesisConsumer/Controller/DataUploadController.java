package com.aws.kinesisConsumer.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aws.kinesisConsumer.kinesis.DataProducer;
import com.aws.kinesisConsumer.kinesis.StockTradeGenerator;
import com.aws.kinesisConsumer.model.StockPrice;
import com.aws.kinesisConsumer.model.StockTrade;



@RestController
@Component
public class DataUploadController {
	
	@Autowired
	private StockTradeGenerator stocktradeGenerator;
	
	@Value(value = "${aws_stream_name}")
	private String awsStreamName;
	
	@Autowired
	private DataProducer dataProducer;
	
	@PostMapping("/uploadToStream")
	public ResponseEntity<String> dataUpload(@RequestBody List<StockPrice> stockPrices) {
		try {
		dataProducer.sendStockToKinesis(awsStreamName, stockPrices);
		Thread.sleep(10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok("Data Uploaded to Kinesis");
	}


}
