package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGAssembler;
import com.nurverek.firestorm.FSGAutomator;
import com.nurverek.firestorm.FSGBluePrint;
import com.nurverek.firestorm.FSGScanner;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSLinkType;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLListType;

import java.util.Arrays;

public final class Layer extends FSGBluePrint{

    public static final int INSTANCE_COUNT = 24;

    public FSP program;
    public FSMesh mesh;
    public String name;

    public Layer(FSP program, String name){
        this.program = program;
        this.name = name;
    }

    @Override
    public FSGScanner register(FSG gen){
        FSGAssembler config = new FSGAssembler();
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
        config.LOAD_COLORS = false;
        config.LOAD_TEXCOORDS = true;
        config.LOAD_NORMALS = true;
        config.LOAD_INDICES = true;
        config.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        config.DRAW_MODE_INDEXED = true;
        config.configure();

        return new FSGScanner.Instanced(this, config, name, GLES32.GL_TRIANGLES, INSTANCE_COUNT);
    }

    @Override
    protected void adjustPreAssembly(FSMesh mesh, FSInstance instance){
        instance.data().colors(new VLArrayFloat(Animations.COLOR_LAYER1));
        instance.colorTexture(Game.texArrayLayer1);
        instance.lightMaterial(Loader.MATERIAL_OBSIDIAN);
    }

    @Override
    protected void adjustPostScan(FSMesh mesh){

    }

    @Override
    public FSBufferLayout layout(FSMesh mesh, FSBufferManager manager){
        int modelbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));
        int texcontrolbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));
        int colorbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));

        FSBufferLayout layout = new FSBufferLayout(mesh);
        layout.add(manager, modelbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_MODEL));

        layout.add(manager, colorbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_COLOR));

        layout.add(manager, Loader.BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_TEXCOORD))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_NORMAL));

        layout.add(manager, Loader.BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, FSG.ELEMENT_INDEX));

        layout.add(manager, texcontrolbuffer, 1)
                .addLink(new FSBufferLayout.EntryLink(FSBufferLayout.LINK_SEQUENTIAL_SINGULAR, 0, 0, 1, 1, 4));

        return layout;
    }

    @Override
    public void makeLinks(FSMesh mesh){
        VLListType<FSLinkType> links1 = new VLListType<>(1, 0);

        float[] array1 = new float[INSTANCE_COUNT];
        Arrays.fill(array1, Animations.TEXCONTROL_IDLE);

        links1.add(new ModColor.TextureControlLink(new VLArrayFloat(array1)));
        mesh.initLinks(links1);
    }

    @Override
    protected void adjustPostBuffer(FSMesh mesh){

    }

    @Override
    public void program(FSG gen, FSMesh mesh){
        program.addMesh(mesh);
    }
}
