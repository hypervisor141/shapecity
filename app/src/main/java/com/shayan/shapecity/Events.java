package com.shayan.shapecity;

import android.app.Activity;
import android.opengl.GLES32;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSEvents;
import com.nurverek.firestorm.FSR;
import com.nurverek.firestorm.FSRPass;
import com.nurverek.firestorm.FSViewConfig;

public class Events extends FSEvents{

    protected static final float[] BG_COLOR = new float[]{ 0.05f, 0.05f, 0.05f, 1f };

    public Events(){

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

            Activity act = FSControl.getActivity();

            Gen gen = new Gen();
            gen.assemble(act);

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
}
