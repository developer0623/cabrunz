package com.cabrunzltd.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cabrunzltd.user.adapter.PlacesAutoCompleteAdapter;
import com.cabrunzltd.user.adapter.VehicalTypeListAdapter;
import com.cabrunzltd.user.models.VehicalType;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//import com.android.datetimepicker.date.DatePickerDialog;
//import com.android.datetimepicker.time.RadialPickerLayout;
//import com.android.datetimepicker.time.TimePickerDialog;
///import com.sromku.simple.fb.listeners.OnCheckinsListener;///

public class ScheduleActivity extends ActionBarBaseActivitiy {
	private static TextView tvdate, tvtime;
	private int selectedPostion = -1;
	private AutoCompleteTextView etEnterSouceltr, etEnterdestltr;
	private PlacesAutoCompleteAdapter adapter;
	public ParseContent pContent;
	public PreferenceHelper pHelper;
	private TextView[] tvServiceTypes;
	private LinearLayout linearLayout;
	private TextView rowTextView;
	private LinearLayout[] layouts;
	private ArrayList<VehicalType> listType;
	private VehicalTypeListAdapter typeAdapter;
	private View[] views;
	private LinearLayout lLayout;
	private View v;
	Button btnnextltr, btnrecurr;
	Context context = this;
	private LatLng latlng_places;
	private RadioGroup rgpayment;

	private static final String TIME_PATTERN = "HH:mm";

	private TextView lblDate;
	private TextView lblTime;
	private Calendar calendar;
	private DateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private int payment_type = -1;
	private String date, time = "";
	private String weekdays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_later);
		setIconMenu(R.drawable.ub__nav_history);
		setTitle("Schedule");
		setIcon(R.drawable.icon_back);
		adapter = new PlacesAutoCompleteAdapter(this,
				R.layout.autocomplete_list_text);
		getVehicalTypes();
		selectedPostion = 0;
		listType = new ArrayList<VehicalType>();
		typeAdapter = new VehicalTypeListAdapter(context, listType);
		btnnextltr = (Button) findViewById(R.id.btnsubmit);
		btnnextltr.setOnClickListener(this);
		btnrecurr = (Button) findViewById(R.id.btnrecurr);
		btnrecurr.setOnClickListener(this);
		linearLayout = (LinearLayout) findViewById(R.id.tvServiceType);
		etEnterdestltr = (AutoCompleteTextView) findViewById(R.id.etEnterDestltr);
		etEnterSouceltr = (AutoCompleteTextView) findViewById(R.id.etEnterSouceltr);
		rgpayment = (RadioGroup) findViewById(R.id.rgpayment);
		rgpayment
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdcard:

							payment_type = 1;

							break;

						case R.id.rdcash:
							payment_type = 0;
							break;

						}

					}
				});

		etEnterSouceltr.setAdapter(adapter);
		etEnterdestltr.setAdapter(adapter);
		pHelper = new PreferenceHelper(this);
		pContent = new ParseContent(this);
		etEnterSouceltr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
				adapter.notifyDataSetChanged();
			}
		});
		etEnterdestltr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
				adapter.notifyDataSetChanged();
			}
		});

		calendar = Calendar.getInstance();
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
				Locale.getDefault());
		timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

		lblDate = (TextView) findViewById(R.id.tvdate);
		lblTime = (TextView) findViewById(R.id.tvtime);
		lblDate.setOnClickListener(this);
		lblTime.setOnClickListener(this);

		// update();

	}

	private void update() {
		lblDate.setText(dateFormat.format(calendar.getTime()));
		lblTime.setText(timeFormat.format(calendar.getTime()));
	}

	private void getVehicalTypes() {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_VEHICAL_TYPES);

		new HttpRequester(this, map, Const.ServiceCode.GET_VEHICAL_TYPES, true,
				this);

	}

	private void setTabServiceType(ArrayList<VehicalType> listType2) {

		LayoutParams vParams = new LayoutParams(LayoutParams.MATCH_PARENT, 7);
		LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < listType.size(); i++) {
			rowTextView = new TextView(this);
			lLayout = new LinearLayout(this);
			v = new View(this);

			v.setLayoutParams(vParams);
			lLayout.setLayoutParams(lParams);
			lLayout.setOrientation(LinearLayout.VERTICAL);
			// set some properties of rowTextView or something
			rowTextView.setText(listType2.get(i).getName().toUpperCase());

			rowTextView.setPadding(40, 20, 40, 15);
			rowTextView.setId(i);
			rowTextView.setTextSize(15);
			// rowTextView.sets
			rowTextView.setTypeface(null, Typeface.BOLD);
			v.setBackgroundColor(Color.WHITE);
			rowTextView
					.setTextColor(getResources().getColor(R.color.gray_lite));

			if (listType.get(i).isSelected) {
				rowTextView.setTextColor(getResources().getColor(
						R.color.app_color_blue));
				v.setBackgroundColor(getResources().getColor(
						R.color.app_color_blue));
			}
			// add the textview to the linearlayout

			lLayout.addView(rowTextView);
			lLayout.addView(v);
			linearLayout.addView(lLayout);

			if (listType.size() > 4) {
				linearLayout.setGravity(Gravity.NO_GRAVITY);
			}
			layouts[i] = lLayout;
			views[i] = v;

			// save a reference to the textview for later
			tvServiceTypes[i] = rowTextView;
			tvServiceTypes[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					for (int j = 0; j < listType.size(); j++) {
						if (j == v.getId()) {
							tvServiceTypes[j].setTextColor(getResources()
									.getColor(R.color.app_color_blue));
							views[j].setBackgroundColor(getResources()
									.getColor(R.color.app_color_blue));

						} else {
							tvServiceTypes[j].setTextColor(getResources()
									.getColor(R.color.gray_lite));
							views[j].setBackgroundColor(getResources()
									.getColor(R.color.white));
						}

					}

					selectedPostion = v.getId();

				}
			});

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.tvdate:
//			DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
//					calendar.get(Calendar.MONTH),
//					calendar.get(Calendar.DAY_OF_MONTH)).show(
//					getFragmentManager(), "datePicker");
//
//			break;
//		case R.id.tvtime:
//			TimePickerDialog.newInstance(this,
//					calendar.get(Calendar.HOUR_OF_DAY),
//					calendar.get(Calendar.MINUTE), true).show(
//					getFragmentManager(), "timePicker");
//			break;

		case R.id.btnActionNotification:
			onBackPressed();
			break;

		case R.id.btnrecurr:
			if (etEnterdestltr.getText().length() == 0
					|| etEnterSouceltr.getText().length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter proper data to continue",
						Toast.LENGTH_LONG).show();
			} else {
				showrecurrdailog();
			}

			break;

		case R.id.btnsubmit:
			if (etEnterSouceltr.getText().length() == 0
					|| etEnterdestltr.getText().length() == 0) {
				Toast.makeText(context,
						"Please fill out Pick Up and Drop Address",
						Toast.LENGTH_LONG).show();
			} else if (payment_type == -1) {
				Toast.makeText(context, "Please select payment type !",
						Toast.LENGTH_LONG).show();
			} else {
				pickuplater();
			}
			break;
		}

	}

	private void showrecurrdailog() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.week_days);
		final ArrayList<Integer> checkedweeks = new ArrayList<Integer>();
		Button btnsubmit = (Button) dialog.findViewById(R.id.btnsubmit);
		CheckBox rd1 = (CheckBox) dialog.findViewById(R.id.rd1);
		CheckBox rd2 = (CheckBox) dialog.findViewById(R.id.rd2);
		CheckBox rd3 = (CheckBox) dialog.findViewById(R.id.rd3);
		CheckBox rd4 = (CheckBox) dialog.findViewById(R.id.rd4);
		CheckBox rd5 = (CheckBox) dialog.findViewById(R.id.rd5);
		CheckBox rd6 = (CheckBox) dialog.findViewById(R.id.rd6);
		CheckBox rd7 = (CheckBox) dialog.findViewById(R.id.rd7);

		rd1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(0);

				} else {
					checkedweeks.remove(0);
				}

			}

		});
		rd2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(1);

				} else {
					checkedweeks.remove(1);
				}

			}

		});
		rd3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(2);

				} else {
					checkedweeks.remove(2);
				}

			}

		});
		rd4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(3);

				} else {
					checkedweeks.remove(3);
				}

			}

		});
		rd5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(4);

				} else {
					checkedweeks.remove(4);
				}

			}

		});
		rd6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(5);

				} else {
					checkedweeks.remove(5);
				}

			}

		});
		rd7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					checkedweeks.add(6);

				} else {
					checkedweeks.remove(6);
				}

			}

		});

		btnsubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				weekdays = checkedweeks.toString().replaceAll("\\[|\\]","");
				
				reccurerequest();

			}
		});

		dialog.show();

	}

	protected void reccurerequest() {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CREATE_REQUEST_RECURR);
		map.put(Const.Params.TOKEN, pHelper.getSessionToken());
		map.put(Const.Params.ID, pHelper.getUserId());
		map.put(Const.Params.DAY, weekdays);
		LatLng curretLatLng = getLocationFromAddress(etEnterSouceltr.getText()
				.toString());
		if (curretLatLng != null) {
			map.put(Const.Params.LATITUDE,
					String.valueOf(curretLatLng.latitude));
			map.put(Const.Params.LONGITUDE,
					String.valueOf(curretLatLng.longitude));
		} else {
			AndyUtils.removeCustomProgressDialog();
			Toast.makeText(this, "Please Enter The  Source Address Correctly",
					Toast.LENGTH_LONG).show();
			return;
		}
		map.put(Const.Params.S_ADDRESS, etEnterSouceltr.getText().toString());
		map.put(Const.Params.TYPE,
				String.valueOf(listType.get(selectedPostion).getId()));
		map.put(Const.Params.START, time);

		if (etEnterdestltr.getVisibility() == View.VISIBLE
				&& !TextUtils.isEmpty(etEnterdestltr.getText().toString())) {
			LatLng destlatlng = getLocationFromAddress(etEnterdestltr.getText()
					.toString());
			map.put(Const.Params.D_ADDRESS, etEnterdestltr.getText().toString());
			if (destlatlng != null) {
				map.put(Const.Params.DEST_LATITUDE,
						String.valueOf(destlatlng.latitude));
				map.put(Const.Params.DEST_LONGITUDE,
						String.valueOf(destlatlng.longitude));
			} else {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(this, "Please Enter The Address Correctly",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		map.put(Const.Params.COD, String.valueOf(payment_type));
		map.put(Const.Params.DISTANCE, "1");
		map.put(Const.Params.INSTRUCTION, "not required");
		Log.d("mahi", "map for recurr " + map.toString());
		new HttpRequester(this, map, Const.ServiceCode.CREATE_REQUEST_RECURR,
				this);

	}

	private void pickuplater() {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CREATE_REQUEST_LATER);
		map.put(Const.Params.TOKEN, pHelper.getSessionToken());
		map.put(Const.Params.ID, pHelper.getUserId());
		LatLng curretLatLng = getLocationFromAddress(etEnterSouceltr.getText()
				.toString());
		if (curretLatLng != null) {
			map.put(Const.Params.LATITUDE,
					String.valueOf(curretLatLng.latitude));
			map.put(Const.Params.LONGITUDE,
					String.valueOf(curretLatLng.longitude));
		} else {
			AndyUtils.removeCustomProgressDialog();
			Toast.makeText(this, "Please Enter The  Source Address Correctly",
					Toast.LENGTH_LONG).show();
			return;
		}
		map.put(Const.Params.S_ADDRESS, etEnterSouceltr.getText().toString());
		map.put(Const.Params.TYPE,
				String.valueOf(listType.get(selectedPostion).getId()));
		map.put(Const.Params.DATETIME, date + " " + time);

		if (etEnterdestltr.getVisibility() == View.VISIBLE
				&& !TextUtils.isEmpty(etEnterdestltr.getText().toString())) {
			LatLng destlatlng = getLocationFromAddress(etEnterdestltr.getText()
					.toString());
			map.put(Const.Params.D_ADDRESS, etEnterdestltr.getText().toString());
			if (destlatlng != null) {
				map.put(Const.Params.DEST_LATITUDE,
						String.valueOf(destlatlng.latitude));
				map.put(Const.Params.DEST_LONGITUDE,
						String.valueOf(destlatlng.longitude));
			} else {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(this, "Please Enter The Address Correctly",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		map.put(Const.Params.COD, String.valueOf(payment_type));
		map.put(Const.Params.DISTANCE, "1");
		Log.d("mahi", "map " + map.toString());
		new HttpRequester(this, map, Const.ServiceCode.CREATE_REQUEST_LATER,
				this);

	}

	private LatLng getLocationFromAddress(final String place) {

		LatLng loc = null;
		Geocoder gCoder = new Geocoder(this);
		try {
			final List<Address> list = gCoder.getFromLocationName(place, 1);

			// TODO Auto-generated method stub
			if (list != null && list.size() > 0) {

				loc = new LatLng(list.get(0).getLatitude(), list.get(0)
						.getLongitude());

			}

		} catch (IOException e) {
			getlocfromaddressfromGoogleApi(place);
			if (latlng_places != null)
				loc = latlng_places;

			e.printStackTrace();

		}

		return loc;
	}

	private void getlocfromaddressfromGoogleApi(String address) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_ADDRESS + address);

		new HttpRequester(this, map, Const.ServiceCode.GET_LOCATION, true, this);

	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {

		case Const.ServiceCode.GET_VEHICAL_TYPES:

			if (this.pContent.isSuccess(response)) {
				listType.clear();
				pContent.parseTypes(response, listType);
				if (listType.size() > 0) {
					if (listType != null && listType.get(0) != null)
						listType.get(0).isSelected = true;
					typeAdapter.notifyDataSetChanged();
				}

				layouts = new LinearLayout[0];
				layouts = new LinearLayout[listType.size()];
				views = new View[0];
				views = new View[listType.size()];

				tvServiceTypes = new TextView[0];
				tvServiceTypes = new TextView[listType.size()];

				setTabServiceType(listType);
			}

			AndyUtils.removeCustomProgressDialog();
			break;
		case Const.ServiceCode.GET_LOCATION:

			try {
				JSONObject jsonobject = new JSONObject("" + response);

				if (jsonobject.get("status").equals("OK")) {
					JSONArray jarray = jsonobject.getJSONArray("results");

					if (jarray.length() > 0) {

						JSONObject jobj1 = jarray.getJSONObject(0);
						JSONObject jobj2 = jobj1.getJSONObject("geometry");
						JSONObject jobj3 = jobj2.getJSONObject("location");
						latlng_places = new LatLng(jobj3.getDouble("lat"),
								jobj3.getDouble("lng"));

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case Const.ServiceCode.CREATE_REQUEST_LATER:

			Log.d("mahi", "create req later" + response);

			if (pContent.isSuccess(response)) {

				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(this, "Your Trip is Scheduled Successfully !",
						Toast.LENGTH_LONG).show();
				Intent i = new Intent(this, MainDrawerActivity.class);
				startActivity(i);

			} else {
				Toast.makeText(this, "Failed to Schedule Trip !",
						Toast.LENGTH_LONG).show();
				AndyUtils.removeCustomProgressDialog();
			}

			break;
		case Const.ServiceCode.CREATE_REQUEST_RECURR:

			Log.d("mahi", "create req later reccc" + response);

			if (pContent.isSuccess(response)) {

				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(this, "Your Trip is Scheduled Successfully !",
						Toast.LENGTH_LONG).show();
				Intent i = new Intent(this, MainDrawerActivity.class);
				startActivity(i);

			} else {
				Toast.makeText(this, "Failed to Schedule Trip !",
						Toast.LENGTH_LONG).show();
				AndyUtils.removeCustomProgressDialog();
			}

			break;
		default:
			break;
		}
	}

	//@Override
	public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute) + ":"
				+ "00";
		update();

	}

	//@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		calendar.set(year, monthOfYear, dayOfMonth);
		date = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1)
				+ "-" + String.valueOf(dayOfMonth);
		update();

	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
