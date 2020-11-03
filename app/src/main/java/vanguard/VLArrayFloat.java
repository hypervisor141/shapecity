package vanguard;

import java.util.Arrays;

public class VLArrayFloat extends VLArray<Float, float[]> {
    
    public VLArrayFloat(float[] s){
        super(s);
    }

    public VLArrayFloat(int size){
        super(new float[size]);
    }


    @Override
    public void set(int index, Float element){
        array[index] = element;
    }

    @Override
    public Float get(int index){
        return array[index];
    }

    @Override
    public void resize(int size){
        array = VLArrayUtils.resize(array, size);
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        super.stringify(src, hint);

        src.append(" content[");
        src.append(Arrays.toString(array));
        src.append("]");
    }

    @Override
    public int size(){
        return array.length;
    }
}
