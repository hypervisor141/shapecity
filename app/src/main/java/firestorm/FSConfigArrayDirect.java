package firestorm;

import vanguard.VLArray;
import vanguard.VLDebug;

public abstract class FSConfigArrayDirect<TYPE extends VLArray> extends FSConfigArray<TYPE>{

    public FSConfigArrayDirect(FSConfig.Policy policy, TYPE array, int offset, int count){
        super(policy, array, offset, count);
    }

    public FSConfigArrayDirect(TYPE array, int offset, int count){
        super(array, offset, count);
    }

    @Override
    public void debugInfo(FSP program, FSMesh mesh, int debug){
        VLDebug.append("offset[");
        VLDebug.append(offset);
        VLDebug.append("] count[");
        VLDebug.append(count);
        VLDebug.append("] array[");

        array.stringify(VLDebug.get(), null);

        VLDebug.append("]");
    }
}
