package com.shayan.shapecity;

import android.opengl.Matrix;
import android.util.Log;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.firestorm.Loader;
import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLTaskTargetValue;
import com.nurverek.vanguard.VLVConst;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerManager;
import com.nurverek.vanguard.VLVRunnerManagers;
import com.nurverek.vanguard.VLVariable;

public final class Animation{

    public static final float[] COLOR_WHITE = new float[]{ 1F, 1F, 1F, 1F };
    public static final float[] COLOR_WHITE_LESS = new float[]{ 0.8F, 0.8F, 0.8F, 1F };
    public static final float[] COLOR_ORANGE = new float[]{ 1.0F, 0.7F, 0F, 1F };
    public static final float[] COLOR_OBSIDIAN = new float[]{ 0.4F, 0.4F, 0.4F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS = new float[]{ 0.15F, 0.15F, 0.15F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS2 = new float[]{ 0.1F, 0.1F, 0.1F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS3 = new float[]{ 0.05F, 0.05F, 0.05F, 1F };
    public static final float[] COLOR_GOLD = new float[]{ 0.83F, 0.68F, 0.21F, 1F };
    public static final float[] COLOR_DARK_ORANGE = new float[]{ 1.0F, 0.4F, 0F, 1F };

    public static final float[] COLOR_LAYER1 = COLOR_OBSIDIAN_LESS3;
    public static final float[] COLOR_LAYER2 = COLOR_OBSIDIAN_LESS3;
    public static final float[] COLOR_LAYER3 = COLOR_OBSIDIAN_LESS3;
    private static final float[] COLOR_BLINK = COLOR_OBSIDIAN_LESS2;
    private static final float[] COLOR_DEACTIVATED = COLOR_DARK_ORANGE;
    public static final float[] COLOR_STANDBY = COLOR_WHITE_LESS;

    public static final float TEXCONTROL_IDLE = 0F;
    public static final float TEXCONTROL_ACTIVE = 1F;

    private static final int ROW_COLOR_STANDBY = 0;
    private static final int ROW_COLOR_BLINK = 1;
    private static final int ROW_COLOR_DEACTIVATED = 2;
    private static final int ROW_MODEL_ROTATE_FACE = 0;
    private static final int ROW_MODEL_POSITION = 1;
    private static final int ROW_MODEL_RAISE_BASE = 3;
    private static final int ROW_MODEL_BOUNCE = 4;

    private static final int CYCLES_LIGHT_ROTATION = 3600;
    private static final int CYCLES_BLINK = 20;
    private static final int CYCLES_DEACTIVATED = 60;
    private static final int CYCLES_STANDBY = 100;
    private static final int CYCLES_ROTATE = 30;
    private static final int CYCLES_RAISE_BASE_MIN = 150;
    private static final int CYCLES_RAISE_BASE_MAX = 250;
    private static final int CYCLES_RAISE_BASE_DELAY_MIN = 0;
    private static final int CYCLES_RAISE_BASE_DELAY_MAX = 100;
    private static final int CYCLES_LOWER_BASE_MIN = 60;
    private static final int CYCLES_LOWER_BASE_MAX = 120;
    private static final int CYCLES_LOWER_BASE_DELAY_MIN = 0;
    private static final int CYCLES_LOWER_BASE_DELAY_MAX = 50;
    private static final int CYCLES_BOUNCE = 200;
    private static final int CYCLES_REVEAL_MIN = 60;
    private static final int CYCLES_REVEAL_MAX = 100;
    private static final int CYCLES_REVEAL_DELAY_MIN = 0;
    private static final int CYCLES_REVEAL_DELAY_MAX = 200;
    private static final int CYCLES_REVEAL_REPEAT = 360;
    private static final int CYCLES_REVEAL_INPUT = 80;
    private static final int CYCLES_TEXCONTROL = 100;

    private static final float Y_REDUCTION = 0.5f;
    private static final float Y_BOUNCE_HEIGHT_MULTIPLIER = 0.5f;
    private static final float Y_BASE_HEIGHT_MULTIPLIER = 0.5f;

    private static VLListType<VLVRunnerManagers> managers;

    public static void setupProcessors(Loader loader){
        int itemsize = Loader.LAYER_INSTANCE_COUNT * Loader.layers.length;
        managers = new VLListType<>(3, 0);

        float[][] colors = new float[][]{ COLOR_LAYER1, COLOR_LAYER2, COLOR_LAYER3, };

        FSMesh layer;
        FSInstance instance;
        FSMatrixModel modelmatrix;
        FSSchematics schematics;
        VLVMatrix colormatrix;
        VLVCurved texblinkvar;
        ModColor.TextureControlLink link;
        VLArrayFloat linkdata;

        float yv;
        float yraise;
        float yraisebase;

        int size = Loader.layers.length;

        VLListType<VLVRunner> main = loader.runners();

        for(int i = 0; i < size; i++){
            layer = Loader.layers[i];
            linkdata = ((ModColor.TextureControlLink)layer.link(0)).data;

            VLVRunnerManagers layermanagers = new VLVRunnerManagers(3, 0);

            VLVRunnerManager raise = new VLVRunnerManager(size, 0);
            VLVRunnerManager standby = new VLVRunnerManager(size, 0);
            VLVRunnerManager reveal = new VLVRunnerManager(3 * size, 0);

            VLVRunner processorRaiseBase = new VLVRunner(itemsize, 0);
            VLVRunner processorStandby = new VLVRunner(itemsize, 0);
            VLVRunner processorBlink = new VLVRunner(itemsize, 0);
            VLVRunner processorBounce = new VLVRunner(itemsize, 0);
            VLVRunner processorTextureBlink = new VLVRunner(itemsize, 0);
            VLVRunner processorDeactivate = new VLVRunner(itemsize, 0);

            main.add(processorRaiseBase);
            main.add(processorBounce);
            main.add(processorBlink);
            main.add(processorTextureBlink);
            main.add(processorDeactivate);
            main.add(processorStandby);

            raise.add(processorRaiseBase);

            standby.add(processorStandby);

            reveal.add(processorBlink);
            reveal.add(processorBounce);
            reveal.add(processorTextureBlink);

            layermanagers.add(raise);
            layermanagers.add(standby);
            layermanagers.add(reveal);

            managers.add(layermanagers);

            for(int i2 = 0; i2 < layer.size(); i2++){
                instance = layer.instance(i2);
                modelmatrix = instance.modelMatrix();
                schematics = instance.schematics();
                yv = modelmatrix.getY(0).get() - Y_REDUCTION;

                modelmatrix.getY(0).set(yv);

                yraise = schematics.modelHeight() * Y_BOUNCE_HEIGHT_MULTIPLIER;
                yraisebase = 0;

                for(int i3 = 0; i3 < i; i3++){
                    yraisebase += Loader.layers[i3].instance(i2).schematics().modelHeight() * Y_BASE_HEIGHT_MULTIPLIER;
                }

                modelmatrix.addRowRotate(0, new VLVConst(90f), VLVConst.ZERO, VLVConst.ONE, VLVConst.ZERO);
                modelmatrix.addRowRotate(0, new VLVConst(90f), VLVConst.ZERO, VLVConst.ZERO, VLVConst.ONE);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVCurved(0f, yraisebase, CYCLES_RAISE_BASE_MAX, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT), VLVConst.ZERO);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVCurved(0f, yraise, CYCLES_BOUNCE, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT), VLVConst.ZERO);

                colormatrix = new VLVMatrix(3, 0);
                colormatrix.addRow(4, 0);
                colormatrix.addRow(4, 0);
                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVCurved(colors[i][0], COLOR_STANDBY[0], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVCurved(colors[i][1], COLOR_STANDBY[1], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVCurved(colors[i][2], COLOR_STANDBY[2], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVCurved(colors[i][3], COLOR_STANDBY[3], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVCurved(colors[i][0], COLOR_BLINK[0], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVCurved(colors[i][1], COLOR_BLINK[1], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVCurved(colors[i][2], COLOR_BLINK[2], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVCurved(colors[i][3], COLOR_BLINK[3], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVCurved(colors[i][0], COLOR_DEACTIVATED[0], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVCurved(colors[i][1], COLOR_DEACTIVATED[1], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVCurved(colors[i][2], COLOR_DEACTIVATED[2], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVCurved(colors[i][3], COLOR_DEACTIVATED[3], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT));

                texblinkvar = new VLVCurved(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, CYCLES_TEXCONTROL, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);

                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_STANDBY, 0));
                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_BLINK, 0));
                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_DEACTIVATED, 0));
                texblinkvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

                modelmatrix.sync();
                colormatrix.sync();
                texblinkvar.sync();

                processorRaiseBase.add(new VLVRunner.EntryMatRow(modelmatrix, ROW_MODEL_RAISE_BASE, 0));
                processorStandby.add(new VLVRunner.EntryMatRow(colormatrix, ROW_COLOR_STANDBY, VLVRunner.SYNC_INDEX, 0, 0));
                processorBounce.add(new VLVRunner.EntryMatRow(modelmatrix, ROW_MODEL_BOUNCE, 0));
                processorBlink.add(new VLVRunner.EntryMatRow(colormatrix, ROW_COLOR_BLINK, VLVRunner.SYNC_INDEX, 1, 0));
                processorTextureBlink.add(new VLVRunner.EntryVar(texblinkvar, 0));
                processorDeactivate.add(new VLVRunner.EntryMatRow(colormatrix, ROW_COLOR_DEACTIVATED, VLVRunner.SYNC_INDEX, 2, 0));

                schematics.inputBounds().add(new FSBoundsCuboid(schematics, 50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC, 40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }
        }
    }

    public static void rotateLightSource(){
        final float[] orgpos = Loader.lightPoint.position().provider().clone();

        VLVControl v = new VLVControl(CYCLES_LIGHT_ROTATION, VLVariable.LOOP_FORWARD, new VLTaskContinous(new VLTask.Task<VLVLinear>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask<VLVLinear> task, VLVRunner processor, VLVLinear var){
                float[] pos = Loader.lightPoint.position().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.rotateM(cache, 0, var.get(), 0f, 1f, 0f);
                Matrix.multiplyMV(pos, 0, cache, 0, orgpos, 0);

                pos[0] /= pos[3];
                pos[1] /= pos[3];
                pos[2] /= pos[3];

                Loader.shadowPoint.updateLightVP();
            }
        }));

        VLVRunner controlproc = FSRenderer.getControllersProcessor();
        controlproc.add(new VLVRunner.EntryVar(v, 0));
        controlproc.start();
    }

    public static void raiseBases(int layer){
        VLVRunnerManager manager = managers.get(layer).get(0);
        manager.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true, true);
        manager.randomizeDelays(CYCLES_RAISE_BASE_DELAY_MIN, CYCLES_RAISE_BASE_DELAY_MAX, true, false);
        manager.start();
    }

    public static void standBy(int layer){
        VLVRunnerManager manager = managers.get(layer).get(1);
        manager.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true, true);
        manager.randomizeDelays(CYCLES_RAISE_BASE_DELAY_MIN, CYCLES_RAISE_BASE_DELAY_MAX, true, false);
        manager.start();
    }

    public static void reveal(int layer){
        VLVRunnerManager manager = managers.get(layer).get(2);
        manager.randomizeCycles(CYCLES_REVEAL_MIN, CYCLES_REVEAL_MAX, false, true);
        manager.randomizeDelays(CYCLES_REVEAL_DELAY_MIN, CYCLES_REVEAL_DELAY_MAX, false, false);
        manager.start();
    }

    public static void lowerBases(int layer){
        VLVRunnerManager manager = managers.get(layer).get(0);
        manager.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true, true);
        manager.randomizeDelays(CYCLES_RAISE_BASE_DELAY_MIN, CYCLES_RAISE_BASE_DELAY_MAX, true, false);
        manager.reverse();
        manager.reset();
    }

    public static void reveal(int layer, final int instance){

    }

    public static void revealRepeat(final int layer){
        VLVControl control = new VLVControl(CYCLES_REVEAL_REPEAT, VLVariable.LOOP_FORWARD, new VLTaskTargetValue(new VLTask.Task<VLVLinear>(){

            @Override
            public void run(VLTask<VLVLinear> task, VLVRunner processor, VLVLinear var){
                reveal(layer);
            }
        }));

        FSRenderer.getControllersProcessor().add(new VLVRunner.EntryVar(control, 0));
    }

    public static void deactivatePiece(int instance){

    }

    public static void destroy(){

    }
}
