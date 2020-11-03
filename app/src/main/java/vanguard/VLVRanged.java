package vanguard;

import firestorm.FSMath;

public final class VLVRanged extends VLVInterpolated{

    private Listener listener;

    private float high, low, rangedvalue;
    private int wrapcounts;

    public VLVRanged(float initialvalue, float low, float high, Interpolator interpolation, Listener listener){
        super(initialvalue, LOOP_NONE, interpolation);

        wrapValue();

        this.low = low;
        this.high = high;
        this.listener = listener;
    }

    @Override
    public void set(float s){
        super.set(s);
        wrapValue();
    }

    @Override
    public float get(){
        return rangedvalue;
    }

    @Override
    public boolean next(){
        if(super.next()){
            wrapValue();

            if(listener != null){
                if(wrapcounts > listener.prevwrapcount){
                    listener.crossed(this);
                }

                listener.prevwrapcount = wrapcounts;
            }

            return true;
        }

        return false;
    }

    @Override
    public void push(float amount){
        throw new RuntimeException("Use push(amount, directionalcycles) instead");
    }

    @Override
    public void push(float amount, int cycles){
        change = (float)1 / cycles;
        from = value;
        to = value + amount;

        if(cycles < 0){
            value = to;
            wrapValue();
            tracker = 1;

        }else{
            tracker = 0;
        }

        if(listener != null){
            listener.prevwrapcount = 0;
        }
    }

    private void wrapValue(){
        float[] results = FSMath.wrapOverRange(value, low, high);
        rangedvalue = results[0];
        wrapcounts = (int)results[1];
    }

    @Override
    public void setLow(float low){
        this.low = low;
    }

    @Override
    public void setHigh(float high){
        this.high = high;
    }

    @Override
    public float getLow(){
        return low;
    }

    @Override
    public float getHigh(){
        return high;
    }

    @Override
    public int getWrapCounts(){
        return wrapcounts;
    }

    @Override
    public boolean isVariableType(){
        return true;
    }



    public static class Listener{

        private Object uid;
        private float prevwrapcount = 0;

        public Listener(Object uid){
            this.uid = uid;
        }

        public void crossed(VLVRanged v){}
    }
}
