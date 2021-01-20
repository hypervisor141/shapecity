package com.shayan.shapecity;

import android.content.Context;

import java.nio.ByteOrder;

public class Base{

    public static void build(Context cxt, Gen gen){
        try{
            gen.automator.add(cxt.getAssets().open("base.fsm"), ByteOrder.LITTLE_ENDIAN, true, 300);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        gen.register(gen.bpsingular, "foundation_Cube.001", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE_MORE_SPECULAR);
        gen.register(gen.bpsingular, "surface_Cube.002", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE_MORE_SPECULAR);
        gen.register(gen.bpsingular, "center_Cube.255", Animation.COLOR_OBSIDIAN_LESS4, Material.MATERIAL_WHITE_MORE_SPECULAR);
        gen.register(gen.bpinstanced, "placeholder.", Animation.COLOR_BLUE, Material.MATERIAL_WHITE_MORE_SPECULAR);

        gen.automator.run(Gen.DEBUG_MODE_AUTOMATOR);
    }
}
