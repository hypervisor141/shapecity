package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public class Phase2{

    //phase2 pillar frames : -320 to -10
    //phase2 pillar caps : -150 to -10

    private static int CYCLES_APPEAR = 100;

    private static VLVRunner runner_pillars;
    private static VLVRunner runner_pillars_caps;

    public static void initialize(Gen gen){
        runner_pillars = new VLVRunner(gen.phase2_pillars.size(), 20);
        runner_pillars_caps = new VLVRunner(gen.phase2_pillars_caps.size() * 2, 20);

        Animation.lower(runner_pillars, CYCLES_APPEAR, 309, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase2_pillars
        });
        Animation.lower(runner_pillars_caps, CYCLES_APPEAR, 139, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase2_pillars_caps,
                gen.phase2_pillars_caps2
        });

        VLVManager m = gen.vManager();
        m.add(runner_pillars);
        m.add(runner_pillars_caps);
    }
}
