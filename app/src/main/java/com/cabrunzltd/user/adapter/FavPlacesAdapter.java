package com.cabrunzltd.user.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cabrunzltd.user.models.FavoritePlace;
import com.cabrunzltd.user.R;

import java.util.ArrayList;

public class FavPlacesAdapter extends BaseAdapter {

	private ArrayList<FavoritePlace> favPlaces;
	private Activity mActivity;
	
	public FavPlacesAdapter(ArrayList<FavoritePlace> favPlaces, Activity activity) {
		this.favPlaces = favPlaces;
		this.mActivity = activity;
	}
	
	@Override
	public int getCount() {
		return favPlaces.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		if(getCount() != 0) {
			return getCount();
		}
		
		return 1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View favPlacesListLayout = mActivity.getLayoutInflater().inflate(R.layout.fav_places_list_layout, parent,
				false);
		TextView tvName = (TextView) favPlacesListLayout.findViewById(R.id.fav_place_name);
		tvName.setText(this.favPlaces.get(position).getName());
		TextView tvAddress = (TextView) favPlacesListLayout.findViewById(R.id.fav_place_address);
		tvAddress.setText(this.favPlaces.get(position).getAddress()); 
		TextView tvId = (TextView) favPlacesListLayout.findViewById(R.id.fav_place_id);
		tvId.setText(this.favPlaces.get(position).getId());
		return favPlacesListLayout;
	}

}
