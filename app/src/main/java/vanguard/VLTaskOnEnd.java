package vanguard;

public final class VLTaskOnEnd extends VLTask {


    public VLTaskOnEnd(Task task){
        super(task);
    }


    @Override
    protected boolean checkRun(VLVConst var){
        if(var.isDone()){
            task.run(this, var);
            return true;
        }

        return false;
    }

    @Override
    protected void reset(){

    }
}