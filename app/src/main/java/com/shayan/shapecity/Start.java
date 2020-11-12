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

import javax.security.auth.DestroyFailedException;

public class Start extends FSActivity {

    protected static final float[] BG_COLOR = new float[]{ 0.1f, 0.1f, 0.1f, 1f }; //grey

    protected static Runnable POSTPERMISSIONS;
    private static String CROPPED_PIC_LOCATION;

    @Override
    public void modifyUI(final RelativeLayout base){
        try{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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

            Loader loader = new Loader();
            loader.initialize(this);

            FSRenderPass mainpass = new FSRenderPass().build();
            FSRenderPass shadowpass = new FSRenderPass().setClearDepth(false).setAdvanceProcessors(false).setRunTasks(false).build();

            shadowpass.add(new FSRenderPass.Entry(loader, 0));
            mainpass.add(new FSRenderPass.Entry(loader, 1));

            FSRenderer.addRenderPass(shadowpass);
            FSRenderer.addRenderPass(mainpass);
        }
    }

    @Override
    public void GLPreChange(int width, int height){
        FSViewConfig scene = FSControl.getViewConfig();
        scene.setPerspectiveMode();
        scene.viewPort(0, 0, width, height);
        scene.perspective(10f, (float)width / height, 1F, 1500F);
        scene.updateViewPort();

        scene.eyePosition(0f, 40f, -0.01f);
        scene.lookAt(0f, 0f, 0f, 0f, 1f, 0f);
        scene.updateViewProjection();

//        VLVInterpolated v = new VLVInterpolated(0, 360, 1500, VLV.LOOP_FORWARD, VLV.INTERP_LINEAR);
//
//        v.setTask(new VLTaskContinous(new VLTask.Task(){
//
//            private float[] cache = new float[16];
//
//            @Override
//            public void run(VLTask t, VLVConst v){
//                FSViewConfig c = FSControl.getViewConfig();
//                c.eyePosition(0, 10, 0.01F);
//
//                float[] eyepos = c.eyePosition().provider();
//
//                Matrix.setIdentityM(cache, 0);
//                Matrix.rotateM(cache, 0, v.get(), 0f, 1f ,0f);
//                Matrix.multiplyMV(eyepos, 0, cache, 0, eyepos, 0);
//
//                c.eyePositionDivideByW();
//                c.lookAt(0f, 0, 0f, 0f, 1f, 0f);
//                c.updateViewProjection();
//            }
//        }));
//
//        VLVProcessor controlproc = FSRenderer.getControllersProcessor();
//        controlproc.add(new VLVProcessor.Entry(v, 0));
//        controlproc.activateLatest();
//        controlproc.start();
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
