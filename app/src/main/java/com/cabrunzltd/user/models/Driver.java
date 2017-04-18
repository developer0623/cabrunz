/**
 * 
 */
package com.cabrunzltd.user.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hardik A Bhalodi
 * 
 */
@SuppressWarnings("serial")
public class Driver implements Parcelable {

	/**
	 * 
	 */

	private String firstName;
	private String lastName;

	private String phone;
	private String bio;
	private String picture;
	private double latitude;
	private double longitude;
	private double d_latitude;
	private double d_longitude;
	private double rating;
	private String lastTime;
	private String lastDistance;
	private Bill bill;

	private String Car_NO;
	private String Car_Model;





	public String getCar_NO() {
		return Car_NO;
	}

	public void setCar_NO(String car_NO) {
		Car_NO = car_NO;
	}

	public String getCar_Model() {
		return Car_Model;
	}

	public void setCar_Model(String car_Model) {
		Car_Model = car_Model;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastDistance() {
		return lastDistance;
	}

	public void setLastDistance(String lastDistance) {
		this.lastDistance = lastDistance;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */

	public Driver() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double getD_latitude() {
		return d_latitude;
	}

	public void setD_latitude(double d_latitude) {
		this.d_latitude = d_latitude;
	}

	public double getD_longitude() {
		return d_longitude;
	}

	public void setD_longitude(double d_longitude) {
		this.d_longitude = d_longitude;
	}


    protected Driver(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        bio = in.readString();
        picture = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        d_latitude = in.readDouble();
        d_longitude = in.readDouble();
        rating = in.readDouble();
        lastTime = in.readString();
        lastDistance = in.readString();
        bill = (Bill) in.readValue(Bill.class.getClassLoader());
        Car_NO = in.readString();
        Car_Model = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(bio);
        dest.writeString(picture);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(d_latitude);
        dest.writeDouble(d_longitude);
        dest.writeDouble(rating);
        dest.writeString(lastTime);
        dest.writeString(lastDistance);
        dest.writeValue(bill);
        dest.writeString(Car_NO);
        dest.writeString(Car_Model);
    }

    @SuppressWarnings("unused")
    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}