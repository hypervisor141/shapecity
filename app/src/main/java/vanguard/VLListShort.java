package vanguard;

import java.util.Arrays;

public final class VLListShort extends VLList{

    private short[] array;

    public VLListShort(int initialsize, int resizercount){
        super(resizercount, 0);
        array = new short[initialsize];
    }

    public VLListShort(short[] data, int resizercount){
        super(resizercount, data.length);
        array = data;
    }


    public void add(short item){
        if(currentsize >= array.length){
            resize(array.length + resizercount);
        }

        array[currentsize++] = item;
    }

    public void add(short[] items){
        int target = currentsize + items.length;

        if(target >= array.length){
            resize(target + resizercount);
        }

        for(int i = 0; i < items.length; i++){
            array[currentsize++] = items[i];
        }
    }

    public void add(VLListShort items){
        int target = currentsize + items.size();

        if(target >= array.length){
            resize(target + resizercount);
        }

        for(int i = 0; i < items.size(); i++){
            array[currentsize++] = items.get(i);
        }
    }

    public void add(int index, short item){
        if(currentsize >= array.length){
            resize(array.length + resizercount);
        }

        VLArrayUtils.addInPlace(index, currentsize, array, item);
        currentsize++;
    }

    public void set(int index, short item){
        checkIndex(index, 1);
        array[index] = item;
    }

    public short get(int index){
        checkIndex(index, 1);
        return array[index];
    }

    public int indexOf(short target){
        int size = size();

        for(int i = 0; i < size; i++){
            if(array[i] == target){
                return i;
            }
        }

        return -1;
    }

    public short remove(int index){
        checkIndex(index, 1);

        short item = array[index];
        VLArrayUtils.removeInPlace(array, index, 1);
        currentsize--;

        return item;
    }

    public void remove(short item){
        if(VLArrayUtils.removeInPlace(array, item) != -1){
            currentsize--;
        }
    }

    @Override
    public void remove(int index, int count){
        checkIndex(index, count);
        VLArrayUtils.removeInPlace(array, index, count);
        currentsize -= count;
    }

    @Override
    public int realSize(){
        return array.length;
    }

    @Override
    public short[] array(){
        return array;
    }

    @Override
    public void resize(int size){
        if(currentsize > size){
            currentsize = size;
        }

        short[] newarray = new short[size];
        System.arraycopy(array, 0, newarray, 0, currentsize);
        array = newarray;
    }

    @Override
    public void clear(){
        array = new short[resizercount];
        currentsize = 0;
    }

    @Override
    public void clear(int capacity){
        array = new short[capacity];
        currentsize = 0;
    }

    @Override
    public void nullify(){
        for(int i = 0; i < currentsize; i++){
            array[i] = 0;
        }
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        super.stringify(src, hint);

        src.append(" content[");
        src.append(Arrays.toString(array));
        src.append("]");
    }
}