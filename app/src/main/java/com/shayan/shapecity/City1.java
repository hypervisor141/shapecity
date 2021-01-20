package com.shayan.shapecity;

import com.nurverek.firestorm.FSGAutomator;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVariable;
import com.shayan.shapecity.Animation;
import com.shayan.shapecity.Camera;
import com.shayan.shapecity.Gen;
import com.shayan.shapecity.Light;
import com.shayan.shapecity.Puzzle;

public final class City1{

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

    private static VLVRunner runner_phase1;
    private static VLVRunner runner_phase2;
    private static VLVRunner runner_phase3;
    private static VLVRunner runner_phase4;
    private static VLVRunner runner_phase5;
    private static VLVRunner runner_phase6;
    private static VLVRunner runner_randomcontrol;

    private static int phaseindex;

    public static FSMesh platform;
    public static FSMesh puzzlebase;
    public static FSMesh puzzlebase_lining;
    public static FSMesh puzzlebase_innerwalls1_frame;
    public static FSMesh puzzlebase_innerwalls1_linings1;
    public static FSMesh puzzlebase_innerwalls1_linings2;
    public static FSMesh puzzlebase_innerwalls1_linings3;
    public static FSMesh puzzlebase_innerwalls1_linings4;
    public static FSMesh puzzlebase_innerwalls1_linings5;
    public static FSMesh puzzlebase_innerwalls1_linings6;
    public static FSMesh puzzlebase_innerwalls1_linings7;
    public static FSMesh puzzlebase_innerwalls1_linings8;
    public static FSMesh puzzlebase_bottom;
    public static FSMesh puzzlebase_innerwalls2;
    public static FSMesh pieces;
    public static FSMesh phase1_base;
    public static FSMesh phase1_base_inner;
    public static FSMesh phase1_base_inner_lining;
    public static FSMesh phase1_pillars;
    public static FSMesh phase1_pillars_stripes;
    public static FSMesh phase2;
    public static FSMesh phase2_stripes;
    public static FSMesh phase2_caps;
    public static FSMesh phase3;
    public static FSMesh phase3_caps;
    public static FSMesh phase3_blades;
    public static FSMesh phase3_baseframe1;
    public static FSMesh phase3_baseframe2;
    public static FSMesh phase3_baseframe3;
    public static FSMesh phase3_baseframe4;
    public static FSMesh phase4;
    public static FSMesh phase4_caps;
    public static FSMesh phase4_caps2;
    public static FSMesh phase5_layer1;
    public static FSMesh phase5_layer2;
    public static FSMesh phase5_layer3;
    public static FSMesh phase5_layer1_stripes;
    public static FSMesh phase5_layer2_stripes;
    public static FSMesh phase5_layer3_stripes;
    public static FSMesh phase5_caps;
    public static FSMesh phase5_trapezoidx1;
    public static FSMesh phase5_trapezoidy1;
    public static FSMesh phase5_trapezoidx2;
    public static FSMesh phase5_trapezoidy2;
    public static FSMesh phase6_layer1;
    public static FSMesh phase6_layer2;
    public static FSMesh phase6_layer3;
    public static FSMesh phase6_layer4;
    public static FSMesh phase6_layer5;
    public static FSMesh phase6_layer6;
    public static FSMesh phase6_layer7;
    public static FSMesh phase6_layer8;
    public static FSMesh phase6_layer9;
    public static FSMesh phase6_layer10;
    public static FSMesh phase6_layer11;
    public static FSMesh mainbase1;
    public static FSMesh mainbase2;
    
    public static void register(Gen gen, FSGAutomator automator){
        pieces = gen.register(gen.bppieces, "pieces.", Puzzle.COLOR_LAYER, Material.MATERIAL_WHITE_MORE_SPECULAR);
        platform = gen.register(gen.bpsingular, "platform_Cube.637", Animation.COLOR_ORANGE, Material.MATERIAL_WHITE);
        puzzlebase = gen.register(gen.bpsingular, "puzzlebase_Cube.036", Animation.COLOR_PURPLE_MORE, Material.MATERIAL_WHITE);
        puzzlebase_lining = gen.register(gen.bpsingular, "puzzlebase_lining_Cube.634", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        puzzlebase_innerwalls1_frame = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_frame_Cube.024", Animation.COLOR_BLUE, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings1 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings1_Cube.028", Animation.COLOR_BLUE_LESS1, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings2 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings2_Cube.029", Animation.COLOR_BLUE_LESS2, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings3 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings3_Cube.030", Animation.COLOR_BLUE_LESS3, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings4 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings4_Cube.031", Animation.COLOR_BLUE_LESS4, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings5 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings5_Cube.032", Animation.COLOR_BLUE_LESS5, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings6 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings6_Cube.033", Animation.COLOR_BLUE_LESS6, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings7 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings7_Cube.034", Animation.COLOR_BLUE_LESS7, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings8 = gen.register(gen.bpsingular, "puzzlebase_innerwalls1_linings8_Cube.035", Animation.COLOR_BLUE_LESS8, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls2 = gen.register(gen.bpsingular, "puzzlebase_innerwalls2_Cube.025", Animation.COLOR_PURPLE_MORE, Material.MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_bottom = gen.register(gen.bpsingular, "puzzlebase_bottom_Cube.026", Animation.COLOR_BLUE, Material.MATERIAL_WHITE_LESS_SPECULAR);
        mainbase1 = gen.register(gen.bpsingular, "mainbase1_Cube.037", Animation.COLOR_RED_LESS1, Material.MATERIAL_WHITE);
        mainbase2 = gen.register(gen.bpsingular, "mainbase2_Cube.157", Animation.COLOR_PURPLE_MORE, Material.MATERIAL_WHITE);
        phase1_base = gen.register(gen.bpinstanced, "phase1_base_Cube.027", Animation.COLOR_PURPLE_MORE, Material.MATERIAL_WHITE);
        phase1_base_inner = gen.register(gen.bpinstanced, "phase1_base_inner_Cube.039", Animation.COLOR_PURPLE_LESS, Material.MATERIAL_WHITE);
        phase1_base_inner_lining = gen.register(gen.bpinstanced, "phase1_base_inner_lining_Cube.040", Animation.COLOR_BLUE_LESS6, Material.MATERIAL_WHITE);
        phase1_pillars = gen.register(gen.bpinstanced, "phase1_pillar.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase1_pillars_stripes = gen.register(gen.bpinstanced, "phase1_pillars_stripe.", Animation.COLOR_OBSIDIAN_LESS3, Material.MATERIAL_WHITE);
        phase2 = gen.register(gen.bpinstanced, "phase2.", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE);
        phase2_stripes = gen.register(gen.bpinstanced, "phase2_stripe.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase2_caps = gen.register(gen.bpinstanced, "phase2_cap.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase3 = gen.register(gen.bpinstanced, "phase3.", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE);
        phase3_caps = gen.register(gen.bpinstanced, "phase3_cap.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase3_blades = gen.register(gen.bpinstanced, "phase3_blade.", Animation.COLOR_OBSIDIAN_LESS1, Material.MATERIAL_WHITE);
        phase3_baseframe1 = gen.register(gen.bpinstanced, "phase3_baseframe1.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase3_baseframe2 = gen.register(gen.bpinstanced, "phase3_baseframe2.", Animation.COLOR_BLUE_LESS3, Material.MATERIAL_WHITE);
        phase3_baseframe3 = gen.register(gen.bpinstanced, "phase3_baseframe3.", Animation.COLOR_BLUE_LESS4, Material.MATERIAL_WHITE);
        phase3_baseframe4 = gen.register(gen.bpinstanced, "phase3_baseframe4.", Animation.COLOR_BLUE_LESS7, Material.MATERIAL_WHITE);
        phase4 = gen.register(gen.bpinstanced, "phase4.", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE);
        phase4_caps = gen.register(gen.bpinstanced, "phase4_cap.", Animation.COLOR_BLUE_LESS3, Material.MATERIAL_WHITE);
        phase4_caps2 = gen.register(gen.bpinstanced, "phase4_cap2.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase5_layer1 = gen.register(gen.bpinstanced, "phase5_layer1.", Animation.COLOR_OBSIDIAN_LESS2, Material.MATERIAL_WHITE);
        phase5_layer2 = gen.register(gen.bpinstanced, "phase5_layer2.", Animation.COLOR_OBSIDIAN_LESS3, Material.MATERIAL_WHITE);
        phase5_layer3 = gen.register(gen.bpinstanced, "phase5_layer3.", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE);
        phase5_layer1_stripes = gen.register(gen.bpinstanced, "phase5_layer1_stripe.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase5_layer2_stripes = gen.register(gen.bpinstanced, "phase5_layer2_stripe.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase5_layer3_stripes = gen.register(gen.bpinstanced, "phase5_layer3_stripe.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase5_caps = gen.register(gen.bpinstanced, "phase5_cap.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase5_trapezoidx1 = gen.register(gen.bpinstanced, "phase5_trapezoidx1.", Animation.COLOR_OBSIDIAN_LESS2, Material.MATERIAL_WHITE);
        phase5_trapezoidy1 = gen.register(gen.bpinstanced, "phase5_trapezoidy1.", Animation.COLOR_OBSIDIAN_LESS2, Material.MATERIAL_WHITE);
        phase5_trapezoidx2 = gen.register(gen.bpinstanced, "phase5_trapezoidx2.", Animation.COLOR_OBSIDIAN_LESS3, Material.MATERIAL_WHITE);
        phase5_trapezoidy2 = gen.register(gen.bpinstanced, "phase5_trapezoidy2.", Animation.COLOR_OBSIDIAN_LESS3, Material.MATERIAL_WHITE);
        phase6_layer1 = gen.register(gen.bpinstanced, "phase6_layer1.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
        phase6_layer2 = gen.register(gen.bpinstanced, "phase6_layer2.", Animation.COLOR_OBSIDIAN_LESS3, Material.MATERIAL_WHITE);
        phase6_layer3 = gen.register(gen.bpinstanced, "phase6_layer3.", Animation.COLOR_BLUE_LESS1, Material.MATERIAL_WHITE);
        phase6_layer4 = gen.register(gen.bpinstanced, "phase6_layer4.", Animation.COLOR_BLUE_LESS2, Material.MATERIAL_WHITE);
        phase6_layer5 = gen.register(gen.bpinstanced, "phase6_layer5.", Animation.COLOR_BLUE_LESS3, Material.MATERIAL_WHITE);
        phase6_layer6 = gen.register(gen.bpinstanced, "phase6_layer6.", Animation.COLOR_BLUE_LESS4, Material.MATERIAL_WHITE);
        phase6_layer7 = gen.register(gen.bpinstanced, "phase6_layer7.", Animation.COLOR_BLUE_LESS5, Material.MATERIAL_WHITE);
        phase6_layer8 = gen.register(gen.bpinstanced, "phase6_layer8.", Animation.COLOR_BLUE_LESS6, Material.MATERIAL_WHITE);
        phase6_layer9 = gen.register(gen.bpinstanced, "phase6_layer9.", Animation.COLOR_BLUE_LESS7, Material.MATERIAL_WHITE);
        phase6_layer10 = gen.register(gen.bpinstanced, "phase6_layer10.", Animation.COLOR_BLUE_LESS8, Material.MATERIAL_WHITE);
        phase6_layer11 = gen.register(gen.bpinstanced, "phase6_layer11.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE);
    }

    public static void initialize(Gen gen){
        phaseindex = 0;

        runner_phase1 = new VLVRunner(50, 50);
        runner_phase2 = new VLVRunner(50, 50);
        runner_phase3 = new VLVRunner(50, 50);
        runner_phase3 = new VLVRunner(50, 50);
        runner_phase4 = new VLVRunner(50, 50);
        runner_phase5 = new VLVRunner(50, 50);
        runner_phase6 = new VLVRunner(50, 50);
        runner_randomcontrol = new VLVRunner(50, 50);

        VLVCurved.Curve curve = VLVCurved.CURVE_DEC_SINE_SQRT;

        Animation.lower(runner_phase1, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase1_pillars, phase1_pillars_stripes

        }, new float[]{
                17F, 17F
        });
        Animation.lower(runner_phase2, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase2, phase2_stripes, phase2_caps

        }, new float[]{
                64F, 64F, 64F
        });
        Animation.lower(runner_phase3, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase3, phase3_caps, phase3_blades, phase3_baseframe1, phase3_baseframe2, phase3_baseframe3, phase3_baseframe4

        }, new float[]{
                289F, 289F, 289F, 134F, 134F, 134F, 134F
        });
        Animation.lower(runner_phase4, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase4, phase4_caps, phase4_caps2

        }, new float[]{
                309F, 139F, 139F
        });
        Animation.lower(runner_phase5, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase5_layer1, phase5_layer1_stripes, phase5_layer2, phase5_layer2_stripes, phase5_layer3, phase5_layer3_stripes,
                phase5_trapezoidx1, phase5_trapezoidy1, phase5_trapezoidx2, phase5_trapezoidy2

        }, new float[]{
                689F, 689F, 1289F, 1289F, 1739F, 1739F, 189F, 189F, 189F, 189F
        });
        Animation.lower(runner_phase6, CYCLES_APPEAR_MIN, CYCLES_APPEAR_MAX, DELAY_APPEAR_MIN, DELAY_APPEAR_MAX, curve, new FSMesh[]{
                phase6_layer2, phase6_layer3, phase6_layer4, phase6_layer5, phase6_layer6, phase6_layer7,
                phase6_layer8, phase6_layer9, phase6_layer10, phase6_layer11

        }, new float[]{
                169F, 444F, 756F, 1051F, 1339F, 1619F, 1892F, 2155F, 2297F, 2297F
        });

        VLVManager m = gen.vManager();
        m.add(runner_phase1);
        m.add(runner_phase2);
        m.add(runner_phase3);
        m.add(runner_phase4);
        m.add(runner_phase5);
        m.add(runner_phase6);
        m.add(runner_randomcontrol);
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
        reveal(gen, 70F, 80F, 130F, 1F, 120, 120, runner_phase1, post);
    }

    public static void raisePhase2(Gen gen, Runnable post){
        reveal(gen, 200F, 300F,750F, 5F, 240,120, runner_phase2, post);
    }

    public static void raisePhase3(Gen gen, Runnable post){
        reveal(gen, 400F, 600F,1200F, 10F, 300,120, runner_phase3, post);
    }

    public static void raisePhase4(Gen gen, Runnable post){
        reveal(gen, 600F, 1000F,1800F, 25F, 360,120, runner_phase4, post);
    }

    public static void raisePhase5(Gen gen, Runnable post){
        reveal(gen, 1000F, 2000F,2750F, 50F, 420,120, runner_phase5, post);
    }

    public static void raisePhase6(Gen gen, Runnable post){
        reveal(gen, 1500F, 3000F,3500F, 100F, 480,120, runner_phase6, post);
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
                                Animation.randomize(gen, runner_phase1, -10F, -1F, phase1_pillars.size(), 120);

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
