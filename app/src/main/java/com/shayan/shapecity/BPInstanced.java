package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGAssembler;
import com.nurverek.firestorm.FSGScanner;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLBufferFloat;

public class BPInstanced extends CustomBluePrint{

    public FSP program;
    public int maxinstancecount;
    public int bindpointoffset;

    public BPInstanced(FSG gen, int maxinstancecount){
        this.maxinstancecount = maxinstancecount;

        bindpointoffset = Gen.UBOBINDPOINT;
        Gen.UBOBINDPOINT++;

        initialize(gen);
    }

    @Override
    protected void createPrograms(){
        program = new FSP(Gen.DEBUG_MODE_PROGRAMS);
        program.modify(new ModModel.UBO(1, maxinstancecount), FSConfig.POLICY_ALWAYS);
        program.modify(new ModColor.UBO(1, maxinstancecount), FSConfig.POLICY_ALWAYS);
        program.modify(new ModLight.Point(Gen.GAMMA, null, Gen.BRIGHTNESS, Gen.light, null, Gen.MATERIAL_WHITE_RUBBER.getGLSLSize()), FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0));
        program.build();
    }

    @Override
    protected void attachPrograms(FSG gen){
        gen.programSet(Gen.MAIN_PROGRAMSET).add(program);
    }

    @Override
    public FSGScanner register(FSG gen, String name){
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

        return new FSGScanner.Instanced(this, config, name, GLES32.GL_TRIANGLES, maxinstancecount);
    }

    @Override
    protected void preAssemblyAdjustment(FSMesh mesh, FSInstance instance){
        super.preAssemblyAdjustment(mesh, instance);
    }

    @Override
    protected void postScanAdjustment(FSMesh mesh){

    }

    @Override
    public void createLinks(FSMesh mesh){

    }

    @Override
    public FSBufferLayout bufferLayouts(FSMesh mesh, FSBufferManager manager){
        int modelbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, bindpointoffset), new VLBufferFloat()));
        int colorbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, bindpointoffset + 1), new VLBufferFloat()));

        FSBufferLayout layout = new FSBufferLayout(mesh);

        layout.add(manager, modelbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_MODEL));

        layout.add(manager, colorbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, FSG.ELEMENT_COLOR));

        layout.add(manager, Gen.BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_TEXCOORD))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_NORMAL));

        layout.add(manager, Gen.BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, FSG.ELEMENT_INDEX));

        return layout;
    }

    @Override
    protected void postBufferAdjustment(FSMesh mesh){

    }

    @Override
    protected void attachMeshToPrograms(FSMesh mesh){
        program.addMesh(mesh);
    }

    @Override
    protected void finished(FSMesh mesh){

    }
}
