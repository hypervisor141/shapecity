package vanguard;

public class VLVRealTime extends VLVConst{

    private VLTask task;
    private float millismultiplier;
    private float min;
    private float max;

    public VLVRealTime(float millismultiplier, float min, float max){
        super(System.currentTimeMillis() * millismultiplier);
        this.millismultiplier = millismultiplier;
    }

    @Override
    protected float advanceValue(){
        return System.currentTimeMillis() * millismultiplier;
    }

    @Override
    public VLVRealTime setTask(VLTask task){
        this.task = task;
        return this;
    }

    @Override
    public VLTask getTask(){
        return task;
    }

    @Override
    public float getMillisMultiplier(){
        return millismultiplier;
    }

    @Override
    public float getFrom(){
        return min;
    }

    @Override
    public float getTo(){
        return max;
    }
}
