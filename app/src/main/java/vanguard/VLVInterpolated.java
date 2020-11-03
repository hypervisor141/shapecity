package vanguard;

import firestorm.FSMath;

public class VLVInterpolated extends VLVConst{

    protected VLTask task;
    protected Interpolator interpolator;
    protected Loop loop;

    protected float change;
    protected float tracker;
    protected float from;
    protected float to;


    public VLVInterpolated(float from, float to, int cycles, Loop loop, Interpolator interpolator){
        this.loop = loop;
        this.interpolator = interpolator;
        this.from = from;
        this.to = to;

        change = (float)1 / cycles;

        if(cycles >= 0){
            set(from);
            tracker = 0;

        }else{
            set(to);
            tracker = 1;
        }
    }

    protected VLVInterpolated(float startvalue, Loop loop, Interpolator interpolator){
        super(startvalue);

        this.loop = loop;
        this.interpolator = interpolator;
    }



    @Override
    protected float advanceValue(){
        tracker += change;

        if(tracker > 0 && tracker < 1){
            return FSMath.range((float)interpolator.process(tracker), from, to);

        }else if(tracker <= 0){
            tracker = 0;
            return from;

        }else{
            tracker = 1;
            return to;
        }
    }

    protected void resetTracker(){
        tracker = change < 0 ? 1f : 0f;
    }

    @Override
    public void finish(){
        super.finish();
        tracker = change < 0 ? 0f : 1f;
    }

    @Override
    public void reset(){
        super.reset();
        resetTracker();
    }

    @Override
    public void reverse(){
        super.reverse();
        resetTracker();
    }

    @Override
    public void setLoop(Loop loop){
        this.loop = loop;
    }

    @Override
    public VLVInterpolated setTask(VLTask task){
        this.task = task;
        return this;
    }

    @Override
    protected void setFrom(float s){
        from = s;
    }

    @Override
    protected void setTo(float s){
        to = s;
    }

    @Override
    protected void setChange(float s){
        change = s;
    }

    @Override
    public float getChange(){
        return change;
    }

    @Override
    public float getFrom(){
        return from;
    }

    @Override
    public float getTo(){
        return to;
    }

    @Override
    public Loop getLoop(){
        return loop;
    }

    @Override
    public VLTask getTask(){
        return task;
    }

    @Override
    public Interpolator getInterpolator(){
        return interpolator;
    }

    @Override
    public boolean isDone(){
        return (change > 0 && tracker == 1) || (change < 0 && tracker == 0);
    }

    @Override
    public boolean isVariableType(){
        return true;
    }
}
