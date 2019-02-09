package com.wifi_demo;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	private static Button enable, disable, scan;
	private static ListView scannedWifi;

	private static WifiManager wifiManager;// Wifi Manager to manage wifi
	private static WifiReceiver receiverWifi; // Wifi broadcase receiver
	private static List<ScanResult> wifiList;// List to store scanned wifi's

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setListeners();
	}

	// Initialize all views
	private void init() {
		enable = (Button) findViewById(R.id.enable);
		disable = (Button) findViewById(R.id.disable);
		scan = (Button) findViewById(R.id.scan_wifi);
		scannedWifi = (ListView) findViewById(R.id.show_scaned_wifi);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); // Get
																			// wifi
																			// service
																			// to
																			// use
																			// Wifi

		receiverWifi = new WifiReceiver();// Broadcast receiver for wifi

	}

	// Setting click listener over buttons
	private void setListeners() {
		enable.setOnClickListener(this);
		disable.setOnClickListener(this);
		scan.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.enable:

			// Check if wifi is enabled or disabled and do according to it
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);// enable wifi
				Toast.makeText(MainActivity.this, "WIFI enabled.",
						Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(MainActivity.this, "WIFI already enabled.",
						Toast.LENGTH_SHORT).show();
			break;

		case R.id.disable:
			// If wifi is enabled then disable it
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);// disable wifi
				Toast.makeText(MainActivity.this, "WIFI disabled.",
						Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(MainActivity.this, "WIFI is not enabled.",
						Toast.LENGTH_SHORT).show();

			break;
		case R.id.scan_wifi:
			// If wifi is enabled then scan wifi's
			if (wifiManager.isWifiEnabled()) {
				// Register broadcast receiver
				// Broacast receiver will automatically call when number of wifi
				// connections changed
				registerReceiver(receiverWifi, new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				wifiManager.startScan();// Start wifi scan
			} else
				Toast.makeText(MainActivity.this, "WIFI is not enabled.",
						Toast.LENGTH_SHORT).show();

			break;
		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		// On destroy we have to unregister receiver
		try {
			unregisterReceiver(receiverWifi);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Broadcast receiver to scan new wifi
	class WifiReceiver extends BroadcastReceiver {

		// This method call when number of wifi connections changed and
		// disaplayed wifi connections over listview
		public void onReceive(Context c, Intent intent) {
			String action = intent.getAction();// Get sent action

			// If sent action is equal to scan results
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

				wifiList = wifiManager.getScanResults();// get scanned results
														// in list
				if (wifiList.size() > 0) {// if size is greater then 0 then show
											// wifi connection over listview

					Toast.makeText(
							MainActivity.this,
							"Number of Wifi connections found : "
									+ wifiList.size(), Toast.LENGTH_SHORT)
							.show(); // Toast to display no. of connections
					ArrayAdapter<ScanResult> adapter = new ArrayAdapter<ScanResult>(
							MainActivity.this,
							android.R.layout.simple_list_item_1, wifiList);
					scannedWifi.setAdapter(adapter);// Setting adapter over
													// listview
					adapter.notifyDataSetChanged();// Notify adapter
				} else {
					// If list size is 0 then show toast
					Toast.makeText(MainActivity.this, "No Wifi found.",
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	}

}
