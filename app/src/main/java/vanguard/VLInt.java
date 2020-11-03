package vanguard;

public class VLInt extends VLSyncer.Syncable implements VLStringify{

    private int field;

    public VLInt(int v){
        field = v;
    }

    public VLInt(){

    }


    public void set(int field){
        this.field = field;
    }

    public int get(){
        return field;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        src.append("int[");
        src.append(field);
        src.append("]");
    }

    public static class DefinitionVLV extends VLSyncer.Definition<VLV, VLInt>{

        public DefinitionVLV(VLInt target){
            super(target);
        }

        @Override
        protected void sync(VLV source, VLInt target){
            target.set(source.getInt());
        }
    }
}
