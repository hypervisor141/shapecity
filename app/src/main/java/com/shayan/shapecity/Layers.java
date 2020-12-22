package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSG;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;

import java.util.Random;

public final class Register{

    protected static void registerLayers(FSG.Automator automator){
        baseconfig.ENABLE_DATA_PACK = true;
        baseconfig.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        baseconfig.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        baseconfig.SYNC_MODELARRAY_AND_BUFFER = true;
        baseconfig.SYNC_POSITION_AND_SCHEMATICS = true;
        baseconfig.SYNC_POSITION_AND_BUFFER = true;
        baseconfig.SYNC_COLOR_AND_BUFFER = true;
        baseconfig.SYNC_TEXCOORD_AND_BUFFER = true;
        baseconfig.SYNC_NORMAL_AND_BUFFER = true;
        baseconfig.SYNC_INDICES_AND_BUFFER = true;
        baseconfig.INSTANCE_SHARE_POSITIONS = true;
        baseconfig.INSTANCE_SHARE_COLORS = false;
        baseconfig.INSTANCE_SHARE_TEXCOORDS = true;
        baseconfig.INSTANCE_SHARE_NORMALS = true;
        baseconfig.LOAD_MODELS = true;
        baseconfig.LOAD_POSITIONS = true;
        baseconfig.LOAD_COLORS = true;
        baseconfig.LOAD_TEXCOORDS = true;
        baseconfig.LOAD_NORMALS = true;
        baseconfig.LOAD_INDICES = true;
        baseconfig.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        baseconfig.ENABLE_COLOR_FILL = true;
        baseconfig.DRAW_MODE_INDEXED = true;
        baseconfig.configure();

        VLListType<FSG.DataPack> group1 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<FSG.DataPack> group2 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<FSG.DataPack> group3 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);

        FSG.DataPack pack1 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER1), Game.texArrayLayer1, MATERIAL_OBSIDIAN, null);
        FSG.DataPack pack2 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER2), Game.texArrayLayer2, MATERIAL_OBSIDIAN, null);
        FSG.DataPack pack3 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER3), Game.texArrayLayer3, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group1.add(pack1);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group2.add(pack2);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group3.add(pack3);
        }

        FSG.Registration reg1 = automator.addScannerInstanced(baseconfig, new FSG.DataGroup(group1), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        FSG.Registration reg2 = automator.addScannerInstanced(baseconfig, new FSG.DataGroup(group2), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        FSG.Registration reg3 = automator.addScannerInstanced(baseconfig, new FSG.DataGroup(group3), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reg1.addProgram(programLayers);
        reg2.addProgram(programLayers);
        reg3.addProgram(programLayers);

        layer1 = reg1.mesh();
        layer2 = reg2.mesh();
        layer3 = reg3.mesh();

        baseconfig.INSTANCE_SHARE_COLORS = true;
    }
}
