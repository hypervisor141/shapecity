package com.shayan.shapecity;

import android.opengl.Matrix;
import android.util.Log;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVTypeManager;
import com.nurverek.vanguard.VLVariable;

public final class Camera{

    private static VLVLinear controlcamera;
    private static VLVRunner control;

    public static void rotateCamera(){
        VLVCurved controlcamera1 = new VLVCurved(0, 360, 300, VLVariable.LOOP_FORWARD_BACKWARD, VLVCurved.CURVE_DEC_SINE, new VLTaskContinous(new VLTask.Task<VLVCurved>(){

            @Override
            public void run(VLTask<VLVCurved> task, VLVCurved var){
//                FSControl.getViewConfig().eyePosition(0F, 500 + var.get() * 15, 500 + var.get() * 15);
            }
        }));

        controlcamera = new VLVLinear(0, 360, 3000, VLVariable.LOOP_FORWARD, new VLTaskContinous(new VLTask.Task<VLVLinear>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask<VLVLinear> task, VLVLinear var){
                FSViewConfig c = FSControl.getViewConfig();
//                c.eyePosition(0F, 5F, 5F);
//                c.eyePosition(0F, 1000, 1000);
                c.eyePosition(0F, 6000, 6000);
//                c.eyePosition(0F, 4000F, 6000F);

                float[] eyepos = c.eyePosition().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.rotateM(cache, 0, var.get(), 0f, 1f, 0f);
                Matrix.multiplyMV(eyepos, 0, cache, 0, eyepos, 0);

                c.eyePositionDivideByW();
                c.lookAt(0f, 2.5f, 0f, 0f, 1f, 0f);
                c.updateViewProjection();
            }
        }));

        control = new VLVRunner(2, 0);
        control.add(new VLVRunnerEntry(controlcamera1, 0));
        control.add(new VLVRunnerEntry(controlcamera, 0));
        control.start();

        FSRenderer.getControlManager().add(control);
    }
}
