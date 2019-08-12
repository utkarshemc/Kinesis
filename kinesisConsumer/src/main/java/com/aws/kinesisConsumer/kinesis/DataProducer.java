package com.aws.kinesisConsumer.kinesis;

import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.aws.kinesisConsumer.model.StockPrice;
import com.aws.kinesisConsumer.model.StockTrade;

@Component
public class DataProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(DataConsumer.class);
	
	@Value(value = "${applicationName}")
	private static String applicationName;
	
	@Value(value = "${aws_region}")
	private String awsRegion;
	
	 private static final String APPLICATION_NAME = applicationName;
	 private static final String VERSION = "1.0.0";
	 
	 @Autowired
	private StockTradeGenerator stocktradeGenerator;
	
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
	
	 
	
	/*public AmazonKinesis getKinesisClientConsumer() {
		try {
			AWSCredentialsProvider credentialsProvider = getCredentialsProvider();
			AmazonKinesis kinesisClient = new AmazonKinesisClient(awsCredentials);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAcsessKey, awsSecretKey);
		

		return kinesisClient;
	}*/
	 
	 public static ClientConfiguration getClientConfigWithUserAgent() {
	        final ClientConfiguration config = new ClientConfiguration();
	        final StringBuilder userAgent = new StringBuilder(ClientConfiguration.DEFAULT_USER_AGENT);

	        // Separate fields of the user agent with a space
	        userAgent.append(" ");
	        // Append the application name followed by version number of the sample
	        userAgent.append(applicationName);
	        userAgent.append("/");
	        userAgent.append(VERSION);

	        config.setUserAgentPrefix(userAgent.toString());
	        config.setUserAgentSuffix(null);

	        return config;
	    }
	
	public void sendStockToKinesis(String streamName, List<StockPrice> stockPrices) {
		 
		 try {
			 AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
			 clientBuilder.setRegion(awsRegion);
			 clientBuilder.setCredentials(getCredentialsProvider());
			 clientBuilder.setClientConfiguration(getClientConfigWithUserAgent());
			 AmazonKinesis kinesisClient = clientBuilder.build();
			 while(true) {
		            StockTrade trade = stocktradeGenerator.getRandomTrade(stockPrices);
		            sendStockTrade(trade, kinesisClient, streamName);
		            Thread.sleep(100);
		        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private static void sendStockTrade(StockTrade trade, AmazonKinesis kinesisClient,
            String streamName){
	        byte[] bytes = trade.toJsonAsBytes();
	        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
	        if (bytes == null) {
	        	logger.warn("Could not get JSON bytes for stock trade");
	            return;
	        }

	        logger.info("Putting trade: " + trade.toString());
	        PutRecordRequest putRecord = new PutRecordRequest();
	        putRecord.setStreamName(streamName);
	        // We use the ticker symbol as the partition key, as explained in the tutorial.
	        putRecord.setPartitionKey(trade.getTickerSymbol());
	        putRecord.setData(ByteBuffer.wrap(bytes));

	        try {
	            kinesisClient.putRecord(putRecord);
	        } catch (AmazonClientException ex) {
	        	logger.warn("Error sending record to Amazon Kinesis.", ex);
	        }
	    }
	

}
