package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;

import java.nio.ByteOrder;
import java.security.SecureRandom;

public final class Gen extends FSG{

    public static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_FULL;
    public static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    public static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.72525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 1f, 1f, 1f }), new VLFloat(32));
    public static final FSLightMaterial MATERIAL_WHITE_LESS_SPECULAR = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 1f, 1f, 1f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE_MORE_SPECULAR = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 1f, 1f, 1f }), new VLFloat(128));
    public static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_SILVER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.19225F, 0.19225F, 0.19225F }), new VLArrayFloat(new float[]{ 0.50754F, 0.50754F, 0.50754F}), new VLArrayFloat(new float[]{ 0.508273F, 0.508273F, 0.508273F }), new VLFloat(16));
    
    public static final int MAIN_PROGRAMSET = 0;
    public static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(1F));
    public static final FSGamma GAMMA = new FSGamma(new VLFloat(1F));

    public static int BUFFER_ELEMENT_SHORT_DEFAULT;
    public static int BUFFER_ARRAY_FLOAT_DEFAULT;
    public static int TEXUNIT = 1;
    public static int UBOBINDPOINT = 0;

    public static final SecureRandom RANDOM = new SecureRandom();

    public static FSLightPoint light;

    public BPLayer bppieces;
    public BPBase bpsingular;
    public BPInstanced bpinstanced;

    public FSMesh platform;
    public FSMesh puzzlebase;
    public FSMesh puzzlebase_lining;
    public FSMesh puzzlebase_innerwalls1_frame;
    public FSMesh puzzlebase_innerwalls1_linings1;
    public FSMesh puzzlebase_innerwalls1_linings2;
    public FSMesh puzzlebase_innerwalls1_linings3;
    public FSMesh puzzlebase_innerwalls1_linings4;
    public FSMesh puzzlebase_innerwalls1_linings5;
    public FSMesh puzzlebase_innerwalls1_linings6;
    public FSMesh puzzlebase_innerwalls1_linings7;
    public FSMesh puzzlebase_innerwalls1_linings8;
    public FSMesh puzzlebase_bottom;
    public FSMesh puzzlebase_innerwalls2;
    public FSMesh pieces;
    public FSMesh phase1_base;
    public FSMesh phase1_base_inner;
    public FSMesh phase1_base_inner_lining;
    public FSMesh phase1_pillars;
    public FSMesh phase1_pillars_stripes;
    public FSMesh phase2;
    public FSMesh phase2_stripes;
    public FSMesh phase2_caps;
    public FSMesh phase3;
    public FSMesh phase3_caps;
    public FSMesh phase3_blades;
    public FSMesh phase3_baseframe1;
    public FSMesh phase3_baseframe2;
    public FSMesh phase3_baseframe3;
    public FSMesh phase3_baseframe4;
    public FSMesh phase4;
    public FSMesh phase4_caps;
    public FSMesh phase4_caps2;
    public FSMesh phase5_layer1;
    public FSMesh phase5_layer2;
    public FSMesh phase5_layer3;
    public FSMesh phase5_layer1_stripes;
    public FSMesh phase5_layer2_stripes;
    public FSMesh phase5_layer3_stripes;
    public FSMesh phase5_caps;
    public FSMesh phase5_trapezoidx1;
    public FSMesh phase5_trapezoidy1;
    public FSMesh phase5_trapezoidx2;
    public FSMesh phase5_trapezoidy2;
    public FSMesh phase6_layer1;
    public FSMesh phase6_layer2;
    public FSMesh phase6_layer3;
    public FSMesh phase6_layer4;
    public FSMesh phase6_layer5;
    public FSMesh phase6_layer6;
    public FSMesh phase6_layer7;
    public FSMesh phase6_layer8;
    public FSMesh phase6_layer9;
    public FSMesh phase6_layer10;
    public FSMesh phase6_layer11;
    public FSMesh mainbase1;
    public FSMesh mainbase2;

    public Gen(){
        super(2, 50, 10);
    }

    @Override
    public void assemble(FSActivity act){
        FSBufferManager manager = bufferManager();
        BUFFER_ELEMENT_SHORT_DEFAULT = manager.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        light = new FSLightPoint(new FSAttenuation.Radius(new VLFloat(20000F)), new VLArrayFloat(new float[]{ 0F, 5F, -50F, 1.0F }));

        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        bppieces = new BPLayer(this);
        bpsingular = new BPBase(this);
        bpinstanced = new BPInstanced(this, 60);

        pieces = register(bppieces, "pieces.", Puzzle.COLOR_LAYER, MATERIAL_WHITE_MORE_SPECULAR);
        platform = register(bpsingular, "platform_Cube.637", Animation.COLOR_ORANGE, MATERIAL_WHITE);
        puzzlebase = register(bpsingular, "puzzlebase_Cube.036", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE);
        puzzlebase_lining = register(bpsingular, "puzzlebase_lining_Cube.634", Animation.COLOR_BLUE, MATERIAL_WHITE);
        puzzlebase_innerwalls1_frame = register(bpsingular, "puzzlebase_innerwalls1_frame_Cube.024", Animation.COLOR_BLUE, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings1 = register(bpsingular, "puzzlebase_innerwalls1_linings1_Cube.028", Animation.COLOR_BLUE_LESS1, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings2 = register(bpsingular, "puzzlebase_innerwalls1_linings2_Cube.029", Animation.COLOR_BLUE_LESS2, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings3 = register(bpsingular, "puzzlebase_innerwalls1_linings3_Cube.030", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings4 = register(bpsingular, "puzzlebase_innerwalls1_linings4_Cube.031", Animation.COLOR_BLUE_LESS4, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings5 = register(bpsingular, "puzzlebase_innerwalls1_linings5_Cube.032", Animation.COLOR_BLUE_LESS5, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings6 = register(bpsingular, "puzzlebase_innerwalls1_linings6_Cube.033", Animation.COLOR_BLUE_LESS6, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings7 = register(bpsingular, "puzzlebase_innerwalls1_linings7_Cube.034", Animation.COLOR_BLUE_LESS7, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls1_linings8 = register(bpsingular, "puzzlebase_innerwalls1_linings8_Cube.035", Animation.COLOR_BLUE_LESS8, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_innerwalls2 = register(bpsingular, "puzzlebase_innerwalls2_Cube.025", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE_LESS_SPECULAR);
        puzzlebase_bottom = register(bpsingular, "puzzlebase_bottom_Cube.026", Animation.COLOR_BLUE, MATERIAL_WHITE_LESS_SPECULAR);
        mainbase1 = register(bpsingular, "mainbase1_Cube.037", Animation.COLOR_RED_LESS1, MATERIAL_WHITE);
        mainbase2 = register(bpsingular, "mainbase2_Cube.157", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE);
        phase1_base = register(bpinstanced, "phase1_base_Cube.027", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE);
        phase1_base_inner = register(bpinstanced, "phase1_base_inner_Cube.039", Animation.COLOR_PURPLE_LESS, MATERIAL_WHITE);
        phase1_base_inner_lining = register(bpinstanced, "phase1_base_inner_lining_Cube.040", Animation.COLOR_BLUE_LESS6, MATERIAL_WHITE);
        phase1_pillars = register(bpinstanced, "phase1_pillar.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_pillars_stripes = register(bpinstanced, "phase1_pillars_stripe.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase2 = register(bpinstanced, "phase2.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase2_stripes = register(bpinstanced, "phase2_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase2_caps = register(bpinstanced, "phase2_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3 = register(bpinstanced, "phase3.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase3_caps = register(bpinstanced, "phase3_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_blades = register(bpinstanced, "phase3_blade.", Animation.COLOR_OBSIDIAN_LESS1, MATERIAL_WHITE);
        phase3_baseframe1 = register(bpinstanced, "phase3_baseframe1.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_baseframe2 = register(bpinstanced, "phase3_baseframe2.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        phase3_baseframe3 = register(bpinstanced, "phase3_baseframe3.", Animation.COLOR_BLUE_LESS4, MATERIAL_WHITE);
        phase3_baseframe4 = register(bpinstanced, "phase3_baseframe4.", Animation.COLOR_BLUE_LESS7, MATERIAL_WHITE);
        phase4 = register(bpinstanced, "phase4.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase4_caps = register(bpinstanced, "phase4_cap.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        phase4_caps2 = register(bpinstanced, "phase4_cap2.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase5_layer1 = register(bpinstanced, "phase5_layer1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase5_layer2 = register(bpinstanced, "phase5_layer2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase5_layer3 = register(bpinstanced, "phase5_layer3.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase5_layer1_stripes = register(bpinstanced, "phase5_layer1_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase5_layer2_stripes = register(bpinstanced, "phase5_layer2_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase5_layer3_stripes = register(bpinstanced, "phase5_layer3_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase5_caps = register(bpinstanced, "phase5_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase5_trapezoidx1 = register(bpinstanced, "phase5_trapezoidx1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase5_trapezoidy1 = register(bpinstanced, "phase5_trapezoidy1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase5_trapezoidx2 = register(bpinstanced, "phase5_trapezoidx2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase5_trapezoidy2 = register(bpinstanced, "phase5_trapezoidy2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase6_layer1 = register(bpinstanced, "phase6_layer1.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase6_layer2 = register(bpinstanced, "phase6_layer2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase6_layer3 = register(bpinstanced, "phase6_layer3.", Animation.COLOR_BLUE_LESS1, MATERIAL_WHITE);
        phase6_layer4 = register(bpinstanced, "phase6_layer4.", Animation.COLOR_BLUE_LESS2, MATERIAL_WHITE);
        phase6_layer5 = register(bpinstanced, "phase6_layer5.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        phase6_layer6 = register(bpinstanced, "phase6_layer6.", Animation.COLOR_BLUE_LESS4, MATERIAL_WHITE);
        phase6_layer7 = register(bpinstanced, "phase6_layer7.", Animation.COLOR_BLUE_LESS5, MATERIAL_WHITE);
        phase6_layer8 = register(bpinstanced, "phase6_layer8.", Animation.COLOR_BLUE_LESS6, MATERIAL_WHITE);
        phase6_layer9 = register(bpinstanced, "phase6_layer9.", Animation.COLOR_BLUE_LESS7, MATERIAL_WHITE);
        phase6_layer10 = register(bpinstanced, "phase6_layer10.", Animation.COLOR_BLUE_LESS8, MATERIAL_WHITE);
        phase6_layer11 = register(bpinstanced, "phase6_layer11.", Animation.COLOR_BLUE, MATERIAL_WHITE);

        automator().run(DEBUG_MODE_AUTOMATOR);

        Game.initialize(this);
    }

    private FSMesh register(CustomBluePrint bp, String name, float[] color, FSLightMaterial material){
        bp.addCustoms(name, color, material);
        return automator().register(bp, name);
    }

    @Override
    public void update(int passindex, int programsetindex){
        bufferManager().updateIfNeeded();
    }

    @Override
    protected void destroyAssets(){

    }
}