package com.shayan.shapecity;

import android.opengl.Matrix;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Camera{

    private static final float DISTANCE_FROM_PLATFORM_ASCEND = 5F;
    private static final float DISTANCE_FROM_PLATFORM_FINAL = 1.75F;

    private static VLVCurved control1;
    private static VLVRunner controller;

    public static void initialize(Gen gen){
        controller = new VLVRunner(2, 0);
        FSRenderer.getControlManager().add(controller);

        setupPlatformRise(gen);
    }

    private static void setupPlatformRise(Gen gen){
        final float platformy = gen.platform.instance(0).modelMatrix().getY(0).get();
        final float initialvalue = platformy + DISTANCE_FROM_PLATFORM_ASCEND;

        FSViewConfig config = FSControl.getViewConfig();
        config.setPerspectiveMode();
        config.viewPort(0, 0, FSControl.getMainWidth(), FSControl.getMainHeight());
        config.eyePosition(0F, initialvalue, -0.01F);
        config.lookAt(0f, initialvalue - 10F, 0f, 0f, 1f, 0f);
        config.updateViewProjection();
        config.perspective(70f, (float)FSControl.getMainWidth() / FSControl.getMainHeight(), 0.1F, 10000F);
        config.updateViewPort();

        control1 = new VLVCurved(initialvalue, DISTANCE_FROM_PLATFORM_FINAL, Platform.CYCLES_RISE, VLVariable.LOOP_NONE, Platform.CURVE_RISE, new VLTaskContinous(new VLTask.Task<VLVCurved>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask<VLVCurved> task, VLVCurved var){
                float value = var.get();

                FSViewConfig config = FSControl.getViewConfig();
                config.eyePosition(0F, value, -0.01F);
                config.lookAt(0f, value - 10F, 0f, 0f, 1f, 0f);
                config.updateViewProjection();
            }
        }));

        controller.add(new VLVRunnerEntry(control1, Platform.DELAY_RISE));
        controller.start();
    }
}
