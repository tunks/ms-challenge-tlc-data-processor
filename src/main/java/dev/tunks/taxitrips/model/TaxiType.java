package dev.tunks.taxitrips.model;

public enum TaxiType {
	 YELLOW(1),
	 GREEN(2),
	 RHV(3);
	 
	 private int type;
	 
	 private TaxiType(int type) {
		 this.type = type;
	 }
	 
	 public int getType() {
		return type;
	 }
}
