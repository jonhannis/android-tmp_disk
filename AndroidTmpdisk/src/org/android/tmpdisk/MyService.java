package org.android.tmpdisk;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyService extends Service implements Runnable {
	public static final String TAG = "ATD-MyService.java";
	
	private boolean isstarted = false;
	private boolean chargestatus;
	
	private Handler mHandler;
	
	private RootSetup rs;
	TmpdiskManager tdm;
	
	public MyService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.v(TAG, "Service onCreate");
		
		rs = RootSetup.getRootSetup(this);
		tdm = new TmpdiskManager(rs);
		
		setupHandler();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		Log.v(TAG, "onStartCommand: " + startId);
		if(!(isstarted)) {
			isstarted = true;
			chargestatus = isPlugged();
			mHandler.sendEmptyMessageDelayed(0, 5*1000);
		}
		return START_STICKY;
	}
	
	private void setupHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				boolean newstatus = isPlugged();
				Log.d(TAG, "charge status: " + chargestatus + " / " + newstatus);
				if (newstatus == chargestatus) {
					if(newstatus)
						tdm.mountTmpdisk();
					else
						tdm.umountTmpdisk();
				}
				MyService.this.stopSelf();
			}
		};
	}

	public boolean isPlugged()
	{
	    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    Intent batteryStatus = this.registerReceiver(null, ifilter);
	    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	    boolean bCharging = status == BatteryManager.BATTERY_PLUGGED_AC ||
	                         status == BatteryManager.BATTERY_PLUGGED_USB;
	    return bCharging;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
