package com.cabrunzltd.user.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.cabrunzltd.user.ActionBarBaseActivitiy;
import com.cabrunzltd.user.FriendPayActivity;
import com.cabrunzltd.user.MainDrawerActivity;
import com.cabrunzltd.user.R;
import com.cabrunzltd.user.UberAddPaymentActivity;
import com.cabrunzltd.user.UberViewPaymentActivity;
import com.cabrunzltd.user.adapter.PlacesAutoCompleteAdapter;
import com.cabrunzltd.user.adapter.VehicalTypeListAdapter;
import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.interfaces.OnProgressCancelListener;
import com.cabrunzltd.user.models.Cards;
import com.cabrunzltd.user.models.Driver;
import com.cabrunzltd.user.models.VehicalType;
import com.cabrunzltd.user.models.Walkerinfo;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationClient;

/**
 * @author Hardik A Bhalodi
 */


public class UberMapFragment extends UberBaseFragment implements
		OnProgressCancelListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	private PlacesAutoCompleteAdapter adapter;
	private AutoCompleteTextView etSource, enterdestination;

	public static boolean isMapTouched = false;
	private float currentZoom = -1;
	private BroadcastReceiver mReceiver, mReceiver2;

	private GoogleMap map;
	private GoogleApiClient client;
	private LocationRequest mLocationRequest;
	private LocationRequest locationRequest;
	private LatLng curretLatLng;
	private String strAddress = null;
	private boolean clicked;
	private boolean isContinueRequest;
	private Timer timer;
	private WalkerStatusReceiver walkerReceiver;
	private FavStoreStatusReceiver favStoreReceiver;

	private ImageButton btnMyLocation;

	private FrameLayout mapFrameLayout;

	private GridView listViewType;

	private ArrayList<VehicalType> listType;

	private ArrayList<Marker> drivermarkers;

	private VehicalTypeListAdapter typeAdapter;

	private int selectedPostion = -1;
	private boolean isGettingVehicalType = true;
	private boolean ispayaccept = false, ispayreject = false;

	private boolean isLocationFound;
	// private Animation topToBottomAnimation, bottomToTopAnimation,
	// buttonTopToBottomAnimation;

	private MyFontButton btnSelectService, btnconfirmservice, btnpayment,
			btnratecard, btnfareestimate, btnpromocard;
	private static MyFontButton bubble;
	private static SlidingDrawer drawer;
	private static LinearLayout markers;
	private static RelativeLayout pickuppop;
	private EditText etFriendEmail;
	private AQuery aQuery;
	private ImageOptions imageOptions;
	private static ImageButton btnadddestination;
	int payment_type = -1;// no payment selected
	private String payment_mode[] = {"By Card", "By Cash", "By PayPal"};
	private ArrayList<Walkerinfo> walkerlist;
	private TextView eta;
	private RelativeLayout destaddlayout;
	private ImageButton clearfield;
	private SharedPreferences promopref;
	SharedPreferences.Editor editorpromo;
	private String promoglobal;
	int paydebt_indicator = 0;
	int stored_card = 0;
	private Animation slidedown, slideup;
	public LatLng destlatlng_places, destlatlng;
	private TextView[] tvServiceTypes;
	private static MyFontButton btnPickMeUp;

	private String instruction;
	private int errorCode = 0;
	WakeLock wakeLock;

	private Marker mylocation, destinationmarker;

	// PopupWindow window;

	public static UberMapFragment newInstance() {
		UberMapFragment mapFragment = new UberMapFragment();
		return mapFragment;
	}

	TextView markerBubblePickMeUp;
	private LinearLayout linearLayout;
	private TextView rowTextView;
	private LinearLayout[] layouts;
	private View[] views;
	private LinearLayout lLayout;
	private View v;
	private boolean mapmarker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		imageOptions = new ImageOptions();
		imageOptions.fileCache = true;
		imageOptions.memCache = true;
		imageOptions.fallback = R.drawable.default_user;
		aQuery = new AQuery(activity);

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);

			isLocationFound = false;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			try {
				MapsInitializer.initialize(activity);
			} catch (Exception e) {

			}

			markerBubblePickMeUp = (TextView) view
					.findViewById(R.id.markerBubblePickMeUp);
			markerBubblePickMeUp.setOnClickListener(this);

			bubble = (MyFontButton) view.findViewById(R.id.markerBubblePickMeUp);
			selectedPostion = 0;/* modified by Amal *//* reverted back */

			listViewType = (GridView) view.findViewById(R.id.gvTypes);

			promopref = getActivity().getSharedPreferences("promocode",
					Context.MODE_PRIVATE);
			editorpromo = promopref.edit();

			markers = (LinearLayout) view.findViewById(R.id.layoutMarker);
			pickuppop = (RelativeLayout) view.findViewById(R.id.pickuppop);

			// llBottomLayout = (LinearLayout)
			// view.findViewById(R.id.llBottomLayout);
			if (new PreferenceHelper(activity).getUserId() == null) {
				login();
			}
			mapFrameLayout = (FrameLayout) view.findViewById(R.id.mapFrameLayout);

			mapFrameLayout.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE:
							UberMapFragment.isMapTouched = true;
							break;

						case MotionEvent.ACTION_UP:
							UberMapFragment.isMapTouched = false;
							break;
					}
					return true;

				}
			});
			btnMyLocation = (ImageButton) view.findViewById(R.id.btnMyLocation);
			btnSelectService = (MyFontButton) view
					.findViewById(R.id.btnSelectService);
			btnSelectService.setOnClickListener(this);

			btnconfirmservice = (MyFontButton) view
					.findViewById(R.id.btnconfirmservice);
			btnconfirmservice.setOnClickListener(this);
			btnpayment = (MyFontButton) view.findViewById(R.id.btnpayment);
			btnpayment.setOnClickListener(this);
			enterdestination = (AutoCompleteTextView) view
					.findViewById(R.id.EnterDestination);
			destaddlayout = (RelativeLayout) view
					.findViewById(R.id.destinationaddlayout);
			clearfield = (ImageButton) view.findViewById(R.id.clearfield);
			clearfield.setOnClickListener(this);

			btnfareestimate = (MyFontButton) view
					.findViewById(R.id.btnfareestimate);
			btnfareestimate.setOnClickListener(this);
			eta = (TextView) view.findViewById(R.id.eta);
			btnratecard = (MyFontButton) view.findViewById(R.id.btnratecard);
			btnratecard.setOnClickListener(this);
			btnpromocard = (MyFontButton) view.findViewById(R.id.btnpromocode);
			btnpromocard.setOnClickListener(this);
			slidedown = AnimationUtils.loadAnimation(getActivity(),
					R.anim.destaddtopbottom);
			slideup = AnimationUtils.loadAnimation(getActivity(),
					R.anim.destaddbottomtop);






//			 etSource = (AutoCompleteTextView)view.findViewById(R.id.etEnterSouce);
		drawer = (SlidingDrawer) view.findViewById(R.id.drawer);

			locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(10000);
			locationRequest.setFastestInterval(5000);
			client = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

			linearLayout = (LinearLayout) view.findViewById(R.id.tvServiceType);
			btnPickMeUp = (MyFontButton) view.findViewById(R.id.btnPickMeUp);
			btnPickMeUp.setOnClickListener(this);
			setUpMapIfNeeded();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PowerManager powerManager = (PowerManager) activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				Const.TAG);
		wakeLock.acquire();

		drivermarkers = new ArrayList<Marker>();
		try {
			btnadddestination = activity.btnadddestination;
			btnadddestination.setOnClickListener(this);
			btnadddestination.setVisibility(View.VISIBLE);
			// btnadddestination.setImageResource(R.drawable.)

			etSource = activity.etSource;
			activity.tvTitle.setVisibility(View.GONE);
			etSource.setVisibility(View.VISIBLE);
			createrequestforfriend();
			payreject();

		} catch (Exception e) {
			e.printStackTrace();
			etSource = ActionBarBaseActivitiy.etSource;
			etSource.setVisibility(View.VISIBLE);
		}
		IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
		IntentFilter favStoreFilter = new IntentFilter(
				Const.INTENT_FAVORITE_STORE_STATUS);

		walkerReceiver = new WalkerStatusReceiver();
		favStoreReceiver = new FavStoreStatusReceiver();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				walkerReceiver, filter);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				favStoreReceiver, favStoreFilter);

		walkerlist = new ArrayList<Walkerinfo>();
		walkerarrayformarker = new ArrayList<UberMapFragment.walkerinfo_marker>();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		adapter = new PlacesAutoCompleteAdapter(activity,
				R.layout.autocomplete_list_text);
		etSource.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		enterdestination.setAdapter(adapter);
		enterdestination.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
				adapter.notifyDataSetChanged();
				if (enterdestination.getText().length() != 0) {
					destlatlng = getLocationFromAddress(enterdestination
							.getText().toString());
					setdestinationmarker(destlatlng);
				}

			}
		});

		// PopUp Window
		// LayoutInflater inflate = LayoutInflater.from(activity);
		// LinearLayout ll=(LinearLayout) inflate.inflate(R.layout.popup_window,
		// null);

		// window = new
		// PopupWindow(ll,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// window.showAsDropDown(activity.btnNotification);

		etSource.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
				final String selectedDestPlace = adapter.getItem(arg2);

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final LatLng latlng = getLocationFromAddress(selectedDestPlace);
						if (latlng != null) {

							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									isMapTouched = true;
									curretLatLng = latlng;
									// removemarkers();

									animateCameraToMarker(latlng);
									removemarkers();
									getallproviders();

								}
							});

						}

					}
				}).start();

			}
		});
		listType = new ArrayList<VehicalType>();
		typeAdapter = new VehicalTypeListAdapter(activity, listType);
		listViewType.setAdapter(typeAdapter);
		getVehicalTypes();
		// drawer.lock();
		listViewType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				for (int i = 0; i < listType.size(); i++)
					listType.get(i).isSelected = false;
				listType.get(position).isSelected = true;
				// btnSelectService.setCompoundDrawables(new , top, right,
				// bottom)
				// onItemClick(position);
				selectedPostion = position;

				removemarkers();
				getallproviders();

				typeAdapter.notifyDataSetChanged();
				/* added by amal */
				if (drawer.isOpened()) {
					drawer.animateClose();
					drawer.unlock();
				}

			}
		});

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
		activity.currentFragment = Const.FRAGMENT_MAP;
		/*new PreferenceHelper(activity)
		.putCurrentFragment(Const.FRAGMENT_MAP);*/

		activity.tvTitle.setVisibility(View.GONE);
		activity.btnNotification.setVisibility(View.INVISIBLE);
		etSource.setVisibility(View.VISIBLE);
		startCheckingStatusUpdate();

	}

	private void payreject() {
		// TODO Auto-generated method stub
		ispayreject = true;
		IntentFilter intentFilter = new IntentFilter("PAY_REJECT");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action != null && action.equals("PAY_REJECT")) {

					new AlertDialog.Builder(context)
							.setTitle(null)
							.setMessage(
									"Your friend doesnt accepted the payment so it cancelled !! Please Try with another Friend !")

							.setNegativeButton("CLOSE",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// do nothing

										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();

				}
			}
		};
		activity.registerReceiver(mReceiver, intentFilter);

	}

	private void createrequestforfriend() {
		// TODO Auto-generated method stub
		ispayaccept = true;
		IntentFilter intentFilter = new IntentFilter("PAY_ACCEPT");
		mReceiver2 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action != null && action.equals("PAY_ACCEPT")) {

					showfriendrequest();

				}
			}
		};

		activity.registerReceiver(mReceiver2, intentFilter);

	}

	protected void showfriendrequest() {
		// TODO Auto-generated method stub


		final Dialog mDialog = new Dialog(activity,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		/*
		 * mDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.WHITE));
		 */
		mDialog.setContentView(R.layout.pickup_friend);

		Button btnrequest = (Button) mDialog
				.findViewById(R.id.btnrequest);

		btnrequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();

				pickmeupfrnd();


			}

		});

		mDialog.setCancelable(false);
		mDialog.show();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
		startCheckingStatusUpdate();

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		setUpMap();

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
		map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

			public void onCameraChange(CameraPosition camPos) {
				// TODO Auto-generated method stub

//				Log.e("MSG", "map " + camPos.toString());
				if (currentZoom == -1) {
					currentZoom = camPos.zoom;
				} else if (camPos.zoom != currentZoom) {
					currentZoom = camPos.zoom;
					return;
				}

				if (!isMapTouched) {
					curretLatLng = camPos.target;
//					 removemarkers();
//					getallproviders();
//					 if (pickuppop.getVisibility() == View.VISIBLE)
//					 gettime();
					getAddressFromLocation(camPos.target, etSource);

				}
				isMapTouched = false;
				// setMarker(camPos.target);

			}
		});

		btnMyLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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
					Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
//					Log.e("MSG", "map " + loc.toString());
					if (loc != null) {
						LatLng latLang = new LatLng(loc.getLatitude(), loc
								.getLongitude());

						animateCameraToMarker(latLang);
						removemarkers();
						getallproviders();
					} else {
						activity.showLocationOffDialog();
					}

				}
			});





	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
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
		mLocationRequest.setInterval(500);
		mLocationRequest.setFastestInterval(500);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
		LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);

//		if(loc == null){
//			LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, activity);
//		}

//				if (loc != null) {
//
//					LatLng latLang = new LatLng(loc.getLatitude(),
//							loc.getLongitude());
//					animateCameraToMarker(latLang);
//
//				} else {
//					activity.showLocationOffDialog();
//				}


		if (loc != null) {
			if (map != null) {
				if (mylocation == null) {

					LatLng latLang = new LatLng(loc.getLatitude(),
							loc.getLongitude());

					mylocation = map.addMarker(new MarkerOptions()
								.position(
										new LatLng(loc.getLatitude(), loc
												.getLongitude()))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.img_usermappin)));
					animateCameraToMarker(latLang);
					getAddressFromLocation(latLang, etSource);

//					map.animateCamera(CameraUpdateFactory.newLatLngZoom(
//							new LatLng(loc.getLatitude(),
//									loc.getLongitude()),
//							12));

				} else {
					mylocation.setPosition(new LatLng(loc.getLatitude(),
									loc.getLongitude()));

				}
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
//		if (mylocation == null) {
//						mylocation = map.addMarker(new MarkerOptions()
//								.position(
//										new LatLng(loc.getLatitude(), loc
//												.getLongitude()))
//								.icon(BitmapDescriptorFactory
//										.fromResource(R.drawable.img_usermappin)));

		mylocation.setPosition(new LatLng(loc
				.getLatitude(), loc.getLongitude()));

//					}
//					if (!mylocation.isVisible()) {
//						mylocation = null;
//					}
	}


	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
			mapFrag.getMapAsync(this);
		}


	}

//	private void setUpMapIfNeeded() {
//		// Do a null check to confirm that we have not already instantiated the
//		// map.
//		if (map == null) {
//			map = ((SupportMapFragment) activity.getSupportFragmentManager()
//					.findFragmentById(R.id.map)).getMap();
//			map.setMyLocationEnabled(true);
//
//			map.getUiSettings().setMyLocationButtonEnabled(false);
//			map.getUiSettings().setZoomControlsEnabled(false);
//
//			map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//				@Override
//				public void onMyLocationChange(Location loc) {
//					// TODO Auto-generated method stub
//					if (mylocation == null) {
//						mylocation = map.addMarker(new MarkerOptions()
//								.position(
//										new LatLng(loc.getLatitude(), loc
//												.getLongitude()))
//								.icon(BitmapDescriptorFactory
//										.fromResource(R.drawable.img_usermappin)));
//
//					}
//					if (!mylocation.isVisible()) {
//						mylocation = null;
//					}
//				}
//			});
//
//			btnMyLocation.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Location loc = client.getLastLocation();
//					if (loc != null) {
//						LatLng latLang = new LatLng(loc.getLatitude(), loc
//								.getLongitude());
//
//						animateCameraToMarker(latLang);
//						getallproviders();
//					} else {
//						activity.showLocationOffDialog();
//					}
//
//				}
//			});
//
//			map.setOnCameraChangeListener(new OnCameraChangeListener() {
//
//				public void onCameraChange(CameraPosition camPos) {
//					// TODO Auto-generated method stub
//					if (currentZoom == -1) {
//						currentZoom = camPos.zoom;
//					} else if (camPos.zoom != currentZoom) {
//						currentZoom = camPos.zoom;
//						return;
//					}
//
//					if (!isMapTouched) {
//						curretLatLng = camPos.target;
//						// removemarkers();
//						getallproviders();
//						// if (pickuppop.getVisibility() == View.VISIBLE)
//						// gettime();
//						getAddressFromLocation(camPos.target, etSource);
//
//					}
//					isMapTouched = false;
//					// setMarker(camPos.target);
//
//				}
//			});
//
//		}
//
//		client = new LocationClient(activity, new GoogleApiClient.ConnectionCallbacks() {
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
//
//					LatLng latLang = new LatLng(loc.getLatitude(),
//							loc.getLongitude());
//					animateCameraToMarker(latLang);
//
//				} else {
//					activity.showLocationOffDialog();
//				}
//
//			}
//		}, new GoogleApiClient.OnConnectionFailedListener() {
//
//			@Override
//			public void onConnectionFailed(ConnectionResult result) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		client.connect();
//	}

	private void pickmeupfrnd() {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CREATE_REQUEST);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(activity).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
		map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
		map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
		map.put(Const.Params.S_ADDRESS, etSource.getText().toString());
		map.put(Const.Params.TYPE,
				String.valueOf(listType.get(selectedPostion).getId()));
		map.put(Const.Params.INSTRUCTION, instruction);
		if (enterdestination.getVisibility() == View.VISIBLE
				&& !TextUtils.isEmpty(enterdestination.getText().toString())) {
			LatLng destlatlng = getLocationFromAddress(enterdestination
					.getText().toString());
			map.put(Const.Params.D_ADDRESS, enterdestination.getText()
					.toString());
			if (destlatlng != null) {
				map.put(Const.Params.DEST_LATITUDE,
						String.valueOf(destlatlng.latitude));
				map.put(Const.Params.DEST_LONGITUDE,
						String.valueOf(destlatlng.longitude));
			} else {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Please Enter The Address Correctly",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		if (promopref.getString("promocode", "") != "") {
			map.put(Const.Params.PROMO_CODE,
					promopref.getString("promocode", ""));
		}
		map.put(Const.Params.COD, String.valueOf(3));
		map.put(Const.Params.DISTANCE, "1");
//		Log.e("MSG", "map " + map.toString());
		new HttpRequester(activity, map, Const.ServiceCode.CREATE_REQUEST, this);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		stopCheckingStatusUpdate();

		super.onPause();

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		startCheckingStatusUpdate();
		client.connect();
		super.onStart();
	}

	@Override
	public void onDestroyView() {
		wakeLock.release();
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
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

	@Override
	public void onDestroy() {

		client.disconnect();
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				walkerReceiver);
		if (ispayreject) {
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(mReceiver);
			ispayreject = false;
		} else if (ispayaccept) {
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(mReceiver2);
			ispayaccept = false;
		}

		activity.tvTitle.setVisibility(View.VISIBLE);
		etSource.setVisibility(View.GONE);

	}

	public static void setmarkerVisibile() {
		// markers.setVisibility(View.VISIBLE);
		bubble.setVisibility(View.VISIBLE);

		drawer.setVisibility(View.GONE);
	}

	public static void setmarkerInvisibile() {
		// markers.setVisibility(View.INVISIBLE);
		bubble.setVisibility(View.INVISIBLE);

		drawer.setVisibility(View.INVISIBLE);

		pickuppop.setVisibility(View.INVISIBLE);

	}

	public static void setpickpopupVisible() {
		MainDrawerActivity.popon = true;
		btnPickMeUp.setVisibility(View.GONE);
		pickuppop.setVisibility(View.VISIBLE);
		btnadddestination.setVisibility(View.VISIBLE);
		drawer.setVisibility(View.GONE);
	}

	public static void setpickpopupInvisible() {
		MainDrawerActivity.popon = false;
		btnPickMeUp.setVisibility(View.VISIBLE);
		pickuppop.setVisibility(View.GONE);
		btnadddestination.setVisibility(View.GONE);
		drawer.setVisibility(View.INVISIBLE);
	}

	private void setdestinationmarker(LatLng latlng) {
		if (latlng != null) {
			if (map != null && this.isVisible()) {

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.markerBubblePickMeUp:

			if (isValidate()) {
				setmarkerInvisibile();
				setpickpopupVisible();
				gettime();

				try {
					btnconfirmservice.setText("Request "
							+ listType.get(selectedPostion).getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// getCards(); modified by amal
			/*
			 * if (isValidate()) { pickMeUp(); //modified by amal }
			 */

			break;
		case R.id.btnSelectService:
			if (drawer.isOpened()) {
				drawer.animateClose();
				drawer.unlock();
			} else {
				drawer.animateOpen();
				drawer.lock();
			}
			break;

		case R.id.btnconfirmservice:

//			Log.d("amal", Integer.toString(payment_type));
			btnconfirmservice.setEnabled(false);
			btnconfirmservice.postDelayed(new Runnable() {
				@Override
				public void run() {
					btnconfirmservice.setEnabled(true);
				}
			}, 3000);

			if (payment_type == -1) {
				new AlertDialog.Builder(getActivity())
						.setTitle("No Payment option selected")
						.setMessage(
								"Please select any given payment option to request a ride")
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
			if (payment_type == 0)
				getCards();
			if (payment_type == 1 || payment_type == 2) {
//				Log.d("amal", "going to pick up");
				showAddInstructionsDialog();
			}
			break;
		case R.id.btnpayment:
			selectPayment();
			break;
		case R.id.btnAdddestination:

			// destaddlayout.setVisibility(View.VISIBLE);
			// destaddlayout.startAnimation(slidedown);

			// call fav api

			// AddToFav();

			if (!TextUtils.isEmpty(etSource.getText().toString())) {
				Favname();
			} else {
				Toast.makeText(activity, "Waiting for address",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btnfareestimate:
			if (enterdestination.getVisibility() == View.VISIBLE
					&& !TextUtils
							.isEmpty(enterdestination.getText().toString())) {
				getDistance();
			} else if (destaddlayout.getVisibility() == View.VISIBLE
					&& TextUtils.isEmpty(enterdestination.getText().toString())) {
				Toast.makeText(getActivity(), "Enter Destination",
						Toast.LENGTH_LONG).show();

			} else {
				destaddlayout.setVisibility(View.VISIBLE);
				destaddlayout.startAnimation(slidedown);
				Toast.makeText(getActivity(), "Enter Destination",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.btnratecard:
			// showpopup();
			/*
			 * final Dialog mDialog = new Dialog(getActivity(),
			 * R.style.MyFareestimateDialog);
			 *
			 * // mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 * mDialog.getWindow().setBackgroundDrawable( new
			 * ColorDrawable(android.graphics.Color.TRANSPARENT));
			 * mDialog.setContentView(R.layout.ratecard);
			 * mDialog.setTitle("Rate Card");
			 */

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflate = getActivity().getLayoutInflater();
			View contentview = inflate.inflate(R.layout.ratecard, null);
			View titleview = inflate
					.inflate(R.layout.ratecardcustomtitle, null);
			builder.setView(contentview).setCustomTitle(titleview);
			final AlertDialog mDialog = builder.create();

			MyFontTextView ratecardtitle;
			TextView baseprice,
			distanceprice,
			timeprice;
			baseprice = (TextView) contentview
					.findViewById(R.id.Basepricefield);
			distanceprice = (TextView) contentview
					.findViewById(R.id.Distancepricefield);
			timeprice = (TextView) contentview
					.findViewById(R.id.Timepricefield);
			ratecardtitle = (MyFontTextView) titleview
					.findViewById(R.id.ratecardtitle);

			baseprice.setText(listType.get(selectedPostion).getCurrency() + " "
					+ listType.get(selectedPostion).getBasePrice());
			distanceprice.setText(listType.get(selectedPostion).getCurrency()
					+ " "
					+ listType.get(selectedPostion).getPricePerUnitDistance()
					+ "/" + listType.get(selectedPostion).getUnit());
			timeprice.setText(listType.get(selectedPostion).getCurrency() + " "
					+ listType.get(selectedPostion).getPricePerUnitTime()
					+ "/min");
			ratecardtitle.setText(listType.get(selectedPostion).getName());
			mDialog.show();
//			Log.d("amal", Integer.toString(selectedPostion));
			break;
		case R.id.btnpromocode:
			ApplyPromo();
			break;

		case R.id.clearfield:
			if (TextUtils.isEmpty(enterdestination.getText().toString())) {

				destaddlayout.startAnimation(slideup);
				destaddlayout.setVisibility(View.GONE);

			}
			if (!TextUtils.isEmpty(enterdestination.getText().toString()))
				enterdestination.setText("");

			break;
		default:
			break;

		case R.id.btnPickMeUp:

			// if (payment_type == 0)
			// getCards();
			// if (payment_type == 1 || payment_type == 2) {
			// Log.d("amal", "going to pick up");
			// pickMeUp();
			// }
			if (enterdestination.getText().length() != 0) {
				setpickpopupVisible();

			} else {
				Toast.makeText(activity, "Enter Your Destination", Toast.LENGTH_LONG).show();
			}
			// selectPayment();
			break;

		}
	}

	private void showAddInstructionsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Add Instruction");
		final EditText etInstruction = new EditText(activity);
		etInstruction.setHint("Add instruction here");
		builder.setView(etInstruction);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				instruction = etInstruction.getText().toString();
				pickMeUp();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						pickMeUp();
					}
				});
		builder.show();

	}

	@SuppressWarnings("deprecation")
	private void showOptions() {
		// TODO Auto-generated method stub
//		Log.d("pavan", "in show option");
		listViewType.setVisibility(View.GONE);
		drawer.setVisibility(View.GONE);

	}

	private void getAddressFromLocation(final LatLng latlng, final EditText et) {

		et.setText("Waiting for Address");
		et.setTextColor(Color.GRAY);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Geocoder gCoder = new Geocoder(getActivity());
				try {
					final List<Address> list = gCoder.getFromLocation(
							latlng.latitude, latlng.longitude, 1);
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
					}
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (!TextUtils.isEmpty(strAddress)) {
								et.setFocusable(false);
								et.setFocusableInTouchMode(false);
								et.setText(strAddress);
								et.setTextColor(getResources().getColor(
										android.R.color.white));
								et.setFocusable(true);
								et.setFocusableInTouchMode(true);

							} else {
								et.setText("");
								et.setTextColor(getResources().getColor(
										android.R.color.white));
							}
							etSource.setEnabled(true);
						}
					});

				} catch (IOException exc) {
					exc.printStackTrace();
					getAddressFromGooleApi(latlng);
				}
			}

		}).start();

	}

	private void animateCameraToMarker(LatLng latLng) {
		try {
			etSource.setFocusable(false);
			etSource.setFocusableInTouchMode(false);
			CameraUpdate cameraUpdate = null;

			cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
			map.animateCamera(cameraUpdate);
			etSource.setFocusable(true);
			etSource.setFocusableInTouchMode(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LatLng getLocationFromAddress(final String place) {
//		Log.d("amal", place);
		LatLng loc = null;
		Geocoder gCoder = new Geocoder(getActivity());
		try {
			final List<Address> list = gCoder.getFromLocationName(place, 1);

			// TODO Auto-generated method stub
			if (list != null && list.size() > 0) {

				loc = new LatLng(list.get(0).getLatitude(), list.get(0)
						.getLongitude());

			}

		} catch (IOException e) {
			getlocfromaddressfromGoogleApi(place);
			if (destlatlng_places != null)
				loc = destlatlng_places;
			e.printStackTrace();
		}

		return loc;
	}

	@Override
	public void onProgressCancel() {
		stopCheckingStatusUpdate();
		cancleRequest();

		// stopCheckingStatusUpdate();
	}

	private void getAddressFromGooleApi(LatLng latlong) {

		// call google api here
		AndyUtils.removeCustomProgressDialog();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_LOCATION + latlong.latitude
				+ "," + latlong.longitude);
//		AppLog.Log("pavan", Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_ADDRESS, true,
				this);
	}

	private void getlocfromaddressfromGoogleApi(String address) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_ADDRESS + address);
//		AppLog.Log("pavan", Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_LOCATION, true,
				this);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.uberorg.fragments.BaseFragment#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		String msg = null;
		if (curretLatLng == null) {
			msg = getString(R.string.text_location_not_found);
		} else if (selectedPostion == -1) {
			msg = getString(R.string.text_select_type);
		} else if (TextUtils.isEmpty(etSource.getText().toString())
				|| etSource.getText().toString()
						.equalsIgnoreCase("Waiting for Address")) {
			msg = getString(R.string.text_waiting_for_address);
		}
		if (msg == null)
			return true;
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		return false;
	}

	private void paydebt() {
//		Log.d("amal", "in here");
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.PAY_DEBT);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(activity).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
		new HttpRequester(activity, map, Const.ServiceCode.PAY_DEBT, this);

	}

	private void pickMeUp() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CREATE_REQUEST);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(activity).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
		map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
		map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
		map.put(Const.Params.S_ADDRESS, etSource.getText().toString());
		map.put(Const.Params.TYPE,
				String.valueOf(listType.get(selectedPostion).getId()));
		map.put(Const.Params.INSTRUCTION, instruction);
		if (enterdestination.getVisibility() == View.VISIBLE
				&& !TextUtils.isEmpty(enterdestination.getText().toString())) {
			LatLng destlatlng = getLocationFromAddress(enterdestination
					.getText().toString());
			map.put(Const.Params.D_ADDRESS, enterdestination.getText()
					.toString());
			if (destlatlng != null) {
				map.put(Const.Params.DEST_LATITUDE,
						String.valueOf(destlatlng.latitude));
				map.put(Const.Params.DEST_LONGITUDE,
						String.valueOf(destlatlng.longitude));
			} else {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Please Enter The Address Correctly",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		if (promopref.getString("promocode", "") != "") {
			map.put(Const.Params.PROMO_CODE,
					promopref.getString("promocode", ""));
		}
		map.put(Const.Params.COD, String.valueOf(payment_type));
		map.put(Const.Params.DISTANCE, "1");
//		Log.e("MSG", "map " + map.toString());
		new HttpRequester(activity, map, Const.ServiceCode.CREATE_REQUEST, this);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.uberorg.fragments.BaseFragment#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		super.onTaskCompleted(response, serviceCode);

		String distance = "";
		String duration = "";
		String errormsg2 = "";
		String errormsg = "";
		// AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.CREATE_REQUEST:
//			Log.d("MSG", "request response " + "" + response);

			if (activity.pContent.isSuccess(response)) {
				//AndyUtils.removeCustomProgressDialog();
				editorpromo.putString("promocode", "");
				editorpromo.commit();
				AndyUtils.removeCustomProgressDialog();
				activity.pHelper.putRequestId(activity.pContent
						.getRequestId(response));
				try {
					AndyUtils.showCustomProgressDialog(activity,
							getString(R.string.text_contacting), false, this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startCheckingStatusUpdate();
			} else {
				AndyUtils.removeCustomProgressDialog();
				// if (activity.pContent.getErrorCode(response) == 417) {

				int errorCode = 0;
				// 09-29 13:26:58.906: I/UBER FOR X(7924):
				// {"success":false,"error":"Already request is Pending"}

				try {
					JSONObject obj = new JSONObject("" + response);
					if (obj.has("error")) {


						errormsg = obj.getString("error");
					}
					errorCode = obj.getInt("error_code");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (errorCode == 417) {
					AndyUtils.removeCustomProgressDialog();
					AlertDialog.Builder debtalert = new AlertDialog.Builder(
							activity);
					debtalert.setTitle("Could not request ride").setMessage(
							errormsg);

					debtalert.setPositiveButton("Pay",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									paydebt_indicator = 1;
									getCards();

								}

							});

					debtalert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							});
					debtalert.show();

				} else if (errorCode == 425) {
					AndyUtils.removeCustomProgressDialog();
					AlertDialog.Builder debtalert = new AlertDialog.Builder(
							activity);
					debtalert.setTitle("Could not request ride").setMessage(
							errormsg2);

					debtalert.setNegativeButton("CLOSE",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									AndyUtils.removeCustomProgressDialog();

								}
							});
					debtalert.show();
				}

			else if (errorCode == 406) {
				AndyUtils.removeCustomProgressDialog();
				AlertDialog.Builder debtalert = new AlertDialog.Builder(
						activity);
				debtalert.setTitle(null).setMessage(
						errormsg);

				debtalert.setNegativeButton("CLOSE",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								AndyUtils.removeCustomProgressDialog();
								cancleRequest();


							}
						});
				debtalert.show();
			}
		}
			// }
			break;
		case Const.ServiceCode.ASK_FRIEND:
			Toast.makeText(
					activity,
					"Please Wait for some time ! we are requesting your friend !",
					Toast.LENGTH_LONG).show();
//			Log.d("mahi", response);
			int errorcode = 0;
			String errormsg3 = " ";
			if (activity.pContent.isSuccess(response)) {
				AndyUtils.removeCustomProgressDialog();

			} else

				// 09-29 13:26:58.906: I/UBER FOR X(7924):
				// {"success":false,"error":"Already request is Pending"}

				try {
					JSONObject obj = new JSONObject("" + response);
					errormsg3 = obj.getString("error");
					if (obj.has("error")) {
						errormsg = obj.getString("error");
					}
					errorcode = obj.getInt("error_code");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			if (errorcode == 403) {
				AndyUtils.removeCustomProgressDialog();
				AlertDialog.Builder debtalert = new AlertDialog.Builder(
						activity);
				debtalert.setTitle(null).setMessage(
						errormsg3);

				debtalert.setNegativeButton("CLOSE",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								AndyUtils.removeCustomProgressDialog();

							}
						});
				if (stored_card == 1) {
					debtalert.setPositiveButton(null, null);
				} else {
					debtalert.setPositiveButton("ADD CARD",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									AndyUtils.removeCustomProgressDialog();
									Intent i = new Intent(getActivity(),
											UberAddPaymentActivity.class);
									startActivity(i);

								}
							});
				}

				debtalert.show();

			}

			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:

			if (activity.pContent.isSuccess(response)) {
				switch (activity.pContent.checkRequestStatus(response)) {
				case Const.IS_WALK_STARTED:
				case Const.IS_WALKER_ARRIVED:
				case Const.IS_COMPLETED:
				case Const.IS_WALKER_STARTED:
					AndyUtils.removeCustomProgressDialog();
					stopCheckingStatusUpdate();
					Driver driver = activity.pContent.getDriverDetail(""
							+ response);
					if (btnadddestination.getVisibility() == View.VISIBLE) {
						btnadddestination.setVisibility(View.GONE);
					}
					// activity.gotoTripFragment(driver);
					break;
				case Const.IS_WALKER_RATED:
					stopCheckingStatusUpdate();
					activity.gotoRateFragment(activity.pContent
							.getDriverDetail(response));
					break;

				// case Const.IS_WALKER_REACHED:
				// stopCheckingStatusUpdate();
				// activity.gotoRateFragment(activity.pContent
				// .getDriverDetail(response));
				// break;

				case Const.IS_REQEUST_CREATED:
					if (activity.pHelper.getRequestId() != Const.NO_REQUEST)
						/*AndyUtils.showCustomProgressDialog(activity,
								getString(R.string.text_contacting), false,
								this);*/
					isContinueRequest = true;
					break;
				case Const.NO_REQUEST:
					if (!isGettingVehicalType) {
						AndyUtils.removeCustomProgressDialog();
					}
					stopCheckingStatusUpdate();
					break;
				default:
					isContinueRequest = false;
					break;
				}
//				Log.d("mahi", "response" + "" + response);
			} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				AndyUtils.removeCustomProgressDialog();
				activity.pHelper.clearRequestData();
				isContinueRequest = false;
			} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_CANCEL) {
				AndyUtils.removeCustomProgressDialog();
				activity.pHelper.clearRequestData();
				isContinueRequest = false;
			}

			else if (activity.pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				if (activity.pHelper.getLoginBy()
						.equalsIgnoreCase(Const.MANUAL))
					login();
				else
					loginSocial(activity.pHelper.getUserId(),
							activity.pHelper.getLoginBy());
			}

			else {
				isContinueRequest = true;
			}
			break;
		case Const.ServiceCode.LOGIN:
			if (activity.pContent.isSuccessWithStoreId(response)) {

			}
			break;
		case Const.ServiceCode.CANCEL_REQUEST:

//			Log.d("mahi", "response in cancel request" + "" + response);

			if (activity.pContent.isSuccess(response)) {

			}
			activity.pHelper.clearRequestData();
			AndyUtils.removeCustomProgressDialog();
			break;
		case Const.ServiceCode.GET_VEHICAL_TYPES:

			if (activity.pContent.isSuccess(response)) {
				AndyUtils.removeCustomProgressDialog();
				listType.clear();
				activity.pContent.parseTypes(response, listType);
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

			break;
		/* added by amal */
		case Const.ServiceCode.GET_CARDS:

//			Log.d("amal", "GET CARD" + "" + response);

			if (new ParseContent(getActivity()).isSuccess(response)) {

				AndyUtils.removeCustomProgressDialog();
				ArrayList<Cards> listCards;
				listCards = new ArrayList<Cards>();
				listCards.clear();
				new ParseContent(getActivity()).parseCards(response, listCards);
				if (listCards.size() > 0) {

					if (paydebt_indicator == 1) {
						paydebt();
						paydebt_indicator = 0;
					} else
						pickMeUp();

				}
				adapter.notifyDataSetChanged();
			} else {
//				Log.d("yyy", "in else of 0 card");
				startActivity(new Intent(getActivity(),
						UberViewPaymentActivity.class));

			}

			break;

		case Const.ServiceCode.PAY_DEBT:
//			Log.d("amal", "pay debt" + "" + response);
			if (activity.pContent.isSuccess(response)) {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Debt cleared successfully",
						Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(activity, "Debt not cleared successfully",
						Toast.LENGTH_LONG).show();

			break;
		case Const.ServiceCode.PAYMENT_OPTIONS:
//			Log.e("amal", "" + "" + response);
			if (activity.pContent.isSuccess(response)) {
				AndyUtils.removeCustomProgressDialog();
				List<String> paymentoption = new ArrayList<String>();
				paymentoption.clear();
				// String[] paymentoptions=new String[2];
				int inter = 0;
				try {
					JSONObject jsonobject = new JSONObject("" + response);
					JSONObject obj = jsonobject
							.getJSONObject("payment_options");
					if (obj.has("stored_cards")) {
						stored_card = obj.getInt("stored_cards");
					}
					if (obj.getInt("stored_cards") == 1) {
						paymentoption.add("By Card");

						/*
						 * paymentoptions[inter]="By Card"; inter++;
						 */
					}
					if (obj.getInt("cod") == 1) {
						paymentoption.add("By Cash");

						/*
						 * paymentoptions[inter]="By Cash"; inter++;
						 */
					}
					if (obj.getInt("paypal") == 1) {
						paymentoption.add("By PayPal");
						/*
						 * paymentoptions[inter]="By PayPal"; inter++;
						 */
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				showPaymentDailog(paymentoption);

				// Dialog dialog = new Dialog(activity);
				// dialog.setContentView(R.layout.bill_layout);

				// final CharSequence[] paymentoptions = paymentoption
				// .toArray(new CharSequence[paymentoption.size()]);
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// getActivity());
				// builder.setTitle("Pay");
				//
				// builder.setItems(paymentoptions,
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface optiondialog,
				// int which) {
				// if (paymentoptions[which].toString().equals(
				// payment_mode[0])) {
				//
				// payment_type = 0;
				// btnpayment.setText("Pay by Card");
				// } else if (paymentoptions[which].toString()
				// .equals(payment_mode[1])) {
				//
				// payment_type = 1;
				// btnpayment.setText("Pay by Cash");
				//
				// } else if (paymentoptions[which].toString()
				// .equals(payment_mode[2])) {
				//
				// payment_type = 2;
				// btnpayment.setText("Pay by PayPal");
				// }
				//
				// }
				// });
				// builder.setNegativeButton("Cancel",
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface arg0, int arg1) {
				// arg0.dismiss();
				// }
				// });
				// AlertDialog alert = builder.create();
				// alert.setCancelable(true);
				// alert.show();

			}

			break;
		case Const.ServiceCode.GET_MAP_DETAILS:
			AndyUtils.removeCustomProgressDialog();
//			Log.d("amal", "in distance success" + "" + response);
			if (response != null) {

				try {
					JSONObject jObject = new JSONObject("" + response);

					if (jObject.getString("status").equals("OK")) {

						JSONArray jaArray = jObject.getJSONArray("rows");

						for (int i = 0; i < jaArray.length(); i++) {

							JSONObject jobj = jaArray.getJSONObject(i);

							JSONArray jaArray2 = jobj.getJSONArray("elements");

							for (int j = 0; i < jaArray2.length(); j++) {

								JSONObject jobj1 = jaArray2.getJSONObject(j);

								JSONObject jobj_distance = jobj1
										.getJSONObject("distance");
//								Log.d("amal",
//										"distance "
//												+ jobj_distance
//														.getString("text")
//												+ " , "
//												+ jobj_distance
//														.getString("value"));

								JSONObject jobj_duration = jobj1
										.getJSONObject("duration");
//								Log.d("amal",
//										"distance "
//												+ jobj_duration
//														.getString("text")
//												+ " , "
//												+ jobj_duration
//														.getString("value"));

								duration = jobj_duration.getString("value");
								distance = jobj_distance.getString("value");
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
		case Const.ServiceCode.GET_MAP_TIME:
			if (response != null) {

				try {
					JSONObject jObject = new JSONObject("" + response);

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
								eta.setText("Pick up time is approximately "
										+ duration);

							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.FARE_CALCULATOR:

//			Log.d("amal", "in farecalc success" + "" + response);
			if (response != null) {
				try {
					JSONObject jObject = new JSONObject("" + response);
					if (jObject.getString("success").equals("true")) {
						AndyUtils.removeCustomProgressDialog();

						ShowFare(jObject.getString("estimated_fare"),
								jObject.getString("currency"));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;

		case Const.ServiceCode.GETPROVIDER_ALL:
//			Log.d("amal", "" + response);
			if (activity.pContent.isSuccess(response)) {
				walkerlist.clear();
				try {
					JSONObject jsonobject = new JSONObject("" + response);
					JSONArray jsonarr = jsonobject.getJSONArray("walkers");
					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject obj = jsonarr.getJSONObject(i);
						Walkerinfo walkerinfo = new Walkerinfo();
						walkerinfo.setId(obj.getString("id"));
						walkerinfo.setLatitude(obj.getString("latitude"));
						walkerinfo.setLongitude(obj.getString("longitude"));
						walkerinfo.setTime_cost(obj.getString("time_cost"));
						walkerinfo.setDistance_cost(obj
								.getString("distance_cost"));
						walkerinfo.setType(obj.getString("type"));
						walkerinfo.setDistance(obj.getString("distance"));
						walkerinfo.setBase_price(obj.getString("base_price"));
//						Log.d("amal", obj.getString("base_price"));
						walkerlist.add(walkerinfo);

						int flag = 0;
						if (walkerarrayformarker.size() == 0) {
							Marker m = map
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(
															Double.parseDouble(walkerinfo
																	.getLatitude()),
															Double.parseDouble(walkerinfo
																	.getLongitude())))
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.img_taxi_pin)));
							walkerinfo_marker mwalkerinfo_marker = new walkerinfo_marker(
									walkerinfo, m);
							walkerarrayformarker.add(mwalkerinfo_marker);
						}

						for (int k = 0; k < walkerarrayformarker.size(); k++) {
//							Log.d("hey", "going to for loop");
//
//							Log.d("hey",
//									String.valueOf(walkerarrayformarker.get(k)
//											.getWalkerlistclone().getId()));
//							Log.d("hey", String.valueOf(walkerinfo.getId()));
							if (TextUtils.equals(
									String.valueOf(walkerarrayformarker.get(k)
											.getWalkerlistclone().getId()),
									String.valueOf(walkerinfo.getId()))) {
								flag++;
								if (walkerarrayformarker.get(k)
										.getWalkerlistclone().getLatitude() != walkerinfo
										.getLatitude()
										|| walkerarrayformarker.get(k)
												.getWalkerlistclone()
												.getLongitude() != walkerinfo
												.getLongitude()) {

									walkerarrayformarker.get(k)
											.getDrivermarkers().remove();
									walkerarrayformarker.get(k)
											.setWalkerlistclone(walkerinfo);
									Marker m = map
											.addMarker(new MarkerOptions()
													.position(
															new LatLng(
																	Double.parseDouble(walkerinfo
																			.getLatitude()),
																	Double.parseDouble(walkerinfo
																			.getLongitude())))
													.icon(BitmapDescriptorFactory
															.fromResource(R.drawable.img_taxi_pin)));
									walkerarrayformarker.get(k)
											.setDrivermarkers(m);
								}
							}
						}
						if (flag == 0) {
//							Log.d("hey", "in flag");
							Marker m = map
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(
															Double.parseDouble(walkerinfo
																	.getLatitude()),
															Double.parseDouble(walkerinfo
																	.getLongitude())))
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.img_taxi_pin)));
							walkerinfo_marker mwalkerinfo_marker = new walkerinfo_marker(
									walkerinfo, m);
							walkerarrayformarker.add(mwalkerinfo_marker);

						}

					}

					for (int s = 0; s < walkerarrayformarker.size(); s++) {
						int flag1 = 0;
						for (int r = 0; r < walkerlist.size(); r++) {
							if (TextUtils.equals(walkerarrayformarker.get(s)
									.getWalkerlistclone().getId(), walkerlist
									.get(r).getId())) {
								flag1++;
							}

						}
						if (flag1 == 0) {
							walkerarrayformarker.get(s).getDrivermarkers()
									.remove();
							walkerarrayformarker.remove(s);
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				 markthewalkers();

			} else {
				removemarkers();
				walkerlist.clear();

			}
			if (pickuppop.getVisibility() == View.VISIBLE) {
				gettime();
			}
			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.GET_PROMO_REQUEST:
			if (activity.pContent.isSuccess(response)) {
				String discount = "";

				try {
					JSONObject obj = new JSONObject("" + response);
					discount = obj.getString("discount");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				promoresponse.setText("You get " + discount
						+ " off in your current ride");
				editorpromo.putString("promocode", promoglobal);
				editorpromo.commit();
				Toast.makeText(activity, "PromoCode entered successfully",
						Toast.LENGTH_LONG).show();

			} else {
				// if (activity.pContent.getErrorCode(response) == 417) {

				// 09-29 13:26:58.906: I/UBER FOR X(7924):
				// {"success":false,"error":"Already request is Pending"}

				try {
					JSONObject obj = new JSONObject("" + response);

					errorCode = obj.getInt("error_code");
					if (obj.has("error")) {
						errormsg = obj.getString("error");
						promoresponse.setText(errormsg);
						errorCode = obj.getInt("error_code");

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (errorCode == 416) {
				editorpromo.putString("promocode", "");
				editorpromo.commit();
			}

			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.GET_ADDRESS:

			AndyUtils.removeCustomProgressDialog();

			try {
//				Log.d("pavan", "map response" + "" + response);
				JSONObject jobj = new JSONObject("" + response);

				if (jobj.get("status").equals("OK")) {

//					Log.d("pavan", "in okay");

					JSONArray jarray = jobj.getJSONArray("results");

					if (jarray.length() > 0) {

						JSONObject jobj1 = jarray.getJSONObject(0);

						strAddress = jobj1.getString("formatted_address");
						strAddress = strAddress.replace(",null", "");
						strAddress = strAddress.replace("null", "");
						strAddress = strAddress.replace("Unnamed", "");

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!TextUtils.isEmpty(strAddress)) {
									etSource.setFocusable(false);
									etSource.setFocusableInTouchMode(false);
									etSource.setText(strAddress);
									etSource.setTextColor(getResources()
											.getColor(android.R.color.white));
									etSource.setFocusable(true);
									etSource.setFocusableInTouchMode(true);

								} else {
									etSource.setText("");
									etSource.setTextColor(getResources()
											.getColor(android.R.color.white));
								}
								etSource.setEnabled(true);
							}
						});
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
						destlatlng_places = new LatLng(jobj3.getDouble("lat"),
								jobj3.getDouble("lng"));

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case Const.ServiceCode.Add_FAV:
			AndyUtils.removeCustomProgressDialog();
			// Save fav place
//			Log.d("MSG", "Fav Place Response: " + "" + response);
			break;
		}

	}

	private void showPaymentDailog(List<String> paymentoption) {
		// TODO Auto-generated method stub

		final Dialog dialog = new Dialog(activity);
		View view = activity.getLayoutInflater().inflate(
				R.layout.popup_select_payment, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		final MyFontTextView tvSelect;

		tvSelect = (MyFontTextView) view.findViewById(R.id.tvSelect);
		final TextView addcard = (TextView) view.findViewById(R.id.addcard);
		etFriendEmail = (EditText) view.findViewById(R.id.etFriendEmail);
		final LinearLayout askfrnd = (LinearLayout) view
				.findViewById(R.id.askfrnd);
		final RadioButton rbCash = (RadioButton) view.findViewById(R.id.rdCash);
		final RadioButton rbCard = (RadioButton) view.findViewById(R.id.rdCard);
		final RadioButton rdfriend = (RadioButton) view
				.findViewById(R.id.rdfriend);
		Button btnask = (Button) view.findViewById(R.id.btnask);

		final RadioButton rbPayPal = (RadioButton) view
				.findViewById(R.id.rdCash);
		final RadioButton rbanother = (RadioButton) view
				.findViewById(R.id.rdanother);
		etFriendEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				rbCash.setChecked(false);
				rbCard.setChecked(false);
			}
		});

		rdfriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (rdfriend.isChecked()) {

					rbCash.setVisibility(View.GONE);
					addcard.setVisibility(View.GONE);
					rbCard.setVisibility(View.GONE);
					askfrnd.setVisibility(View.VISIBLE);
					tvSelect.setVisibility(View.GONE);
					rdfriend.setVisibility(View.GONE);
				} else {
					rbCash.setVisibility(View.VISIBLE);
					addcard.setVisibility(View.VISIBLE);
					rbCard.setVisibility(View.VISIBLE);
					askfrnd.setVisibility(View.GONE);
					rdfriend.setVisibility(View.VISIBLE);
					tvSelect.setVisibility(View.VISIBLE);

				}
			}
		});

		if (paymentoption.contains("By Cash")) {
			rbCash.setVisibility(View.VISIBLE);
		} else
			rbCash.setVisibility(View.GONE);

		if (paymentoption.contains("By Card")) {
			rbCard.setVisibility(View.VISIBLE);
			addcard.setVisibility(View.GONE);
		} else
			rbCard.setVisibility(View.GONE);

		if (paymentoption.contains("By PayPal"))
			rbPayPal.setVisibility(View.GONE);
		btnask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etFriendEmail.getText().length() == 0) {
					Toast.makeText(activity, "Please Add your friend email",
							Toast.LENGTH_LONG).show();
				} else{
					requestmoney();
				rbCard.setVisibility(View.GONE);
				rbCash.setVisibility(View.GONE);
				rdfriend.setVisibility(View.GONE);
				dialog.dismiss();
				}

			}

		});
		addcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				Intent i = new Intent(getActivity(),
						UberAddPaymentActivity.class);
				startActivity(i);

			}

		});

		tvSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (rbanother.isChecked()) {
					payment_type = 3;
					activity.pHelper.putPay(1);
				}
				if (rbCard.isChecked())
					payment_type = 0;

				if (rbCash.isChecked())
					payment_type = 1;

				// if (rbPayPal.isChecked())
				// payment_type = 2;

				if (payment_type == 0)
					getCards();
				if (payment_type == 1 || payment_type == 2 || payment_type == 3) {
//					Log.d("amal", "going to pick up");
					pickMeUp();
				}

			}
		});
		dialog.setContentView(view);

		dialog.show();

	}

	private void verifyEmail(String email) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.please_wait), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SEARCH_USER);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(activity).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
		map.put(Const.Params.EMAIL, String.valueOf(email));
//		Log.e("", "verifyEmail:" + map);
		new HttpRequester(activity, map, Const.ServiceCode.SEARCH_USER, this);
	}

	private void requestmoney() {
		// TODO Auto-generated method stub
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.please_wait), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.ASK_FRIEND);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(activity).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
		map.put("friend_email", etFriendEmail.getText().toString());
//		Log.d("mahi", "map sen" + map);

		new HttpRequester(activity, map, Const.ServiceCode.ASK_FRIEND, this);

	}

	private void setTabServiceType(ArrayList<VehicalType> listType2) {

		LayoutParams vParams = new LayoutParams(LayoutParams.MATCH_PARENT, 7);
		LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < listType.size(); i++) {
			rowTextView = new TextView(activity);
			lLayout = new LinearLayout(activity);
			v = new View(activity);

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
			rowTextView.setTextColor(activity.getResources().getColor(
					R.color.gray_lite));

			if (listType.get(i).isSelected) {
				rowTextView.setTextColor(activity.getResources().getColor(
						R.color.app_color_blue));
				v.setBackgroundColor(activity.getResources().getColor(
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

//					Log.e("NIKHIL",
//							listType.get(v.getId()).getName()
//									+ listType.get(v.getId()).getId() + " :: "
//									+ String.valueOf(v.getId()));

					for (int j = 0; j < listType.size(); j++) {
						if (j == v.getId()) {
							tvServiceTypes[j].setTextColor(activity
									.getResources().getColor(
											R.color.app_color_blue));
							views[j].setBackgroundColor(activity.getResources()
									.getColor(R.color.app_color_blue));

						} else {
							tvServiceTypes[j]
									.setTextColor(activity.getResources()
											.getColor(R.color.gray_lite));
							views[j].setBackgroundColor(activity.getResources()
									.getColor(R.color.white));
						}

					}

					selectedPostion = v.getId();

					removemarkers();
					getallproviders();

				}
			});

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
			if (isContinueRequest) {
				isContinueRequest = false;
				getRequestStatus(String
						.valueOf(activity.pHelper.getRequestId()));
			}
		}

	}

	private void getRequestStatus(String requestId) {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

//		AppLog.Log("GET REQUEST STATUS",
//				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
//						+ new PreferenceHelper(activity).getUserId() + "&"
//						+ Const.Params.TOKEN + "="
//						+ new PreferenceHelper(activity).getSessionToken()
//						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

		new HttpRequester(activity, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	private void startCheckingStatusUpdate() {
		stopCheckingStatusUpdate();
		if (activity.pHelper.getRequestId() != Const.NO_REQUEST) {
			isContinueRequest = true;
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY,
					Const.TIME_SCHEDULE);
		}
	}

	private void stopCheckingStatusUpdate() {
		isContinueRequest = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void cancleRequest() {
//		Log.d("mahi", "in cancel request");
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.removeCustomProgressDialog();
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

	class FavStoreStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String id = intent.getStringExtra(Const.Params.ID);
			if (!TextUtils.isEmpty(id)) {
				String name = intent.getStringExtra(Const.Params.NAME);
				double latitude = intent.getDoubleExtra(Const.Params.LATITUDE,
						0);
				double longitude = intent.getDoubleExtra(
						Const.Params.LONGITUDE, 0);
				LatLng favPlacePosition = new LatLng(latitude, longitude);
				animateCameraToMarker(favPlacePosition);
			}
		}

	}

	class WalkerStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				String response1 = intent.getStringExtra("message");
				if (!TextUtils.isEmpty(response1)) {
					startActivity(new Intent(activity, FriendPayActivity.class));
					activity.finish();
				}

				String response = intent
						.getStringExtra(Const.EXTRA_WALKER_STATUS);
//				Log.e("NIKHIL", "Push " + response);
				if (TextUtils.isEmpty(response))
					return;
				stopCheckingStatusUpdate();

				if (activity.pContent.isSuccess(response)) {

//					Log.d("pavan",
//							"request_status "
//									+ activity.pContent
//											.checkRequestStatus(response));

					switch (activity.pContent.checkRequestStatus(response)) {
					case Const.IS_WALK_STARTED:
					case Const.IS_WALKER_ARRIVED:
					case Const.IS_COMPLETED:
					case Const.IS_WALKER_STARTED:
						AndyUtils.removeCustomProgressDialog();
						// stopCheckingStatusUpdate();
						Driver driver = activity.pContent.getDriverDetail(""
								+ response);
//						Log.d("amal", "driver detail  --->" + driver.toString());
						if (btnadddestination.getVisibility() == View.VISIBLE) {
							btnadddestination.setVisibility(View.GONE);
						}
						removeThisFragment();
						activity.pHelper.putDistance(Const.NO_DISTANCE);
						activity.pHelper.putRequestTime(Const.NO_TIME);
						activity.gotoTripFragment(driver);
						break;
					case Const.IS_WALKER_RATED:
						// stopCheckingStatusUpdate();
						activity.gotoRateFragment(activity.pContent
								.getDriverDetail(response));
						break;

					// case Const.IS_WALKER_REACHED:
					// activity.gotoRateFragment(activity.pContent
					// .getDriverDetail(response));
					// break;

					case Const.IS_REQEUST_CREATED:
						AndyUtils.showCustomProgressDialog(activity,
								getString(R.string.text_contacting), false,
								UberMapFragment.this);
						startCheckingStatusUpdate();
						isContinueRequest = true;
						break;
					default:
						isContinueRequest = false;
						break;
					}

				} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
					AndyUtils.removeCustomProgressDialog();
					activity.pHelper.clearRequestData();
					isContinueRequest = false;
				} else if (activity.pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
					if (activity.pHelper.getLoginBy().equalsIgnoreCase(
							Const.MANUAL))
						login();
					else
						loginSocial(activity.pHelper.getUserId(),
								activity.pHelper.getLoginBy());
				} else {
					isContinueRequest = true;
					startCheckingStatusUpdate();
				}
				// startCheckingStatusUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void removeThisFragment() {
		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getVehicalTypes() {
		isGettingVehicalType = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_VEHICAL_TYPES);
//		AppLog.Log(Const.TAG, Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_VEHICAL_TYPES,
				true, this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(int pos) {
		selectedPostion = pos;

	}

	/*
	 * Added by Amal
	 */
	private void getCards() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_CARDS + Const.Params.ID + "="
				+ new PreferenceHelper(getActivity()).getUserId() + "&"
				+ Const.Params.TOKEN + "="
				+ new PreferenceHelper(getActivity()).getSessionToken());
		new HttpRequester(getActivity(), map, Const.ServiceCode.GET_CARDS,
				true, this);
	}

	private void selectPayment() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.PAYMENT_OPTIONS + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken());

		new HttpRequester(activity, map, Const.ServiceCode.PAYMENT_OPTIONS,
				true, this);

	}

	private void getDistance() {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();

		LatLng deslatlng = getLocationFromAddress(enterdestination.getText()
				.toString());
		if (deslatlng != null) {
			map.put(Const.URL,
					Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
							+ "=" + String.valueOf(curretLatLng.latitude) + ","
							+ String.valueOf(curretLatLng.longitude) + "&"
							+ Const.Params.MAP_DESTINATIONS + "="
							+ String.valueOf(deslatlng.latitude) + ","
							+ String.valueOf(deslatlng.longitude));

//			Log.d("amal", "request from getdistance " + map);
			new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_DETAILS,
					true, this);
		} else {
			AndyUtils.removeCustomProgressDialog();
			Toast.makeText(activity, "Please Enter The Address Correctly",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	TextView promoresponse;

	private void ApplyPromo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflate = getActivity().getLayoutInflater();
		View contentview = inflate.inflate(R.layout.promocard, null);
		View titleview = inflate.inflate(R.layout.promocodecustomtitle, null);
		builder.setView(contentview).setCustomTitle(titleview);
		final AlertDialog mdialog = builder.create();
		mdialog.show();
		MyFontButton apply, cancel;

		final MyFontEdittextView promofield;
		apply = (MyFontButton) contentview.findViewById(R.id.promoApply);
		cancel = (MyFontButton) contentview.findViewById(R.id.promoCancel);
		promofield = (MyFontEdittextView) contentview.findViewById(R.id.promo);
		promoresponse = (TextView) contentview.findViewById(R.id.promoresponse);

		apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendpromo(promofield.getText().toString());

			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mdialog.dismiss();

			}
		});

	}

	void sendpromo(String promostring) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		promoglobal = promostring;
		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_PROMO_REQUEST + Const.Params.ID + "="
						+ new PreferenceHelper(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(activity).getSessionToken()
						+ "&" + Const.Params.PROMO_CODE + "=" + promostring);

		new HttpRequester(activity, map, Const.ServiceCode.GET_PROMO_REQUEST,
				true, this);
	}

	private void gettime() {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		if (walkerlist.isEmpty()) {
			eta.setText("No Providers Nearby");
		} else {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL,
					Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
							+ "=" + String.valueOf(curretLatLng.latitude) + ","
							+ String.valueOf(curretLatLng.longitude) + "&"
							+ Const.Params.MAP_DESTINATIONS + "="
							+ walkerlist.get(0).getLatitude() + ","
							+ walkerlist.get(0).getLongitude());

//			Log.d("amal", "request " + map);
			new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_TIME,
					true, this);
		}
	}

	private void getEstimation(String distance, String duration) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.FARE_CALCULATOR);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(getActivity()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getActivity()).getUserId());
		map.put(Const.Params.TIME, String.valueOf(duration));
		map.put(Const.Params.DISTANCE, String.valueOf(distance));
		// map.put(Const.Params.TYPE,
		// String.valueOf(listType.get(selectedPostion).getId()));
		//
		// map.put(Const.Params.DISTANCE, "1");
//		Log.d("amal", "request " + map);
		new HttpRequester(activity, map, Const.ServiceCode.FARE_CALCULATOR,
				this);

		// Log.d("amal", "request " + map);

	}

	private void ShowFare(String fare, String currency) {
//		Log.d("amal", "in show fare");
		final Dialog mDialog = new Dialog(getActivity(),
				R.style.MyFareestimateDialog);
//		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mDialog.setContentView(R.layout.fareestimate);

		TextView fromaddress, toaddress, estimatedfare, servicename;
		MyFontButton fareclose;
		fromaddress = (TextView) mDialog.findViewById(R.id.fromaddress);
		toaddress = (TextView) mDialog.findViewById(R.id.toaddress);
		estimatedfare = (TextView) mDialog.findViewById(R.id.estimatedfare);
		servicename = (TextView) mDialog.findViewById(R.id.servicename);
		fareclose = (MyFontButton) mDialog.findViewById(R.id.faredialogclose);
		fromaddress.setText(etSource.getText().toString());
		toaddress.setText(enterdestination.getText().toString());
		estimatedfare.setText(currency + " " + fare);
		servicename.setText(listType.get(selectedPostion).getName());
		fareclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();

			}
		});
		mDialog.show();

	}

	private void getallproviders() {
//		Log.d("amal", "in get all provider");
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}

		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL, Const.ServiceType.GETPROVIDER_ALL);
			map.put(Const.Params.TOKEN,
					new PreferenceHelper(activity).getSessionToken());
			map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
			map.put(Const.Params.LATITUDE,
					String.valueOf(curretLatLng.latitude));
			map.put(Const.Params.LONGITUDE,
					String.valueOf(curretLatLng.longitude));
			map.put(Const.Params.TYPE,
					String.valueOf(listType.get(selectedPostion).getId()));
			map.put(Const.Params.DISTANCE, "1");
			new HttpRequester(activity, map, Const.ServiceCode.GETPROVIDER_ALL,
					this);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void removemarkers() {
		for (int a = 0; a < walkerarrayformarker.size(); a++) {
			walkerarrayformarker.get(a).getDrivermarkers().remove();
		}
		walkerarrayformarker.clear();
		/*
		 * for (int i = 0; i < drivermarkers.size(); i++) { if
		 * (drivermarkers.get(i) != null) drivermarkers.get(i).remove(); }
		 * drivermarkers.clear();
		 */
	}

	void markthewalkers() {

		for (int i = 0; i < walkerlist.size(); i++) {
			Marker m = map.addMarker(new MarkerOptions().position(
					new LatLng(Double.parseDouble(walkerlist.get(i)
							.getLatitude()), Double.parseDouble(walkerlist.get(
							i).getLongitude()))).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.img_taxi_pin)));

			/*
			 * map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double
			 * .parseDouble(walkerlist.get(i).getLatitude()), Double
			 * .parseDouble(walkerlist.get(i).getLongitude()))));
			 * map.animateCamera(CameraUpdateFactory.zoomTo(15));
			 */
			LatLngBounds.Builder bld = new LatLngBounds.Builder();
			bld.include(m.getPosition());
			LatLngBounds latLngBounds = bld.build();
			map.moveCamera(CameraUpdateFactory
					.newLatLngBounds(latLngBounds, 15));
			CameraUpdate cameraUpdate = null;

			cameraUpdate = CameraUpdateFactory.newLatLngZoom(curretLatLng, 15);
			map.animateCamera(cameraUpdate);
			drivermarkers.add(m);
		}

	}

	private class walkerinfo_marker {
		private Walkerinfo walkerlistclone;
		private Marker drivermarkers;

		public walkerinfo_marker(Walkerinfo walkerinfo, Marker m) {
			walkerlistclone = walkerinfo;
			drivermarkers = m;
		}

		public Walkerinfo getWalkerlistclone() {
			return walkerlistclone;
		}

		public void setWalkerlistclone(Walkerinfo walkerlistclone) {
			this.walkerlistclone = walkerlistclone;
		}

		public Marker getDrivermarkers() {
			return drivermarkers;
		}

		public void setDrivermarkers(Marker drivermarkers) {
			this.drivermarkers = drivermarkers;
		}

	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private ArrayList<walkerinfo_marker> walkerarrayformarker;

	private void AddToFav(String favPlaceName) {
//		Log.d("MSG", "Name: " + favPlaceName);
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.please_wait), true, null);

		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL, Const.ServiceType.AddFav);
			map.put(Const.Params.TOKEN,
					new PreferenceHelper(activity).getSessionToken());
			map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
			map.put(Const.Params.NAME, favPlaceName);
			map.put(Const.Params.LATITUDE,
					String.valueOf(curretLatLng.latitude));
			map.put(Const.Params.LONGITUDE,
					String.valueOf(curretLatLng.longitude));
			map.put(Const.Params.ADDRESS, etSource.getText().toString());

			new HttpRequester(activity, map, Const.ServiceCode.Add_FAV, this);

//			AppLog.Log("pavan", "map " + map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Favname() {

		final Dialog dialog = new Dialog(activity);
		View view = activity.getLayoutInflater().inflate(
				R.layout.popup_fav_place, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		TextView tvNo = (TextView) view.findViewById(R.id.tvNo);
		TextView tvOK = (TextView) view.findViewById(R.id.tvOK);
		final EditText etFavPlace = (EditText) view
				.findViewById(R.id.etFavPlace);

		tvNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		tvOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddToFav(etFavPlace.getText().toString());
				dialog.dismiss();
			}
		});

		dialog.setContentView(view);

		dialog.show();

		// AlertDialog.Builder alertDialog = new
		// AlertDialog.Builder(getActivity());
		// alertDialog.setTitle("Favourite address");
		// // alertDialog.setMessage("Enter name");
		//
		// final EditText input = new EditText(getActivity());
		// input.setHint("Enter Favourite name");
		//
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT);
		// lp.setMargins(10, 200, 10, 10);
		// input.setLayoutParams(lp);
		// alertDialog.setView(input);
		//
		// alertDialog.setPositiveButton("Ok",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// AddToFav(input.getText().toString());
		// }
		// });
		//
		// alertDialog.setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.cancel();
		// }
		// });
		//
		// alertDialog.show();

	}

}
