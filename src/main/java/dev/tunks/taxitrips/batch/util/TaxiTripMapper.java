package dev.tunks.taxitrips.batch.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import dev.tunks.taxitrips.model.TaxiTrip;
import dev.tunks.taxitrips.model.TaxiType;

public class TaxiTripMapper implements FieldSetMapper<TaxiTrip> {
    private TaxiType taxiType;
	
    public TaxiTripMapper(TaxiType taxiType) {
      this.taxiType = taxiType;
	}

	@Override
	public TaxiTrip mapFieldSet(FieldSet fieldSet) throws BindException {
		switch(taxiType) {
			case RHV:
			    return mapRfvVehicle(fieldSet);
			default:
				return mapGreenAndYellowVehicle(fieldSet);
		}
	}

	private TaxiTrip mapGreenAndYellowVehicle(FieldSet fieldSet) {
		TaxiTrip trip =  new TaxiTrip();
		String pickupLocationId = fieldSet.readString(DataUtil.PICKUP_LOCATION_ID_FIELD);
		String dropoffLocationId = fieldSet.readString(DataUtil.DROP_OFF_LOCATION_ID_FIELD);
        Date pickupDateTime = fieldSet.readDate(DataUtil.PICKUP_DATETIME_FIELD, DataUtil.DATE_FIELD_PATTERN);
        Date dropoffDateTime = fieldSet.readDate(DataUtil.DROPOFF_DATETIME_FIELD, DataUtil.DATE_FIELD_PATTERN);
        double distance = getValue(fieldSet,DataUtil.DISTANCE_FIELD);
        double totalAmount = getValue(fieldSet,DataUtil.TOTAL_AMOUNT_FIELD);
       
        trip.setPickupLocationId(pickupLocationId);
        trip.setDropoffLocationId(dropoffLocationId);
        trip.setPickupDateTime(pickupDateTime);
        trip.setDropoffDateTime(dropoffDateTime);
        trip.setDistance(distance);
        trip.setTotalAmount(totalAmount);
		return trip;
	}
	
	private TaxiTrip mapRfvVehicle(FieldSet fieldSet) {
		TaxiTrip trip =  new TaxiTrip();
		String pickupLocationId = fieldSet.readString(DataUtil.PICKUP_LOCATION_ID_FIELD);
		String dropoffLocationId = fieldSet.readString(DataUtil.DROP_OFF_LOCATION_ID_FIELD);
        Date pickupDateTime = fieldSet.readDate(DataUtil.RFV_PICKUP_DATETIME_FIELD, DataUtil.DATE_FIELD_PATTERN);
        Date dropoffDateTime = fieldSet.readDate(DataUtil.RFV_DROPOFF_DATETIME_FIELD, DataUtil.DATE_FIELD_PATTERN);
        trip.setPickupDateTime(pickupDateTime);
        trip.setDropoffDateTime(dropoffDateTime);
        trip.setPickupLocationId(pickupLocationId);
        trip.setDropoffLocationId(dropoffLocationId);
		return trip;
	}
	
	private double getValue(FieldSet fieldSet, String fieldName) {
		try {
		   return fieldSet.readDouble(fieldName);
		}
		catch(NumberFormatException ex) {
			return 0;
		}
	}

}
