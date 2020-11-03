package vanguard;

public class VLShort extends VLSyncer.Syncable implements VLStringify{

    private short field;

    public VLShort(short v){
        field = v;
    }

    public VLShort(){

    }


    public void set(short field){
        this.field = field;
    }

    public short get(){
        return field;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        src.append("short[");
        src.append(field);
        src.append("]");
    }

    public static class DefinitionVLV extends VLSyncer.Definition<VLV, VLShort>{

        public DefinitionVLV(VLShort target){
            super(target);
        }

        @Override
        protected void sync(VLV source, VLShort target){
            target.set(source.getShort());
        }
    }
}

