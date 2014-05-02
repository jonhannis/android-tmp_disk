package org.android.tmpdisk;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	RootSetup rs;
	TmpdiskManager tdm;
	
	TextView txtStatus, txtSize, txtUsed, txtFree;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			TmpdiskManager.DiskInfo di = tdm.getDiskInfo();
			if(di.isMounted)
				txtStatus.setText("Current status: mounted");
			else
				txtStatus.setText("Current status: umounted");
			txtSize.setText("Disk size: " + di.size + "MB");
			txtUsed.setText("Used: " + di.used + "MB");
			txtFree.setText("Free: " + di.free + "MB");
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		**/
		rs = RootSetup.getRootSetup(this);
		tdm = new TmpdiskManager(rs);
		
		txtStatus = (TextView)findViewById(R.id.txtStatus);
		txtSize = (TextView)findViewById(R.id.txtSize);
		txtUsed = (TextView)findViewById(R.id.txtUsed);
		txtFree = (TextView)findViewById(R.id.txtFree);
		
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void btnMountClick(View v) {
		tdm.mountTmpdisk();
	}
	
	public void btnUmountClick(View v) {
		tdm.umountTmpdisk();
	}
	
	public void btnWipeClick(View v) {
		tdm.wipeTmpdisk();
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
