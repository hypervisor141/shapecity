package com.shayan.hive;

import firestorm.FSConfig;
import firestorm.FSLoader;
import firestorm.FSP;
import firestorm.FSShader;

public final class ModModel{

    public static final class Uniform extends FSP.Modifier{

        public Uniform(){
            
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();

            FSConfig model = new FSP.UniformMatrix4fve(policy,0, FSLoader.ELEMENT_MODEL, 0, 1);
            
            program.registerUniformLocation(vertex, model);
            program.addMeshConfig(model);

            vertex.addUniform(model.location(), "mat4","modelin");
            vertex.addMainCode("mat4 model = modelin;");
        }
    }

    public static final class UBO extends FSP.Modifier{

        private int segments;
        private int instancecount;

        public UBO(int segments, int instancecount){
            this.segments = segments;
            this.instancecount = instancecount;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();

            if(segments == 1){
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL", 0));

                vertex.addUniformBlock("std140", "MODEL", "mat4 models[" + instancecount + "]");
                vertex.addMainCode("mat4 model = models[gl_InstanceID];");

            }else if(segments == 2){
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL1", 0));
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL2", 1));

                instancecount = instancecount / 2 + (instancecount % 2 == 0 ? 0 : 1);

                vertex.addUniformBlock("std140", "MODEL1", "vec4 models1[" + instancecount + "]", "vec4 models2[" + instancecount + "]");
                vertex.addUniformBlock("std140", "MODEL2", "vec4 models3[" + instancecount + "]", "vec4 models4[" + instancecount + "]");

                vertex.addMainCode("int index = gl_InstanceID / 2;");
                vertex.addMainCode("mat4 model = mat4(models1[index], models1[index], models2[index], models3[index]);");

            }else if(segments == 4){
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL1", 0));
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL2", 1));
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL3", 2));
                program.addMeshConfig(new FSP.UniformBlockElement(policy,FSLoader.ELEMENT_MODEL, "MODEL4", 3));

                vertex.addUniformBlock("std140", "MODEL1", "vec4 models1[" + instancecount + "]");
                vertex.addUniformBlock("std140", "MODEL2", "vec4 models2[" + instancecount + "]");
                vertex.addUniformBlock("std140", "MODEL3", "vec4 models3[" + instancecount + "]");
                vertex.addUniformBlock("std140", "MODEL4", "vec4 models4[" + instancecount + "]");

                vertex.addMainCode("mat4 model = mat4(models1[gl_InstanceID], models2[gl_InstanceID], models3[gl_InstanceID], models4[gl_InstanceID]);");

            }else{
                throw new RuntimeException("Invalid hints : segment[" + segments + "] instance-per-segment[" + instancecount + "].");
            }
        }
    }
}
