package com.cabrunzltd.user.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.cabrunzltd.user.ContactListActivity;
import com.cabrunzltd.user.R;
import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontPopUpTextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.models.Driver;
import com.cabrunzltd.user.models.DriverLocation;
import com.cabrunzltd.user.models.Route;
import com.cabrunzltd.user.models.Step;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.VolleyHttpRequest;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.LocationHelper;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.cabrunzltd.user.R.id.btnMyLocation;

//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationClient;

/**
 * @author Hardik A Bhalodi
 */
public class UberTripFragment extends UberBaseFragment implements ErrorListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	private static final int PICK_CONTACT = 1, PICK_MULTIPLE_CONTACT = 2;
	private static GoogleMap map;
	private PolylineOptions lineOptions;
	private Route route;
	ArrayList<LatLng> points;
	private Route routeDest;
	private PolylineOptions lineOptionsDest;
	private ArrayList<LatLng> pointsDest;
	private RequestQueue requestQueue;
	AlertDialog.Builder gpsBuilder;

	private TextView tvTime, tvDist, tvDriverName, tvDriverPhone, tvRate,
			tvStatus;
	private Driver driver;
	private Marker myMarker, markerDriver, destinationmarker;
	private ImageView ivDriverPhoto;
	private LocationHelper locHelper;
	LinearLayout timerview;
	private boolean isContinueStatusRequest;
	private boolean isContinueDriverRequest;
	private Timer timer, timerDriverLocation;
//	private LocationClient client;
	private final int LOCATION_SCHEDULE = 10 * 1000;
	// private final int LOCATION_SCHEDULE = 5 * 1000;
	private String strDistance;
	private Polyline polyLine,polyLineDest;
	private LatLng myLatLng,latlngdestination;
	private Location myLocation;
	private boolean isTripStarted = false;
	private TextView tvvehicalno, tvmodelno;
	private final int DRAW_TIME = 5 * 1000;
	private String lastTime,lastDist;
	private String lastTime2;
	private String lastDistance;
	private WalkerStatusReceiver walkerReceiver;
	private boolean isAllLocationReceived = false;
	WakeLock wakeLock;
	private PopupWindow notificationWindow, driverStatusWindow;
	private MyFontPopUpTextView tvPopupMsg, tvJobAccepted, tvDriverStarted,
			tvDriverArrvied, tvTripStarted, tvTripCompleted;
	private ImageView ivJobAccepted, ivDriverStarted, ivDriverArrvied,
			ivTripStarted, ivTripCompleted;
	private boolean isNotificationArrievd = false;
	private RatingBar ratingBarTrip;
	Chronometer tvtripTime;
	long timetrip = 0;

	private Pubnub pubnub;

	private Uri notification;
	private MediaPlayer r;
	int selector = 2;
	private ArrayList<String> selectedContacts, phonesarray;
	String duration = "";
	MyFontButton shareeta, btnCanceltrip;

	private GoogleApiClient client;
	private LocationRequest mLocationRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.UberBaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		phonesarray = new ArrayList<String>();
		PowerManager powerManager = (PowerManager) activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				Const.TAG);
		wakeLock.acquire();
		driver = (Driver) getArguments().getParcelable(Const.DRIVER);
		points = new ArrayList<LatLng>();
		route = new Route();
		IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
		walkerReceiver = new WalkerStatusReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				walkerReceiver, filter);
		isAllLocationReceived = false;

		//activity.pHelper.putCurrentFragment(Const.FRAGMENT_TRIP);

		pubnub = new Pubnub(Const.PUBNUB_PUBLISH_KEY,
				Const.PUBNUB_SUBSCRIBE_KEY);
		requestQueue = Volley.newRequestQueue(activity);

		try {
			pubnub.subscribe(Const.PUBNUB_WALK_STARTED_REQ_ID,
					new CallbackListener());
			pubnub.subscribe(Const.PUBNUB_WALKER_ARRIVED_REQ_ID,
					new CallbackListener());
			pubnub.subscribe(Const.PUBNUB_WALKER_STARTED_REQ_ID,
					new CallbackListener());
			pubnub.subscribe(Const.PUBNUB_WALKER_REACHED_REQ_ID,
					new CallbackListener());
		} catch (PubnubException e1) {
			e1.printStackTrace();
//			Log.e("MSG", "##PubNub exception: " + e1.toString());
		}

		try {
			notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = MediaPlayer.create(getActivity(), notification);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class CallbackListener extends Callback {
		@Override
		public void connectCallback(String arg0, Object arg1) {
			super.connectCallback(arg0, arg1);
		}

		@Override
		public void disconnectCallback(String arg0, Object arg1) {
			super.disconnectCallback(arg0, arg1);
		}

		@Override
		public void reconnectCallback(String arg0, Object arg1) {
			super.reconnectCallback(arg0, arg1);
		}

		@Override
		public void successCallback(String channel, Object message) {
//			Log.e("PubClient", "##PubNub Message: " + message);
			final String callbackMessage = message.toString();
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					DriverLocation driverLocation = activity.pContent
							.getDriverLocation(callbackMessage);
					// LatLng client_location = new LatLng(12.9135636,
					// 77.5950804);
					LatLng driver_location = driverLocation.getLatLng();
					try {
						UpdateDriverLocation(driver_location);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// drawTrip(driverLocation.getLatLng());
					if (myLatLng != null) {
						setMarker(myLatLng);
//						Log.e("", "" + myLatLng.latitude);
						String route_url = getDirectionsUrl(myLatLng,
								driver_location);
						Map<String, String> map = new HashMap<String, String>();
						map.put("url", route_url);
						new HttpRequester(activity, map,
								Const.ServiceCode.DRIVER_ROUTE, true,
								UberTripFragment.this);
					}
					if (isTripStarted) {
						long startTime = Const.NO_TIME;
						if (activity.pHelper.getRequestTime() == Const.NO_TIME) {
							startTime = System.currentTimeMillis();
							activity.pHelper.putRequestTime(startTime);
						} else {
							startTime = activity.pHelper.getRequestTime();
						}
						double distance = Double.parseDouble(driverLocation
								.getDistance());
						lastDist = String.valueOf(distance);
						tvDist.setText(new DecimalFormat("0.00")
								.format(distance)
								+ " "
								+ driverLocation.getUnit());
						// long elapsedTime = System.currentTimeMillis()
						// - startTime;
						lastTime = driverLocation.getTime()
								+ " "
								+ activity.getResources().getString(
										R.string.text_mins);

						/*
						 * long elapsedTime = System.currentTimeMillis() -
						 * startTime; lastTime2 = elapsedTime / (1000 * 60) +
						 * " " + getResources().getString(R.string.text_mins);
						 */
						// tvTime.setText(lastTime);

						// tvTime.setText(lastTime);
					}
					isContinueDriverRequest = true;
				}
			});

			super.successCallback(channel, message);
		}

		@Override
		public void errorCallback(String arg0, PubnubError arg1) {
			super.errorCallback(arg0, arg1);
		}
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity.setTitle(getString(R.string.app_name));
		if (container == null) {
	        return null;
	    }
		
		 	
		 View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_trip, container, false);
				view.findViewById(R.id.btnCall).setOnClickListener(this);
		
			
		try {
			MapsInitializer.initialize(activity);
		} catch (Exception e) {
		}
		// view.findViewById(R.id.btnCanceltrip).setOnClickListener(this);
		btnCanceltrip = (MyFontButton) view.findViewById(R.id.btnCanceltrip);
		btnCanceltrip.setOnClickListener(this);
		shareeta = (MyFontButton) view.findViewById(R.id.btnshareeta);
		shareeta.setOnClickListener(this);
		tvTime = (MyFontTextView) view.findViewById(R.id.tvJobTime);
		tvDist = (MyFontTextView) view.findViewById(R.id.tvJobDistance);
		timerview = (LinearLayout) view.findViewById(R.id.timer);
		tvtripTime = (Chronometer) view.findViewById(R.id.tvtripTime);
		tvDriverName = (MyFontTextView) view.findViewById(R.id.tvDriverName);
		tvDriverPhone = (MyFontTextView) view.findViewById(R.id.tvDriverPhone);
		ivDriverPhoto = (ImageView) view.findViewById(R.id.ivDriverPhoto);
		tvvehicalno = (TextView) view.findViewById(R.id.tvvehicalno);
		tvmodelno = (TextView) view.findViewById(R.id.tvmodelno);
		// tvRate = (TextView) view.findViewById(R.id.tvRate);
		ratingBarTrip = (RatingBar) view.findViewById(R.id.ratingBarTrip);

		tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		if (driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) {
			shareeta.setVisibility(View.GONE);
		}
		// tvRate.setText(""+new
		// DecimalFormat("0.0").format(driver.getRating()));
		ratingBarTrip.setRating((float) driver.getRating());

		tvDriverPhone.setText(driver.getPhone());
		tvDriverName
				.setText(driver.getFirstName() + " " + driver.getLastName());
		tvvehicalno.setText(driver.getCar_NO());
		tvmodelno.setText(driver.getCar_Model());
		new AQuery(activity).id(ivDriverPhoto).progress(R.id.pBar)
				.image(driver.getPicture(), true, true);
//		setUpMap();
			client = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

			setUpMapIfNeeded();
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return view;
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// tvDist.setText(strDistance + "");
		// new AQuery(activity).id(ivDriverPhoto).progress(R.id.pBar)
		// .image(driver.getPicture(), true, true);
		locHelper = new LocationHelper(activity);
		locHelper.setLocationReceivedLister(new LocationHelper.OnLocationReceived() {

			@Override
			public void onLocationReceived(LatLng latlong) {
				// TODO Auto-generated method stub
				
				if (isTripStarted && isAllLocationReceived && myLocation != null) {
					// drawTrip(latlong);
					myLocation.setLatitude(latlong.latitude);
					myLocation.setLongitude(latlong.longitude);
					setMarker(latlong);
					startUpdateDriverLocation();

				}

			}
		});
		locHelper.onStart();
		// PopUp Window
		LayoutInflater inflate = LayoutInflater.from(activity);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(
				R.layout.popup_notification_window, null);
		tvPopupMsg = (MyFontPopUpTextView) layout.findViewById(R.id.tvPopupMsg);

		notificationWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setOnClickListener(this);
		activity.btnNotification.setOnClickListener(this);

		// Big PopUp Window
		RelativeLayout bigPopupLayout = (RelativeLayout) inflate.inflate(
				R.layout.popup_notification_status_window, null);
		tvJobAccepted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvJobAccepted);
		tvDriverStarted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvDriverStarted);
		tvDriverArrvied = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvDriverArrvied);
		tvTripStarted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvTripStarted);
		tvTripCompleted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvTripCompleted);

		ivJobAccepted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivJobAccepted);
		ivDriverStarted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivDriverStarted);
		ivDriverArrvied = (ImageView) bigPopupLayout
				.findViewById(R.id.ivDriverArrvied);
		ivTripStarted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivTripStarted);
		ivTripCompleted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivTripCompleted);
		driverStatusWindow = new PopupWindow(bigPopupLayout,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		driverStatusWindow.setBackgroundDrawable(new BitmapDrawable());
		// driverStatusWindow.setFocusable(false);
		// driverStatusWindow.setTouchable(true);
		driverStatusWindow.setOutsideTouchable(true);
		showNotificationPopUp("Accepted");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCall:
			if (driver != null) {
				String number = driver.getPhone();
				if (!TextUtils.isEmpty(number)) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + number));
					startActivity(callIntent);
				}
			}
			break;
		case R.id.rlPopupWindow:
			notificationWindow.dismiss();
			activity.setIcon(R.drawable.icon_notification);
			break;

		case R.id.btnCanceltrip:
			canceltrippopup();
			break;
		case R.id.btnActionNotification:
			showDriverStatusNotification();
			break;
		case R.id.btnshareeta:
			if (driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) {
				Toast.makeText(activity, "Destination not set",
						Toast.LENGTH_LONG).show();
			} else {
				// gettime();
				Intent intent = new Intent(activity, ContactListActivity.class);
				activity.startActivityForResult(intent, PICK_MULTIPLE_CONTACT,
						Const.FRAGMENT_TRIP);
			}
			/*
			 * Intent intent = new Intent(Intent.ACTION_PICK,
			 * Contacts.CONTENT_URI); activity.startActivityForResult(intent,
			 * PICK_CONTACT,Const.FRAGMENT_TRIP);
			 */
			break;

		default:
			// if(driverStatusWindow.isShowing())
			// driverStatusWindow.dismiss();
			break;
		}

	}

	private void canceltrippopup() {
		// TODO Auto-generated method stub

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					cancelRequest();
					dialog.dismiss();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					dialog.dismiss();
					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Are you sure you want to cancel Trip?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();

	}

	private void cancelRequest() {
		// TODO Auto-generated method stub

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		/* AndyUtils.removeCustomProgressDialog(); */
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_canceling_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CANCEL_REQUEST);
		map.put(Const.Params.ID, String.valueOf(activity.pHelper.getUserId()));
		map.put(Const.Params.TOKEN,
				String.valueOf(activity.pHelper.getSessionToken()));
		map.put(Const.Params.REQUEST_ID,
				String.valueOf(activity.pHelper.getRequestId()));

		new HttpRequester(activity, map, Const.ServiceCode.CANCEL_REQUEST, this);

	}

	public void showDriverStatusNotification() {
		activity.setIcon(R.drawable.icon_notification);
		if (driverStatusWindow.isShowing())
			driverStatusWindow.dismiss();
		else {
			if (notificationWindow.isShowing())
				notificationWindow.dismiss();
			else
				driverStatusWindow.showAsDropDown(activity.btnNotification);
		}

	}

	public void showNotificationPopUp(String text) {
		tvPopupMsg.setText(text);
		if (!driverStatusWindow.isShowing()) {

			if (!notificationWindow.isShowing()) {
				activity.setIcon(R.drawable.icon_notification);
				notificationWindow.showAsDropDown(activity.btnNotification);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.currentFragment = Const.FRAGMENT_TRIP;
		// if (activity.pHelper.getRequestTime() == Const.NO_TIME)
		// setRequestTime(SystemClock.e);

		activity.btnNotification.setVisibility(View.VISIBLE);
		startUpdateDriverLocation();
		startCheckingStatusUpdate();

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		client.connect();
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
		startUpdateDriverLocation();
		startCheckingStatusUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		stopUpdateDriverLoaction();
		stopCheckingStatusUpdate();

		super.onPause();

	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		setUpMap();

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maptrip);
			mapFrag.getMapAsync(this);
		}


	}

	private void setUpMap() {

		if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			final int REQUEST_LOCATION = 2;

			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				// Display UI and wait for user interaction
			} else {
				ActivityCompat.requestPermissions(
						activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						99);
			}


		}
		map.setMyLocationEnabled(true);

//		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//		map.setTrafficEnabled(true);
//		map.setIndoorEnabled(true);
//		map.setBuildingsEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.getUiSettings().setZoomControlsEnabled(false);

		map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

				// Use default InfoWindow frame

				@Override
				public View getInfoWindow(Marker marker) {
					View v = activity.getLayoutInflater().inflate(
							R.layout.info_window_layout, null);
					MyFontTextView title = (MyFontTextView) v
							.findViewById(R.id.locationtitle);
					MyFontTextView content = (MyFontTextView) v
							.findViewById(R.id.infoaddress);
					title.setText(marker.getTitle());

					getAddressFromLocation(marker.getPosition(), content);

					// ((MyFontTextView) v).setText(marker.getTitle());
					return v;
				}

				// Defines the contents of the InfoWindow

				@Override
				public View getInfoContents(Marker marker) {

					// Getting view from the layout file info_window_layout View

					// Getting reference to the TextView to set title TextView

					// Returning the view containing InfoWindow contents return
					return null;

				}

		});

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.showInfoWindow();
					return true;
				}
		});







	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}



	public void onConnected(Bundle connectionHint) {

		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}


		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(3000);
		mLocationRequest.setFastestInterval(3000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
		LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);

		if (loc != null) {



					myLocation = loc;
					myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
					setMarkers(myLatLng);
					try {
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}




		} else {
			activity.showLocationOffDialog();
		}


	}


	@Override
	public void onConnectionSuspended(int arg0) {
	}



	@Override
	public void onLocationChanged(Location loc) {
		drawTrip(new LatLng(loc.getLatitude(), loc.getLongitude()));
	}

//	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
//		if (map == null) {
//			map = ((SupportMapFragment) getActivity()
//					.getSupportFragmentManager().findFragmentById(R.id.maptrip))
//					.getMap();
//			// map.setOnMyLocationChangeListener(new
//			// OnMyLocationChangeListener() {
//			//
//			// @Override
//			// public void onMyLocationChange(Location arg0) {
//			// // TODO Auto-generated method stub
//			// drawTrip(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
//			// }
//			// });
//			map.setInfoWindowAdapter(new InfoWindowAdapter() {
//
//				// Use default InfoWindow frame
//
//				@Override
//				public View getInfoWindow(Marker marker) {
//					View v = activity.getLayoutInflater().inflate(
//							R.layout.info_window_layout, null);
//					MyFontTextView title = (MyFontTextView) v
//							.findViewById(R.id.locationtitle);
//					MyFontTextView content = (MyFontTextView) v
//							.findViewById(R.id.infoaddress);
//					title.setText(marker.getTitle());
//
//					getAddressFromLocation(marker.getPosition(), content);
//
//					// ((MyFontTextView) v).setText(marker.getTitle());
//					return v;
//				}
//
//				// Defines the contents of the InfoWindow
//
//				@Override
//				public View getInfoContents(Marker marker) {
//
//					// Getting view from the layout file info_window_layout View
//
//					// Getting reference to the TextView to set title TextView
//
//					// Returning the view containing InfoWindow contents return
//					return null;
//
//				}
//
//			});
//
//			map.setOnMarkerClickListener(new OnMarkerClickListener() {
//				@Override
//				public boolean onMarkerClick(Marker marker) {
//					marker.showInfoWindow();
//					return true;
//				}
//			});
//		}
//		//initPreviousDrawPath();
//		client = new LocationClient(activity, new ConnectionCallbacks() {
//
//			@Override
//			public void onDisconnected() {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onConnected(Bundle connectionHint) {
//				// TODO Auto-generated method stub
//
//				Location loc = client.getLastLocation();
//
//				if (loc != null) {
//					myLocation = loc;
//					myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
//					setMarkers(myLatLng);
//					try {
//						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}else {
//					showLocationOffDialog();
//				}
//
//			}
//		}, new OnConnectionFailedListener() {
//
//			@Override
//			public void onConnectionFailed(ConnectionResult result) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		client.connect();

//	}
	
	public void showLocationOffDialog() {

		gpsBuilder = new AlertDialog.Builder(activity);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getString(R.string.dialog_no_location_service_title))
				.setMessage(getString(R.string.dialog_no_location_service))
				.setPositiveButton(
						getString(R.string.dialog_enable_location_service),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								dialog.dismiss();
								Intent viewIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(viewIntent);

							}
						})

				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								dialog.dismiss();
								activity.finish();
							}
						});
		gpsBuilder.create();
		gpsBuilder.show();
	}

	private void setMarkers(LatLng latLang) {
		LatLng latLngDriver = new LatLng(driver.getLatitude(),
				driver.getLongitude());

		setMarker(latLang);

		setDriverMarker(latLngDriver);
		if (driver.getD_latitude() != 0.0 && driver.getD_longitude() != 0.0) {
			latlngdestination = new LatLng(driver.getD_latitude(),
					driver.getD_longitude());
			setdestinationmarker(latlngdestination);
		}
		animateCameraToMarkerWithZoom(latLngDriver);

		// showDirection(latLang, latLngDriver);
		// Location locDriver = new Location("");
		// locDriver.setLatitude(driver.getLatitude());
		// locDriver.setLongitude(driver.getLongitude());
		// strDistance = convertMilesFromMeters(loc
		// .distanceTo(locDriver));
		// animateCameraToMarker(latLang);
	}

	private void showDirection(LatLng source, LatLng destination) {

		Map<String, String> hashMap = new HashMap<String, String>();

		final String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
				+ source.latitude
				+ ","
				+ source.longitude
				+ "&destination="
				+ destination.latitude
				+ ","
				+ destination.longitude
				+ "&sensor=false";

		// hashMap.put("url", url);
		new HttpRequester(activity, hashMap, Const.ServiceCode.GET_ROUTE, true,
				this);
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_getting_direction), false, null);

	}
	private void drawPath(LatLng source, LatLng destination) {
		if (source == null || destination == null) {
			return;
		}
		if (destination.latitude != 0) {
			

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL,
					"http://maps.googleapis.com/maps/api/directions/json?origin="
							+ source.latitude + "," + source.longitude
							+ "&destination=" + destination.latitude + ","
							+ destination.longitude + "&sensor=false");
			// new HttpRequester(activity, map, Const.ServiceCode.DRAW_PATH,
			// true,
			// this);
//			Log.d("map", "map draw"+map);
			requestQueue.add(new VolleyHttpRequest(1, map,
					Const.ServiceCode.DRAW_PATH, this, this));
		}

	}
	public void onDestroyView() {
		wakeLock.release();
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.maptrip);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		map = null;
		super.onDestroyView();
	}

	public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for (int i = 0; i < jRoutes.length(); i++) {
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++) {
					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for (int k = 0; k < jSteps.length(); k++) {
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for (int l = 0; l < list.size(); l++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat",
									Double.toString(((LatLng) list.get(l)).latitude));
							hm.put("lng",
									Double.toString(((LatLng) list.get(l)).longitude));
							path.add(hm);
						}
					}
					routes.add(path);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}

		return routes;
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskCompleted(final String response, int serviceCode) {
		// TODO Auto-generated method stub
		if (!this.isVisible())
			return;
		switch (serviceCode) {
		case Const.ServiceCode.DRIVER_ROUTE:
			if (response != null)
				try {

					/*List<List<HashMap<String, String>>> result = parse(new JSONObject(
							response));
					// Traversing through all the routes
					for (int i = 0; i < result.size(); i++) {
						points = new ArrayList<LatLng>();
						lineOptions = new PolylineOptions();

						// Fetching i-th route
						List<HashMap<String, String>> path = result.get(i);

						// Fetching all the points in i-th route
						for (int j = 0; j < path.size(); j++) {
							HashMap<String, String> point = path.get(j);

							double lat = Double.parseDouble(point.get("lat"));
							double lng = Double.parseDouble(point.get("lng"));
							LatLng position = new LatLng(lat, lng);

							points.add(position);
						}

						// Adding all the points in the route to LineOptions
						lineOptions.addAll(points);
						lineOptions.width(10);
						lineOptions.color(getResources().getColor(
								R.color.skyblue));
					}
					map.addPolyline(lineOptions);*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			break;
			
		case Const.ServiceCode.DRAW_PATH_ROAD:
//			Log.d("map", ""+response);
			if (!TextUtils.isEmpty(response)) {
				routeDest = new Route();
				activity.pContent.parseRoute(response, routeDest);

				final ArrayList<Step> step = routeDest.getListStep();
				System.out.println("step size=====> " + step.size());
				pointsDest = new ArrayList<LatLng>();
				lineOptionsDest = new PolylineOptions();
				lineOptionsDest.geodesic(true);

				for (int i = 0; i < step.size(); i++) {
					List<LatLng> path = step.get(i).getListPoints();
					System.out.println("step =====> " + i + " and "
							+ path.size());
					pointsDest.addAll(path);
				}
				if (pointsDest != null && pointsDest.size() > 0 && latlngdestination != null && myMarker.getPosition()!= null) {
					drawPath(myMarker.getPosition(),latlngdestination);
				}
			}
			break;
			
		case Const.ServiceCode.DRAW_PATH:
			if (!TextUtils.isEmpty(response)) {
				routeDest = new Route();
				activity.pContent.parseRoute(response, routeDest);

				final ArrayList<Step> step = routeDest.getListStep();
				System.out.println("step size=====> " + step.size());
				pointsDest = new ArrayList<LatLng>();
				lineOptionsDest = new PolylineOptions();
				lineOptionsDest.geodesic(true);

				for (int i = 0; i < step.size(); i++) {
					List<LatLng> path = step.get(i).getListPoints();
					System.out.println("step =====> " + i + " and "
							+ path.size());
					pointsDest.addAll(path);
				}
				if (polyLineDest != null)
					polyLineDest.remove();
				lineOptionsDest.addAll(pointsDest);
				lineOptionsDest.width(6);
				lineOptionsDest.color(getResources().getColor(
						R.color.color_text)); // #00008B rgb(0,0,139)
				try {
					if (lineOptionsDest != null && map != null) {
						polyLineDest = map.addPolyline(lineOptionsDest);
						boundLatLang();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
			
		case Const.ServiceCode.GET_ROUTE:
			AndyUtils.removeCustomProgressDialog();
			/*if (response != null)
				if (!TextUtils.isEmpty(response)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new ParseContent(activity).parseRoute(response,
									route);

							final ArrayList<Step> step = route.getListStep();
							points = new ArrayList<LatLng>();
							lineOptions = new PolylineOptions();

							for (int i = 0; i < step.size(); i++) {

								List<LatLng> path = step.get(i).getListPoints();
								// System.out.println("step =====> " + i +
								// " and "
								// + path.size());
								points.addAll(path);

							}
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (polyLine != null)
										polyLine.remove();
									lineOptions.addAll(points);
									lineOptions.width(15);

									lineOptions.color(getResources().getColor(
											R.color.skyblue));
									polyLine = map.addPolyline(lineOptions);
									LatLngBounds.Builder bld = new LatLngBounds.Builder();
									bld.include(myMarker.getPosition());
									bld.include(markerDriver.getPosition());
									LatLngBounds latLngBounds = bld.build();
									map.moveCamera(CameraUpdateFactory
											.newLatLngBounds(latLngBounds, 50));
									// tvDist.setText(route.getDistanceText());
									// tvTime.setText(route.getDurationText());
									// tvDist.setText(0 + " KM");
									// tvTime.setText(0 + " MINS");

								}
							});
						}
					}).start();
				}*/
			break;
		case Const.ServiceCode.GET_REQUEST_LOCATION:

			if (activity.pContent.isSuccess(response)) {
//				Log.e("mahi", "location update"+response);
				
				try {
					JSONObject jobj = new JSONObject(response);
					String time = jobj.getString("time");
					
					tvTime.setText(time+" "+ "MINS");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				DriverLocation driverLocation = activity.pContent
						.getDriverLocation(response);
				if (driverLocation == null || !this.isVisible())
					return;
				setDriverMarker(driverLocation.getLatLng());
				//drawTrip(driverLocation.getLatLng());
				if (isTripStarted) {
					
					startUpdateDriverLocation();
					long startTime = Const.NO_TIME;
					if (activity.pHelper.getRequestTime() == Const.NO_TIME) {
						startTime = System.currentTimeMillis();
						activity.pHelper.putRequestTime(startTime);
					} else {
						startTime = activity.pHelper.getRequestTime();
					}

					double distance = Double.parseDouble(driverLocation
							.getDistance());
					// distance = distance / 1625;
					// tvDist.setText(new DecimalFormat("0.00").format(distance)
					// + " " + driverLocation.getUnit());
					tvDist.setText(new DecimalFormat("0.00").format(distance)
							+ " " + driverLocation.getUnit());
					long elapsedTime = Calendar.getInstance().getTimeInMillis()
							- startTime;
					lastTime = elapsedTime / (1000 * 60) + " "
							+ getResources().getString(R.string.text_mins);
					//tvTime.setText(lastTime);
					// tvTime.setText("0" + " MINS");
					// tvDist.setText("0" + " KM");
				}

			}
			isContinueDriverRequest = true;
			break;
		case Const.ServiceCode.CANCEL_REQUEST:
			if (response != null)
				if (activity.pContent.isSuccess(response)) {
					Toast.makeText(getActivity(), "Trip Canceled Successfully",
							Toast.LENGTH_SHORT).show();
					AndyUtils.removeCustomProgressDialog();
					activity.pHelper.clearRequestData();
					activity.gotoMapFragment();

				}
			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:
//			Log.d("mahi", response);
			if (response != null)
				if (activity.pContent.isSuccess(response)) {

					// if (selector ==
					// activity.pContent.checkRequestStatus(response)) {
					// selector++;
					driver = activity.pContent.getDriverDetail(response);
					if (driver.getLastDistance() != null) {
						if (driver.getD_latitude() != 0) {
							LatLng location = new LatLng(
									driver.getD_latitude(),
									driver.getD_longitude());
							setDriverMarker(location);
							setdestinationmarker(location);
							// drawTrip(location);
						}
						try {

							tvDist.setText(""
									+ new DecimalFormat("0.00").format(driver
											.getLastDistance()) + " KM");
							/* tvTime.setText(""+driver.getLastTime()+" MINS"); */
						} catch (Exception e) {
//							Log.e("", "" + driver.getLastDistance());
							tvDist.setText("" + driver.getLastDistance()
									+ " KM");
							// tvTime.setText(""+driver.getLastTime()+" MINS");
							e.printStackTrace();
						}
					}

					switch (activity.pContent.checkRequestStatus(response)) {

					case Const.IS_WALK_STARTED:
						showNotificationPopUp("Arrived");
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_arrvied)));
						// showNotificationPopUp(getString(R.string.text_driver_arrvied));
						changeNotificationPopUpUI(3);
						isContinueStatusRequest = true;
						btnCanceltrip.setVisibility(View.GONE);
						setMarkerOnRoad(myLatLng, latlngdestination);
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);

						isTripStarted = false;
						try {
							// myMarker.remove();
						} catch (Exception e) {
							e.printStackTrace();
						}
						// GCMIntentService.(getActivity(),
						// getString(R.string.text_driver_arrvied), 1);

						break;
					case Const.IS_COMPLETED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_trip_started)));
						setMarkerOnRoad(myLatLng, latlngdestination);
						tvTime.setVisibility(View.VISIBLE);
						timerview.setVisibility(View.GONE);
						startUpdateDriverLocation();
						
						/*
						 * tvtripTime .setBase(SystemClock.elapsedRealtime() +
						 * timetrip);
						 */
						// tvtripTime.start();
						// showNotificationPopUp(getString(R.string.text_trip_started));
						showNotificationPopUp("Trip Started");
						changeNotificationPopUpUI(4);
						btnCanceltrip.setVisibility(View.GONE);
						if (!isAllLocationReceived) {
							isAllLocationReceived = true;
							getPath(String.valueOf(activity.pHelper
									.getRequestId()));
						}
						isContinueStatusRequest = true;
						isTripStarted = true;

						// GCMIntentService.(getActivity(),
						// getString(R.string.text_trip_started), 1);

						break;
					case Const.IS_WALKER_ARRIVED:
						showNotificationPopUp("On the way");
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_started)));
						if(markerDriver != null){
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
						}
						// showNotificationPopUp(getString(R.string.text_driver_started));
						changeNotificationPopUpUI(2);
						btnCanceltrip.setVisibility(View.GONE);
						isContinueStatusRequest = true;

						break;
					case Const.IS_WALKER_STARTED:
						showNotificationPopUp("Accepted");
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_job_accepted)));
						// showNotificationPopUp(getString(R.string.text_job_accepted));
						if(markerDriver != null){
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
						}
						changeNotificationPopUpUI(1);
						isContinueStatusRequest = true;

						break;
					case Const.IS_WALKER_RATED:
						// case Const.IS_WALKER_REACHED:
						stopCheckingStatusUpdate();
						/*btnCanceltrip.setVisibility(View.GONE);
						// tvtripTime.stop();
						long elapsedMillis = SystemClock.elapsedRealtime()
								- tvtripTime.getBase();*/
						isTripStarted = false;
						if (notificationWindow.isShowing())
							notificationWindow.dismiss();
						if (driverStatusWindow.isShowing())
							driverStatusWindow.dismiss();
//						Log.d("hey", response);
//
//						Log.d("mean", "trip " + response);
						activity.pContent.getDriverDetail(response);
						driver.setLastDistance(lastDist);
						driver.setLastTime(lastTime);
						activity.gotoRateFragment(driver);
						break;

					default:

						break;
					}

					// }

				} else {

					isContinueStatusRequest = true;
				}
			break;
		case Const.ServiceCode.GET_PATH:
			AndyUtils.removeCustomProgressDialog();
			activity.pContent.parsePathRequest(response, points);
			//initPreviousDrawPath();
//			AppLog.Log(Const.TAG, "Path====>" + response + "");
			break;

		case Const.ServiceCode.SEND_ETA:
//			Log.d("xxx", "response from share eta  " + response);
			selectedContacts.clear();
			phonesarray.clear();
			if (activity.pContent.isSuccess(response)) {
				Toast.makeText(activity, getString(R.string.text_eta_success),
						Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(activity, getString(R.string.text_eta_fail),
						Toast.LENGTH_LONG).show();
			break;

		case Const.ServiceCode.GET_MAP_TIME:
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

								JSONObject jobj_duration = jobj1
										.getJSONObject("duration");

								duration = jobj_duration.getString("text");

							}

						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!(driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0)) {

					shareeta.setText("Share ETA " + "(" + duration + ")");
				}

			}

			AndyUtils.removeCustomProgressDialog();
			break;
		}

	}

	private void changeNotificationPopUpUI(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 2:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 3:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverArrvied.setImageResource(R.drawable.checkbox);
			tvDriverArrvied.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 4:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverArrvied.setImageResource(R.drawable.checkbox);
			tvDriverArrvied.setTextColor(getResources().getColor(
					R.color.color_text));
			ivTripStarted.setImageResource(R.drawable.checkbox);
			tvTripStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragment#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	class TrackLocation extends TimerTask {

		public void run() {

			if (isContinueDriverRequest) {
				isContinueDriverRequest = false;
				getDriverLocation();
			}
		}
	}

	private void getDriverLocation() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "="
						+ new PreferenceHelper(activity).getRequestId());
//		AppLog.Log("TAG",
//				Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
//						+ new PreferenceHelper(activity).getUserId() + "&"
//						+ Const.Params.TOKEN + "="
//						+ new PreferenceHelper(activity).getSessionToken()
//						+ "&" + Const.Params.REQUEST_ID + "="
//						+ new PreferenceHelper(activity).getRequestId());
		new HttpRequester(activity, map,
				Const.ServiceCode.GET_REQUEST_LOCATION, true, this);
	}

	private void setdestinationmarker(LatLng latlng) {
		if (latlng != null) {
//			if (map != null && this.isVisible()) {
			if (map != null) {
				if (destinationmarker == null) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(latlng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.img_destination));
					opt.title(getString(R.string.text_my_destination));

					destinationmarker = map.addMarker(opt);
					// animateCameraToMarkerWithZoom(latLng);

				} else {
					destinationmarker.setPosition(latlng);
					// animateCameraToMarker(latLng);
				}

			}

		}
	}

	private void setMarker(LatLng latLng) {
		if (latLng != null) {

			if (map != null && this.isVisible()) {

				if (myMarker == null) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.img_usermappin));
					opt.title(getString(R.string.text_my_location));

					myMarker = map.addMarker(opt);
					// animateCameraToMarkerWithZoom(latLng);

				} else {
					myMarker.setPosition(latLng);
					// animateCameraToMarker(latLng);
				}

			}

			// if (!(driver.getD_latitude() == 0.0 && driver.getD_longitude() ==
			// 0.0)) {
			// gettime();
			// }

		} else {
			Log.e("ii", "iiii");
		}

	}

	private void setDriverMarker(LatLng latLng) {
		if (latLng != null) {
			if (map != null && this.isVisible()) {

				if (markerDriver == null) {

					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_driver));
					opt.title(getString(R.string.text_drive_location));
					markerDriver = map.addMarker(opt);

				} else {
					markerDriver.setPosition(latLng);

				}
				// animateCameraToMarker(latLng);
			}

		}

	}

	private void setDsetMarker(LatLng latLng) {
		if (latLng != null) {
			if (map != null && this.isVisible()) {

				if (markerDriver == null) {

					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_driver));
					opt.title(getString(R.string.text_drive_location));
					markerDriver = map.addMarker(opt);

				} else {
					markerDriver.setPosition(latLng);

				}
				// animateCameraToMarker(latLng);
			}

		}

	}

	private void setMarkerOnRoad(LatLng source, LatLng destination) {
		String msg = null;
		if (source == null) {
			msg = "Unable to get source location, please try again";
		} else if (destination == null) {
			msg = "Unable to get destination location, please try again";
		}
		if (msg != null) {
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				"http://maps.googleapis.com/maps/api/directions/json?origin="
						+ source.latitude + "," + source.longitude
						+ "&destination=" + destination.latitude + ","
						+ destination.longitude + "&sensor=false");
//		Log.d("map", "send"+map);

		new HttpRequester(activity, map, Const.ServiceCode.DRAW_PATH_ROAD,
				true, this);
	}

	private void UpdateDriverLocation(LatLng latLng) {
		if (map != null && this.isVisible()) {

			if (markerDriver != null) {
				markerDriver.remove();
			}
			MarkerOptions opt = new MarkerOptions();
			opt.position(latLng);
			opt.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.pin_driver));
			opt.title(getString(R.string.text_drive_location));
			try {
				markerDriver = map.addMarker(opt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// animateCameraToMarker(latLng);
		}
	}

	private void startUpdateDriverLocation() {
		isContinueDriverRequest = true;
		timerDriverLocation = new Timer();
		timerDriverLocation.scheduleAtFixedRate(new TrackLocation(),
				0, LOCATION_SCHEDULE);
	}

	private void stopUpdateDriverLoaction() {
		isContinueDriverRequest = false;
		if (timerDriverLocation != null) {
			timerDriverLocation.cancel();
			timerDriverLocation = null;
		}

	}

	private void animateCameraToMarkerWithZoom(LatLng latLng) {

		CameraUpdate cameraUpdate = null;
		cameraUpdate = CameraUpdateFactory
				.newLatLngZoom(latLng, Const.MAP_ZOOM);
		try {
			map.animateCamera(cameraUpdate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void animateCameraToMarker(LatLng latLng) {

		CameraUpdate cameraUpdate = null;
		cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
		map.animateCamera(cameraUpdate);
	}

	private String convertKmFromMeters(float disatanceInMeters) {
		return new DecimalFormat("0.0").format(0.001f * disatanceInMeters);
	}

	private void startCheckingStatusUpdate() {
		stopCheckingStatusUpdate();
		if (activity.pHelper.getRequestId() != Const.NO_REQUEST) {
			isContinueStatusRequest = true;
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY,
					Const.TIME_SCHEDULE);
		}
	}

	private void stopCheckingStatusUpdate() {
		isContinueStatusRequest = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private class TimerRequestStatus extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (isContinueStatusRequest) {
				isContinueStatusRequest = false;
				getRequestStatus(String
						.valueOf(activity.pHelper.getRequestId()));
			}
		}

	}

	private void getRequestStatus(String requestId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

//		Log.e("", "" + map);
		new HttpRequester(activity, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	private void getPath(String requestId) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_PATH + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);
		new HttpRequester(activity, map, Const.ServiceCode.GET_PATH, true, this);
	}

	private void setRequestTime(long time) {
		activity.pHelper.putRequestTime(time);
	}

	private void drawTrip(LatLng latlng) {

		if (map != null && this.isVisible()) {

			points.add(latlng);
			lineOptions = new PolylineOptions();
			lineOptions.addAll(points);
			lineOptions.width(15);
			lineOptions.color(getResources().getColor(R.color.skyblue));

			map.addPolyline(lineOptions);
		}

	}

	class WalkerStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			/*String response1 = intent.getStringExtra("message");
			Log.e("", ":" + response1);
			if (!TextUtils.isEmpty(response1)) {
				startActivity(new Intent(activity, FriendPayActivity.class));
				activity.finish();
			} else {
			}*/

			String response = intent.getStringExtra(Const.EXTRA_WALKER_STATUS);
//			Log.d("hey", response);
//			AppLog.Log("Response ---- Trip", response);
			if (TextUtils.isEmpty(response))
				return;
			stopCheckingStatusUpdate();

			if (activity.pContent.isSuccess(response)) {

				// if (selector ==
				// activity.pContent.checkRequestStatus(response)) {
				// selector++;

				switch (activity.pContent.checkRequestStatus(response)) {
				
				
				case Const.IS_WALK_STARTED:
					tvStatus.setText(Html
							.fromHtml(getString(R.string.text_driver_arrvied)));
					showNotificationPopUp("Arrived");
					setMarkerOnRoad(myLatLng, latlngdestination);
					changeNotificationPopUpUI(3);
					btnCanceltrip.setVisibility(View.GONE);
//					Log.d("yyy", "3");
					isContinueStatusRequest = true;
					isTripStarted = false;
					// myMarker.remove();
					break;
				case Const.IS_COMPLETED:
					tvStatus.setText(Html
							.fromHtml(getString(R.string.text_trip_started)));
					showNotificationPopUp("Trip Started");
					changeNotificationPopUpUI(4);
					try {
						setMarkerOnRoad(myLatLng, latlngdestination);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					btnCanceltrip.setVisibility(View.GONE);
					tvTime.setVisibility(View.VISIBLE);
					timerview.setVisibility(View.GONE);
					startUpdateDriverLocation();
					tvtripTime
							.setBase(SystemClock.elapsedRealtime() + timetrip);
					tvtripTime.start();
//					Log.d("yyy", "4");
					if (!isAllLocationReceived) {
						isAllLocationReceived = true;
						getPath(String.valueOf(activity.pHelper.getRequestId()));
					}
					isContinueStatusRequest = true;
					isTripStarted = true;
					break;
				case Const.IS_WALKER_ARRIVED:
					tvStatus.setText(Html
							.fromHtml(getString(R.string.text_driver_started)));
					showNotificationPopUp("On the way");
					changeNotificationPopUpUI(2);
					try {
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					btnCanceltrip.setVisibility(View.GONE);
//					Log.d("yyy", "2");
					isContinueStatusRequest = true;
					break;
				case Const.IS_WALKER_STARTED:
					tvStatus.setText(Html
							.fromHtml(getString(R.string.text_job_accepted)));
					showNotificationPopUp("Accepted");
					changeNotificationPopUpUI(1);
					try {
						setMarkerOnRoad(markerDriver.getPosition(), myLatLng);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					Log.d("yyy", "1");

					isContinueStatusRequest = true;

					break;
				case Const.IS_WALKER_RATED:
					// case Const.IS_WALKER_REACHED:
					stopCheckingStatusUpdate();
					/*tvtripTime.stop();
					btnCanceltrip.setVisibility(View.GONE);*/
					isTripStarted = false;
					if (notificationWindow.isShowing())
						notificationWindow.dismiss();
					if (driverStatusWindow.isShowing())
						driverStatusWindow.dismiss();
//					Log.e("MSG", "trip in broadcast" + response);

					driver = activity.pContent.getDriverDetail(response);
					// tvDist.setText(driver.getLastDistance()+ " KM");
					// tvTime.setText(driver.getLastTime() + " MINS");
					// Log.d("hey", "trip" + response);
					driver.setLastDistance(lastDist);
					driver.setLastTime(lastTime);
					activity.gotoRateFragment(driver);
					break;

				default:

					break;

				}
			}
			// }

			else {
				isContinueStatusRequest = true;
			}
			startCheckingStatusUpdate();

		}
	}

	private void initPreviousDrawPath() {
		lineOptions = new PolylineOptions();
		lineOptions.addAll(points);
		lineOptions.width(15);
		lineOptions.color(getResources().getColor(R.color.skyblue));
		if (map != null && this.isVisible())
			map.addPolyline(lineOptions);
		points.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				walkerReceiver);
		if (notificationWindow.isShowing())
			notificationWindow.dismiss();
		if (driverStatusWindow.isShowing())
			driverStatusWindow.dismiss();

		
	}

	/* added by amal */
	private String strAddress = null;

	private void getAddressFromLocation(final LatLng latlng,
			final MyFontTextView et) {

		/*
		 * et.setText("Waiting for Address"); et.setTextColor(Color.GRAY);
		 */
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 */

		Geocoder gCoder = new Geocoder(getActivity());
		try {
			final List<Address> list = gCoder.getFromLocation(latlng.latitude,
					latlng.longitude, 1);
			if (list != null && list.size() > 0) {
				Address address = list.get(0);
				StringBuilder sb = new StringBuilder();
				if (address.getAddressLine(0) != null) {

					sb.append(address.getAddressLine(0)).append(", ");
				}
				sb.append(address.getLocality()).append(", ");
				// sb.append(address.getPostalCode()).append(",");
				sb.append(address.getCountryName());
				strAddress = sb.toString();

				strAddress = strAddress.replace(",null", "");
				strAddress = strAddress.replace("null", "");
				strAddress = strAddress.replace("Unnamed", "");
				if (!TextUtils.isEmpty(strAddress)) {

					et.setText(strAddress);

				}
			}
			/*
			 * getActivity().runOnUiThread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub
			 * if (!TextUtils.isEmpty(strAddress)) {
			 * 
			 * et.setText(strAddress);
			 * 
			 * 
			 * } else { et.setText("");
			 * 
			 * }
			 * 
			 * } });
			 */

		} catch (IOException exc) {
			exc.printStackTrace();
		}
		// }
		// }).start();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		Log.d("xxx",
//				"in activity result of fragment    "
//						+ String.valueOf(requestCode));
		switch (requestCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = getActivity().getContentResolver().query(
						contactData, null, null, null, null);

				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						String id = c.getString(c
								.getColumnIndex(ContactsContract.Contacts._ID));
						String name = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						if (Integer
								.parseInt(c.getString(c
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
							System.out.println("name : " + name + ", ID : "
									+ id);

							// get the phone number
							Cursor pCur = getActivity()
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = ?",
											new String[] { id }, null);
							while (pCur.moveToNext()) {
								String phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								System.out.println("phone" + phone);
//								Log.d("xxx", phone);
							}
							pCur.close();
						}

						/*
						 * if (c.moveToFirst()) { String number =
						 * c.getString(c.getColumnIndex
						 * (ContactsContract.Contacts.DISPLAY_NAME));
						 * Toast.makeText(getActivity(), number,
						 * Toast.LENGTH_LONG).show(); Log.d("xxx", number);
						 */
						// TODO Fetch other Contact details as you want to use

					}
				}

			}
			break;

		case PICK_MULTIPLE_CONTACT:
			if (data != null) {
				Bundle bundle = data.getExtras();
				selectedContacts = bundle.getStringArrayList("sel_contacts");
//				Log.d("amal", "Selected contacts-->" + selectedContacts);
				if (selectedContacts.size() < 0) {
					Toast.makeText(activity, "No contacts selected",
							Toast.LENGTH_LONG).show();
				} else {

					String[] phones = new String[selectedContacts.size()];
					for (int i = 0; i < selectedContacts.size(); i++) {
						phones[i] = selectedContacts.get(i).replaceAll("\\s+",
								"");
					}
					for (int i = 0; i < phones.length; i++) {
						phonesarray.add(phones[i]);
					}
					String phonesingleton = null;
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < phonesarray.size(); i++) {
						sb.append(phonesarray.get(i));
						if (i != phonesarray.size() - 1)
							sb.append(",");
					}
					phonesingleton = sb.toString();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Const.URL,
							Const.ServiceType.SEND_ETA
									+ Const.Params.ID
									+ "="
									+ new PreferenceHelper(activity)
											.getUserId()
									+ "&"
									+ Const.Params.TOKEN
									+ "="
									+ new PreferenceHelper(activity)
											.getSessionToken()
									+ "&"
									+ Const.Params.REQUEST_ID
									+ "="
									+ String.valueOf(activity.pHelper
											.getRequestId()) + "&"
									+ Const.Params.PHONE + "=" + phonesingleton
									+ "&" + Const.Params.ETA + "=" + duration);
//					Log.d("amal", "map from eta  " + map.toString());
					new HttpRequester(activity, map,
							Const.ServiceCode.SEND_ETA, true, this);
				}
			}
			break;
		}

	}

	private void boundLatLang() {

		try {
			if (myMarker != null && markerDriver != null
					&& destinationmarker != null) {
				LatLngBounds.Builder bld = new LatLngBounds.Builder();
				bld.include(new LatLng(myMarker.getPosition().latitude,
						myMarker.getPosition().longitude));
				bld.include(new LatLng(markerDriver.getPosition().latitude,
						markerDriver.getPosition().longitude));
				bld.include(new LatLng(
						destinationmarker.getPosition().latitude,
						destinationmarker.getPosition().longitude));
				LatLngBounds latLngBounds = bld.build();

				map.animateCamera(CameraUpdateFactory.newLatLngBounds(
						latLngBounds, 18));
			} else if (myMarker != null && markerDriver != null) {
				LatLngBounds.Builder bld = new LatLngBounds.Builder();
				bld.include(new LatLng(myMarker.getPosition().latitude,
						myMarker.getPosition().longitude));
				bld.include(new LatLng(markerDriver.getPosition().latitude,
						markerDriver.getPosition().longitude));
				LatLngBounds latLngBounds = bld.build();

				map.animateCamera(CameraUpdateFactory.newLatLngBounds(
						latLngBounds, 18));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	private void gettime() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
						+ "=" + String.valueOf(myMarker.getPosition().latitude)
						+ ","
						+ String.valueOf(myMarker.getPosition().longitude)
						+ "&" + Const.Params.MAP_DESTINATIONS + "="
						+ driver.getD_latitude() + ","
						+ driver.getD_longitude());

//		Log.d("amal", "gettime " + map);
		new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_TIME, true,
				this);

	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		// TODO Auto-generated method stub
//		Log.d("mahi", ""+arg0);
		
	}

}
