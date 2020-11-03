package vanguard;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class VLBufferShort extends VLBufferDirect<Short, ShortBuffer>{

    public VLBufferShort(int capacity){
        super(capacity);
    }

    public VLBufferShort(){

    }


    @Override
    public VLBufferDirect initialize(ByteBuffer b){
        buffer = b.asShortBuffer();
        position(0);

        return this;
    }

    @Override
    public void put(short data){
        buffer.put(data);
    }

    @Override
    public void put(VLV data){
        buffer.put(data.getShort());
    }

    @Override
    public void put(VLListType<VLV> data, int offset, int count){
        int limit = offset + count;

        for(int i = offset; i < limit; i++){
            buffer.put(data.get(i).getShort());
        }
    }

    @Override
    public void put(short[] data, int offset, int count){
        buffer.put(data, offset, count);
    }

    @Override
    public Short read(int index){
        return buffer.get(index);
    }

    @Override
    public void read(short[] data, int offset, int count){
        buffer.get(data, offset, count);
    }

    @Override
    public void remove(int offset, int size){
        ShortBuffer b = buffer;
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
    public void removeInterleaved(int offset, int unitsize, int stride, int size){
        ShortBuffer b = buffer;
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
        ShortBuffer b = buffer;
        initialize(size);
        b.position(0);

        if(b.hasArray()){
            if(b.capacity() <= buffer.capacity()){
                buffer.put(b.array());

            }else{
                buffer.put(b.array(), 0, buffer.capacity());
            }

        }else{
            short[] data;

            if(b.capacity() <= buffer.capacity()){
                data = new short[b.capacity()];

            }else{
                data = new short[buffer.capacity()];
            }

            b.get(data);
            buffer.put(data);
        }

        buffer.position(0);
    }

    @Override
    public int getTypeBytes(){
        return Short.SIZE / Byte.SIZE;
    }

    @Override
    public int sizeBytes(){
        return buffer.capacity() * getTypeBytes();
    }


    public static class DefinitionArray extends VLBufferDirect.DefinitionArray<VLArray<Short, short[]>>{

        public DefinitionArray(VLBufferDirect target, int bufferoffset){
            super(target, bufferoffset);
            this.bufferoffset = bufferoffset;
        }

        @Override
        protected void sync(VLArray<Short, short[]> source, VLBufferDirect target){
            target.position(bufferoffset);
            target.put(source.provider());
        }
    }

    public static class DefinitionArrayInterleaved extends VLBufferDirect.DefinitionArrayInterleaved<VLArray<Short, short[]>>{

        public DefinitionArrayInterleaved(VLBufferDirect target, int bufferoffset, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
            super(target, bufferoffset, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }

        @Override
        protected void sync(VLArray<Short, short[]> source, VLBufferDirect target){
            target.position(bufferoffset);
            target.putInterleaved(source.provider(), arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
        }
    }
}

