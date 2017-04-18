/**
 * 
 */
package com.cabrunzltd.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.cabrunzltd.user.adapter.HistoryAdapter;
import com.cabrunzltd.user.models.History;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import de.halfbit.pinnedsection.PinnedSectionListView;
//import com.hb.views.PinnedSectionListView;

/**
 * @author Kishan H Dhamat
 * 
 */
public class HistoryActivity extends ActionBarBaseActivitiy implements
		OnItemClickListener {
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private PinnedSectionListView lvHistory;
	private HistoryAdapter historyAdapter;
	private ArrayList<History> historyList;
	private ArrayList<History> historyListOrg;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private ImageView tvNoHistory;
	private ArrayList<Date> dateList = new ArrayList<Date>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setTitle(getString(R.string.text_history));
		setIconMenu(R.drawable.ub__nav_history);



		setIcon(R.drawable.icon_back);
		lvHistory = (PinnedSectionListView) findViewById(R.id.lvHistory);
		lvHistory.setOnItemClickListener(this);
		historyList = new ArrayList<History>();

		tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
		//
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setTitle(getString(R.string.text_history));
		preferenceHelper = new PreferenceHelper(this);
		parseContent = new ParseContent(this);
		dateList = new ArrayList<Date>();
		historyListOrg = new ArrayList<History>();
//		historyAdapter = new HistoryAdapter(this, historyListOrg,
//				mSeparatorsSet);
//		lvHistory.setAdapter(historyAdapter);
		getHistory();

	}

	/**
	 * 
	 */
	private void getHistory() {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.progress_getting_history),
				false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.HISTORY + Const.Params.ID + "="
				+ preferenceHelper.getUserId() + "&" + Const.Params.TOKEN + "="
				+ preferenceHelper.getSessionToken());
		
		AppLog.Log("History", Const.ServiceType.HISTORY + Const.Params.ID + "="
				+ preferenceHelper.getUserId() + "&" + Const.Params.TOKEN + "="
				+ preferenceHelper.getSessionToken());
		
		new HttpRequester(this, map, Const.ServiceCode.HISTORY, true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		if (mSeparatorsSet.contains(position))
			return;
		History history = historyListOrg.get(position);

//		showBillDialog(history.getTimecost(), history.getTotal(),
//				history.getDistanceCost(), history.getBasePrice(),
//
//				history.getTime(), history.getDistance(),
//				history.getCurrency(), null, "", "", "", "", "",
//				history.getActual_total(), ""); // any prob
		// keep only
		// till null
		// and check

		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uberdriverforx.parse.AsyncTaskCompleteListener#onTaskCompleted(java
	 * .lang.String, int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.HISTORY:
			Log.e("mahi", "history done" + response);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Calendar cal = Calendar.getInstance();
				historyListOrg.clear();
				historyList.clear();
				dateList.clear();
				parseContent.parseHistory(response, historyList);

				Collections.sort(historyList, new Comparator<History>() {
					@Override
					public int compare(History o1, History o2) {

						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						try {

							String firstStrDate = o1.getDate();
							String secondStrDate = o2.getDate();

							Date date2 = dateFormat.parse(secondStrDate);
							Date date1 = dateFormat.parse(firstStrDate);
							return date2.compareTo(date1);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return 0;

					}
				});

				HashSet<Date> listToSet = new HashSet<Date>();
				for (int i = 0; i < historyList.size(); i++) {
					AppLog.Log("date", historyList.get(i).getDate() + "");
					if (listToSet.add(sdf.parse(historyList.get(i).getDate()))) {
						dateList.add(sdf.parse(historyList.get(i).getDate()));
					}

				}

				for (int i = 0; i < dateList.size(); i++) {

					cal.setTime(dateList.get(i));
					History item = new History();
					item.setDate(sdf.format(dateList.get(i)));
					historyListOrg.add(item);

					mSeparatorsSet.add(historyListOrg.size() - 1);
					for (int j = 0; j < historyList.size(); j++) {
						Calendar messageTime = Calendar.getInstance();
						messageTime.setTime(sdf.parse(historyList.get(j)
								.getDate()));
						if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
							historyListOrg.add(historyList.get(j));
						}
					}
				}

				if (historyList.size() > 0) {
					lvHistory.setVisibility(View.VISIBLE);
					tvNoHistory.setVisibility(View.GONE);
				} else {
					lvHistory.setVisibility(View.GONE);
					tvNoHistory.setVisibility(View.VISIBLE);
				}
				Log.e("historyListOrg size  ", "" + historyListOrg.size());

				historyAdapter = new HistoryAdapter(this, historyListOrg,
						mSeparatorsSet);
				lvHistory.setAdapter(historyAdapter);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;

		default:
			break;
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
