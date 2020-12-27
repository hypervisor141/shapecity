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

public final class Loader extends FSG{

    public static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_FULL;
    public static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    public static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.72525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 1f, 1f, 1f }), new VLFloat(32));
    public static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_SILVER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.19225F, 0.19225F, 0.19225F }), new VLArrayFloat(new float[]{ 0.50754F, 0.50754F, 0.50754F}), new VLArrayFloat(new float[]{ 0.508273F, 0.508273F, 0.508273F }), new VLFloat(16));
    
    public static final int MAIN_PROGRAMSET = 0;
    public static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(1F));
    public static final FSGamma GAMMA = new FSGamma(new VLFloat(1F));

    public static int BUFFER_ELEMENT_SHORT_DEFAULT;
    public static int BUFFER_ARRAY_FLOAT_DEFAULT;
    public static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static final SecureRandom RANDOM = new SecureRandom();

    public static FSLightPoint light1;

    public static BPLayer bplayer;
    public static BPBase bpsingular;
    public static BPInstanced bpinstanced;

    public static FSMesh[] layers;
    public static FSMesh puzzle_layer1;
    public static FSMesh puzzle_layer2;
    public static FSMesh puzzle_layer3;
    public static FSMesh phase1_trapezoidx1;
    public static FSMesh phase1_trapezoidx2;
    public static FSMesh phase1_trapezoidy1;
    public static FSMesh phase1_trapezoidy2;
    public static FSMesh phase1_rect;
    public static FSMesh phase1_walls;
    public static FSMesh phase1_walls_stripes;
    public static FSMesh phase1_walls_caps;
    public static FSMesh phase1_pillars;
    public static FSMesh phase1_pillars_stripes;
    public static FSMesh phase2_pillars;
    public static FSMesh phase2_pillar_stripes;
    public static FSMesh phase3_rect_layer1;
    public static FSMesh phase3_rect_layer2;
    public static FSMesh phase3_rect_layer3;
    public static FSMesh phase3_rect_layer4;
    public static FSMesh phase3_rect_layer1_stripes;
    public static FSMesh phase3_rect_layer2_stripes;
    public static FSMesh phase3_rect_layer3_stripes;
    public static FSMesh phase3_rect_layer4_stripes;
    public static FSMesh phase3_trapezoidx1;
    public static FSMesh phase3_trapezoidx2;
    public static FSMesh phase3_trapezoidy1;
    public static FSMesh phase3_trapezoidy2;
    public static FSMesh phase3_outrect_part1;
    public static FSMesh phase3_outrect_part2;
    public static FSMesh phase3_outrect_part3;
    public static FSMesh outbase_powerplants;
    public static FSMesh outbase_powerplant_base;
    public static FSMesh outbase_powerplant_baseplates;
    public static FSMesh outbase_walls;
    public static FSMesh outbase_walls_stripes;
    public static FSMesh puzzle_base;
    public static FSMesh mainbase;

    public Loader(){
        super(2, 50, 10);
    }

    @Override
    public void assemble(FSActivity act){
        FSBufferManager manager = bufferManager();
        BUFFER_ELEMENT_SHORT_DEFAULT = manager.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        light1 = new FSLightPoint(new FSAttenuation.Radius(new VLFloat(10000F)), new VLArrayFloat(new float[]{ 0F, 10F, -40F, 1.0F }));

        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        bplayer = new BPLayer(this);
        bpsingular = new BPBase(this);
        bpinstanced = new BPInstanced(this, 60);

        puzzle_layer1 = register(bplayer, "pieces1.", Animations.COLOR_LAYER, MATERIAL_WHITE);
        puzzle_layer2 = register(bplayer, "pieces2.", Animations.COLOR_LAYER, MATERIAL_WHITE);
        puzzle_layer3 = register(bplayer, "pieces3.", Animations.COLOR_LAYER, MATERIAL_WHITE);
        phase1_trapezoidx1 = register(bpinstanced, "phase1_trapezoid_x1.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_trapezoidx2 = register(bpinstanced, "phase1_trapezoid_x2.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_trapezoidy1 = register(bpinstanced, "phase1_trapezoid_y1.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_trapezoidy2 = register(bpinstanced, "phase1_trapezoid_y2.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_walls = register(bpinstanced, "phase1_walls.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_walls_stripes = register(bpinstanced, "phase1_walls_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase1_walls_caps = register(bpinstanced, "phase1_walls_cap.", Animations.COLOR_RED_LESS2, MATERIAL_WHITE);
        phase1_pillars = register(bpinstanced, "phase1_pillar.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase1_pillars_stripes = register(bpinstanced, "phase1_pillars_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase1_rect = register(bpinstanced, "phase1_rect.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase2_pillars = register(bpinstanced, "phase2_pillar.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase2_pillar_stripes = register(bpinstanced, "phase2_pillars_stripe.", Animations.COLOR_GOLD, MATERIAL_WHITE);
        phase3_rect_layer1 = register(bpinstanced, "phase3_rect_layer1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_rect_layer2 = register(bpinstanced, "phase3_rect_layer2.", Animations.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_rect_layer3 = register(bpinstanced, "phase3_rect_layer3.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        phase3_rect_layer4 = register(bpinstanced, "phase3_rect_layer4.", Animations.COLOR_OBSIDIAN_LESS5, MATERIAL_WHITE);
        phase3_rect_layer1_stripes = register(bpinstanced, "phase3_rect_layer1_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase3_rect_layer2_stripes = register(bpinstanced, "phase3_rect_layer2_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase3_rect_layer3_stripes = register(bpinstanced, "phase3_rect_layer3_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase3_rect_layer4_stripes = register(bpinstanced, "phase3_rect_layer4_stripe.", Animations.COLOR_RED, MATERIAL_WHITE);
        phase3_trapezoidx1 = register(bpinstanced, "phase3_trapezoidx1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_trapezoidx2 = register(bpinstanced, "phase3_trapezoidx2.", Animations.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_trapezoidy1 = register(bpinstanced, "phase3_trapezoidy1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);
        phase3_trapezoidy2 = register(bpinstanced, "phase3_trapezoidy2.", Animations.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_outrect_part1 = register(bpinstanced, "phase3_outrect_part1.", Animations.COLOR_OBSIDIAN_LESS5, MATERIAL_WHITE);
        phase3_outrect_part2 = register(bpinstanced, "phase3_outrect_part2.", Animations.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE);
        phase3_outrect_part3 = register(bpinstanced, "phase3_outrect_part3.", Animations.COLOR_RED_LESS2, MATERIAL_WHITE);
        outbase_walls = register(bpsingular, "outbase_walls_Cube.480", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        outbase_walls_stripes = register(bpsingular, "outbase_walls_stripes_Cube.449", Animations.COLOR_GOLD, MATERIAL_WHITE);
        outbase_powerplants = register(bpinstanced, "outbase_powerplant.", Animations.COLOR_OBSIDIAN_LESS4, MATERIAL_WHITE);
        outbase_powerplant_base = register(bpsingular, "powerplants_base_Cube.308", Animations.COLOR_OBSIDIAN_LESS5, MATERIAL_WHITE);
        outbase_powerplant_baseplates = register(bpsingular, "powerplants_baseplates_Cube.309", Animations.COLOR_GOLD, MATERIAL_WHITE);
        puzzle_base = register(bpsingular, "puzzlebase_Cube.036", Animations.COLOR_OBSIDIAN_LESS1, MATERIAL_WHITE);
        mainbase = register(bpsingular, "base_Cube.157", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE);

        automator().run(DEBUG_MODE_AUTOMATOR);

        layers = new FSMesh[]{
                puzzle_layer1, puzzle_layer2, puzzle_layer3
        };

        Game.startGame(this);
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