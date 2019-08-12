package com.aws.kinesisConsumer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aws.kinesisConsumer.kinesis.DataConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Component
public class DataDownloadController {
	
	@Autowired
	private DataConsumer dataConsumer;
	
	
	//private DataConsumer data;

	@GetMapping("/getStreams")
	public ResponseEntity<String> dataUpload() {
		try {
			dataConsumer.readFromKinesis();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok("Data Downloaded from Kinesis");
	}

}
