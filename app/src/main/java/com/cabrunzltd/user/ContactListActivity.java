package com.cabrunzltd.user;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactListActivity extends Activity {

	static ContentResolver cr;
	String[] phone_nos;
	String[] names;
	ArrayList<String> selectedContacts = new ArrayList<String>();
	Activity thisActivity;
	Button btn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);
		thisActivity = this;
		setTitle("Select Contacts");
		final ListView lst = (ListView) findViewById(R.id.contact_listview);
		populateContact();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisActivity,
				android.R.layout.simple_list_item_multiple_choice, names);
		lst.setAdapter(adapter);
		lst.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		final int len = lst.getCount();
		final SparseBooleanArray checked = lst.getCheckedItemPositions();

		btn = (Button) findViewById(R.id.btn_contact_send);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < len; i++)
					if (checked.get(i)) {
						selectedContacts.add(phone_nos[i]);
						// you can you this array list to next activity
						/* do whatever you want with the checked item */
					}
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("sel_contacts", selectedContacts);

				Intent contactIntent = new Intent();
				contactIntent.putExtras(bundle);
				setResult(1, contactIntent);
				thisActivity.finish();
				// Log.v("", "selected-->"+selectedContacts);
			}
		});

	}

	private void populateContact() {
		Uri myContacts = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor mqCur = managedQuery(myContacts, null, null, null, null);
		phone_nos = new String[mqCur.getCount()];
		names = new String[mqCur.getCount()];
		int i = 0;
		if (mqCur.moveToFirst()) {
			do {
				String name=mqCur
						.getString(mqCur
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				names[i]=name;
				
				String phone_no = mqCur
						.getString(mqCur
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phone_nos[i] = phone_no;
				i++;
			}

			while (mqCur.moveToNext());
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
