package com.shayan.shapecity;

import com.nurverek.firestorm.FSGBluePrint;

public abstract class CustomBluePrint extends FSGBluePrint{

    private float[] colors;

    public void customColors(float[] colors){
        this.colors = colors;
    }

    public float[] customColors(){
        return colors;
    }
}
