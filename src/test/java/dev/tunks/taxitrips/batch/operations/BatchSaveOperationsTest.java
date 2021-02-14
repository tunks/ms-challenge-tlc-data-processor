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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import dev.tunks.taxitrips.batch.util.DataUtil;
import dev.tunks.taxitrips.model.TaxiTrip;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
public class BatchSaveOperationsTest {
	@Autowired
	private MongoTemplate mongoTemplate;
	
    private BatchSaveOperations<TaxiTrip> saveOperations;
	private List<TaxiTrip> trips;
	private Query query; 
    
	@BeforeEach
	public void setUp() throws Exception {
        String pickupLocationId = "1";
    	String dropoffLocationId = "2";
		trips = new ArrayList<TaxiTrip>();
		for(int i = 1; i<=10; i++) {
			trips.add(createTaxiTrip(pickupLocationId,dropoffLocationId));
		}
		query  = new Query(Criteria.where("pickupLocationId").is(pickupLocationId)
				           .and("dropoffLocationId").is(dropoffLocationId));
    	saveOperations = new BatchSaveOperations<TaxiTrip>(mongoTemplate,DataUtil.TRIPS_COLLECTION);
	}

	@AfterEach
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testSaveAll() {
		saveOperations.saveAll(trips);
		long count = mongoTemplate.count(query, TaxiTrip.class);
		assertEquals(trips.size(),count);
	}
	
	private TaxiTrip createTaxiTrip(String pickupLocationId, String dropoffLocationId) {
		TaxiTrip trip = new TaxiTrip();
		trip.setPickupLocationId(pickupLocationId);
		trip.setDropoffLocationId(dropoffLocationId);
		return trip;
	}

}
