
package com.shayan.shapecity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES32;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSInput;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSLoader;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSModelCluster;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSSchematics;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.firestorm.FSTexture;
import com.nurverek.firestorm.FSTools;
import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLArrayFloat;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public final class Loader extends FSLoader {

    protected static final float[] COLOR_WHITE = new float[]{
            1F, 1F, 1F, 1F
    };
    protected static final float[] COLOR_WHITE_EXTRA = new float[]{
            4F, 4F, 4F, 1F
    };
    protected static final float[] COLOR_WHITE_EXTRA_2 = new float[]{
            8F, 8F, 8F, 1F
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

    protected static final float[] COLOR_PIECES = COLOR_WHITE;
    protected static final float[] COLOR_SELECTED = COLOR_WHITE_EXTRA_2;
    protected static final int COLOR_PIECE_TEXTURE_BG = Color.argb(255,20,20,20);
    protected static final int COLOR_PIECE_TEXTURE_TEXT = Color.WHITE;

    private static final int SHADOW_PROGRAMSET = 0;
    private static final int MAIN_PROGRAMSET = 1;
    private static final int LAYER_INSTANCE_COUNT = 36;
    private static final int SHADOWMAP_ORTHO_DIAMETER = 4;
    private static final int SHADOWMAP_ORTHO_NEAR = 1;
    private static final int SHADOWMAP_ORTHO_FAR = 1500;
    private static final int PIECE_TEXTURE_DIMENSION = 512;
    private static final int SELECTION_CYCLES = 5;
    private static final int LIGHT_SPIN_CYCLES = 3600;
    private static int UBOBINDPOINT = 0;
    protected static int TEXUNIT = 0;

    protected static FSInput.Entry COLLISION_CLOSEST;
    protected static float COLLISION_MIN_DISTANCE;
    protected static final float[] CLAMPEDPOINTCACHE = new float[3];

    private static final SecureRandom RANDOM = new SecureRandom();

    protected static FSTexture TEX_ARRAY;
    protected static FSLightPoint LIGHT_POINT;
    protected static FSShadowPoint SHADOW_POINT;
    protected static FSBrightness BRIGHTNESS;
    private static FSGamma GAMMA;
    private static FSLightMaterial MATERIAL_DEFAULT;
    private static FSLightMaterial MATERIAL_GOLD;
    private static FSLightMaterial MATERIAL_OBSIDIAN;
    private static FSLightMaterial MATERIAL_WHITE_RUBBER;
    private static final VLInt SHADOW_POINT_PCF_SAMPLES = new VLInt(20);

    private static ModDepthMap.Prepare MOD_DEPTH_PREP;
    private static ModDepthMap.SetupPoint MOD_DEPTH_SETUP_POINT;
    private static ModDepthMap.Finish MODE_DEPTH_FINISH;
    private static ModModel.UBO MOD_MODEL_UBO;
    private static ModModel.Uniform MOD_MODEL_UNIFORM;
    private static ModColor.TextureAndUBO MOD_COLOR_TEXTURE_AND_UBO;
    private static ModColor.Uniform MOD_COLOR_UNIFORM;
    private static ModLight.Point MOD_LIGHT_POINT;
    private static ModNoLight MOD_NO_LIGHT;

    private static FSMesh lightbox;
    private static FSMesh layer1;
    private static FSMesh layer2;
    private static FSMesh layer3;
    private static FSMesh city;

    private static ByteBuffer PIXEL_BUFFER = null;

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

//        addLightSphere();
        addLayers();
        addSingularParts();

        AUTOMATOR.execute(DEBUG_DISABLED);

        postProcess();
    }

    @Override
    public void update(int passindex, int programsetindex){
        BUFFERMANAGER.updateIfNeeded();
    }

    private void prepare(final FSActivity act){
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

        BRIGHTNESS = new FSBrightness(new VLFloat(2f));
        GAMMA = new FSGamma(new VLFloat(1.5f));
        LIGHT_POINT = new FSLightPoint(
                new FSAttenuation(new VLFloat(1.0f), new VLFloat(0.014f), new VLFloat(0.0007f)),
                new VLArrayFloat(new float[]{ -10, 10, 0, 1.0f }));

        SHADOW_POINT = new FSShadowPoint(LIGHT_POINT,
                new VLInt(1250),
                new VLInt(1250),
                new VLFloat(0.5f), new VLFloat(0.55f),
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

        MOD_DEPTH_PREP = new ModDepthMap.Prepare(SHADOW_POINT.frameBuffer(), SHADOW_POINT.width(), SHADOW_POINT.height(), false);
        MOD_DEPTH_SETUP_POINT = new ModDepthMap.SetupPoint(SHADOW_POINT, FSShadowPoint.SELECT_LIGHT_TRANSFORMS, LIGHT_POINT.position(), SHADOW_POINT.zFar());
        MODE_DEPTH_FINISH = new ModDepthMap.Finish(SHADOW_POINT.frameBuffer());
        MOD_LIGHT_POINT = new ModLight.Point(GAMMA, null, BRIGHTNESS, LIGHT_POINT, SHADOW_POINT, materialsize);
        MOD_NO_LIGHT = new ModNoLight(GAMMA, BRIGHTNESS);
        MOD_MODEL_UBO = new ModModel.UBO(1, LAYER_INSTANCE_COUNT);
        MOD_MODEL_UNIFORM = new ModModel.Uniform();
        MOD_COLOR_TEXTURE_AND_UBO = new ModColor.TextureAndUBO(1, LAYER_INSTANCE_COUNT, true, false);
        MOD_COLOR_UNIFORM = new ModColor.Uniform();

        addTextures(act);
        addBuffers();
    }

    private void addTextures(FSActivity act){
        TEX_ARRAY = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(TEXUNIT));
        TEX_ARRAY.bind();
        TEX_ARRAY.storage3D(1, GLES32.GL_RGBA8, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, LAYER_INSTANCE_COUNT);
        TEX_ARRAY.minFilter(GLES32.GL_NEAREST);
        TEX_ARRAY.magFilter(GLES32.GL_NEAREST);
        TEX_ARRAY.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        TEX_ARRAY.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        TEX_ARRAY.baseLevel(0);
        TEX_ARRAY.maxLevel(LAYER_INSTANCE_COUNT - 1);

        FSTools.checkGLError();

        Bitmap b = null;

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            b = FSTools.generateTextedBitmap(act, String.valueOf(i), 30, COLOR_PIECE_TEXTURE_BG, COLOR_PIECE_TEXTURE_TEXT, true,
                    PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, FSTools.LOCATION_MID_CENTER, Bitmap.Config.ARGB_8888);

            if(PIXEL_BUFFER == null){
                PIXEL_BUFFER = ByteBuffer.allocate(b.getAllocationByteCount());
                PIXEL_BUFFER.order(ByteOrder.nativeOrder());
            }

            PIXEL_BUFFER.position(0);

            b.copyPixelsToBuffer(PIXEL_BUFFER);
            b.recycle();

            PIXEL_BUFFER.position(0);

            TEX_ARRAY.subImage3D(0, 0, 0, i, PIECE_TEXTURE_DIMENSION,
                    PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);

            FSTools.checkGLError();
        }

        TEX_ARRAY.unbind();
    }

    private void addBuffers(){
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

    private void addLayers(){
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

        VLListType<DataPack> packs = new VLListType<>(LAYER_INSTANCE_COUNT, 10);
        DataPack pack = new DataPack(new VLArrayFloat(COLOR_PIECES), TEX_ARRAY, MATERIAL_OBSIDIAN, null);

        for(int i = 0; i < LAYER_INSTANCE_COUNT; i++){
            packs.add(pack);
        }

        Registration reg1 = AUTOMATOR.addScannerInstanced(assembler, new DataGroup(packs), "layer1.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reg2 = AUTOMATOR.addScannerInstanced(assembler, new DataGroup(packs), "layer2.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);
        Registration reg3 = AUTOMATOR.addScannerInstanced(assembler, new DataGroup(packs), "layer3.", GLES32.GL_TRIANGLES, LAYER_INSTANCE_COUNT);

        reg1.addProgram(program1);
        reg2.addProgram(program1);
        reg3.addProgram(program1);

        reg1.addProgram(program2);
        reg2.addProgram(program2);
        reg3.addProgram(program2);

        layer1 = reg1.mesh();
        layer2 = reg2.mesh();
        layer3 = reg3.mesh();

        FSBufferLayout[] layouts = new FSBufferLayout[]{
                reg1.bufferLayout(),
                reg2.bufferLayout(),
                reg3.bufferLayout()
        };

        FSBufferLayout layout;

        for(int i = 0; i < layouts.length; i++){
            layout = layouts[i];

            int modelbuffer = BUFFERMANAGER.addFloatBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++);
            int colorbuffer = BUFFERMANAGER.addFloatBuffer(GLES32.GL_UNIFORM_BUFFER, GLES32.GL_DYNAMIC_DRAW, UBOBINDPOINT++);

            layout.add(BUFFERMANAGER, modelbuffer, FSBufferLayout.MECHANISM_SEQUENTIAL_INSTANCED).add(ELEMENT_MODEL);
            layout.add(BUFFERMANAGER, colorbuffer, FSBufferLayout.MECHANISM_SEQUENTIAL_INSTANCED).add(ELEMENT_COLOR);
            layout.add(BUFFERMANAGER, BUFFER_ARRAY_FLOAT_DEFAULT, FSBufferLayout.MECHANISM_COMPLEX_SINGULAR).add(ELEMENT_POSITION).add(ELEMENT_TEXCOORD).add(ELEMENT_NORMAL);
            layout.add(BUFFERMANAGER, BUFFER_ELEMENT_SHORT_DEFAULT, FSBufferLayout.MECHANISM_INDICES_SINGULAR).add(ELEMENT_INDEX);
        }

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

        VLVLinear v = new VLVLinear(0, 360, LIGHT_SPIN_CYCLES, VLV.LOOP_FORWARD)
                .setTask(new VLTaskContinous(new VLTask.Task<VLVLinear>(){

            private float[] cache = new float[16];

            @Override
            public void run(VLTask t, VLVLinear v){
//                float[] pos = LIGHT_DIRECT.position().provider();
                float[] pos = LIGHT_POINT.position().provider();

                Matrix.setIdentityM(cache, 0);
                Matrix.rotateM(cache, 0, v.get(), 0f, 1f ,0f);
                Matrix.multiplyMV(pos, 0, cache, 0, orgpos, 0);

                pos[0] /= pos[3];
                pos[1] /= pos[3];
                pos[2] /= pos[3];

//                SHADOW_DIRECT.updateLightProjection(0, 1, 0, -SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_DIAMETER,
//                        -SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_DIAMETER, SHADOWMAP_ORTHO_NEAR, SHADOWMAP_ORTHO_FAR);
//
//                LIGHT_DIRECT.updateDirection();

                SHADOW_POINT.updateLightVP();

//                FSModelCluster set = lightbox.get(0).modelCluster();
//                set.getX(0, 0).set(pos[0]);
//                set.getY(0, 0).set(pos[1]);
//                set.getZ(0, 0).set(pos[2]);
//                set.sync();
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

        final VLVProcessor yproc = new VLVProcessor(LAYER_INSTANCE_COUNT, LAYER_INSTANCE_COUNT / 2);
        final VLVProcessor cproc = new VLVProcessor(LAYER_INSTANCE_COUNT, LAYER_INSTANCE_COUNT / 2);

        VLVCluster colorcluster = new VLVCluster(layer1.size(), layer1.size() / 2);
        cproc.add(new VLVProcessor.Entry(colorcluster, 0, VLVProcessor.SYNC_INDEX, 0, 0));

        for(int i2 = 0; i2 < layer1.size(); i2++){
            instance = layer1.get(i2);
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
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_PIECES[0], COLOR_SELECTED[0], SELECTION_CYCLES, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_PIECES[1], COLOR_SELECTED[1], SELECTION_CYCLES, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.addColumn(i2, 0, new VLVInterpolated(COLOR_PIECES[2], COLOR_SELECTED[2], SELECTION_CYCLES, VLV.LOOP_RETURN_ONCE, VLV.INTERP_LINEAR));
            colorcluster.SYNCER.add(new VLArray.DefinitionCluster(instance.colors(), i2, 0, 0));
            colorcluster.sync();

            FSInput.add(FSInput.TYPE_TOUCH, new FSInput.Entry(layer1, i2, new FSInput.CollisionListener(){

                @Override
                public int activated(FSBounds.Collision results, FSInput.Entry entry, int boundindex, MotionEvent e1, MotionEvent e2, float f1, float f2, float[] near, float[] far){
                    if(e1.getAction() == MotionEvent.ACTION_UP){
                        FSBoundsCuboid bounds = (FSBoundsCuboid)entry.mesh.get(entry.instanceindex).schematics().inputBounds().get(boundindex);

                        float[] coords = bounds.offset().coordinates();

                        CLAMPEDPOINTCACHE[0] = coords[0] + VLMath.clamp(near[0], -bounds.getHalfWidth(), bounds.getHalfWidth());
                        CLAMPEDPOINTCACHE[1] = coords[1] + VLMath.clamp(near[1], -bounds.getHalfHeight(), bounds.getHalfHeight());
                        CLAMPEDPOINTCACHE[2] = coords[2] + VLMath.clamp(near[2], -bounds.getHalfDepth(), bounds.getHalfDepth());

                        float distance = VLMath.euclideanDistance(CLAMPEDPOINTCACHE, 0, near, 0, 3);

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

        FSControl.setRenderContinuously(true);

        PROCESSORS.add(yproc);
        PROCESSORS.add(cproc);
    }

    private void changePieceTexture(final FSInstance instance, final int subimageindex, final String text){
        FSRenderer.addTask(new Runnable(){

            @Override
            public void run(){
                Bitmap b = FSTools.generateTextedBitmap(FSControl.getContext(), text, 30, COLOR_PIECE_TEXTURE_BG, COLOR_PIECE_TEXTURE_TEXT, true,
                        PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION, FSTools.LOCATION_MID_CENTER, Bitmap.Config.ARGB_8888);

                PIXEL_BUFFER.position(0);

                b.copyPixelsToBuffer(PIXEL_BUFFER);
                b.recycle();

                PIXEL_BUFFER.position(0);

                TEX_ARRAY.bind();
                TEX_ARRAY.subImage3D(0, 0, 0, subimageindex, PIECE_TEXTURE_DIMENSION, PIECE_TEXTURE_DIMENSION,
                        1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
                TEX_ARRAY.unbind();

                FSTools.checkGLError();
            }
        });
    }

    @Override
    protected void destroyAssets(){
        SHADOW_POINT.destroy();
        TEX_ARRAY.destroy();
    }
}