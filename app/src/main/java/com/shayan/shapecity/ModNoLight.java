package com.shayan.shapecity;

import firestorm.FSBrightness;
import firestorm.FSConfig;
import firestorm.FSConfigDynamic;
import firestorm.FSControl;
import firestorm.FSGamma;
import firestorm.FSLoader;
import firestorm.FSP;
import firestorm.FSShader;

public class ModNoLight extends FSP.Modifier{

    private FSGamma gamma;
    private FSBrightness brightness;

    public ModNoLight(FSGamma gamma, FSBrightness brightness){
        this.gamma = gamma;
        this.brightness = brightness;
    }

    @Override
    protected void modify(FSP program, FSConfig.Policy policy){
        FSShader vertex = program.vertexShader();
        FSShader fragment = program.fragmentShader();

        FSConfig model = new FSP.UniformMatrix4fve(policy,0, FSLoader.ELEMENT_MODEL, 0, 1);
        FSConfig position = new FSP.AttribPointer(policy, FSLoader.ELEMENT_POSITION, 0);
        FSConfig color = new FSP.Uniform4fve(policy, 0, FSLoader.ELEMENT_COLOR, 0, 1);
        FSConfig vp = new FSP.UniformMatrix4fvd(policy, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
        FSConfig bright = new FSConfigDynamic<FSBrightness>(brightness);
        FSConfig gam = new FSConfigDynamic<FSGamma>(gamma);

        program.registerAttributeLocation(vertex, position);
        program.registerUniformLocation(vertex, vp);
        program.registerUniformLocation(vertex, model);
        program.registerUniformLocation(fragment, color);
        program.registerUniformLocation(fragment, gam);
        program.registerUniformLocation(fragment, bright);

        FSConfig enableposition = new FSP.AttribEnable(policy, position.location());
        FSConfig disableposition = new FSP.AttribDisable(policy, position.location());

        program.addSetupConfig(vp);
        program.addSetupConfig(gam);
        program.addSetupConfig(bright);
        program.addSetupConfig(enableposition);
        program.addMeshConfig(model);
        program.addMeshConfig(position);
        program.addMeshConfig(color);
        program.addPostDrawConfig(disableposition);

        vertex.addAttribute(position.location(),"vec4", "position");
        vertex.addUniform(vp.location(), "mat4","vp");
        vertex.addUniform(model.location(), "mat4","model");
        vertex.addMainCode("gl_Position = vp * model * position;");

        fragment.addPrecision("mediump", "float");
        fragment.addFunctionCode(gamma.getGammaFunction());
        fragment.addUniform(color.location(), "vec4", "vcolor");
        fragment.addUniform(gam.location(), "float","gamma");
        fragment.addUniform(bright.location(),"float", "brightness");
        fragment.addPipedOutputField("vec4", "fragColor");
        fragment.addMainCode("fragColor = vcolor * brightness;");
        fragment.addMainCode("correctGamma(fragColor, gamma);");
    }
}
