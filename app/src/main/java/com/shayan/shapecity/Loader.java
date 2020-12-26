package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGAutomator;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightDirect;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSShadowDirect;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;

import java.nio.ByteOrder;
import java.security.SecureRandom;

public final class Loader extends FSG{

    public static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_FULL;
    public static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    public static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(16));

    public static final int SHADOW_PROGRAMSET = 0;
    public static final int MAIN_PROGRAMSET = 1;
    public static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(1F));
    public static final FSGamma GAMMA = new FSGamma(new VLFloat(1.1F));

    public static int BUFFER_ELEMENT_SHORT_DEFAULT;
    public static int BUFFER_ARRAY_FLOAT_DEFAULT;
    public static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static final SecureRandom RANDOM = new SecureRandom();

    public static FSLightPoint light;
    public static FSShadowPoint shadow;

    public static FSLightDirect light2;
    public static FSShadowDirect shadow2;

    public static BPLayer bplayer;
    public static BPBase bpbase;
    public static BPInstanced bpinstanced;

    public static FSMesh[] layers;
    public static FSMesh layer1;
    public static FSMesh layer2;
    public static FSMesh layer3;
    public static FSMesh phase1_trapezoidx1;
    public static FSMesh phase1_trapezoidx2;
    public static FSMesh phase1_trapezoidy1;
    public static FSMesh phase1_trapezoidy2;
    public static FSMesh phase1_rect;
    public static FSMesh phase1_walls;
    public static FSMesh phase1_base;
    public static FSMesh phase2_pillar;
    public static FSMesh phase3_rect_layer1;
    public static FSMesh phase3_rect_layer2;
    public static FSMesh phase3_rect_layer3;
    public static FSMesh phase3_rect_layer4;
    public static FSMesh phase3_trapezoidx1;
    public static FSMesh phase3_trapezoidx2;
    public static FSMesh phase3_trapezoidy1;
    public static FSMesh phase3_trapezoidy2;
    public static FSMesh phase3_outrect;
    public static FSMesh outbase_powerplant;
    public static FSMesh outbase_walls;
    public static FSMesh mainbase;

    public Loader(){
        super(2, 50, 10);
    }

    @Override
    public void assemble(FSActivity act){
        FSBufferManager manager = bufferManager();
        BUFFER_ELEMENT_SHORT_DEFAULT = manager.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        light = new FSLightPoint(new FSAttenuation.Radius(new VLFloat(10000F)), new VLArrayFloat(new float[]{ 0F, 10F, -40F, 1.0F }));
//        light2 = new FSLightDirect(new VLArrayFloat(new float[]{ 0F, 4000F, 6000F, 1.0F }), new VLArrayFloat(new float[]{ 0F, 0F, 0F, 1.0F }));

//        shadow = new FSShadowPoint(light, new VLInt(1024), new VLInt(1024), new VLFloat(0.0005F), new VLFloat(0.0005F), new VLFloat(1.5F), new VLFloat(1F), new VLFloat(140F));
//        shadow.initialize(new VLInt(Loader.TEXUNIT++));
//
//        shadow2 = new FSShadowDirect(light2, new VLInt(1500), new VLInt(1500), new VLFloat(0.0005F), new VLFloat(0.005F), new VLFloat(4F));
//        shadow2.initialize(new VLInt(Loader.TEXUNIT++));

        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        bplayer = new BPLayer(this);
        bpbase = new BPBase(this);
        bpinstanced = new BPInstanced(this, 60);

        FSGAutomator automator = automator();

        layer1 = automator.register(bplayer, "pieces1.");
        layer2 = automator.register(bplayer, "pieces2.");
        layer3 = automator.register(bplayer, "pieces3.");
        phase1_trapezoidx1 = automator.register(bpinstanced, "phase1_trapezoid_x1.");
        phase1_trapezoidx2 = automator.register(bpinstanced, "phase1_trapezoid_x2.");
        phase1_trapezoidy1 = automator.register(bpinstanced, "phase1_trapezoid_y1.");
        phase1_trapezoidy2 = automator.register(bpinstanced, "phase1_trapezoid_y2.");
        phase1_rect = automator.register(bpinstanced, "phase1_rect.");
        phase1_walls = automator.register(bpinstanced, "phase1_walls.");
        phase1_base = automator.register(bpbase, "phase1_base_Cube.157");
        phase2_pillar = automator.register(bpinstanced, "phase2_pillar.");
        phase3_rect_layer1 = automator.register(bpinstanced, "phase3_rect_layer1.");
        phase3_rect_layer2 = automator.register(bpinstanced, "phase3_rect_layer2.");
        phase3_rect_layer3 = automator.register(bpinstanced, "phase3_rect_layer3.");
        phase3_rect_layer4 = automator.register(bpinstanced, "phase3_rect_layer4.");
        phase3_trapezoidx1 = automator.register(bpinstanced, "phase3_trapezoidx1.");
        phase3_trapezoidx2 = automator.register(bpinstanced, "phase3_trapezoidx2.");
        phase3_trapezoidy1 = automator.register(bpinstanced, "phase3_trapezoidy1.");
        phase3_trapezoidy2 = automator.register(bpinstanced, "phase3_trapezoidy2.");
        phase3_outrect = automator.register(bpinstanced, "phase3_outrect.");
        outbase_powerplant = automator.register(bpinstanced, "outbase_powerplant.");
        outbase_walls = automator.register(bpinstanced, "outbase_walls.");
        mainbase = automator.register(bpbase, "base_Cube.036");

        automator.run(DEBUG_MODE_AUTOMATOR);

        layers = new FSMesh[]{
                layer1,
                layer2,
                layer3
        };

        Game.startGame(this);
    }

    @Override
    public void update(int passindex, int programsetindex){
        bufferManager().updateIfNeeded();
    }

    @Override
    protected void destroyAssets(){
        shadow.destroy();
    }
}