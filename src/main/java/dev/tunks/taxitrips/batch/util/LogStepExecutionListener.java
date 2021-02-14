package dev.tunks.taxitrips.batch.util;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LogStepExecutionListener implements StepExecutionListener{
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
       System.out.println("Before step: "+stepExecution);	
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		//stepExecution.getStepName()
	    System.out.println("After step: "+stepExecution);	
		return stepExecution.getExitStatus();
	}

}
