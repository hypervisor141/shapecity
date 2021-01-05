package com.shayan.shapecity;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;

public final class City{

    //phase1 center items : 0 to -10

    //phase2 walls : -75 to -10

    //phase3 frames and caps and blades : -300 to -10
    //phase3 baseframes : -145 to -10

    //phase4 frames : -320 to -10
    //phase4 caps : -150 to -10

    //phase5 layer1 : -700 to -10
    //phase5 layer2 : -1300 to -10
    //phase5 layer3 : -1750 to -10
    //phase5 trapezoids : -200 to -10

    //phase6 layer1 : -10 to -10
    //phase6 layer2 : -180 to 50
    //phase6 layer3 : -465 to 100
    //phase6 layer4 : -767 to 300
    //phase6 layer5 : -1062 to 500
    //phase6 layer6 : -1350 to 700
    //phase6 layer7 : -1630 to 900
    //phase6 layer8 : -1903 to 1100
    //phase6 layer9 : -2166 to 1300
    //phase6 layer10 : -2308 to 1500
    //phase6 layer11 : -2308 to 1500

    //phase7 powerplants : -950 to -10

    private static int CYCLES_APPEAR = 100;

    private static VLVRunner phase1;
    private static VLVRunner phase2;
    private static VLVRunner phase3;
    private static VLVRunner phase3_baseframes;

    private static VLVRunner phase4;
    private static VLVRunner phase4_caps;

    private static VLVRunner phase5_layer1;
    private static VLVRunner phase5_layer2;
    private static VLVRunner phase5_layer3;
    private static VLVRunner phase5_trapezoids;

    private static VLVRunner phase6_layer2;
    private static VLVRunner phase6_layer3;
    private static VLVRunner phase6_layer4;
    private static VLVRunner phase6_layer5;
    private static VLVRunner phase6_layer6;
    private static VLVRunner phase6_layer7;
    private static VLVRunner phase6_layer8;
    private static VLVRunner phase6_layer9;
    private static VLVRunner phase6_layer10;
    private static VLVRunner phase6_layer11;

    private static VLVRunner phase7;

    public static void initialize(Gen gen){
        phase1 = new VLVRunner(gen.phase1_trapezoidx1.size() * 4 * 4 + gen.phase1_rects.size(), 20);
        phase2 = new VLVRunner(gen.phase2.size() * 3, 20);
        phase3 = new VLVRunner(gen.phase3.size() * 2 + 1, 20);
        phase3_baseframes = new VLVRunner(gen.phase3_baseframe1.size() * 4, 20);
        phase4 = new VLVRunner(gen.phase4.size(), 20);
        phase4_caps = new VLVRunner(gen.phase4_caps.size() * 2, 20);
        phase5_layer1 = new VLVRunner(gen.phase5_layer1.size() * 2, 20);
        phase5_layer2 = new VLVRunner(gen.phase5_layer2.size() * 2, 20);
        phase5_layer3 = new VLVRunner(gen.phase5_layer3.size() * 3, 20);
        phase5_trapezoids = new VLVRunner(gen.phase5_trapezoidx1.size() * 4, 20);
        phase6_layer2 = new VLVRunner(gen.phase6_layer2.size(), 20);
        phase6_layer3 = new VLVRunner(gen.phase6_layer3.size(), 20);
        phase6_layer4 = new VLVRunner(gen.phase6_layer4.size(), 20);
        phase6_layer5 = new VLVRunner(gen.phase6_layer5.size(), 20);
        phase6_layer6 = new VLVRunner(gen.phase6_layer6.size(), 20);
        phase6_layer7 = new VLVRunner(gen.phase6_layer7.size(), 20);
        phase6_layer8 = new VLVRunner(gen.phase6_layer8.size(), 20);
        phase6_layer9 = new VLVRunner(gen.phase6_layer9.size(), 20);
        phase6_layer10 = new VLVRunner(gen.phase6_layer10.size(), 20);
        phase6_layer11 = new VLVRunner(gen.phase6_layer11.size(), 20);
        phase7 = new VLVRunner(gen.phase7.size(), 20);

        Animation.lower(phase1, CYCLES_APPEAR, 10F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase1_trapezoidx1,
                gen.phase1_trapezoidy1,
                gen.phase1_trapezoidx2,
                gen.phase1_trapezoidy2,
                gen.phase1_rects
        });
        Animation.lower(phase2, CYCLES_APPEAR, 64F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase2,
                gen.phase2_stripes,
                gen.phase2_caps
        });
        Animation.lower(phase3, CYCLES_APPEAR, 289F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3,
                gen.phase3_caps,
                gen.phase3_blades
        });
        Animation.lower(phase3_baseframes, CYCLES_APPEAR, 134F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase3_baseframe1,
                gen.phase3_baseframe2,
                gen.phase3_baseframe3,
                gen.phase3_baseframe4
        });
        Animation.lower(phase4, CYCLES_APPEAR, 309, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase4
        });
        Animation.lower(phase4_caps, CYCLES_APPEAR, 139, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase4_caps,
                gen.phase4_caps2
        });
        Animation.lower(phase5_layer1, CYCLES_APPEAR, 689F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase5_layer1,
                gen.phase5_layer1_stripes
        });
        Animation.lower(phase5_layer1, CYCLES_APPEAR, 1289F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase5_layer2,
                gen.phase5_layer2_stripes
        });
        Animation.lower(phase5_layer1, CYCLES_APPEAR, 1739F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase5_layer3,
                gen.phase5_layer3_stripes,
                gen.phase5_caps
        });
        Animation.lower(phase5_trapezoids, CYCLES_APPEAR, 189F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase5_trapezoidx1,
                gen.phase5_trapezoidy1,
                gen.phase5_trapezoidx2,
                gen.phase5_trapezoidy2
        });
        Animation.lower(phase6_layer2, CYCLES_APPEAR, 169F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer2
        });
        Animation.lower(phase6_layer3, CYCLES_APPEAR, 444F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer3
        });
        Animation.lower(phase6_layer4, CYCLES_APPEAR, 756F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer4
        });
        Animation.lower(phase6_layer5, CYCLES_APPEAR, 1051F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer5
        });
        Animation.lower(phase6_layer6, CYCLES_APPEAR, 1339F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer6
        });
        Animation.lower(phase6_layer7, CYCLES_APPEAR, 1619F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer7
        });
        Animation.lower(phase6_layer8, CYCLES_APPEAR, 1892F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer8
        });
        Animation.lower(phase6_layer9, CYCLES_APPEAR, 2155F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer9
        });
        Animation.lower(phase6_layer10, CYCLES_APPEAR, 2297F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer10
        });
        Animation.lower(phase6_layer11, CYCLES_APPEAR, 2297F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase6_layer11
        });
        Animation.lower(phase7, CYCLES_APPEAR, 939F, 0, VLVCurved.CURVE_DEC_SINE_SQRT, new FSMesh[]{
                gen.phase7,
                gen.phase7_caps,
                gen.phase7_caps2
        });

        VLVManager m = gen.vManager();
        m.add(phase2);
        m.add(phase3);
        m.add(phase3_baseframes);
        m.add(phase4);
        m.add(phase4_caps);
        m.add(phase5_layer1);
        m.add(phase5_layer2);
        m.add(phase5_layer3);
        m.add(phase5_trapezoids);
        m.add(phase6_layer2);
        m.add(phase6_layer3);
        m.add(phase6_layer4);
        m.add(phase6_layer5);
        m.add(phase6_layer6);
        m.add(phase6_layer7);
        m.add(phase6_layer8);
        m.add(phase6_layer9);
        m.add(phase6_layer10);
        m.add(phase6_layer11);
        m.add(phase7);
    }
}
