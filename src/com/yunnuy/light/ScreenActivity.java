package com.yunnuy.light;

import com.yunnuy.light.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class ScreenActivity extends Activity{
	private TextView textView;
	private WakeLock mWakeLock;
	private float posX = 0;
	private int bgId = 0; //the current background drawable resource id
	private float oldBrightness = 0.5f;
	private static final String TAG = "ScreenActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen);
        textView = (TextView) findViewById(R.id.front);
        bgId = R.drawable.light_on;
        
        oldBrightness = getWindow().getAttributes().screenBrightness;
        
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "lightWk");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		setBrightness(0.9f);
		if(bgId == R.drawable.light_on){
			Log.d("ScreenActivity", "onResume");
			mWakeLock.acquire();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		setBrightness(oldBrightness);
		if(bgId == R.drawable.light_on){
			Log.d("ScreenActivity", "onPause");
			mWakeLock.release();
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
					if(bgId == R.drawable.light_on){
						textView.setBackgroundResource(R.drawable.light_off);
						textView.setText(R.string.front_off);
						bgId = R.drawable.light_off;
						mWakeLock.release();
					}else{
						textView.setBackgroundResource(R.color.white);
						textView.setText(R.string.front_on);
						bgId = R.drawable.light_on;
						mWakeLock.acquire();
					}
				}
		}
		return true;
	}
	
	/**
	 * set screen brightness, 0.0 dark, 1.0 bright
	 * @date 2011-5-15
	 * @param f 
	 * @return void
	 * @throws
	 */
	private void setBrightness(float f){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = f;   
		getWindow().setAttributes(lp);
		Log.d(TAG, "the screen brightness is set to: " + f);
	}
	
	/** 
     * 判断是否开启了自动亮度调节 
     * 
     * @param aContext 
     * @return 
     */ 
    public static boolean isAutoBrightness(ContentResolver aContentResolver) { 
        boolean automicBrightness = false; 
        try { 
        	//need api 8,but my project is api 7.
            automicBrightness = Settings.System.getInt(aContentResolver, 
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; 
        } catch (SettingNotFoundException e) { 
            e.printStackTrace(); 
        } 
        return automicBrightness; 
    } 
}
