package org.android.tmpdisk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class RootSetup {

	private static final String TAG = "ATD-RootSetup.java";

	private static RootSetup rs = null;
	
	private String bb_location;
	
	private String HOME_DIR;
	private Context context;
	
	public RootSetup(Context context) {
		this.context = context;
		setupExe();
	}

	private void setupExe() {
		Log.v(TAG, "Pocinjemo setupExe...");

		File dir = context.getDir("appexe", Context.MODE_WORLD_READABLE);
		bb_location = dir.getAbsolutePath() + "/busybox";
		
		HOME_DIR = dir.getAbsolutePath();

		// Setup BusyBox
		Log.v(TAG, "Setting up BusyBox");
		copyExeFromRaw(bb_location, R.raw.busybox);
		Log.v(TAG, "Setting up BusyBox completed");
		
		Log.v(TAG, "Zavrsvamo setupExe...");
	}
	
	private void copyExeFromRaw(String location, int id) {
		try {
			File dd = new File(location);
			InputStream istream = context.getResources().openRawResource(id);
			FileOutputStream ostream = new FileOutputStream(dd);
			BufferedInputStream bis = new BufferedInputStream(istream);
			BufferedOutputStream bos = new BufferedOutputStream(ostream);

			byte[] buff = new byte[1024];
			int read = 0;
			while ((read = bis.read(buff)) > 0) {
				bos.write(buff, 0, read);
			}

			bos.flush();
			bis.close();
			bos.close();

			dd.setExecutable(true, false);
			dd.setReadable(true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String get_bb_location() {
		return bb_location;
	}
	
	public String get_home_dir() {
		return HOME_DIR;
	}
	
	public Context getContext() {
		return context;
	}
	
	public static String static_nh_location(Context context) {
		File dir = context.getDir("appexe", Context.MODE_WORLD_READABLE);
		return dir.getAbsolutePath() + "/nohup.out";
	}
	
	public static RootSetup getRootSetup(Context context) {
		if (rs == null) rs = new RootSetup(context);
		else rs.context = context;
		return rs;
	}
	
	public static boolean isRooted() {
		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());

			os.writeBytes("echo imamo root\n");
			os.writeBytes("exit\n");
			os.flush();


			Log.v(TAG, "Waiting for p in isRooted()");
			p.waitFor();
			if (p.exitValue() == 255) 
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
