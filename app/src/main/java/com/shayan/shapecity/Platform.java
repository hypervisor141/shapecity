package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVRunner;

public final class Platform{

    //max puzzlebase height : -10 to 1990
    //platform : -24 to -10

    public static final int CYCLES_RISE = 600;
    public static final VLVCurved.Curve CURVE_RISE = VLVCurved.CURVE_DEC_SINE_SQRT;

    private static VLVRunner runner_platformrise;

    public static void initialize(Gen gen){
        runner_platformrise = new VLVRunner(1, 0);

        Animation.lower(runner_platformrise, CYCLES_RISE, 14, CURVE_RISE, new FSMesh[]{
                gen.platform
        });

        gen.vManager().add(runner_platformrise);

        runner_platformrise.start();
    }
}
