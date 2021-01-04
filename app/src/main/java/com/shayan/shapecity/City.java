package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public final class City{

    //phase1 center items : 0 to -10
    //phase1 walls : -75 to -10
    //phase1 pillar frames and caps and blades : -300 to -10
    //phase1 pillar baseframes : -145 to -10

    //phase2 pillar frames : -320 to -10
    //phase2 pillar caps : -150 to -10

    //phase3 pillar layer1 : -700 to -10
    //phase3 pillar layer2 : -1300 to -10
    //phase3 pillar layer3 : -1750 to -10
    //phase3 pillar trapezoids : -200 to -10
    //phase3 outrect layer1 : -130 to -10
    //phase3 outrect layer2 : -253 to -10
    //phase3 outrect layer3 : -365 to -10
    //phase3 outrect layer4 : -467 to -10
    //phase3 outrect layer5 : -562 to -10
    //phase3 outrect layer6 : -650 to -10
    //phase3 outrect layer7 : -730 to -10
    //phase3 outrect layer8 : -803 to -10
    //phase3 outrect layer9 : -866 to -10
    //phase3 outrect layer10 : -908 to -10
    //phase3 outrect layer11 : -908 to -10
    //phase3 outrect layer11 : -950 to -10

    private static int CYCLES_APPEAR = 100;

    private static VLVRunner phase1_runner_centerpieces;
    private static VLVRunner phase1_runner_walls;
    private static VLVRunner phase1_runner_pillars;
    private static VLVRunner phase1_runner_pillars_baseframes;

    private static VLVRunner phase2_runner_pillars;
    private static VLVRunner phase2_runner_pillars_caps;

    private static VLVRunner phase3_runner_rect_layer1;
    private static VLVRunner phase3_runner_rect_layer2;
    private static VLVRunner phase3_runner_rect_layer3;
    private static VLVRunner phase3_runner_trapezoids;
    private static VLVRunner phase3_runner_outrect1;
    private static VLVRunner phase3_runner_outrect2;
    private static VLVRunner phase3_runner_outrect3;
    private static VLVRunner phase3_runner_outrect4;
    private static VLVRunner phase3_runner_outrect5;
    private static VLVRunner phase3_runner_outrect6;
    private static VLVRunner phase3_runner_outrect7;
    private static VLVRunner phase3_runner_outrect8;
    private static VLVRunner phase3_runner_outrect9;
    private static VLVRunner phase3_runner_outrect10;
    private static VLVRunner phase3_runner_outrect11;

    public static void initialize(Gen gen){
        phase1_runner_centerpieces = new VLVRunner(gen.phase1_trapezoidx1.size() * 4 * 4 + gen.phase1_rects.size(), 20);
        phase1_runner_walls = new VLVRunner(gen.phase1_walls.size() * 3, 20);
        phase1_runner_pillars = new VLVRunner(gen.phase1_pillars.size() * 2 + 1, 20);
        phase1_runner_pillars_baseframes = new VLVRunner(gen.phase1_pillars_baseframe1.size() * 4, 20);
        phase2_runner_pillars = new VLVRunner(gen.phase2_pillars.size(), 20);
        phase2_runner_pillars_caps = new VLVRunner(gen.phase2_pillars_caps.size() * 2, 20);
        phase3_runner_rect_layer1 = new VLVRunner(gen.phase3_rect_layer1.size() * 2, 20);
        phase3_runner_rect_layer2 = new VLVRunner(gen.phase3_rect_layer2.size() * 2, 20);
        phase3_runner_rect_layer3 = new VLVRunner(gen.phase3_rect_layer3.size() * 3, 20);
        phase3_runner_trapezoids = new VLVRunner(gen.phase3_trapezoidx1.size() * 4, 20);
        phase3_runner_outrect1 = new VLVRunner(gen.phase3_outrect_layer1.size(), 20);
        phase3_runner_outrect2 = new VLVRunner(gen.phase3_outrect_layer2.size(), 20);
        phase3_runner_outrect3 = new VLVRunner(gen.phase3_outrect_layer3.size(), 20);
        phase3_runner_outrect4 = new VLVRunner(gen.phase3_outrect_layer4.size(), 20);
        phase3_runner_outrect5 = new VLVRunner(gen.phase3_outrect_layer5.size(), 20);
        phase3_runner_outrect6 = new VLVRunner(gen.phase3_outrect_layer6.size(), 20);
        phase3_runner_outrect7 = new VLVRunner(gen.phase3_outrect_layer7.size(), 20);
        phase3_runner_outrect8 = new VLVRunner(gen.phase3_outrect_layer8.size(), 20);
        phase3_runner_outrect9 = new VLVRunner(gen.phase3_outrect_layer9.size(), 20);
        phase3_runner_outrect10 = new VLVRunner(gen.phase3_outrect_layer10.size(), 20);
        phase3_runner_outrect11 = new VLVRunner(gen.phase3_outrect_layer11.size(), 20);

        Animation.lower(phase1_runner_centerpieces, CYCLES_APPEAR, 10F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_trapezoidx1,
                gen.phase1_trapezoidy1,
                gen.phase1_trapezoidx2,
                gen.phase1_trapezoidy2,
                gen.phase1_rects
        });
        Animation.lower(phase1_runner_walls, CYCLES_APPEAR, 64F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_walls,
                gen.phase1_walls_stripes,
                gen.phase1_walls_caps
        });
        Animation.lower(phase1_runner_pillars, CYCLES_APPEAR, 289F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_pillars,
                gen.phase1_pillars_caps,
                gen.phase1_pillars_blades
        });
        Animation.lower(phase1_runner_pillars_baseframes, CYCLES_APPEAR, 134F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_pillars_baseframe1,
                gen.phase1_pillars_baseframe2,
                gen.phase1_pillars_baseframe3,
                gen.phase1_pillars_baseframe4
        });
        Animation.lower(phase2_runner_pillars, CYCLES_APPEAR, 309, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase2_pillars
        });
        Animation.lower(phase2_runner_pillars_caps, CYCLES_APPEAR, 139, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase2_pillars_caps,
                gen.phase2_pillars_caps2
        });
        Animation.lower(phase3_runner_rect_layer1, CYCLES_APPEAR, 689F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer1,
                gen.phase3_rect_layer1_stripes
        });
        Animation.lower(phase3_runner_rect_layer1, CYCLES_APPEAR, 1289F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer2,
                gen.phase3_rect_layer2_stripes
        });
        Animation.lower(phase3_runner_rect_layer1, CYCLES_APPEAR, 1739F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer3,
                gen.phase3_rect_layer3_stripes,
                gen.phase3_rect_caps
        });
        Animation.lower(phase3_runner_trapezoids, CYCLES_APPEAR, 189F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_trapezoidx1,
                gen.phase3_trapezoidy1,
                gen.phase3_trapezoidx2,
                gen.phase3_trapezoidy2
        });
        Animation.lower(phase3_runner_outrect1, CYCLES_APPEAR, 119F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer1
        });
        Animation.lower(phase3_runner_outrect2, CYCLES_APPEAR, 242F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer2
        });
        Animation.lower(phase3_runner_outrect3, CYCLES_APPEAR, 354F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer3
        });
        Animation.lower(phase3_runner_outrect4, CYCLES_APPEAR, 456F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer4
        });
        Animation.lower(phase3_runner_outrect5, CYCLES_APPEAR, 551F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer5
        });
        Animation.lower(phase3_runner_outrect6, CYCLES_APPEAR, 639F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer6
        });
        Animation.lower(phase3_runner_outrect7, CYCLES_APPEAR, 719F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer7
        });
        Animation.lower(phase3_runner_outrect8, CYCLES_APPEAR, 792F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer8
        });
        Animation.lower(phase3_runner_outrect9, CYCLES_APPEAR, 855F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer9
        });
        Animation.lower(phase3_runner_outrect10, CYCLES_APPEAR, 897F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer10
        });
        Animation.lower(phase3_runner_outrect11, CYCLES_APPEAR, 939F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer11
        });

        VLVManager m = gen.vManager();
        m.add(phase1_runner_walls);
        m.add(phase1_runner_pillars);
        m.add(phase1_runner_pillars_baseframes);
        m.add(phase2_runner_pillars);
        m.add(phase2_runner_pillars_caps);
        m.add(phase3_runner_rect_layer1);
        m.add(phase3_runner_rect_layer2);
        m.add(phase3_runner_rect_layer3);
        m.add(phase3_runner_trapezoids);
        m.add(phase3_runner_outrect1);
        m.add(phase3_runner_outrect2);
        m.add(phase3_runner_outrect3);
        m.add(phase3_runner_outrect4);
        m.add(phase3_runner_outrect5);
        m.add(phase3_runner_outrect6);
        m.add(phase3_runner_outrect7);
        m.add(phase3_runner_outrect8);
        m.add(phase3_runner_outrect9);
        m.add(phase3_runner_outrect10);
        m.add(phase3_runner_outrect11);
    }
}
