package vanguard;

import android.app.RecoverableSecurityException;
import android.util.Log;

import firestorm.FSMath;

public abstract class VLV extends VLSyncer.Syncable implements VLStringify{

    public static final InterpolatorConstant INTERP_LINEAR = new InterpolatorConstant();
    public static final InterpolatorCosAcc INTERP_ACCELERATE_COS = new InterpolatorCosAcc();
    public static final InterpolatorSineSqrtAcc INTERP_ACCELERATE_SINE_SQRT = new InterpolatorSineSqrtAcc();
    public static final InterpolatorCosAccDec INTERP_ACCELERATE_DECELERATE_COS = new InterpolatorCosAccDec();
    public static final InterpolatorCubicAccDec INTERP_ACCELERATE_DECELERATE_CUBIC = new InterpolatorCubicAccDec();
    public static final InterpolatorSineDec INTERP_DECELERATE_SINE = new InterpolatorSineDec();
    public static final InterpolatorSineSqrtDec INTERP_DECELERATE_SINE_SQRT = new InterpolatorSineSqrtDec();
    public static final InterpolatorCosSqrtDec INTERP_DECELERATE_COS_SQRT = new InterpolatorCosSqrtDec();

    public static final LoopForward LOOP_FORWARD = new LoopForward();
    public static final LoopForwardBackward LOOP_FORWARD_BACKWARD = new LoopForwardBackward();
    public static final LoopReturnOnce LOOP_RETURN_ONCE = new LoopReturnOnce();
    public static final LoopReturning LOOP_RETURNING = new LoopReturning();
    public static final LoopNone LOOP_NONE = new LoopNone();

    public void set(float s){
        throw new RuntimeException("Stub");
    }

    public void push(float amount, int cycles){
        throw new RuntimeException("Stub");
    }

    public void push(int hint, float amount, int cycles){
        throw new RuntimeException("Stub");
    }

    public void push(float amount){
        throw new RuntimeException("Stub");
    }

    public void push(int hint, float amount){
        throw new RuntimeException("Stub");
    }

    public float get(){
        throw new RuntimeException("Stub");
    }

    public short getShort(){
        return (short)get();
    }

    public int getInt(){
        return (int)get();
    }

    public long getLong(){
        return (long)get();
    }

    public double getDouble(){
        return (double)get();
    }

    public boolean next(){
        throw new RuntimeException("Stub");
    }

    public boolean next(int hint){
        throw new RuntimeException("Stub");
    }

    protected float advance(){
        throw new RuntimeException("Stub");
    }

    protected void resetTask(){
        throw new RuntimeException("Stub");
    }

    protected void taskCheck(){
        throw new RuntimeException("Stub");
    }

    public void finish(){
        throw new RuntimeException("Stub");
    }

    public void finish(int hint){
        throw new RuntimeException("Stub");
    }

    public void reset(){
        throw new RuntimeException("Stub");
    }

    public void reset(int hint){
        throw new RuntimeException("Stub");
    }

    public void reinitialize(int cycles){
        throw new RuntimeException("Stub");
    }

    public void chain(int cycles, float to){
        throw new RuntimeException("Stub");
    }

    public void reverse(){
        throw new RuntimeException("Stub");
    }

    public void reverse(int hint){
        throw new RuntimeException("Stub");
    }

    public void permanent(){
        throw new RuntimeException("Stub");
    }

    public void setLoop(Loop loop){
        throw new RuntimeException("Stub");
    }

    public VLV setTask(VLTask task){
        throw new RuntimeException("Stub");
    }

    protected void setChange(float s){
        throw new RuntimeException("Stub");
    }

    protected void setFrom(float s){
        throw new RuntimeException("Stub");
    }

    protected void setTo(float s){
        throw new RuntimeException("Stub");
    }

    protected void setLow(float s){
        throw new RuntimeException("Stub");
    }

    protected void setHigh(float s){
        throw new RuntimeException("Stub");
    }

    public boolean isDone(){
        throw new RuntimeException("Stub");
    }

    public boolean isDone(int hint){
        throw new RuntimeException("Stub");
    }

    public Loop getLoop(){
        throw new RuntimeException("Stub");
    }

    public float getFrom(){
        throw new RuntimeException("Stub");
    }

    public float getTo(){
        throw new RuntimeException("Stub");
    }

    public float getChange(){
        throw new RuntimeException("Stub");
    }

    public boolean getIncreasing(){
        throw new RuntimeException("Stub");
    }

    public float getTargetValue(){
        throw new RuntimeException("Stub");
    }

    public int getCycles(){
        throw new RuntimeException("Stub");
    }

    public int getCycles(int hint){
        throw new RuntimeException("Stub");
    }

    public int getAbsoluteCycles(){
        throw new RuntimeException("Stub");
    }

    public int getAbsoluteCycles(int hint){
        throw new RuntimeException("Stub");
    }

    public int getCyclesRemaining(){
        throw new RuntimeException("Stub");
    }

    public int getCyclesRemaining(int hint){
        throw new RuntimeException("Stub");
    }

    public int getAbsoluteCyclesRemaining(){
        throw new RuntimeException("Stub");
    }

    public int getAbsoluteCyclesRemaining(int hint){
        throw new RuntimeException("Stub");
    }

    public VLTask getTask(){
        throw new RuntimeException("Stub");
    }

    public Interpolator getInterpolator(){
        throw new RuntimeException("Stub");
    }

    public float getHigh(){
        throw new RuntimeException("Stub");
    }

    public float getLow(){
        throw new RuntimeException("Stub");
    }

    public int getWrapCounts(){
        throw new RuntimeException("Stub");
    }

    public float getMillisMultiplier(){
        throw new RuntimeException("Stub");
    }

    public boolean isVariableType(){
        throw new RuntimeException("Stub");
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        if((boolean)hint){
            src.append("type[");
            src.append(getClass().getSimpleName());
            src.append("] vartype[");
            src.append(isVariableType());
            src.append("] from[");
            src.append(getFrom());
            src.append("] to[");
            src.append(getTo());
            src.append("] value[");
            src.append(get());
            src.append("] low[");
            src.append(getLow());
            src.append("] high[");
            src.append(getHigh());
            src.append("] cycles[");
            src.append(getCycles());
            src.append("] interpolator[");
            src.append(getInterpolator().getClass().getSimpleName());
            src.append("] millismult[");
            src.append(getMillisMultiplier());
            src.append("] change[");
            src.append(getChange());
            src.append("] target[");
            src.append(getTargetValue());
            src.append("]");
            src.append(" loop[");
            src.append(getLoop().getClass().getSimpleName());
            src.append("] wrapcount[");
            src.append(getWrapCounts());
            src.append("] done[");
            src.append(isDone());
            src.append("]");

        }else{
            src.append("type[");
            src.append(getClass().getSimpleName());
            src.append("] vartype[");
            src.append(isVariableType());
            src.append("] value[");
            src.append(get());
            src.append("]");
        }
    }

    public abstract static class Loop{

        public abstract boolean process(VLV var);
        public abstract void resetting(VLV var);
    }

    public static class LoopNone extends Loop{

        @Override
        public boolean process(VLV var){
            return false;
        }

        @Override
        public void resetting(VLV var){

        }
    }

    public static class LoopForward extends Loop{

        @Override
        public boolean process(VLV var){
            var.reset();
            return true;
        }

        @Override
        public void resetting(VLV var){

        }
    }

    public static class LoopForwardBackward extends Loop{

        @Override
        public boolean process(VLV var){
            var.reverse();
            var.reset();

            return true;
        }

        @Override
        public void resetting(VLV var){

        }
    }

    public static class LoopReturnOnce extends Loop{

        @Override
        public boolean process(VLV var){
            var.reverse();
            var.reset();
            var.setLoop(LOOP_RETURNING);

            return true;
        }

        @Override
        public void resetting(VLV var){

        }
    }

    public static class LoopReturning extends Loop{

        @Override
        public boolean process(VLV var){
            return false;
        }

        @Override
        public void resetting(VLV var){
            var.reverse();
            var.setLoop(LOOP_RETURN_ONCE);
        }
    }





    public abstract static class Interpolator{

        public abstract double process(float tracker);
    }

    public static final class InterpolatorConstant extends Interpolator{

        @Override
        public double process(float tracker){
            return Math.abs(tracker);
        }
    }

    public static final class InterpolatorCosAcc extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateCosAccelerate(tracker);
        }
    }

    public static final class InterpolatorSineSqrtAcc extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateSinSqrtAccelerate(tracker);
        }
    }

    public static final class InterpolatorCosAccDec extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateCosAccelerateDecelerate(tracker);
        }
    }

    public static final class InterpolatorCubicAccDec extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateCubicAccelerateDecelerate(tracker);
        }
    }

    public static final class InterpolatorSineDec extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateSineDecelerate(tracker);
        }
    }

    public static final class InterpolatorSineSqrtDec extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateSinSqrtDecelerate(tracker);
        }
    }

    public static final class InterpolatorCosSqrtDec extends Interpolator{

        @Override
        public double process(float tracker){
            return FSMath.interpolateCosSqrtDecelerate(tracker);
        }
    }
}
