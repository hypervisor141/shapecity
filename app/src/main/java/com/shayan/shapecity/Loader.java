package com.nurverek.firestorm;

import android.opengl.GLES32;
import android.util.Log;

import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListType;
import com.nurverek.vanguard.VLVRunner;
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

    private static final int DEBUG_AUTOMATOR = FSControl.DEBUG_DISABLED;
    private static final int DEBUG_PROGRAMS = FSControl.DEBUG_DISABLED;

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
    private static final FSGamma GAMMA = new FSGamma(new VLFloat(2.5f));

    private static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static FSMesh layer1;
    public static FSMesh layer2;
    public static FSMesh layer3;
    public static FSMesh city;
    public static FSMesh district1;
    public static FSMesh district2;
    public static FSMesh district3;
    public static FSMesh district4;

    public static FSMesh[] layers;
    public static FSMesh[] districts;

    private static FSP programDepthSingular;
    private static FSP programCity;
    private static FSP programDistricts;
    private static FSP programDepthLayers;
    private static FSP programMainLayers;

    private static int BUFFER_ELEMENT_SHORT_DEFAULT;
    private static int BUFFER_ARRAY_FLOAT_DEFAULT;

    public static final int LAYER_INSTANCE_COUNT = 24;
    public static FSLightPoint lightPoint;
    public static FSShadowPoint shadowPoint;

    public Loader(){
        super(2, 50, 10);
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

        FSBufferLayout[] layerlayouts = registerLayers();
        FSBufferLayout citylayout = registerCity();
        FSBufferLayout[] districtslayouts = registerDistricts();

        AUTOMATOR.build(DEBUG_AUTOMATOR);

        ////////// BUFFER

        createLinks();
        prepareBufferLayouts(layerlayouts, citylayout, districtslayouts);

        AUTOMATOR.buffer(DEBUG_AUTOMATOR);

        ////////// PROGRAM

        setupPrograms();

        AUTOMATOR.program(DEBUG_AUTOMATOR);

        ////////// POST

        postFullSetup();
        Game.startGame(this);
    }

    @Override
    public void update(int passindex, int programsetindex){
        BUFFERMANAGER.updateIfNeeded();
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

        programDepthSingular = new FSP(DEBUG_PROGRAMS);
        programCity = new FSP(DEBUG_PROGRAMS);
        programDistricts = new FSP(DEBUG_PROGRAMS);
        programDepthLayers = new FSP(DEBUG_PROGRAMS);
        programMainLayers = new FSP(DEBUG_PROGRAMS);

        lightPoint = new FSLightPoint(new FSAttenuation(new VLFloat(1.0F), new VLFloat(0.007F), new VLFloat(0.0002F)), new VLArrayFloat(new float[]{ 0F, 8F, -7F, 1.0F }));

        shadowPoint = new FSShadowPoint(lightPoint, new VLInt(1024), new VLInt(1024), new VLFloat(0.005F), new VLFloat(0.005F), new VLFloat(1.1F), new VLFloat(1F), new VLFloat(50F));
        shadowPoint.initialize(new VLInt(TEXUNIT++));

        BUFFER_ELEMENT_SHORT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));
    }

    private FSBufferLayout[] registerLayers(){
        Assembler assemblerlayers = new Assembler();
        assemblerlayers.ENABLE_DATA_PACK = true;
        assemblerlayers.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        assemblerlayers.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assemblerlayers.SYNC_MODELARRAY_AND_BUFFER = true;
        assemblerlayers.SYNC_POSITION_AND_BUFFER = true;
        assemblerlayers.SYNC_COLOR_AND_BUFFER = true;
        assemblerlayers.SYNC_TEXCOORD_AND_BUFFER = true;
        assemblerlayers.SYNC_NORMAL_AND_BUFFER = true;
        assemblerlayers.SYNC_INDICES_AND_BUFFER = true;
        assemblerlayers.INSTANCE_SHARE_POSITIONS = true;
        assemblerlayers.INSTANCE_SHARE_COLORS = false;
        assemblerlayers.INSTANCE_SHARE_TEXCOORDS = true;
        assemblerlayers.INSTANCE_SHARE_NORMALS = true;
        assemblerlayers.LOAD_MODELS = true;
        assemblerlayers.LOAD_POSITIONS = true;
        assemblerlayers.LOAD_COLORS = true;
        assemblerlayers.LOAD_TEXCOORDS = true;
        assemblerlayers.LOAD_NORMALS = true;
        assemblerlayers.LOAD_INDICES = true;
        assemblerlayers.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assemblerlayers.ENABLE_COLOR_FILL = true;
        assemblerlayers.DRAW_MODE_INDEXED = true;
        assemblerlayers.configure();

        VLListType<DataPack> layer1packs = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<DataPack> layer2packs = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        VLListType<DataPack> layer3packs = new VLListType<>(LAYER_INSTANCE_COUNT, 10);

        DataPack layer1pack = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER1), Game.texArrayLayer1, MATERIAL_OBSIDIAN, null);
        DataPack layer2pack = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER2), Game.texArrayLayer2, MATERIAL_OBSIDIAN, null);
        DataPack layer3pack = new DataPack(new VLArrayFloat(Animations.COLOR_LAYER3), Game.texArrayLayer3, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            layer1packs.add(layer1pack);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            layer2packs.add(layer2pack);
        }
        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            layer3packs.add(layer3pack);
        }

        Registration reglayer1 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layer1packs), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reglayer2 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layer2packs), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reglayer3 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layer3packs), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reglayer1.addProgram(programDepthLayers);
        reglayer2.addProgram(programDepthLayers);
        reglayer3.addProgram(programDepthLayers);

        reglayer1.addProgram(programMainLayers);
        reglayer2.addProgram(programMainLayers);
        reglayer3.addProgram(programMainLayers);

        layer1 = reglayer1.mesh();
        layer2 = reglayer2.mesh();
        layer3 = reglayer3.mesh();

        FSBufferLayout[] layerlayouts = new FSBufferLayout[]{ reglayer1.bufferLayout(), reglayer2.bufferLayout(), reglayer3.bufferLayout() };

        return layerlayouts;
    }

    private FSBufferLayout registerCity(){
        Assembler assemblersingular = new Assembler();
        assemblersingular.ENABLE_DATA_PACK = true;
        assemblersingular.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        assemblersingular.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assemblersingular.SYNC_MODELARRAY_AND_BUFFER = true;
        assemblersingular.SYNC_POSITION_AND_BUFFER = true;
        assemblersingular.SYNC_COLOR_AND_BUFFER = true;
        assemblersingular.SYNC_TEXCOORD_AND_BUFFER = true;
        assemblersingular.SYNC_NORMAL_AND_BUFFER = true;
        assemblersingular.SYNC_INDICES_AND_BUFFER = true;
        assemblersingular.INSTANCE_SHARE_POSITIONS = false;
        assemblersingular.INSTANCE_SHARE_COLORS = false;
        assemblersingular.INSTANCE_SHARE_TEXCOORDS = false;
        assemblersingular.INSTANCE_SHARE_NORMALS = false;
        assemblersingular.LOAD_MODELS = true;
        assemblersingular.LOAD_POSITIONS = true;
        assemblersingular.LOAD_COLORS = false;
        assemblersingular.LOAD_TEXCOORDS = true;
        assemblersingular.LOAD_NORMALS = true;
        assemblersingular.LOAD_INDICES = true;
        assemblersingular.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assemblersingular.ENABLE_COLOR_FILL = false;
        assemblersingular.DRAW_MODE_INDEXED = true;
        assemblersingular.configure();

        DataPack citypack = new DataPack(null, Game.texCity, MATERIAL_WHITE_RUBBER, null);
        Registration cityreg = AUTOMATOR.addScannerSingle(assemblersingular, citypack, "city_cylinder", GLES32.GL_TRIANGLES);

        cityreg.addProgram(programDepthSingular);
        cityreg.addProgram(programCity);

        city = cityreg.mesh();

        return cityreg.bufferLayout();
    }

    private FSBufferLayout[] registerDistricts(){
        Assembler assemblersingular = new Assembler();
        assemblersingular.ENABLE_DATA_PACK = true;
        assemblersingular.SYNC_MODELMATRIX_AND_MODELARRAY = true;
        assemblersingular.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assemblersingular.SYNC_MODELARRAY_AND_BUFFER = true;
        assemblersingular.SYNC_POSITION_AND_BUFFER = true;
        assemblersingular.SYNC_COLOR_AND_BUFFER = true;
        assemblersingular.SYNC_TEXCOORD_AND_BUFFER = true;
        assemblersingular.SYNC_NORMAL_AND_BUFFER = true;
        assemblersingular.SYNC_INDICES_AND_BUFFER = true;
        assemblersingular.INSTANCE_SHARE_POSITIONS = false;
        assemblersingular.INSTANCE_SHARE_COLORS = false;
        assemblersingular.INSTANCE_SHARE_TEXCOORDS = false;
        assemblersingular.INSTANCE_SHARE_NORMALS = false;
        assemblersingular.LOAD_MODELS = true;
        assemblersingular.LOAD_POSITIONS = true;
        assemblersingular.LOAD_COLORS = true;
        assemblersingular.LOAD_TEXCOORDS = false;
        assemblersingular.LOAD_NORMALS = true;
        assemblersingular.LOAD_INDICES = true;
        assemblersingular.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assemblersingular.ENABLE_COLOR_FILL = true;
        assemblersingular.DRAW_MODE_INDEXED = true;
        assemblersingular.configure();

        DataPack pack1 = new DataPack(new VLArrayFloat(Animations.COLOR_DISTRICTS), null, MATERIAL_WHITE_RUBBER, null);
        DataPack pack2 = new DataPack(new VLArrayFloat(Animations.COLOR_DISTRICTS), null, MATERIAL_WHITE_RUBBER, null);
        DataPack pack3 = new DataPack(new VLArrayFloat(Animations.COLOR_DISTRICTS), null, MATERIAL_WHITE_RUBBER, null);
        DataPack pack4 = new DataPack(new VLArrayFloat(Animations.COLOR_DISTRICTS), null, MATERIAL_WHITE_RUBBER, null);

        Registration district1reg = AUTOMATOR.addScannerSingle(assemblersingular, pack1, "district.000_Cube.036", GLES32.GL_TRIANGLES);
        Registration district2reg = AUTOMATOR.addScannerSingle(assemblersingular, pack2, "district.001_Cube.037", GLES32.GL_TRIANGLES);
        Registration district3reg = AUTOMATOR.addScannerSingle(assemblersingular, pack3, "district.002_Cube.038", GLES32.GL_TRIANGLES);
        Registration district4reg = AUTOMATOR.addScannerSingle(assemblersingular, pack4, "district.003_Cube.039", GLES32.GL_TRIANGLES);

        district1reg.addProgram(programDepthSingular);
        district2reg.addProgram(programDepthSingular);
        district3reg.addProgram(programDepthSingular);
        district4reg.addProgram(programDepthSingular);

        district1reg.addProgram(programDistricts);
        district2reg.addProgram(programDistricts);
        district3reg.addProgram(programDistricts);
        district4reg.addProgram(programDistricts);

        district1 = district1reg.mesh();
        district2 = district2reg.mesh();
        district3 = district3reg.mesh();
        district4 = district4reg.mesh();

        return new FSBufferLayout[]{
                district1reg.bufferLayout(),
                district2reg.bufferLayout(),
                district3reg.bufferLayout(),
                district4reg.bufferLayout(),
        };
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

        city.initLinks(new VLListType<>(0, 0));
    }

    private void prepareBufferLayouts(FSBufferLayout[] layerlayouts, FSBufferLayout citylayout, FSBufferLayout[] districtslayouts){
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

        int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_DYNAMIC_DRAW), new VLBufferFloat()));

        citylayout.add(BUFFERMANAGER, modelbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_SINGULAR, ELEMENT_MODEL));

        citylayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_TEXCOORD))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        citylayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));

        modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_DYNAMIC_DRAW), new VLBufferFloat()));

        for(int i = 0; i < districtslayouts.length; i++){
            layout = districtslayouts[i];

            layout.add(BUFFERMANAGER, modelbuffer, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_SINGULAR, ELEMENT_MODEL));

            layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 3)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

            layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                    .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));
        }
    }

    private void setupPrograms(){
        FSConfig drawlayers = new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0);
        FSConfig drawsingular = new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0);

        FSP.Modifier modcolorlayers = new ModColor.TextureAndUBO(1, LAYER_INSTANCE_COUNT, true, false, true);
        FSP.Modifier moddepthprep = new ModDepthMap.Prepare(shadowPoint.frameBuffer(), shadowPoint.width(), shadowPoint.height(), true);
        FSP.Modifier moddepthsetup = new ModDepthMap.SetupPoint(shadowPoint, FSShadowPoint.SELECT_LIGHT_TRANSFORMS, lightPoint.position(), shadowPoint.zFar());
        FSP.Modifier moddepthfinish = new ModDepthMap.Finish(shadowPoint.frameBuffer());
        FSP.Modifier modlightpoint = new ModLight.Point(GAMMA, null, BRIGHTNESS, lightPoint, shadowPoint, MATERIAL_DEFAULT.getGLSLSize());
        FSP.Modifier modnolight = new ModNoLight(GAMMA, BRIGHTNESS);
        FSP.Modifier modmodelubo = new ModModel.UBO(1, LAYER_INSTANCE_COUNT);
        FSP.Modifier modmodeluniform = new ModModel.Uniform();
        FSP.Modifier modcolortex = new ModColor.Texture(false,false,1, false);
        FSP.Modifier modcoloruniform = new ModColor.Uniform();

        programSet(SHADOW_PROGRAMSET).add(programDepthLayers);
        programSet(SHADOW_PROGRAMSET).add(programDepthSingular);

        programSet(MAIN_PROGRAMSET).add(programMainLayers);
        programSet(MAIN_PROGRAMSET).add(programCity);
        programSet(MAIN_PROGRAMSET).add(programDistricts);

        programDepthLayers.modify(moddepthprep, FSConfig.POLICY_ALWAYS);
        programDepthLayers.modify(modmodelubo, FSConfig.POLICY_ALWAYS);
        programDepthLayers.modify(moddepthsetup, FSConfig.POLICY_ALWAYS);
        programDepthLayers.addMeshConfig(drawlayers);
        programDepthLayers.build();

        programMainLayers.modify(modmodelubo, FSConfig.POLICY_ALWAYS);
        programMainLayers.modify(modcolorlayers, FSConfig.POLICY_ALWAYS);
        programMainLayers.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programMainLayers.addMeshConfig(drawlayers);
        programMainLayers.build();

        programDepthSingular.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programDepthSingular.modify(moddepthsetup, FSConfig.POLICY_ALWAYS);
        programDepthSingular.modify(moddepthfinish, FSConfig.POLICY_ALWAYS);
        programDepthSingular.addMeshConfig(drawsingular);
        programDepthSingular.build();

        programCity.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programCity.modify(modcolortex, FSConfig.POLICY_ALWAYS);
        programCity.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programCity.addMeshConfig(drawsingular);
        programCity.build();

        programDistricts.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programDistricts.modify(modcoloruniform, FSConfig.POLICY_ALWAYS);
        programDistricts.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programDistricts.addMeshConfig(drawsingular);
        programDistricts.build();
    }

    private void postFullSetup(){
        layers = new FSMesh[]{
                layer1,
                layer2,
                layer3
        };

        districts = new FSMesh[]{
                district1,
                district2,
                district3,
                district4
        };
    }

    @Override
    protected void destroyAssets(){
        shadowPoint.destroy();

        Game.destroy();
        Animations.destroy();
    }
}