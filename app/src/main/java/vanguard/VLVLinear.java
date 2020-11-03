package vanguard;

public class VLVLinear extends VLVConst {

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
        target = cycles < 0 ? from : to;
    }



    @Override
    protected float advanceValue(){
        value += change;

        if((from <= to && ((change < 0 && value < from) || (change > 0 && value > to))) ||
                (to < from && ((change < 0 && value < to) || (change > 0 && value > from)))){
            value = to;
        }

        return value;
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
