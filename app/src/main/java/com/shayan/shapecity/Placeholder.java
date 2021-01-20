package com.shayan.shapecity;

import android.content.Context;

import java.nio.ByteOrder;

public class Placeholder{

    public static void build(Context cxt, Gen gen){
        try{
            gen.automator.add(cxt.getAssets().open("placeholder1.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        gen.register(gen.bpsingular, "structure_Cube.001", Animation.COLOR_WHITE, Material.MATERIAL_WHITE_MORE_SPECULAR);
        gen.register(gen.bpsingular, "location_Cube.002", Animation.COLOR_WHITE, Material.MATERIAL_WHITE_MORE_SPECULAR);

        gen.automator.run(Gen.DEBUG_MODE_AUTOMATOR);
    }
}