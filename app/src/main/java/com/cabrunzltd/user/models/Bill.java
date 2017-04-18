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
public class Bill implements Parcelable {
	private String distance;
	private String time;
	private String basePrice;
	private String distanceCost;
	private String timeCost;
	private String total;
	private String isPaid;
	private String unit;
	private String currency;
	private String promo;
	private String promo_discount;
	private String actual_price;

	private String primary_id;
	private String primary_amount;
	private String secoundry_id;
	private String secoundry_amount;
	private String payment_mode;
	private double discounted_amount;
	private double actual_total;
	private String perdistancecost;
	private String pertimecost;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
		
	}

	public String getDistanceCost() {
		return distanceCost;
	}

	public void setDistanceCost(String distanceCost) {
		this.distanceCost = distanceCost;
	}

	public String getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(String timeCost) {
		this.timeCost = timeCost;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(String isPaid) {
		this.isPaid = isPaid;
	}

	public String getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(String primary_id) {
		this.primary_id = primary_id;
	}

	public String getPrimary_amount() {
		return primary_amount;
	}

	public void setPrimary_amount(String primary_amount) {
		this.primary_amount = primary_amount;
	}

	public String getSecoundry_id() {
		return secoundry_id;
	}

	public void setSecoundry_id(String secoundry_id) {
		this.secoundry_id = secoundry_id;
	}

	public String getSecoundry_amount() {
		return secoundry_amount;
	}

	public void setSecoundry_amount(String secoundry_amount) {
		this.secoundry_amount = secoundry_amount;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getDiscounted_amount() {
		return discounted_amount;
	}

	public void setDiscounted_amount(double discounted_amount) {
		this.discounted_amount = discounted_amount;
	}

	public double getActual_total() {
		return actual_total;
	}

	public void setActual_total(double actual_total) {
		this.actual_total = actual_total;
	}



	public String getPertimecost() {
		return pertimecost;
	}

	public void setPertimecost(String pertimecost) {
		this.pertimecost = pertimecost;
	}

	public String getPerdistancecost() {
		return perdistancecost;
	}

	public void setPerdistancecost(String perdistancecost) {
		this.perdistancecost = perdistancecost;
	}

	public String getPromo() {
		return promo;
	}

	public void setPromo(String promo) {
		this.promo = promo;
	}

	public String getPromo_discount() {
		return promo_discount;
	}

	public void setPromo_discount(String promo_discount) {
		this.promo_discount = promo_discount;
	}

	public String getActual_price() {
		return actual_price;
	}

	public void setActual_price(String actual_price) {
		this.actual_price = actual_price;
	}
	
	
	


    public Bill() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected Bill(Parcel in) {
        distance = in.readString();
        time = in.readString();
        basePrice = in.readString();
        distanceCost = in.readString();
        timeCost = in.readString();
        total = in.readString();
        isPaid = in.readString();
        unit = in.readString();
        currency = in.readString();
        promo = in.readString();
        promo_discount = in.readString();
        actual_price = in.readString();
        primary_id = in.readString();
        primary_amount = in.readString();
        secoundry_id = in.readString();
        secoundry_amount = in.readString();
        payment_mode = in.readString();
        discounted_amount = in.readDouble();
        actual_total = in.readDouble();
        perdistancecost = in.readString();
        pertimecost = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeString(basePrice);
        dest.writeString(distanceCost);
        dest.writeString(timeCost);
        dest.writeString(total);
        dest.writeString(isPaid);
        dest.writeString(unit);
        dest.writeString(currency);
        dest.writeString(promo);
        dest.writeString(promo_discount);
        dest.writeString(actual_price);
        dest.writeString(primary_id);
        dest.writeString(primary_amount);
        dest.writeString(secoundry_id);
        dest.writeString(secoundry_amount);
        dest.writeString(payment_mode);
        dest.writeDouble(discounted_amount);
        dest.writeDouble(actual_total);
        dest.writeString(perdistancecost);
        dest.writeString(pertimecost);
    }

    @SuppressWarnings("unused")
    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };
}