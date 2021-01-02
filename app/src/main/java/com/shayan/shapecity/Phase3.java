package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public class Phase3{

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

    private static VLVRunner runner_rect_layer1;
    private static VLVRunner runner_rect_layer2;
    private static VLVRunner runner_rect_layer3;
    private static VLVRunner runner_trapezoids;
    private static VLVRunner runner_outrect1;
    private static VLVRunner runner_outrect2;
    private static VLVRunner runner_outrect3;
    private static VLVRunner runner_outrect4;
    private static VLVRunner runner_outrect5;
    private static VLVRunner runner_outrect6;
    private static VLVRunner runner_outrect7;
    private static VLVRunner runner_outrect8;
    private static VLVRunner runner_outrect9;
    private static VLVRunner runner_outrect10;
    private static VLVRunner runner_outrect11;

    public static void initialize(Gen gen){
        runner_rect_layer1 = new VLVRunner(gen.phase3_rect_layer1.size() * 2, 20);
        runner_rect_layer2 = new VLVRunner(gen.phase3_rect_layer2.size() * 2, 20);
        runner_rect_layer3 = new VLVRunner(gen.phase3_rect_layer3.size() * 3, 20);
        runner_trapezoids = new VLVRunner(gen.phase3_trapezoidx1.size() * 4, 20);
        runner_outrect1 = new VLVRunner(gen.phase3_outrect_layer1.size(), 20);
        runner_outrect2 = new VLVRunner(gen.phase3_outrect_layer2.size(), 20);
        runner_outrect3 = new VLVRunner(gen.phase3_outrect_layer3.size(), 20);
        runner_outrect4 = new VLVRunner(gen.phase3_outrect_layer4.size(), 20);
        runner_outrect5 = new VLVRunner(gen.phase3_outrect_layer5.size(), 20);
        runner_outrect6 = new VLVRunner(gen.phase3_outrect_layer6.size(), 20);
        runner_outrect7 = new VLVRunner(gen.phase3_outrect_layer7.size(), 20);
        runner_outrect8 = new VLVRunner(gen.phase3_outrect_layer8.size(), 20);
        runner_outrect9 = new VLVRunner(gen.phase3_outrect_layer9.size(), 20);
        runner_outrect10 = new VLVRunner(gen.phase3_outrect_layer10.size(), 20);
        runner_outrect11 = new VLVRunner(gen.phase3_outrect_layer11.size(), 20);

        Animation.lower(runner_rect_layer1, CYCLES_APPEAR, 689F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer1,
                gen.phase3_rect_layer1_stripes
        });
        Animation.lower(runner_rect_layer1, CYCLES_APPEAR, 1289F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer2,
                gen.phase3_rect_layer2_stripes
        });
        Animation.lower(runner_rect_layer1, CYCLES_APPEAR, 1739F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_rect_layer3,
                gen.phase3_rect_layer3_stripes,
                gen.phase3_rect_caps
        });
        Animation.lower(runner_trapezoids, CYCLES_APPEAR, 189F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_trapezoidx1,
                gen.phase3_trapezoidy1,
                gen.phase3_trapezoidx2,
                gen.phase3_trapezoidy2
        });
        Animation.lower(runner_outrect1, CYCLES_APPEAR, 119F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer1
        });
        Animation.lower(runner_outrect2, CYCLES_APPEAR, 242F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer2
        });
        Animation.lower(runner_outrect3, CYCLES_APPEAR, 354F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer3
        });
        Animation.lower(runner_outrect4, CYCLES_APPEAR, 456F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer4
        });
        Animation.lower(runner_outrect5, CYCLES_APPEAR, 551F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer5
        });
        Animation.lower(runner_outrect6, CYCLES_APPEAR, 639F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer6
        });
        Animation.lower(runner_outrect7, CYCLES_APPEAR, 719F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer7
        });
        Animation.lower(runner_outrect8, CYCLES_APPEAR, 792F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer8
        });
        Animation.lower(runner_outrect9, CYCLES_APPEAR, 855F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer9
        });
        Animation.lower(runner_outrect10, CYCLES_APPEAR, 897F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer10
        });
        Animation.lower(runner_outrect11, CYCLES_APPEAR, 939F, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_outrect_layer11
        });

        VLVManager m = gen.vManager();
        m.add(runner_rect_layer1);
        m.add(runner_rect_layer2);
        m.add(runner_rect_layer3);
        m.add(runner_trapezoids);
        m.add(runner_outrect1);
        m.add(runner_outrect2);
        m.add(runner_outrect3);
        m.add(runner_outrect4);
        m.add(runner_outrect5);
        m.add(runner_outrect6);
        m.add(runner_outrect7);
        m.add(runner_outrect8);
        m.add(runner_outrect9);
        m.add(runner_outrect10);
        m.add(runner_outrect11);
    }
}
