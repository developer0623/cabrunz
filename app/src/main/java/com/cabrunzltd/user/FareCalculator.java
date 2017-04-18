package com.cabrunzltd.user;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.cabrunzltd.user.adapter.PlacesAutoCompleteAdapter;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//import com.google.android.gms.internal.br;
//import com.google.android.gms.internal.fn;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;

public class FareCalculator extends Activity implements
		AsyncTaskCompleteListener {

	private PlacesAutoCompleteAdapter adapter;

	AutoCompleteTextView to_address;
	AutoCompleteTextView from_address;
	Button button_calculate;
	TextView estimated_fare, tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fare_calculator);

		adapter = new PlacesAutoCompleteAdapter(getApplicationContext(),
				R.layout.autocomplete_list_text);

		to_address = (AutoCompleteTextView) findViewById(R.id.to_address);
		to_address.setAdapter(adapter);

		from_address = (AutoCompleteTextView) findViewById(R.id.from_address);
		from_address.setAdapter(adapter);

		estimated_fare = (TextView) findViewById(R.id.estimated_fare);
		tvTitle = (TextView) findViewById(R.id.tvTitle);

		button_calculate = (Button) findViewById(R.id.button_calculate);
		button_calculate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getDistance();
			}
		});

	}

	private void getDistance() {

		AndyUtils.showCustomProgressDialog(FareCalculator.this,
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_MAP_API
				+ Const.Params.MAP_ORIGINS + "="
				+ to_address.getText().toString() + "&"
				+ Const.Params.MAP_DESTINATIONS + "="
				+ from_address.getText().toString());
		new HttpRequester(FareCalculator.this, map,
				Const.ServiceCode.GET_MAP_DETAILS, true, this);

		Log.d("pavan", "request " + map);

	}

	private void getEstimation(String distance, String duration) {

		AndyUtils.showCustomProgressDialog(FareCalculator.this,
				getString(R.string.progress_loading), false, null);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.FARE_CALCULATOR);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(FareCalculator.this).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(FareCalculator.this).getUserId());
		map.put(Const.Params.TIME, String.valueOf(duration));
		map.put(Const.Params.DISTANCE, String.valueOf(distance));
		// map.put(Const.Params.TYPE,
		// String.valueOf(listType.get(selectedPostion).getId()));
		//
		// map.put(Const.Params.DISTANCE, "1");

		new HttpRequester(FareCalculator.this, map,
				Const.ServiceCode.FARE_CALCULATOR, this);

		Log.d("pavan", "request " + map);

	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub

		String distance = "";
		String duration = "";

		Log.d("pavan", "response  " + response);
		AndyUtils.removeCustomProgressDialog();

		switch (serviceCode) {
		case Const.ServiceCode.GET_MAP_DETAILS:

			if (response != null) {

				try {
					JSONObject jObject = new JSONObject(response);

					if (jObject.getString("status").equals("OK")) {

						JSONArray jaArray = jObject.getJSONArray("rows");

						for (int i = 0; i < jaArray.length(); i++) {

							JSONObject jobj = jaArray.getJSONObject(i);

							JSONArray jaArray2 = jobj.getJSONArray("elements");

							for (int j = 0; i < jaArray2.length(); j++) {

								JSONObject jobj1 = jaArray2.getJSONObject(j);

								JSONObject jobj_distance = jobj1
										.getJSONObject("distance");
								Log.d("pavan",
										"distance "
												+ jobj_distance
														.getString("text")
												+ " , "
												+ jobj_distance
														.getString("value"));

								JSONObject jobj_duration = jobj1
										.getJSONObject("duration");
								Log.d("pavan",
										"distance "
												+ jobj_duration
														.getString("text")
												+ " , "
												+ jobj_duration
														.getString("value"));

								distance = jobj_duration.getString("value");
								duration = jobj_distance.getString("value");
								getEstimation(distance, duration);

							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;

		case Const.ServiceCode.FARE_CALCULATOR:
			if (response != null) {
				try {
					JSONObject jObject = new JSONObject(response);
					if (jObject.getString("success").equals("true")) {

						ShowFare(jObject.getString("estimated_fare"));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;

		default:
			break;
		}
	}

	private void ShowFare(String fare) {

		tvTitle.setVisibility(View.VISIBLE);
		estimated_fare.setVisibility(View.VISIBLE);
		estimated_fare.setText(fare + "$");

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.fare_calculator, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
