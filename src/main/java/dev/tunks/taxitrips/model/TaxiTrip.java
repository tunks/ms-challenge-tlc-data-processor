package dev.tunks.taxitrips.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 >> FHV Trip Records
 Pickup_datetime | The date and time of the trip pick-up
 DropOff_datetime | The date and time of the trip dropoff
 PULocationID | TLC Taxi Zone in which the trip began
 DOLocationID | TLC Taxi Zone in which the trip ended
 SR_Flag | Indicates if the trip was a part of a shared ride chain offered by a High Volume FHV company 
           (e.g. Uber Pool, Lyft Line). For shared trips, the value is 1. 
           For non-shared rides, this field is null.
           
 *******************
 >> Yellow Taxi Trip Records
 tpep_pickup_datetime | The date and time when the meter was engaged.
 tpep_dropoff_datetime | The date and time when the meter was disengaged.
 Passenger_count | The number of passengers in the vehicle. This is a driver-entered value.
 Trip_distance | The elapsed trip distance in miles reported by the taximeter.
 PULocationID | TLC Taxi Zone in which the taximeter was engaged
 DOLocationID | TLC Taxi Zone in which the taximeter was disengaged
 RateCodeID | The final rate code in effect at the end of the trip.
 Fare_amount | The time-and-distance fare calculated by the meter.
 Extra | Miscellaneous extras and surcharges. Currently, this only includes the $0.50 and $1 rush hour and overnight charges.
 MTA_tax | $0.50 MTA tax that is automatically triggered based on the metered rate in use.
 Improvement_surcharge | $0.30 improvement surcharge assessed trips at the flag drop. The improvement surcharge began being levied in 2015.
 Tolls_amount | Total amount of all tolls paid in trip.
 Total_amount | The total amount charged to passengers. Does not include cash tips
 
 ********************
 >>> Green Taxi 
 tpep_pickup_datetime | The date and time when the meter was engaged.
 tpep_dropoff_datetime | The date and time when the meter was disengaged.
 Trip_distance | The elapsed trip distance in miles reported by the taximeter.
 PULocationID | TLC Taxi Zone in which the taximeter was engaged
 DOLocationID | TLC Taxi Zone in which the taximeter was disengaged
 RateCodeID | The final rate code in effect at the end of the trip.
 Fare_amount | The time-and-distance fare calculated by the meter.
 Extra | Miscellaneous extras and surcharges. Currently, this only includes the $0.50 and $1 rush hour and overnight charges.
 MTA_tax | $0.50 MTA tax that is automatically triggered based on the metered rate in use.
 Improvement_surcharge| $0.30 improvement surcharge assessed trips at the flag drop. The improvement surcharge began being levied in 2015.
 Tolls_amount| Total amount of all tolls paid in trip.
 Total_amount| The total amount charged to passengers. Does not include cash tips.
*/

@Document
public class TaxiTrip {
	@Id
	private String id;
	@Indexed
	private Date pickupDateTime;
	@Indexed
	private Date dropoffDateTime;
	@Indexed
	private TaxiType taxiType;
	private double distance;
	@Indexed
	private double totalAmount;
	@Indexed
	private String pickupLocationId;
	@Indexed
	private String dropoffLocationId;
	//duration in minutes
	private double duration; 

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getPickupDateTime() {
		return pickupDateTime;
	}
	
	public void setPickupDateTime(Date pickupDateTime) {
		this.pickupDateTime = pickupDateTime;
	}
	
	public Date getDropoffDateTime() {
		return dropoffDateTime;
	}
	
	public void setDropoffDateTime(Date dropoffDateTime) {
		this.dropoffDateTime = dropoffDateTime;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	public String getPickupLocationId() {
		return pickupLocationId;
	}

	public void setPickupLocationId(String pickupLocationId) {
		this.pickupLocationId = pickupLocationId;
	}

	public String getDropoffLocationId() {
		return dropoffLocationId;
	}

	public void setDropoffLocationId(String dropoffLocationId) {
		this.dropoffLocationId = dropoffLocationId;
	}

	public TaxiType getTaxiType() {
		return taxiType;
	}

	public void setTaxiType(TaxiType taxiType) {
		this.taxiType = taxiType;
	}
	
	public double getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "VehicleTrip [id=" + id + ", pickupDateTime=" + pickupDateTime + ", dropoffDateTime=" + dropoffDateTime
				+ ", distance=" + distance + ", totalAmount=" + totalAmount + ", pickupLocationId=" + pickupLocationId 
				+ ", dropoffLocationId=" + dropoffLocationId +"]";
	}
}
