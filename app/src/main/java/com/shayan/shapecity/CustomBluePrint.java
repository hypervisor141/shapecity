package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSGBluePrint;
import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListType;

import java.util.Arrays;
import java.util.HashMap;

public abstract class CustomBluePrint extends FSGBluePrint{

    private HashMap<String, Entry> entries;

    protected CustomBluePrint(){
        entries = new HashMap<>(100);
    }

    @Override
    protected void preAssemblyAdjustment(FSMesh mesh, FSInstance instance){
        Entry e = entries.get(mesh.name());

        instance.data().colors(new VLArrayFloat(e.color.clone()));
        instance.lightMaterial(e.material);
    }

    public void addCustoms(String name, float[] color, FSLightMaterial material){
        entries.put(name.toLowerCase(), new Entry(color, material));
    }

    public void clear(){
        entries.clear();
    }

    private static final class Entry{

        protected float[] color;
        protected FSLightMaterial material;
        protected int index;

        protected Entry(float[] color, FSLightMaterial material){
            this.color = color;
            this.material = material;

            index = 0;
        }

        protected void incrementIndex(){
            index++;
        }
    }
}
