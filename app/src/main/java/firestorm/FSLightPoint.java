package firestorm;

import vanguard.VLArrayFloat;
import vanguard.VLListType;

public class FSLightPoint extends FSLight{

    public static final String[] STRUCT_MEMBERS = new String[]{
            "vec3 position",
            "float constant",
            "float linear",
            "float quadratic"
    };

    public static final String FUNCTION =
            "vec3 pointLight(PointLight light, Material material, vec3 normal, vec3 vertexpos, vec3 cameraPos, vec3 lightdir, float shadow){\n" +
                    "\tfloat distance = length(light.position - vertexpos);\n" +
                    "\tfloat attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));\n" +
                    "\n" +
                    "\treturn material.ambient * attenuation +\n" +
                    "\t       shadow * (material.diffuse * max(dot(normal, lightdir), 0.0) * attenuation +\n" +
                    "\t       material.specular * pow(max(dot(normal, normalize(lightdir + cameraPos)), 0.0), material.shininess) * attenuation);\n" +
                    "}";

    protected FSAttenuation attenuation;
    protected VLArrayFloat position;

    public FSLightPoint(FSAttenuation attenuation, VLArrayFloat position){
        this.attenuation = attenuation;
        this.position = position;

        update(new VLListType<FSConfig>(new FSConfig[]{
                new FSP.Uniform3fvd(position, 0, 1),
                attenuation
        }, 0));
    }

    @Override
    public FSAttenuation attenuation(){
        return attenuation;
    }

    @Override
    public VLArrayFloat position(){
        return position;
    }

    @Override
    public String[] getStructMembers(){
        return STRUCT_MEMBERS;
    }

    @Override
    public String getLightFunction(){
        return FUNCTION;
    }
}
