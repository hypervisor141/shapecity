package com.shayan.shapecity;

import com.nurverek.firestorm.FSBrightness;
import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSConfigDynamic;
import com.nurverek.firestorm.FSConfigDynamicSelective;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSGamma;
import com.nurverek.firestorm.FSLightDirect;
import com.nurverek.firestorm.FSLightMaterial;
import com.nurverek.firestorm.FSLightPoint;
import com.nurverek.firestorm.FSLoader;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSShader;
import com.nurverek.firestorm.FSShadowDirect;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.vanguard.VLInt;

public final class ModLight{

    public static final class Direct extends FSP.Modifier{

        private FSGamma gamma;
        private FSBrightness brightness;
        private FSLightDirect lightsource;
        private FSShadowDirect directshadow;
        private int materialglslsize;

        public Direct(FSGamma gamma, FSBrightness brightness, FSLightDirect lightsource, FSShadowDirect directshadow, int materialglslsize){
            this.gamma = gamma;
            this.brightness = brightness;
            this.lightsource = lightsource;
            this.directshadow = directshadow;
            this.materialglslsize = materialglslsize;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();

            FSConfig positions = new FSP.AttribPointer(policy, FSLoader.ELEMENT_POSITION, 0);
            FSConfig normals = new FSP.AttribPointer(policy, FSLoader.ELEMENT_NORMAL, 0);
            FSConfig vp = new FSP.UniformMatrix4fvd(policy, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
            FSConfig material = new FSP.MaterialDynamic(policy, materialglslsize);
            FSConfig cameraPos = new FSP.Uniform3fvd(policy, FSControl.getViewConfig().eyePosition(),0, 1);
            FSConfig bright = new FSConfigDynamic<>(brightness);
            FSConfig gam = new FSConfigDynamic<FSGamma>(gamma);
            FSConfig light = new FSConfigDynamic<FSLightDirect>(lightsource);

            program.registerAttributeLocation(vertex, positions);
            program.registerAttributeLocation(vertex, normals);
            program.registerUniformLocation(vertex, vp);
            program.registerUniformLocation(fragment, bright);
            program.registerUniformLocation(fragment, gam);
            program.registerUniformLocation(fragment, light);
            program.registerUniformLocation(fragment, material);
            program.registerUniformLocation(fragment, cameraPos);

            FSConfig enableposition = new FSP.AttribEnable(policy, positions.location());
            FSConfig disableposition = new FSP.AttribDisable(policy, positions.location());
            FSConfig enablenormals = new FSP.AttribEnable(policy, normals.location());
            FSConfig disablenormals = new FSP.AttribDisable(policy, normals.location());

            program.addSetupConfig(vp);
            program.addSetupConfig(bright);
            program.addSetupConfig(gam);
            program.addSetupConfig(light);
            program.addSetupConfig(cameraPos);
            program.addSetupConfig(enableposition);
            program.addSetupConfig(enablenormals);

            program.addMeshConfig(material);
            program.addMeshConfig(positions);
            program.addMeshConfig(normals);

            program.addPostDrawConfig(disableposition);
            program.addPostDrawConfig(disablenormals);

            vertex.addAttribute(positions.location(), "vec4", "position");
            vertex.addAttribute(normals.location(), "vec3", "normal");
            vertex.addUniform(vp.location(), "mat4", "vp");
            vertex.addPipedOutputField("vec3", "vnormal");
            vertex.addMainCode("vec4 worldpos = model * position;");
            vertex.addMainCode("gl_Position = vp * worldpos;");
            vertex.addMainCode("vnormal = transpose(inverse(mat3(model))) * normal;");

            fragment.addPrecision("mediump", "float");
            fragment.addStruct("Material", FSLightMaterial.STRUCT_MEMBERS);
            fragment.addStruct("DirectLight", lightsource.getStructMembers());
            fragment.addFunctionCode(gamma.getGammaFunction());
            fragment.addFunctionCode(lightsource.getLightFunction());
            fragment.addUniform(gam.location(), "float", "gamma");
            fragment.addUniform(bright.location(),"float", "brightness");
            fragment.addUniform(light.location(), "DirectLight", "light");
            fragment.addUniform(material.location(), "Material", "material");
            fragment.addUniform(cameraPos.location(), "vec3", "cameraPos");

            fragment.addPipedInputField("vec3", "vnormal");
            fragment.addPipedOutputField("vec4", "fragColor");
            fragment.addMainCode("vec3 normal = normalize(vnormal);");
            fragment.addMainCode("vec3 lightdir = normalize(-light.direction);");

            if(directshadow != null){
                FSConfig shadow = new FSConfigDynamicSelective(directshadow, FSShadowDirect.SELECT_STRUCT_DATA);
                FSConfig shadowlightvp = new FSP.UniformMatrix4fvd(policy, directshadow.lightViewProjection(), 0, 1);
                FSConfig shadowbind = new FSP.TextureBind(policy, directshadow.texture());
                FSConfig shadowunit = new FSP.Uniform1i(policy, directshadow.texture().unit());

                program.registerUniformLocation(fragment, shadow);
                program.registerUniformLocation(vertex, shadowlightvp);
                program.registerUniformLocation(fragment, shadowunit);

                program.addSetupConfig(shadowlightvp);
                program.addSetupConfig(shadowbind);
                program.addSetupConfig(shadowunit);
                program.addSetupConfig(shadow);

                vertex.addUniform(shadowlightvp.location(), "mat4", "shadowlightvp");
                vertex.addPipedOutputField("vec4", "fragPosLightSpace");
                vertex.addMainCode("fragPosLightSpace = shadowlightvp * worldpos;");

                fragment.addPrecision("highp", "sampler2DShadow");
                fragment.addStruct("ShadowDirect", directshadow.getStructMemebers());
                fragment.addFunctionCode(directshadow.getBiasFunction());
                fragment.addFunctionCode(directshadow.getSoftShadowFunction());
                fragment.addUniform(shadow.location(), "ShadowDirect","shadow");
                fragment.addUniform(shadowunit.location(), "sampler2DShadow", "shadowmap");
                fragment.addPipedInputField("vec4", "fragPosLightSpace");

                fragment.addMainCode("float vshadow = shadowMap(fragPosLightSpace, shadowmap, shadowMapBias(normal, lightdir, shadow.minbias, shadow.maxbias), shadow.divident);");
                fragment.addMainCode("vec3 light = directLight(light, material, normal, cameraPos, lightdir, vshadow);");

            }else{
                fragment.addMainCode("vec3 light = directLight(light, material, normal, cameraPos, lightdir, 1.0);");
            }

            fragment.addMainCode("fragColor = vcolor * brightness * vec4(light, 1.0);");
            fragment.addMainCode("correctGamma(fragColor, gamma);");
        }
    }

    public static final class Point extends FSP.Modifier{

        private FSGamma gamma;
        private FSBrightness brightness;
        private FSLightPoint lightsource;
        private FSShadowPoint pointshadow;
        private VLInt samples;
        private int materialglslsize;

        public Point(FSGamma gamma, VLInt samples, FSBrightness brightness, FSLightPoint lightsource, FSShadowPoint pointshadow, int materialglslsize){
            this.samples = samples;
            this.gamma = gamma;
            this.brightness = brightness;
            this.lightsource = lightsource;
            this.pointshadow = pointshadow;
            this.materialglslsize = materialglslsize;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();

            FSConfig positions = new FSP.AttribPointer(policy, FSLoader.ELEMENT_POSITION, 0);
            FSConfig vp = new FSP.UniformMatrix4fvd(policy, FSControl.getViewConfig().viewProjectionMatrix(), 0, 1);
            FSConfig normals = new FSP.AttribPointer(policy, FSLoader.ELEMENT_NORMAL, 0);
            FSConfig material = new FSP.MaterialDynamic(policy, materialglslsize);
            FSConfig cameraPos = new FSP.Uniform3fvd(policy, FSControl.getViewConfig().eyePosition(),0, 1);
            FSConfig shadowbind = new FSP.TextureBind(policy, pointshadow.texture());
            FSConfig bright = new FSConfigDynamic<FSBrightness>(brightness);
            FSConfig gam = new FSConfigDynamic<FSGamma>(gamma);
            FSConfig light = new FSConfigDynamic<FSLightPoint>(lightsource);

            program.registerUniformLocation(vertex, positions);
            program.registerUniformLocation(vertex, vp);
            program.registerUniformLocation(vertex, normals);
            program.registerUniformLocation(fragment, gam);
            program.registerUniformLocation(fragment, bright);
            program.registerUniformLocation(fragment, light);
            program.registerUniformLocation(fragment, material);
            program.registerUniformLocation(fragment, cameraPos);

            FSConfig enableposition = new FSP.AttribEnable(policy, positions.location());
            FSConfig disableposition = new FSP.AttribDisable(policy, positions.location());
            FSConfig enablenormals = new FSP.AttribEnable(policy, normals.location());
            FSConfig disablenormals = new FSP.AttribDisable(policy, normals.location());

            program.addSetupConfig(vp);
            program.addSetupConfig(gam);
            program.addSetupConfig(bright);
            program.addSetupConfig(light);
            program.addSetupConfig(cameraPos);
            program.addSetupConfig(shadowbind);
            program.addSetupConfig(enableposition);
            program.addSetupConfig(enablenormals);

            program.addMeshConfig(positions);
            program.addMeshConfig(material);
            program.addMeshConfig(normals);

            program.addPostDrawConfig(disableposition);
            program.addPostDrawConfig(disablenormals);

            vertex.addAttribute(positions.location(), "vec4", "position");
            vertex.addAttribute(normals.location(), "vec3", "normal");
            vertex.addUniform(vp.location(), "mat4", "vp");
            vertex.addPipedOutputField("vec3", "vnormal");
            vertex.addPipedOutputField("vec3", "fragPos");
            vertex.addMainCode("vec4 worldpos = model * position;");
            vertex.addMainCode("gl_Position = vp * worldpos;");
            vertex.addMainCode("vnormal = transpose(inverse(mat3(model))) * normal;");
            vertex.addMainCode("fragPos = vec3(worldpos);");

            fragment.addPrecision("mediump", "float");
            fragment.addStruct("Material", FSLightMaterial.STRUCT_MEMBERS);
            fragment.addStruct("PointLight", lightsource.getStructMembers());
            fragment.addFunctionCode(gamma.getGammaFunction());
            fragment.addFunctionCode(lightsource.getLightFunction());
            fragment.addUniform(gam.location(), "float", "gamma");
            fragment.addUniform(bright.location(), "float", "brightness");
            fragment.addUniform(light.location(), "PointLight", "light");
            fragment.addUniform(material.location(), "Material", "material");
            fragment.addUniform(cameraPos.location(), "vec3", "cameraPos");
            fragment.addPipedInputField("vec3", "vnormal");
            fragment.addPipedInputField("vec3", "fragPos");
            fragment.addPipedOutputField("vec4", "fragColor");
            fragment.addMainCode("vec3 normal = normalize(vnormal);");
            fragment.addMainCode("vec3 lightdir = normalize(light.position - fragPos);");
            
            if(pointshadow != null){
                FSConfig shadowdata = new FSConfigDynamicSelective(pointshadow, FSShadowDirect.SELECT_STRUCT_DATA);
                FSConfig shadowunit = new FSP.Uniform1i(policy, pointshadow.texture().unit());

                program.registerUniformLocation(fragment, shadowdata);
                program.registerUniformLocation(fragment, shadowunit);

                program.addSetupConfig(shadowdata);
                program.addSetupConfig(shadowunit);

                fragment.addStruct("ShadowPoint", pointshadow.getStructMemebers());
                fragment.addFunctionCode(pointshadow.getBiasFunction());
                fragment.addUniform(shadowdata.location(), "ShadowPoint","shadow");
                fragment.addUniform(shadowunit.location(), "samplerCube", "shadowmap");

                if(samples != null){
                    FSConfig softsamples = new FSConfigDynamic(policy, new FSP.Uniform1i(samples));
                    program.registerUniformLocation(fragment, softsamples);
                    program.addSetupConfig(softsamples);

                    fragment.addFunctionCode(pointshadow.getSoftShadowFunction());
                    fragment.addFieldCode(pointshadow.getShadowPCFSamplingFields());
                    fragment.addUniform(softsamples.location(), "int", "samples");
                    fragment.addMainCode("float vshadow = shadowMap(fragPos, light.position, cameraPos, shadowmap, shadow.zfar, shadowMapBias(normal, lightdir, shadow.minbias, shadow.maxbias), samples, shadow.divident);");

                }else{
                    fragment.addFunctionCode(pointshadow.getShadowFunction());
                    fragment.addMainCode("float vshadow = shadowMap(fragPos, light.position, shadowmap, shadow.zfar, shadowMapBias(normal, lightdir, shadow.minbias, shadow.maxbias), shadow.divident);");
                }

                fragment.addMainCode("vec3 pointlight = pointLight(light, material, normal, fragPos, cameraPos, lightdir, vshadow);");
                fragment.addMainCode("fragColor = vcolor * brightness * vec4(pointlight, 1.0);");
                fragment.addMainCode("correctGamma(fragColor, gamma);");
                
            }else{
                fragment.addMainCode("vec3 pointlight = pointLight(light, material, normal, fragPos, cameraPos, lightdir, 1.0);");
                fragment.addMainCode("fragColor = vcolor * brightness * vec4(pointlight, 1.0);");
                fragment.addMainCode("correctGamma(fragColor, gamma);");
            }
        }
    }
}
