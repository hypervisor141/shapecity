package vanguard;

public final class VLSyncer{

    private VLListType<Definition> entries;

    public VLSyncer(){
        entries = new VLListType<>(1, 10);
    }

    public VLSyncer(int initialsize, int resizercount){
        entries = new VLListType<>(initialsize, resizercount);
    }


    private final void sync(Syncable self){
        self.SYNCER.syncEntries(self);
    }

    private final void sync(Syncable self, int index){
        self.SYNCER.syncEntry(self, index);
    }

    private final void syncEntries(Syncable self){
        int size = entries.size();

        for(int i = 0; i < size; i++){
            entries.get(i).sync(self);
        }
    }

    private final void syncEntry(Syncable self, int index){
        entries.get(index).sync(self);
    }

    public final int add(Definition item){
        entries.add(item);
        return entries.size() - 1;
    }

    public final void add(int index, Definition item){
        entries.add(index, item);
    }

    public final Definition get(int index){
        return entries.get(index);
    }

    public final int size(){
        return entries.size();
    }

    public final Definition remove(int index){
        return entries.remove(index);
    }

    public final void clear(){
        entries.clear();
    }

    public final void clear(int capacity){
        entries.clear(capacity);
    }



    public static abstract class Syncable{

        public final VLSyncer SYNCER;

        public Syncable(int initialsize, int resizercount){
            SYNCER = new VLSyncer(initialsize, resizercount);
        }

        public Syncable(){
            SYNCER = new VLSyncer();
        }

        public void sync(){
            SYNCER.sync(this);
        }

        public void sync(int index){
            SYNCER.sync(this, index);
        }
    }

    public static abstract class Definition<SOURCETYPE, TARGETTYPE extends Syncable>{

        protected TARGETTYPE target;

        public Definition(TARGETTYPE target){
            this.target = target;
        }

        private final void sync(SOURCETYPE source){
            sync(source, target);
            target.sync();
        }

        protected abstract void sync(SOURCETYPE source, TARGETTYPE target);
    }
}