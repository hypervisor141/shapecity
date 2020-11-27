
package com.nurverek.firestorm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES32;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListType;
import com.nurverek.vanguard.VLMath;
import com.nurverek.vanguard.VLTask;
import com.nurverek.vanguard.VLTaskContinous;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVConst;
import com.nurverek.vanguard.VLVInterpolated;
import com.nurverek.vanguard.VLVLinear;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVProcessor;
import com.shayan.shapecity.ModColor;
import com.shayan.shapecity.ModDepthMap;
import com.shayan.shapecity.ModLight;
import com.shayan.shapecity.ModModel;
import com.shayan.shapecity.ModNoLight;
import com.shayan.shapecity.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;

public final class Loader extends FSG{

    private static final float[] COLOR_WHITE = new float[]{
            1F, 1F, 1F, 1F
    };
    private static final float[] COLOR_ORANGE = new float[]{
            1.0F, 0.7F, 0F, 1F
    };
    private static final float[] COLOR_OBSIDIAN = new float[]{
            0.4F, 0.4F, 0.4F, 1F
    };
    private static final float[] COLOR_OBSIDIAN_LESS = new float[]{
            0.3F, 0.3F, 0.3F, 1F
    };
    private static final float[] COLOR_OBSIDIAN_LESS2 = new float[]{
            0.1F, 0.1F, 0.1F, 1F
    };
    private static final float[] COLOR_OBSIDIAN_LESS3 = new float[]{
            0.05F, 0.05F, 0.05F, 1F
    };
    private static final float[] COLOR_GOLD = new float[]{
            0.83F, 0.68F, 0.21F, 1F
    };
    private static final float[] COLOR_DARK_ORANGE = new float[]{
            1.0F, 0.4F, 0F, 1F
    };

    private static final FSLightMaterial MATERIAL_DEFAULT = new FSLightMaterial(new VLArrayFloat(
            new float[]{ 0.2f, 0.2f, 0.2f }),
            new VLFloat(32));

    private static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(
            new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }),
            new VLArrayFloat(new float[]{0.75164f, 0.60648f, 0.22648f }),
            new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }),
            new VLFloat(32));

    private static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(
            new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }),
            new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }),
            new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }),
            new VLFloat(256));

    private static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(
            new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }),
            new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }),
            new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }),
            new VLFloat(32));

    private static final int DEBUG_AUTOMATOR = FSControl.DEBUG_DISABLED;
    private static final int DEBUG_PROGRAMS = FSControl.DEBUG_DISABLED;

    private static final float[] COLOR_PIECES = COLOR_OBSIDIAN_LESS3;
    private static final float[] COLOR_BLINK = COLOR_OBSIDIAN_LESS2;
    private static final float[] COLOR_SELECTED = COLOR_DARK_ORANGE;

    private static final int GAME_MATCH_SYMBOLS = 124;
    private static final int GAME_MATCH_COLORS = 125;
    private static final int GAME_MATCH_ROTATION = 126;
    private static final int SAME_SYMBOL_PICK_LIMIT = 3;

    private static final int ROW_COLOR_BLINK = 0;
    private static final int ROW_COLOR_DEACTIVATED = 1;
    private static final int ROW_MODEL_ROTATE_FACE = 0;
    private static final int ROW_MODEL_POSITION = 1;
    private static final int ROW_MODEL_BOUNCE_Y = 2;
    private static final int ROW_MODEL_RAISE_Y = 3;
    private static final int PROCESSOR_MODEL_COUNT = 2;
    private static final int PROCESSOR_COLOR_COUNT = 2;

    private static final int CYCLES_BLINK = 20;
    private static final int CYCLES_SELECTED = 60;
    private static final int CYCLES_ROTATE = 30;
    private static final int CYCLES_RAISE = 100;
    private static final int CYCLES_BOUNCE = 200;
    private static final int CYCLES_REVEAL_ONE = 150;

    private static final float TEXCONTROL_IDLE = 0F;
    private static final float TEXCONTROL_ACTIVE = 1F;
    private static final int TEXCONTROL_CYCLES = 100;

    private static final int SHADOW_PROGRAMSET = 0;
    private static final int MAIN_PROGRAMSET = 1;
    private static final int LAYER_INSTANCE_COUNT = 36;
    private static final int SHADOWMAP_ORTHO_DIAMETER = 4;
    private static final int SHADOWMAP_ORTHO_NEAR = 1;
    private static final int SHADOWMAP_ORTHO_FAR = 1500;
    private static final int LAYER1_PIECE_TEXTURE_DIMENSION = 512;
    private static final int LAYER2_PIECE_TEXTURE_DIMENSION = 256;
    private static final int LAYER3_PIECE_TEXTURE_DIMENSION = 128;
    private static final int SELECTION_CYCLES = 5;
    private static final int LIGHT_SPIN_CYCLES = 3600;
    private static final float Y_REDUCTION = 0.99f;
    private static final float Y_MAX_HEIGHT_MULTIPLIER = 1.5f;

    private static final float[] CLAMPEDPOINTCACHE = new float[3];
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final VLInt SHADOW_POINT_PCF_SAMPLES = new VLInt(20);

    private static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(2f));
    private static final FSGamma GAMMA = new FSGamma(new VLFloat(1.5f));

    private static int UBOBINDPOINT = 0;
    private static int TEXUNIT = 1;
    private static ByteBuffer PIXEL_BUFFER = null;

    private FSInput.Entry collisionClosestEntry;
    private float collisionDistance;

    private FSTexture texArrayLayer1;
    private FSTexture texArrayLayer2;
    private FSTexture texArrayLayer3;
    
    private FSLightPoint lightPoint;
    private FSShadowPoint shadowPoint;

    private ModDepthMap.Prepare moddepthprep;
    private ModDepthMap.SetupPoint moddepthsetup;
    private ModDepthMap.Finish moddepthfinish;
    private ModModel.UBO modmodelubo;
    private ModModel.Uniform modmodeluniform;
    private ModColor.Uniform modcoloruniform;
    private ModLight.Point modlightpoint;
    private ModNoLight modnolight;

    private FSMesh layer1;
    private FSMesh layer2;
    private FSMesh layer3;
    private FSMesh city;

    private FSMesh[] layers;

    private VLVProcessor processorModel;
    private VLVProcessor processorColor;
    private VLVProcessor processorTexControl;

    private FSP programDepthSingular;
    private FSP programMainSingular;
    private FSP programDepthLayers;
    private FSP programMainLayers;

    private int BUFFER_ELEMENT_SHORT_DEFAULT;
    private int BUFFER_ARRAY_FLOAT_DEFAULT;

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

        ////////// BUILD

        FSBufferLayout[] layerlayouts = registerLayers();
        FSBufferLayout citylayout = registerSingular();

        AUTOMATOR.build(DEBUG_AUTOMATOR);

        ////////// BUFFER

        createLinks();
        prepareBufferLayouts(layerlayouts, citylayout);

        AUTOMATOR.buffer(DEBUG_AUTOMATOR);

        ////////// PROGRAM

        setupPrograms();

        AUTOMATOR.program(DEBUG_AUTOMATOR);

        ////////// POST

        postFullSetup();
        rotateLightSource();
        setupProcessors();
        startGame();
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
        programMainSingular = new FSP(DEBUG_PROGRAMS);
        programDepthLayers = new FSP(DEBUG_PROGRAMS);
        programMainLayers = new FSP(DEBUG_PROGRAMS);

        lightPoint = new FSLightPoint(
                new FSAttenuation(new VLFloat(1.0f), new VLFloat(0.014f), new VLFloat(0.0007f)),
                new VLArrayFloat(new float[]{ -10, 10, 0, 1.0f }));

        shadowPoint = new FSShadowPoint(lightPoint,
                new VLInt(1024),
                new VLInt(1024),
                new VLFloat(0.5f), new VLFloat(0.55f),
                new VLFloat(1.25f),
                new VLFloat(1f),
                new VLFloat(1300));

        shadowPoint.initialize(new VLInt(TEXUNIT++));

        int materialsize = MATERIAL_DEFAULT.getGLSLSize();

        moddepthprep = new ModDepthMap.Prepare(shadowPoint.frameBuffer(), shadowPoint.width(), shadowPoint.height(), false);
        moddepthsetup = new ModDepthMap.SetupPoint(shadowPoint, FSShadowPoint.SELECT_LIGHT_TRANSFORMS, lightPoint.position(), shadowPoint.zFar());
        moddepthfinish = new ModDepthMap.Finish(shadowPoint.frameBuffer());
        modlightpoint = new ModLight.Point(GAMMA, null, BRIGHTNESS, lightPoint, shadowPoint, materialsize);
        modnolight = new ModNoLight(GAMMA, BRIGHTNESS);
        modmodelubo = new ModModel.UBO(1, LAYER_INSTANCE_COUNT);
        modmodeluniform = new ModModel.Uniform();
        modcoloruniform = new ModColor.Uniform();

        BUFFER_ELEMENT_SHORT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        texArrayLayer1 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT++));
        texArrayLayer1.bind();
        texArrayLayer1.storage3D(1, GLES32.GL_RGBA8, LAYER1_PIECE_TEXTURE_DIMENSION, LAYER1_PIECE_TEXTURE_DIMENSION, LAYER_INSTANCE_COUNT);
        texArrayLayer1.minFilter(GLES32.GL_LINEAR);
        texArrayLayer1.magFilter(GLES32.GL_LINEAR);
        texArrayLayer1.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer1.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer1.baseLevel(0);
        texArrayLayer1.maxLevel(LAYER_INSTANCE_COUNT - 1);
        texArrayLayer1.unbind();

        FSTools.checkGLError();

        texArrayLayer2 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT++));
        texArrayLayer2.bind();
        texArrayLayer2.storage3D(1, GLES32.GL_RGBA8, LAYER2_PIECE_TEXTURE_DIMENSION, LAYER2_PIECE_TEXTURE_DIMENSION, LAYER_INSTANCE_COUNT);
        texArrayLayer2.minFilter(GLES32.GL_LINEAR);
        texArrayLayer2.magFilter(GLES32.GL_LINEAR);
        texArrayLayer2.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer2.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer2.baseLevel(0);
        texArrayLayer2.maxLevel(LAYER_INSTANCE_COUNT - 1);
        texArrayLayer2.unbind();

        FSTools.checkGLError();

        texArrayLayer3 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT++));
        texArrayLayer3.bind();
        texArrayLayer3.storage3D(1, GLES32.GL_RGBA8, LAYER3_PIECE_TEXTURE_DIMENSION, LAYER3_PIECE_TEXTURE_DIMENSION, LAYER_INSTANCE_COUNT);
        texArrayLayer3.minFilter(GLES32.GL_LINEAR);
        texArrayLayer3.magFilter(GLES32.GL_LINEAR);
        texArrayLayer3.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer3.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer3.baseLevel(0);
        texArrayLayer3.maxLevel(LAYER_INSTANCE_COUNT - 1);
        texArrayLayer3.unbind();

        FSTools.checkGLError();
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

        DataPack layer1pack = new DataPack(new VLArrayFloat(COLOR_PIECES), texArrayLayer1, MATERIAL_OBSIDIAN, null);
        DataPack layer2pack = new DataPack(new VLArrayFloat(COLOR_PIECES), texArrayLayer2, MATERIAL_OBSIDIAN, null);
        DataPack layer3pack = new DataPack(new VLArrayFloat(COLOR_PIECES), texArrayLayer3, MATERIAL_OBSIDIAN, null);

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

        FSBufferLayout[] layerlayouts = new FSBufferLayout[]{
                reglayer1.bufferLayout(),
                reglayer2.bufferLayout(),
                reglayer3.bufferLayout()
        };

        return layerlayouts;
    }

    private FSBufferLayout registerSingular(){
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

        DataPack citypack = new DataPack(new VLArrayFloat(COLOR_WHITE), null, MATERIAL_WHITE_RUBBER, null);
        Registration cityreg = AUTOMATOR.addScannerSingle(assemblersingular, citypack, "city_cylinder", GLES32.GL_TRIANGLES);

        cityreg.addProgram(programDepthSingular);
        cityreg.addProgram(programMainSingular);

        city = cityreg.mesh();

        return cityreg.bufferLayout();
    }

    private void createLinks(){
        VLListType<FSLinkType> links1 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links2 = new VLListType<>(1, 0);
        VLListType<FSLinkType> links3 = new VLListType<>(1, 0);

        float[] array1 = new float[LAYER_INSTANCE_COUNT];
        float[] array2 = new float[LAYER_INSTANCE_COUNT];
        float[] array3 = new float[LAYER_INSTANCE_COUNT];

        Arrays.fill(array1, TEXCONTROL_IDLE);
        Arrays.fill(array2, TEXCONTROL_IDLE);
        Arrays.fill(array3, TEXCONTROL_IDLE);

        links1.add(new ModColor.TextureControlLink(new VLArrayFloat(array1)));
        links2.add(new ModColor.TextureControlLink(new VLArrayFloat(array2)));
        links3.add(new ModColor.TextureControlLink(new VLArrayFloat(array3)));

        layer1.initLinks(links1);
        layer2.initLinks(links2);
        layer3.initLinks(links3);

        city.initLinks(new VLListType<>(0, 0));
    }

    private void prepareBufferLayouts(FSBufferLayout[] layerlayouts, FSBufferLayout citylayout){
        FSBufferLayout layout;

        for(int i = 0; i < layerlayouts.length; i++){
            layout = layerlayouts[i];

            int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER,
                    GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

            int texcontrolbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER,
                    GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

            int colorbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER,
                    GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

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

        int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER,
                GLES32.GL_DYNAMIC_DRAW), new VLBufferFloat()));

        citylayout.add(BUFFERMANAGER, modelbuffer, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_SINGULAR, ELEMENT_MODEL));

        citylayout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, 2)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_POSITION))
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_INTERLEAVED_SINGULAR, ELEMENT_NORMAL));

        citylayout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, 1)
                .addElement(new FSBufferLayout.EntryElement(FSBufferLayout.ELEMENT_SEQUENTIAL_INDICES, ELEMENT_INDEX));
    }

    private void setupPrograms(){
        FSConfig drawlayers = new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0);
        FSConfig drawsingular = new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0);
        FSP.Modifier modcolorlayers = new ModColor.TextureAndUBO(1, LAYER_INSTANCE_COUNT, true, false);

        programSet(SHADOW_PROGRAMSET).add(programDepthLayers);
        programSet(SHADOW_PROGRAMSET).add(programDepthSingular);

        programSet(MAIN_PROGRAMSET).add(programMainLayers);
        programSet(MAIN_PROGRAMSET).add(programMainSingular);

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

        programMainSingular.modify(modmodeluniform, FSConfig.POLICY_ALWAYS);
        programMainSingular.modify(modcoloruniform, FSConfig.POLICY_ALWAYS);
        programMainSingular.modify(modlightpoint, FSConfig.POLICY_ALWAYS);
        programMainSingular.addMeshConfig(drawsingular);
        programMainSingular.build();
    }

    private void postFullSetup(){
        layers = new FSMesh[]{
                layer1, layer2, layer3
        };
    }

    private void rotateLightSource(){
        final float[] orgpos = lightPoint.position().provider().clone();

        VLVLinear v = new VLVLinear(0, 360, LIGHT_SPIN_CYCLES, VLV.LOOP_FORWARD)
                .setTask(new VLTaskContinous(new VLTask.Task<VLVLinear>(){

                    private float[] cache = new float[16];

                    @Override
                    public void run(VLTask t, VLVLinear v){
                        float[] pos = lightPoint.position().provider();

                        Matrix.setIdentityM(cache, 0);
                        Matrix.rotateM(cache, 0, v.get(), 0f, 1f ,0f);
                        Matrix.multiplyMV(pos, 0, cache, 0, orgpos, 0);

                        pos[0] /= pos[3];
                        pos[1] /= pos[3];
                        pos[2] /= pos[3];

                        shadowPoint.updateLightVP();
                    }
                }));

        VLVProcessor controlproc = FSRenderer.getControllersProcessor();
        controlproc.add(new VLVProcessor.Entry(v, 0));
        controlproc.activateLatest();
        controlproc.start();
    }

    private void setupProcessors(){
        processorModel = new VLVProcessor(LAYER_INSTANCE_COUNT * layers.length * PROCESSOR_MODEL_COUNT, 0);
        processorColor = new VLVProcessor(LAYER_INSTANCE_COUNT * layers.length * PROCESSOR_COLOR_COUNT, 0);
        processorTexControl = new VLVProcessor(LAYER_INSTANCE_COUNT * layers.length, 0);

        FSMesh layer;
        FSInstance instance;
        FSModelMatrix modelmatrix;
        FSSchematics schematics;
        VLVMatrix colormatrix;
        VLVInterpolated texcontrolvar;
        ModColor.TextureControlLink link;
        VLArrayFloat linkdata;

        float yv;
        float yraise;

        PROCESSORS.add(processorModel);
        PROCESSORS.add(processorColor);
        PROCESSORS.add(processorTexControl);

        for(int i = 0; i < layers.length; i++){
            layer = layers[i];
            linkdata = ((ModColor.TextureControlLink)layer.link(0)).data;

            for(int i2 = 0; i2 < layer.size(); i2++){
                instance = layer.instance(i2);
                modelmatrix = instance.modelMatrix();
                schematics = instance.schematics();
                yv = modelmatrix.getY(0).get() - Y_REDUCTION;
                yraise = yv + schematics.modelHeight() * Y_MAX_HEIGHT_MULTIPLIER;

                modelmatrix.getY(0).set(yv);

                texcontrolvar = new VLVInterpolated(TEXCONTROL_IDLE, TEXCONTROL_ACTIVE, TEXCONTROL_CYCLES, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT);
                texcontrolvar.SYNCER.add(new VLArray.DefinitionVLV(linkdata, i2));

                processorTexControl.add(new VLVProcessor.Entry(texcontrolvar, 0));

                modelmatrix.addRowRotate(0, new VLVConst(-90f), VLVConst.ZERO, VLVConst.ZERO, VLVConst.ONE);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVInterpolated(0f, yraise, CYCLES_BOUNCE, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT), VLVConst.ZERO);
                modelmatrix.addRowTranslation(VLVConst.ZERO, new VLVInterpolated(0f, yraise, CYCLES_RAISE, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT), VLVConst.ZERO);
                modelmatrix.sync();

                processorModel.add(new VLVProcessor.Entry(modelmatrix, ROW_MODEL_BOUNCE_Y, 0));
                processorModel.add(new VLVProcessor.Entry(modelmatrix, ROW_MODEL_RAISE_Y, 0));

                colormatrix = new VLVMatrix(2, 0);
                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(COLOR_PIECES[0], COLOR_BLINK[0], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(COLOR_PIECES[1], COLOR_BLINK[1], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(COLOR_PIECES[2], COLOR_BLINK[2], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_BLINK, new VLVInterpolated(COLOR_PIECES[3], COLOR_BLINK[3], CYCLES_BLINK, VLV.LOOP_RETURN_ONCE, VLV.INTERP_DECELERATE_COS_SQRT));

                colormatrix.addRow(4, 0);
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(COLOR_PIECES[0], COLOR_SELECTED[0], CYCLES_SELECTED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(COLOR_PIECES[1], COLOR_SELECTED[1], CYCLES_SELECTED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(COLOR_PIECES[2], COLOR_SELECTED[2], CYCLES_SELECTED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));
                colormatrix.addColumn(ROW_COLOR_DEACTIVATED, new VLVInterpolated(COLOR_PIECES[3], COLOR_SELECTED[3], CYCLES_SELECTED, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_COS_SQRT));

                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), 0, 0));
                colormatrix.SYNCER.add(new VLArray.DefinitionMatrix(instance.colors(), 1, 0));

                processorColor.add(new VLVProcessor.Entry(colormatrix, ROW_COLOR_BLINK, VLVProcessor.SYNC_INDEX, 0, 0));
                processorColor.add(new VLVProcessor.Entry(colormatrix, ROW_COLOR_DEACTIVATED, VLVProcessor.SYNC_INDEX, 1, 0));

                schematics.inputBounds().add(new FSBoundsCuboid(schematics,
                        50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC,
                        40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }
        }

        reveal(0,true, true,150,200, 175, 200);
    }

    private void reveal(int layer, boolean randomdelays, boolean randomcycles, int mincycles, int maxcycles, int mindelay, int maxdelay){
        processorModel.pause();
        processorColor.pause();
        processorTexControl.pause();

        processorModel.reset();
        processorColor.reset();
        processorTexControl.reset();

        processorModel.deactivateAll();
        processorColor.deactivateAll();
        processorTexControl.deactivateAll();

        int size = processorColor.sizeData();
        int cyclegap = maxcycles - mincycles;
        int delaygap = maxdelay - mindelay;
        int cycles = 0;
        int delay = 0;

        VLVProcessor.Entry modelentry;
        VLVProcessor.Entry colorentry;
        VLVProcessor.Entry texcontrolentry;

        for(int i = layer * PROCESSOR_MODEL_COUNT * LAYER_INSTANCE_COUNT,
            i2 = layer * PROCESSOR_COLOR_COUNT * LAYER_INSTANCE_COUNT,
            i3 = layer * LAYER_INSTANCE_COUNT;
            i < size; i += PROCESSOR_MODEL_COUNT, i2 += PROCESSOR_COLOR_COUNT, i3++){

            modelentry = processorModel.get(i);
            colorentry = processorColor.get(i2);
            texcontrolentry = processorTexControl.get(i3);

            if(randomcycles){
                cycles = mincycles + RANDOM.nextInt(cyclegap);

                modelentry.target.reinitialize(cycles);
                colorentry.target.reinitialize(cycles);
                texcontrolentry.target.reinitialize(cycles);
            }
            if(randomdelays){
                delay = mindelay + RANDOM.nextInt(delaygap);

                modelentry.delay = delay;
                colorentry.delay = delay;
                texcontrolentry.delay = delay;
            }

            processorModel.activate(i);
            processorColor.activate(i2);
            processorTexControl.activate(i3);
        }

        processorModel.reset();
        processorColor.reset();
        processorTexControl.reset();

        processorModel.start();
        processorColor.start();
        processorTexControl.start();
    }

    private void revealOne(int layer, int instance, int cycles, int delay){
        int i = (layer * LAYER_INSTANCE_COUNT + instance) * PROCESSOR_MODEL_COUNT;
        int i2 = (layer * LAYER_INSTANCE_COUNT + instance) * PROCESSOR_COLOR_COUNT;
        int i3 = layer * LAYER_INSTANCE_COUNT + instance;

        if(!processorModel.isActive(i)){
            processorModel.activate(i);
        }
        if(!processorColor.isActive(i2)){
            processorColor.activate(i2);
        }
        if(!processorTexControl.isActive(i3)){
            processorTexControl.activate(i3);
        }

        VLVProcessor.Entry modelentry = processorModel.get(i);
        VLVProcessor.Entry colorentry = processorColor.get(i2);
        VLVProcessor.Entry texcontrolentry = processorTexControl.get(i3);

        modelentry.target.reset();
        colorentry.target.reset();
        texcontrolentry.target.reset();

        modelentry.target.reinitialize(cycles);
        colorentry.target.reinitialize(cycles);
        texcontrolentry.target.reinitialize(cycles);

        modelentry.delay = delay;
        colorentry.delay = delay;
        texcontrolentry.delay = delay;

        modelentry.resetDelayTracker();
        colorentry.resetDelayTracker();
        texcontrolentry.resetDelayTracker();

        processorModel.start();
        processorColor.start();
        processorTexControl.start();
    }

    private void changePieceTexture(final FSTexture layertexture, final int dimensions, final int subimageindex, final int resource){
        FSRenderer.addTask(new Runnable(){

            @Override
            public void run(){
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                opts.outConfig = Bitmap.Config.ARGB_8888;
                opts.inScaled = true;
                opts.inMutable = true;

                Bitmap b = BitmapFactory.decodeResource(FSControl.getContext().getResources(), resource, opts);

                PIXEL_BUFFER.position(0);

                b.copyPixelsToBuffer(PIXEL_BUFFER);
                b.recycle();

                PIXEL_BUFFER.position(0);

                layertexture.bind();
                layertexture.subImage3D(0, 0, 0, subimageindex, dimensions, dimensions, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
                layertexture.unbind();

                FSTools.checkGLError();
            }
        });
    }

    private void startGame(){
        RANDOM.setSeed(System.currentTimeMillis());

        int choice = 124 + RANDOM.nextInt(3);
        choice = GAME_MATCH_SYMBOLS;

        switch(choice){
            case GAME_MATCH_SYMBOLS:
                startMatchSymbolsGame();
                break;

            case GAME_MATCH_COLORS:
                startMatchColorsGame();
                break;

            case GAME_MATCH_ROTATION:
                startMatchRotationGame();
                break;

            default:
                new RuntimeException("Invalid game type choice : " + choice);
        }
    }

    private void startMatchSymbolsGame(){
        Bitmap b = null;

        int[] resources = new int[]{
                R.drawable.circle,
                R.drawable.hex,
                R.drawable.square,
                R.drawable.triangle,
                R.drawable.circlecone,
                R.drawable.squarestar,
                R.drawable.bladecircle,
                R.drawable.pointedsquare,
                R.drawable.rsquare,
                R.drawable.rhombus,
                R.drawable.rectangle,
                R.drawable.trapezoid
        };

        int[] timespicked = new int[resources.length];
        Arrays.fill(timespicked, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        Context cxt = FSControl.getContext();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.outConfig = Bitmap.Config.ARGB_8888;
        opts.inScaled = true;
        opts.inMutable = true;

        int choice = 0;
        int requiredchoices = LAYER_INSTANCE_COUNT / 3;
        int index = 0;

        PIXEL_BUFFER = null;
        texArrayLayer1.bind();

        final int[] symbols = new int[LAYER_INSTANCE_COUNT];
        Arrays.fill(symbols, -1);

        for(int i = 0; i < requiredchoices; i++){
            choice = RANDOM.nextInt(resources.length);

            while(timespicked[choice] >= SAME_SYMBOL_PICK_LIMIT){
                choice = RANDOM.nextInt(resources.length);
            }

            b = BitmapFactory.decodeResource(cxt.getResources(), resources[choice], opts);

            if(PIXEL_BUFFER == null){
                PIXEL_BUFFER = ByteBuffer.allocate(b.getAllocationByteCount());
                PIXEL_BUFFER.order(ByteOrder.nativeOrder());
            }

            PIXEL_BUFFER.position(0);

            b.copyPixelsToBuffer(PIXEL_BUFFER);
            b.recycle();

            for(int i2 = 0; i2 < 3; i2++){
                index = RANDOM.nextInt(LAYER_INSTANCE_COUNT);

                while(symbols[index] != -1){
                    index = RANDOM.nextInt(LAYER_INSTANCE_COUNT);
                }

                symbols[index] = choice;

                PIXEL_BUFFER.position(0);
                texArrayLayer1.subImage3D(0, 0, 0, index, LAYER1_PIECE_TEXTURE_DIMENSION, LAYER1_PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
            }
        }

        FSTools.checkGLError();
        texArrayLayer1.unbind();

        activateInputListeners(layer1, new Runnable(){

            private long delay;

            @Override
            public void run(){
                revealOne(0, collisionClosestEntry.instanceindex, CYCLES_REVEAL_ONE, 0);
            }
        });
    }

    private void startMatchColorsGame(){

    }

    private void startMatchRotationGame(){
        
    }

    private void changePieceTextureIntensityPiece(FSMesh layer, int index, float intensity){
        VLArrayFloat data = ((ModColor.TextureControlLink)layer.link(0)).data;
        data.provider()[index] = intensity;
        data.sync();
    }

    private void changePieceTextureIntensityLayer(FSMesh layer, float intensity){
        int size = layer.size();
        VLArrayFloat data = ((ModColor.TextureControlLink)layer.link(0)).data;

        for(int i = 0; i < size; i++){
            data.provider()[i] = intensity;
        }

        data.sync();
    }

    private void changePieceTextureIntensityAll(float intensity){
        int lsize = layers.length;
        int size;
        VLArrayFloat data;
        FSMesh layer;

        for(int i = 0; i < lsize; i++){
            layer = layers[i];
            size = layer.size();
            data = ((ModColor.TextureControlLink)layer.link(0)).data;

            for(int i2 = 0; i2 < size; i2++){
                data.provider()[i2] = intensity;
            }

            data.sync();
        }
    }

    private void activateInputListeners(FSMesh targetlayer, Runnable onactivated){
        int size = targetlayer.size();
        FSInput.clear(FSInput.TYPE_TOUCH);

        for(int i = 0; i < size; i++){
            FSInput.add(FSInput.TYPE_TOUCH, new FSInput.Entry(targetlayer, i, new FSInput.CollisionListener(){

                @Override
                public int activated(FSBounds.Collision results, FSInput.Entry entry, int boundindex, MotionEvent e1, MotionEvent e2, float f1, float f2, float[] near, float[] far){
                    if(e1.getAction() == MotionEvent.ACTION_UP){
                        FSBoundsCuboid bounds = (FSBoundsCuboid)entry.mesh.instance(entry.instanceindex).schematics().inputBounds().get(boundindex);

                        float[] coords = bounds.offset().coordinates();

                        CLAMPEDPOINTCACHE[0] = coords[0] + VLMath.clamp(near[0], -bounds.getHalfWidth(), bounds.getHalfWidth());
                        CLAMPEDPOINTCACHE[1] = coords[1] + VLMath.clamp(near[1], -bounds.getHalfHeight(), bounds.getHalfHeight());
                        CLAMPEDPOINTCACHE[2] = coords[2] + VLMath.clamp(near[2], -bounds.getHalfDepth(), bounds.getHalfDepth());

                        float distance = VLMath.euclideanDistance(CLAMPEDPOINTCACHE, 0, near, 0, 3);

                        if(collisionDistance > distance){
                            collisionDistance = distance;
                            collisionClosestEntry = entry;
                        }
                    }

                    return FSInput.INPUT_CHECK_CONTINUE;
                }
            }));
        }

        FSInput.setMainListener(new FSInput.Listener(){

            @Override
            public void preProcess(){
                collisionClosestEntry = null;
                collisionDistance = Float.MAX_VALUE;
            }

            @Override
            public void postProcess(){
                if(collisionClosestEntry != null){
                    onactivated.run();
                }
            }
        });
    }

    @Override
    protected void destroyAssets(){
        shadowPoint.destroy();

        texArrayLayer1.destroy();
        texArrayLayer2.destroy();
        texArrayLayer3.destroy();
    }
}