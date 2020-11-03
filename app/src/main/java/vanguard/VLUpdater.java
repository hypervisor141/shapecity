package vanguard;

public interface VLUpdater<TYPE>{

    VLUpdater UPDATE_NOTHING = new VLUpdater(){
        @Override
        public void update(Object target){}
    };

    void update(TYPE target);
}
