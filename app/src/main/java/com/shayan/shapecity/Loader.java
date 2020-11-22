
package com.nurverek.firestorm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES32;
import android.opengl.Matrix;
import android.view.MotionEvent;

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
import com.nurverek.vanguard.VLVCluster;
import com.nurverek.vanguard.VLVConst;
import com.nurverek.vanguard.VLVInterpolated;
import com.nurverek.vanguard.VLVLinear;
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

public final class Loader extends FSG{

    private static final float[] COLOR_WHITE = new float[]{
            1F, 1F, 1F, 1F
    };
    private static final float[] COLOR_WHITE_EXTRA = new float[]{
            4F, 4F, 4F, 1F
    };
    private static final float[] COLOR_WHITE_EXTRA_2 = new float[]{
            8F, 8F, 8F, 1F
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
    private static final float[] COLOR_GOLD = new float[]{
            0.83F, 0.68F, 0.21F, 1F
    };
    private static final float[] COLOR_DARK_ORANGE = new float[]{
            1.0F, 0.4F, 0F, 1F
    };

    private final static FSLightMaterial MATERIAL_DEFAULT = new FSLightMaterial(new VLArrayFloat(
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

    private static final float[] COLOR_PIECES = COLOR_OBSIDIAN_LESS;
    private static final float[] COLOR_INPUT = COLOR_WHITE_EXTRA_2;
    private static final float[] COLOR_ACTIVE = COLOR_DARK_ORANGE;

    private static final int CLUSTER_COLOR_INPUT = 0;
    private static final int CLUSTER_COLOR_ACTIVATE = 1;
    private static final int CLUSTER_MODEL_ROTATE_Z = 0;
    private static final int CLUSTER_MODEL_ROTATE_Y = 1;
    private static final int CLUSTER_MODEL_NAVIGATION = 2;

    private static final int CYCLES_INPUT = 20;
    private static final int CYCLES_ACTIVATE = 60;
    private static final int CYCLES_ROTATE = 30;
    private static final int CYCLES_VERTICAL_MOVE = 200;

    private static final int SHADOW_PROGRAMSET = 0;
    private static final int MAIN_PROGRAMSET = 1;
    private static final int LAYER_INSTANCE_COUNT = 36;
    private static final int SHADOWMAP_ORTHO_DIAMETER = 4;
    private static final int SHADOWMAP_ORTHO_NEAR = 1;
    private static final int SHADOWMAP_ORTHO_FAR = 1500;
    private static final int PIECE_TEXTURE_DIMENSION = 512;
    private static final int SELECTION_CYCLES = 5;
    private static final int LIGHT_SPIN_CYCLES = 3600;
    private static final float Y_REDUCTION = 0.5f;

    private static final float[] CLAMPEDPOINTCACHE = new float[3];
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final VLInt SHADOW_POINT_PCF_SAMPLES = new VLInt(20);

    private static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(2f));
    private static final FSGamma GAMMA = new FSGamma(new VLFloat(1.5f));

    private static int UBOBINDPOINT = 0;
    private static int TEXUNIT = 0;
    private static ByteBuffer PIXEL_BUFFER = null;

    private FSInput.Entry collisionClosestPoint;
    private float collisionMinDistance;

    private FSTexture texArray;
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

    private VLVProcessor procLayer1Y;
    private VLVProcessor procLayer2Y;
    private VLVProcessor procLayer3Y;

    private VLVProcessor procLayer1R;
    private VLVProcessor procLayer2R;
    private VLVProcessor procLayer3R;

    private VLVProcessor procLayer1CI;
    private VLVProcessor procLayer2CI;
    private VLVProcessor procLayer3CI;

    private VLVProcessor procLayer1CA;
    private VLVProcessor procLayer2CA;
    private VLVProcessor procLayer3CA;

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
        
        int debug = FSControl.DEBUG_FULL;

        ////////// SETUP

        addBasics();
        addTextures(act);

        ////////// BUILD

        FSBufferLayout[] layerlayouts = registerLayers();
        FSBufferLayout citylayout = registerSingular();

        AUTOMATOR.build(debug);

        ////////// BUFFER

        createLinks();
        prepareBufferLayouts(layerlayouts, citylayout);

        AUTOMATOR.buffer(debug);

        ////////// PROGRAM

        setupPrograms();

        AUTOMATOR.program(debug);

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

        programDepthSingular = new FSP(FSControl.DEBUG_FULL);
        programMainSingular = new FSP(FSControl.DEBUG_FULL);
        programDepthLayers = new FSP(FSControl.DEBUG_FULL);
        programMainLayers = new FSP(FSControl.DEBUG_FULL);

        lightPoint = new FSLightPoint(
                new FSAttenuation(new VLFloat(1.0f), new VLFloat(0.014f), new VLFloat(0.0007f)),
                new VLArrayFloat(new float[]{ -10, 10, 0, 1.0f }));

        shadowPoint = new FSShadowPoint(lightPoint,
                new VLInt(1250),
                new VLInt(1250),
                new VLFloat(0.5f), new VLFloat(0.55f),
                new VLFloat(1.5f),
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
    }

    private void addTextures(FSActivity act){
        texArray = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT));
        texArray.bind();
        texArray.storage3D(1, GLES32.GL_RGBA8, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, LAYER_INSTANCE_COUNT);
        texArray.minFilter(GLES32.GL_LINEAR);
        texArray.magFilter(GLES32.GL_LINEAR);
        texArray.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArray.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArray.baseLevel(0);
        texArray.maxLevel(LAYER_INSTANCE_COUNT - 1);

        FSTools.checkGLError();

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

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.outConfig = Bitmap.Config.ARGB_8888;
            opts.inScaled = true;
            opts.inMutable = true;

            b = BitmapFactory.decodeResource(act.getResources(), resources[i % resources.length], opts);

            if(PIXEL_BUFFER == null){
                PIXEL_BUFFER = ByteBuffer.allocate(b.getAllocationByteCount());
                PIXEL_BUFFER.order(ByteOrder.nativeOrder());
            }

            PIXEL_BUFFER.position(0);

            b.copyPixelsToBuffer(PIXEL_BUFFER);
            b.recycle();

            PIXEL_BUFFER.position(0);

            texArray.subImage3D(0, 0, 0, i, PIECE_TEXTURE_DIMENSION,
                    PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);

            FSTools.checkGLError();
        }

        texArray.unbind();
    }

    private FSBufferLayout[] registerLayers(){
        FSP programdepthlayers = new FSP(FSControl.DEBUG_FULL);
        FSP programmainlayers = new FSP(FSControl.DEBUG_FULL);

        Assembler assemblerlayers = new Assembler();
        assemblerlayers.ENABLE_DATA_PACK = true;
        assemblerlayers.SYNC_MODELCLUSTER_AND_MODELARRAY = true;
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

        VLListType<DataPack> layerpacks = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        DataPack pack = new DataPack(new VLArrayFloat(COLOR_PIECES), texArray, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            layerpacks.add(pack);
        }

        Registration reglayer1 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layerpacks), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reglayer2 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layerpacks), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reglayer3 = AUTOMATOR.addScannerInstanced(assemblerlayers, new DataGroup(layerpacks), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reglayer1.addProgram(programdepthlayers);
        reglayer1.addProgram(programmainlayers);
        reglayer2.addProgram(programdepthlayers);
        reglayer2.addProgram(programmainlayers);
        reglayer3.addProgram(programdepthlayers);
        reglayer3.addProgram(programmainlayers);

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
        FSP programdepthsingular = new FSP(FSControl.DEBUG_FULL);
        FSP programmainsingular = new FSP(FSControl.DEBUG_FULL);

        Assembler assemblersingular = new Assembler();
        assemblersingular.ENABLE_DATA_PACK = true;
        assemblersingular.SYNC_MODELCLUSTER_AND_MODELARRAY = true;
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

        cityreg.addProgram(programdepthsingular);
        cityreg.addProgram(programmainsingular);

        city = cityreg.mesh();

        return cityreg.bufferLayout();
    }

    private void createLinks(){
//        VLListType<FSLinkType> links1 = new VLListType<>(1, 0);
//        VLListType<FSLinkType> links2 = new VLListType<>(1, 0);
//        VLListType<FSLinkType> links3 = new VLListType<>(1, 0);
//
//        float[] array1 = new float[LAYER_INSTANCE_COUNT];
//        float[] array2 = new float[LAYER_INSTANCE_COUNT];
//        float[] array3 = new float[LAYER_INSTANCE_COUNT];
//
//        Arrays.fill(array1, 1);
//        Arrays.fill(array2, 1);
//        Arrays.fill(array3, 1);
//
//        links1.add(new CustomLinks.TextureControlLink(new VLArrayFloat(array1)));
//        links2.add(new CustomLinks.TextureControlLink(new VLArrayFloat(array2)));
//        links3.add(new CustomLinks.TextureControlLink(new VLArrayFloat(array3)));
//
//        layer1.initLinks(links1);
//        layer2.initLinks(links2);
//        layer3.initLinks(links3);
//
//        city.initLinks(new VLListType<>(0, 0));
    }

    private void prepareBufferLayouts(FSBufferLayout[] layerlayouts, FSBufferLayout citylayout){
        FSBufferLayout layout;

        for(int i = 0; i < layerlayouts.length; i++){
            layout = layerlayouts[i];

            int modelbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER,
                    GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

//            int texcontrolbuffer = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_UNIFORM_BUFFER,
//                    GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++), new VLBufferFloat()));

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

//            layout.add(BUFFERMANAGER, texcontrolbuffer, 1)
//                    .addLink(new FSBufferLayout.EntryLink(FSBufferLayout.LINK_SEQUENTIAL_SINGULAR, 0, 0, 1, 1));
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
        procLayer1Y = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer2Y = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer3Y = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);

        procLayer1R = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer2R = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer3R = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);

        procLayer1CI = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer2CI = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer3CI = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);

        procLayer1CA = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer2CA = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);
        procLayer3CA = new VLVProcessor(LAYER_INSTANCE_COUNT, 0);

        VLVProcessor[] rprocs = new VLVProcessor[]{ procLayer1R, procLayer2R, procLayer3R
        };
        VLVProcessor[] yprocs = new VLVProcessor[]{ procLayer1Y, procLayer2Y, procLayer3Y
        };
        VLVProcessor[] ciprocs = new VLVProcessor[]{ procLayer1CI, procLayer2CI, procLayer3CI
        };
        VLVProcessor[] caprocs = new VLVProcessor[]{ procLayer1CA, procLayer2CA, procLayer3CA
        };

        FSMesh layer;
        FSInstance instance;
        FSModelCluster modelcluster;
        FSSchematics schematics;
        VLVCluster colorcluster;
        VLVProcessor yproc;
        VLVProcessor rproc;
        VLVProcessor ciproc;
        VLVProcessor caproc;
        float yv;

        for(int i = 0; i < layers.length; i++){
            layer = layers[i];
            yproc = yprocs[i];
            rproc = rprocs[i];
            ciproc = ciprocs[i];
            caproc = caprocs[i];

            PROCESSORS.add(caproc);
            PROCESSORS.add(yproc);

            for(int i2 = 0; i2 < layer.size(); i2++){
                instance = layer.instance(i2);
                modelcluster = instance.modelCluster();
                schematics = instance.schematics();
                yv = modelcluster.getY(0, 0).get() - Y_REDUCTION;

                modelcluster.addSet(0,1, 0);
                modelcluster.addSet(0,1, 0);
                modelcluster.addRowRotate(CLUSTER_MODEL_ROTATE_Z, new VLVConst(-90), VLVConst.ZERO, VLVConst.ZERO, VLVConst.ONE);
                modelcluster.addRowRotate(CLUSTER_MODEL_ROTATE_Y, new VLVInterpolated(0, -45, CYCLES_ROTATE, VLV.LOOP_NONE, VLV.INTERP_DECELERATE_SINE_SQRT), VLVConst.ZERO, VLVConst.ONE, VLVConst.ZERO);
                modelcluster.setY(2, 0, new VLVInterpolated(yv, yv + schematics.modelHeight() * 0.75F, 120 + RANDOM.nextInt(60), VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_CUBIC));
                modelcluster.sync();

                colorcluster = new VLVCluster(2, 0);
                colorcluster.addSet(1, 0);
                colorcluster.addSet(1, 0);

                colorcluster.addRow(CLUSTER_COLOR_INPUT, 4, 0);
                colorcluster.addColumn(CLUSTER_COLOR_INPUT, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_INPUT[0], CYCLES_INPUT, VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_INPUT, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_INPUT[1], CYCLES_INPUT, VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_INPUT, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_INPUT[2], CYCLES_INPUT, VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_INPUT, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_INPUT[3], CYCLES_INPUT, VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_COS));

                colorcluster.addRow(CLUSTER_COLOR_ACTIVATE, 4, 0);
                colorcluster.addColumn(CLUSTER_COLOR_ACTIVATE, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_ACTIVE[0], CYCLES_ACTIVATE, VLV.LOOP_NONE, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_ACTIVATE, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_ACTIVE[1], CYCLES_ACTIVATE, VLV.LOOP_NONE, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_ACTIVATE, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_ACTIVE[2], CYCLES_ACTIVATE, VLV.LOOP_NONE, VLV.INTERP_ACCELERATE_DECELERATE_COS));
                colorcluster.addColumn(CLUSTER_COLOR_ACTIVATE, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_ACTIVE[3], CYCLES_ACTIVATE, VLV.LOOP_NONE, VLV.INTERP_ACCELERATE_DECELERATE_COS));

                rproc.add(new VLVProcessor.Entry(modelcluster, CLUSTER_MODEL_ROTATE_Y, RANDOM.nextInt(300)));
                yproc.add(new VLVProcessor.Entry(modelcluster, CLUSTER_MODEL_NAVIGATION, RANDOM.nextInt(300)));

                caproc.add(new VLVProcessor.Entry(colorcluster, CLUSTER_COLOR_INPUT, VLVProcessor.SYNC_INDEX, 0, 0));
                ciproc.add(new VLVProcessor.Entry(colorcluster, CLUSTER_COLOR_ACTIVATE, VLVProcessor.SYNC_INDEX, 0, 0));

                schematics.inputBounds().add(new FSBoundsCuboid(schematics,
                        50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC,
                        40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));
            }
        }
    }

    private void activateProc(VLVProcessor proc, int index){
        proc.pause();
        proc.reset();
        proc.deactivateAll();
        proc.activate(index);
        proc.start();

        FSControl.signalFrameRender(true);
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

                        if(collisionMinDistance > distance){
                            collisionMinDistance = distance;
                            collisionClosestPoint = entry;
                        }
                    }

                    return FSInput.INPUT_CHECK_CONTINUE;
                }
            }));
        }

        FSInput.setMainListener(new FSInput.Listener(){

            @Override
            public void preProcess(){
                collisionClosestPoint = null;
                collisionMinDistance = Float.MAX_VALUE;
            }

            @Override
            public void postProcess(){
                if(collisionClosestPoint != null){
                    onactivated.run();
                }
            }
        });
    }

    private void changePieceTexture(final FSInstance instance, final int subimageindex, final int resource){
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

                texArray.bind();
                texArray.subImage3D(0, 0, 0, subimageindex, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION,
                        1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
                texArray.unbind();

                FSTools.checkGLError();
            }
        });
    }

    private void startGame(){

    }

    @Override
    protected void destroyAssets(){
        shadowPoint.destroy();
        texArray.destroy();
    }
}