package com.shayan.shapecity;

import android.opengl.GLES32;

import com.nurverek.firestorm.FSConfig;
import com.nurverek.firestorm.FSConfigDynamicSelective;
import com.nurverek.firestorm.FSConfigSelective;
import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSFrameBuffer;
import com.nurverek.firestorm.FSGenerator;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSP;
import com.nurverek.firestorm.FSRenderer;
import com.nurverek.firestorm.FSShader;
import com.nurverek.firestorm.FSViewConfig;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLDebug;
import com.nurverek.vanguard.VLFloat;
import com.nurverek.vanguard.VLInt;

public final class ModDepthMap{

    public static final class Prepare extends FSP.Modifier{

        private FSFrameBuffer framebuffer;
        private VLInt width;
        private VLInt height;
        private boolean cullfrontface;

        public Prepare(FSFrameBuffer framebuffer, VLInt width, VLInt height, boolean cullfrontface){
            this.framebuffer = framebuffer;
            this.width = width;
            this.height = height;
            this.cullfrontface = cullfrontface;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            final boolean cull = cullfrontface;

            program.addSetupConfig(new FSConfig(policy){

                @Override
                public void configure(FSP program, FSMesh mesh, int meshindex, int passindex){
                    FSViewConfig config = FSControl.getViewConfig();

                    config.viewPort(0, 0, width.get(), height.get());
                    config.updateViewPort();

                    framebuffer.bind();

                    FSRenderer.clear(GLES32.GL_DEPTH_BUFFER_BIT);
                    FSRenderer.colorMask(false, false, false, false);

                    if(cull){
                        FSRenderer.cullFace(GLES32.GL_FRONT);
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

    public static final class SetupDirect extends FSP.Modifier{

        private VLArrayFloat lightViewProjection;

        public SetupDirect(VLArrayFloat lightViewProjection){
            this.lightViewProjection = lightViewProjection;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();

            FSConfig position = new FSP.AttribPointer(policy, FSGenerator.ELEMENT_POSITION, 0);
            FSConfig lighttransform = new FSP.UniformMatrix4fvd(policy, lightViewProjection, 0, 1);
            
            program.registerAttributeLocation(vertex, position);
            program.registerUniformLocation(vertex, lighttransform);

            FSConfig positionenable = new FSP.AttribEnable(policy, position.location());
            FSConfig positiondisable = new FSP.AttribEnable(policy, position.location());

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

    public static final class SetupPoint extends FSP.Modifier{

        private FSConfigSelective transformsrc;
        private VLArrayFloat cubeposition;
        private VLFloat zfar;
        private int selection;

        public SetupPoint(FSConfigSelective transformsrc, int selection, VLArrayFloat cubeposition, VLFloat zfar){
            this.transformsrc = transformsrc;
            this.selection = selection;
            this.cubeposition = cubeposition;
            this.zfar = zfar;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();
            FSShader geomtry = program.initializeGeometryShader();

            FSConfig transforms = new FSConfigDynamicSelective(transformsrc, selection);
            FSConfig position = new FSP.AttribPointer(policy, FSGenerator.ELEMENT_POSITION, 0);
            FSConfig far = new FSP.Uniform1f(policy, zfar);
            FSConfig cubepos = new FSP.Uniform3fvd(policy, cubeposition, 0, 1);

            program.registerAttributeLocation(vertex, position);
            program.registerUniformArrayLocation(geomtry, 6, transforms);
            program.registerUniformLocation(fragment, far);
            program.registerUniformLocation(fragment, cubepos);

            FSConfig enableposition = new FSP.AttribEnable(policy, position.location());
            FSConfig disableposition = new FSP.AttribDisable(policy, position.location());

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

    public static final class Finish extends FSP.Modifier{

        private FSFrameBuffer framebuffer;

        public Finish(FSFrameBuffer framebuffer){
            this.framebuffer = framebuffer;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            program.addPostDrawConfig(new FSConfig(policy){

                @Override
                public void configure(FSP program, FSMesh mesh, int meshindex, int passindex){
                    FSViewConfig config = FSControl.getViewConfig();

                    config.viewPort(0, 0, FSControl.getContainerWidth(), FSControl.getContainerHeight());
                    config.updateViewPort();

                    framebuffer.unbind();
                    
                    FSRenderer.colorMask(true, true, true, true);
                    FSRenderer.cullFace(GLES32.GL_BACK);
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
