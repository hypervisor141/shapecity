package com.shayan.shapecity;

import com.nurverek.firestorm.FSConfigLocated;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;

public class CustomConfigs{

    public static final class TexControlConfig extends FSConfigLocated{

        public TexControlConfig(){

        }

        @Override
        public void configure(FSP program, FSMesh mesh, int meshindex, int passindex){

        }

        @Override
        public int getGLSLSize(){
            return 0;
        }
    }
}
