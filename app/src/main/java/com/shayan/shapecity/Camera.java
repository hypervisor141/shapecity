package com.shayan.shapecity;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Camera{

    private static final float DISTANCE_FROM_PLATFORM_ASCEND = 5F;
    private static final float DISTANCE_REVEAL_PLATFORM = 5F;
    private static final float DISTANCE_FROM_PLATFORM_FINAL = 2F;

    private static final int CYCLES_CAMERA_PLACEMENT = 10;
//    private static final int CYCLES_CAMERA_PLACEMENT = 100;

    private static final VLVCurved.Curve CURVE_CAMERA_PLACEMENT = VLVCurved.CURVE_ACC_DEC_COS;

    private static VLVCurved controlx;
    private static VLVCurved controly;
    private static VLVCurved controlz;
    private static VLVCurved controlviewx;
    private static VLVCurved controlviewy;
    private static VLVCurved controlviewz;
    private static VLVControl update;
    private static VLVRunner controller;

    public static void initialize(Gen gen){
        controller = new VLVRunner(10, 10);
        FSRenderer.getControlManager().add(controller);

        setupPlatformRise(gen);
    }

    private static void setupPlatformRise(Gen gen){
        final float platformy = gen.platform.instance(0).modelMatrix().getY(0).get();
        final float initialvalue = platformy + DISTANCE_FROM_PLATFORM_ASCEND;

        FSViewConfig config = FSControl.getViewConfig();
        config.eyePosition(0F, initialvalue, -0.01F);
        config.lookAt(0f, initialvalue - 10F, 0f, 0f, 1f, 0f);
        config.updateViewProjection();

        moveCamera(0F, DISTANCE_REVEAL_PLATFORM, -0.01F, 0, -1000F, 0F, Platform.DELAY_RISE, Platform.CYCLES_RISE, Platform.CURVE_RISE, new Runnable(){

            @Override
            public void run(){
                moveCamera(0F, DISTANCE_FROM_PLATFORM_FINAL, -0.01F, 0, -1000F, 0F, 0, CYCLES_CAMERA_PLACEMENT, CURVE_CAMERA_PLACEMENT, null);
            }
        });
    }

    public static void moveCamera(float x, float y, float z, float viewx, float viewy, float viewz, int delay, int cycles, VLVCurved.Curve curve, final Runnable post){
        FSViewConfig config = FSControl.getViewConfig();
        final float[] orgviewsettings = config.viewMatrixSettings().provider().clone();

        controlx = new VLVCurved(orgviewsettings[0], x, cycles, VLVariable.LOOP_NONE, curve);
        controly = new VLVCurved(orgviewsettings[1], y, cycles, VLVariable.LOOP_NONE, curve);
        controlz = new VLVCurved(orgviewsettings[2], z, cycles, VLVariable.LOOP_NONE, curve);
        controlviewx = new VLVCurved(orgviewsettings[3], viewx, cycles, VLVariable.LOOP_NONE, curve);
        controlviewy = new VLVCurved(orgviewsettings[4], viewx, cycles, VLVariable.LOOP_NONE, curve);
        controlviewz = new VLVCurved(orgviewsettings[5], viewx, cycles, VLVariable.LOOP_NONE, curve);

        update = new VLVControl(cycles, VLVariable.LOOP_NONE, new VLTaskContinous(new VLTask.Task(){

            @Override
            public void run(VLTask task, VLVariable var){
                float value = var.get();

                FSViewConfig config = FSControl.getViewConfig();
                config.eyePosition(controlx.get(), controly.get(), controlz.get());
                config.lookAt(controlviewx.get(), controlviewy.get(), controlviewz.get(), 0f, 1f, 0f);
                config.updateViewProjection();

                if(!var.active()){
                    controller.clear();

                    if(post != null){
                        post.run();
                    }
                }
            }
        }));

        controller.add(new VLVRunnerEntry(controlx, delay));
        controller.add(new VLVRunnerEntry(controly, delay));
        controller.add(new VLVRunnerEntry(controlz, delay));
        controller.add(new VLVRunnerEntry(controlviewx, delay));
        controller.add(new VLVRunnerEntry(controlviewy, delay));
        controller.add(new VLVRunnerEntry(controlviewz, delay));
        controller.add(new VLVRunnerEntry(update, delay));
        controller.start();
    }
}
