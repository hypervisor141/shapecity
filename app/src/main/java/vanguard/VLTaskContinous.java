package vanguard;

public class VLTaskContinous extends VLTask {

    public VLTaskContinous(Task task){
        super(task);
    }

    @Override
    protected boolean checkRun(VLVConst v){
        task.run(this, v);
        return false;
    }

    @Override
    protected void reset(){

    }
}
