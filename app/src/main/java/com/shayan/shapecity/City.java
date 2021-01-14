package com.shayan.shapecity;

import com.nurverek.firestorm.FSArrayModel;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLMath;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVariable;

public final class City{

    //phase1 center items : -27 to -10

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

    private static final int CYCLES_APPEAR_MIN = 120;
    private static final int CYCLES_APPEAR_MAX = 200;
    private static final int DELAY_APPEAR_MIN = 0;
    private static final int DELAY_APPEAR_MAX = 200;

    private static VLVRunner phase1;
    private static VLVRunner phase2;
    private static VLVRunner phase3;
    private static VLVRunner phase4;
    private static VLVRunner phase5;
    private static VLVRunner phase6;

    private static VLVRunner randomcontrol;

    private static int phaseindex;

    public static void initialize(Gen gen){
        phaseindex = 0;

        phase1 = new VLVRunner(50, 50);
        phase2 = new VLVRunner(50, 50);
        phase3 = new VLVRunner(50, 50);
        phase3 = new VLVRunner(50, 50);
        phase4 = new VLVRunner(50, 50);
        phase5 = new VLVRunner(50, 50);
        phase6 = new VLVRunner(50, 50);
        randomcontrol = new VLVRunner(50, 50);

        VLVCurved.Curve curve = VLVCurved.CURVE_DEC_SINE_SQRT;

        Animation.lower(phase1, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase1_pillars, gen.phase1_pillars_stripes

        }, new float[]{
                17F, 17F
        });
        Animation.lower(phase2, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase2, gen.phase2_stripes, gen.phase2_caps

        }, new float[]{
                64F, 64F, 64F
        });
        Animation.lower(phase3, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase3, gen.phase3_caps, gen.phase3_blades, gen.phase3_baseframe1, gen.phase3_baseframe2, gen.phase3_baseframe3, gen.phase3_baseframe4

        }, new float[]{
                289F, 289F, 289F, 134F, 134F, 134F, 134F
        });
        Animation.lower(phase4, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase4, gen.phase4_caps, gen.phase4_caps2

        }, new float[]{
                309F, 139F, 139F,
        });
        Animation.lower(phase5, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase5_layer1, gen.phase5_layer1_stripes, gen.phase5_layer2, gen.phase5_layer2_stripes, gen.phase5_layer3, gen.phase5_layer3_stripes,
                gen.phase5_trapezoidx1, gen.phase5_trapezoidy1, gen.phase5_trapezoidx2, gen.phase5_trapezoidy2

        }, new float[]{
                689F, 689F, 1289F, 1289F, 1739F, 1739F, 189F, 189F, 189F, 189F
        });
        Animation.lower(phase6, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                gen.phase6_layer2, gen.phase6_layer3, gen.phase6_layer4, gen.phase6_layer5, gen.phase6_layer6, gen.phase6_layer7,
                gen.phase6_layer8, gen.phase6_layer9, gen.phase6_layer10, gen.phase6_layer11

        }, new float[]{
                169F, 444F, 756F, 1051F, 1339F, 1619F, 1892F, 2155F, 2297F, 2297F
        });

        VLVManager m = gen.vManager();
        m.add(phase1);
        m.add(phase2);
        m.add(phase3);
        m.add(phase4);
        m.add(phase5);
        m.add(phase6);
        m.add(randomcontrol);
    }

    public static void initiateNextPhase(Gen gen, Runnable post){
        if(phaseindex == 0){
            raisePhase1(gen, post);

        }else if(phaseindex == 1){
            raisePhase2(gen, post);

        }else if(phaseindex == 2){
            raisePhase3(gen, post);

        }else if(phaseindex == 3){
            raisePhase4(gen, post);

        }else if(phaseindex == 4){
            raisePhase5(gen, post);

        }else if(phaseindex == 5){
            raisePhase6(gen, post);
        }

        phaseindex++;
    }

    public static void raisePhase1(Gen gen, Runnable post){
        reveal(gen, 70F, 80F, 130F, 1F, 120, 120, phase1, post);
    }

    public static void raisePhase2(Gen gen, Runnable post){
        reveal(gen, 200F, 300F,750F, 5F, 240,120, phase2, post);
    }

    public static void raisePhase3(Gen gen, Runnable post){
        reveal(gen, 400F, 600F,1200F, 10F, 300,120, phase3, post);
    }

    public static void raisePhase4(Gen gen, Runnable post){
        reveal(gen, 600F, 1000F,1800F, 25F, 360,120, phase4, post);
    }

    public static void raisePhase5(Gen gen, Runnable post){
        reveal(gen, 1000F, 2000F,2750F, 50F, 420,120, phase5, post);
    }

    public static void raisePhase6(Gen gen, Runnable post){
        reveal(gen, 1500F, 3000F,3500F, 100F, 480,120, phase6, post);
    }

    private static void reveal(final Gen gen, final float lighty, final float cameray, final float cameraxz, float near, final int ascendcycles, final int rotatecyclesbase, final VLVRunner phase, final Runnable post){
        Camera.rotate(0F, 45F, 0F, 1F, 0F, 0, rotatecyclesbase / 2, VLVCurved.CURVE_DEC_SINE_SQRT, VLVariable.LOOP_NONE, new Runnable(){

            @Override
            public void run(){
                Light.movePosition(gen, 0, lighty, 0, 0, ascendcycles, VLVCurved.CURVE_ACC_DEC_COS, null);
                Light.moveRadius(gen, lighty * 5F, 0, (int)Math.floor(ascendcycles * 1.25F), VLVCurved.CURVE_ACC_DEC_COS, null);

                Camera.moveNear(near, 0, ascendcycles, VLVCurved.CURVE_ACC_DEC_COS, null);
                Camera.movePosition(cameraxz, cameray, cameraxz, 0, ascendcycles, VLVCurved.CURVE_ACC_DEC_COS, new Runnable(){

                    @Override
                    public void run(){
                        phase.start();

                        float angle = (45F + Gen.RANDOM.nextInt(180));
                        angle = Gen.RANDOM.nextBoolean() ? angle : -angle;

                        Camera.rotate(0F, angle, lighty, lighty * 7F, lighty,
                                0, rotatecyclesbase * 4, VLVCurved.CURVE_ACC_DEC_COS, VLVariable.LOOP_NONE, new Runnable(){

                            @Override
                            public void run(){
                                Animation.randomize(gen, phase1, -10F, -1F, gen.phase1_pillars.size(), 120);

                                Light.radiateForPuzzle(gen, 150, rotatecyclesbase);
                                Camera.lookAtPuzzle(150, (int)Math.floor(rotatecyclesbase * 1.25F));

                                post.run();
                            }
                        });
                    }
                });
            }
        });
    }
}
