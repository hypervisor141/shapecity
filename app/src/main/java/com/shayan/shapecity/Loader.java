package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGAutomator;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightDirect;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;

import java.nio.ByteOrder;
import java.security.SecureRandom;

public final class Loader extends FSG{

    public static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_DISABLED;
    public static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    public static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(16));

    public static final int MAIN_PROGRAMSET = 0;
    public static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(2F));
    public static final FSGamma GAMMA = new FSGamma(new VLFloat(1.15F));

    public static int BUFFER_ELEMENT_SHORT_DEFAULT;
    public static int BUFFER_ARRAY_FLOAT_DEFAULT;
    public static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static final SecureRandom RANDOM = new SecureRandom();

    public static FSLightDirect lightDirect;
    public static FSLightPoint lightPoint;

    public static Layer layer1;
    public static Layer layer2;
    public static Layer layer3;
    public static Layer[] layers;

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

        lightPoint = new FSLightPoint(new FSAttenuation(new VLFloat(1.0F), new VLFloat(0.007F), new VLFloat(0.0002F)), new VLArrayFloat(new float[]{ 0F, 5F, -5F, 1.0F }));
        lightDirect = new FSLightDirect(new VLArrayFloat(new float[]{ 0F, 400F, 600F, 1.0F }), new VLArrayFloat(new float[]{ 0F, 0F, 0F, 1.0F }));

        FSBufferManager manager = bufferManager();
        BUFFER_ELEMENT_SHORT_DEFAULT = manager.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        FSP program = new FSP(DEBUG_MODE_PROGRAMS);
        program.modify(new ModModel.UBO(1, Layer.INSTANCE_COUNT), FSConfig.POLICY_ALWAYS);
        program.modify(new ModColor.TextureAndUBO(1, Layer.INSTANCE_COUNT, true, false, true), FSConfig.POLICY_ALWAYS);
        program.modify(new ModLight.Point(Loader.GAMMA, null, Loader.BRIGHTNESS, Loader.lightPoint, null, Loader.MATERIAL_WHITE_RUBBER.getGLSLSize()), FSConfig.POLICY_ALWAYS);
        program.addMeshConfig(new FSP.DrawElementsInstanced(FSConfig.POLICY_ALWAYS, 0));
        program.build();

        programSet(Loader.MAIN_PROGRAMSET).add(program);

        layer1 = new Layer(program, "pieces1.");
        layer2 = new Layer(program, "pieces2.");
        layer3 = new Layer(program, "pieces3.");

        layers = new Layer[]{
                layer1,
                layer2,
                layer3,
        };

        FSGAutomator automator = automator();
        automator.register(layer1);
        automator.register(layer2);
        automator.register(layer3);
        automator.run(DEBUG_MODE_AUTOMATOR);

        Game.startGame(this);
    }

    @Override
    public void update(int passindex, int programsetindex){
        bufferManager().updateIfNeeded();
    }

    @Override
    protected void destroyAssets(){
        layer1.mesh().instance(0).colorTexture().destroy();
        layer2.mesh().instance(0).colorTexture().destroy();
        layer3.mesh().instance(0).colorTexture().destroy();
    }
}