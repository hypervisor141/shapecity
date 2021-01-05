package com.shayan.shapecity;

import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Light{

    private static final int CYCLES_PHASE_CHANGE = 200;

    private final static float[] CACHE = new float[16];
    private static VLVCurved controlx;
    private static VLVCurved controly;
    private static VLVCurved controlz;
    private static VLVCurved controlradius;
    private static VLVControl update;
    private static VLVRunner controller;

    public static void initialize(Gen gen){
        controller = new VLVRunner(3, 0);
        FSRenderer.getControlManager().add(controller);

        setupPlatformRise(gen);
    }

    private static void setupPlatformRise(Gen gen){
        set(gen,0, 10, 0, 20F);
    }

    public static void set(Gen gen, float x, float y, float z, float radius){
        float[] pos = gen.light.position().provider();
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;

        ((FSAttenuation.Radius)gen.light.attenuation()).radius().set(radius);
    }

    public static void move(final Gen gen, float x, float y, float z, float radius, int delay, int cycles, VLVCurved.Curve curve, final Runnable post){
        final float[] orgpos = gen.light.position().provider().clone();
        float orgradius = ((FSAttenuation.Radius)gen.light.attenuation()).radius().get();

        controlx = new VLVCurved(orgpos[0], x, cycles, VLVariable.LOOP_NONE, curve);
        controly = new VLVCurved(orgpos[1], y, cycles, VLVariable.LOOP_NONE, curve);
        controlz = new VLVCurved(orgpos[2], z, cycles, VLVariable.LOOP_NONE, curve);
        controlradius = new VLVCurved(orgradius, radius, cycles, VLVariable.LOOP_NONE, curve);

        update = new VLVControl(cycles, VLVariable.LOOP_NONE, new VLTaskContinous(new VLTask.Task(){

            @Override
            public void run(VLTask task, VLVariable var){
                set(gen, controlx.get(), controly.get(), controlz.get(), controlradius.get());

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
        controller.add(new VLVRunnerEntry(controlradius, delay));
        controller.add(new VLVRunnerEntry(update, delay));
        controller.start();
    }
}
