package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSActivity;
import com.nurverek.firestorm.FSAttenuation;
import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSBufferLayout;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightDirect;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSLinkType;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSPMod;
import com.nurverek.firestorm.FSShadowDirect;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.firestorm.FSVertexBuffer;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferShort;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListType;

import java.nio.ByteOrder;
import java.util.Arrays;

public final class Loader extends FSG{

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

    public static final int DEBUG_MODE_AUTOMATOR = FSControl.DEBUG_DISABLED;
    public static final int DEBUG_MODE_PROGRAMS = FSControl.DEBUG_DISABLED;

    public static final FSLightMaterial MATERIAL_GOLD = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.24725f, 0.1995f, 0.0745f }), new VLArrayFloat(new float[]{ 0.75164f, 0.60648f, 0.22648f }), new VLArrayFloat(new float[]{ 0.628281f, 0.555802f, 0.366065f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_OBSIDIAN = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05375f, 0.05f, 0.06625f }), new VLArrayFloat(new float[]{ 0.18275f, 0.17f, 0.22525f }), new VLArrayFloat(new float[]{ 0.332741f, 0.328634f, 0.346435f }), new VLFloat(16));
    public static final FSLightMaterial MATERIAL_WHITE_RUBBER = new FSLightMaterial(new VLArrayFloat(new float[]{ 0.05f, 0.05f, 0.05f }), new VLArrayFloat(new float[]{ 0.5f, 0.5f, 0.5f }), new VLArrayFloat(new float[]{ 0.7f, 0.7f, 0.7f }), new VLFloat(16));

    public static final int MAIN_PROGRAMSET = 0;
    public static final FSBrightness BRIGHTNESS = new FSBrightness(new VLFloat(2f));
    public static final FSGamma GAMMA = new FSGamma(new VLFloat(1.5f));

    public static int BUFFER_ELEMENT_SHORT_DEFAULT;
    public static int BUFFER_ARRAY_FLOAT_DEFAULT;
    public static int UBOBINDPOINT = 0;
    public static int TEXUNIT = 1;

    public static FSLightDirect lightDirect;
    public static FSLightPoint lightPoint;

//    public static FSShadowDirect shadowDirect;
//    public static FSShadowPoint shadowPoint;

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

        lightPoint = new FSLightPoint(new FSAttenuation(new VLFloat(1.0F), new VLFloat(0.007F), new VLFloat(0.0002F)), new VLArrayFloat(new float[]{ 0F, 15F, -15F, 1.0F }));
        lightDirect = new FSLightDirect(new VLArrayFloat(new float[]{ 0F, 400F, 600F, 1.0F }), new VLArrayFloat(new float[]{ 0F, 0F, 0F, 1.0F }));

//        shadowPoint = new FSShadowPoint(lightPoint, new VLInt(1024), new VLInt(1024), new VLFloat(0.005F), new VLFloat(0.005F), new VLFloat(1.1F), new VLFloat(1F), new VLFloat(50));
//        shadowDirect = new FSShadowDirect(lightDirect, new VLInt(2048), new VLInt(2048), new VLFloat(0.0001F), new VLFloat(0.01F), new VLFloat(1.2F));
//
//        shadowPoint.initialize(new VLInt(TEXUNIT++));
//        shadowDirect.initialize(new VLInt(TEXUNIT++));

        BUFFER_ELEMENT_SHORT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryShort(new FSVertexBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferShort()));
        BUFFER_ARRAY_FLOAT_DEFAULT = BUFFERMANAGER.add(new FSBufferManager.EntryFloat(new FSVertexBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_STATIC_DRAW), new VLBufferFloat()));

        Game.initialize();

        Layers.register(this);

        AUTOMATOR.build(DEBUG_MODE_AUTOMATOR);

        Layers.makeLinks();

        AUTOMATOR.buffer(DEBUG_MODE_AUTOMATOR);

        Layers.program(this);

        AUTOMATOR.program(DEBUG_MODE_AUTOMATOR);

        Game.startGame(this);
    }

    @Override
    public void update(int passindex, int programsetindex){
        BUFFERMANAGER.updateIfNeeded();
    }

    @Override
    protected void destroyAssets(){
        Game.destroy();
        Animations.destroy();
    }
}