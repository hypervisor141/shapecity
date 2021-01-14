package com.shayan.shapecity;

import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLMath;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public class Animation{

    public static final float[] COLOR_WHITE = new float[]{ 1F, 1F, 1F, 1F };
    public static final float[] COLOR_WHITE_LESS = new float[]{ 0.8F, 0.8F, 0.8F, 1F };
    public static final float[] COLOR_WHITE_LESS2 = new float[]{ 0.6F, 0.6F, 0.6F, 1F };
    public static final float[] COLOR_WHITE_LESS3 = new float[]{ 0.5F, 0.5F, 0.5F, 1F };
    public static final float[] COLOR_ORANGE = new float[]{ 1.0F, 0.9F, 0F, 1F };
    public static final float[] COLOR_OBSIDIAN = new float[]{ 0.4F, 0.4F, 0.4F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS1 = new float[]{ 0.25F, 0.25F, 0.25F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS2 = new float[]{ 0.2F, 0.2F, 0.2F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS3 = new float[]{ 0.15F, 0.15F, 0.15F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS4 = new float[]{ 0.1F, 0.1F, 0.1F, 1F };
    public static final float[] COLOR_OBSIDIAN_LESS5 = new float[]{ 0.05F, 0.05F, 0.05F, 1F };
    public static final float[] COLOR_GOLD = new float[]{ 0.83F, 0.68F, 0.21F, 1F };
    public static final float[] COLOR_LIGHT_GOLD = new float[]{ 0.98F, 0.76F, 0.01F, 1F };
    public static final float[] COLOR_BLUE = new float[]{ 0F, 0.872F, 1.0F, 1F };
    public static final float[] COLOR_BLUE_LESS1 = new float[]{ 0F, 0.802F, 0.92F, 1F };
    public static final float[] COLOR_BLUE_LESS2 = new float[]{ 0F, 0.727F, 0.83F, 1F };
    public static final float[] COLOR_BLUE_LESS3 = new float[]{ 0F, 0.68F, 0.78F, 1F };
    public static final float[] COLOR_BLUE_LESS4 = new float[]{ 0F, 0.523F, 0.6F, 1F };
    public static final float[] COLOR_BLUE_LESS5 = new float[]{ 0F, 0.459F, 0.527F, 1F };
    public static final float[] COLOR_BLUE_LESS6 = new float[]{ 0F, 0.401F, 0.460F, 1F };
    public static final float[] COLOR_BLUE_LESS7 = new float[]{ 0F, 0.355F, 0.407F, 1F };
    public static final float[] COLOR_BLUE_LESS8 = new float[]{ 0F, 0.285F, 0.327F, 1F };

    public static final float[] COLOR_RED = new float[]{ 1.0F, 0.3F, 0F, 1F };
    public static final float[] COLOR_RED_LESS1 = new float[]{ 0.8F, 0.2F, 0F, 1F };
    public static final float[] COLOR_RED_LESS2 = new float[]{ 0.7F, 0.1F, 0F, 1F };
    public static final float[] COLOR_RED_LESS3 = new float[]{ 0.5F, 0F, 0F, 1F };
    public static final float[] COLOR_PURPLE_LESS = new float[]{ 0F, 0.087F, 0.140F, 1F };
    public static final float[] COLOR_PURPLE = new float[]{ 0F, 0.137F, 0.22F, 1F };
    public static final float[] COLOR_PURPLE_MORE = new float[]{ 0F, 0.237F, 0.380F, 1F };

    protected static void lower(VLVRunner runner, int mincycles, int maxcycles, float decrease, int mindelay, int maxdelay, VLVCurved.Curve curve, FSMesh[] group){
        int size = group.length;
        int cyclediff = maxcycles - mincycles;
        int delaydiff = maxdelay - mindelay;

        for(int i = 0; i < size; i++){
            FSMesh mesh = group[i];

            for(int i2 = 0; i2 < mesh.size(); i2++){
                FSInstance instance = mesh.instance(i2);
                FSMatrixModel model = instance.modelMatrix();

                float height = instance.schematics().modelHeight();
                float y = model.getY(0).get();
                float randomization = VLMath.range(Gen.RANDOM.nextFloat(),height * 0.25F, height);

                VLVCurved var = new VLVCurved(y - decrease, y - randomization, cyclediff == 0 ? maxcycles : mincycles + Gen.RANDOM.nextInt(cyclediff), VLVariable.LOOP_NONE, curve);
                var.SYNCER.add(new VLVMatrix.Definition(model));

                model.setY(0, var);

                runner.add(new VLVRunnerEntry(var, delaydiff == 0 ? maxdelay : mindelay + Gen.RANDOM.nextInt(delaydiff)));
            }
        }

        runner.findEndPointIndex();
        runner.targetSync();
    }
}
