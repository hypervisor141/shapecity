package vanguard;

public abstract class VLFactory {

    protected VLFactory(){

    }

    public <REFERENCE extends VLFactory, FABRICATED, HINT> FABRICATED fabricate(VLFactory.Mold<FABRICATED, REFERENCE, HINT> mold, HINT hint){
        return mold.mold((REFERENCE)this, hint);
    }

    public <REFERENCE extends VLFactory, HINT> void edit(VLFactory.Edit< REFERENCE, HINT> editor, HINT hint){
        editor.edit((REFERENCE)this, hint);
    }

    public interface Mold<FABRICATED, REFERENCE extends VLFactory, HINT>{

        public abstract FABRICATED mold(REFERENCE src, HINT hint);
    }

    public interface Edit<REFERENCE extends VLFactory, HINT>{

        public abstract void edit(REFERENCE src, HINT hint);
    }
}
