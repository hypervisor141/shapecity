package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSG;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;

import java.util.Random;

public final class Register{

    protected static void registerCenter(){
        FSG.Assembler config = new FSG.Assembler();
        config.ENABLE_DATA_PACK = true;
        config.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        config.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        config.SYNC_MODELARRAY_AND_BUFFER = true;
        config.SYNC_POSITION_AND_SCHEMATICS = true;
        config.SYNC_POSITION_AND_BUFFER = true;
        config.SYNC_COLOR_AND_BUFFER = true;
        config.SYNC_TEXCOORD_AND_BUFFER = true;
        config.SYNC_NORMAL_AND_BUFFER = true;
        config.SYNC_INDICES_AND_BUFFER = true;
        config.INSTANCE_SHARE_POSITIONS = false;
        config.INSTANCE_SHARE_COLORS = false;
        config.INSTANCE_SHARE_TEXCOORDS = false;
        config.INSTANCE_SHARE_NORMALS = false;
        config.LOAD_MODELS = true;
        config.LOAD_POSITIONS = true;
        config.LOAD_COLORS = false;
        config.LOAD_TEXCOORDS = true;
        config.LOAD_NORMALS = true;
        config.LOAD_INDICES = true;
        config.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        config.ENABLE_COLOR_FILL = false;
        config.DRAW_MODE_INDEXED = true;
        config.configure();

        FSG.DataPack pack = new FSG.DataPack(null, Game.texCenter, MATERIAL_WHITE_RUBBER, null);
        FSG.Registration reg = AUTOMATOR.addScannerSingle(config, pack, "center_Cylinder.001", GLES32.GL_TRIANGLES);

        reg.addProgram(programCenterDepth);
        reg.addProgram(programCenter);

        center = reg.mesh();
    }

    protected static void registerBase(){
        FSG.Assembler config = new FSG.Assembler();
        config.ENABLE_DATA_PACK = true;
        config.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        config.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        config.SYNC_POSITION_AND_SCHEMATICS = true;
        config.SYNC_MODELARRAY_AND_BUFFER = true;
        config.SYNC_POSITION_AND_BUFFER = true;
        config.SYNC_COLOR_AND_BUFFER = true;
        config.SYNC_TEXCOORD_AND_BUFFER = true;
        config.SYNC_NORMAL_AND_BUFFER = true;
        config.SYNC_INDICES_AND_BUFFER = true;
        config.INSTANCE_SHARE_POSITIONS = false;
        config.INSTANCE_SHARE_COLORS = false;
        config.INSTANCE_SHARE_TEXCOORDS = false;
        config.INSTANCE_SHARE_NORMALS = false;
        config.LOAD_MODELS = true;
        config.LOAD_POSITIONS = true;
        config.LOAD_COLORS = true;
        config.LOAD_TEXCOORDS = false;
        config.LOAD_NORMALS = true;
        config.LOAD_INDICES = true;
        config.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        config.ENABLE_COLOR_FILL = true;
        config.DRAW_MODE_INDEXED = true;
        config.configure();

        FSG.DataPack pack = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_BASE), null, MATERIAL_WHITE_RUBBER, null);

        FSG.Registration reg = AUTOMATOR.addScannerSingle(config, pack, "base_Cube.036", GLES32.GL_TRIANGLES);
        base = reg.mesh();

        reg.addProgram(programBaseDepth);
        reg.addProgram(programBase);
    }

    protected static void registerLayers(){
        FSG.Assembler config = new FSG.Assembler();
        config.ENABLE_DATA_PACK = true;
        config.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        config.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        config.SYNC_POSITION_AND_SCHEMATICS = true;
        config.SYNC_MODELARRAY_AND_BUFFER = true;
        config.SYNC_POSITION_AND_BUFFER = true;
        config.SYNC_COLOR_AND_BUFFER = true;
        config.SYNC_TEXCOORD_AND_BUFFER = true;
        config.SYNC_NORMAL_AND_BUFFER = true;
        config.SYNC_INDICES_AND_BUFFER = true;
        config.INSTANCE_SHARE_POSITIONS = true;
        config.INSTANCE_SHARE_COLORS = false;
        config.INSTANCE_SHARE_TEXCOORDS = true;
        config.INSTANCE_SHARE_NORMALS = true;
        config.LOAD_MODELS = true;
        config.LOAD_POSITIONS = true;
        config.LOAD_COLORS = true;
        config.LOAD_TEXCOORDS = true;
        config.LOAD_NORMALS = true;
        config.LOAD_INDICES = true;
        config.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        config.ENABLE_COLOR_FILL = true;
        config.DRAW_MODE_INDEXED = true;
        config.configure();

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

        FSG.Registration reg1 = AUTOMATOR.addScannerInstanced(config, new FSG.DataGroup(group1), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        FSG.Registration reg2 = AUTOMATOR.addScannerInstanced(config, new FSG.DataGroup(group2), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        FSG.Registration reg3 = AUTOMATOR.addScannerInstanced(config, new FSG.DataGroup(group3), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reg1.addProgram(programLayersDepth);
        reg2.addProgram(programLayersDepth);
        reg3.addProgram(programLayersDepth);

        reg1.addProgram(programLayers);
        reg2.addProgram(programLayers);
        reg3.addProgram(programLayers);

        layer1 = reg1.mesh();
        layer2 = reg2.mesh();
        layer3 = reg3.mesh();
    }

    protected static void registerPillars(FSG.Automator automator){
        FSG.Assembler config = new FSG.Assembler();
        config.ENABLE_DATA_PACK = true;
        config.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        config.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        config.SYNC_POSITION_AND_SCHEMATICS = true;
        config.SYNC_MODELARRAY_AND_BUFFER = true;
        config.SYNC_POSITION_AND_BUFFER = true;
        config.SYNC_COLOR_AND_BUFFER = true;
        config.SYNC_TEXCOORD_AND_BUFFER = true;
        config.SYNC_NORMAL_AND_BUFFER = true;
        config.SYNC_INDICES_AND_BUFFER = true;
        config.INSTANCE_SHARE_POSITIONS = true;
        config.INSTANCE_SHARE_COLORS = false;
        config.INSTANCE_SHARE_TEXCOORDS = true;
        config.INSTANCE_SHARE_NORMALS = true;
        config.LOAD_MODELS = true;
        config.LOAD_POSITIONS = true;
        config.LOAD_COLORS = true;
        config.LOAD_TEXCOORDS = false;
        config.LOAD_NORMALS = true;
        config.LOAD_INDICES = true;
        config.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        config.ENABLE_COLOR_FILL = true;
        config.DRAW_MODE_INDEXED = true;
        config.configure();

        VLListType<FSG.DataPack> packs = new VLListType<>(PILLAR_INSTANCE_COUNT, 0);
        Random random = new Random();

        for(int i = 0; i < PILLAR_INSTANCE_COUNT; i++){
            float[] color = new float[]{
                    random.nextFloat(),
                    random.nextFloat(),
                    random.nextFloat(),
                    random.nextFloat(),
            };
            packs.add(new FSG.DataPack(new VLArrayFloat(Animations.COLOR_PILLARS), null, MATERIAL_WHITE_RUBBER, null));
        }

        FSG.Registration reg = automator.addScannerInstanced(config, new FSG.DataGroup(packs), "pillars", GLES32.GL_TRIANGLES, PILLAR_INSTANCE_COUNT);
        pillars = reg.mesh();

        reg.addProgram(programPillarsDepth);
        reg.addProgram(programPillars);
    }
}
