package com.yunnuy.light;

import java.lang.reflect.Method;

import com.yunnuy.light.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class FlashActivity extends Activity {
	TextView view;
	TextView tip;
	private int bgId = 0;
	private float posX = 0;
	private Led led = null;
	private Notification notification;
	private NotificationManager nm;
	private static final int flashIsOn = 1;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash);
		view = (TextView) findViewById(R.id.back);
		tip = (TextView) findViewById(R.id.back_text);
		
		try{
			led = new Led();
		}catch (Exception e) {
			Toast.makeText(this, this.getResources().getString(R.string.openFlashError), Toast.LENGTH_LONG).show();
		}
		if(MainActivity.ledOn){
			view.setBackgroundResource(R.drawable.flash_on);
			tip.setText(R.string.led_on);
			bgId = R.drawable.flash_on;
		}
		initNotify();
	
	}
	
	private void initNotify(){
		long when = System.currentTimeMillis();         // notification time
		Context context = getApplicationContext();      // application Context
		Resources rs = this.getResources();
		CharSequence tickerText = rs.getString(R.string.notify_ticker);              // ticker-text
		CharSequence contentTitle = rs.getString(R.string.notify_expand_title);  // expanded message title
		CharSequence contentText = rs.getString(R.string.notify_expand_content);      // expanded message text

		Intent intent = new Intent();
		intent.putExtra(MainActivity.TAB, "2");
		intent.setClass(FlashActivity.this, MainActivity.class);
//		intent.setAction(String.valueOf(when));
		PendingIntent contentIntent = PendingIntent.getActivity(FlashActivity.this, 0, intent, 0);

		// the next two lines initialize the Notification, using the configurations above
		notification = new Notification(R.drawable.notify, tickerText, when);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private void switchFlash(boolean b){
		if(!led.enable(b)) return; //if invoke failed, no hint need to change
		if(b){
			nm.notify(flashIsOn, notification);
			view.setBackgroundResource(R.drawable.flash_on);
			tip.setText(R.string.led_on);
			bgId = R.drawable.flash_on;
		}else{
			nm.cancel(flashIsOn);
			view.setBackgroundResource(R.drawable.flash_off);
			tip.setText(R.string.led_off);
			bgId = R.drawable.flash_off;
		}
	}
	
	/**
	 * switch light on or off, contains two step:
	 * switch background image, acquire/release wakeLock
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = 0;
		
		switch(action){
			case MotionEvent.ACTION_DOWN:
				 posX = event.getX();
				break;
			
			case MotionEvent.ACTION_UP:
				x = event.getX();
				if( Math.abs(x - posX) > 5){
					if(bgId == R.drawable.flash_on){
						switchFlash(false);
					}else{
						switchFlash(true);
					}
				}
		}
		return true;
	}
	
	/**
	 * get the idea from http://simpleled.googlecode.com/svn›trunk›src›com›droidled›demo›DroidLED.java
	 */
	class Led {
		private Object svc = null;
		private Method getFlashlightEnabled = null;
		private Method setFlashlightEnabled = null;

		@SuppressWarnings("rawtypes")
		public Led() throws Exception {
			try {
				// call ServiceManager.getService("hardware") to get an IBinder
				// for the service.
				// this appears to be totally undocumented and not exposed in
				// the SDK whatsoever.
				Class sm = Class.forName("android.os.ServiceManager");
				Object hwBinder = sm.getMethod("getService", String.class)
						.invoke(null, "hardware");

				// get the hardware service stub. this seems to just get us one
				// step closer to the proxy
				Class hwsstub = Class
						.forName("android.os.IHardwareService$Stub");
				Method asInterface = hwsstub.getMethod("asInterface",
						android.os.IBinder.class);
				svc = asInterface.invoke(null, (IBinder) hwBinder);

				// grab the class (android.os.IHardwareService$Stub$Proxy) so we
				// can reflect on its methods
				Class proxy = svc.getClass();

				// save methods
				getFlashlightEnabled = proxy.getMethod("getFlashlightEnabled");
				setFlashlightEnabled = proxy.getMethod("setFlashlightEnabled",
						boolean.class);
			} catch (Exception e) {
				throw new Exception("LED could not be initialized");
			}
		}
		
		//在模拟器上不能用，真机上没试
		public boolean isEnabled() {
			try {
				return getFlashlightEnabled.invoke(svc).equals(true);
			} catch (Exception e) {
				Log.e("Led", "invoke isEnabled error!", e);
				return false;
			}
		}
		
		public boolean enable(boolean tf) {
			try {
				setFlashlightEnabled.invoke(svc, tf);
				return true;
			} catch (Exception e) {
				Log.e("Led", " set led " + tf + " error!", e);
				return false;
			}
		}
	}
	
}
