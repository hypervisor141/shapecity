package vanguard;

public class VLDouble extends VLSyncer.Syncable implements VLStringify{

    private double field;

    public VLDouble(double v){
        field = v;
    }

    public VLDouble(){

    }


    public void set(double field){
        this.field = field;
    }

    public double get(){
        return field;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        src.append("double[");
        src.append(field);
        src.append("]");
    }

    public static class DefinitionVLV extends VLSyncer.Definition<VLV, VLDouble>{

        public DefinitionVLV(VLDouble target){
            super(target);
        }

        @Override
        protected void sync(VLV source, VLDouble target){
            target.set(source.getDouble());
        }
    }
}
