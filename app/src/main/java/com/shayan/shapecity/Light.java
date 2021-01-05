package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLFloat;
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
    private static VLVariable control1;
    private static VLVRunner controller;

    public static void initialize(Gen gen){
        controller = new VLVRunner(3, 0);
        FSRenderer.getControlManager().add(controller);

        setupPlatformRise(gen);
    }

    private static void setupPlatformRise(Gen gen){
        ((FSAttenuation.Radius)gen.light.attenuation()).radius().set(20F);

        VLArrayFloat position = gen.light.position();
        position.set(0, 0F);
        position.set(1, 10F);
        position.set(2, 0F);
    }

    public static void revealNextPhase(Gen gen){
        final float initialY = gen.light.position().get(1);
        final float initialradius = ((FSAttenuation.Radius)gen.light.attenuation()).radius().get();

        control1 = new VLVCurved(0, 100F, CYCLES_PHASE_CHANGE, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_SINE, new VLTaskContinous<VLVCurved>(new VLTask.Task<VLVCurved>(){

            @Override
            public void run(VLTask<VLVCurved> task, VLVCurved var){
                float value = var.get();

                ((FSAttenuation.Radius)gen.light.attenuation()).radius().set(initialradius + value * 10F);

                VLArrayFloat position = gen.light.position();
                position.set(1, initialY + value * 5F);

                if(!var.active()){
                    controller.clear();
                }
            }
        }));

        controller.add(new VLVRunnerEntry(control1, 0));
        controller.start();
    }
}
