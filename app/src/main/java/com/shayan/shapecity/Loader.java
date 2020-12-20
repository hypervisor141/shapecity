package com.nurverek.firestorm;

import android.opengl.GLES32;
import android.util.Log;

import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListType;
import com.shayan.shapecity.Animations;
import com.shayan.shapecity.Game;
import com.shayan.shapecity.ModColor;
import com.shayan.shapecity.ModDepthMap;
import com.shayan.shapecity.ModLight;
import com.shayan.shapecity.ModModel;
import com.shayan.shapecity.ModNoLight;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;

public final class Loader extends FSG{

    private static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_DISABLED;
    private static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    private static final FSLightMaterial MATERIAL_DEFAULT = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.2f, 0.2f, 0.2f }), new VLFloat(32));
    private static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(32));
    private static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(256));
    private static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(32));

    private static final int SHADOW_PROGRAMSET = 0;
    private static final int MAIN_PROGRAMSET = 1;
    private static final int SHADOWMAP_ORTHO_DIAMETER = 4;
    private static final int SHADOWMAP_ORTHO_NEAR = 1;
    private static final int SHADOWMAP_ORTHO_FAR = 1500;

    private static final VLInt SHADOW_POINT_PCF_SAMPLES = new VLInt(20);
    private static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(2f));
    private static final FSGamma GAMMA = new FSGamma(new VLFloat(1.5f));

    private static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static FSMesh center;
    public static FSMesh base;
    public static FSMesh baselining;
    public static FSMesh layer1;
    public static FSMesh layer2;
    public static FSMesh layer3;
    public static FSMesh pillars;

    public static FSMesh[] layers;

    private static FSP programCenterDepth;
    private static FSP programCenter;
    private static FSP programLayersDepth;
    private static FSP programLayers;
    private static FSP programBaseDepth;
    private static FSP programBase;
    private static FSP programPillarsDepth;
    private static FSP programPillars;

    private static int BUFFER_ELEMENT_SHORT_DEFAULT;
    private static int BUFFER_ARRAY_FLOAT_DEFAULT;

    public static final int LAYER_INSTANCE_COUNT = 24;
    public static final int PILLAR_INSTANCE_COUNT = 252;

    public static FSLightDirect lightDirect;
    public static FSLightPoint lightPoint;

    public static FSShadowDirect shadowDirect;
    public static FSShadowPoint shadowPoint;

    public Loader(){
        super(2, 50, 10);
    }

    private void addBasics(){
        //        7 	1.0 	0.7 	1.8
        //        13 	1.0 	0.35 	0.44
        //        20 	1.0 	0.22 	0.20
        //        32 	1.0 	0.14 	0.07
        //        50 	1.0 	0.09 	0.032
        //        65 	1.0 	0.07 	0.017
        //        100 	1.0 	0.045 	0.0075
        //        160 	1.0 	0.027 	0.0028
        //        200 	1.0 	0.022 	0.0019
        //        325 	1.0 	0.014 	0.0007
        //        600 	1.0 	0.007 	0.0002
        //        3250 	1.0 	0.0014 	0.000007

        programCenterDepth = new FSP(DEBUG_MODE_PROGRAMS);
        programLayersDepth = new FSP(DEBUG_MODE_PROGRAMS);
        programBaseDepth = new FSP(DEBUG_MODE_PROGRAMS);
        programPillarsDepth = new FSP(DEBUG_MODE_PROGRAMS);

        programCenter = new FSP(DEBUG_MODE_PROGRAMS);
        programLayers = new FSP(DEBUG_MODE_PROGRAMS);
        programBase = new FSP(DEBUG_MODE_PROGRAMS);
        programPillars = new FSP(DEBUG_MODE_PROGRAMS);

        lightPoint = new FSLightPoint(new FSAttenuation(new VLFloat(1.0F), new VLFloat(0.007F), new VLFloat(0.0002F)), new VLArrayFloat(new float[]{ 0F, 20F, -10F, 1.0F }));
        lightDirect = new FSLightDirect(new VLArrayFloat(new float[]{ 0F, 500F, 1000F, 1.0F }), new VLArrayFloat(new float[]{ 0F, 0F, 0F, 1.0F }));

        shadowDirect = new FSShadowDirect(lightDirect, new VLInt(1024), new VLInt(1024), new VLFloat(0.0001F), new VLFloat(0.0005F), new VLFloat(1.1F));
        shadowDirect.initialize(new VLInt(TEXUNIT++));

        shadowPoint = new FSShadowPoint(lightPoint, new VLInt(1024), new VLInt(1024), new VLFloat(0.005F), new VLFloat(0.005F), new VLFloat(1.1F), new VLFloat(1F), new VLFloat(300F));
        shadowPoint.initialize(new VLInt(TEXUNIT++));

        BUFFER_ELEMENT_SHORT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));
    }

    @Override
    public void assemble(FSActivity act){
        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        ////////// SETUP

        addBasics();
        Game.initialize();

        ////////// BUILD

        FSBufferLayout centerlayout = registerCenter();
        FSBufferLayout[] layerlayouts = registerLayers();
        FSBufferLayout baselayout = registerBase();
        FSBufferLayout baselininglayout = registerBaseLining();
        FSBufferLayout pillarslayout = registerPillars();

        AUTOMATOR.build(DEBUG_MODE_AUTOMATOR);

        ////////// BUFFER

        createLinks();

        FSBufferLayout layout;

        for(int i = 0; i < layerlayouts.length; i++){
            layout = layerlayouts[i];

            int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));
            int texcontrolbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));
            int colorbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

            layout.add(BUFFERMANAGER, modelbuffer, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, ELEMENT_MODEL));

            layout.add(BUFFERMANAGER, colorbuffer, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, ELEMENT_COLOR));

            layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_TEXCOORD))
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

            layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

            layout.add(BUFFERMANAGER, texcontrolbuffer, 1)
                    .addLink(new FSBufferLayout.EntryLink(FSBufferLayout.LINK_SEQUENTIAL_SINGULAR, 0, 0, 1, 1, 4));
        }

        centerlayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_TEXCOORD))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        centerlayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

        int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));
        int colorbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

        pillarslayout.add(BUFFERMANAGER, modelbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, ELEMENT_MODEL));

        pillarslayout.add(BUFFERMANAGER, colorbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INSTANCED, ELEMENT_COLOR));

        pillarslayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 2)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        pillarslayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

        baselayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 2)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        baselayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

        baselininglayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 2)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        baselininglayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

        AUTOMATOR.buffer(DEBUG_MODE_AUTOMATOR);

        ////////// PROGRAM

        setupPrograms();

        AUTOMATOR.program(DEBUG_MODE_AUTOMATOR);

        ////////// POST

        postFullSetup();
        Game.startGame(this);
    }

    @Override
    public void update(int passindex, int programsetindex){
        BUFFERMANAGER.updateIfNeeded();
    }

    private FSBufferLayout registerCenter(){
        Assembler config = new Assembler();
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

        DataPack pack = new DataPack(null, Game.texCenter, MATERIAL_WHITE_RUBBER, null);
        Registration reg = AUTOMATOR.addScannerSingle(config, pack, "center_Cylinder.001", GLES32.GL_TRIANGLES);

        reg.addProgram(programCenterDepth);
        reg.addProgram(programCenter);

        center = reg.mesh();

        return reg.bufferLayout();
    }

    private FSBufferLayout registerBase(){
        Assembler config = new Assembler();
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

        DataPack pack = new DataPack(new VLArrayFloat(Animations.COLOR_BASE), null, MATERIAL_WHITE_RUBBER, null);

        Registration reg = AUTOMATOR.addScannerSingle(config, pack, "base_Cube.076", GLES32.GL_TRIANGLES);
        base = reg.mesh();

        reg.addProgram(programBaseDepth);
        reg.addProgram(programBase);

        return reg.bufferLayout();
    }

    private FSBufferLayout registerBaseLining(){
        Assembler config = new Assembler();
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

        DataPack pack = new DataPack(new VLArrayFloat(Animations.COLOR_BASE_LINING), null, MATERIAL_WHITE_RUBBER, null);

        Registration reg = AUTOMATOR.addScannerSingle(config, pack, "baselining_Cube.036", GLES32.GL_TRIANGLES);
        baselining = reg.mesh();

        reg.addProgram(programBaseDepth);
        reg.addProgram(programBase);

        return reg.bufferLayout();
    }

    private FSBufferLayout[] registerLayers(){
        Assembler config = new Assembler();
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

        VLListType<DataPack> group1 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<DataPack> group2 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<DataPack> group3 = new VLListType<>(LAYER_INSTANCE_COUNT, 10);

        DataPack pack1 = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER1), Game.texArrayLayer1, MATERIAL_OBSIDIAN, null);
        DataPack pack2 = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER2), Game.texArrayLayer2, MATERIAL_OBSIDIAN, null);
        DataPack pack3 = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER3), Game.texArrayLayer3, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group1.add(pack1);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group2.add(pack2);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            group3.add(pack3);
        }

        Registration reg1 = AUTOMATOR.addScannerInstanced(config, new DataGroup(group1), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reg2 = AUTOMATOR.addScannerInstanced(config, new DataGroup(group2), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reg3 = AUTOMATOR.addScannerInstanced(config, new DataGroup(group3), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reg1.addProgram(programLayersDepth);
        reg2.addProgram(programLayersDepth);
        reg3.addProgram(programLayersDepth);

        reg1.addProgram(programLayers);
        reg2.addProgram(programLayers);
        reg3.addProgram(programLayers);

        layer1 = reg1.mesh();
        layer2 = reg2.mesh();
        layer3 = reg3.mesh();

        FSBufferLayout[] layouts = new FSBufferLayout[]{ reg1.bufferLayout(), reg2.bufferLayout(), reg3.bufferLayout() };

        return layouts;
    }

    private FSBufferLayout registerPillars(){
        Assembler config = new Assembler();
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

        VLListType<DataPack> packs = new VLListType<>(PILLAR_INSTANCE_COUNT, 0);
        Random random = new Random();

        for(int i = 0; i < PILLAR_INSTANCE_COUNT; i++){
            float[] color = new float[]{
                    random.nextFloat(),
                    random.nextFloat(),
                    random.nextFloat(),
                    random.nextFloat(),
            };
            packs.add(new DataPack(new VLArrayFloat(Animations.COLOR_PILLARS), null, MATERIAL_WHITE_RUBBER, null));
        }

        Registration reg = AUTOMATOR.addScannerInstanced(config, new DataGroup(packs), "pillars", GLES32.GL_TRIANGLES, PILLAR_INSTANCE_COUNT);
        pillars = reg.mesh();

        reg.addProgram(programPillarsDepth);
        reg.addProgram(programPillars);

        return reg.bufferLayout();
    }

    private void createLinks(){
        VLListType<FSLinkType> links1 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links2 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links3 = new VLListType<>(1, 0);

        float[] array1 = new float[LAYER_INSTANCE_COUNT];
        float[] array2 = new float[LAYER_INSTANCE_COUNT];
        float[] array3 = new float[LAYER_INSTANCE_COUNT];

        Arrays.fill(array1, Animations.TEXCONTROL_IDLE);
        Arrays.fill(array2, Animations.TEXCONTROL_IDLE);
        Arrays.fill(array3, Animations.TEXCONTROL_IDLE);

        links1.add(new ModColor.TextureControlLink(new VLArrayFloat(array1)));
        links2.add(new ModColor.TextureControlLink(new VLArrayFloat(array2)));
        links3.add(new ModColor.TextureControlLink(new VLArrayFloat(array3)));

        layer1.initLinks(links1);
        layer2.initLinks(links2);
        layer3.initLinks(links3);

        center.initLinks(new VLListType<>(0, 0));
    }

    private void setupPrograms(){
        FSP.Modifier moddepthinitpoint = new ModDepthMap.Prepare(shadowPoint.frameBuffer(), shadowPoint.width(), shadowPoint.height(), true);
        FSP.Modifier moddepthinitdirect = new ModDepthMap.Prepare(shadowDirect.frameBuffer(), shadowDirect.width(), shadowDirect.height(), true);

        FSP.Modifier moddepthsetuppoint = new ModDepthMap.SetupPoint(shadowPoint, FSShadowPoint.SELECT_LIGHT_TRANSFORMS, lightPoint.position(), shadowPoint.zFar());
        FSP.Modifier moddepthsetupdirect = new ModDepthMap.SetupDirect(shadowDirect.lightViewProjection());

        FSP.Modifier moddepthfinishpoint = new ModDepthMap.Finish(shadowPoint.frameBuffer());
        FSP.Modifier moddepthfinishdirect = new ModDepthMap.Finish(shadowPoint.frameBuffer());

        FSP.Modifier modlightpoint = new ModLight.Point(GAMMA, null, BRIGHTNESS, lightPoint, shadowPoint, MATERIAL_DEFAULT.getGLSLSize());
        FSP.Modifier modlightdirect = new ModLight.Direct(GAMMA, BRIGHTNESS, lightDirect, shadowDirect, MATERIAL_DEFAULT.getGLSLSize());

        FSP.Modifier modmodeluniform = new ModModel.Uniform();
        FSP.Modifier modmodelubo = new ModModel.UBO(1, LAYER_INSTANCE_COUNT);
        FSP.Modifier modmodelubo2 = new ModModel.UBO(1, PILLAR_INSTANCE_COUNT);
        FSP.Modifier modcolorlayers = new ModColor.TextureAndUBO(1, LAYER_INSTANCE_COUNT, true, false, true);
        FSP.Modifier modcolortex = new ModColor.Texture(false,false,1, false);
        FSP.Modifier modcoloruniform = new ModColor.Uniform();
        FSP.Modifier modcolorpillars = new ModColor.UBO(1, PILLAR_INSTANCE_COUNT);

        FSConfig draw = new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0);
        FSConfig drawinstanced = new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0);

        programCenterDepth.modify(moddepthinitpoint, FSConfig.POLICY_ALWAYS);
        programCenterDepth.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programCenterDepth.modify(moddepthsetuppoint, FSConfig.POLICY_ALWAYS);
        programCenterDepth.addMeshConfig(draw);
        programCenterDepth.build();

        programSet(SHADOW_PROGRAMSET).add(programCenterDepth);

        programLayersDepth.modify(modmodelubo, FSConfig.POLICY_ALWAYS);
        programLayersDepth.modify(moddepthsetuppoint, FSConfig.POLICY_ALWAYS);
        programLayersDepth.modify(moddepthfinishpoint, FSConfig.POLICY_ALWAYS);
        programLayersDepth.addMeshConfig(drawinstanced);
        programLayersDepth.build();

        programSet(SHADOW_PROGRAMSET).add(programLayersDepth);

        programBaseDepth.modify(moddepthinitdirect, FSConfig.POLICY_ALWAYS);
        programBaseDepth.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programBaseDepth.modify(moddepthsetupdirect, FSConfig.POLICY_ALWAYS);
        programBaseDepth.addMeshConfig(draw);
        programBaseDepth.build();

        programSet(SHADOW_PROGRAMSET).add(programBaseDepth);

        programPillarsDepth.modify(modmodelubo2, FSConfig.POLICY_ALWAYS);
        programPillarsDepth.modify(moddepthsetupdirect, FSConfig.POLICY_ALWAYS);
        programPillarsDepth.modify(moddepthfinishdirect, FSConfig.POLICY_ALWAYS);
        programPillarsDepth.addMeshConfig(draw);
        programPillarsDepth.build();

        programSet(SHADOW_PROGRAMSET).add(programPillarsDepth);

        programCenter.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programCenter.modify(modcolortex, FSConfig.POLICY_ALWAYS);
        programCenter.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programCenter.addMeshConfig(draw);
        programCenter.build();

        programSet(MAIN_PROGRAMSET).add(programCenter);

        programLayers.modify(modmodelubo, FSConfig.POLICY_ALWAYS);
        programLayers.modify(modcolorlayers, FSConfig.POLICY_ALWAYS);
        programLayers.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programLayers.addMeshConfig(drawinstanced);
        programLayers.build();

        programSet(MAIN_PROGRAMSET).add(programLayers);

        programBase.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programBase.modify(modcoloruniform, FSConfig.POLICY_ALWAYS);
        programBase.modify(modlightdirect, FSConfig.POLICY_ALWAYS);
        programBase.addMeshConfig(draw);
        programBase.build();

        programSet(MAIN_PROGRAMSET).add(programBase);

        programPillars.modify(modmodelubo2, FSConfig.POLICY_ALWAYS);
        programPillars.modify(modcolorpillars, FSConfig.POLICY_ALWAYS);
        programPillars.modify(modlightdirect, FSConfig.POLICY_ALWAYS);
        programPillars.addMeshConfig(drawinstanced);
        programPillars.build();

        programSet(MAIN_PROGRAMSET).add(programPillars);
    }

    private void postFullSetup(){
        layers = new FSMesh[]{ layer1, layer2, layer3 };
    }

    @Override
    protected void destroyAssets(){
        shadowPoint.destroy();

        Game.destroy();
        Animations.destroy();
    }
}