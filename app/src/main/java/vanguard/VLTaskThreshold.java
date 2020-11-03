package vanguard;

public final class VLTaskThreshold extends VLTask {


    private float threshold;
    private boolean ran = false;


    public VLTaskThreshold(Task task, float threshold){
        super(task);
        this.threshold = threshold;
    }




    @Override
    protected boolean checkRun(VLVConst v){
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
