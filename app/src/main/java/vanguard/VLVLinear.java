package vanguard;

import android.util.Log;

public class VLVLinear extends VLVConst{

    protected VLTask task;
    protected Loop loop;

    protected float change;
    protected float from;
    protected float to;
    protected float target;


    public VLVLinear(float from, float to, int cycles, Loop loop){
        super(from);

        this.from = from;
        this.to = to;
        this.loop = loop;

        change = (to - from) / cycles;
        target = to;
    }



    @Override
    protected float advance(){
        value += change;

        if(from <= to){
            if(change > 0 && value > to){
                value = to;

            }else if(change < 0 && value < from){
                value = from;
            }

        }else if(to < from){
            if(change > 0 && value > from){
                value = from;

            }else if(change < 0 && value < to){
                value = to;
            }
        }

        return value;
    }

    @Override
    public void reverse(){
        super.reverse();
        target = target == to ? from : to;
    }

    @Override
    protected void setChange(float s){
        change = s;
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
    public void setLoop(Loop loop) {
        this.loop = loop;
    }

    @Override
    public VLVLinear setTask(VLTask task){
        this.task = task;
        return this;
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
    public float getTargetValue(){
        return target;
    }

    @Override
    public boolean isVariableType(){
        return true;
    }
}
