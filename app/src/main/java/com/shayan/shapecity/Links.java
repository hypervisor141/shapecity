package com.shayan.shapecity;

import com.nurverek.firestorm.FSBufferAddress;
import com.nurverek.firestorm.FSBufferManager;
import com.nurverek.firestorm.FSLink;
import com.nurverek.firestorm.FSP;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLBufferFloat;
import com.nurverek.vanguard.VLBufferManager;

public final class Links{

    private Links(){

    }

    public static final class TextureControlLink extends FSLink<VLArrayFloat, FSP.Uniform1fvd,
            FSBufferManager.EntryFloat, FSBufferManager, FSBufferAddress>{

        public TextureControlLink(VLArrayFloat array, FSP.Uniform1fvd config){
            super(array, config);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex){
            address = buffer.buffer(bufferindex, link);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex, int unitoffset, int unitsize){
            address = buffer.buffer(bufferindex, link, unitoffset, unitsize);
        }

        @Override
        public void buffer(FSBufferManager buffer, int bufferindex, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
            address = buffer.buffer(bufferindex, link, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }

        @Override
        public int size(){
            return link.size();
        }
    }
}
