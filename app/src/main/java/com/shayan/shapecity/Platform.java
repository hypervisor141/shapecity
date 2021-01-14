package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVTypeVariable;

public final class Platform{

    //max puzzlebase height : -10 to 1990
    //platform : -24 to 0

//    public static final int CYCLES_RISE = 400;
    public static final int CYCLES_RISE = 10;
    public static final int DELAY_RISE = 20;

    public static final VLVCurved.Curve CURVE_RISE = VLVCurved.CURVE_DEC_SINE_SQRT;

    private static VLVRunner runner_platformrise;

    public static void initialize(Gen gen){
        runner_platformrise = new VLVRunner(1, 0);

        VLVTypeVariable y = gen.platform.instance(0).modelMatrix().getY(0);
        gen.platform.instance(0).modelMatrix().getY(0).set(y.get() - 0.05F);

        Animation.lower(runner_platformrise, CYCLES_RISE, CYCLES_RISE, DELAY_RISE, DELAY_RISE, CURVE_RISE, new FSMesh[]{
                gen.platform

        }, new float[]{
                14F
        });

        gen.vManager().add(runner_platformrise);
    }

    public static void raisePlatform(){
        runner_platformrise.start();
    }
}
