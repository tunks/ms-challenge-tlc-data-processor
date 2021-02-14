package dev.tunks.taxitrips.batch.util;

public class DataUtil {
	public static final String PICKUP_DATETIME_FIELD = "pep_pickup_datetime";
	public static final String DROPOFF_DATETIME_FIELD = "lpep_dropoff_datetime";
	public static final String PICKUP_LOCATION_ID_FIELD = "PULocationID";
	public static final String DROP_OFF_LOCATION_ID_FIELD = "DOLocationID";
	public static final String RFV_PICKUP_DATETIME_FIELD = "pickup_datetime";
	public static final String RFV_DROPOFF_DATETIME_FIELD = "dropoff_datetime";
	public static final String DISTANCE_FIELD = "trip_distance";
	public static final String TOTAL_AMOUNT_FIELD = "total_amount";
	//Taxi location zone fields
	public static final String LOCATION_ID_FIELD = "LocationID";
	public static final String BOROUGH_FIELD = "Borough";
	public static final String ZONE_FIELD = "Zone";

	public static final String DATE_FIELD_PATTERN = "yyyy-mm-dd HH:mm:ss";//2020-01-01 00:33:0
	
	public static final String TRIPS_COLLECTION = "taxiTrip";
	public static final String LOCATION_COLLECTION = "location";


	public static String[] getZoneDataFields() {
		return new String[] { LOCATION_ID_FIELD,BOROUGH_FIELD,ZONE_FIELD };
	}
	
	public static int[] getZoneDataIndexes() {
		return new int[] { 0, 1, 2, 3};
	}
}
