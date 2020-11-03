package vanguard;

import java.util.Arrays;

public final class VLListDouble extends VLList{

    private double[] array;

    public VLListDouble(int initialsize, int resizercount){
        super(resizercount, 0);
        array = new double[initialsize];
    }

    public VLListDouble(double[] data, int resizercount){
        super(resizercount, data.length);
        array = data;
    }


    public void add(double item){
        if(currentsize >= array.length){
            resize(array.length + resizercount);
        }

        array[currentsize++] = item;
    }

    public void add(double[] items){
        int target = currentsize + items.length;

        if(target >= array.length){
            resize(target + resizercount);
        }

        for(int i = 0; i < items.length; i++){
            array[currentsize++] = items[i];
        }
    }

    public void add(VLListDouble items){
        int target = currentsize + items.size();

        if(target >= array.length){
            resize(target + resizercount);
        }

        for(int i = 0; i < items.size(); i++){
            array[currentsize++] = items.get(i);
        }
    }

    public void add(int index, double item){
        if(currentsize >= array.length){
            resize(array.length + resizercount);
        }

        VLArrayUtils.addInPlace(index, currentsize, array, item);
        currentsize++;
    }

    public void set(int index, double item){
        checkIndex(index, 1);
        array[index] = item;
    }

    public double get(int index){
        checkIndex(index, 1);
        return array[index];
    }

    public int indexOf(double item){
        int size = size();

        for(int i = 0; i < size; i++){
            if(array[i] == item){
                return i;
            }
        }

        return -1;
    }

    public double remove(int index){
        checkIndex(index, 1);

        double item = array[index];
        VLArrayUtils.removeInPlace(array, index, 1);
        currentsize--;

        return item;
    }

    public void remove(double item){
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
    public double[] array(){
        return array;
    }

    @Override
    public void resize(int size){
        if(currentsize > size){
            currentsize = size;
        }

        double[] newarray = new double[size];
        System.arraycopy(array, 0, newarray, 0, currentsize);
        array = newarray;
    }

    @Override
    public void clear(){
        array = new double[resizercount];
        currentsize = 0;
    }

    @Override
    public void clear(int capacity){
        array = new double[capacity];
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
