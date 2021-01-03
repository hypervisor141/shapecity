package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
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

public final class Puzzle{

    public static final float[] COLOR_LAYER = Animation.COLOR_OBSIDIAN_LESS3;
    private static final float[] COLOR_BLINK = Animation.COLOR_OBSIDIAN_LESS3;
    private static final float[] COLOR_DEACTIVATED = Animation.COLOR_RED_LESS2;

    private static final VLVCurved.Curve CURVE_TYPE = VLVCurved.CURVE_DEC_COS_SQRT;

    public static final float TEXCONTROL_IDLE = 0F;
    public static final float TEXCONTROL_ACTIVE = 1F;

    private static final int CYCLES_BOUNCE = 80;
    private static final int CYCLES_BOUNCE_REVERSE = 40;

    private static final int CYCLES_REVEAL_MIN = 20;
    private static final int CYCLES_REVEAL_MAX = 25;
    private static final int CYCLES_REVEAL_DELAY_MIN = 0;
    private static final int CYCLES_REVEAL_DELAY_MAX = 10;

    private static final int CYCLES_REVEAL_REPEAT = 300;
    private static final int CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT = 180;
    private static final int CYCLES_REVEAL_INPUT = 20;

    private static final float Y_BOUNCE_HEIGHT_MULTIPLIER = 0.4f;
    private static final float Y_BASE_HEIGHT_MULTIPLIER = 0.3f;

    private static VLVManager rootmanager;
    private static VLVRunner controller;
    private static VLVRunner raisecontroller;
    private static VLVControl revealinterval;

    public static void initialize(Gen gen){
        rootmanager = new VLVManager(2, 0);
        controller = new VLVRunner(10, 10);
        raisecontroller = new VLVRunner(gen.pieces.size(), 0);

        VLVManager vmanager = gen.vManager();
        vmanager.add(rootmanager);
        vmanager.add(controller);
        vmanager.add(raisecontroller);

        setupGameplay(gen);
        raisePuzzleAndStartGame(gen);
    }

    public static void setupGameplay(Gen gen){
        VLArrayFloat linkdata = ((ModColor.TextureControlLink)gen.pieces.link(0)).data;

        // basics
        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            FSInstance instance = gen.pieces.instance(i2);
            FSMatrixModel modelmatrix = instance.modelMatrix();
            FSSchematics schematics = instance.schematics();

            modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ONE, VLV.ZERO);
            modelmatrix.addRowRotate(0, new VLV(90f), VLV.ZERO, VLV.ZERO, VLV.ONE);

            schematics.inputBounds().add(new FSBoundsCuboid(schematics, 50, 50f, 50f,
                    FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC,
                    40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
        }

        // bounce
        VLVRunner bounce = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVManager reveal = new VLVManager(3, 0);

        reveal.add(bounce);
        rootmanager.add(reveal);

        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            FSInstance instance = gen.pieces.instance(i2);
            FSMatrixModel modelmatrix = instance.modelMatrix();

            float ybounce = instance.schematics().modelHeight() * Y_BOUNCE_HEIGHT_MULTIPLIER;

            VLVCurved translatebouncey = new VLVCurved(0f, ybounce, CYCLES_BOUNCE, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);
            translatebouncey.SYNCER.add(new VLVMatrix.Definition(modelmatrix));

            modelmatrix.addRowTranslation(VLV.ZERO, translatebouncey, VLV.ZERO);
            bounce.add(new VLVRunnerEntry(translatebouncey, 0));
        }

        // blink
        VLVRunner bred = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner bgreen = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner bblue = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner balpha = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);

        VLVManager blink = new VLVManager(4, 0);
        blink.add(bred);
        blink.add(bgreen);
        blink.add(bblue);
        blink.add(balpha);

        reveal.add(blink);

        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            VLArrayFloat colorarray = gen.pieces.instance(i2).colors();

            VLVCurved blinkred = new VLVCurved(COLOR_LAYER[0], COLOR_BLINK[0], CYCLES_REVEAL_MAX, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);
            VLVCurved blinkgreen = new VLVCurved(COLOR_LAYER[1], COLOR_BLINK[1], CYCLES_REVEAL_MAX, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);
            VLVCurved blinkblue = new VLVCurved(COLOR_LAYER[2], COLOR_BLINK[2], CYCLES_REVEAL_MAX, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);
            VLVCurved blinkalpha = new VLVCurved(COLOR_LAYER[3], COLOR_BLINK[3], CYCLES_REVEAL_MAX, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);

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
        VLVRunner dred = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner dgreen = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner dblue = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        VLVRunner dalpha = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);

        VLVManager deactivate = new VLVManager(4, 0);
        deactivate.add(dred);
        deactivate.add(dgreen);
        deactivate.add(dblue);
        deactivate.add(dalpha);

        rootmanager.add(deactivate);

        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            VLArrayFloat colorarray = gen.pieces.instance(i2).colors();

            VLVCurved deactivatedred = new VLVCurved(COLOR_LAYER[0], COLOR_DEACTIVATED[0], CYCLES_REVEAL_MAX, VLVariable.LOOP_NONE, CURVE_TYPE);
            VLVCurved deactivatedgreen = new VLVCurved(COLOR_LAYER[1], COLOR_DEACTIVATED[1], CYCLES_REVEAL_MAX, VLVariable.LOOP_NONE, CURVE_TYPE);
            VLVCurved deactivatedblue = new VLVCurved(COLOR_LAYER[2], COLOR_DEACTIVATED[2], CYCLES_REVEAL_MAX, VLVariable.LOOP_NONE, CURVE_TYPE);
            VLVCurved deactivatedalpha = new VLVCurved(COLOR_LAYER[3], COLOR_DEACTIVATED[3], CYCLES_REVEAL_MAX, VLVariable.LOOP_NONE, CURVE_TYPE);

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

        //texture blink
        VLVRunner texblink = new VLVRunner(BPLayer.INSTANCE_COUNT, 0);
        reveal.add(texblink);

        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            VLVCurved texblinkvar = new VLVCurved(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, CYCLES_REVEAL_MAX, VLVariable.LOOP_RETURN_ONCE, CURVE_TYPE);
            texblinkvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

            texblink.add(new VLVRunnerEntry(texblinkvar, 0));
        }

        rootmanager.findEndPointIndex();
        rootmanager.connections(1, 1);
        rootmanager.targetSync();
    }

    public static void raisePuzzleAndStartGame(Gen gen){
        final float platformy = gen.platform.instance(0).modelMatrix().getY(0).get();

        for(int i2 = 0; i2 < gen.pieces.size(); i2++){
            FSInstance instance = gen.pieces.instance(i2);
            FSMatrixModel model = instance.modelMatrix();

            VLVCurved var = new VLVCurved(platformy + instance.schematics().modelHeight(), model.getY(2).get(), Platform.CYCLES_RISE, VLVariable.LOOP_NONE, Platform.CURVE_RISE);
            var.SYNCER.add(new VLVMatrix.Definition(model));

            model.setY(2, var);
            raisecontroller.add(new VLVRunnerEntry(var, Platform.DELAY_RISE));
        }

        raisecontroller.targetSync();
        raisecontroller.findEndPointIndex();
        raisecontroller.connections(1, 0);
        raisecontroller.connect(new VLVConnection.EndPoint(){

            @Override
            public void run(VLVConnection connection){
                raisecontroller.purge();
                Game.startGame(gen);
            }
        });

        raisecontroller.start();
    }

    public static VLVManager getReveal(){
        return (VLVManager)rootmanager.get(0);
    }

    public static VLVManager getDeactivate(){
        return (VLVManager)rootmanager.get(1);
    }

    public static VLVRunner getBounce(){
        return (VLVRunner)getReveal().get(0);
    }

    public static VLVManager getBlink(){
        return (VLVManager)getReveal().get(1);
    }

    public static VLVRunner getTexBlink(){
        return (VLVRunner)getReveal().get(2);
    }

    public static void reveal(){
        VLVRunner bounce = getBounce();
        VLVManager blink = getBlink();
        VLVRunner texblink = getTexBlink();

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

        getReveal().start();
    }

    public static void reveal(int instance, final Runnable post){
        VLVRunner bounce = getBounce();
        VLVManager blink = getBlink();
        VLVRunner texblink = getTexBlink();
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
        revealinterval.reset();
        revealinterval.fastForward(CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT);
    }

    public static void revealRepeat(){
        revealinterval = new VLVControl(CYCLES_REVEAL_REPEAT, VLVariable.LOOP_FORWARD, new VLTaskTargetValue(new VLTask.Task<VLVControl>(){

            @Override
            public void run(VLTask<VLVControl> task, VLVControl var){
                reveal();
            }
        }));

        revealinterval.fastForward(CYCLES_REVEAL_REPEAT_FASTFORWARD_AFTER_INPUT);

        controller.add(new VLVRunnerEntry(revealinterval, 0));
        controller.start();
    }

    public static void deactivate(int instance){
        VLVManager manager = getDeactivate();

        for(int i = 0; i < manager.size(); i++){
            VLVRunner runner = (VLVRunner)manager.get(i);

            runner.get(instance).activate();
            runner.start();
        }

        VLVRunner bounce = getBounce();
        VLVManager blink = getBlink();
        VLVRunner texblink = getTexBlink();
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
        controller.remove(controller.size() - 1);
    }
}
