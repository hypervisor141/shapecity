package vanguard;

import vanguard.VLBufferDirect;

public interface VLBufferable<TYPE extends VLBufferDirect>{

    void buffer(TYPE buffer);
}

