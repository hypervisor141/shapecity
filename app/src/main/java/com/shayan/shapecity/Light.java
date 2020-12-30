package com.shayan.shapecity;

import android.opengl.Matrix;

import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Light{

    private final static float[] CACHE = new float[16];
    private static VLVLinear controldirect;
    private static VLVLinear controlpoint;

    public static void rotatePointLight(){
        final float[] orgpos = Loader.light.position().provider().clone();

        controlpoint = new VLVLinear(0, 360, 500, VLVariable.LOOP_FORWARD, new VLTaskContinous(new VLTask.Task<VLVLinear>(){

            @Override
            public void run(VLTask<VLVLinear> task, VLVLinear var){
                float[] pos = Loader.light.position().provider();

                Matrix.setIdentityM(CACHE, 0);
                Matrix.rotateM(CACHE, 0, var.get(), 0f, 1f, 0f);
                Matrix.multiplyMV(pos, 0, CACHE, 0, orgpos, 0);

                pos[0] /= pos[3];
                pos[1] /= pos[3];
                pos[2] /= pos[3];

//                Loader.shadow.updateLightVP();
            }
        }));

        Animations.controlrunner.add(new VLVRunnerEntry(controlpoint, 0));
        Animations.controlrunner.start();
    }
}
