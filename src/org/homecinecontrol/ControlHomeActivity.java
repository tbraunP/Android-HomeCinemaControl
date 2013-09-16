package org.homecinecontrol;

import java.util.ArrayList;

import org.homecinecontrol.controlHome.ControlElementsAdapter;
import org.homecinecontrol.controlHome.controlElements.ConcreteDemoControlElement;
import org.homecinecontrol.controlHome.controlElements.ControlElement;
import org.homecinecontrol.protocol.connection.Connection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ControlHomeActivity extends Activity {

	public static final String APP = "org.homecinecontrol";

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private Connection con;
	private ArrayAdapter<ControlElement> controlAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control_home, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_home);

		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		// StrictMode.setThreadPolicy(policy);

		final ListView listview = (ListView) findViewById(R.id.listViewElements);
		ConcreteDemoControlElement[] values = new ConcreteDemoControlElement[] {
				new ConcreteDemoControlElement("A"),
				new ConcreteDemoControlElement("B") };

		final ArrayList<ControlElement> list = new ArrayList<ControlElement>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}

		// set adapter for list
		controlAdapter = new ControlElementsAdapter(this, list);
		listview.setAdapter(controlAdapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final ConcreteDemoControlElement item = (ConcreteDemoControlElement) parent
						.getItemAtPosition(position);
				item.onClick();
			}

		});

		// try to establish the connection to the control interface
		if (!isNetworkAvailable()) {
			Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG)
					.show();
			Log.e(ControlHomeActivity.APP, "No Network");
		} else {
			con = Connection.getInstance();
			con.setContext(getBaseContext());
			con.connect("192.168.1.105");
			Log.e(ControlHomeActivity.APP, "Connection");
		}
	}

	public void updateElements() {
		controlAdapter.notifyDataSetChanged();
	}
}
