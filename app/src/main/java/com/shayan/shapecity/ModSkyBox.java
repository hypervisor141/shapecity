package com.shayan.shapecity;

import firestorm.FSBrightness;
import firestorm.FSConfig;
import firestorm.FSConfigDynamic;
import firestorm.FSControl;
import firestorm.FSGamma;
import firestorm.FSLoader;
import firestorm.FSP;
import firestorm.FSShader;

public class ModSkyBox extends FSP.Modifier{

    private FSGamma gamma;
    private FSBrightness brightness;
    
    public ModSkyBox(FSGamma gamma, FSBrightness brightness){
        this.gamma = gamma;
        this.brightness = brightness;
    }

    @Override
    protected void modify(FSP program, FSConfig.Policy policy){
        FSShader vertex = program.vertexShader();
        FSShader fragment = program.fragmentShader();

        FSConfig position = new FSP.AttribPointer(policy, FSLoader.ELEMENT_POSITION, 0);
        FSConfig vp = new FSP.UniformMatrix4fvd(policy, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
        FSConfig skyboxtexunit = new FSP.TextureColorUnit(policy);
        FSConfig skyboxtexbind = new FSP.TextureColorBind(policy);
        FSConfig depthoff = new FSP.DepthMask(policy, false);
        FSConfig depthon = new FSP.DepthMask(policy, true);
        FSConfig bright = new FSConfigDynamic<FSBrightness>(brightness);
        FSConfig gam = new FSConfigDynamic<FSGamma>(gamma);

        program.registerAttributeLocation(vertex, position);
        program.registerUniformLocation(vertex, vp);
        program.registerUniformLocation(fragment, gam);
        program.registerUniformLocation(fragment, skyboxtexunit);
        program.registerUniformLocation(fragment, bright);

        FSConfig enableposition = new FSP.AttribEnable(policy, position.location());
        FSConfig disableposition = new FSP.AttribDisable(policy, position.location());

        program.addSetupConfig(vp);
        program.addSetupConfig(gam);
        program.addSetupConfig(bright);
        program.addSetupConfig(depthoff);
        program.addSetupConfig(enableposition);
        program.addMeshConfig(skyboxtexunit);
        program.addMeshConfig(skyboxtexbind);
        program.addMeshConfig(position);
        program.addPostDrawConfig(depthon);
        program.addPostDrawConfig(disableposition);

        vertex.addAttribute(position.location(),"vec4", "position");
        vertex.addUniform(vp.location(), "mat4","vp");
        vertex.addPipedOutputField("vec3", "texcoords");
        vertex.addMainCode("gl_Position = vp * model * position;");
        vertex.addMainCode("texcoords = vec3(position);");

        fragment.addPrecision("mediump", "float");
        fragment.addFunctionCode(gamma.getGammaFunction());
        fragment.addUniform(gam.location(), "float","gamma");
        fragment.addUniform(skyboxtexunit.location(),"samplerCube", "texunit");
        fragment.addUniform(bright.location(),"float", "brightness");
        fragment.addPipedInputField("vec3", "texcoords");
        fragment.addPipedOutputField("vec4", "fragColor");
        fragment.addMainCode("vec4 vcolor = texture(texunit, texcoords);");
        fragment.addMainCode("fragColor = vcolor * brightness;");
        fragment.addMainCode("correctGamma(fragColor, gamma);");
    }
}
