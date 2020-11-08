
package com.shayan.shapecity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES32;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

import firestorm.FSActivity;
import firestorm.FSAttenuation;
import firestorm.FSBounds;
import firestorm.FSBoundsCuboid;
import firestorm.FSBrightness;
import firestorm.FSBufferLayout;
import firestorm.FSConfig;
import firestorm.FSGamma;
import firestorm.FSInput;
import firestorm.FSInstance;
import firestorm.FSLightDirect;
import firestorm.FSLightMaterial;
import firestorm.FSLightPoint;
import firestorm.FSLoader;
import firestorm.FSMath;
import firestorm.FSMesh;
import firestorm.FSModelCluster;
import firestorm.FSP;
import firestorm.FSRenderer;
import firestorm.FSSchematics;
import firestorm.FSShadowDirect;
import firestorm.FSShadowPoint;
import firestorm.FSTexture;
import firestorm.FSTools;
import vanguard.VLArray;
import vanguard.VLArrayFloat;
import vanguard.VLFloat;
import vanguard.VLInt;
import vanguard.VLListType;
import vanguard.VLTask;
import vanguard.VLTaskContinous;
import vanguard.VLV;
import vanguard.VLVCluster;
import vanguard.VLVConst;
import vanguard.VLVInterpolated;
import vanguard.VLVLinear;
import vanguard.VLVProcessor;

public final class Loader extends FSLoader{

    protected static final float[] COLOR_WHITE = new float[]{
            1F, 1F, 1F, 1F
    };
    protected static final float[] COLOR_WHITE_X2 = new float[]{
            4F, 4F, 4F, 1F
    };
    protected static final float[] COLOR_ORANGE = new float[]{
            1.0F, 0.7F, 0F, 1F
    };
    protected static final float[] COLOR_OBSIDIAN = new float[]{
            0.3F, 0.3F, 0.3F, 1F
    };
    protected static final float[] COLOR_OBSIDIAN2 = new float[]{
            0.4F, 0.4F, 0.4F, 1F
    };
    protected static final float[] COLOR_GOLD = new float[]{
            0.83F, 0.68F, 0.21F, 1F
    };
    protected static final float[] COLOR_DARK_ORANGE = new float[]{
            1.0F, 0.4F, 0F, 1F
    };

    protected static final float[] COLOR_CURRENT = COLOR_WHITE;
    protected static final float[] COLOR_SELECTED = COLOR_WHITE_X2;

    private static final int SHADOW_PROGRAMSET = 0;
    private static final int MAIN_PROGRAMSET = 1;
    private static final int PIECES_INSTANCE_COUNT = 36;
    private static final int SHADOWMAP_ORTHO_DIAMETER = 4;
    private static final int SHADOWMAP_ORTHO_NEAR = 1;
    private static final int SHADOWMAP_ORTHO_FAR = 1500;
    private static final int PIECE_TEXTURE_DIMENSION = 512;
    private static int UBOBINDPOINT = 0;
    protected static int TEXUNIT = 0;

    protected static FSInput.Entry COLLISION_CLOSEST;
    protected static float COLLISION_MIN_DISTANCE;
    protected static final float[] CLAMPEDPOINTCACHE = new float[3];

    private static final SecureRandom RANDOM = new SecureRandom();

    protected static FSTexture TEX_ARRAY;
    protected static FSLightDirect LIGHT_DIRECT;
    protected static FSLightPoint LIGHT_POINT;
    protected static FSShadowDirect SHADOW_DIRECT;
    protected static FSShadowPoint SHADOW_POINT;
    protected static FSBrightness BRIGHTNESS;
    private static FSGamma GAMMA;
    private static FSLightMaterial MATERIAL_DEFAULT;
    private static FSLightMaterial MATERIAL_GOLD;
    private static FSLightMaterial MATERIAL_OBSIDIAN;
    private static FSLightMaterial MATERIAL_WHITE_RUBBER;
    private static final VLInt SHADOW_POINT_PCF_SAMPLES = new VLInt(20);

    private static ModDepthMap.Prepare MOD_DEPTH_PREP;
    private static ModDepthMap.SetupDirect MOD_DEPTH_SETUP_DIRECT;
    private static ModDepthMap.SetupPoint MOD_DEPTH_SETUP_POINT;
    private static ModDepthMap.Finish MODE_DEPTH_FINISH;
    private static ModModel.UBO MOD_MODEL_UBO;
    private static ModModel.Uniform MOD_MODEL_UNIFORM;
    private static ModColor.TextureAndUBO MOD_COLOR_TEXTURE_AND_UBO;
    private static ModColor.Uniform MOD_COLOR_UNIFORM;
    private static ModLight.Direct MOD_LIGHT_DIRECT;
    private static ModLight.Point MOD_LIGHT_POINT;
    private static ModNoLight MOD_NO_LIGHT;

    private static FSMesh lightbox;
    private static FSMesh pieces;
    private static FSMesh city;

    private static int BUFFER_ELEMENT_SHORT_DEFAULT;
    private static int BUFFER_ARRAY_FLOAT_DEFAULT;

    protected Loader(){
        super(2);
    }

    @Override
    protected void assemble(FSActivity act){
        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        prepare(act);

        addLightSphere();
        addPieces();
        addSingularParts();

        AUTOMATOR.execute(DEBUG_DISABLED);

        postProcess();
    }

    @Override
    public void update(int passindex, int programsetindex){
        BUFFERMANAGER.updateIfNeeded();
    }

    private void prepare(final FSActivity act){
        BRIGHTNESS = new FSBrightness(new VLFloat(2f));

        GAMMA = new FSGamma(new VLFloat(1.5f));

//        LIGHT_DIRECT = new FSLightDirect(
//                new VLArrayFloat(new float[]{ 0, 10, 30, 1.0f }),
//                new VLArrayFloat(new float[]{ 0f, 0f, 0f }));
//
//        SHADOW_DIRECT = new FSShadowDirect(LIGHT_DIRECT,
//                new VLInt(2048),
//                new VLInt(2048),
//                new VLFloat(0.00001f),
//                new VLFloat(0.0001f),
//                new VLFloat(4f));
//
//        SHADOW_DIRECT.initialize(new VLInt(Loader.TEXUNIT++));

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

        LIGHT_POINT = new FSLightPoint(
                new FSAttenuation(new VLFloat(1.0f), new VLFloat(0.014f), new VLFloat(0.0007f)),
                new VLArrayFloat(new float[]{ -10, 10, 0, 1.0f }));

        SHADOW_POINT = new FSShadowPoint(LIGHT_POINT,
                new VLInt(1024),
                new VLInt(1024),
                new VLFloat(0.45f), new VLFloat(0.5f),
                new VLFloat(1.5f),
                new VLFloat(1f),
                new VLFloat(1300));

        SHADOW_POINT.initialize(new VLInt(Loader.TEXUNIT++));

        MATERIAL_DEFAULT = new FSLightMaterial(new VLArrayFloat(
                new float[]{ 0.2f, 0.2f, 0.2f }),
                new VLFloat(32));

        MATERIAL_GOLD = new FSLightMaterial(
                new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }),
                new VLArrayFloat(new float[]{0.75164f, 0.60648f, 0.22648f }),
                new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }),
                new VLFloat(32));

        MATERIAL_OBSIDIAN = new FSLightMaterial(
                new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }),
                new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }),
                new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }),
                new VLFloat(256));

        MATERIAL_WHITE_RUBBER = new FSLightMaterial(
                new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }),
                new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }),
                new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }),
                new VLFloat(32));

        int materialsize = MATERIAL_DEFAULT.getGLSLSize();

//        MOD_DEPTH_PREP = new ModDepthMap.Prepare(SHADOW_DIRECT.frameBuffer(), SHADOW_DIRECT.width(), SHADOW_DIRECT.height());
//        MOD_DEPTH_SETUP_DIRECT = new ModDepthMap.SetupDirect(SHADOW_DIRECT.lightViewProjection());
//        MOD_DEPTH_PREP = new ModDepthMap.Prepare(SHADOW_POINT.frameBuffer(), SHADOW_POINT.width(), SHADOW_POINT.height(), true);
        MOD_DEPTH_PREP = new ModDepthMap.Prepare(SHADOW_POINT.frameBuffer(), SHADOW_POINT.width(), SHADOW_POINT.height(), false);
        MOD_DEPTH_SETUP_POINT = new ModDepthMap.SetupPoint(SHADOW_POINT, FSShadowPoint.SELECT_LIGHT_TRANSFORMS, LIGHT_POINT.position(), SHADOW_POINT.zFar());
//        MODE_DEPTH_FINISH = new ModDepthMap.Finish(SHADOW_DIRECT.frameBuffer());
        MODE_DEPTH_FINISH = new ModDepthMap.Finish(SHADOW_POINT.frameBuffer());
//        MOD_LIGHT_DIRECT = new ModLight.Direct(GAMMA, BRIGHTNESS, LIGHT_DIRECT, SHADOW_DIRECT, materialsize);
        MOD_LIGHT_POINT = new ModLight.Point(GAMMA, null, BRIGHTNESS, LIGHT_POINT, SHADOW_POINT, materialsize);
        MOD_NO_LIGHT = new ModNoLight(GAMMA, BRIGHTNESS);
        MOD_MODEL_UBO = new ModModel.UBO(1, PIECES_INSTANCE_COUNT);
        MOD_MODEL_UNIFORM = new ModModel.Uniform();
        MOD_COLOR_TEXTURE_AND_UBO = new ModColor.TextureAndUBO(1, PIECES_INSTANCE_COUNT, true, false);
        MOD_COLOR_UNIFORM = new ModColor.Uniform();

        TEX_ARRAY = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT));
        TEX_ARRAY.bind();
        TEX_ARRAY.storage3D(1, GLES32.GL_RGBA8, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, PIECES_INSTANCE_COUNT);
        TEX_ARRAY.minFilter(GLES32.GL_NEAREST);
        TEX_ARRAY.magFilter(GLES32.GL_NEAREST);
        TEX_ARRAY.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        TEX_ARRAY.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        TEX_ARRAY.baseLevel(0);
        TEX_ARRAY.maxLevel(PIECES_INSTANCE_COUNT - 1);

        FSTools.checkGLError();

        ByteBuffer pixels = null;
        Bitmap b = null;

        for(int i = 0; i < PIECES_INSTANCE_COUNT; i++){
            b = FSTools.generateTextedBitmap(act, String.valueOf(i), 40, Color.argb(255, 50, 50, 50),
                    Color.WHITE, true, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, FSTools.LOCATION_MID_CENTER, Bitmap.Config.ARGB_8888);

            if(pixels == null){
                pixels = ByteBuffer.allocate(b.getAllocationByteCount());
                pixels.order(ByteOrder.nativeOrder());
            }

            pixels.position(0);
            b.copyPixelsToBuffer(pixels);
            b.recycle();
            pixels.position(0);

            TEX_ARRAY.subImage3D(0, 0, 0, i, PIECE_TEXTURE_DIMENSION,
                    PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, pixels);

            FSTools.checkGLError();
        }

        TEX_ARRAY.unbind();

        BUFFER_ELEMENT_SHORT_DEFAULT = BUFFERMANAGER.addShortBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW, -1);
        BUFFER_ARRAY_FLOAT_DEFAULT = BUFFERMANAGER.addFloatBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW, -1);
    }

    private void addLightSphere(){
        FSConfig draw = new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0);

        FSP program = new FSP(DEBUG_DISABLED);
        program.modify(MOD_NO_LIGHT, FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(draw);
        program.build();

        Assembler assembler = new Assembler();
        assembler.ENABLE_DATA_PACK = true;
        assembler.ENABLE_COLOR_FILL = true;
        assembler.SYNC_MODELCLUSTER_AND_MODELARRAY = true;
        assembler.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assembler.SYNC_MODELARRAY_AND_BUFFER = true;
        assembler.SYNC_POSITION_AND_BUFFER = true;
        assembler.SYNC_COLOR_AND_BUFFER = true;
        assembler.SYNC_TEXCOORD_AND_BUFFER = true;
        assembler.SYNC_NORMAL_AND_BUFFER = true;
        assembler.SYNC_INDICES_AND_BUFFER = true;
        assembler.INSTANCE_SHARE_POSITIONS = true;
        assembler.INSTANCE_SHARE_COLORS = true;
        assembler.INSTANCE_SHARE_TEXCOORDS = true;
        assembler.INSTANCE_SHARE_NORMALS = true;
        assembler.LOAD_MODELS = true;
        assembler.LOAD_POSITIONS = true;
        assembler.LOAD_COLORS = true;
        assembler.LOAD_TEXCOORDS = false;
        assembler.LOAD_NORMALS = false;
        assembler.LOAD_INDICES = true;
        assembler.BUFFER_MODELS = false;
        assembler.BUFFER_POSITIONS = true;
        assembler.BUFFER_COLORS = false;
        assembler.BUFFER_TEXCOORDS = false;
        assembler.BUFFER_NORMALS = false;
        assembler.BUFFER_INDICES = true;
        assembler.DRAW_MODE_INDEXED = true;
        assembler.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assembler.configure();

        Registration reg = AUTOMATOR.addScannerSingle(assembler, new DataPack(new VLArrayFloat(COLOR_WHITE), null, MATERIAL_DEFAULT, null),
                "light_cylinder.001", GLES32.GL_TRIANGLES);
        reg.addProgram(program);

        lightbox = reg.mesh();

        FSBufferLayout layout = reg.bufferLayout();
        layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, FSBufferLayout.MECHANISM_SEQUENTIAL_SINGULAR).add(ELEMENT_POSITION);
        layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, FSBufferLayout.MECHANISM_INDICES_SINGULAR).add(ELEMENT_INDEX);

        programSet(MAIN_PROGRAMSET).add(program);
    }

    private void addPieces(){
        FSConfig draw = new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0);

        FSP program1 = new FSP(DEBUG_DISABLED);
        program1.modify(MOD_DEPTH_PREP, FSConfig.POLICY_ALWAYS);
        program1.modify(MOD_MODEL_UBO, FSConfig.POLICY_ALWAYS);
        program1.modify(MOD_DEPTH_SETUP_POINT, FSConfig.POLICY_ALWAYS);
        program1.addMeshConfig(draw);
        program1.build();

        FSP program2 = new FSP(DEBUG_DISABLED);
        program2.modify(MOD_MODEL_UBO, FSConfig.POLICY_ALWAYS);
        program2.modify(MOD_COLOR_TEXTURE_AND_UBO, FSConfig.POLICY_ALWAYS);
        program2.modify(MOD_LIGHT_POINT, FSConfig.POLICY_ALWAYS);
        program2.addMeshConfig(draw);
        program2.build();

        Assembler assembler = new Assembler();
        assembler.ENABLE_DATA_PACK = true;
        assembler.SYNC_MODELCLUSTER_AND_MODELARRAY = true;
        assembler.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assembler.SYNC_MODELARRAY_AND_BUFFER = true;
        assembler.SYNC_POSITION_AND_BUFFER = true;
        assembler.SYNC_COLOR_AND_BUFFER = true;
        assembler.SYNC_TEXCOORD_AND_BUFFER = true;
        assembler.SYNC_NORMAL_AND_BUFFER = true;
        assembler.SYNC_INDICES_AND_BUFFER = true;
        assembler.INSTANCE_SHARE_POSITIONS = true;
        assembler.INSTANCE_SHARE_COLORS = false;
        assembler.INSTANCE_SHARE_TEXCOORDS = true;
        assembler.INSTANCE_SHARE_NORMALS = true;
        assembler.LOAD_MODELS = true;
        assembler.LOAD_POSITIONS = true;
        assembler.LOAD_COLORS = true;
        assembler.LOAD_TEXCOORDS = true;
        assembler.LOAD_NORMALS = true;
        assembler.LOAD_INDICES = true;
        assembler.BUFFER_MODELS = true;
        assembler.BUFFER_POSITIONS = true;
        assembler.BUFFER_COLORS = true;
        assembler.BUFFER_TEXCOORDS = true;
        assembler.BUFFER_NORMALS = true;
        assembler.BUFFER_INDICES = true;
        assembler.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assembler.ENABLE_COLOR_FILL = true;
        assembler.DRAW_MODE_INDEXED = true;
        assembler.configure();

        VLListType<DataPack> packs = new VLListType<>(PIECES_INSTANCE_COUNT, 10);
        DataPack pack = new DataPack(new VLArrayFloat(COLOR_WHITE), TEX_ARRAY, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < PIECES_INSTANCE_COUNT; i++){
            packs.add(pack);
        }

        Registration reg = AUTOMATOR.addScannerInstanced(assembler, new DataGroup(packs), "piece.", GLES32.GL_TRIANGLES, PIECES_INSTANCE_COUNT);

        reg.addProgram(program1);
        reg.addProgram(program2);

        pieces = reg.mesh();

        FSBufferLayout layout = reg.bufferLayout();

        int modelbuffer = BUFFERMANAGER.addFloatBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++);
        int colorbuffer = BUFFERMANAGER.addFloatBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++);

        layout.add(BUFFERMANAGER, modelbuffer, FSBufferLayout.MECHANISM_SEQUENTIAL_INSTANCED).add(ELEMENT_MODEL);
        layout.add(BUFFERMANAGER, colorbuffer, FSBufferLayout.MECHANISM_SEQUENTIAL_INSTANCED).add(ELEMENT_COLOR);
        layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, FSBufferLayout.MECHANISM_COMPLEX_SINGULAR).add(ELEMENT_POSITION).add(ELEMENT_TEXCOORD).add(ELEMENT_NORMAL);
        layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, FSBufferLayout.MECHANISM_INDICES_SINGULAR).add(ELEMENT_INDEX);

        programSet(SHADOW_PROGRAMSET).add(program1);
        programSet(MAIN_PROGRAMSET).add(program2);
    }

    private void addSingularParts(){
        FSConfig draw = new FSP.DrawElements(FSConfig.POLICY_ALWAYS, 0);

        FSP program1 = new FSP(DEBUG_DISABLED);
        program1.modify(MOD_MODEL_UNIFORM, FSConfig.POLICY_ALWAYS);
        program1.modify(MOD_DEPTH_SETUP_POINT, FSConfig.POLICY_ALWAYS);
        program1.modify(MODE_DEPTH_FINISH, FSConfig.POLICY_ALWAYS);
        program1.addMeshConfig(draw);
        program1.build();

        FSP program2 = new FSP(DEBUG_DISABLED);
        program2.modify(MOD_MODEL_UNIFORM, FSConfig.POLICY_ALWAYS);
        program2.modify(MOD_COLOR_UNIFORM, FSConfig.POLICY_ALWAYS);
        program2.modify(MOD_LIGHT_POINT, FSConfig.POLICY_ALWAYS);
        program2.addMeshConfig(draw);
        program2.build();

        Assembler assembler = new Assembler();
        assembler.ENABLE_DATA_PACK = true;
        assembler.SYNC_MODELCLUSTER_AND_MODELARRAY = true;
        assembler.SYNC_MODELARRAY_AND_SCHEMATICS = true;
        assembler.SYNC_MODELARRAY_AND_BUFFER = true;
        assembler.SYNC_POSITION_AND_BUFFER = true;
        assembler.SYNC_COLOR_AND_BUFFER = true;
        assembler.SYNC_TEXCOORD_AND_BUFFER = true;
        assembler.SYNC_NORMAL_AND_BUFFER = true;
        assembler.SYNC_INDICES_AND_BUFFER = true;
        assembler.INSTANCE_SHARE_POSITIONS = false;
        assembler.INSTANCE_SHARE_COLORS = false;
        assembler.INSTANCE_SHARE_TEXCOORDS = false;
        assembler.INSTANCE_SHARE_NORMALS = false;
        assembler.LOAD_MODELS = true;
        assembler.LOAD_POSITIONS = true;
        assembler.LOAD_COLORS = true;
        assembler.LOAD_TEXCOORDS = false;
        assembler.LOAD_NORMALS = true;
        assembler.LOAD_INDICES = true;
        assembler.BUFFER_MODELS = true;
        assembler.BUFFER_POSITIONS = true;
        assembler.BUFFER_COLORS = false;
        assembler.BUFFER_TEXCOORDS = false;
        assembler.BUFFER_NORMALS = true;
        assembler.BUFFER_INDICES = true;
        assembler.CONVERT_POSITIONS_TO_MODELARRAYS = true;
        assembler.ENABLE_COLOR_FILL = true;
        assembler.DRAW_MODE_INDEXED = true;
        assembler.configure();

        DataPack defaultpack = new DataPack(new VLArrayFloat(COLOR_WHITE), null, MATERIAL_WHITE_RUBBER, null);
        Registration reg = AUTOMATOR.addScannerSingle(assembler, defaultpack, "city_cylinder", GLES32.GL_TRIANGLES);

        reg.addProgram(program1);
        reg.addProgram(program2);

        city = reg.mesh();

        FSBufferLayout layout = reg.bufferLayout();
        int modelbuffer = BUFFERMANAGER.addFloatBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_DYNAMIC_DRAW, -1);

        layout.add(BUFFERMANAGER, modelbuffer, FSBufferLayout.MECHANISM_SEQUENTIAL_SINGULAR).add(ELEMENT_MODEL);
        layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, FSBufferLayout.MECHANISM_COMPLEX_SINGULAR).add(ELEMENT_POSITION).add(ELEMENT_NORMAL);
        layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, FSBufferLayout.MECHANISM_INDICES_SINGULAR).add(ELEMENT_INDEX);

        programSet(SHADOW_PROGRAMSET).add(program1);
        programSet(MAIN_PROGRAMSET).add(program2);
    }

    private void postProcess(){
        final float[] orgpos = LIGHT_POINT.position().provider().clone();

        VLVInterpolated v = new VLVInterpolated(0, 20, 100, VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_COS)
                .setTask(new VLTaskContinous(new VLTask.Task(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask t, VLVConst v){
//                float[] pos = LIGHT_DIRECT.position().provider();
                float[] pos = LIGHT_POINT.position().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.translateM(cache, 0, v.get(), 0f ,0f);
//                Matrix.rotateM(cache, 0, 0.4f, 0f, 1f ,0f);
                Matrix.multiplyMV(pos, 0, cache, 0, orgpos, 0);

                pos[0] /= pos[3];
                pos[1] /= pos[3];
                pos[2] /= pos[3];

//                SHADOW_DIRECT.updateLightProjection(0, 1, 0, -SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_DIAMETER,
//                        -SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_NEAR, SHADOWMAP_ORTHO_FAR);
//
//                LIGHT_DIRECT.updateDirection();

                SHADOW_POINT.updateLightVP();

                FSModelCluster set = lightbox.get(0).modelCluster();
                set.getX(0, 0).set(pos[0]);
                set.getY(0, 0).set(pos[1]);
                set.getZ(0, 0).set(pos[2]);
                set.sync();
            }
        }));

        VLVProcessor controlproc = FSRenderer.getControllersProcessor();
        controlproc.add(new VLVProcessor.Entry(v, 0));
        controlproc.activateLatest();
        controlproc.start();

        FSInstance instance;
        FSModelCluster modelcluster;
        FSSchematics schematics;
        float yv;

        final VLVProcessor yproc = new VLVProcessor(PIECES_INSTANCE_COUNT, PIECES_INSTANCE_COUNT / 2);
        final VLVProcessor cproc = new VLVProcessor(PIECES_INSTANCE_COUNT, PIECES_INSTANCE_COUNT / 2);

        VLVCluster colorcluster = new VLVCluster(pieces.size(), pieces.size() / 2);
        cproc.add(new VLVProcessor.Entry(colorcluster, 0, VLVProcessor.SYNC_INDEX, 0, 0));

        for(int i2 = 0; i2 < pieces.size(); i2++){
            instance = pieces.get(i2);
            modelcluster = instance.modelCluster();
            yv = modelcluster.getY(0, 0).get();
            schematics = instance.schematics();

            modelcluster.setY(0, 0, new VLVInterpolated(yv, yv + schematics.modelHeight() * 0.75F, 120 + RANDOM.nextInt(60),
                    VLV.LOOP_FORWARD_BACKWARD, VLV.INTERP_ACCELERATE_DECELERATE_CUBIC));

            modelcluster.addSet(0,1, 0);
            modelcluster.addRowRotate(0, new VLVConst(-90), VLVConst.ZERO, VLVConst.ZERO, VLVConst.ONE);
            modelcluster.sync();

            schematics.inputBounds().add(new FSBoundsCuboid(schematics,
                    50, 50f, 50f, FSBounds.MODE_X_OFFSET_VOLUMETRIC, FSBounds.MODE_Y_OFFSET_VOLUMETRIC, FSBounds.MODE_Z_OFFSET_VOLUMETRIC,
                    40f, 40f, 40f, FSBounds.MODE_X_VOLUMETRIC, FSBounds.MODE_Y_VOLUMETRIC, FSBounds.MODE_Z_VOLUMETRIC));

            yproc.add(new VLVProcessor.Entry(modelcluster, 1, RANDOM.nextInt(300)));
            yproc.activateLatest();

            colorcluster.addSet(1, 0);
            colorcluster.addRow(i2, 3, 0);
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_CURRENT[0], COLOR_SELECTED[0], 15, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_CURRENT[1], COLOR_SELECTED[1], 15, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_CURRENT[2], COLOR_SELECTED[2], 15, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.SYNCER.add(new VLArray.DefinitionCluster(instance.colors(), i2, 0, 0));
            colorcluster.sync();

            FSInput.add(FSInput.TYPE_TOUCH, new FSInput.Entry(pieces, i2, new FSInput.CollisionListener(){

                @Override
                public int activated(FSBounds.Collision results, FSInput.Entry entry, int boundindex, MotionEvent e1, MotionEvent e2, float f1, float f2, float[] near, float[] far){
                    if(e1.getAction() == MotionEvent.ACTION_UP){
                        FSBoundsCuboid bounds = (FSBoundsCuboid)entry.mesh.get(entry.instanceindex).schematics().inputBounds().get(boundindex);

                        float[] coords = bounds.offset().coordinates();

                        CLAMPEDPOINTCACHE[0] = coords[0] + FSMath.clamp(near[0], -bounds.getHalfWidth(), bounds.getHalfWidth());
                        CLAMPEDPOINTCACHE[1] = coords[1] + FSMath.clamp(near[1], -bounds.getHalfHeight(), bounds.getHalfHeight());
                        CLAMPEDPOINTCACHE[2] = coords[2] + FSMath.clamp(near[2], -bounds.getHalfDepth(), bounds.getHalfDepth());

                        float distance = FSMath.euclideanDistance(CLAMPEDPOINTCACHE, 0, near, 0, 3);

                        if(COLLISION_MIN_DISTANCE > distance){
                            COLLISION_MIN_DISTANCE = distance;
                            COLLISION_CLOSEST = entry;
                        }
                    }

                    return FSInput.INPUT_CHECK_CONTINUE;
                }
            }));
        }

        FSInput.setMainListener(new FSInput.Listener(){

            @Override
            public void preProcess(){
                COLLISION_CLOSEST = null;
                COLLISION_MIN_DISTANCE = Float.MAX_VALUE;
            }

            @Override
            public void postProcess(){
                if(COLLISION_CLOSEST != null){
                    cproc.pause();
                    cproc.reset();
                    cproc.sync();

                    cproc.deactivateAll();
                    cproc.activate(0);

                    VLVProcessor.Entry e = cproc.get(0);
                    e.setindex = COLLISION_CLOSEST.instanceindex;
                    e.syncindex = COLLISION_CLOSEST.instanceindex;

                    cproc.start();
                }
            }
        });

        yproc.start();

        PROCESSORS.add(yproc);
        PROCESSORS.add(cproc);
    }

    @Override
    protected void destroyAssets(){
        SHADOW_DIRECT.destroy();
        TEX_ARRAY.destroy();
    }
}