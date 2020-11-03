package vanguard;

public class VLVConst extends VLV{

    public static final VLVConst ZERO = new VLVConst(0);
    public static final VLVConst ONE = new VLVConst(1);
    public static final VLVConst MINUS_ONE = new VLVConst(-1);

    protected float value;


    public VLVConst(float value){
        this.value = value;
    }

    public VLVConst(){

    }


    @Override
    public void set(float s){
        value = s;
    }

    @Override
    public void push(float amount, int cycles){

    }

    @Override
    public void push(int hint, float amount, int cycles){
        push(amount, cycles);
    }

    @Override
    public void push(float amount){

    }

    @Override
    public void push(int hint, float amount) {
        push(amount);
    }

    @Override
    public float get(){
        return value;
    }

    @Override
    public boolean next(){
        if(!isVariableType()){
            return false;
        }

        if(isDone()){
            return getLoop().process(this);

        }else{
            value = advanceValue();
            taskCheck();

            return true;
        }
    }

    @Override
    public boolean next(int hint){
        return next();
    }

    @Override
    protected float advanceValue(){
        return value;
    }

    @Override
    protected void resetTask(){
        VLTask task = getTask();

        if(task != null){
            task.reset();
        }
    }

    @Override
    protected void taskCheck(){
        VLTask task = getTask();

        if(task != null){
            task.checkRun(this);
        }
    }

    @Override
    public void finish(){
        value = getTargetValue();
        setLoop(LOOP_NONE);
    }

    @Override
    public void finish(int hint){
        finish();
    }

    @Override
    public void reset(){
        if(isVariableType()){
            resetTask();

            if(getLoop() == LOOP_RETURNING){
                reverse();
                setLoop(LOOP_RETURN_ONCE);
            }

            value = getChange() < 0 ? getTo() : getFrom();
        }
    }

    @Override
    public void reset(int hint){
        reset();
    }

    @Override
    public void reinitialize(int cycles){
        if(isVariableType()){
            setChange((float)1 / cycles);
            reset();
        }
    }

    @Override
    public void chain(int cycles, float to){
        float from = value;

        if(from < to){
            setFrom(from);
            setTo(to);

            cycles = cycles < 0 ? -cycles : cycles;

        }else{
            setFrom(to);
            setTo(from);

            cycles = cycles < 0 ? cycles : -cycles;
        }

        reinitialize(cycles);
    }

    @Override
    public void reverse(){
        if(isVariableType()){
            setChange(-getChange());
        }
    }

    @Override
    public void reverse(int hint){
        reverse();
    }

    @Override
    public void permanent(){
        setChange(0);
    }

    @Override
    public void setLoop(Loop loop){

    }

    @Override
    public VLVConst setTask(VLTask task){
        return this;
    }

    @Override
    protected void setChange(float s){

    }

    @Override
    protected void setFrom(float s){

    }

    @Override
    protected void setTo(float s){

    }

    @Override
    protected void setLow(float s){

    }

    @Override
    protected void setHigh(float s){

    }

    @Override
    public boolean isDone(){
        return !isVariableType() || value == getTargetValue();
    }

    @Override
    public boolean isDone(int hint){
        return isDone();
    }

    @Override
    public Loop getLoop(){
        return LOOP_NONE;
    }

    @Override
    public float getFrom(){
        return 0;
    }

    @Override
    public float getTo(){
        return 0;
    }

    @Override
    public float getChange(){
        return 0;
    }

    @Override
    public boolean getIncreasing(){
        return getChange() > 0;
    }

    @Override
    public float getTargetValue(){
        if(getChange() >= 0){
            return getTo();

        }else{
            return getFrom();
        }
    }

    @Override
    public int getCycles(){
        return (int)Math.floor(1f / getChange());
    }

    @Override
    public int getCycles(int hint){
        return getCycles();
    }

    @Override
    public int getAbsoluteCycles(){
        return Math.abs(getCycles());
    }

    @Override
    public int getAbsoluteCycles(int hint){
        return getAbsoluteCycles();
    }

    @Override
    public int getCyclesRemaining(){
        return (int)Math.ceil((getTargetValue() - value) * getCycles() / Math.abs(getTo() - getFrom()));
    }

    @Override
    public int getCyclesRemaining(int hint){
        return getCyclesRemaining();
    }

    @Override
    public int getAbsoluteCyclesRemaining(){
        return Math.abs(getCyclesRemaining());
    }

    @Override
    public int getAbsoluteCyclesRemaining(int hint){
        return getAbsoluteCyclesRemaining();
    }

    @Override
    public VLTask getTask(){
        return null;
    }

    @Override
    public Interpolator getInterpolator(){
        return INTERP_LINEAR;
    }

    @Override
    public float getHigh(){
        return 0;
    }

    @Override
    public float getLow(){
        return 0;
    }

    @Override
    public int getWrapCounts(){
        return 0;
    }

    @Override
    public float getMillisMultiplier(){
        return 0;
    }

    @Override
    public boolean isVariableType(){
        return false;
    }
}
