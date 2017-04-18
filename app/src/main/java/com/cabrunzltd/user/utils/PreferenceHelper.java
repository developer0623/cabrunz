package com.cabrunzltd.user.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

public class PreferenceHelper {

	private static final String PAYMENT_REQUEST = "payment_request";
	private static final String PAYMENT_REQUEST_DATA = "payment_request_data";
	private SharedPreferences app_prefs;
	private final String USER_ID = "user_id";
	private final String PAY_ID = "pay_id";
	private final String EMAIL = "email";
	private final String PASSWORD = "password";
	private final String DEVICE_TOKEN = "device_token";
	private final String SESSION_TOKEN = "session_token";
	private final String REQUEST_ID = "request_id";
	private final String REQUEST_TIME = "request_time";
	private final String REQUEST_LATITUDE = "request_latitude";
	private final String REQUEST_LONGITUDE = "request_longitude";
	private final String LOGIN_BY = "login_by";
	private final String DISTANCE = "distance";
	private final String SOCIAL_ID = "social_id";
	private final String IS_VERIFIED = "is_verified";
	private final String CURRENT_FRAGMENT = "currentFragment";
	private Context context;

	public PreferenceHelper(Context context) {
		app_prefs = context.getSharedPreferences(Const.PREF_NAME,
				Context.MODE_PRIVATE);
		this.context = context;
	}

	public void setPaymentRequest(boolean isPay) {
		Editor edit = app_prefs.edit();
		edit.putBoolean(PAYMENT_REQUEST, isPay);
		edit.commit();
	}

	public boolean isPamentRequest() {
		return app_prefs.getBoolean(PAYMENT_REQUEST, false);
	}

	public void putPaymentRequestData(String userId) {
		Editor edit = app_prefs.edit();
		edit.putString(PAYMENT_REQUEST_DATA, userId);
		edit.commit();
	}

	public String getPaymentRequestData() {
		return app_prefs.getString(PAYMENT_REQUEST_DATA, null);
	}

	public void putUserId(String userId) {
		Editor edit = app_prefs.edit();
		edit.putString(USER_ID, userId);
		edit.commit();
	}

	public void putPay(int userId) {
		Editor edit = app_prefs.edit();
		edit.putInt(PAY_ID, userId);
		edit.commit();
	}

	public void putEmail(String email) {
		Editor edit = app_prefs.edit();
		edit.putString(EMAIL, email);
		edit.commit();
	}

	public String getEmail() {
		return app_prefs.getString(EMAIL, null);
	}
	
	public void putIs_Verified(String is_verified) {
		Editor edit = app_prefs.edit();
		edit.putString(IS_VERIFIED, is_verified);
		edit.commit();
	}

	public String getIs_Verified() {
		return app_prefs.getString(IS_VERIFIED, "");
	}

	public void putCurrentFragment(String currentFragment) {
		Editor edit = app_prefs.edit();
		edit.putString(CURRENT_FRAGMENT, currentFragment);
		edit.commit();
	}

	public String getCurrentFragment() {
		return app_prefs.getString(CURRENT_FRAGMENT, "");
	}

	public void putPassword(String password) {
		Editor edit = app_prefs.edit();
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	public String getPassword() {
		return app_prefs.getString(PASSWORD, null);
	}

	public void putDistance(Float distance) {
		Editor edit = app_prefs.edit();
		edit.putFloat(DISTANCE, distance);
		edit.commit();
	}

	public float getDistance() {
		return app_prefs.getFloat(DISTANCE, 0.0f);
	}

	public void putSocialId(String id) {
		Editor edit = app_prefs.edit();
		edit.putString(SOCIAL_ID, id);
		edit.commit();
	}

	public String getSocialId() {
		return app_prefs.getString(SOCIAL_ID, null);
	}

	public String getUserId() {
		return app_prefs.getString(USER_ID, null);

	}

	public int getPayId() {
		return app_prefs.getInt(PAY_ID, 0);

	}

	public void putDeviceToken(String deviceToken) {
		Editor edit = app_prefs.edit();
		edit.putString(DEVICE_TOKEN, deviceToken);
		edit.commit();
	}

	public String getDeviceToken() {
		return app_prefs.getString(DEVICE_TOKEN, null);

	}

	public void putSessionToken(String sessionToken) {
		Editor edit = app_prefs.edit();
		edit.putString(SESSION_TOKEN, sessionToken);
		edit.commit();
	}

	public String getSessionToken() {
		return app_prefs.getString(SESSION_TOKEN, null);

	}

	public void putRequestId(int requestId) {
		Editor edit = app_prefs.edit();
		edit.putInt(REQUEST_ID, requestId);
		edit.commit();
	}

	public int getRequestId() {
		return app_prefs.getInt(REQUEST_ID, Const.NO_REQUEST);

	}

	public void putLoginBy(String loginBy) {
		Editor edit = app_prefs.edit();
		edit.putString(LOGIN_BY, loginBy);
		edit.commit();
	}

	public String getLoginBy() {
		return app_prefs.getString(LOGIN_BY, Const.MANUAL);
	}

	public void putRequestTime(long time) {
		Editor edit = app_prefs.edit();
		edit.putLong(REQUEST_TIME, time);
		edit.commit();
	}

	public long getRequestTime() {
		return app_prefs.getLong(REQUEST_TIME, Const.NO_TIME);
	}

	private void putRequestLocation(LatLng latLang) {
		Editor edit = app_prefs.edit();
		edit.putString(REQUEST_LATITUDE, String.valueOf(latLang.latitude));
		edit.putString(REQUEST_LONGITUDE, String.valueOf(latLang.longitude));
		edit.commit();
	}

	public LatLng getRequestLocation() {
		LatLng latLng = null;
		try {
			latLng = new LatLng(Double.parseDouble(app_prefs.getString(
					REQUEST_LATITUDE, "0.0")), Double.parseDouble(app_prefs
					.getString(REQUEST_LONGITUDE, "0.0")));
		} catch (NumberFormatException nfe) {
			latLng = new LatLng(0.0, 0.0);
		}
		return latLng;

	}

	public void clearRequestData() {
		putRequestId(Const.NO_REQUEST);
		putRequestTime(Const.NO_TIME);
		putDistance(Const.NO_DISTANCE);
		putRequestLocation(new LatLng(0.0, 0.0));
		//putCurrentFragment("");
		// new DBHelper(context).deleteAllLocations();

	}

	public void Logout() {
		clearRequestData();
		// new DBHelper(context).deleteUser();
		putUserId(null);
		putSessionToken(null);
		putSocialId(null);
		putLoginBy(Const.MANUAL);

	}

}
