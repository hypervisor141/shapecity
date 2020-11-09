package vanguard;

public abstract class VLTask<VARTYPE extends VLV>{

    protected Task<VARTYPE> task;

    public VLTask(Task<VARTYPE> task){
        this.task = task;
    }


    protected abstract boolean checkRun(VARTYPE var);

    protected void reset(){

    }

    public interface Task<VARTYPE extends VLV>{

        void run(VLTask<VARTYPE> t, VARTYPE v);
    }
}
