package vanguard;

public final class VLTaskThreshold<VARTYPE extends VLV> extends VLTask<VARTYPE>{


    private float threshold;
    private boolean ran = false;


    public VLTaskThreshold(Task<VARTYPE> task, float threshold){
        super(task);
        this.threshold = threshold;
    }




    @Override
    protected boolean checkRun(VARTYPE v){
        if(!ran){
            float val = v.get();
            boolean increasing = v.getIncreasing();

            if((increasing && val >= threshold) || (!increasing && val <= threshold)){
                task.run(this, v);
                ran = true;

                return true;
            }
        }

        return false;
    }

    @Override
    protected void reset(){
        ran = false;
    }
}
