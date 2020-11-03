package vanguard;

import firestorm.FSControl;
import firestorm.FSRenderer;

public final class VLVProcessor implements VLStringify{

    public static final VLUpdater<Entry> SYNC_INDEX = new VLUpdater<Entry>(){
        @Override
        public void update(Entry target){
            target.target.sync(target.syncindex);
        }
    };
    public static final VLUpdater<Entry> SYNC_ALL = new VLUpdater<Entry>(){
        @Override
        public void update(Entry target){
            target.target.sync();
        }
    };
    public static final VLUpdater<Entry> SYNC_NOTHING = VLUpdater.UPDATE_NOTHING;

    private static final Listener DEFAULT_LISTENER = new Listener(){ };

    private VLListType<Entry> data;
    private VLListType<Entry> active;

    private Listener listener;

    private boolean pause;
    private boolean directmode;
    private boolean reversed;
    
    private long id;

    public VLVProcessor(int initialsize, int resizercount){
        data = new VLListType<>(initialsize, resizercount);
        active = new VLListType<>(initialsize, resizercount);

        id = FSControl.getNextID();
        listener = DEFAULT_LISTENER;

        pause = true;
        reversed = false;
        directmode = false;
    }

    public VLVProcessor(){
        data = new VLListType<>(1, 10);
        active = new VLListType<>(1, 10);

        id = FSControl.getNextID();
        listener = DEFAULT_LISTENER;

        pause = true;
        reversed = false;
        directmode = false;
    }

    
    public int next(){
        if(!pause && !directmode){
            return iterate();

        }else{
            return 0;
        }
    }

    private int iterate(){
        int count = 0;
        int size = active.size();

        Entry current;

        for(int i = 0; i < size; i++){
            current = active.get(i);
            count += current.iterate(this, i);

            listener.iterated(this, current, i);
        }

        if(count == 0){
            pause();
            listener.finished(this);
        }

        return count;
    }

    public int nextIsolated(long sleep){
        int count = 0;
        long totalsleep = 0;
        int currentcount;

        synchronized(FSRenderer.RENDERLOCK){
            directmode = true;
            FSControl.setEfficientRenderControl(false);
        }

        while(true){
            synchronized(FSRenderer.RENDERLOCK){
                currentcount = iterate();
            }

            count += currentcount;

            try{
                Thread.sleep(sleep);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }

            if(currentcount == 0){
                break;
            }
        }

        synchronized(FSRenderer.RENDERLOCK){
            directmode = false;
            FSControl.setEfficientRenderControl(true);
        }

        return count;
    }

    public void start(){
        pause = false;
        listener.started(this);
        
        FSControl.setRenderContinuously(true);
    }

    public void pause(){
        pause = true;
        listener.paused(this);
    }

    public void sync(){
        int size = active.size();
        
        for(int i = 0; i < size; i++){
            active.get(i).sync();
        }
    }
    
    public void push(float amount, int directionalcycles){
        int size = active.size();
        Entry e;
        
        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.target.push(e.setindex, amount, directionalcycles);
        }
    }

    public void reset(){
        int size = active.size();
        Entry e;
        
        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.target.reset(e.setindex);
        }

        resetDelay();

        listener.reset(this);
    }

    public void reverseReset(){
        reversed = !reversed;
        
        int size = active.size();
        Entry e;

        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.target.reverse(e.setindex);
            e.target.reset(e.setindex);
        }

        resetDelay();
        
        listener.reversed(this);
    }

    public void finish(){
        int size = active.size();
        Entry e;
        
        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.target.finish(e.setindex);

            listener.finishedGLV(this, e.target, i);
        }

        listener.finished(this);
    }

    public void releaseGLVs(){
        pause();
        
        int size = active.size();
        Entry e;

        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.target = new VLVConst(e.target.get());
        }

        if(listener != null){
            listener.releasedGLVs(this);
        }
    }
    
    public void resetDelay(){
        int size = active.size();
        Entry e;

        for(int i = 0; i < size; i++){
            e = active.get(i);
            e.delaytracker = e.delay;
        }
    }

    public void getMinCycles(int type, int[] results){
        VLV var;
        int currentcycles;
        int index = -1;
        int cycles = Integer.MAX_VALUE;
        int size = active.size();

        for(int i = 0; i < size; i++){
            var = active.get(i).target;
            currentcycles = var.getAbsoluteCycles();

            if(cycles > currentcycles){
                cycles = currentcycles;
                index = i;
            }
        }

        results[0] = cycles;
        results[1] = index;
    }

    public void getMinRemainingCycles(int type, int[] results){
        VLV var;
        int currentcycles;
        int index = -1;
        int cycles = Integer.MAX_VALUE;
        int size = active.size();

        for(int i = 0; i < size; i++){
            var = active.get(i).target;
            currentcycles = var.getAbsoluteCyclesRemaining();

            if(cycles > currentcycles){
                cycles = currentcycles;
                index = i;
            }
        }

        results[0] = cycles;
        results[1] = index;
    }

    public void getMaxCycles(int type, int[] results){
        VLV var;
        int currentcycles;
        int index = -1;
        int cycles = 0;
        int size = active.size();

        for(int i = 0; i < size; i++){
            var = active.get(i).target;
            currentcycles = var.getAbsoluteCycles();

            if(cycles < currentcycles){
                cycles = currentcycles;
                index = i;
            }
        }

        results[0] = cycles;
        results[1] = index;
    }

    public void getMaxRemainingCycles(int type, int[] results){
        VLV var;
        int currentcycles;
        int index = -1;
        int cycles = 0;
        int size = active.size();

        for(int i = 0; i < size; i++){
            var = active.get(i).target;
            currentcycles = var.getAbsoluteCyclesRemaining();

            if(cycles < currentcycles){
                cycles = currentcycles;
                index = i;
            }
        }

        results[0] = cycles;
        results[1] = index;
    }

    public boolean isDone(){
        for(int i = 0; i < active.size(); i++){
            Entry d = active.get(i);

            if(!d.target.isDone(d.setindex)){
                return false;
            }
        }

        return true;
    }

    public void setListener(Listener l){
        listener = l;
    }

    public void add(Entry entry){
        data.add(entry);
    }

    public VLListType<Entry> get(){
        return data;
    }

    public Entry get(int index){
        return data.get(index);
    }

    public Entry getActive(int index){
        return active.get(index);
    }

    public boolean isPaused(){
        return pause;
    }

    public boolean isReversed(){
        return reversed;
    }

    public int sizeData(){
        return data.size();
    }

    public int sizeActive(){
        return active.size();
    }

    public long id(){
        return id;
    }

    public int activate(int index){
        active.add(data.get(index));
        return active.size() - 1;
    }

    public void deactivate(int index){
        active.remove(index);
    }

    public int activateLatest(){
        active.add(data.get(data.size() - 1));
        return active.size() - 1;
    }

    public void deactivateLatest(){
        active.remove(active.size() - 1);
    }

    public void activateAll(){
        int size = data.size();

        for(int i = 0; i < size; i++){
            active.add(data.get(i));
        }
    }

    public void deactivateAll(){
        active.virtualSize(0);
    }

    public Entry remove(int index){
        return data.remove(index);
    }

    public void clear(){
        data.virtualSize(0);
        active.virtualSize(0);
    }

    @Override
    public void stringify(StringBuilder info, Object hint){
        boolean verbose = (boolean)hint;

        info.append("VLVProcessor[");
        info.append(data.size());
        info.append("] paused[");
        info.append(pause);
        info.append("] directmode[");
        info.append(directmode);
        info.append("] reversed[");
        info.append(reversed);
        info.append("] data[");

        if(verbose){
            info.append("\n");
        }

        info.append("[");

        for(int i = 0; i < data.size(); i++){
            VLV v = data.get(i).target;

            info.append("[");
            v.stringify(info, verbose);
            info.append("]");

            if(i != data.size() - 1){
                info.append( ", ");

                if(verbose){
                    info.append("\n");
                }

            }else{
                info.append("]");
            }
        }

        info.append("] activeData[");

        for(int i = 0; i < active.size(); i++){
            VLV v = active.get(i).target;

            info.append("[");
            v.stringify(info, verbose);
            info.append("]");

            if(i != data.size() - 1){
                info.append( ", ");

                if(verbose){
                    info.append("\n");
                }

            }else{
                info.append("]");
            }
        }

        info.append("]");
    }




    public static abstract class Listener{

        public void started(VLVProcessor a){ }
        public void iterated(VLVProcessor a, Entry entry, int index){ }
        public void paused(VLVProcessor a){ }
        public void reset(VLVProcessor a){ }
        public void reversed(VLVProcessor a){ }
        public void releasedGLVs(VLVProcessor a){ }
        public void finishedGLV(VLVProcessor a, VLV v, int index){ }
        public void finished(VLVProcessor a){ }
    }

    private static final class Target{

        protected VLV target;
        protected int setindex;

        protected Target(VLV target, int setindex){
            this.target = target;
            this.setindex = setindex;
        }

        protected Target(VLV target){
            this.target = target;
            this.setindex = -1;
        }
    }

    public static final class Entry{

        public VLV target;
        public VLUpdater<Entry> syncmode;

        public int setindex;
        public int syncindex;
        public int delay;
        public int delaytracker;

        public Entry(VLV target, int delay){
            this.target = target;
            this.setindex = -1;
            this.syncmode = SYNC_ALL;
            this.syncindex = -1;
            this.delay = delay;
        }

        public Entry(VLV target, VLUpdater<Entry> syncmode, int syncindex, int delay){
            this.target = target;
            this.setindex = -1;
            this.syncmode = syncmode;
            this.syncindex = syncindex;
            this.delay = delay;
        }

        public Entry(VLVCluster target, int setindex, int delay){
            this.target = target;
            this.setindex = setindex;
            this.syncmode = SYNC_ALL;
            this.syncindex = -1;
            this.delay = delay;
        }

        public Entry(VLVCluster target, int setindex, VLUpdater<Entry> syncmode, int syncindex, int delay){
            this.target = target;
            this.setindex = setindex;
            this.syncmode = syncmode;
            this.syncindex = syncindex;
            this.delay = delay;
        }

        protected void sync(){
            syncmode.update(this);
        }
        
        protected int iterate(VLVProcessor processor, int index){
            int count = 0;

            if(delaytracker >= 0){
                if(delaytracker < delay){
                    delaytracker++;
                    count++;

                }else{
                    if(!target.next(setindex)){
                        processor.listener.finishedGLV(processor, target, index);
                        delaytracker = -1;

                    }else{
                        count++;
                    }

                    sync();
                }
            }

            return count;
        }
    }
}
