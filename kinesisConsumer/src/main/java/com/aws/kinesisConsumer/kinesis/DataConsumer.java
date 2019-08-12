package com.aws.kinesisConsumer.kinesis;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;

@Component
public class DataConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(DataConsumer.class);
	
	@Value(value = "${aws_region}")
	private String awsRegion;
	
	@Value(value = "${aws_stream_name}")
	private String awsStreamName;
	
	/*@Value(value = "${aws_access_key}")
	private String awsAcsessKey;

	@Value(value = "${aws_secret_key}")
	private String awsSecretKey;
	*/
	@Value(value = "${applicationName}")
	private String applicationName;
	
	private String workerId = String.valueOf(UUID.randomUUID());
	
	private KinesisClientLibConfiguration kinesisClientLibConfiguration = null;
	
	 public static AWSCredentialsProvider getCredentialsProvider() throws Exception {
	        /*
	         * The ProfileCredentialsProvider will return your [default] credential profile by
	         * reading from the credentials file located at (~/.aws/credentials).
	         */
	        AWSCredentialsProvider credentialsProvider = null;
	        try {
	            //credentialsProvider = new ProfileCredentialsProvider("admin");
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
	
	public KinesisClientLibConfiguration getKinesisClientConsumer() {
		try {
			AWSCredentialsProvider credentialsProvider = getCredentialsProvider();
			kinesisClientLibConfiguration = new KinesisClientLibConfiguration(applicationName, awsStreamName,
					credentialsProvider, workerId).withRegionName(awsRegion);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAcsessKey, awsSecretKey);
		

		return kinesisClientLibConfiguration;
	}
	
	public void readFromKinesis()
	{
		kinesisClientLibConfiguration = getKinesisClientConsumer();
		IRecordProcessorFactory recordProcessorFactory = new DataRecordProcessorFactory();
		Worker worker = new Worker(recordProcessorFactory, kinesisClientLibConfiguration);
		int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
        	logger.error("Caught throwable while processing data.", t);
            exitCode = 1;
        }
        System.exit(exitCode);
	}

}
