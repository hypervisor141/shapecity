package com.shayan.shapecity;

import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSConfigDynamic;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSPMod;
import com.nurverek.firestorm.FSShader;

public class ModSkyBox implements FSPMod{

    private FSGamma gamma;
    private FSBrightness brightness;
    
    public ModSkyBox(FSGamma gamma, FSBrightness brightness){
        this.gamma = gamma;
        this.brightness = brightness;
    }

    @Override
    public void modify(FSP program){
        FSShader vertex = program.vertexShader();
        FSShader fragment = program.fragmentShader();

        FSConfig position = new FSP.AttribPointer(FSConfig.POLICY_ALWAYS, FSG.ELEMENT_POSITION, 0);
        FSConfig vp = new FSP.UniformMatrix4fvd(FSConfig.POLICY_ALWAYS, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
        FSConfig skyboxtexunit = new FSP.TextureColorUnit(FSConfig.POLICY_ALWAYS);
        FSConfig skyboxtexbind = new FSP.TextureColorBind(FSConfig.POLICY_ALWAYS);
        FSConfig depthoff = new FSP.DepthMask(FSConfig.POLICY_ALWAYS, false);
        FSConfig depthon = new FSP.DepthMask(FSConfig.POLICY_ALWAYS, true);
        FSConfig bright = new FSConfigDynamic<>(FSConfig.POLICY_ALWAYS, brightness);
        FSConfig gam = new FSConfigDynamic<FSGamma>(FSConfig.POLICY_ALWAYS, gamma);

        program.registerAttributeLocation(vertex, position);
        program.registerUniformLocation(vertex, vp);
        program.registerUniformLocation(fragment, gam);
        program.registerUniformLocation(fragment, skyboxtexunit);
        program.registerUniformLocation(fragment, bright);

        FSConfig enableposition = new FSP.AttribEnable(FSConfig.POLICY_ALWAYS, position.location());
        FSConfig disableposition = new FSP.AttribDisable(FSConfig.POLICY_ALWAYS, position.location());

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
