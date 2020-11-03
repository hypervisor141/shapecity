package vanguard;

public class VLFloat extends VLSyncer.Syncable implements VLStringify{

    private float field;

    public VLFloat(float v){
        field = v;
    }

    public VLFloat(){

    }


    public void set(float field){
        this.field = field;
    }

    public float get(){
        return field;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        src.append("float[");
        src.append(field);
        src.append("]");
    }

    public static class DefinitionVLV extends VLSyncer.Definition<VLV, VLFloat>{

        public DefinitionVLV(VLFloat target){
            super(target);
        }

        @Override
        protected void sync(VLV source, VLFloat target){
            target.set(source.getShort());
        }
    }
}
