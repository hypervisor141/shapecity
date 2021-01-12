package com.shayan.shapecity;

import android.opengl.Matrix;

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

    private static final int CYCLES_CAMERA_PLACEMENT = 100;
//    private static final int CYCLES_DESCEND = 240;
    private static final int CYCLES_DESCEND = 10;

    private static final VLVCurved.Curve CURVE_CAMERA_PLACEMENT = VLVCurved.CURVE_ACC_DEC_COS;
    private static final VLVCurved.Curve CURVE_DESCEND = VLVCurved.CURVE_ACC_DEC_COS;

    private static VLVRunner controllerview;
    private static VLVRunner controllerpos;
    private static VLVRunner controllerrotate;

    public static void initialize(Gen gen){
        controllerpos = new VLVRunner(10, 10);
        controllerview = new VLVRunner(10, 10);
        controllerrotate = new VLVRunner(10, 10);

        FSRenderer.getControlManager().add(controllerpos);
        FSRenderer.getControlManager().add(controllerview);
        FSRenderer.getControlManager().add(controllerrotate);
    }

    public static void descend(Gen gen, Runnable post){
        final float platformy = gen.platform.instance(0).modelMatrix().getY(0).get();
        final float initialvalue = platformy + DISTANCE_FROM_PLATFORM_ASCEND;

        position(0F, 4000F, 0.01F);
        lookAt(0F, -1000F, 0F);

        movePosition(0F, initialvalue, -0.01F, 0, CYCLES_DESCEND, CURVE_DESCEND, post);
        moveView(0F, initialvalue - 10F, 0, 0, CYCLES_DESCEND, CURVE_DESCEND, null);
    }

    public static void riseWithPlatform(final Gen gen){
        Light.setForPlatformRise(gen);

        movePosition(0F, DISTANCE_REVEAL_PLATFORM, -0.01F, Platform.DELAY_RISE, Platform.CYCLES_RISE, Platform.CURVE_RISE, new Runnable(){

            @Override
            public void run(){
                Light.radiateForPuzzle(gen);
                lookAt(0F, 0F, 0F);
                movePosition(0F, DISTANCE_FROM_PLATFORM_FINAL, -0.01F, 0, CYCLES_CAMERA_PLACEMENT, CURVE_CAMERA_PLACEMENT, null);
            }
        });
    }

    public static void position(float x, float y, float z){
        FSViewConfig config = FSControl.getViewConfig();
        config.eyePosition(x, y, z);
        config.lookAtUpdate();
        config.updateViewProjection();
    }

    public static void lookAt(float viewx, float viewy, float viewz){
        FSViewConfig config = FSControl.getViewConfig();
        config.lookAt(viewx, viewy, viewz, 0f, 1f, 0f);
        config.updateViewProjection();
    }

    public static void movePosition(float x, float y, float z, int delay, int cycles, VLVCurved.Curve curve, final Runnable post){
        final float[] orgviewsettings = FSControl.getViewConfig().viewMatrixSettings().provider().clone();

        VLVCurved controlx = new VLVCurved(orgviewsettings[0], x, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controly = new VLVCurved(orgviewsettings[1], y, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controlz = new VLVCurved(orgviewsettings[2], z, cycles, VLVariable.LOOP_NONE, curve);

        VLVControl update = new VLVControl(cycles, VLVariable.LOOP_NONE, new VLTaskContinous(new VLTask.Task(){

            @Override
            public void run(VLTask task, VLVariable var){
                position(controlx.get(), controly.get(), controlz.get());

                if(!var.active()){
                    controllerpos.clear();

                    if(post != null){
                        post.run();
                    }
                }
            }
        }));

        controllerpos.add(new VLVRunnerEntry(controlx, delay));
        controllerpos.add(new VLVRunnerEntry(controly, delay));
        controllerpos.add(new VLVRunnerEntry(controlz, delay));
        controllerpos.add(new VLVRunnerEntry(update, delay));
        controllerpos.start();
    }

    public static void moveView(float viewx, float viewy, float viewz, int delay, int cycles, VLVCurved.Curve curve, final Runnable post){
        final float[] orgviewsettings = FSControl.getViewConfig().viewMatrixSettings().provider().clone();

        VLVCurved controlviewx = new VLVCurved(orgviewsettings[3], viewx, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controlviewy = new VLVCurved(orgviewsettings[4], viewy, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controlviewz = new VLVCurved(orgviewsettings[5], viewz, cycles, VLVariable.LOOP_NONE, curve);

        VLVControl update = new VLVControl(cycles, VLVariable.LOOP_NONE, new VLTaskContinous(new VLTask.Task(){

            @Override
            public void run(VLTask task, VLVariable var){
                lookAt(controlviewx.get(), controlviewy.get(), controlviewz.get());

                if(!var.active()){
                    controllerview.clear();

                    if(post != null){
                        post.run();
                    }
                }
            }
        }));

        controllerview.add(new VLVRunnerEntry(controlviewx, delay));
        controllerview.add(new VLVRunnerEntry(controlviewy, delay));
        controllerview.add(new VLVRunnerEntry(controlviewz, delay));
        controllerview.add(new VLVRunnerEntry(update, delay));
        controllerview.start();
    }

    public static void rotate(float fromangle, float toangle, float rotationx, float rotationy, float rotationz, float viewx, float viewy, float viewz, int delay, int cycles, VLVCurved.Curve curve, VLVariable.Loop loop){
        final float[] orgviewsettings = FSControl.getViewConfig().viewMatrixSettings().provider().clone();
        VLVCurved angle = new VLVCurved(fromangle, toangle, cycles, loop, curve, new VLTaskContinous(new VLTask.Task(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask task, VLVariable var){
                FSViewConfig config = FSControl.getViewConfig();
                final float[] pos = config.eyePosition().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.rotateM(cache, 0, var.get(), rotationx, rotationy, rotationz);
                Matrix.multiplyMV(pos, 0, cache, 0, orgviewsettings, 0);

                config.eyePositionUpdate();
                config.lookAtUpdate();
            }
        }));

        VLVCurved controlviewx = new VLVCurved(orgviewsettings[3], viewx, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controlviewy = new VLVCurved(orgviewsettings[4], viewy, cycles, VLVariable.LOOP_NONE, curve);
        VLVCurved controlviewz = new VLVCurved(orgviewsettings[5], viewz, cycles, VLVariable.LOOP_NONE, curve);

        VLVControl update = new VLVControl(cycles, loop, new VLTaskContinous(new VLTask.Task(){

            @Override
            public void run(VLTask task, VLVariable var){
                lookAt(controlviewx.get(), controlviewy.get(), controlviewz.get());

                if(!var.active()){
                    controllerrotate.clear();
                }
            }
        }));

        controllerrotate.add(new VLVRunnerEntry(angle, delay));
        controllerrotate.add(new VLVRunnerEntry(controlviewx, delay));
        controllerrotate.add(new VLVRunnerEntry(controlviewy, delay));
        controllerrotate.add(new VLVRunnerEntry(controlviewz, delay));
        controllerrotate.add(new VLVRunnerEntry(update, delay));
        controllerrotate.start();
    }

    public static void stopPosition(){
        controllerpos.clear();
    }

    public static void stopView(){
        controllerview.clear();
    }

    public static void stopRotation(){
        controllerrotate.clear();
    }

    public static void stop(){
        stopPosition();
        stopView();
        stopRotation();
    }
}
