package dev.tunks.taxitrips.batch.operations;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import dev.tunks.taxitrips.model.TaxiTrip;

/***
 * Item writer concrete implementation to store Taxi trips
 *  
 *  @author ebrimatunkara
 */
public class TaxiTripItemWriter implements ItemWriter<TaxiTrip>{
	private static final Logger logger = LoggerFactory.getLogger(LocationItemWriter.class);
    private SaveOperations<TaxiTrip> saveOperations;
    
	public TaxiTripItemWriter(SaveOperations<TaxiTrip> saveOperations) {
		this.saveOperations = saveOperations;
	}

	@Override
	public void write(List<? extends TaxiTrip> items) throws Exception {
		try {
		   saveOperations.saveAll(items);
		}
		catch(Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}