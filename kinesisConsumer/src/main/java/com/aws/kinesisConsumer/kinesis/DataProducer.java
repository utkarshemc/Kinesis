package com.aws.kinesisConsumer.kinesis;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.aws.kinesisConsumer.model.StockPrice;
import com.aws.kinesisConsumer.model.StockTrade;

@Component
public class DataProducer {
	
	
	@Value(value = "${applicationName}")
	private String applicationName;
	
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
	
	public void sendStockToKinesis(String streamName, List<StockPrice> stockPrices) {
		
		
		 
		 
		
	}

}
