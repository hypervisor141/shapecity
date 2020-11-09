package vanguard;

public class VLTaskContinous<VARTYPE extends VLV> extends VLTask<VARTYPE>{

    public VLTaskContinous(Task<VARTYPE> task){
        super(task);
    }

    @Override
    protected boolean checkRun(VARTYPE v){
        task.run(this, v);
        return false;
    }

    @Override
    protected void reset(){

    }
}
