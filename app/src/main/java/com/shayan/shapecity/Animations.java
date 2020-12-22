package com.shayan.shapecity;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskTargetValue;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVConnection;
import com.nurverek.vanguard.VLVControl;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVManager;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public final class Animations{

    public static final float[] COLOR_WHITE = new float[]{ 1F, 1F, 1F, 1F };
    public static final float[] COLOR_WHITE_LESS = new float[]{ 0.8F, 0.8F, 0.8F, 1F };
    public static final float[] COLOR_WHITE_LESS2 = new float[]{ 0.6F, 0.6F, 0.6F, 1F };
    public static final float[] COLOR_ORANGE = new float[]{ 1.0F, 0.8F, 0F, 1F };
    public static final float[] COLOR_OBSIDIAN = new float[]{ 0.4F, 0.4F, 0.4F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS = new float[]{ 0.15F, 0.15F, 0.15F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS2 = new float[]{ 0.1F, 0.1F, 0.1F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS3 = new float[]{ 0.05F, 0.05F, 0.05F, 1F };
    public static final float[] COLOR_GOLD = new float[]{ 0.83F, 0.68F, 0.21F, 1F };
    public static final float[] COLOR_RED = new float[]{ 1.0F, 0.3F, 0F, 1F };
    public static final float[] COLOR_PURPLE = new float[]{ 0F, 0.137F, 0.220F, 1F };

    public static final float[] COLOR_BASE = COLOR_WHITE;
    public static final float[] COLOR_BASE_LINING = COLOR_ORANGE;
    public static final float[] COLOR_LAYER1 = COLOR_RED;
    public static final float[] COLOR_LAYER2 = COLOR_RED;
    public static final float[] COLOR_LAYER3 = COLOR_RED;
    public static final float[] COLOR_PILLARS = COLOR_OBSIDIAN_LESS2;
    private static final float[] COLOR_BLINK = COLOR_ORANGE;
    private static final float[] COLOR_DEACTIVATED = COLOR_PURPLE;
    public static final float[] COLOR_STANDBY = COLOR_RED;

    public static final float TEXCONTROL_IDLE = 0F;
    public static final float TEXCONTROL_ACTIVE = 1F;

    private static final int CYCLES_BLINK = 20;
    private static final int CYCLES_DEACTIVATED = 60;
    private static final int CYCLES_STANDBY = 100;
    private static final int CYCLES_ROTATE = 30;
    private static final int CYCLES_RAISE_BASE_MIN = 80;
    private static final int CYCLES_RAISE_BASE_MAX = 120;
    private static final int CYCLES_RAISE_BASE_DELAY_MIN = 0;
    private static final int CYCLES_RAISE_BASE_DELAY_MAX = 40;
    private static final int CYCLES_LOWER_BASE_MIN = 60;
    private static final int CYCLES_LOWER_BASE_MAX = 120;
    private static final int CYCLES_LOWER_BASE_DELAY_MIN = 0;
    private static final int CYCLES_LOWER_BASE_DELAY_MAX = 50;
    private static final int CYCLES_BOUNCE = 200;
    private static final int CYCLES_BOUNCE_REVERSE = 50;
    private static final int CYCLES_REVEAL_MIN = 45;
    private static final int CYCLES_REVEAL_MAX = 80;
    private static final int CYCLES_REVEAL_DELAY_MIN = 0;
    private static final int CYCLES_REVEAL_DELAY_MAX = 30;
    private static final int CYCLES_REVEAL_REPEAT = 360;
    private static final int CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT = 240;
    private static final int CYCLES_REVEAL_INPUT = 50;
    private static final int CYCLES_TEXCONTROL = 100;

    private static final float Y_REDUCTION = 0.5f;
    private static final float Y_BOUNCE_HEIGHT_MULTIPLIER = 0.4f;
    private static final float Y_BASE_HEIGHT_MULTIPLIER = 0.3f;

    private static VLVManager rootmanager;
    private static VLVManager controlmanager;
    protected static VLVRunner controlrunner;

    private static VLVControl controlreveal;

    public static void initialize(Loader loader){
        rootmanager = new VLVManager(3, 0);
        controlmanager = new VLVManager(1, 0);
        controlrunner = new VLVRunner(3, 0);

        controlmanager.add(controlrunner);

        float[][] colors = new float[][]{ COLOR_LAYER1, COLOR_LAYER2, COLOR_LAYER3 };

        int size = Loader.layers.length;
        int itemsize = Layer.INSTANCE_COUNT * size;

        for(int i = 0; i < size; i++){
            FSMesh layer = Loader.layers[i].mesh();
            VLArrayFloat linkdata = ((ModColor.TextureControlLink)layer.link(0)).data;
            VLVManager layermanager = new VLVManager(4, 0);

            // basics
            for(int i2 = 0; i2 < layer.size(); i2++){
                FSInstance instance = layer.instance(i2);
                FSMatrixModel modelmatrix = instance.modelMatrix();
                FSSchematics schematics = instance.schematics();

                modelmatrix.getY(0).set(modelmatrix.getY(0).get() - Y_REDUCTION);

                modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ONE, VLV.ZERO);
                modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ZERO, VLV.ONE);

                schematics.inputBounds().add(new FSBoundsCuboid(schematics, 50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC, 40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }

            // raise bases
            VLVRunner raise = new VLVRunner(itemsize, 0);
            layermanager.add(raise);
            
            for(int i2 = 0; i2 < layer.size(); i2++){
                FSMatrixModel modelmatrix = layer.instance(i2).modelMatrix();
                float yraisebase = 0;

                for(int i3 = 0; i3 < i; i3++){
                    yraisebase += Loader.layers[i3].mesh().instance(i2).schematics().modelHeight() * Y_BASE_HEIGHT_MULTIPLIER;
                }

                VLVCurved translateraisey = new VLVCurved(0f, yraisebase, CYCLES_RAISE_BASE_MAX, VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_SINE_SQRT);
                translateraisey.SYNCER.add(new VLVMatrix.Definition(modelmatrix));

                modelmatrix.addRowTranslation(VLV.ZERO, translateraisey, VLV.ZERO);
                raise.add(new VLVRunnerEntry(translateraisey, 0));
            }

            // bounce
            VLVRunner bounce = new VLVRunner(itemsize, 0);
            VLVManager reveal = new VLVManager(3, 0);

            reveal.add(bounce);
            layermanager.add(reveal);
            
            for(int i2 = 0; i2 < layer.size(); i2++){
                FSInstance instance = layer.instance(i2);
                FSMatrixModel modelmatrix = instance.modelMatrix();

                float ybounce = instance.schematics().modelHeight() * Y_BOUNCE_HEIGHT_MULTIPLIER;

                VLVCurved translatebouncey = new VLVCurved(0f, ybounce, CYCLES_BOUNCE, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                translatebouncey.SYNCER.add(new VLVMatrix.Definition(modelmatrix));

                modelmatrix.addRowTranslation(VLV.ZERO, translatebouncey, VLV.ZERO);
                bounce.add(new VLVRunnerEntry(translatebouncey, 0));
            }

            // standby
            VLVRunner sbred = new VLVRunner(itemsize, 0);
            VLVRunner sbgreen = new VLVRunner(itemsize, 0);
            VLVRunner sbblue = new VLVRunner(itemsize, 0);
            VLVRunner sbalpha = new VLVRunner(itemsize, 0);
            
            VLVManager standby = new VLVManager(4, 0);
            standby.add(sbred);
            standby.add(sbgreen);
            standby.add(sbblue);
            standby.add(sbalpha);

            layermanager.add(standby);
            
            for(int i2 = 0; i2 < layer.size(); i2++){
                VLArrayFloat colorarray = layer.instance(i2).colors();

                VLVCurved standbyred = new VLVCurved(colors[i][0], COLOR_STANDBY[0], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved standbygreen = new VLVCurved(colors[i][1], COLOR_STANDBY[1], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved standbyblue = new VLVCurved(colors[i][2], COLOR_STANDBY[2], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved standbyalpha = new VLVCurved(colors[i][3], COLOR_STANDBY[3], CYCLES_STANDBY, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);

                standbyred.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 0));
                standbygreen.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 1));
                standbyblue.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 2));
                standbyalpha.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 3));

                sbred.add(new VLVRunnerEntry(standbyred, 0));
                sbgreen.add(new VLVRunnerEntry(standbygreen, 0));
                sbblue.add(new VLVRunnerEntry(standbyblue, 0));
                sbalpha.add(new VLVRunnerEntry(standbyalpha, 0));
            }

            // blink
            VLVRunner bred = new VLVRunner(itemsize, 0);
            VLVRunner bgreen = new VLVRunner(itemsize, 0);
            VLVRunner bblue = new VLVRunner(itemsize, 0);
            VLVRunner balpha = new VLVRunner(itemsize, 0);

            VLVManager blink = new VLVManager(4, 0);
            blink.add(bred);
            blink.add(bgreen);
            blink.add(bblue);
            blink.add(balpha);

            reveal.add(blink);
            
            for(int i2 = 0; i2 < layer.size(); i2++){
                VLArrayFloat colorarray = layer.instance(i2).colors();

                VLVCurved blinkred = new VLVCurved(colors[i][0], COLOR_BLINK[0], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved blinkgreen = new VLVCurved(colors[i][1], COLOR_BLINK[1], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved blinkblue = new VLVCurved(colors[i][2], COLOR_BLINK[2], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved blinkalpha = new VLVCurved(colors[i][3], COLOR_BLINK[3], CYCLES_BLINK, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);

                blinkred.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 0));
                blinkgreen.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 1));
                blinkblue.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 2));
                blinkalpha.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 3));

                bred.add(new VLVRunnerEntry(blinkred, 0));
                bgreen.add(new VLVRunnerEntry(blinkgreen, 0));
                bblue.add(new VLVRunnerEntry(blinkblue, 0));
                balpha.add(new VLVRunnerEntry(blinkalpha, 0));
            }

            // deactivate
            VLVRunner dred = new VLVRunner(itemsize, 0);
            VLVRunner dgreen = new VLVRunner(itemsize, 0);
            VLVRunner dblue = new VLVRunner(itemsize, 0);
            VLVRunner dalpha = new VLVRunner(itemsize, 0);

            VLVManager deactivate = new VLVManager(4, 0);
            deactivate.add(dred);
            deactivate.add(dgreen);
            deactivate.add(dblue);
            deactivate.add(dalpha);

            layermanager.add(deactivate);

            for(int i2 = 0; i2 < layer.size(); i2++){
                VLArrayFloat colorarray = layer.instance(i2).colors();

                VLVCurved deactivatedred = new VLVCurved(colors[i][0], COLOR_DEACTIVATED[0], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved deactivatedgreen = new VLVCurved(colors[i][1], COLOR_DEACTIVATED[1], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved deactivatedblue = new VLVCurved(colors[i][2], COLOR_DEACTIVATED[2], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                VLVCurved deactivatedalpha = new VLVCurved(colors[i][3], COLOR_DEACTIVATED[3], CYCLES_DEACTIVATED, VLVariable.LOOP_NONE, VLVCurved.CURVE_ACC_DEC_CUBIC);

                deactivatedred.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 0));
                deactivatedgreen.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 1));
                deactivatedblue.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 2));
                deactivatedalpha.SYNCER.add(new VLArray.DefinitionVLV(colorarray, 3));

                dred.add(new VLVRunnerEntry(deactivatedred, 0));
                dgreen.add(new VLVRunnerEntry(deactivatedgreen, 0));
                dblue.add(new VLVRunnerEntry(deactivatedblue, 0));
                dalpha.add(new VLVRunnerEntry(deactivatedalpha, 0));
            }

            reveal.deactivate();
            deactivate.deactivate();

            // texture blink
            VLVRunner texblink = new VLVRunner(itemsize, 0);
            reveal.add(texblink);

            for(int i2 = 0; i2 < layer.size(); i2++){
                VLVCurved texblinkvar = new VLVCurved(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, CYCLES_TEXCONTROL, VLVariable.LOOP_RETURN_ONCE, VLVCurved.CURVE_ACC_DEC_CUBIC);
                texblinkvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

                texblink.add(new VLVRunnerEntry(texblinkvar, 0));
            }

            rootmanager.add(layermanager);
        }

        rootmanager.findEndPointIndex();
        rootmanager.connections(1, 1);
        rootmanager.targetSync();

        VLVManager loadermanager = loader.vManager();
        loadermanager.add(rootmanager);
        loadermanager.add(controlmanager);
    }

    public static VLVRunner getRaise(int layer){
        return (VLVRunner)rootmanager.get(layer).get(0);
    }

    public static VLVManager getReveal(int layer){
        return (VLVManager)rootmanager.get(layer).get(1);
    }

    public static VLVManager getStandBy(int layer){
        return (VLVManager)rootmanager.get(layer).get(2);
    }

    public static VLVManager getDeactivate(int layer){
        return (VLVManager)rootmanager.get(layer).get(3);
    }

    public static VLVRunner getBounce(int layer){
        return (VLVRunner)getReveal(layer).get(0);
    }

    public static VLVManager getBlink(int layer){
        return (VLVManager)getReveal(layer).get(1);
    }

    public static VLVRunner getTexBlink(int layer){
        return (VLVRunner)getReveal(layer).get(2);
    }

    public static void raiseBases(int layer){
        VLVRunner runner = getRaise(layer);
        runner.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true, false);
        runner.start();
    }

    public static void standBy(int layer){
        getStandBy(layer).start();
    }

    public static void unstandBy(int layer){
        VLVManager manager = getStandBy(layer);
        manager.reverse();
        manager.reset();
        manager.activate();
        manager.start();
    }

    public static void reveal(int layer){
        VLVRunner bounce = getBounce(layer);
        VLVManager blink = getBlink(layer);
        VLVRunner texblink = getTexBlink(layer);

        VLVRunnerEntry entry;

        for(int i = 0; i < Game.enabledPieces.length; i++){
            entry = bounce.get(i);

            if(!entry.active() && Game.enabledPieces[i] && Game.activatedSymbols.get(i) == -1){
                entry.randomizeCycles(CYCLES_REVEAL_MIN, CYCLES_REVEAL_MAX, false, false);
                entry.randomizeDelays(CYCLES_REVEAL_DELAY_MIN, CYCLES_REVEAL_DELAY_MAX, false, false);

                for(int i2 = 0; i2 < 4; i2++){
                    entry = (VLVRunnerEntry)blink.get(i2).get(i);
                    entry.randomizeCycles(CYCLES_REVEAL_MIN, CYCLES_REVEAL_MAX, false, false);
                    entry.randomizeDelays(CYCLES_REVEAL_DELAY_MIN, CYCLES_REVEAL_DELAY_MAX, false, false);
                }

                entry = texblink.get(i);
                entry.randomizeCycles(CYCLES_REVEAL_MIN, CYCLES_REVEAL_MAX, false, false);
                entry.randomizeDelays(CYCLES_REVEAL_DELAY_MIN, CYCLES_REVEAL_DELAY_MAX, false, false);
            }
        }

        getReveal(layer).start();
    }

    public static void reveal(int layer, int instance, final Runnable post){
        VLVRunner bounce = getBounce(layer);
        VLVManager blink = getBlink(layer);
        VLVRunner texblink = getTexBlink(layer);
        VLVRunnerEntry entry;

        entry = bounce.get(instance);
        entry.initialize(CYCLES_REVEAL_INPUT);
        entry.delay(0);
        entry.reset();
        entry.activate();

        for(int i = 0; i < 4; i++){
            entry = (VLVRunnerEntry)blink.get(i).get(instance);
            entry.initialize(CYCLES_REVEAL_INPUT);
            entry.delay(0);
            entry.reset();
            entry.activate();
        }

        entry = texblink.get(instance);
        entry.initialize(CYCLES_REVEAL_INPUT);
        entry.delay(0);
        entry.reset();
        entry.activate();

        bounce.start();
        blink.start();
        texblink.start();

        if(post != null){
            bounce.connect(new VLVConnection.EndPoint(){

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

        controlreveal.fastForward(CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT);
        controlrunner.add(new VLVRunnerEntry(controlreveal, 0));
    }

    public static void lowerBases(int layer, final Runnable post){
        VLVRunner bounce = getBounce(layer);
        bounce.initialize(-CYCLES_BOUNCE_REVERSE);
        bounce.activate();
        bounce.start();

        VLVRunner runner = getRaise(layer);
        runner.randomizeCycles(CYCLES_RAISE_BASE_MIN, CYCLES_RAISE_BASE_MAX, true, false);
        runner.reverse();
        runner.reset();
        runner.start();
        runner.connect(new VLVConnection.EndPoint(){

            @Override
            public void run(VLVConnection connections){
                post.run();
                connections.removeCurrentConnection();
            }
        });
    }

    public static void deactivate(int layer, int instance){
        VLVManager manager = getDeactivate(layer);

        for(int i = 0; i < manager.size(); i++){
            VLVRunner runner = (VLVRunner)manager.get(i);

            runner.get(instance).activate();
            runner.start();
        }

        VLVRunner bounce = getBounce(layer);
        VLVManager blink = getBlink(layer);
        VLVRunner texblink = getTexBlink(layer);
        VLVariable var;

        var = ((VLVariable)((VLVRunnerEntry)bounce.get(instance)).target);
        var.setLoop(VLVariable.LOOP_NONE);
        var.activate();

        if(!var.isIncreasing()){
            var.reverse();
        }

        for(int i = 0; i < 4; i++){
            var = ((VLVariable)((VLVRunnerEntry)blink.get(i).get(instance)).target);
            var.setLoop(VLVariable.LOOP_NONE);
            var.activate();

            if(!var.isIncreasing()){
                var.reverse();
            }
        }

        var = ((VLVariable)((VLVRunnerEntry)texblink.get(instance)).target);
        var.setLoop(VLVariable.LOOP_NONE);
        var.activate();

        if(!var.isIncreasing()){
            var.reverse();
        }
    }

    public static void removeDeactivationControl(){
        controlrunner.remove(controlrunner.size() - 1);
    }



    public static void destroy(){

    }
}
