package vanguard;

public final class VLTaskOnCycle extends VLTask {

    private int cycles;
    private int count;


    public VLTaskOnCycle(Task task, int cycles){
        super(task);
        this.cycles = cycles;
    }


    @Override
    protected boolean checkRun(VLVConst v){
        count++;

        if(count >= cycles){
            count = 0;
            task.run(this, v);

            return true;
        }

        return false;
    }

    @Override
    protected void reset(){

    }
}