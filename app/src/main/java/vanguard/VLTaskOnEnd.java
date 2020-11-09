package vanguard;

public final class VLTaskOnEnd<VARTYPE extends VLV> extends VLTask<VARTYPE>{


    public VLTaskOnEnd(Task<VARTYPE> task){
        super(task);
    }


    @Override
    protected boolean checkRun(VARTYPE var){
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