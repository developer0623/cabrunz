package com.cabrunzltd.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.cabrunzltd.user.adapter.DrawerAdapter;
import com.cabrunzltd.user.adapter.FavPlacesAdapter;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.component.MyFontPopUpTextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.db.DBHelper;
import com.cabrunzltd.user.fragments.UberFeedbackFragment;
import com.cabrunzltd.user.fragments.UberMapFragment;
import com.cabrunzltd.user.fragments.UberTripFragment;
import com.cabrunzltd.user.models.ApplicationPages;
import com.cabrunzltd.user.models.Driver;
import com.cabrunzltd.user.models.FavoritePlace;
import com.cabrunzltd.user.models.Reffral;
import com.cabrunzltd.user.models.User;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.mikhaellopez.circularimageview.CircularImageView;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class MainDrawerActivity extends ActionBarBaseActivitiy {
	private DrawerAdapter adapter;
	MenuDrawer mMenuDrawer;
	// DrawerLayout drawerLayout;
	private ListView listDrawer;
	private BroadcastReceiver mReceiver, mReceiver2;
	// private ActionBarDrawerToggle drawerToggel;
	public PreferenceHelper pHelper;
	public ParseContent pContent;
	private ArrayList<ApplicationPages> listMenu;
	private boolean isDataRecieved = false, isRecieverRegistered = false,
			isNetDialogShowing = false, isGpsDialogShowing = false,
			isbroadregpay = false, isbroadregsucces = false;
	private AlertDialog internetDialog, gpsAlertDialog, locationAlertDialog;
	private DBHelper dbHelper;
	private AQuery aQuery;
	private String name, picture, friend_id, actual_total = "";
	private LocationManager manager;
	private CircularImageView ivMenuProfile;
	private MyFontPopUpTextView tvMenuName;
	private ImageOptions imageOptions;

	public static boolean popon = false;
	private boolean doubleBackToExitPressedOnce = false;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

		dbHelper = new DBHelper(getApplicationContext());
		User user = dbHelper.getUser();
		aQuery = new AQuery(this);

		//////mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer = MenuDrawer.attach(this);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int menusize = (int) (width * 0.80);
		mMenuDrawer.setMenuSize(menusize);
		// Log.e("", "menusize:"+menusize);
		mMenuDrawer.setContentView(R.layout.activity_map);
		mMenuDrawer.setMenuView(R.layout.menu_drawer);
		mMenuDrawer.setDropShadowEnabled(true);
		registerfriendrequest();
		showinvoice();
		// Log.d("mahi", "job frgment in map" +pHelper.getCurrentFragment());
		if (isExternalStorageWritable() == false) {
			Toast.makeText(getApplicationContext(),
					"No SD Card Please insert !", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, MainActivity.class));
			this.finish();

		}

		btnActionMenu.setVisibility(View.VISIBLE);
		setIcon(R.drawable.notification_box);

		imageOptions = new ImageOptions();
		imageOptions.memCache = true;
		imageOptions.fileCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.default_user;

		// setContentView(R.layout.activity_map);
		pHelper = new PreferenceHelper(this);
		pContent = new ParseContent(this);
		// drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		listDrawer = (ListView) findViewById(R.id.left_drawer);
		listMenu = new ArrayList<ApplicationPages>();
		adapter = new DrawerAdapter(this, listMenu);
		listDrawer.setAdapter(adapter);

		Log.d("mahi", new PreferenceHelper(this).getCurrentFragment());

		ivMenuProfile = (CircularImageView) findViewById(R.id.ivMenuProfile);
		try {
			aQuery.id(ivMenuProfile).progress(R.id.pBar)
					.image(user.getPicture(), imageOptions);

			tvMenuName = (MyFontPopUpTextView) findViewById(R.id.tvMenuName);

			tvMenuName.setText(user.getFname() + " " + user.getLname());
		} catch (Exception e) {
			e.printStackTrace();
		}

		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// drawerToggel = new ActionBarDrawerToggle(this, drawerLayout,
		// R.drawable.slide_btn, 0, 0);
		// drawerLayout.setDrawerListener(drawerToggel);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setHomeAsUpIndicator(R.drawable.slide_btn);

		listDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// drawerLayout.closeDrawer(listDrawer);
				mMenuDrawer.closeMenu();
				if (position == 0) {
					startActivity(new Intent(MainDrawerActivity.this,
							ProfileActivity.class));

				} else if (position == 1) {
					startActivity(new Intent(MainDrawerActivity.this,
							UberViewPaymentActivity.class));

				/*} else if (position == 2) {
					startActivity(new Intent(MainDrawerActivity.this,
							ScheduleActivity.class));
				} else if (position == 3) {
					startActivity(new Intent(MainDrawerActivity.this,
							ScheduleList.class));
*/
				} else if (position == 2) {
					startActivity(new Intent(MainDrawerActivity.this,
							HistoryActivity.class));
				} else if (position == 3) {
					getReffrelaCode();

				} else if (position == 4) {
					startActivity(new Intent(MainDrawerActivity.this,
							promotionActivity.class));
				} else if (position == 5) {
					requestFavoritePlaces();
				} else if (position == (listMenu.size() - 1)) {

					ShowLogoutPopup();

					// new AlertDialog.Builder(MainDrawerActivity.this)
					// .setTitle(getString(R.string.dialog_logout))
					// .setMessage(getString(R.string.dialog_logout_text))
					// .setPositiveButton(android.R.string.yes,
					// new DialogInterface.OnClickListener() {
					// public void onClick(
					// DialogInterface dialog,
					// int which) {
					// // continue with delete
					// pHelper.Logout();
					// goToMainActivity();
					//
					// }
					// })
					// .setNegativeButton(android.R.string.no,
					// new DialogInterface.OnClickListener() {
					// public void onClick(
					// DialogInterface dialog,
					// int which) {
					// // do nothing
					// dialog.cancel();
					// }
					// })
					// .setIcon(android.R.drawable.ic_dialog_alert).show();
				} else {
					Intent intent = new Intent(MainDrawerActivity.this,
							MenuDescActivity.class);
					intent.putExtra(Const.Params.TITLE, listMenu.get(position)
							.getTitle());
					intent.putExtra(Const.Params.CONTENT, listMenu
							.get(position).getData());
					startActivity(intent);
				}

			}

		});
		// mMenuDrawer.peekDrawer();

	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private void ShowLogoutPopup() {

		final Dialog dialog = new Dialog(this);
		View view = this.getLayoutInflater().inflate(R.layout.popup_logout,
				null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		TextView tvNo = (TextView) view.findViewById(R.id.tvNo);
		TextView tvLogout = (TextView) view.findViewById(R.id.tvLogout);

		tvNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pHelper.Logout();
				goToMainActivity();
				dialog.dismiss();
			}
		});

		dialog.setContentView(view);

		dialog.show();

		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			ShowGpsDialog();
		} else {
			removeGpsDialog();
		}
		registerReceiver(internetConnectionReciever, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
		registerReceiver(GpsChangeReceiver, new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION));
		isRecieverRegistered = true;

		if (AndyUtils.isNetworkAvailable(this)
				&& manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (!isDataRecieved) {
				isDataRecieved = true;
				checkStatus();
			}

			startLocationUpdateService();
		}

	}

	private void showinvoice() {
		// TODO Auto-generated method stub
		isbroadregsucces = true;
		IntentFilter intentFilter = new IntentFilter("PAY_SUCCESS");
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action != null && action.equals("PAY_SUCCESS")) {

					actual_total = intent.getExtras().getString("actual_total");
					Log.d("mahi", actual_total);

					final Dialog mDialog = new Dialog(MainDrawerActivity.this,
							android.R.style.Theme_Translucent_NoTitleBar);
					mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					mDialog.setContentView(R.layout.friend_invoice);

					Button btncancel = (Button) mDialog
							.findViewById(R.id.btnclose);
					TextView tvtotalprice = (TextView) mDialog
							.findViewById(R.id.tvtotalprice);

					tvtotalprice.setText(String.format("%.2f",
							Double.valueOf(actual_total)));

					btncancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mDialog.dismiss();

						}

					});

					mDialog.setCancelable(false);
					mDialog.show();

				}
			}
		};
		this.registerReceiver(mReceiver, intentFilter);

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	private void registerfriendrequest() {
		// TODO Auto-generated method stub
		isbroadregpay = true;
		IntentFilter intentFilter = new IntentFilter("PAY_FRIEND");
		mReceiver2 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action != null && action.equals("PAY_FRIEND")) {
					name = intent.getExtras().getString("name");
					picture = intent.getExtras().getString("picture");
					friend_id = intent.getExtras().getString("friend_id");
					Log.d("mahi", "Broadcast received: " + name);
					Log.d("mahi", "Broadcast received: " + picture);

					final Dialog mDialog = new Dialog(MainDrawerActivity.this,
							android.R.style.Theme_Translucent_NoTitleBar);
					mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					/*
					 * mDialog.getWindow().setBackgroundDrawable( new
					 * ColorDrawable(android.graphics.Color.WHITE));
					 */
					mDialog.setContentView(R.layout.pay_friend);

					Button btncancel = (Button) mDialog
							.findViewById(R.id.btncancel);
					Button btnsubmit = (Button) mDialog
							.findViewById(R.id.btnsubmit);
					CircularImageView tvprofile = (CircularImageView) mDialog
							.findViewById(R.id.tvprofile);

					TextView tvname = (TextView) mDialog
							.findViewById(R.id.tvname);
					if (picture != null || name != null) {
						tvname.setText(name);
						aQuery.id(tvprofile).progress(R.id.pBar)
								.image(picture, imageOptions);
					}
					btncancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mDialog.dismiss();
							acceptrequest(0);

						}

					});
					btnsubmit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mDialog.dismiss();
							acceptrequest(1);
						}

					});
					mDialog.setCancelable(false);
					mDialog.show();

				}
			}
		};
		this.registerReceiver(mReceiver2, intentFilter);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		hideKeyboard();
		// if (drawerToggel.onOptionsItemSelected(item)) {
		// return true;
		// }
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

		super.onPause();

	}

	private void acceptrequest(int accept) {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.ACCEPT_REJECT);
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(this).getSessionToken());
		map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
		map.put(Const.Params.FRIEND_ID, friend_id);
		map.put(Const.Params.ACCEPT, String.valueOf(accept));
		new HttpRequester(this, map, Const.ServiceCode.ACCEPT_REJECT, this);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// drawerToggel.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// drawerToggel.onConfigurationChanged(newConfig);
	}

	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionMenu:
		case R.id.tvTitle:
			mMenuDrawer.toggleMenu();
			break;
		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (popon == true) {
			UberMapFragment.setpickpopupInvisible();
			UberMapFragment.setmarkerVisibile();
			popon = false;
		}
		if (mMenuDrawer.isEnabled()) {
			mMenuDrawer.closeMenu();
		} else {
			super.onBackPressed();
		}

		if (doubleBackToExitPressedOnce
				&& new PreferenceHelper(this).getCurrentFragment().equals(
						Const.FRAGMENT_MAP)
				|| new PreferenceHelper(this).getCurrentFragment().equals(
						Const.FRAGMENT_TRIP)) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		// Toast.makeText(this, "Please click back again to exit",
		// Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);

	}

	private void getRequestInProgress() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.REQUEST_IN_PROGRESS + Const.Params.ID + "="
						+ new PreferenceHelper(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.GET_REQUEST_IN_PROGRESS,
				true, this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.uberorg.ActionBarBaseActivitiy#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		super.onTaskCompleted(response, serviceCode);
		// Log.e("", serviceCode+":"+response);
		switch (serviceCode) {
		case Const.ServiceCode.GET_REQUEST_IN_PROGRESS:
			if (response != null)
				AndyUtils.removeCustomProgressDialog();
			if (pContent.isSuccess(response)) {
				if (pContent.getRequestInProgress(response) == Const.NO_REQUEST) {
					AndyUtils.removeCustomProgressDialog();
					gotoMapFragment();
				} else {
					pHelper.putRequestId(pContent.getRequestId(response));
					getRequestStatus(String.valueOf(pHelper.getRequestId()));
				}
			} else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				Log.d("yyy", "invalid token");
				if (pHelper.getLoginBy().equalsIgnoreCase(Const.MANUAL))
					login();
				else
					loginSocial(pHelper.getUserId(), pHelper.getLoginBy());
			} else if (pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				AndyUtils.removeCustomProgressDialog();
				pHelper.clearRequestData();
				gotoMapFragment();
			}

			getMenuItems();
			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:
			Log.d("mahi", "map rec" + response);
			if (response != null)
				AndyUtils.removeCustomProgressDialog();

			if (pContent.isSuccess(response)) {
				switch (pContent.checkRequestStatus(response)) {
				case Const.IS_WALK_STARTED:
				case Const.IS_WALKER_ARRIVED:
				case Const.IS_COMPLETED:
				case Const.IS_WALKER_STARTED:
					// case Const.IS_WALKER_RATED:
					Driver driver = pContent.getDriverDetail(response);
					gotoTripFragment(driver);
					break;

				case Const.IS_WALKER_RATED:
					gotoRateFragment(pContent.getDriverDetail(response));
					break;

				// case Const.IS_WALKER_REACHED:
				// gotoRateFragment(pContent.getDriverDetail(response));
				// break;

				default:
					gotoMapFragment();
					break;
				}

			} else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				login();
			} else if (pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				pHelper.clearRequestData();
				gotoMapFragment();
			}
			getMenuItems();
			break;
		case Const.ServiceCode.LOGIN:
			if (response != null)
				if (pContent.isSuccessWithStoreId(response)) {
					checkStatus();
				}
			break;
		case Const.ServiceCode.ACCEPT_REJECT:
			if (response != null)
				AndyUtils.removeCustomProgressDialog();
			Log.d("mahi", "accept or reject" + "" + response);

			break;
		case Const.ServiceCode.GET_PAGES:
			if (response != null)
				listMenu.clear();
			pContent.parsePages(listMenu, response);
			ApplicationPages applicationPages = new ApplicationPages();
			applicationPages.setData("");
			applicationPages.setId(-3);
			applicationPages.setTitle(getString(R.string.dialog_logout));
			listMenu.add(applicationPages);
			adapter.notifyDataSetChanged();

			break;
		case Const.ServiceCode.GET_REFERREL:
			if (pContent.isSuccess(response)) {
				Reffral ref = pContent.parseReffrelCode(response);
				if (ref != null) {
					showRefferelDialog(ref.getReferralCode());
				}

			} else {
				showRefferelDialog("");

			}
			AndyUtils.removeCustomProgressDialog();

			break;

		case Const.ServiceCode.UPDATE_REFFRAL_CODE:

			AndyUtils.removeCustomProgressDialog();
			Log.d("yyy", "kumar " + response);
			getReffrelaCode();
			if (pContent.isSuccess(response)) {

				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.toast_update_referal_success),
						Toast.LENGTH_LONG).show();
			}

			break;

		case Const.ServiceCode.LIST_FAV_PLACES:
			ArrayList<FavoritePlace> favPlaces = new ArrayList<FavoritePlace>();
			if (pContent.isSuccess(response)) {
				favPlaces = pContent.parseFavPlaceResponse(response);
			}
			AndyUtils.removeCustomProgressDialog();
			showFavoritePlacesList(favPlaces);
			break;

		case Const.ServiceCode.DELETE_FAV_PLACES:
			if (pContent.isSuccess(response)) {
				Log.d("MSG", "Delete Fav Response: " + response);
			}
			break;
		}
	}

	private void getRequestStatus(String requestId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REQUEST_STATUS
				+ Const.Params.ID + "=" + pHelper.getUserId() + "&"
				+ Const.Params.TOKEN + "=" + pHelper.getSessionToken() + "&"
				+ Const.Params.REQUEST_ID + "=" + requestId);

		Log.d("mahi", "map send" + map);

		new HttpRequester(this, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	public void gotoMapFragment() {
		if (!currentFragment.equals(Const.FRAGMENT_MAP)) {
		etSource.setVisibility(View.VISIBLE);
		UberMapFragment frag = UberMapFragment.newInstance();
		addFragment(frag, false, Const.FRAGMENT_MAP);
		}

	}

	public void gotoTripFragment(Driver driver) {
		 if (!currentFragment.equals(Const.FRAGMENT_TRIP)) {
		UberTripFragment tripFrag = new UberTripFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Const.DRIVER, driver);
		tripFrag.setArguments(bundle);
		addFragment(tripFrag, false, Const.FRAGMENT_TRIP);
		}
	}

	public void gotoRateFragment(Driver driver) {
		try {
			if (TextUtils.isEmpty(driver.getLastTime()))
				driver.setLastTime(0 + " " + getString(R.string.text_mins));
			if (TextUtils.isEmpty(driver.getLastDistance()))
				driver.setLastDistance(0.0 + " "
						+ getString(R.string.text_miles));


			if(!currentFragment.equals(Const.FRAGMENT_FEEDBACK)) {
			UberFeedbackFragment feedBack = new UberFeedbackFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Const.DRIVER, driver);
			feedBack.setArguments(bundle);
			addFragmentWithStateLoss(feedBack, false, Const.FRAGMENT_FEEDBACK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.EMAIL, pHelper.getEmail());
		map.put(Const.Params.PASSWORD, pHelper.getPassword());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN, pHelper.getDeviceToken());
		map.put(Const.Params.LOGIN_BY, Const.MANUAL);
		new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);

	}

	private void checkStatus() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_gettting_request_stat), false, null);
		if (pHelper.getRequestId() == Const.NO_REQUEST) {
			getRequestInProgress();
		} else {
			getRequestStatus(String.valueOf(pHelper.getRequestId()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.text_signin), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				new PreferenceHelper(this).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, loginType);
		new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);

	}

	private void getMenuItems() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_PAGES);
		AppLog.Log(Const.TAG, Const.URL);

		new HttpRequester(this, map, Const.ServiceCode.GET_PAGES, true, this);
	}

	private void getMenuItemsDetail(String id) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_PAGES_DETAIL);
		new HttpRequester(this, map, Const.ServiceCode.GET_PAGES_DETAILS, true,
				this);
	}

	public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final LocationManager manager = (LocationManager) context
					.getSystemService(LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// do something
				removeGpsDialog();
			} else {
				// do something else
				if (isGpsDialogShowing) {
					return;
				}
				ShowGpsDialog();
			}

		}
	};
	public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo activeWIFIInfo = connectivityManager
					.getNetworkInfo(connectivityManager.TYPE_WIFI);

			if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
				removeInternetDialog();
			} else {
				if (isNetDialogShowing) {
					return;
				}
				showInternetDialog();
			}
		}
	};
	private MyFontEdittextView etVerificationCode;

	private void ShowGpsDialog() {
		AndyUtils.removeCustomProgressDialog();
		isGpsDialogShowing = true;
		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getString(R.string.dialog_no_gps))
				.setMessage(getString(R.string.dialog_no_gps_messgae))
				.setPositiveButton(getString(R.string.dialog_enable_gps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
								removeGpsDialog();
							}
						})

				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeGpsDialog();
								finish();
							}
						});
		gpsAlertDialog = gpsBuilder.create();
		gpsAlertDialog.show();
	}

	public void showLocationOffDialog() {

		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
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
								finish();
							}
						});
		locationAlertDialog = gpsBuilder.create();
		locationAlertDialog.show();
	}

	private void removeLocationoffDialog() {
		if (locationAlertDialog != null && locationAlertDialog.isShowing()) {
			locationAlertDialog.dismiss();
			locationAlertDialog = null;

		}
	}

	private void removeGpsDialog() {
		if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
			gpsAlertDialog.dismiss();
			isGpsDialogShowing = false;
			gpsAlertDialog = null;

		}
	}

	private void removeInternetDialog() {
		if (internetDialog != null && internetDialog.isShowing()) {
			internetDialog.dismiss();
			isNetDialogShowing = false;
			internetDialog = null;

		}
	}

	private void showInternetDialog() {
		AndyUtils.removeCustomProgressDialog();
		isNetDialogShowing = true;
		AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
		internetBuilder.setCancelable(false);
		internetBuilder
				.setTitle(getString(R.string.dialog_no_internet))
				.setMessage(getString(R.string.dialog_no_inter_message))
				.setPositiveButton(getString(R.string.dialog_enable_3g),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										Settings.ACTION_SETTINGS);
								startActivity(intent);
								removeInternetDialog();
							}
						})
				.setNeutralButton(getString(R.string.dialog_enable_wifi),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// User pressed Cancel button. Write
								// Logic Here
								startActivity(new Intent(
										Settings.ACTION_WIFI_SETTINGS));
								removeInternetDialog();
							}
						})
				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeInternetDialog();
								finish();
							}
						});
		internetDialog = internetBuilder.create();
		internetDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isRecieverRegistered) {
			unregisterReceiver(internetConnectionReciever);
			unregisterReceiver(GpsChangeReceiver);

		}

		if (isbroadregpay) {

			unregisterReceiver(mReceiver2);
			isbroadregpay = false;
		} else if (isbroadregsucces) {
			unregisterReceiver(mReceiver);
			isbroadregsucces = false;
		}

	}

	private void showFavoritePlacesList(final ArrayList<FavoritePlace> favPlaces) {
		final Dialog mDialog = new Dialog(this, R.style.MyDialog);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.fav_places_layout);

		RelativeLayout pageFooter = (RelativeLayout) mDialog
				.findViewById(R.id.pageFooterWrapper);
		final ListView lvFavPlaces = (ListView) mDialog
				.findViewById(R.id.fav_places_list);
		final FavPlacesAdapter placesAdapter = new FavPlacesAdapter(favPlaces,
				MainDrawerActivity.this);
		lvFavPlaces.setAdapter(placesAdapter);

		lvFavPlaces.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String favPlaceName = favPlaces.get(position).getName();
				String favPlaceId = favPlaces.get(position).getId();
				LatLng favPlacePosition = favPlaces.get(position).getLatLng();
				double lat = favPlacePosition.latitude;
				double lng = favPlacePosition.longitude;
				Intent pushIntent = new Intent(
						Const.INTENT_FAVORITE_STORE_STATUS);
				pushIntent.putExtra(Const.Params.ID, favPlaceId);
				pushIntent.putExtra(Const.Params.NAME, favPlaceName);
				pushIntent.putExtra(Const.Params.LATITUDE, lat);
				pushIntent.putExtra(Const.Params.LONGITUDE, lng);
				LocalBroadcastManager.getInstance(MainDrawerActivity.this)
						.sendBroadcast(pushIntent);
				mDialog.dismiss();
			}
		});

		lvFavPlaces.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainDrawerActivity.this);
				builder.setMessage("Are you sure that you want to delete favorite place \""
						+ favPlaces.get(position).getName() + "\"?");
				builder.setTitle("Delete Favorite Place");
				builder.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								removeFavPlace(favPlaces.get(position).getId());
								favPlaces.remove(position);
								placesAdapter.notifyDataSetChanged();
							}

							private void removeFavPlace(String id) {
								HashMap<String, String> map = new HashMap<String, String>();
								map.put(Const.URL,
										Const.ServiceType.DELETE_FAV_PLACE);
								map.put(Const.Params.ID, new PreferenceHelper(
										MainDrawerActivity.this).getUserId());
								map.put(Const.Params.TOKEN,
										new PreferenceHelper(
												MainDrawerActivity.this)
												.getSessionToken());
								map.put(Const.Params.FAV_ADDRESS_ID, id);
								new HttpRequester(MainDrawerActivity.this, map,
										Const.ServiceCode.DELETE_FAV_PLACES,
										MainDrawerActivity.this);
							}
						});
				builder.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				builder.show();

				return false;

			}
		});

		pageFooter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		mDialog.show();
	}

	private void showRefferelDialog(final String refCode) {

		final Dialog mDialog = new Dialog(this, R.style.MyDialog);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// mDialog.getWindow().setBackgroundDrawable(
		// new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.ref_code_layout);

		// TextView tvTitle = (TextView) mDialog.findViewById(R.id.tvTitle);
		// tvTitle.setText(getString(R.string.text_ref_code) + refCode);

		final EditText edit_referal_code = (EditText) mDialog
				.findViewById(R.id.edit_referal_code);
		edit_referal_code.setText(refCode);
		final TextView blinkingtext = (TextView) mDialog
				.findViewById(R.id.blinkingtext);

		if (TextUtils.isEmpty(edit_referal_code.getText().toString())) {

			blinkingtext.setVisibility(View.VISIBLE);
			Animation anim = new AlphaAnimation(0.0f, 1.0f);
			anim.setDuration(500);
			anim.setStartOffset(20);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);
			blinkingtext.startAnimation(anim);
		}
		MyFontTextView tvReferralCode = (MyFontTextView) mDialog
				.findViewById(R.id.tvReferralCode);
		tvReferralCode.setText(refCode);

		ImageView btnBack = (ImageView) mDialog.findViewById(R.id.btnback);

		Button btnupdate = (Button) mDialog.findViewById(R.id.btnupdate);
		btnupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String update_refCode = refCode;
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"Use my promo code, "
										+ update_refCode
										+ ", and get exciting offers "
										+ System.getProperty("line.separator")
										+ "https://play.google.com/store/apps/details?id=com.cabrunzltd.user");
				startActivity(Intent.createChooser(sharingIntent,
						"Share Reffral Code"));

				// String update_refCode =
				// edit_referal_code.getText().toString();
				//
				// UpdateReffrelaCode(update_refCode);
				// if (blinkingtext.getVisibility() == View.VISIBLE)
				// blinkingtext.setVisibility(View.INVISIBLE);

				mDialog.dismiss();

			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});

		mDialog.show();

	}

	private void getReffrelaCode() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_getting_ref_code), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REFERRAL + Const.Params.ID
				+ "=" + pHelper.getUserId() + "&" + Const.Params.TOKEN + "="
				+ pHelper.getSessionToken());

		new HttpRequester(this, map, Const.ServiceCode.GET_REFERREL, true, this);
	}

	private void UpdateReffrelaCode(String code) {

		/*
		 * AndyUtils.showCustomProgressDialog(this,
		 * getString(R.string.text_getting_ref_code), false, null);
		 * HashMap<String, String> map = new HashMap<String, String>();
		 * map.put(Const.URL, Const.ServiceType.GET_REFERRAL + Const.Params.ID +
		 * "=" + pHelper.getUserId() + "&" + Const.Params.TOKEN + "=" +
		 * pHelper.getSessionToken());
		 * 
		 * new HttpRequester(this, map, Const.ServiceCode.GET_REFERREL, true,
		 * this);
		 */
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.text_referral_code), false,
				null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REFERRAL);
		map.put(Const.Params.ID, pHelper.getUserId());
		map.put(Const.Params.TOKEN, pHelper.getSessionToken());
		map.put(Const.Params.REFERRAL_CODE, code);
		Log.d("pavan", "Request  " + map);
		new HttpRequester(this, map, Const.ServiceCode.UPDATE_REFFRAL_CODE,
				false, this);

	}

	public boolean isLocationEnabled(Context context) {
		int locationMode = 0;
		String locationProviders;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				locationMode = Settings.Secure.getInt(
						context.getContentResolver(),
						Settings.Secure.LOCATION_MODE);

			} catch (SettingNotFoundException e) {
				e.printStackTrace();
				return false;
			}

			return locationMode != Settings.Secure.LOCATION_MODE_OFF;

		} else {
			locationProviders = Settings.Secure.getString(
					context.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			return !TextUtils.isEmpty(locationProviders);
		}

	}

	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { // TODO Auto-generated method stub
	 * super.onActivityResult(requestCode, resultCode, data);
	 * android.support.v4.app.Fragment
	 * frag=getSupportFragmentManager().findFragmentByTag(Const.FRAGMENT_TRIP);
	 * frag.onActivityResult(requestCode, resultCode, data); Log.d("xxx",
	 * "in parent activity"); }
	 */

	private void requestFavoritePlaces() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_get_fav_places), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LIST_FAV_PLACES);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				MainDrawerActivity.this).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(MainDrawerActivity.this).getUserId());
		new HttpRequester(MainDrawerActivity.this, map,
				Const.ServiceCode.LIST_FAV_PLACES, this);
	}

	// private void showMobileNumberValidationDialog(final String response,
	// final int otp_code) {
	//
	// final Dialog mDialog = new Dialog(this,
	// android.R.style.Theme_Translucent_NoTitleBar);
	// mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// // mDialog.getWindow().
	//
	// mDialog.getWindow().setBackgroundDrawable(
	// new ColorDrawable(android.graphics.Color.TRANSPARENT));
	//
	// mDialog.setCancelable(false);
	// mDialog.show();
	//
	// LayoutInflater li = LayoutInflater.from(getApplicationContext());
	// View addressView = li.inflate(R.layout.popup_otp, null);
	//
	// mDialog.setContentView(addressView);
	//
	// MyFontTextView tvVerfy = (MyFontTextView) addressView
	// .findViewById(R.id.tvVerify);
	//
	// MyFontButton btnVerify = (MyFontButton) addressView
	// .findViewById(R.id.btnVerify);
	//
	// etVerificationCode = (MyFontEdittextView) addressView
	// .findViewById(R.id.etVerificationCode);
	//
	// ImageView btnBack = (ImageView) addressView.findViewById(R.id.btnback);
	// btnVerify.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Toast.makeText(getApplicationContext(), "OnButton",
	// Toast.LENGTH_SHORT).show();
	// VerifyOPT(response, otp_code);
	// }
	//
	// });
	// tvVerfy.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Toast.makeText(getApplicationContext(), "tvVerfy",
	// Toast.LENGTH_SHORT).show();
	// VerifyOPT(response, otp_code);
	// }
	//
	// });
	//
	// btnBack.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// mDialog.dismiss();
	// Toast.makeText(getApplicationContext(),
	// "Mobile Number not validated", Toast.LENGTH_LONG)
	// .show();
	// }
	//
	// });
	//
	// //
	// // AlertDialog.Builder dialogBuilder = new Builder(activity);
	// // dialogBuilder.setTitle("Validate Mobile Number");
	// // final View view = activity.getLayoutInflater().inflate(
	// // R.layout.mobile_validation_layout, null);
	// // dialogBuilder.setView(view);
	// // dialogBuilder.setPositiveButton("OK", new OnClickListener() {
	// // EditText etCodeText = (EditText) view
	// // .findViewById(R.id.validationCode);
	// //
	// // @Override
	// // public void onClick(DialogInterface dialog, int which) {
	// // String code = etCodeText.getText().toString();
	// // if (code.equals(String.valueOf(otp_code))) {
	// // Toast.makeText(activity,
	// // "Mobile Number successfully validated",
	// // Toast.LENGTH_LONG).show();
	// // mobileValidated = true;
	// // if (pContent.isSuccessWithStoreId(response)) {
	// // Toast.makeText(activity,
	// // getString(R.string.toast_register_success),
	// // Toast.LENGTH_SHORT).show();
	// // // activity.phelper.putPassword(etPassword.getText().toString());
	// // pContent.parseUserAndStoreToDb(response);
	// // activity.phelper.putPassword(etPassword.getText()
	// // .toString());
	// // // JSONObject jsonObject;
	// // // try {
	// // // jsonObject = new JSONObject(response);
	// // // activity.phelper.putSessionToken(jsonObject
	// // // .getString(Const.Params.TOKEN));
	// // // activity.phelper.putUserId(jsonObject
	// // // .getString(Const.Params.ID));
	// // // gotoPaymentFragment(
	// // //
	// // // jsonObject.getString(Const.Params.ID),
	// // // jsonObject.getString(Const.Params.TOKEN));
	// // goToReffralCodeFragment(activity.phelper.getUserId(),
	// // activity.phelper.getSessionToken());
	// // // } catch (JSONException e) {
	// // // // TODO Auto-generated catch block
	// // // e.printStackTrace();
	// // // }
	// //
	// // } else {
	// // Toast.makeText(activity,
	// // getString(R.string.toast_register_failed),
	// // Toast.LENGTH_SHORT).show();
	// // }
	// // }
	// //
	// // }
	// // }).setNegativeButton("Cancel", new OnClickListener() {
	// //
	// // @Override
	// // public void onClick(DialogInterface dialog, int which) {
	// // mobileValidated = false;
	// // Toast.makeText(activity, "Mobile Number not validated",
	// // Toast.LENGTH_LONG).show();
	// // }
	// // }).setNeutralButton("Re-send Code", new OnClickListener() {
	// //
	// // @Override
	// // public void onClick(DialogInterface dialog, int which) {
	// // Toast.makeText(activity, "Re-sending validation code",
	// // Toast.LENGTH_LONG).show();
	// // }
	// // }).show();
	// // }
	// }
	//
	// public void VerifyOPT(String response, int otp_code) {
	// String code = etVerificationCode.getText().toString();
	// if (code.equals(String.valueOf(otp_code))) {
	// Toast.makeText(getApplicationContext(),
	// "Mobile Number successfully validated", Toast.LENGTH_LONG)
	// .show();
	// if (pContent.isSuccessWithStoreId(response)) {
	// Toast.makeText(getApplicationContext(),
	// getString(R.string.toast_register_success),
	// Toast.LENGTH_SHORT).show();
	// // activity.phelper.putPassword(etPassword.getText().toString());
	// pContent.parseUserAndStoreToDb(response);
	// new PreferenceHelper(context).putPassword(etVerificationCode
	// .getText().toString());
	// // goToReffralCodeFragment(
	// // new PreferenceHelper(context).getUserId(),
	// // new PreferenceHelper(context).getSessionToken());
	//
	// } else {
	// Toast.makeText(getApplicationContext(),
	// getString(R.string.toast_register_failed),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	//
	// }

}
