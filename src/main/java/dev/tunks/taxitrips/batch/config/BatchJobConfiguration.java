package dev.tunks.taxitrips.batch.config;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import dev.tunks.taxitrips.batch.operations.BatchSaveOperations;
import dev.tunks.taxitrips.batch.operations.LocationItemWriter;
import dev.tunks.taxitrips.batch.operations.SaveOperations;
import dev.tunks.taxitrips.batch.operations.TaxiTripItemWriter;
import dev.tunks.taxitrips.batch.util.DataUtil;
import dev.tunks.taxitrips.batch.util.DefaultResourcePartitioner;
import dev.tunks.taxitrips.batch.util.LocationZoneMapper;
import dev.tunks.taxitrips.batch.util.LogStepExecutionListener;
import dev.tunks.taxitrips.batch.util.TaxiTripItemProcessor;
import dev.tunks.taxitrips.batch.util.TaxiTripMapper;
import dev.tunks.taxitrips.model.Location;
import dev.tunks.taxitrips.model.TaxiTrip;
import dev.tunks.taxitrips.model.TaxiType;
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
	
	@Autowired
	private FlatFileItemReader<TaxiTrip> tripFileItemReader;

	@Autowired
	private ItemProcessor<TaxiTrip, TaxiTrip> tripItemProcessor;
	
	@Value("${data.taxi_zone.url}")
	private Resource taxiZoneResource;
	  
    @Value("#{'${data.yellow_taxi.urls}'.split(',')}")
	private Resource[] yellowTaxiResources;
    
    @Value("${data.yellow_taxi.field_names}")
    private String[] yellowTaxiFields;
    
    @Value("${data.yellow_taxi.field_indexes}")
    private int[] yellowTaxiFieldIndexes;
    
    @Value("#{'${data.green_taxi.urls}'.split(',')}")
  	private Resource[] greenTaxiResources;
    
    @Value("${data.green_taxi.field_names}")
    private String[] greenTaxiFields;
    
    @Value("${data.green_taxi.field_indexes}")
    private int[] greenTaxiFieldIndexes;
    
    @Value("#{'${data.rfv_taxi.urls}'.split(',')}")
  	private Resource[] rfvTaxiResources;
    
    @Value("${data.rfv_taxi.field_names}")
    private String[] rfvTaxiFields;
    
    @Value("${data.rfv_taxi.field_indexes}")
    private int[] rfvTaxiFieldIndexes;
	
    
    
	@Bean
	public TaskExecutor taskExecutor() {
	       ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	       executor.setCorePoolSize(100);
	       executor.setMaxPoolSize(100);
	       executor.setQueueCapacity(100);
	       executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	       executor.setThreadNamePrefix("spring-batch");
	       return executor;
	}
	
	@Bean("batchJob")
	public Job batchJob() {
		 //Split the main steps into parallel flow executions
		 Flow flow1 = new FlowBuilder<Flow>("flow1").from(yellowTaxiMasterStep()).end();
	     Flow flow2 = new FlowBuilder<Flow>("flow2").from(greenTaxiMasterStep()).end();
	     Flow flow3 = new FlowBuilder<Flow>("flow3").from(rfvTaxiMasterStep()).end();
	     
		 Flow splitFlow = new FlowBuilder<Flow>("splitFlow")
					        .start(taxiZoneMasterStep())
					        .split(taskExecutor())
					        .add(flow1,flow2,flow3)
					        .build();	
		return jobBuilderFactory
				.get("dataProcessingJob()")
				.incrementer(new RunIdIncrementer())
				.flow(taxiZoneMasterStep())
				.next(splitFlow)
				.end()
				.build();
	}
	
    /**
     * 
     * Batch workflow step configurations
     **/
	//Taxi zone master step - read, process and write taxi location zone data
	@Bean
	public Step taxiZoneMasterStep() {
		return stepBuilderFactory
				.get("taxiZoneMasterStep")
				.<Location, Future<Location>>chunk(500)
				.reader(zoneItemReader())
				.processor(asyncZoneProcessor())
				.writer(asyncZoneItemWriter())
				.listener(new LogStepExecutionListener())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	//Yellow Taxi master step - to manage and execute its corresponding slave step
	@Bean
	public Step yellowTaxiMasterStep() {
		Map<String,Object> config = new HashMap<String,Object>();
		config.put("taxiType", TaxiType.YELLOW);
		config.put("fieldNames", yellowTaxiFields);
		config.put("fieldIndexes", yellowTaxiFieldIndexes);
		Partitioner partitioner = new DefaultResourcePartitioner(config, yellowTaxiResources);
		return stepBuilderFactory
				.get("yellowTaxiMasterStep")
				.partitioner("yellowTaxiSlaveStep",partitioner)				
				.step(yellowTaxiSlaveStep())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	//Yellow Taxi slave step - to read, process and write yellow taxi trip data
	@Bean
	public Step yellowTaxiSlaveStep() {
		return stepBuilderFactory
				.get("yellowTaxiSlaveStep")
				.<TaxiTrip, Future<TaxiTrip>>chunk(500)
				.reader(tripFileItemReader)
				.processor(asyncTripProcessor())
				.writer(asyncTaxiTripItemWriter())
				.listener(new LogStepExecutionListener())
				.taskExecutor(taskExecutor())
				.throttleLimit(100)
				.build();
	}
	
	//Green Taxi master step - to manage and execute its corresponding slave step
	@Bean
	public Step greenTaxiMasterStep() {
		Map<String,Object> config = new HashMap<String,Object>();
		config.put("taxiType", TaxiType.GREEN);
		config.put("fieldNames", greenTaxiFields);
		config.put("fieldIndexes", greenTaxiFieldIndexes);
		Partitioner partitioner = new DefaultResourcePartitioner(config, greenTaxiResources);
		return stepBuilderFactory
				.get("greenTaxiMasterStep")
				.partitioner("greenTaxiSlaveStep", partitioner)				
				.step(greenTaxiSlaveStep())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	//Green Taxi slave step - to read, process and write green taxi trip data
	@Bean
	public Step greenTaxiSlaveStep() {
		return stepBuilderFactory
				.get("greenTaxiSlaveStep")
				.<TaxiTrip, Future<TaxiTrip>>chunk(500)
				.reader(tripFileItemReader)
				.processor(asyncTripProcessor())
				.writer(asyncTaxiTripItemWriter())
				.listener(new LogStepExecutionListener())
				.taskExecutor(taskExecutor())
				.throttleLimit(100)
				.build();
	}
	
	//RFV Taxi master step - to manage and execute its corresponding slave step
	@Bean
	public Step rfvTaxiMasterStep() {
		Map<String,Object> config = new HashMap<String,Object>();
		config.put("taxiType", TaxiType.RHV);
		config.put("fieldNames", rfvTaxiFields);
		config.put("fieldIndexes", rfvTaxiFieldIndexes);
		Partitioner partitioner = new DefaultResourcePartitioner(config, rfvTaxiResources);
		return stepBuilderFactory
				.get("rfvTaxiMasterStep")
				.partitioner("rfvTaxiSlaveStep", partitioner)				
				.step(rfvTaxiSlaveStep())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	//Green Taxi slave step - to read, process and write green taxi trip data
	@Bean
	public Step rfvTaxiSlaveStep() {
		return stepBuilderFactory
				.get("rfvTaxiSlaveStep")
				.<TaxiTrip, Future<TaxiTrip>>chunk(500)
				.reader(tripFileItemReader)
				.processor(asyncTripProcessor())
				.writer(asyncTaxiTripItemWriter())
				.listener(new LogStepExecutionListener())
				.taskExecutor(taskExecutor())
				.throttleLimit(100)
				.build();
	}
	
	/**
	 * 
	 * Data item reader beans for read and parse the file data
	 */
	@Bean
    public FlatFileItemReader<Location> zoneItemReader() {
        return new FlatFileItemReaderBuilder<Location> ()
            .name("zoneItemReader")
            .resource(taxiZoneResource)
            .linesToSkip(1)
            .lineMapper(zoneLineMapper())
            .build();
    }
	
	@Bean
	@StepScope
	public FlatFileItemReader<TaxiTrip> tripFileItemReader(LineMapper<TaxiTrip> tripLineMapper, 
			@Value("#{stepExecutionContext['fileName']}") String fileName) throws MalformedURLException {
		FlatFileItemReader<TaxiTrip> reader = new FlatFileItemReader<TaxiTrip>();
		reader.setLineMapper(tripLineMapper);
		reader.setResource(new UrlResource(fileName));
		//Skip the first header line
		reader.setLinesToSkip(1);
		reader.setMaxItemCount(100);
		return reader;
	}
	
	@Bean
	@StepScope
    public LineMapper<TaxiTrip> tripLineMapper(@Value("#{stepExecutionContext['fieldNames']}") String[] fieldNames, 
    		@Value("#{stepExecutionContext['fieldIndexes']}") int[] fieldIndexes, @Value("#{stepExecutionContext[taxiType]}") String taxiType)
	 {
        DefaultLineMapper<TaxiTrip> lineMapper = new DefaultLineMapper<TaxiTrip>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(fieldNames);
        lineTokenizer.setIncludedFields(fieldIndexes);
        lineTokenizer.setStrict(false);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new TaxiTripMapper(TaxiType.valueOf(taxiType)));
        return lineMapper;
    }
	
	@Bean
    public LineMapper<Location> zoneLineMapper() {
        DefaultLineMapper<Location> lineMapper = new DefaultLineMapper<Location>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(DataUtil.getZoneDataFields());
        lineTokenizer.setIncludedFields(DataUtil.getZoneDataIndexes());
        lineTokenizer.setStrict(false);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new LocationZoneMapper());
        return lineMapper;
    }
	
	/**
	 * 
	 * Data item processor beans for asynchronous processing of data
	 */
	@Bean
	public AsyncItemProcessor<TaxiTrip, TaxiTrip> asyncTripProcessor() {
		AsyncItemProcessor<TaxiTrip, TaxiTrip> asyncItemProcessor = new AsyncItemProcessor<TaxiTrip, TaxiTrip>();
		asyncItemProcessor.setDelegate(tripItemProcessor);
		asyncItemProcessor.setTaskExecutor(taskExecutor());
		return asyncItemProcessor;
	}
	
	@Bean
	public AsyncItemProcessor<Location, Location> asyncZoneProcessor() {
		AsyncItemProcessor<Location, Location> asyncItemProcessor = new AsyncItemProcessor<Location, Location>();
		asyncItemProcessor.setDelegate( zoneItemProcessor());
		asyncItemProcessor.setTaskExecutor(taskExecutor());
		return asyncItemProcessor;
	}

	@Bean
	@StepScope
	public ItemProcessor<TaxiTrip, TaxiTrip> tripItemProcessor(@Value("#{stepExecutionContext[taxiType]}") TaxiType taxiType){	
		return new TaxiTripItemProcessor(taxiType);
	}
	
	@Bean
	@StepScope
	public ItemProcessor<Location, Location> zoneItemProcessor() {	 
		return (entity) -> {return entity;};
	}

	/**
	 * 
	 * Data item writer beans for asynchronous writing of data into MongoDB
	 */
	@Bean
	public AsyncItemWriter<TaxiTrip> asyncTaxiTripItemWriter() {
		AsyncItemWriter<TaxiTrip> asyncItemWriter = new AsyncItemWriter<>();
		SaveOperations<TaxiTrip> saveOperations = new BatchSaveOperations<TaxiTrip>(mongoTemplate,DataUtil.TRIPS_COLLECTION);
		asyncItemWriter.setDelegate(new TaxiTripItemWriter(saveOperations));
		return asyncItemWriter;
	}
	
	@Bean
	public AsyncItemWriter<Location> asyncZoneItemWriter() {
		AsyncItemWriter<Location> asyncItemWriter = new AsyncItemWriter<Location>();
		SaveOperations<Location> saveOperations = new BatchSaveOperations<Location>(mongoTemplate,DataUtil.LOCATION_COLLECTION);
		asyncItemWriter.setDelegate(new LocationItemWriter(saveOperations));
		return asyncItemWriter;
	}
	
}
