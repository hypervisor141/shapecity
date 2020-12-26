package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGAssembler;
import com.nurverek.firestorm.FSGBluePrint;
import com.nurverek.firestorm.FSGScanner;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLFloat;

public final class BPInstanced extends FSGBluePrint{

    public FSP programdepth;
    public FSP program;
    public int maxinstancecount;

    public BPInstanced(FSG gen, int maxinstancecount){
        this.maxinstancecount = maxinstancecount;
        initialize(gen);
    }

    @Override
    protected void createPrograms(){
        ModModel.UBO model = new ModModel.UBO(1, maxinstancecount);

//        programdepth = new FSP(Loader.DEBUG_MODE_PROGRAMS);
//        programdepth.modify(new ModShadow.Prepare(Loader.shadow2, false), FSConfig.POLICY_ALWAYS);
//        programdepth.modify(model, FSConfig.POLICY_ALWAYS);
//        programdepth.modify(new ModShadow.SetupDirect(Loader.shadow2), FSConfig.POLICY_ALWAYS);
//        programdepth.modify(new ModShadow.Finish(Loader.shadow2), FSConfig.POLICY_ALWAYS);
//        programdepth.addMeshConfig(new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0));
//        programdepth.build();

        program = new FSP(Loader.DEBUG_MODE_PROGRAMS);
        program.modify(model, FSConfig.POLICY_ALWAYS);
        program.modify(new ModColor.UBO(1, maxinstancecount), FSConfig.POLICY_ALWAYS);
        program.modify(new ModLight.Point(Loader.GAMMA, null, Loader.BRIGHTNESS, Loader.light, null, Loader.MATERIAL_WHITE_RUBBER.getGLSLSize()), FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0));
        program.build();
    }

    @Override
    protected void attachPrograms(FSG gen){
//        gen.programSet(Loader.SHADOW_PROGRAMSET).add(programdepth);
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
        instance.data().colors(new VLArrayFloat(Animations.COLOR_ORANGE.clone()));
        instance.lightMaterial(Loader.MATERIAL_WHITE_RUBBER);
    }

    @Override
    protected void postScanAdjustment(FSMesh mesh){

    }

    @Override
    public void createLinks(FSMesh mesh){

    }

    @Override
    public FSBufferLayout bufferLayouts(FSMesh mesh, FSBufferManager manager){
        int modelbuffer = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, Loader.UBOBINDPOINT++), new VLBufferFloat()));
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

        return layout;
    }

    @Override
    protected void postBufferAdjustment(FSMesh mesh){

    }

    @Override
    protected void attachMeshToPrograms(FSMesh mesh){
//        programdepth.addMesh(mesh);
        program.addMesh(mesh);
    }

    @Override
    protected void finished(FSMesh mesh){

    }
}
