package com.shayan.shapecity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.GLES32;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderPass;
import com.nurverek.firestorm.FSRenderer;
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

        synchronized(FSRenderer.RENDERLOCK){
            FSControl.getSurfaceConfig().setKeepAlive(true);
            FSControl.setClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]);

            FSRenderer.enable(GLES32.GL_CULL_FACE);
            FSRenderer.enable(GLES32.GL_BLEND);
            FSRenderer.enable(GLES32.GL_DEPTH_TEST);

            FSRenderer.cullFace(GLES32.GL_BACK);
            FSRenderer.frontFace(GLES32.GL_CCW);
            FSRenderer.blendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA);
            FSRenderer.depthMask(true);

            Gen gen = new Gen();
            gen.assemble(this);

            FSRenderPass mainpass = new FSRenderPass(FSControl.DEBUG_FULL).build();
            mainpass.add(new FSRenderPass.Entry(gen, Gen.MAIN_PROGRAMSET));
            FSRenderer.addRenderPass(mainpass);
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
    public void GLPreDraw(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

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

        FSControl.getSurfaceConfig().setKeepAlive(false);
        destroy();
        System.gc();
    }

    @Override
    protected void destroy(){
        super.destroy();
    }
}
