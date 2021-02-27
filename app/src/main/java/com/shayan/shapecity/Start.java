package com.shayan.shapecity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.GLES32;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSEvents;
import com.nurverek.firestorm.FSRPass;
import com.nurverek.firestorm.FSR;
import com.nurverek.firestorm.FSViewConfig;

public class Start extends FSActivity{

    @Override
    public void modifyUI(final RelativeLayout base){
        try{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            base.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            base.setX(0);
            base.setY(0);
            base.setBackgroundColor(Color.argb(255, 255, 255, 255));
            base.setFitsSystemWindows(false);

        }catch(Exception ex){
            ex.printStackTrace();
            finish();
        }
    }

    @Override
    protected FSEvents createEvents(){
        return new Events();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        FSControl.setKeepAlive(false);
        destroy();
        System.gc();
    }

    @Override
    protected void destroy(){
        super.destroy();
    }
}
