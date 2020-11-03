package com.shayan.hive;

import firestorm.FSConfig;
import firestorm.FSLoader;
import firestorm.FSP;
import firestorm.FSShader;

public final class ModColor {

    public static final class Uniform extends FSP.Modifier{

        public Uniform(){
            
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader fragment = program.fragmentShader();
            FSConfig color = new FSP.Uniform4fve(policy, 0, FSLoader.ELEMENT_COLOR, 0, 1);

            program.registerUniformLocation(fragment, color);

            program.addMeshConfig(color);
            fragment.addUniform(color.location(), "vec4", "vcolor");
        }
    }

    public static final class UBO extends FSP.Modifier{

        private int segments;
        private int instancecount;

        public UBO(int segments, int instancecount){
            this.segments = segments;
            this.instancecount = instancecount / segments;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();

            if(segments == 1){
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS", 0));

                vertex.addUniformBlock("std140","COLORS", "vec4 colors[" + instancecount + "]");
                vertex.addMainCode("vcolor = colors[gl_InstanceID];");

            }else if(segments == 2){
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS1", 0));
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS2", 1));

                vertex.addUniformBlock("std140","COLORS1", "vec2 colors1[" + instancecount + "]");
                vertex.addUniformBlock("std140","COLORS2", "vec2 colors2[" + instancecount + "]");

                vertex.addMainCode("vcolor = vec4(colors1[gl_InstanceID], colors2[gl_InstanceID]);");

            }else if(segments == 4){
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS1", 0));
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS2", 1));
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS3", 2));
                program.addMeshConfig(new FSP.UniformBlockElement(policy, FSLoader.ELEMENT_COLOR, "COLORS4", 3));

                vertex.addUniformBlock("std140","COLORS1", "vec1 colors1[" + instancecount + "]");
                vertex.addUniformBlock("std140","COLORS2", "vec1 colors2[" + instancecount + "]");
                vertex.addUniformBlock("std140","COLORS3", "vec1 colors3[" + instancecount + "]");
                vertex.addUniformBlock("std140","COLORS4", "vec1 colors4[" + instancecount + "]");

                vertex.addMainCode("vcolor = vec4(colors1[gl_InstanceID], colors2[gl_InstanceID], colors3[gl_InstanceID], colors4[gl_InstanceID]);");

            }else{
                throw new RuntimeException("Invalid hints : segment[" + segments + "] instance-per-segment[" + instancecount + "].");
            }

            vertex.addPipedOutputField("vec4", "vcolor");
            fragment.addPipedInputField("vec4", "vcolor");
        }
    }

    public static final class Texture extends FSP.Modifier{

        private int segments;
        private int instancecount;

        public Texture(int segments, int instancecount){
            this.segments = segments;
            this.instancecount = instancecount / segments;
        }

        @Override
        protected void modify(FSP program, FSConfig.Policy policy){
            FSShader vertex = program.vertexShader();
            FSShader fragment = program.fragmentShader();

            FSConfig coords = new FSP.AttribPointer(policy, FSLoader.ELEMENT_TEXCOORD, 0);
            FSConfig unit = new FSP.TextureColorUnit(policy);
            FSConfig texturebind = new FSP.TextureColorBind(policy);

            program.registerAttributeLocation(vertex, coords);
            program.registerUniformLocation(fragment, unit);

            FSConfig enabletexcoords = new FSP.AttribEnable(policy, coords.location());
            FSConfig disabletexcoords = new FSP.AttribDisable(policy, coords.location());

            program.addSetupConfig(enabletexcoords);
            program.addMeshConfig(texturebind);
            program.addMeshConfig(unit);
            program.addMeshConfig(coords);
            program.addPostDrawConfig(disabletexcoords);

            vertex.addPipedOutputField("vec2", "vcolortexcoord");
            vertex.addAttribute(coords.location(), "vec2", "colortexcoords");
            vertex.addMainCode("vcolortexcoord = colortexcoords;");

            fragment.addUniform(unit.location(),"sampler2D", "colortexture");
            fragment.addPipedInputField("vec2", "vcolortexcoord");
            fragment.addMainCode("vec4 vcolor = texture(colortexture, vcolortexcoord);");
        }
    }
}
