package vanguard;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VLBufferFloat extends VLBufferDirect<Float, FloatBuffer>{

    public VLBufferFloat(int capacity){
        super(capacity);
    }

    public VLBufferFloat(){
        
    }


    @Override
    public VLBufferDirect initialize(ByteBuffer b){
        buffer = b.asFloatBuffer();
        position(0);

        return this;
    }

    @Override
    public void put(float data){
        buffer.put(data);
    }

    @Override
    public void put(VLV data){
        buffer.put(data.get());
    }

    @Override
    public void put(VLListType<VLV> data, int offset, int count){
        int limit = offset + count;

        for(int i = offset; i < limit; i++){
            buffer.put(data.get(i).get());
        }
    }

    @Override
    public void put(float[] data, int offset, int count){
        buffer.put(data, offset, count);
    }

    @Override
    public Float read(int index){
        return buffer.get(index);
    }

    @Override
    public void read(float[] data, int offset, int count){
        buffer.get(data, offset, count);
    }

    @Override
    public void remove(int offset, int size){
        FloatBuffer b = buffer;
        initialize(VLTools.makeDirectByteBuffer(buffer.capacity() - size));
        int cap = b.capacity();

        for(int i = 0; i < offset; i++){
            buffer.put(b.get(i));
        }
        for(int i = offset + size; i < cap; i++){
            buffer.put(b.get(i));
        }
    }

    @Override
    public void removeInterleaved(int offset, int unitsize, int size, int stride){
        FloatBuffer b = buffer;
        initialize(VLTools.makeDirectByteBuffer(buffer.capacity() - size));

        int max = offset + ((size / unitsize) * stride);
        int chunksize = stride - unitsize;

        for(int i = 0; i < offset; i++){
            buffer.put(b.get(i));
        }
        for(int i = offset + unitsize; i < max; i += stride){
            for(int i2 = 0; i2 < chunksize; i2++){
                buffer.put(b.get(i + i2));
            }
        }
        for(int i = max; i < b.capacity(); i++){
            buffer.put(b.get(i));
        }
    }

    @Override
    public void resize(int size){
        FloatBuffer b = buffer;
        initialize(size);
        b.position(0);

        if(b.hasArray()){
            if(b.capacity() <= buffer.capacity()){
                buffer.put(b.array());

            }else{
                buffer.put(b.array(), 0, buffer.capacity());
            }

        }else{
            float[] data;

            if(b.capacity() <= buffer.capacity()){
                data = new float[b.capacity()];

            }else{
                data = new float[buffer.capacity()];
            }

            b.get(data);
            buffer.put(data);
        }

        buffer.position(0);
    }

    @Override
    public int getTypeBytes(){
        return Float.SIZE / Byte.SIZE;
    }

    @Override
    public int sizeBytes(){
        return buffer.capacity() * getTypeBytes();
    }



    public static class DefinitionArray extends VLBufferDirect.DefinitionArray<VLArray<Float, float[]>>{

        public DefinitionArray(VLBufferDirect target, int bufferoffset){
            super(target, bufferoffset);
            this.bufferoffset = bufferoffset;
        }

        @Override
        protected void sync(VLArray<Float, float[]> source, VLBufferDirect target){
            target.position(bufferoffset);
            target.put(source.provider());
        }
    }

    public static class DefinitionArrayInterleaved extends VLBufferDirect.DefinitionArrayInterleaved<VLArray<Float, float[]>>{

        public DefinitionArrayInterleaved(VLBufferDirect target, int bufferoffset, int arrayoffset, int arraycount, int unitoffset,
                                          int unitsize, int unitsubcount, int stride){
            super(target, bufferoffset, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }

        @Override
        protected void sync(VLArray<Float, float[]> source, VLBufferDirect target){
            target.position(bufferoffset);
            target.putInterleaved(source.provider(), arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }
    }
}
