package com.shayan.shapecity;

import com.nurverek.firestorm.FSInstance;
import com.nurverek.firestorm.FSMatrixModel;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLVCurved;
import com.nurverek.vanguard.VLVMatrix;
import com.nurverek.vanguard.VLVRunner;
import com.nurverek.vanguard.VLVRunnerEntry;
import com.nurverek.vanguard.VLVariable;

public class Animation{

    protected static void lower(VLVRunner runner, int cycles, float decrease, FSMesh[] group){
        int size = group.length;

        Gen.RANDOM.setSeed(System.currentTimeMillis());

        for(int i = 0; i < size; i++){
            FSMesh mesh = group[i];

            for(int i2 = 0; i2 < mesh.size(); i2++){
                FSInstance instance = mesh.instance(i2);
                FSMatrixModel model = instance.modelMatrix();

                float y = model.getY(0).get();
                VLVCurved var = new VLVCurved(y - decrease, y, 100 + Gen.RANDOM.nextInt(200), VLVariable.LOOP_NONE, VLVCurved.CURVE_DEC_SINE_SQRT);
                var.SYNCER.add(new VLVMatrix.Definition(model));

                model.setY(0, var);

                runner.add(new VLVRunnerEntry(var, 200 + Gen.RANDOM.nextInt(200)));
            }
        }

        runner.targetSync();
    }
}
