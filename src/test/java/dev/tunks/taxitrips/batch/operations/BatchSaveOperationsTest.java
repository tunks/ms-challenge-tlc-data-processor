package dev.tunks.taxitrips.batch.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import dev.tunks.taxitrips.batch.BatchDataProcessorApplication;
import dev.tunks.taxitrips.batch.util.DataUtil;
import dev.tunks.taxitrips.model.TaxiTrip;


@SpringBootTest(classes = {BatchDataProcessorApplication.class})
public class BatchSaveOperationsTest {
	@Autowired
	private MongoTemplate mongoTemplate;
	
    private BatchSaveOperations<TaxiTrip> saveOperations;
	private List<TaxiTrip> trips;
	
    @BeforeEach
	public void setUp() throws Exception {
		trips = new ArrayList<TaxiTrip>();
		for(int i = 1; i<=10; i++) {
			trips.add(createTaxiTrip());
		}
    	saveOperations = new BatchSaveOperations<TaxiTrip>(mongoTemplate,DataUtil.TRIPS_COLLECTION);
	}

	@AfterEach
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testSaveAll() {
		saveOperations.saveAll(trips);
	}
	
	private TaxiTrip createTaxiTrip() {
		TaxiTrip trip = new TaxiTrip();
		trip.setPickupLocationId("1");
		trip.setDropoffLocationId("2");
		return trip;
	}

}
