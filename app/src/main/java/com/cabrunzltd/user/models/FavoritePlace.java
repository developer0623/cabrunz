package com.cabrunzltd.user.models;

import com.google.android.gms.maps.model.LatLng;

public class FavoritePlace {
	
	private String id;
	
	private String name;
	private String address;
	
	private double latitude, longitude;
	
	public FavoritePlace(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setLatLng(double lat, double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public LatLng getLatLng() {
		LatLng position = new LatLng(latitude, longitude);
		return position;
	}
	
}
