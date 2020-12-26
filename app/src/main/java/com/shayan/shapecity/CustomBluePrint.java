package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSGBluePrint;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;

public abstract class CustomBluePrint extends FSGBluePrint{

    private VLListType<float[]> colors;
    private VLListType<FSLightMaterial> materials;

    private int tracker;

    protected CustomBluePrint(int capacity){
        tracker = 0;

        colors = new VLListType<>(capacity, capacity);
        materials = new VLListType<>(capacity, capacity);
    }

    @Override
    protected void preAssemblyAdjustment(FSMesh mesh, FSInstance instance){
        instance.data().colors(new VLArrayFloat(colors.get(tracker).clone()));
        instance.lightMaterial(materials.get(tracker));

        tracker++;
    }

    public void addColor(float[] c, int count){
        for(int i = 0; i < count; i++){
            colors.add(c);
        }
    }

    public void addMaterial(FSLightMaterial m, int count){
        for(int i = 0; i < count; i++){
            materials.add(m);
        }
    }
}
