package com.cabrunzltd.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.models.Cards;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendPayActivity extends ActionBarBaseActivitiy implements
		AsyncTaskCompleteListener {

	private Activity activity;
	private PreferenceHelper pHelper;
	private String payReqId;
	private TextView tvUserRequest;
	private TextView tvAmount;
	private TextView tvCard;
	private GoogleMap map;
	private String lat, lang, name, currency, amount;
	private MyFontButton btnPay, btnDecline;
	private ArrayList<Cards> listCards;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		pHelper = new PreferenceHelper(this);
		setContentView(R.layout.activity_friendpay);
		setTitle("Payment Request");
		tvUserRequest = (TextView) findViewById(R.id.tvUserRequest);
		tvAmount = (TextView) findViewById(R.id.tvTotal);
		tvCard = (TextView) findViewById(R.id.tvCard);

		btnPay = (MyFontButton) findViewById(R.id.btnPay);
		btnDecline = (MyFontButton) findViewById(R.id.btnReject);
		listCards = new ArrayList<Cards>();
		// Bundle extras = getIntent().getExtras();
		String message = new PreferenceHelper(this).getPaymentRequestData();
		Log.d("NIKHIL", "RecvdMssg: " + message);
		// showPayAlert(message);
		getCards();

		try {
			JSONObject object = new JSONObject(message);
			currency = object.getString("currency_selected");
			payReqId = object.getString("pay_request_id");
			currency = object.getString("currency_selected");
			amount = object.getString("total");
			JSONObject jsonObject = object.getJSONObject("owner_data");
			JSONObject user = jsonObject.getJSONObject("owner");
			lat = user.getString("latitude");
			lang = user.getString("longitude");
			name = user.getString("name");

		} catch (JSONException e) {
			e.printStackTrace();
		}

//		map = ((SupportMapFragment) getSupportFragmentManager()
//				.findFragmentById(R.id.map)).getMap();
//		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.getUiSettings().setZoomControlsEnabled(false);

		map.addMarker(new MarkerOptions().position(
				new LatLng(Double.parseDouble(lat), Double.parseDouble(lang)))
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.img_usermappin)));

		CameraUpdate cameraUpdate = null;

		cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				new LatLng(Double.parseDouble(lat), Double.parseDouble(lang)),
				16);
		map.animateCamera(cameraUpdate);
		tvAmount.setText(currency + amount);
		tvUserRequest.setText(getResources().getString(
				R.string.text_user_requestl)
				+ " " + name);

		btnPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callAcceptPayApi();
			}
		});
		btnDecline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callRejectPayApi();
			}
		});
	}

	private void showPayAlert(final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Pay Request");
		builder.setMessage(getAlertMessage(message));
		builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callAcceptPayApi();
			}
		});
		builder.setNegativeButton("Reject",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						callRejectPayApi();
					}
				});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	protected boolean isValidate() {
		return false;

	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {

		case Const.ServiceCode.REQUEST_SEND_FRIEND_ACCEPTED:
		case Const.ServiceCode.REQUEST_SEND_FRIEND_REJECTED:
			Log.d("NIKHIL", "AcceptedResponse: " + response);
			if (new ParseContent(this).isSuccess(response)) {
				new PreferenceHelper(this).setPaymentRequest(false);
				startActivity(new Intent(this, MainDrawerActivity.class));
				finish();
			}
			break;

		case Const.ServiceCode.GET_CARDS:
			Log.d("NIKHIL", "get cards" + response);
			AndyUtils.removeCustomProgressDialog();
			if (new ParseContent(this).isSuccess(response)) {
				listCards.clear();
				new ParseContent(this).parseCards(response, listCards);
				if (listCards.size() > 0) {
					tvCard.setText("Personal Debit **** "
							+ listCards.get(0).getLastFour());

				}
			} else {
				// btnAddNewPayment.setImageResource(R.drawable.add_credit);
			}
			break;

		}
	}

	private String getAlertMessage(String message) {
		String alertMessage = "";
		try {
			JSONObject jsonObject = new JSONObject(message);
			payReqId = jsonObject.getString("pay_request_id");
			String amount = jsonObject.getString("total");
			String currency = "$"; // CHANGE THIS
			String owner = jsonObject.getJSONObject("owner_data")
					.getJSONObject("owner").getString("name");
			alertMessage = owner + " wants you to pay \n" + currency + " "
					+ amount;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return alertMessage;
	}

	private void getCards() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.progress_loading), false, null);
		Log.d("user details", "ID=  " + new PreferenceHelper(this).getUserId()
				+ "TOKEN=  " + new PreferenceHelper(this).getSessionToken());

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_CARDS + Const.Params.ID + "="
						+ new PreferenceHelper(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.GET_CARDS, true, this);
	}

	private void callAcceptPayApi() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REQUEST_SEND_FRIEND_ACCEPTED);
		map.put(Const.Params.ID, pHelper.getUserId());
		map.put(Const.Params.TOKEN, pHelper.getSessionToken());
		map.put("pay_request_id", payReqId);
		Log.d("pavan", "Request  " + map);

		new HttpRequester(this, map,
				Const.ServiceCode.REQUEST_SEND_FRIEND_ACCEPTED, false, this);
	}

	private void callRejectPayApi() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REQUEST_SEND_FRIEND_REJECTED);
		map.put(Const.Params.ID, pHelper.getUserId());
		map.put(Const.Params.TOKEN, pHelper.getSessionToken());
		map.put("pay_request_id", payReqId);
		Log.d("pavan", "Request  " + map);
		new HttpRequester(this, map,
				Const.ServiceCode.REQUEST_SEND_FRIEND_REJECTED, false, this);
	}

}
