package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSConfigDynamicSelective;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSDimensions;
import com.nurverek.firestorm.FSFrameBuffer;
import com.nurverek.firestorm.FSG;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSPMod;
import com.nurverek.firestorm.FSR;
import com.nurverek.firestorm.FSShader;
import com.nurverek.firestorm.FSShadow;
import com.nurverek.firestorm.FSShadowDirect;
import com.nurverek.firestorm.FSShadowPoint;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLDebug;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;

public final class ModShadow{

    public static final class Prepare implements FSPMod{

        private FSShadow shadow;
        private boolean cullfrontface;

        public Prepare(FSShadow shadow, boolean cullfrontface){
            this.shadow = shadow;
            this.cullfrontface = cullfrontface;
        }

        @Override
        public void modify(FSP program){
            final boolean cull = cullfrontface;

            program.addSetupConfig(new FSConfig(FSConfig.POLICY_ALWAYS){

                @Override
                public void configure(FSP program, FSMesh mesh, int meshindex, int passindex){
                    FSViewConfig config = FSControl.getViewConfig();

                    config.viewPort(0, 0, shadow.width().get(), shadow.height().get());
                    config.updateViewPort();

                    shadow.frameBuffer().bind();

                    FSR.clear(GLES32.GL_DEPTH_BUFFER_BIT);
                    FSR.colorMask(false, false, false, false);

                    if(cull){
                        FSR.cullFace(GLES32.GL_FRONT);
                    }
                }

                @Override
                public int getGLSLSize(){
                    return 0;
                }

                @Override
                public void debugInfo(FSP program, FSMesh mesh, int debug){
                    VLDebug.append("LIB_DEPTHMAP_PREPARE");
                }
            });
        }
    }

    public static final class SetupDirect implements FSPMod{

        private FSShadowDirect shadow;

        public SetupDirect(FSShadowDirect shadow){
            this.shadow = shadow;
        }

        @Override
        public void modify(FSP program){
            FSShader vertex = program.vertexShader();

            FSConfig position = new FSP.AttribPointer(FSConfig.POLICY_ALWAYS, FSG.ELEMENT_POSITION, 0);
            FSConfig lighttransform = new FSP.UniformMatrix4fvd(FSConfig.POLICY_ALWAYS, shadow.lightViewProjection(), 0, 1);
            
            program.registerAttributeLocation(vertex, position);
            program.registerUniformLocation(vertex, lighttransform);

            FSConfig positionenable = new FSP.AttribEnable(FSConfig.POLICY_ALWAYS, position.location());
            FSConfig positiondisable = new FSP.AttribEnable(FSConfig.POLICY_ALWAYS, position.location());

            program.addSetupConfig(positionenable);
            program.addSetupConfig(lighttransform);
            program.addMeshConfig(position);
            program.addPostDrawConfig(positiondisable);

            vertex.addAttribute(position.location(), "vec4", "position");
            vertex.addUniform(lighttransform.location(), "mat4", "lighttransform");
            vertex.addMainCode("gl_Position = lighttransform * model * position;");

            program.fragmentShader().addPrecision("mediump", "float");
        }
    }

    public static final class SetupPoint implements FSPMod{

        private FSShadowPoint shadow;

        public SetupPoint(FSShadowPoint shadow){
            this.shadow = shadow;
        }

        @Override
        public void modify(FSP program){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();
            FSShader geomtry = program.initializeGeometryShader();

            VLArrayFloat cubeposition = shadow.light().position();

            FSConfig transforms = new FSConfigDynamicSelective(shadow, FSShadowPoint.SELECT_LIGHT_TRANSFORMS);
            FSConfig position = new FSP.AttribPointer(FSConfig.POLICY_ALWAYS, FSG.ELEMENT_POSITION, 0);
            FSConfig far = new FSP.Uniform1f(FSConfig.POLICY_ALWAYS, shadow.zFar());
            FSConfig cubepos = new FSP.Uniform3fvd(FSConfig.POLICY_ALWAYS, cubeposition, 0, 1);

            program.registerAttributeLocation(vertex, position);
            program.registerUniformArrayLocation(geomtry, 6, transforms);
            program.registerUniformLocation(fragment, far);
            program.registerUniformLocation(fragment, cubepos);

            FSConfig enableposition = new FSP.AttribEnable(FSConfig.POLICY_ALWAYS, position.location());
            FSConfig disableposition = new FSP.AttribDisable(FSConfig.POLICY_ALWAYS, position.location());

            program.addSetupConfig(transforms);
            program.addSetupConfig(far);
            program.addSetupConfig(cubepos);
            program.addSetupConfig(enableposition);
            program.addMeshConfig(position);
            program.addPostDrawConfig(disableposition);

            vertex.addAttribute(position.location(), "vec4", "position");
            vertex.addMainCode("gl_Position = model * position;");

            geomtry.addUniformArray(transforms.location(), "mat4", "transforms", 6);
            geomtry.addLayoutIn("triangles");
            geomtry.addLayoutOut("triangle_strip, max_vertices=18");
            geomtry.addFieldCode("out vec4 fragPos;");
            geomtry.addFunction("void", "emitFace", new String[]{ "mat4 transform" }, new String[]{
                    "for(int i = 0; i < 3; i++){",
                    "   fragPos = gl_in[i].gl_Position;",
                    "   gl_Position = transform * fragPos;",
                    "   EmitVertex();",
                    "}",
                    "EndPrimitive();",
            });
            geomtry.addMainCode("gl_Layer = 0;");
            geomtry.addMainCode("emitFace(transforms[0]);");
            geomtry.addMainCode("gl_Layer = 1;");
            geomtry.addMainCode("emitFace(transforms[1]);");
            geomtry.addMainCode("gl_Layer = 2;");
            geomtry.addMainCode("emitFace(transforms[2]);");
            geomtry.addMainCode("gl_Layer = 3;");
            geomtry.addMainCode("emitFace(transforms[3]);");
            geomtry.addMainCode("gl_Layer = 4;");
            geomtry.addMainCode("emitFace(transforms[4]);");
            geomtry.addMainCode("gl_Layer = 5;");
            geomtry.addMainCode("emitFace(transforms[5]);");

            fragment.addUniform(cubepos.location(), "vec3", "pos");
            fragment.addUniform(far.location(), "float", "zfar");
            fragment.addPrecision("mediump", "float");
            fragment.addPipedInputField("vec4", "fragPos");
            fragment.addMainCode("gl_FragDepth = length(fragPos.xyz - pos) / zfar;");
        }
    }

    public static final class Finish implements FSPMod{

        private FSShadow shadow;

        public Finish(FSShadow shadow){
            this.shadow = shadow;
        }

        @Override
        public void modify(FSP program){
            program.addPostDrawConfig(new FSConfig(FSConfig.POLICY_ALWAYS){

                @Override
                public void configure(FSP program, FSMesh mesh, int meshindex, int passindex){
                    FSViewConfig config = FSControl.getViewConfig();

                    config.viewPort(0, 0, FSDimensions.getSurfaceWidth(), FSDimensions.getSurfaceHeight());
                    config.updateViewPort();

                    shadow.frameBuffer().unbind();
                    
                    FSR.colorMask(true, true, true, true);
                    FSR.cullFace(GLES32.GL_BACK);
                }

                @Override
                public int getGLSLSize(){
                    return 0;
                }

                @Override
                public void debugInfo(FSP program, FSMesh mesh, int debug){
                    VLDebug.append("LIB_DEPTHMAP_END");
                }
            });
        }
    }
}
