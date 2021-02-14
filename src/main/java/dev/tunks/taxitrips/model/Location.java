package dev.tunks.taxitrips.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Location {
	@Id
	private String id;
	private String borough;
	private String zone;

	public Location() {
	}

	public Location(String id) {
		this.id = id;
	}

	public Location(String id, String borough, String zone) {
		this.id = id;
		this.borough = borough;
		this.zone = zone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBorough() {
		return borough;
	}

	public void setBorough(String borough) {
		this.borough = borough;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	@Override
	public String toString() {
		return "Location [id=" + id + ", borough=" + borough + ", zone=" + zone + "]";
	}
   
}
