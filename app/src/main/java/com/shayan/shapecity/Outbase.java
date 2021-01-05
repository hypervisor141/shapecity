package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public class Outbase{

    //outbase powerplants : -950 to -10

    private static int CYCLES_APPEAR = 100;

    private static VLVRunner runner_powerplants;

    public static void initialize(Gen gen){
        runner_powerplants = new VLVRunner(gen.phase7.size(), 20);

        Animation.lower(runner_powerplants, CYCLES_APPEAR, 939F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase7,
                gen.phase7_caps,
                gen.phase7_caps2
        });

        VLVManager m = gen.vManager();
        m.add(runner_powerplants);
    }
}
