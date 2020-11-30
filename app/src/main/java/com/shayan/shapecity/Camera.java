package com.shayan.shapecity;

import android.opengl.Matrix;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVInterpolated;
import com.nurverek.vanguard.VLVProcessor;

public final class Camera{

    public static void rotateCamera(){
        VLVInterpolated v = new VLVInterpolated(0, 360, 1000, VLV.LOOP_FORWARD, VLV.INTERP_LINEAR);

        v.setTask(new VLTaskContinous(new VLTask.Task<VLVInterpolated>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask t, VLVInterpolated v){
                FSViewConfig c = FSControl.getViewConfig();
                c.eyePosition(0, 7F, 10F);

                float[] eyepos = c.eyePosition().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.rotateM(cache, 0, v.get(), 0f, 1f ,0f);
                Matrix.multiplyMV(eyepos, 0, cache, 0, eyepos, 0);

                c.eyePositionDivideByW();
                c.lookAt(0f, 0f, 0f, 0f, 1f, 0f);
                c.updateViewProjection();
            }
        }));

        VLVProcessor controlproc = FSRenderer.getControllersProcessor();
        controlproc.add(new VLVProcessor.Entry(v, 0));
        controlproc.start();
    }
}
