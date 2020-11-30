package com.shayan.shapecity;

import android.opengl.Matrix;
import android.util.Log;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSModelMatrix;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.firestorm.Loader;
import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLTaskDone;
import com.nurverek.vanguard.VLTaskTargetValue;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVConst;
import com.nurverek.vanguard.VLVInterpolated;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVProcessor;

public final class Animation{

    public static final float[] COLOR_WHITE = new float[]{
            1F, 1F, 1F, 1F
    };
    public static final float[] COLOR_WHITE_LESS = new float[]{
            0.8F, 0.8F, 0.8F, 1F
    };
    public static final float[] COLOR_ORANGE = new float[]{
            1.0F, 0.7F, 0F, 1F
    };
    public static final float[] COLOR_OBSIDIAN = new float[]{
            0.4F, 0.4F, 0.4F, 1F
    };
    public static final float[] COLOR_OBSIDIAN_LESS = new float[]{
            0.15F, 0.15F, 0.15F, 1F
    };
    public static final float[] COLOR_OBSIDIAN_LESS2 = new float[]{
            0.1F, 0.1F, 0.1F, 1F
    };
    public static final float[] COLOR_OBSIDIAN_LESS3 = new float[]{
            0.05F, 0.05F, 0.05F, 1F
    };
    public static final float[] COLOR_GOLD = new float[]{
            0.83F, 0.68F, 0.21F, 1F
    };
    public static final float[] COLOR_DARK_ORANGE = new float[]{
            1.0F, 0.4F, 0F, 1F
    };

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
    private static final int ROW_MODEL_BOUNCE_Y = 4;
    private static final int ROW_MODEL_RAISE_Y = 5;

    private static final int CYCLES_LIGHT_ROTATION = 3600;
    private static final int CYCLES_BLINK = 20;
    private static final int CYCLES_DEACTIVATED = 60;
    private static final int CYCLES_ROTATE = 30;
    private static final int CYCLES_RAISE = 100;
    private static final int CYCLES_RAISE_BASE_MIN = 150;
    private static final int CYCLES_RAISE_BASE_MAX = 250;
    private static final int CYCLES_RAISE_BASE_DELAY_MIN = 0;
    private static final int CYCLES_RAISE_BASE_DELAY_MAX = 100;
    private static final int CYCLES_BOUNCE = 200;
    private static final int CYCLES_REVEAL_MIN = 40;
    private static final int CYCLES_REVEAL_MAX = 60;
    private static final int CYCLES_REVEAL_DELAY_MIN = 0;
    private static final int CYCLES_REVEAL_DELAY_MAX = 250;
    private static final int CYCLES_REVEAL_REPEAT = 360;
    private static final int CYCLES_REVEAL_INPUT = 50;
    private static final int CYCLES_TEXCONTROL = 100;

    private static final float Y_REDUCTION = 0.5f;
    private static final float Y_BOUNCE_HEIGHT_MULTIPLIER = 0.5f;
    private static final float Y_BASE_HEIGHT_MULTIPLIER = 0.5f;

    private static VLVProcessor processorBounce;
    private static VLVProcessor processorRaiseBase;
    private static VLVProcessor processorRaise;
    private static VLVProcessor processorBlink;
    private static VLVProcessor processorDeactivate;
    private static VLVProcessor processorStandby;
    private static VLVProcessor processorTextureBlink;

    public static void setupProcessors(Loader loader){
        int itemsize = Loader.LAYER_INSTANCE_COUNT * Loader.layers.length;

        processorRaiseBase = new VLVProcessor(itemsize, 0);
        processorBounce = new VLVProcessor(itemsize, 0);
        processorRaise = new VLVProcessor(itemsize, 0);
        processorBlink = new VLVProcessor(itemsize, 0);
        processorDeactivate = new VLVProcessor(itemsize, 0);
        processorStandby = new VLVProcessor(itemsize, 0);
        processorTextureBlink = new VLVProcessor(itemsize, 0);

        FSMesh layer;
        FSInstance instance;
        FSModelMatrix modelmatrix;
        FSSchematics schematics;
        VLVMatrix colormatrix;
        VLVInterpolated texblinkvar;
        ModColor.TextureControlLink link;
        VLArrayFloat linkdata;

        float yv;
        float yraise;
        float yraisebase;

        VLListType<VLVProcessor> processors = loader.processors();

        processors.add(processorRaiseBase);
        processors.add(processorBounce);
        processors.add(processorRaise);
        processors.add(processorBlink);
        processors.add(processorDeactivate);
        processors.add(processorStandby);
        processors.add(processorTextureBlink);

        float[][] colors = new float[][]{
                COLOR_LAYER1,
                COLOR_LAYER2,
                COLOR_LAYER3,
        };

        for(int i = 0; i < Loader.layers.length; i++){
            layer = Loader.layers[i];
            linkdata = ((ModColor.TextureControlLink)layer.link(0)).data;

            for(int i2 = 0; i2 < layer.size(); i2++){
                instance = layer.instance(i2);
                modelmatrix = instance.modelMatrix();
                schematics = instance.schematics();
                yv = modelmatrix.getY(0).get() - Y_REDUCTION;

                modelmatrix.getY(0).set(yv);

                yraise = schematics.modelHeight() * Y_BOUNCE_HEIGHT_MULTIPLIER;
                yraisebase = 0;

                for(int i3 = 0; i3 < i; i3++){
                    yraisebase +=  Loader.layers[i3].instance(i2).schematics().modelHeight() * Y_BASE_HEIGHT_MULTIPLIER;
                }

                modelmatrix.addRowRotate(0, new VLVConst(90f), VLVConst.ZERO, VLVConst.ONE, VLVConst.ZERO);
                modelmatrix.addRowRotate(0, new VLVConst(90f), VLVConst.ZERO, VLVConst.ZERO, VLVConst.ONE);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVInterpolated(0f, yraisebase, CYCLES_RAISE_BASE_MAX, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT), VLVConst.ZERO);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVInterpolated(0f, yraise, CYCLES_BOUNCE, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT), VLVConst.ZERO);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVInterpolated(0f, yraise, CYCLES_RAISE, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT), VLVConst.ZERO);

                colormatrix = new VLVMatrix(3, 0);

                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVInterpolated(colors[i][0], COLOR_STANDBY[0], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVInterpolated(colors[i][1], COLOR_STANDBY[1], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVInterpolated(colors[i][2], COLOR_STANDBY[2], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_STANDBY, new VLVInterpolated(colors[i][3], COLOR_STANDBY[3], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));

                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(colors[i][0], COLOR_BLINK[0], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(colors[i][1], COLOR_BLINK[1], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(colors[i][2], COLOR_BLINK[2], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(colors[i][3], COLOR_BLINK[3], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));

                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(colors[i][0], COLOR_DEACTIVATED[0], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(colors[i][1], COLOR_DEACTIVATED[1], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(colors[i][2], COLOR_DEACTIVATED[2], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(colors[i][3], COLOR_DEACTIVATED[3], CYCLES_DEACTIVATED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));

                texblinkvar = new VLVInterpolated(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, CYCLES_TEXCONTROL, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT);

                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_STANDBY, 0));
                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_BLINK, 0));
                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), ROW_COLOR_DEACTIVATED, 0));
                texblinkvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

                modelmatrix.sync();
                colormatrix.sync();
                texblinkvar.sync();

                processorRaiseBase.add(new VLVProcessor.Entry(modelmatrix, ROW_MODEL_RAISE_BASE, 0));
                processorBounce.add(new VLVProcessor.Entry(modelmatrix, ROW_MODEL_BOUNCE_Y, 0));
                processorRaise.add(new VLVProcessor.Entry(modelmatrix, ROW_MODEL_RAISE_Y, 0));
                processorStandby.add(new VLVProcessor.Entry(colormatrix, ROW_COLOR_STANDBY, VLVProcessor.SYNC_INDEX, 0, 0));
                processorBlink.add(new VLVProcessor.Entry(colormatrix, ROW_COLOR_BLINK, VLVProcessor.SYNC_INDEX, 1, 0));
                processorDeactivate.add(new VLVProcessor.Entry(colormatrix, ROW_COLOR_DEACTIVATED, VLVProcessor.SYNC_INDEX, 2, 0));
                processorTextureBlink.add(new VLVProcessor.Entry(texblinkvar, 0));

                schematics.inputBounds().add(new FSBoundsCuboid(schematics,
                        50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC,
                        40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }
        }
    }

    public static void activateProcessor(VLVProcessor proc, int instance, int cycles, int delay, VLTask<VLVInterpolated> task, boolean activate){
        if(proc.isActive(instance)){
            VLVProcessor.Entry entry = proc.get(instance);

            entry.reset();
            entry.target.reinitialize(cycles);
            entry.delay = delay;
            entry.resetDelayTracker();

            if(task != null){
                entry.target.setTask(task);
            }

            proc.start();

        }else if(activate){
            proc.reactivate(instance);
            activateProcessor(proc, instance, cycles, delay, task, false);
        }
    }

    public static void rotateLightSource(){
        final float[] orgpos = Loader.lightPoint.position().provider().clone();

        VLVLinear v = new VLVLinear(0, 360, CYCLES_LIGHT_ROTATION, VLV.LOOP_FORWARD)
                .setTask(new VLTaskContinous(new VLTask.Task<VLVLinear>(){

                    private float[] cache = new float[16];

                    @Override
                    public void run(VLTask t, VLVLinear v){
                        float[] pos = Loader.lightPoint.position().provider();

                        Matrix.setIdentityM(cache, 0);
                        Matrix.rotateM(cache, 0, v.get(), 0f, 1f ,0f);
                        Matrix.multiplyMV(pos, 0, cache, 0, orgpos, 0);

                        pos[0] /= pos[3];
                        pos[1] /= pos[3];
                        pos[2] /= pos[3];

                        Loader.shadowPoint.updateLightVP();
                    }
                }));

        VLVProcessor controlproc = FSRenderer.getControllersProcessor();
        controlproc.add(new VLVProcessor.Entry(v, 0));
        controlproc.start();
    }

    public static void raiseBases(int layer){
        int basesize = layer * Loader.LAYER_INSTANCE_COUNT;
        int maxsize = basesize + Loader.LAYER_INSTANCE_COUNT;

        int cycles = 0;
        int delay = 0;

        for(int i = basesize; i < maxsize; i++){
            cycles = CYCLES_RAISE_BASE_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_MAX - CYCLES_REVEAL_MIN);
            delay = CYCLES_REVEAL_DELAY_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_DELAY_MAX - CYCLES_REVEAL_DELAY_MIN);

            activateProcessor(processorRaiseBase, i, cycles, delay, null, true);
        }
    }

    public static void reveal(int layer){
        int basesize = layer * Loader.LAYER_INSTANCE_COUNT;
        int maxsize = basesize + Loader.LAYER_INSTANCE_COUNT;

        int cycles = 0;
        int delay = 0;

        for(int i = basesize; i < maxsize; i++){
            cycles = CYCLES_REVEAL_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_MAX - CYCLES_REVEAL_MIN);
            delay = CYCLES_REVEAL_DELAY_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_DELAY_MAX - CYCLES_REVEAL_DELAY_MIN);

            activateProcessor(processorBounce, i, cycles, delay, null, true);
            activateProcessor(processorBlink, i, cycles, delay, null, true);
            activateProcessor(processorTextureBlink, i, cycles, delay, null, true);
        }
    }

    public static void reveal(int layer, final int instance){
        VLTask<VLVInterpolated> task = new VLTaskDone(new VLTask.Task<VLVInterpolated>(){

            @Override
            public void run(VLTask task, VLVInterpolated var){
                Game.activePieces.set(instance, -1);
            }
        });

        int target = layer * Loader.LAYER_INSTANCE_COUNT + instance;

        activateProcessor(processorBounce, target, CYCLES_REVEAL_INPUT, 0, null, true);
        activateProcessor(processorBlink, target, CYCLES_REVEAL_INPUT, 0, null, true);
        activateProcessor(processorTextureBlink, target, CYCLES_REVEAL_INPUT, 0, task, true);
    }

    public static void revealRepeat(final int layer){
        VLVLinear control = new VLVLinear(0, CYCLES_REVEAL_REPEAT, CYCLES_REVEAL_REPEAT, VLV.LOOP_FORWARD).setTask(new VLTaskTargetValue(new VLTask.Task<VLVLinear>(){

            @Override
            public void run(VLTask task, VLVLinear var){
                clearRevealProcessors();
                reveal(layer);
            }
        }));

        VLVProcessor proc = FSRenderer.getControllersProcessor();
        proc.add(new VLVProcessor.Entry(control, 0));
        proc.start();
    }

    public static void standBy(final int layer){
        int basesize = layer * Loader.LAYER_INSTANCE_COUNT;
        int maxsize = basesize + Loader.LAYER_INSTANCE_COUNT;

        int cycles = 0;
        int delay = 0;

        for(int i = basesize; i < maxsize; i++){
            cycles = CYCLES_REVEAL_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_MAX - CYCLES_REVEAL_MIN);
            delay = CYCLES_REVEAL_DELAY_MIN + Game.RANDOM.nextInt(CYCLES_REVEAL_DELAY_MAX - CYCLES_REVEAL_DELAY_MIN);

            activateProcessor(processorStandby, i, cycles, delay, null, true);
        }
    }

    public static void deactivatePiece(int instance){
        processorBounce.get(instance).reset();
        processorBlink.get(instance).reset();
        processorTextureBlink.get(instance).reset();

        processorBounce.deactivate(instance);
        processorBlink.deactivate(instance);
        processorTextureBlink.deactivate(instance);

        activateProcessor(processorRaise, instance, CYCLES_DEACTIVATED, 0, null, true);
        activateProcessor(processorDeactivate, instance, CYCLES_DEACTIVATED, 0, null, true);
    }

    public static void clearRevealProcessors(){
        processorBounce.deactivateAll();
        processorBlink.deactivateAll();
        processorTextureBlink.deactivateAll();
    }

    public static void clearDeactivationProcessors(){
        processorRaise.deactivateAll();
        processorDeactivate.deactivateAll();
    }

    public static void clearStandbyProcessors(){
        processorStandby.deactivateAll();
    }

    public static void clearRaiseBaseProcessors(){
        processorRaiseBase.deactivateAll();
    }

    public static void destroy(){

    }
}
