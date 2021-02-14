package dev.tunks.taxitrips.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
/***
 * 
 * Spring batch job pipeline configuration for New York city vehicle taxi trips data.
 * The data is automatically fetched, processed and stored into MongoDB
 * @see https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page 
 *
 * @author ebrimatunkara
 **/
@Configuration
@EnableBatchProcessing
public class BatchJobConfiguration{

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
}
