package dev.tunks.taxitrips.batch.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.tunks.taxitrips.model.TaxiTrip;
import dev.tunks.taxitrips.model.TaxiType;

public class TaxiTripItemProcessorTest {
    private TaxiTrip trip;
    
    @BeforeEach
    public void setup() {
    	trip = new TaxiTrip();
    }
    
	@Test
	public void processGreenTaxiTripItem() throws Exception {
		TaxiTripItemProcessor processor = new TaxiTripItemProcessor(TaxiType.GREEN);
		processor.process(trip);
		assertEquals(TaxiType.GREEN, trip.getTaxiType());
	}

	@Test
	public void processYellowTaxiTripItem() throws Exception {
		TaxiTripItemProcessor processor = new TaxiTripItemProcessor(TaxiType.YELLOW);
		processor.process(trip);
		assertEquals(TaxiType.YELLOW, trip.getTaxiType());
	}

	@Test
	public void processFHITaxiTripItem() throws Exception {
		TaxiTripItemProcessor processor = new TaxiTripItemProcessor(TaxiType.RHV);
		processor.process(trip);
		assertEquals(TaxiType.RHV, trip.getTaxiType());
	}
}
