package com.aws.kinesisConsumer.kinesis;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;
import com.aws.kinesisConsumer.model.StockTrade;

public class DataRecordProcessor implements IRecordProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(DataRecordProcessor.class);

	private String kinesisShardId;

    // Reporting interval
    private static final long REPORTING_INTERVAL_MILLIS = 30000L; // 30Seconds
    private long nextReportingTimeInMillis;

    // Checkpointing interval
    private static final long CHECKPOINT_INTERVAL_MILLIS = 30000L; // 30Seconds
    private long nextCheckpointTimeInMillis;
    
    private StockStats stockStats = new StockStats();
	
	@Override
	public void initialize(String shardId) {
		// TODO Auto-generated method stub
		logger.info("Initializing record processor for shard: " + shardId);
        this.kinesisShardId = shardId;
        nextReportingTimeInMillis = System.currentTimeMillis() + REPORTING_INTERVAL_MILLIS;
        nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS;
		
	}

	@Override
	public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
		// TODO Auto-generated method stub
		 for (Record record : records) {
	            // process record
	            processRecord(record);
	        }

	        // If it is time to report stats as per the reporting interval, report stats
	        if (System.currentTimeMillis() > nextReportingTimeInMillis) {
	            reportStats();
	            resetStats();
	            nextReportingTimeInMillis = System.currentTimeMillis() + REPORTING_INTERVAL_MILLIS;
	        }

	        // Checkpoint once every checkpoint interval
	        if (System.currentTimeMillis() > nextCheckpointTimeInMillis) {
	            checkpoint(checkpointer);
	            nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS;
	        }
		
	}
	
	 private void reportStats() {
	        System.out.println("****** Shard " + kinesisShardId + " stats for last 1 minute ******\n" +
	                stockStats + "\n" +
	                "****************************************************************\n");
	    }

	 private void resetStats() {
	        stockStats = new StockStats();
	    }

	  private void processRecord(Record record) {
	        StockTrade trade = StockTrade.fromJsonAsBytes(record.getData().array());
	        if (trade == null) {
	            logger.warn("Skipping record. Unable to parse record into StockTrade. Partition Key: " + record.getPartitionKey());
	            return;
	        }
	        System.out.println("*******RECORD FROM KINESIS STREAM*************");
	        System.out.println(trade.toString()+ "\n" +
	                "****************************************************************\n");
	        stockStats.addStockTrade(trade);
	    }

	@Override
	public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
		// TODO Auto-generated method stub
		logger.info("Shutting down record processor for shard: " + kinesisShardId);
	        // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
	        if (reason == ShutdownReason.TERMINATE) {
	            checkpoint(checkpointer);
	        }
	}
	
	private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        logger.info("Checkpointing shard " + kinesisShardId);
        try {
            checkpointer.checkpoint();
        } catch (ShutdownException se) {
            // Ignore checkpoint if the processor instance has been shutdown (fail over).
        	logger.info("Caught shutdown exception, skipping checkpoint.", se);
        } catch (ThrottlingException e) {
            // Skip checkpoint when throttled. In practice, consider a backoff and retry policy.
        	logger.error("Caught throttling exception, skipping checkpoint.", e);
        } catch (InvalidStateException e) {
            // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
        	logger.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
        }
    }

}
