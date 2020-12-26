package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightDirect;
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

    public static FSLightDirect lightdirect;
    public static FSLightPoint lightpoint;

    public static BPLayer bplayer;
    public static BPBase bpsingular;
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
    public static FSMesh phase2_pillars;
    public static FSMesh phase2_pillar_stripes;
    public static FSMesh phase3_rect_layer1;
    public static FSMesh phase3_rect_layer2;
    public static FSMesh phase3_rect_layer3;
    public static FSMesh phase3_rect_layer4;
    public static FSMesh phase3_trapezoidx1;
    public static FSMesh phase3_trapezoidx2;
    public static FSMesh phase3_trapezoidy1;
    public static FSMesh phase3_trapezoidy2;
    public static FSMesh phase3_outrect;
    public static FSMesh phase3_outrect_base_layer1;
    public static FSMesh phase3_outrect_base_layer2;
    public static FSMesh phase3_outrect_base_layer3;
    public static FSMesh phase3_outrect_inneredges;
    public static FSMesh phase3_outrect_outeredges;
    public static FSMesh phase3_outrect_outeredges_tops;
    public static FSMesh outbase_powerplants;
    public static FSMesh outbase_powerplant_base;
    public static FSMesh outbase_powerplant_baseplates;
    public static FSMesh outbase_walls;
    public static FSMesh outbase_walls_stripes;
    public static FSMesh puzzlebase;
    public static FSMesh mainbase;

    public Loader(){
        super(2, 50, 10);
    }

    @Override
    public void assemble(FSActivity act){
        FSBufferManager manager = bufferManager();
        BUFFER_ELEMENT_SHORT_DEFAULT = manager.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = manager.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        lightpoint = new FSLightPoint(new FSAttenuation.Radius(new VLFloat(10000F)), new VLArrayFloat(new float[]{ 0F, 10F, -40F, 1.0F }));
//        light2 = new FSLightDirect(new VLArrayFloat(new float[]{ 0F, 4000F, 6000F, 1.0F }), new VLArrayFloat(new float[]{ 0F, 0F, 0F, 1.0F }));

        try{
            constructAutomator(act.getAssets().open("meshes.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        bplayer = new BPLayer(this, 10);
        bpsingular = new BPBase(this, 10);
        bpinstanced = new BPInstanced(this, 60, 10);

        layer1 = register(bplayer, "pieces1.", Animations.COLOR_LAYER, MATERIAL_WHITE, BPLayer.INSTANCE_COUNT);
        layer2 = register(bplayer, "pieces2.", Animations.COLOR_LAYER, MATERIAL_WHITE, BPLayer.INSTANCE_COUNT);
        layer3 = register(bplayer, "pieces3.", Animations.COLOR_LAYER, MATERIAL_WHITE, BPLayer.INSTANCE_COUNT);
        phase1_trapezoidx1 = register(bpinstanced, "phase1_trapezoid_x1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 4);
        phase1_trapezoidx2 = register(bpinstanced, "phase1_trapezoid_x2.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 4);
        phase1_trapezoidy1 = register(bpinstanced, "phase1_trapezoid_y1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 4);
        phase1_trapezoidy2 = register(bpinstanced, "phase1_trapezoid_y2.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 4);
        phase1_rect = register(bpinstanced, "phase1_rect.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 4);
        phase1_walls = register(bpinstanced, "phase1_walls.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 60);
        phase1_base = register(bpsingular, "phase1_base_Cube.157", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 1);
        phase2_pillars = register(bpinstanced, "phase2_pillar.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 48);
        phase2_pillar_stripes = register(bpsingular, "phase2_pillars_stripes_Cube.573", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        phase3_rect_layer1 = register(bpinstanced, "phase3_rect_layer1.", Animations.COLOR_LIGHT_GOLD, MATERIAL_WHITE, 20);
        phase3_rect_layer2 = register(bpinstanced, "phase3_rect_layer2.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_rect_layer3 = register(bpinstanced, "phase3_rect_layer3.", Animations.COLOR_LIGHT_GOLD, MATERIAL_WHITE, 20);
        phase3_rect_layer4 = register(bpinstanced, "phase3_rect_layer4.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_trapezoidx1 = register(bpinstanced, "phase3_trapezoidx1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_trapezoidx2 = register(bpinstanced, "phase3_trapezoidx2.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_trapezoidy1 = register(bpinstanced, "phase3_trapezoidy1.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_trapezoidy2 = register(bpinstanced, "phase3_trapezoidy2.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        phase3_outrect_base_layer1 = register(bpsingular, "phase3_outrect_base_layer1_Cube.073", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        phase3_outrect_base_layer2 = register(bpsingular, "phase3_outrect_base_layer2_Cube.074", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 1);
        phase3_outrect_base_layer3 = register(bpsingular, "phase3_outrect_base_layer3_Cube.075", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        phase3_outrect_inneredges = register(bpsingular, "phase3_outrect_inneredges_Cube.525", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        phase3_outrect = register(bpinstanced, "phase3_outrect.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 28);
        phase3_outrect_outeredges = register(bpsingular, "phase3_outrect_outeredges_Cube.307", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        phase3_outrect_outeredges_tops = register(bpsingular, "phase3_outrect_outeredges_tops_Cube.348", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 1);
        outbase_powerplants = register(bpinstanced, "outbase_powerplant.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 28);
        outbase_powerplant_base = register(bpsingular, "powerplants_base_Cube.308", Animations.COLOR_OBSIDIAN_LESS3, MATERIAL_WHITE, 1);
        outbase_powerplant_baseplates = register(bpsingular, "powerplants_baseplates_Cube.309", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        outbase_walls = register(bpinstanced, "outbase_walls.", Animations.COLOR_OBSIDIAN_LESS2, MATERIAL_WHITE, 20);
        outbase_walls_stripes = register(bpsingular, "outbase_walls_stripes_Cube.449", Animations.COLOR_GOLD, MATERIAL_WHITE, 1);
        puzzlebase = register(bpsingular, "puzzlebase_Cube.072", Animations.COLOR_OBSIDIAN, MATERIAL_WHITE, 1);
        mainbase = register(bpsingular, "base_Cube.036", Animations.COLOR_OBSIDIAN, MATERIAL_WHITE, 1);

        automator().run(DEBUG_MODE_AUTOMATOR);

        layers = new FSMesh[]{
                layer1,
                layer2,
                layer3
        };

        Game.startGame(this);
    }

    private FSMesh register(CustomBluePrint bp, String name, float[] colors, FSLightMaterial material, int count){
        bp.addColor(colors, count);
        bp.addMaterial(material, count);

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