package vanguard;

import java.nio.ByteBuffer;

public abstract class VLBufferDirect<ELEMENT extends Number, PROVIDER extends java.nio.Buffer> extends VLSyncer.Syncable implements VLStringify{

    protected PROVIDER buffer;

    public VLBufferDirect(int capacity){
        initialize(capacity);
    }

    public VLBufferDirect(){

    }


    public final VLBufferDirect initialize(int capacity){
        return this.initialize(VLTools.makeDirectByteBuffer(capacity * getTypeBytes()));
    }

    public abstract VLBufferDirect initialize(ByteBuffer buffer);

    public void put(short data){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(int data){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(float data){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(VLV data){
        throw new RuntimeException("Invalid operation.");
    }

    public final void put(short[] data){
        put(data, 0, data.length);
    }

    public final void put(int[] data){
        put(data, 0, data.length);
    }
    
    public final void put(float[] data){
        put(data, 0, data.length);
    }

    public void put(VLListType<VLV> data){
        put(data, 0, data.size());
    }

    public void put(short[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(int[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(float[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public void put(VLListType<VLV> data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public final void put(VLBufferable<VLBufferDirect<ELEMENT, PROVIDER>> data){
        data.buffer(this);
    }

    public final void put(int index, VLBufferable<VLBufferDirect<ELEMENT, PROVIDER>> data){
        position(index);
        put(data);
    }
    
    public final int putInterleaved(short[] data, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
        if(unitsize == stride && unitsize == unitsubcount){
            put(data, arrayoffset, arraycount);
            return buffer.position();
        }

        int offset = buffer.position();
        int arrayend = unitoffset + unitsubcount;
        int pos = offset;

        for(int i = arrayoffset; i < arraycount; i += unitsize){
            buffer.position(pos);

            for(int i2 = unitoffset; i2 < arrayend; i2++){
                put(data[i + i2]);
            }

            pos += stride;
        }

        return offset + unitsubcount;
    }

    public final int putInterleaved(int[] data, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
        if(unitsize == unitsubcount && unitsize == stride){
            put(data, arrayoffset, arraycount);
            return buffer.position();
        }

        int offset = buffer.position();
        int arrayend = unitoffset + unitsubcount;
        int pos = offset;

        for(int i = arrayoffset; i < arraycount; i += unitsize){
            buffer.position(pos);

            for(int i2 = unitoffset; i2 < arrayend; i2++){
                put(data[i + i2]);
            }

            pos += stride;
        }

        return offset + unitsubcount;
    }

    public final int putInterleaved(float[] data, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
        if(unitsize == unitsubcount && unitsize == stride){
            put(data, arrayoffset, arraycount);
            return buffer.position();
        }

        int offset = buffer.position();
        int arrayend = unitoffset + unitsubcount;
        int pos = offset;

        for(int i = arrayoffset; i < arraycount; i += unitsize){
            buffer.position(pos);

            for(int i2 = unitoffset; i2 < arrayend; i2++){
                put(data[i + i2]);
            }

            pos += stride;
        }

        return offset + unitsubcount;
    }

    public final int putInterleaved(VLListType<VLV> data, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
        if(unitsize == unitsubcount && unitsize == stride){
            put(data, arrayoffset, arraycount);
            return buffer.position();
        }

        int offset = buffer.position();
        int arrayend = unitoffset + unitsubcount;
        int pos = offset;

        for(int i = arrayoffset; i < arraycount; i += unitsize){
            buffer.position(pos);

            for(int i2 = unitoffset; i2 < arrayend; i2++){
                put(data.get(i + i2));
            }

            pos += stride;
        }

        return offset + unitsubcount;
    }

    public ELEMENT read(int index){
        throw new RuntimeException("Invalid operation.");
    }

    public void read(short[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public void read(int[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public void read(float[] data, int offset, int count){
        throw new RuntimeException("Invalid operation.");
    }

    public abstract void remove(int offset, int size);

    public abstract void removeInterleaved(int offset, int unitsize, int stride, int size);

    public final void provider(PROVIDER p){
        buffer = p;
    }

    public void position(int pos){
        buffer.position(pos);
    }

    public void resize(int size){}

    public PROVIDER provider(){
        return buffer;
    }

    public int position(){
        return buffer.position();
    }

    public abstract int getTypeBytes();

    public void release(){
        buffer = null;
    }

    public int size(){
        return buffer.capacity();
    }

    public abstract int sizeBytes();

    @Override
    public void stringify(StringBuilder src, Object hint){
        int size = size();
        int count = hint == null ? size : (int)hint;

        if(count > size){
            count = size;
        }

        src.append("[");
        src.append(getClass().getSimpleName());
        src.append("] capacity[");
        src.append(size);

        if(size == 0){
            src.append("] content[NONE]");

        }else{
            src.append("] content[");

            for(int i = 0; i < count - 1; i++){
                src.append(read(i));
                src.append(", ");
            }

            src.append(read(count - 1));
            src.append("]");
        }
    }



    public static abstract class DefinitionArray<TYPE extends VLArray> extends VLSyncer.Definition<TYPE, VLBufferDirect>{

        public int bufferoffset;

        public DefinitionArray(VLBufferDirect target, int bufferoffset){
            super(target);
            this.bufferoffset = bufferoffset;
        }
    }

    public static abstract class DefinitionArrayInterleaved<TYPE extends VLArray> extends VLSyncer.Definition<TYPE, VLBufferDirect>{

        public int bufferoffset;
        public int arrayoffset;
        public int arraycount;
        public int unitoffset;
        public int unitsize;
        public int unitsubcount;
        public int stride;

        public DefinitionArrayInterleaved(VLBufferDirect target, int bufferoffset, int arrayoffset, int arraycount, int unitoffset,
                                          int unitsize, int unitsubcount, int stride){
            super(target);

            this.bufferoffset = bufferoffset;
            this.arrayoffset = arrayoffset;
            this.arraycount = arraycount;
            this.unitoffset = unitoffset;
            this.unitsize = unitsize;
            this.unitsubcount = unitsubcount;
            this.stride = stride;
        }
    }

    public static class DefinitionCluster<TYPE extends VLVCluster> extends VLSyncer.Definition<TYPE, VLBufferDirect>{

        public int matrixindex;
        public int rowindex;
        public int bufferoffset;
        public int setarraystartindex;
        public int setarrayendindex;
        public int unitoffset;
        public int unitsize;
        public int unitsubcount;
        public int stride;

        public DefinitionCluster(VLBufferDirect target, int matrixindex, int rowindex, int bufferoffset, int setarraystartindex, int setarrayendindex,
                                 int unitoffset, int unitsize, int unitsubcount, int stride){
            super(target);

            this.matrixindex = matrixindex;
            this.rowindex = rowindex;
            this.bufferoffset = bufferoffset;
            this.setarraystartindex = setarraystartindex;
            this.setarrayendindex = setarrayendindex;
            this.unitoffset = unitoffset;
            this.unitsize = unitsize;
            this.unitsubcount = unitsubcount;
            this.stride = stride;
        }

        @Override
        protected void sync(VLVCluster source, VLBufferDirect target){
            target.position(bufferoffset + setarraystartindex);
            target.putInterleaved(source.getRow(matrixindex, rowindex), setarraystartindex, setarrayendindex - setarraystartindex, unitsize, unitoffset, unitsubcount, stride);
        }
    }
}