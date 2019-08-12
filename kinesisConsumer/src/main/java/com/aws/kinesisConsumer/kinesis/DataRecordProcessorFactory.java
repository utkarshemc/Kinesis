package com.aws.kinesisConsumer.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;

public class DataRecordProcessorFactory implements IRecordProcessorFactory {
	
	

	public DataRecordProcessorFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public IRecordProcessor createProcessor() {
		// TODO Auto-generated method stub
		return new DataRecordProcessor();
	}

}
