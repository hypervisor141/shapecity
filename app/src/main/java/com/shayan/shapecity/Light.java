package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Light{

    private final static float[] CACHE = new float[16];
    private static VLVariable control1;
    private static VLVRunner controller;

    public static void initialize(Gen gen){
        controller = new VLVRunner(3, 0);
        FSRenderer.getControlManager().add(controller);

        setupPlatformRise(gen);
    }

    private static void setupPlatformRise(Gen gen){
        ((FSAttenuation.Radius)gen.light.attenuation()).radius().set(10F);

        VLArrayFloat position = gen.light.position();
        position.set(0, 0F);
        position.set(1, 0F);
        position.set(2, 0F);
    }
}
