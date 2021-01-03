package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public final class Phase1{

    //phase1 center items : 0 to -10
    //phase1 walls : -75 to -10
    //phase1 pillar frames and caps and blades : -300 to -10
    //phase1 pillar baseframes : -145 to -10

    private static int CYCLES_APPEAR = 100;

    public FSMesh phase1_trapezoidx1;
    public FSMesh phase1_trapezoidy1;
    public FSMesh phase1_trapezoidx2;
    public FSMesh phase1_trapezoidy2;
    public FSMesh phase1_rects;

    private static VLVRunner runner_centerpieces;
    private static VLVRunner runner_walls;
    private static VLVRunner runner_pillars;
    private static VLVRunner runner_pillars_baseframes;

    public static void initialize(Gen gen){
        runner_centerpieces = new VLVRunner(gen.phase1_trapezoidx1.size() * 4 * 4 + gen.phase1_rects.size(), 20);
        runner_walls = new VLVRunner(gen.phase1_walls.size() * 3, 20);
        runner_pillars = new VLVRunner(gen.phase1_pillars.size() * 2 + 1, 20);
        runner_pillars_baseframes = new VLVRunner(gen.phase1_pillars_baseframe1.size() * 4, 20);

        Animation.lower(runner_centerpieces, CYCLES_APPEAR, 10F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_trapezoidx1,
                gen.phase1_trapezoidy1,
                gen.phase1_trapezoidx2,
                gen.phase1_trapezoidy2,
                gen.phase1_rects
        });
        Animation.lower(runner_walls, CYCLES_APPEAR, 64F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_walls,
                gen.phase1_walls_stripes,
                gen.phase1_walls_caps
        });
        Animation.lower(runner_pillars, CYCLES_APPEAR, 289F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_pillars,
                gen.phase1_pillars_caps,
                gen.phase1_pillars_blades
        });
        Animation.lower(runner_pillars_baseframes, CYCLES_APPEAR, 134F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_pillars_baseframe1,
                gen.phase1_pillars_baseframe2,
                gen.phase1_pillars_baseframe3,
                gen.phase1_pillars_baseframe4
        });

        VLVManager m = gen.vManager();
        m.add(runner_walls);
        m.add(runner_pillars);
        m.add(runner_pillars_baseframes);
    }
}
