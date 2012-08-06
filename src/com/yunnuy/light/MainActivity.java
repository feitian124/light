package com.yunnuy.light;

import com.yunnuy.light.R;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
	public static final String TAB = "tab";
	public static boolean ledOn = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
               
        setContentView(R.layout.main);
        TabHost mTabHost = getTabHost();
        Resources rs = getResources();
        String fLab = rs.getString(R.string.front_label);
        String bLab = rs.getString(R.string.back_label);
        String hLab = rs.getString(R.string.help_label);
        
        mTabHost.addTab(mTabHost.newTabSpec(fLab)   
                .setIndicator(fLab)  
                .setContent(new Intent(MainActivity.this, ScreenActivity.class))); 
       
        mTabHost.addTab(mTabHost.newTabSpec(bLab)   
                .setIndicator(bLab)  
                .setContent(new Intent(MainActivity.this, FlashActivity.class))); 
        
       mTabHost.addTab(mTabHost.newTabSpec(hLab)   
                .setIndicator(hLab)  
                .setContent(new Intent(MainActivity.this, HelpActivity.class)));
        
        //如果是来自notification，则跳转到led的tab页
        Intent intent=getIntent(); 
        String value=intent.getStringExtra(TAB); 
        if("2".equals(value)){
        	ledOn = true;
        	mTabHost.setCurrentTab(1);
        }
        
    	/*应用Id	,应用密码,广告请求间隔(s),测试模式 , 应用版本号*/
		AdManager.init(this,
					   "91807e62b9a2d13a", 
					   "8b530a32ada1c772",
					   31,              
					   false);
    	//初始化广告视图
    	AdView adView = new AdView(this,Color.GRAY, Color.WHITE, 100);       		
	 
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		
		//设置广告出现的位置(悬浮于底部)		
		params.bottomMargin=0;
		params.gravity=Gravity.BOTTOM; 
 
		//将广告视图加入Activity中
		addContentView(adView, params); 
    }


}
