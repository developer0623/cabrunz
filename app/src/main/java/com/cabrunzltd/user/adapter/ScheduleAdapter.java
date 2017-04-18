package com.cabrunzltd.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cabrunzltd.user.models.schedule;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.cabrunzltd.user.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ScheduleAdapter extends BaseAdapter {
	private ArrayList<schedule> scheduleList;
	private LayoutInflater inflater;
	private PreferenceHelper preferenceHelper;
	private ViewHolder holder;
	private Activity activity;
	private int pos;
	SimpleDateFormat simpleDateFormat;

	public ScheduleAdapter(Activity activity, ArrayList<schedule> scheduleList) {
		this.scheduleList = scheduleList;
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		preferenceHelper = new PreferenceHelper(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scheduleList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.schedulelist_item, parent,
					false);
			holder = new ViewHolder();
			holder.tvdate = (TextView) convertView.findViewById(R.id.tvredate);
			holder.tvtime = (TextView) convertView.findViewById(R.id.tvretime);
			holder.tvpickplace = (TextView) convertView
					.findViewById(R.id.tvsource);
			holder.tvdropplace = (TextView) convertView
					.findViewById(R.id.tvdesti);
			holder.tvreqnum = (TextView) convertView.findViewById(R.id.tvreqid);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvdate.setText(formatDate(scheduleList.get(position)
				.getReq_date()));
		holder.tvtime.setText(formatTime(scheduleList.get(position)
				.getReq_time()));
		holder.tvreqnum.setText(scheduleList.get(position).getId());
		holder.tvpickplace.setText(scheduleList.get(position).getS_address());
		holder.tvdropplace.setText(scheduleList.get(position).getD_address());

		return convertView;
	}

	private class ViewHolder {
		TextView tvdate, tvtime, tvpickplace, tvdropplace, tvreqnum;

	}

	private String formatTime(String strDate) {
		Log.i("String Date", strDate);
		Date time = new Date();
		try {
			time = simpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime());
		return sdf.format(cal.getTime());

	}

	private String formatDate(String strDate) {
		Log.i("String Date", strDate);
		Date time = new Date();
		try {
			time = simpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime());
		return sdf.format(cal.getTime());

	}

}
