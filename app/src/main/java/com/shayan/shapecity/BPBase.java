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

public class BPBase extends CustomBluePrint{

    public FSP program;

    public BPBase(FSG gen){
        initialize(gen);
    }

    @Override
    protected void createPrograms(){
        ModModel.Uniform model = new ModModel.Uniform();

        program = new FSP(Loader.DEBUG_MODE_PROGRAMS);
        program.modify(model, FSConfig.POLICY_ALWAYS);
        program.modify(new ModColor.Uniform(), FSConfig.POLICY_ALWAYS);
        program.modify(new ModLight.Point(Loader.GAMMA, null, Loader.BRIGHTNESS, Loader.light, null, Loader.MATERIAL_WHITE_RUBBER.getGLSLSize()), FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0));
        program.build();
    }

    @Override
    protected void attachPrograms(FSG gen){
        gen.programSet(Loader.MAIN_PROGRAMSET).add(program);
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
        config.DRAW_MODE_INDEXED = true;
        config.configure();

        return new FSGScanner.Singular(this, config, name, GLES32.GL_TRIANGLES);
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
        FSBufferLayout layout = new FSBufferLayout(mesh);

        layout.add(manager, Loader.BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_TEXCOORD))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, FSG.ELEMENT_NORMAL));

        layout.add(manager, Loader.BUFFER_ELEMENT_SHORT_DEFAULT, 1)
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
