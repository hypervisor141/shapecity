package com.shayan.shapecity;

import android.opengl.Matrix;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVariable;

public final class Camera{

    private static VLVLinear controlcamera;

    public static void rotateCamera(){
//        controlcamera = new VLVLinear(0, 360, 1250, VLVariable.LOOP_FORWARD, new VLTaskContinous(new VLTask.Task<VLVLinear>(){
//
//            private float[] cache = new float[16];
//
//            @Override
//            public void run(VLTask<VLVLinear> task, VLVRunner runner, VLVLinear var){
//                FSViewConfig c = FSControl.getViewConfig();
//                c.eyePosition(0, 7F, 5F);
//
//                float[] eyepos = c.eyePosition().provider();
//
//                Matrix.setIdentityM(cache, 0);
//                Matrix.rotateM(cache, 0, var.get(), 0f, 1f, 0f);
//                Matrix.multiplyMV(eyepos, 0, cache, 0, eyepos, 0);
//
//                c.eyePositionDivideByW();
//                c.lookAt(0f, 0f, 0f, 0f, 1f, 0f);
//                c.updateViewProjection();
//            }
//        }));
//
//        VLVRunner controlproc = FSRenderer.getControlRunners();
//        controlproc.add(new VLVRunner.EntryVar(controlcamera, 0));
//        controlproc.start();
    }
}
