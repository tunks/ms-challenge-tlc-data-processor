package dev.tunks.taxitrips.batch.operations;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import dev.tunks.taxitrips.model.Location;

/***
 * Item writer concrete implementation to store Locations
 *  
 *  @author ebrimatunkara
 */
public class LocationItemWriter implements ItemWriter<Location>{
	private static final Logger logger = LoggerFactory.getLogger(LocationItemWriter.class);

    private SaveOperations<Location> saveOperations;

    public LocationItemWriter(SaveOperations<Location> saveOperations) {
		this.saveOperations = saveOperations;
	}

	@Override
	public void write(List<? extends Location> items) throws Exception {
		try {
		   saveOperations.saveAll(items);		
		}
		catch(Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}