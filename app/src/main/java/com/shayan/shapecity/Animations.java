package com.shayan.shapecity;

import android.opengl.Matrix;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.firestorm.Loader;
import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLTaskTargetValue;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVConnection;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Animations{

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

    private static final int RUNNER_RAISE_COUNT_PER_LAYER = 1;
    private static final int RUNNER_STANDBY_COUNT_PER_LAYER = 1;
    private static final int RUNNER_REVEAL_COUNT_PER_LAYER = 3;
    private static final int RUNNER_DEACTIVATE_COUNT_PER_LAYER = 1;

    private static final int RUNNER_RAISE_INDEX = 0;
    private static final int RUNNER_STANDBY_INDEX = 1;
    private static final int RUNNER_REVEAL_INDEX = 2;
    private static final int RUNNER_DEACTIVATE_INDEX = 3;

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
    private static final int CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT = 240;
    private static final int CYCLES_REVEAL_INPUT = 50;
    private static final int CYCLES_TEXCONTROL = 100;

    private static final float Y_REDUCTION = 0.5f;
    private static final float Y_BOUNCE_HEIGHT_MULTIPLIER = 0.1f;
    private static final float Y_BASE_HEIGHT_MULTIPLIER = 0.5f;

    private static VLVManager rootmanager;
    private static VLVManager controlmanager;
    private static VLVRunner controlrunner;
    private static VLVControl controlreveal;
    private static VLVLinear controllight;

    public static void setupRunners(Loader loader){
        rootmanager = new VLVManager(3, 0);
        controlmanager = new VLVManager(1, 0);
        controlrunner = new VLVRunner(3, 0);

        controlmanager.add(controlrunner);

        float[][] colors = new float[][]{ COLOR_LAYER1, COLOR_LAYER2, COLOR_LAYER3, };
        int itemsize = Loader.LAYER_INSTANCE_COUNT * Loader.layers.length;
        int size = Loader.layers.length;

        for(int i = 0; i < size; i++){
            FSMesh layer = Loader.layers[i];
            VLArrayFloat linkdata = ((ModColor.TextureControlLink)layer.link(0)).data;

            VLVManager layermanager = new VLVManager(4, 0);

            VLVManager raisemanager = new VLVManager(RUNNER_RAISE_COUNT_PER_LAYER * size, 0);
            VLVManager standbymanager = new VLVManager(RUNNER_STANDBY_COUNT_PER_LAYER * size, 0);
            VLVManager revealmanager = new VLVManager(RUNNER_REVEAL_COUNT_PER_LAYER * size, 0);
            VLVManager deactivationmanager = new VLVManager(RUNNER_DEACTIVATE_COUNT_PER_LAYER * size, 0);

            VLVRunner raiseBase = new VLVRunner(itemsize, 0);
            VLVRunner standby = new VLVRunner(itemsize * 4, 0);
            VLVRunner blink = new VLVRunner(itemsize * 4, 0);
            VLVRunner bounce = new VLVRunner(itemsize, 0);
            VLVRunner textureblink = new VLVRunner(itemsize, 0);
            VLVRunner deactivate = new VLVRunner(itemsize * 4, 0);

            for(int i2 = 0; i2 < layer.size(); i2++){
                FSInstance instance = layer.instance(i2);
                FSMatrixModel modelmatrix = instance.modelMatrix();
                FSSchematics schematics = instance.schematics();
                VLArrayFloat colorarray = instance.colors();

                modelmatrix.getY(0).set(modelmatrix.getY(0).get() - Y_REDUCTION);

                float yraisebase = 0;

                for(int i3 = 0; i3 < i; i3++){
                    yraisebase += Loader.layers[i3].instance(i2).schematics().modelHeight() * Y_BASE_HEIGHT_MULTIPLIER;
                }

                float ybounce = schematics.modelHeight() * Y_BOUNCE_HEIGHT_MULTIPLIER;

                VLVCurved translateraisey = new VLVCurved(0f, yraisebase, CYCLES_RAISE_BASE_MAX, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved translatebouncey = new VLVCurved(0f, ybounce, CYCLES_BOUNCE, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);

                VLVCurved standbyred = new VLVCurved(colors[i][0], COLOR_STANDBY[0], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved standbygreen = new VLVCurved(colors[i][1], COLOR_STANDBY[1], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved standbyblue = new VLVCurved(colors[i][2], COLOR_STANDBY[2], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved standbyalpha = new VLVCurved(colors[i][3], COLOR_STANDBY[3], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);

                VLVCurved blinkred = new VLVCurved(colors[i][0], COLOR_BLINK[0], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved blinkgreen = new VLVCurved(colors[i][1], COLOR_BLINK[1], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved blinkblue = new VLVCurved(colors[i][2], COLOR_BLINK[2], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved blinkalpha = new VLVCurved(colors[i][3], COLOR_BLINK[3], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);

                VLVCurved deactivatedred = new VLVCurved(colors[i][0], COLOR_DEACTIVATED[0], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved deactivatedgreen = new VLVCurved(colors[i][1], COLOR_DEACTIVATED[1], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved deactivatedblue = new VLVCurved(colors[i][2], COLOR_DEACTIVATED[2], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);
                VLVCurved deactivatedalpha = new VLVCurved(colors[i][3], COLOR_DEACTIVATED[3], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_COS_SQRT);

                VLVCurved texblinkvar = new VLVCurved(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, CYCLES_TEXCONTROL, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_DEC_COS_SQRT);

                VLVMatrix.Definition modeldef = new VLVMatrix.Definition(modelmatrix);
                VLArray.DefinitionVLV colordefred = new VLArray.DefinitionVLV(colorarray, 0);
                VLArray.DefinitionVLV colordefgreen = new VLArray.DefinitionVLV(colorarray, 1);
                VLArray.DefinitionVLV colordefblue = new VLArray.DefinitionVLV(colorarray, 2);
                VLArray.DefinitionVLV colordefalpha = new VLArray.DefinitionVLV(colorarray, 3);

                translateraisey.SYNCER.add(modeldef);
                translatebouncey.SYNCER.add(modeldef);

                standbyred.SYNCER.add(colordefred);
                standbygreen.SYNCER.add(colordefgreen);
                standbyblue.SYNCER.add(colordefblue);
                standbyalpha.SYNCER.add(colordefalpha);

                blinkred.SYNCER.add(colordefred);
                blinkgreen.SYNCER.add(colordefgreen);
                blinkblue.SYNCER.add(colordefblue);
                blinkalpha.SYNCER.add(colordefalpha);

                deactivatedred.SYNCER.add(colordefred);
                deactivatedgreen.SYNCER.add(colordefgreen);
                deactivatedblue.SYNCER.add(colordefblue);
                deactivatedalpha.SYNCER.add(colordefalpha);

                texblinkvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

                raiseBase.add(new VLVRunnerEntry(translateraisey, 0));
                standby.add(new VLVRunnerEntry(standbyred, 0));
                standby.add(new VLVRunnerEntry(standbygreen, 0));
                standby.add(new VLVRunnerEntry(standbyblue, 0));
                standby.add(new VLVRunnerEntry(standbyalpha, 0));
                bounce.add(new VLVRunnerEntry(translatebouncey, 0));
                blink.add(new VLVRunnerEntry(blinkred, 0));
                blink.add(new VLVRunnerEntry(blinkgreen, 0));
                blink.add(new VLVRunnerEntry(blinkblue, 0));
                blink.add(new VLVRunnerEntry(blinkalpha, 0));
                textureblink.add(new VLVRunnerEntry(texblinkvar, 0));
                deactivate.add(new VLVRunnerEntry(deactivatedred, 0));
                deactivate.add(new VLVRunnerEntry(deactivatedgreen, 0));
                deactivate.add(new VLVRunnerEntry(deactivatedblue, 0));
                deactivate.add(new VLVRunnerEntry(deactivatedalpha, 0));

                modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ONE, VLV.ZERO);
                modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ZERO, VLV.ONE);
                modelmatrix.addRowTranslation(VLV.ZERO, translateraisey, VLV.ZERO);
                modelmatrix.addRowTranslation(VLV.ZERO, translatebouncey, VLV.ZERO);

                schematics.inputBounds().add(new FSBoundsCuboid(schematics, 50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC, 40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }

            raiseBase.connections(1, 0);
            raiseBase.connections(1, 0);
            standby.connections(1, 0);
            blink.connections(1, 0);
            bounce.connections(1, 0);
            textureblink.connections(1, 0);
            deactivate.connections(1, 0);

            raisemanager.add(raiseBase);
            standbymanager.add(standby);
            revealmanager.add(blink);
            revealmanager.add(bounce);
            revealmanager.add(textureblink);
            deactivationmanager.add(deactivate);

            raisemanager.findEndPointIndex();
            standbymanager.findEndPointIndex();
            deactivationmanager.findEndPointIndex();
            revealmanager.findEndPointIndex();

            deactivationmanager.deactivate();

            layermanager.add(raisemanager);
            layermanager.add(standbymanager);
            layermanager.add(revealmanager);
            layermanager.add(deactivationmanager);

            rootmanager.add(layermanager);
        }

        rootmanager.sync();

        VLVManager loadermanager = loader.vManager();
        loadermanager.add(rootmanager);
        loadermanager.add(controlmanager);
    }

    public static void rotateLightSource(){
        final float[] orgpos = Loader.lightPoint.position().provider().clone();

        controllight = new VLVLinear(0, 360, CYCLES_LIGHT_ROTATION, VLVariable.LOOP_FORWARD, new VLTaskContinous(new VLTask.Task<VLVLinear>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask<VLVLinear> task, VLVLinear var){
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

        controlrunner.add(new VLVRunnerEntry(controllight, 0));
        controlrunner.start();
    }

    public static void raiseBases(int layer){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_RAISE_INDEX);
        manager.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true);
        manager.randomizeDelays(CYCLES_RAISE_BASE_DELAY_MIN, CYCLES_RAISE_BASE_DELAY_MAX, true);
        manager.start();
    }

    public static void standBy(int layer){
        rootmanager.get(layer).get(RUNNER_STANDBY_INDEX).start();
    }

    public static void unstandBy(int layer){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_STANDBY_INDEX);
        manager.reverse();
        manager.reset();
        manager.activate();
        manager.start();
    }

    public static void reveal(int layer){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_REVEAL_INDEX);
        manager.randomizeCycles(CYCLES_REVEAL_MIN, CYCLES_REVEAL_MAX, false);
        manager.randomizeDelays(CYCLES_REVEAL_DELAY_MIN, CYCLES_REVEAL_DELAY_MAX, false);
        manager.start();
    }

    public static void lowerBases(int layer, final Runnable post){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_RAISE_INDEX);
        manager.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true);
        manager.randomizeDelays(CYCLES_RAISE_BASE_DELAY_MIN, CYCLES_RAISE_BASE_DELAY_MAX, true);
        manager.reverse();
        manager.reset();
        manager.start();

        manager.connect(new VLVConnection.EndPoint(){

            @Override
            public void run(VLVConnection connections){
                post.run();
            }
        });
    }

    public static void deactivatePiece(int layer, int instance){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_DEACTIVATE_INDEX);

        int size = manager.size();

        VLVRunner runner;
        VLVRunnerEntry entry;

        for(int i = 0; i < size; i++){
            runner = (VLVRunner)manager.get(i);

            entry = runner.get(instance);
            entry.activate();

            runner.start();
        }

        VLVManager managerreveal = (VLVManager)rootmanager.get(layer).get(RUNNER_REVEAL_INDEX);
        size = managerreveal.size();

        for(int i = 0; i < size; i++){
            runner = (VLVRunner)managerreveal.get(i);

            entry = runner.get(instance);
            entry.finish();
            entry.targetSync();
        }
    }

    public static void reveal(int layer, int instance, final Runnable post){
        VLVManager manager = (VLVManager)rootmanager.get(layer).get(RUNNER_REVEAL_INDEX);
        int size = manager.size();

        VLVRunner runner;
        VLVRunnerEntry entry;

        for(int i = 0; i < size; i++){
            runner = (VLVRunner)manager.get(i);

            entry = runner.get(instance);
            entry.reset();

            runner.start();
        }

        if(post != null){
            manager.connect(new VLVConnection.EndPoint(){

                @Override
                public void run(VLVConnection connections){
                    post.run();
                    connections.removeCurrentConnection();
                }
            });
        }
    }

    public static void revealResetTimer(){
        controlreveal.reset();
        controlreveal.fastForward(CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT);
    }

    public static void revealRepeat(final int layer){
        controlreveal = new VLVControl(CYCLES_REVEAL_REPEAT, VLVariable.LOOP_FORWARD, new VLTaskTargetValue(new VLTask.Task<VLVControl>(){

            @Override
            public void run(VLTask<VLVControl> task, VLVControl var){
                reveal(layer);
            }
        }));

        controlrunner.add(new VLVRunnerEntry(controlreveal, 0));
    }

    public static void removeDeactivationControl(){
        controlrunner.remove(controlrunner.size() - 1);
    }

    public static void destroy(){

    }
}
