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
    private static final float DISTANCE_REVEAL_PLATFORM = 5F;
    private static final float DISTANCE_FROM_PLATFORM_FINAL = 1.75F;

    private static final int CYCLES_CAMERA_PLACEMENT = 100;
    private static final VLVCurved.Curve CURVE_CAMERA_PLACEMENT = VLVCurved.CURVE_ACC_DEC_CUBIC;

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

        control1 = new VLVCurved(initialvalue, DISTANCE_REVEAL_PLATFORM, Platform.CYCLES_RISE, VLVariable.LOOP_NONE, Platform.CURVE_RISE, new VLTaskContinous(new VLTask.Task<VLVCurved>(){

            @Override
            public void run(VLTask<VLVCurved> task, VLVCurved var){
                float value = var.get();

                FSViewConfig config = FSControl.getViewConfig();
                config.eyePosition(0F, value, -0.01F);
                config.lookAt(0f, value - 10F, 0f, 0f, 1f, 0f);
                config.updateViewProjection();

                if(!control1.active()){
                    control1 = new VLVCurved(DISTANCE_REVEAL_PLATFORM, DISTANCE_FROM_PLATFORM_FINAL, CYCLES_CAMERA_PLACEMENT, VLVariable.LOOP_NONE, CURVE_CAMERA_PLACEMENT, new VLTaskContinous(new VLTask.Task<VLVCurved>(){

                        @Override
                        public void run(VLTask<VLVCurved> task, VLVCurved var){
                            float value = var.get();

                            FSViewConfig config = FSControl.getViewConfig();
                            config.eyePosition(0F, value, -0.01F);
                            config.lookAt(0f, value - 10F, 0f, 0f, 1f, 0f);
                            config.updateViewProjection();

                            if(!control1.active()){
                                controller.remove(0);
                            }
                        }
                    }));

                    controller.remove(0);
                    controller.add(new VLVRunnerEntry(control1, 0));
                    controller.start();
                }
            }
        }));

        controller.add(new VLVRunnerEntry(control1, Platform.DELAY_RISE));
        controller.start();
    }
}
