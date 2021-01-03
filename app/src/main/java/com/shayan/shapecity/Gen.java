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
    public FSMesh phase1_trapezoidx1;
    public FSMesh phase1_trapezoidy1;
    public FSMesh phase1_trapezoidx2;
    public FSMesh phase1_trapezoidy2;
    public FSMesh phase1_rects;
    public FSMesh phase1_walls;
    public FSMesh phase1_walls_stripes;
    public FSMesh phase1_walls_caps;
    public FSMesh phase1_pillars;
    public FSMesh phase1_pillars_caps;
    public FSMesh phase1_pillars_blades;
    public FSMesh phase1_pillars_baseframe1;
    public FSMesh phase1_pillars_baseframe2;
    public FSMesh phase1_pillars_baseframe3;
    public FSMesh phase1_pillars_baseframe4;
    public FSMesh phase2_pillars;
    public FSMesh phase2_pillars_caps;
    public FSMesh phase2_pillars_caps2;
    public FSMesh phase3_rect_layer1;
    public FSMesh phase3_rect_layer2;
    public FSMesh phase3_rect_layer3;
    public FSMesh phase3_rect_layer1_stripes;
    public FSMesh phase3_rect_layer2_stripes;
    public FSMesh phase3_rect_layer3_stripes;
    public FSMesh phase3_rect_caps;
    public FSMesh phase3_trapezoidx1;
    public FSMesh phase3_trapezoidy1;
    public FSMesh phase3_trapezoidx2;
    public FSMesh phase3_trapezoidy2;
    public FSMesh phase3_outrect_layer1;
    public FSMesh phase3_outrect_layer2;
    public FSMesh phase3_outrect_layer3;
    public FSMesh phase3_outrect_layer4;
    public FSMesh phase3_outrect_layer5;
    public FSMesh phase3_outrect_layer6;
    public FSMesh phase3_outrect_layer7;
    public FSMesh phase3_outrect_layer8;
    public FSMesh phase3_outrect_layer9;
    public FSMesh phase3_outrect_layer10;
    public FSMesh phase3_outrect_layer11;
    public FSMesh outbase_powerplants;
    public FSMesh outbase_powerplant_caps;
    public FSMesh outbase_powerplant_caps2;
    public FSMesh outbase_powerplant_baseplates;
    public FSMesh outbase_walls_layer1;
    public FSMesh outbase_walls_layer2;
    public FSMesh outbase_walls_stripes;
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
        mainbase1 = register(bpsingular, "mainbase_Cube.157", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE);
        mainbase2 = register(bpsingular, "mainbase2_Cube.612", Animation.COLOR_RED_LESS1, MATERIAL_WHITE);
        phase1_trapezoidx1 = register(bpinstanced, "phase1_trapezoid_x1.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_trapezoidy1 = register(bpinstanced, "phase1_trapezoid_y1.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_trapezoidx2 = register(bpinstanced, "phase1_trapezoid_x2.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_trapezoidy2 = register(bpinstanced, "phase1_trapezoid_y2.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_rects = register(bpinstanced, "phase1_rect.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_walls = register(bpinstanced, "phase1_walls.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_walls_stripes = register(bpinstanced, "phase1_walls_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_walls_caps = register(bpinstanced, "phase1_walls_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_pillars = register(bpinstanced, "phase1_pillar.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_pillars_caps = register(bpinstanced, "phase1_pillars_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_pillars_blades = register(bpsingular, "phase1_pillars_blades_Cube.475", Animation.COLOR_WHITE_LESS3, MATERIAL_WHITE);
        phase1_pillars_baseframe1 = register(bpinstanced, "phase1_pillars_baseframe1.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase1_pillars_baseframe2 = register(bpinstanced, "phase1_pillars_baseframe2.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        phase1_pillars_baseframe3 = register(bpinstanced, "phase1_pillars_baseframe3.", Animation.COLOR_BLUE_LESS4, MATERIAL_WHITE);
        phase1_pillars_baseframe4 = register(bpinstanced, "phase1_pillars_baseframe4.", Animation.COLOR_BLUE_LESS7, MATERIAL_WHITE);
        phase2_pillars = register(bpinstanced, "phase2_pillar.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase2_pillars_caps = register(bpinstanced, "phase2_pillars_cap.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        phase2_pillars_caps2 = register(bpinstanced, "phase2_pillars_cap2.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_rect_layer1 = register(bpinstanced, "phase3_rect_layer1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_rect_layer2 = register(bpinstanced, "phase3_rect_layer2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_rect_layer3 = register(bpinstanced, "phase3_rect_layer3.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase3_rect_layer1_stripes = register(bpinstanced, "phase3_rect_layer1_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_rect_layer2_stripes = register(bpinstanced, "phase3_rect_layer2_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_rect_layer3_stripes = register(bpinstanced, "phase3_rect_layer3_stripe.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_rect_caps = register(bpinstanced, "phase3_rect_cap.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        phase3_trapezoidx1 = register(bpinstanced, "phase3_trapezoidx1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_trapezoidy1 = register(bpinstanced, "phase3_trapezoidy1.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_trapezoidx2 = register(bpinstanced, "phase3_trapezoidx2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_trapezoidy2 = register(bpinstanced, "phase3_trapezoidy2.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_outrect_layer1 = register(bpinstanced, "phase3_outrect_layer1.", Animation.COLOR_OBSIDIAN_LESS1, MATERIAL_WHITE);
        phase3_outrect_layer2 = register(bpinstanced, "phase3_outrect_layer2.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_outrect_layer3 = register(bpinstanced, "phase3_outrect_layer3.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_outrect_layer4 = register(bpinstanced, "phase3_outrect_layer4.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase3_outrect_layer5 = register(bpinstanced, "phase3_outrect_layer5.", Animation.COLOR_OBSIDIAN_LESS5, MATERIAL_WHITE);
        phase3_outrect_layer6 = register(bpinstanced, "phase3_outrect_layer6.", Animation.COLOR_OBSIDIAN_LESS1, MATERIAL_WHITE);
        phase3_outrect_layer7 = register(bpinstanced, "phase3_outrect_layer7.", Animation.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_outrect_layer8 = register(bpinstanced, "phase3_outrect_layer8.", Animation.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_outrect_layer9 = register(bpinstanced, "phase3_outrect_layer9.", Animation.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase3_outrect_layer10 = register(bpinstanced, "phase3_outrect_layer10.", Animation.COLOR_OBSIDIAN_LESS5, MATERIAL_WHITE);
        phase3_outrect_layer11 = register(bpinstanced, "phase3_outrect_layer11.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        outbase_powerplants = register(bpinstanced, "outbase_powerplant.", Animation.COLOR_BLUE_LESS3, MATERIAL_WHITE);
        outbase_powerplant_caps = register(bpinstanced, "outbase_powerplant_caps.", Animation.COLOR_BLUE, MATERIAL_WHITE);
        outbase_powerplant_caps2 = register(bpinstanced, "outbase_powerplant_caps2.", Animation.COLOR_ORANGE, MATERIAL_WHITE);
        outbase_powerplant_baseplates = register(bpsingular, "powerplants_baseplates_Cube.309", Animation.COLOR_ORANGE, MATERIAL_WHITE);
        outbase_walls_layer1 = register(bpsingular, "outbase_walls_layer1_Cube.480", Animation.COLOR_PURPLE_MORE, MATERIAL_WHITE);
        outbase_walls_layer2 = register(bpsingular, "outbase_walls_layer2_Cube.635", Animation.COLOR_BLUE_LESS4, MATERIAL_WHITE);
        outbase_walls_stripes = register(bpsingular, "outbase_walls_stripes_Cube.449", Animation.COLOR_WHITE_LESS, MATERIAL_WHITE);

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