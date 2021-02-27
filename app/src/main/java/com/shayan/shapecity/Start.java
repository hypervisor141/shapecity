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
import com.nurverek.firestorm.FSRPass;
import com.nurverek.firestorm.FSR;
import com.nurverek.firestorm.FSViewConfig;

public class Start extends FSActivity{

    protected static final float[] BG_COLOR = new float[]{ 0.05f, 0.05f, 0.05f, 1f };

    protected static Runnable POSTPERMISSIONS;
    private static String CROPPED_PIC_LOCATION;

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
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void GLPostCreated(boolean continuing){
        if(continuing){
            return;
        }

        synchronized(FSR.RENDERLOCK){
            FSControl.setKeepAlive(true);
            FSControl.setClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]);

            FSR.enable(GLES32.GL_CULL_FACE);
            FSR.enable(GLES32.GL_BLEND);
            FSR.enable(GLES32.GL_DEPTH_TEST);

            FSR.cullFace(GLES32.GL_BACK);
            FSR.frontFace(GLES32.GL_CCW);
            FSR.blendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA);
            FSR.depthMask(true);

            Gen gen = new Gen();
            gen.assemble(this);

            FSRPass mainpass = new FSRPass(FSControl.DEBUG_FULL).build();
            mainpass.add(new FSRPass.Entry(gen, Gen.MAIN_PROGRAMSET));
            FSR.addRenderPass(mainpass);
        }
    }

    @Override
    public void GLPreChange(int width, int height){
        FSViewConfig config = FSControl.getViewConfig();
        config.setPerspectiveMode();
        config.viewPort(0, 0, width, height);
        config.perspective(70f, (float)width / height, 0.1F, 10000F);
        config.updateViewPort();
        config.updateViewProjection();
    }

    @Override
    public void GLPostSurfaceDestroy(){
        Log.d("wtf", "CALLED : " + FSControl.getKeepAlive());
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public void onClick(View v){

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

        Log.d("wtf", "CALLED");

        FSControl.setKeepAlive(false);
        destroy();
        System.gc();
    }

    @Override
    protected void destroy(){
        super.destroy();
    }
}
