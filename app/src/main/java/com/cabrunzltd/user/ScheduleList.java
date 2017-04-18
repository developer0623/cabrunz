package com.cabrunzltd.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.cabrunzltd.user.adapter.ScheduleAdapter;
import com.cabrunzltd.user.models.schedule;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleList extends ActionBarBaseActivitiy implements
		OnClickListener, AsyncTaskCompleteListener {
	private ListView lvtrips;
	private Button btnschdule, btnreccur;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private ArrayList<schedule> scheduleList;
	private ScheduleAdapter scheduleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_list);
		setTitle("Schedule List");
		setIconMenu(R.drawable.ub__nav_history);
		setIcon(R.drawable.icon_back);
		btnschdule = (Button) findViewById(R.id.btnschdule);
		btnreccur = (Button) findViewById(R.id.btnreccur);
		lvtrips = (ListView) findViewById(R.id.lvtrips);
		btnreccur.setOnClickListener(this);
		btnschdule.setOnClickListener(this);
		preferenceHelper = new PreferenceHelper(this);
		parseContent = new ParseContent(this);
		scheduleList = new ArrayList<schedule>();
		btnschdule.setTextColor(getResources().getColor(R.color.black));
		getscheduletrip();

	}

	private void getscheduletrip() {
		// TODO Auto-generated method stub

		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.progress_loading), false,
				null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SHOW_REQ_LTR + Const.Params.ID
				+ "=" + preferenceHelper.getUserId() + "&" + Const.Params.TOKEN
				+ "=" + preferenceHelper.getSessionToken());

		

		new HttpRequester(this, map, Const.ServiceCode.SHOW_REQ_LTR, true, this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;
		case R.id.btnreccur:

			btnreccur.setTextColor(getResources().getColor(R.color.black));
			btnschdule
					.setTextColor(getResources().getColor(R.color.color_text));
			getreccurlst();

			break;
		case R.id.btnschdule:

			btnreccur.setTextColor(getResources().getColor(R.color.color_text));
			btnschdule.setTextColor(getResources().getColor(R.color.black));

			getscheduletrip();
			break;

		default:
			break;
		}

	}

	private void getreccurlst() {
		// TODO Auto-generated method stub

		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.progress_loading), false,
				null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SHOW_RECUUR + Const.Params.ID
				+ "=" + preferenceHelper.getUserId() + "&" + Const.Params.TOKEN
				+ "=" + preferenceHelper.getSessionToken());
		Log.d("mahi", "map sned" + map);

		new HttpRequester(this, map, Const.ServiceCode.SHOW_RECUUR, true, this);

	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.SHOW_REQ_LTR:

			if (parseContent.isSuccess(response)) {

				try {
					parseContent.parseschedule(response, scheduleList);
					scheduleAdapter = new ScheduleAdapter(this, scheduleList);
					lvtrips.setAdapter(scheduleAdapter);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		case Const.ServiceCode.SHOW_RECUUR:
			Log.d("mahi", "map recive" + response);

			if (parseContent.isSuccess(response)) {
				

				try {
					parseContent.parseschedulerecc(response, scheduleList);
					scheduleAdapter = new ScheduleAdapter(this, scheduleList);
					lvtrips.setAdapter(scheduleAdapter);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		default:
			break;
		}
	}

}
