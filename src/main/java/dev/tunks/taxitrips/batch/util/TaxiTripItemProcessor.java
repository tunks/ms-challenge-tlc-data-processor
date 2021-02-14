package dev.tunks.taxitrips.batch.util;

import java.time.Duration;
import java.time.Instant;
import org.springframework.batch.item.ItemProcessor;
import dev.tunks.taxitrips.model.TaxiTrip;
import dev.tunks.taxitrips.model.TaxiType;

public class TaxiTripItemProcessor implements ItemProcessor<TaxiTrip, TaxiTrip> {
	private TaxiType taxiType;
	
	public TaxiTripItemProcessor(TaxiType taxiType) {
		this.taxiType = taxiType;
	}
	
	@Override
	public TaxiTrip process(TaxiTrip item) throws Exception {
		if (item.getDropoffDateTime() != null && item.getDropoffDateTime() != null) {
			Instant pickupInstant = item.getPickupDateTime().toInstant();
			Instant dropoffInstant = item.getDropoffDateTime().toInstant();
			Duration duration = Duration.between(pickupInstant, dropoffInstant);
			item.setDuration(duration.toMinutes());
		}
		
		if(taxiType != null) {
		   item.setTaxiType(taxiType);
		}
		return item;
	}
}
