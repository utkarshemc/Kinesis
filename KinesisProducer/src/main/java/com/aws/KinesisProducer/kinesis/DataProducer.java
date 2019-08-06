package com.aws.KinesisProducer.kinesis;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordFailedException;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.aws.KinesisProducer.model.StockTrade;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Component
public class DataProducer {

	private static final Logger logger = LoggerFactory.getLogger(DataProducer.class);

	@Value(value = "${aws_stream_name}")
	private String awsStreamName;

	@Value(value = "${aws_region}")
	private String awsRegion;

	private KinesisProducer kinesisProducer = null;

	private final AtomicLong recordsPut = new AtomicLong(0);
	
	 public static AWSCredentialsProvider getCredentialsProvider() throws Exception {
	        /*
	         * The ProfileCredentialsProvider will return your [default] credential profile by
	         * reading from the credentials file located at (~/.aws/credentials).
	         */
	        AWSCredentialsProvider credentialsProvider = null;
	        try {
	            //credentialsProvider = new ProfileCredentialsProvider("dellAdmin");
	        	credentialsProvider = new DefaultAWSCredentialsProviderChain();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (~/.aws/credentials), and is in valid format.",
	                    e);
	        }
	        return credentialsProvider;
	    }

	public KinesisProducer getKinesisProducer() {
		if (kinesisProducer == null) {
			try {
			KinesisProducerConfiguration config = new KinesisProducerConfiguration();
			config.setRegion(awsRegion);
			//BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAcsessKey, awsSecretKey);
			AWSCredentialsProvider awsCreds = getCredentialsProvider();
			//config.setCredentialsProvider(new AWSStaticCredentialsProvider(awsCreds));
			config.setCredentialsProvider(awsCreds);
			config.setMaxConnections(1);
			config.setRequestTimeout(6000); // 6 seconds
			config.setRecordMaxBufferedTime(5000);// 5 seconds
			kinesisProducer = new KinesisProducer(config);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return kinesisProducer;
	}

	public void sendStockToKinesis(String streamName, StockTrade stockTrade) {
		byte[] stockTradeBytes = stockTrade.toJsonAsBytes();
		// The bytes could be null if there is an issue with the JSON serialization by
		// the Jackson JSON library.
		if (stockTradeBytes == null) {
			logger.info("Cannot convert stockTrade object to Bytes");
			return;
		}
		kinesisProducer = getKinesisProducer();
		ByteBuffer data = ByteBuffer.wrap(stockTradeBytes);

		ListenableFuture<UserRecordResult> f = kinesisProducer.addUserRecord(streamName, stockTrade.getTickerSymbol(),
				data);
		Futures.addCallback(f, new FutureCallback<UserRecordResult>() {

			@Override
			public void onFailure(Throwable t) {
				// TODO Auto-generated method stub
				logger.info("Failed to put data into Kinesis");
				if (t instanceof UserRecordFailedException) {
					UserRecordFailedException e = (UserRecordFailedException) t;
					UserRecordResult result = e.getResult();
					logger.info("Result {}", result.isSuccessful());

				}

			}

			@Override
			public void onSuccess(UserRecordResult arg0) {
				logger.info(
						"------------------------------------------------------DATA INTO STREAM------------------------------------------------------------------");
				logger.info(stockTrade.toString());
				logger.info("Succesfully put data into Kinesis");
				logger.info(
						"----------------------------------------------------------------------------------------------------------------------------------------");
			}
		});

	}

	public void stop() {
		if (kinesisProducer != null) {
			kinesisProducer.flushSync();
			kinesisProducer.destroy();
		}
	}

}
