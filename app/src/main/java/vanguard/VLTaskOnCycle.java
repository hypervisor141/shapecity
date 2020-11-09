package vanguard;

public final class VLTaskOnCycle<VARTYPE extends VLV> extends VLTask<VARTYPE>{

    private int cycles;
    private int count;


    public VLTaskOnCycle(Task<VARTYPE> task, int cycles){
        super(task);
        this.cycles = cycles;
    }


    @Override
    protected boolean checkRun(VARTYPE v){
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