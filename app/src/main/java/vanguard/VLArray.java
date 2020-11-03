package vanguard;

public abstract class VLArray<TYPE, PROVIDER> extends VLSyncer.Syncable implements VLStringify{

    protected PROVIDER array;

    public VLArray(PROVIDER array){
        this.array = array;
    }


    public void transform(int index, VLVCluster obj, boolean replace){

    }

    public abstract void set(int index, TYPE obj);

    public void provider(PROVIDER array){
        this.array = array;
    }

    public abstract void resize(int size);

    public abstract int size();

    public abstract TYPE get(int index);

    public PROVIDER provider(){
        return array;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        src.append("size[");
        src.append(size());
        src.append("]");
    }

    public static class DefinitionVLV extends VLSyncer.Definition<VLV, VLArray>{

        private int index;

        public DefinitionVLV(VLArray target, int index){
            super(target);
            this.index = index;
        }

        @Override
        protected void sync(VLV source, VLArray target){
            target.set(index, source.get());
        }
    }

    public static class DefinitionCluster extends VLSyncer.Definition<VLVCluster, VLArray>{

        private int offset;
        private int setindex;
        private int rowindex;

        public DefinitionCluster(VLArray target, int setindex, int rowindex, int offset){
            super(target);

            this.offset = offset;
            this.setindex = setindex;
            this.rowindex = rowindex;
        }

        @Override
        protected void sync(VLVCluster source, VLArray target){
            VLListType<VLV> row = source.getRow(setindex, rowindex);
            int index = offset;

            for(int i = 0; i < row.size(); i++){
                target.set(index++, row.get(i).get());
            }
        }
    }
}
