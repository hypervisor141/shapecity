package vanguard;

public abstract class VLTask {

    protected Task task;

    public VLTask(Task task){
        this.task = task;
    }


    protected abstract boolean checkRun(VLVConst var);

    protected void reset(){

    }

    public interface Task{

        void run(VLTask t, VLVConst v);
    }
}
