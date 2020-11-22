package com.shayan.shapecity;

import com.nurverek.firestorm.FSBufferAddress;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSLinkBufferedType;
import com.nurverek.vanguard.VLArrayFloat;

public final class CustomLinks{

    private CustomLinks(){

    }

    public static final class TextureControlLink extends FSLinkBufferedType<VLArrayFloat, FSBufferManager, FSBufferAddress>{

        public TextureControlLink(VLArrayFloat array){
            super(array);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex){
            buffer.buffer(address, bufferindex, data);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex, int unitoffset, int unitsize){
            buffer.buffer(address, bufferindex, data, unitoffset, unitsize);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
            buffer.buffer(address, bufferindex, data, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }

        @Override
        public int size(){
            return data.size();
        }
    }
}
