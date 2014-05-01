package org.android.tmpdisk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class TmpdiskManager {

	private static final String TAG = "ATD-TmpdiskManager.java";
	RootSetup rs;
	String bb_location;
	
	public static final String mountlocation = "/data/tmp_disk";
	
	public TmpdiskManager(RootSetup rs) {
		this.rs = rs;
		bb_location = rs.get_bb_location();
	}
	
	public void mountTmpdisk() {
		Log.v(TAG, "Mounting temp disk...");
		if(isMounted()) {
			Log.v(TAG, "Tempdisk is mounted before. Skiping mounting.");
			return;
		}
		Process p = getps();
		DataOutputStream os = new DataOutputStream(p.getOutputStream());;
		try {
			os.writeBytes(bb_location + " mkdir " + mountlocation + "\n");
			os.writeBytes(bb_location + " mount -t tmpfs none " + mountlocation + "\n");
			os.writeBytes(bb_location + " chmod 1777 " + mountlocation + "\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitps(p);
		Log.v(TAG, "Mounting temp disk completed");
	}
	
	public void umountTmpdisk() {
		Log.v(TAG, "Umounting temp disk...");
		if(isMounted() == false) {
			Log.v(TAG, "Temp disk is not mounted");
			return;
		}
		Process p = getps();
		DataOutputStream os = new DataOutputStream(p.getOutputStream());;
		try {
			os.writeBytes(bb_location + " umount " + mountlocation + "\n");
			os.writeBytes(bb_location + " umount -r " + mountlocation + "\n");
			os.writeBytes(bb_location + " umount -f " + mountlocation + "\n");
			os.writeBytes(bb_location + " umount -l " + mountlocation + "\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitps(p);
		Log.v(TAG, "Umounting temp disk completed");
	}
	
	private Process getps() {
		return getps(true);
	}
	
	private Process getps(boolean isRoot) {
		Process p = null;
		try {
			// Preform su to get root privledges
			if(isRoot) {
				Log.v(TAG, "Creating root process");
				p = Runtime.getRuntime().exec("su");
			} else
				p = Runtime.getRuntime().exec(bb_location + " sh");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}
	
	private int waitps(Process p) {
		return waitps(p, false);
	}
	
	private int waitps(Process p, boolean onlywait) {
		int ev = -1;
		try {
			Log.v(TAG, "Waiting for p");
			p.waitFor();
			ev = p.exitValue();
		} catch (InterruptedException e) {
			p.destroy();
		}
		Log.v(TAG, "Process exit value: " + ev);
		if (onlywait) return ev;
		Log.v(TAG, "Printing streams: ");
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = bis.readLine()) != null) 
				Log.v(TAG, "Output stream: " + line);
			bis.close();

			bis = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = bis.readLine()) != null) 
				Log.v(TAG, "Error stream: " + line);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ev;
	}
	
	public boolean isMounted() {
		Process p = getps(false);
		DataOutputStream os = new DataOutputStream(p.getOutputStream());;
		try {
			os.writeBytes(bb_location + " mount\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitps(p, true);
		
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = bis.readLine()) != null) 
				if (line.contains(mountlocation)) {
					bis.close();
					return true;
				}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public DiskInfo getDiskInfo() {
		DiskInfo di = new DiskInfo();
		di.isMounted = isMounted();
		if (!di.isMounted)
			return di;
		Process p = getps(false);
		DataOutputStream os = new DataOutputStream(p.getOutputStream());;
		try {
			os.writeBytes(bb_location + " df -m " + mountlocation + "\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitps(p, true);
		
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			bis.readLine();
			line = bis.readLine();
			if (line != null) {
				String[] splited = line.split("\\s+");
				di.size = Integer.parseInt(splited[1]);
				di.used = Integer.parseInt(splited[2]);
				di.free = Integer.parseInt(splited[3]);
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return di;
	}
	
	public class DiskInfo {
		public boolean isMounted = false;
		public int size=0, used=0, free=0;
		
	}

}
