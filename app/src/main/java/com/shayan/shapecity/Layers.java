package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSLinkType;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLListType;

import java.util.Arrays;

public final class Layers{

    public static final int INSTANCE_COUNT = 24;

    public static FSP program;
    public static FSMesh layer1;
    public static FSMesh layer2;
    public static FSMesh layer3;
    public static FSMesh[] layers;

    protected static void register(FSG gen){
        program = new FSP(Loader.DEBUG_MODE_PROGRAMS);

        FSG.Automator automator = gen.automator();
        FSBufferManager bmanager = gen.bufferManager();
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

        VLListType<FSG.DataPack> group1 = new VLListType<>(INSTANCE_COUNT, 10);
        VLListType<FSG.DataPack> group2 = new VLListType<>(INSTANCE_COUNT, 10);
        VLListType<FSG.DataPack> group3 = new VLListType<>(INSTANCE_COUNT, 10);

        FSG.DataPack pack1 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER1), Game.texArrayLayer1, Loader.MATERIAL_OBSIDIAN, null);
        FSG.DataPack pack2 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER2), Game.texArrayLayer2, Loader.MATERIAL_OBSIDIAN, null);
        FSG.DataPack pack3 = new FSG.DataPack(new VLArrayFloat(Animations.COLOR_LAYER3), Game.texArrayLayer3, Loader.MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < INSTANCE_COUNT; i++){
            group1.add(pack1);
        }
        for(int i = 0; i < INSTANCE_COUNT; i++){
            group2.add(pack2);
        }
        for(int i = 0; i < INSTANCE_COUNT; i++){
            group3.add(pack3);
        }

        FSG.Registration reg1 = automator.addScannerInstanced(config, new FSG.DataGroup(group1), "layer1.", GLES32.GL_TRIANGLES, INSTANCE_COUNT);
        FSG.Registration reg2 = automator.addScannerInstanced(config, new FSG.DataGroup(group2), "layer2.", GLES32.GL_TRIANGLES, INSTANCE_COUNT);
        FSG.Registration reg3 = automator.addScannerInstanced(config, new FSG.DataGroup(group3), "layer3.", GLES32.GL_TRIANGLES, INSTANCE_COUNT);

        layer1 = reg1.mesh();
        layer2 = reg2.mesh();
        layer3 = reg3.mesh();

        layers = new FSMesh[]{
                layer1,
                layer2,
                layer3,
        };
        FSBufferLayout[] layerlayouts = new FSBufferLayout[]{
                reg1.bufferLayout(),
                reg2.bufferLayout(),
                reg3.bufferLayout(),
        };

        FSBufferLayout layout;

        for(int i = 0; i < layerlayouts.length; i++){
            layout = layerlayouts[i];

            int modelbuffer = bmanager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));
            int texcontrolbuffer = bmanager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));
            int colorbuffer = bmanager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));

            layout.add(bmanager, modelbuffer, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_MODEL));

            layout.add(bmanager, colorbuffer, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_COLOR));

            layout.add(bmanager, Loader.BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_POSITION))
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_TEXCOORD))
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_NORMAL));

            layout.add(bmanager, Loader.BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, FSG.ELEMENT_INDEX));

            layout.add(bmanager, texcontrolbuffer, 1)
                    .addLink(new FSBufferLayout.EntryLink(FSBufferLayout.LINK_SEQUENTIAL_SINGULAR, 0, 0, 1, 1, 4));
        }

        reg1.addProgram(program);
        reg2.addProgram(program);
        reg3.addProgram(program);
    }

    public static void makeLinks(){
        VLListType<FSLinkType> links1 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links2 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links3 = new VLListType<>(1, 0);

        float[] array1 = new float[INSTANCE_COUNT];
        float[] array2 = new float[INSTANCE_COUNT];
        float[] array3 = new float[INSTANCE_COUNT];

        Arrays.fill(array1, Animations.TEXCONTROL_IDLE);
        Arrays.fill(array2, Animations.TEXCONTROL_IDLE);
        Arrays.fill(array3, Animations.TEXCONTROL_IDLE);

        links1.add(new ModColor.TextureControlLink(new VLArrayFloat(array1)));
        links2.add(new ModColor.TextureControlLink(new VLArrayFloat(array2)));
        links3.add(new ModColor.TextureControlLink(new VLArrayFloat(array3)));

        layer1.initLinks(links1);
        layer2.initLinks(links2);
        layer3.initLinks(links3);
    }

    public static void program(FSG gen){
        program.modify(new ModModel.UBO(1, INSTANCE_COUNT), FSConfig.POLICY_ALWAYS);
        program.modify(new ModColor.TextureAndUBO(1, INSTANCE_COUNT, true, false, true), FSConfig.POLICY_ALWAYS);
        program.modify(new ModLight.Point(Loader.GAMMA, null, Loader.BRIGHTNESS, Loader.lightPoint, null, Loader.MATERIAL_WHITE_RUBBER.getGLSLSize()), FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0));
        program.build();

        gen.programSet(Loader.MAIN_PROGRAMSET).add(program);
    }
}
