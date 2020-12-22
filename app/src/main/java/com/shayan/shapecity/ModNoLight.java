package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSConfigDynamic;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSPMod;
import com.nurverek.firestorm.FSShader;

public class ModNoLight implements FSPMod{

    private FSGamma gamma;
    private FSBrightness brightness;

    public ModNoLight(FSGamma gamma, FSBrightness brightness){
        this.gamma = gamma;
        this.brightness = brightness;
    }

    @Override
    public void modify(FSP program){
        FSShader vertex = program.vertexShader();
        FSShader fragment = program.fragmentShader();

        FSConfig model = new FSP.UniformMatrix4fve(FSConfig.POLICY_ALWAYS,0, FSG.ELEMENT_MODEL, 0, 1);
        FSConfig position = new FSP.AttribPointer(FSConfig.POLICY_ALWAYS, FSG.ELEMENT_POSITION, 0);
        FSConfig color = new FSP.Uniform4fve(FSConfig.POLICY_ALWAYS, 0, FSG.ELEMENT_COLOR, 0, 1);
        FSConfig vp = new FSP.UniformMatrix4fvd(FSConfig.POLICY_ALWAYS, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
        FSConfig bright = new FSConfigDynamic<>(brightness);
        FSConfig gam = new FSConfigDynamic<>(gamma);

        program.registerAttributeLocation(vertex, position);
        program.registerUniformLocation(vertex, vp);
        program.registerUniformLocation(vertex, model);
        program.registerUniformLocation(fragment, color);
        program.registerUniformLocation(fragment, gam);
        program.registerUniformLocation(fragment, bright);

        FSConfig enableposition = new FSP.AttribEnable(FSConfig.POLICY_ALWAYS, position.location());
        FSConfig disableposition = new FSP.AttribDisable(FSConfig.POLICY_ALWAYS, position.location());

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
