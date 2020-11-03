package firestorm;

import vanguard.VLFactory;
import vanguard.VLFloat;
import vanguard.VLListType;

public final class FSAttenuation extends FSConfigSequence{

    protected VLFloat constant;
    protected VLFloat linear;
    protected VLFloat quadratic;

    public FSAttenuation(VLFloat constant, VLFloat linear, VLFloat quadratic){
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
        
        update(new VLListType<FSConfig>(new FSConfig[]{
                new FSP.Uniform1f(constant),
                new FSP.Uniform1f(linear),
                new FSP.Uniform1f(quadratic)
        }, 0));
    }

    public VLFloat constant(){
        return constant;
    }

    public VLFloat linear(){
        return linear;
    }

    public VLFloat quadratic(){
        return quadratic;
    }
}
