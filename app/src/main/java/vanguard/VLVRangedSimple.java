package vanguard;


import firestorm.FSMath;

public class VLVRangedSimple extends VLVConst {

    protected float low, high;

    public VLVRangedSimple(float value, float low, float high){
        super(value);

        this.high = high;
        this.low = low;
    }

    @Override
    public void set(float s){
        value = FSMath.wrapOverRange(s, low, high)[0];
    }

    @Override
    public void push(float amount, int cycles){
        push(amount);
    }

    @Override
    public void push(float amount){
        set(value + amount);
    }

    @Override
    public float getHigh(){
        return high;
    }

    @Override
    public float getLow(){
        return low;
    }

    @Override
    public boolean isDone(){
        return false;
    }
}
