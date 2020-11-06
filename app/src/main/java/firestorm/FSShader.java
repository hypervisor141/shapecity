package firestorm;

import android.opengl.GLES32;
import android.util.Log;

import androidx.annotation.NonNull;

import vanguard.VLStringify;

public final class FSShader implements VLStringify {

    private static final String HEADER = "#version 320 es\n";
    private static final String MAINHEADER = "void main(){\n";
    private static final String MAINCLOSURE = "}\n";

    protected StringBuilder headers;
    protected StringBuilder blocks;
    protected StringBuilder fields;
    protected StringBuilder pipedfields;
    protected StringBuilder main;
    protected StringBuilder functions;

    protected String shadername;
    protected String finalsrc;

    protected int type;
    protected int shaderid;
    protected int attriblocation;

    protected boolean compiled;

    public FSShader(int type){
        this.type = type;

        compiled = false;

        headers = new StringBuilder(HEADER);
        blocks = new StringBuilder();
        fields = new StringBuilder();
        pipedfields = new StringBuilder();
        main = new StringBuilder(MAINHEADER);
        functions = new StringBuilder();

        attriblocation = 0;

        if(FSControl.DEBUG_MODE){
            shadername = resolveShaderName(type);
        }
    }


    public void stringify(StringBuilder src, Object hint){
        buildSource();

        int line = 1;
        int currentindex = 0;
        int newlineindex = 0;

        while(true){
            newlineindex = finalsrc.indexOf("\n", currentindex);

            if(newlineindex < 0){
                break;
            }

            src.append(line);
            src.append("\t\t");
            src.append(finalsrc.substring(currentindex, newlineindex + 1));

            currentindex = newlineindex + 1;
            line++;
        }
    }

    public int type(){
        return type;
    }

    public int id(){
        return shaderid;
    }

    public String src(){
        return finalsrc;
    }


    public final int nextAttribLocation(int glslsize){
        int location = attriblocation;
        attriblocation += glslsize;

        return location;
    }

    public void addAttribute(int location, String type, String name){
        fields.append("layout(location=");
        fields.append(location);
        fields.append(") in ");
        fields.append(type);
        fields.append(" ");
        fields.append(name);
        fields.append(";\n");
    }

    public void addUniform(int location, String type, String name){
        fields.append("layout(location=");
        fields.append(location);
        fields.append(") uniform ");
        fields.append(type);
        fields.append(" ");
        fields.append(name);
        fields.append(";\n");
    }

    public void addUniformArray(int location, String type, String name, int arraysize){
        fields.append("layout(location=");
        fields.append(location);
        fields.append(") uniform ");
        fields.append(type);
        fields.append("[");
        fields.append(arraysize);
        fields.append("] ");
        fields.append(name);
        fields.append(";\n");
    }

    public void addAttributeArray(int location, String type, String name, int arraysize){
        fields.append("layout(location=");
        fields.append(location);
        fields.append(") in ");
        fields.append(type);
        fields.append("[");
        fields.append(arraysize);
        fields.append("] ");
        fields.append(name);
        fields.append(";\n");
    }

    public void addUniformBlock(String layouttype, String name, @NonNull String... members){
        fields.append("layout(");
        fields.append(layouttype);
        fields.append(") uniform ");
        fields.append(name);
        fields.append("{\n");

        for(int i = 0; i < members.length; i++){
            fields.append("\t");
            fields.append(members[i]);
            fields.append(";\n");
        }

        fields.append("};\n");
    }

    public void addBufferBlock(String layouttype, int bindpoint, String name, @NonNull String... members){
        fields.append("layout(");
        fields.append(layouttype);
        fields.append(", binding = ");
        fields.append(bindpoint);
        fields.append(") buffer ");
        fields.append(name);
        fields.append("{\n");

        for(int i = 0; i < members.length; i++){
            fields.append("\t");
            fields.append(members[i]);
            fields.append(";\n");
        }

        fields.append("};\n");
    }

    public void addPrecision(String precision, String type){
        headers.append("precision ");
        headers.append(precision);
        headers.append(" ");
        headers.append(type);
        headers.append(";\n");
    }

    public void addLayoutIn(String type){
        headers.append("layout(");
        headers.append(type);
        headers.append(") in;");
    }

    public void addLayoutOut(String type){
        headers.append("layout(");
        headers.append(type);
        headers.append(") out;");
    }

    public void addStruct(String name, String... members){
        blocks.append("struct ");
        blocks.append(name);
        blocks.append("{\n");

        for(int i = 0; i < members.length; i++){
            blocks.append("\t");
            blocks.append(members[i]);
            blocks.append(";\n");
        }

        blocks.append("};\n");
    }

    public void addOutputBlock(String name, String varname, @NonNull String... members){
        blocks.append("out ");
        blocks.append(name);
        blocks.append("{\n");

        for(int i = 0; i < members.length; i++){
            blocks.append("\t");
            blocks.append(members[i]);
            blocks.append(";\n");
        }

        if(varname == null){
            blocks.append("};\n");

        }else{
            blocks.append("} ");
            blocks.append(varname);
            blocks.append(";\n");
        }
    }

    public void addInputBlock(String name, String varname, @NonNull String... members){
        blocks.append("in ");
        blocks.append(name);
        blocks.append("{\n");

        for(int i = 0; i < members.length; i++){
            blocks.append("\t");
            blocks.append(members[i]);
            blocks.append(";\n");
        }

        if(varname == null){
            blocks.append("};\n");

        }else{
            blocks.append("} ");
            blocks.append(varname);
            blocks.append(";\n");
        }
    }

    public void addPipedInputField(String type, String name){
        pipedfields.append("in ");
        pipedfields.append(type);
        pipedfields.append(" ");
        pipedfields.append(name);
        pipedfields.append(";\n");
    }

    public void addPipedOutputField(String type, String name){
        pipedfields.append("out ");
        pipedfields.append(type);
        pipedfields.append(" ");
        pipedfields.append(name);
        pipedfields.append(";\n");
    }

    public void addFunction(String returntype, String name, String[] params, String[] lines){
        functions.append(returntype);
        functions.append(" ");
        functions.append(name);
        functions.append("(");

        int last = params.length - 1;

        for(int i = 0; i < last; i++){
            functions.append(params[i]);
            functions.append(", ");
        }

        functions.append(params[last]);
        functions.append("){\n");

        for(int i = 0; i < lines.length; i++){
            functions.append(lines[i]);
            functions.append("\n");
        }

        functions.append("}\n\n");
    }

    public void addHeaderCode(String code){
        headers.append(code);
        headers.append("\n");
    }

    public void addBlocksCode(String code){
        blocks.append(code);
        blocks.append("\n");
    }

    public void addFieldCode(String code){
        fields.append(code);
        fields.append("\n");
    }

    public void addPipedFieldCode(String code){
        pipedfields.append(code);
        pipedfields.append("\n");
    }

    public void addFunctionCode(String func){
        functions.append(func);
        functions.append("\n\n");
    }

    public void addMainCode(String code){
        main.append("\t");
        main.append(code);
        main.append("\n");
    }

    public void buildSource(){
        StringBuilder builder = new StringBuilder();

        builder.append(headers);
        builder.append("\n");
        builder.append(blocks);
        builder.append("\n");
        builder.append(fields);
        builder.append("\n");
        builder.append(pipedfields);
        builder.append("\n");
        builder.append(functions);
        builder.append(main);
        builder.append(MAINCLOSURE);

        finalsrc = builder.toString();
    }

    public void compile(){
        if(!compiled){
            shaderid = GLES32.glCreateShader(type);
            FSTools.checkGLError();

            GLES32.glShaderSource(shaderid, finalsrc);
            FSTools.checkGLError();

            GLES32.glCompileShader(shaderid);
            FSTools.checkGLError();

            compiled = true;
        }
    }

    public void attach(FSP program){
        GLES32.glAttachShader(program.id(), shaderid);
        FSTools.checkGLError();
    }

    public void detach(FSP program){
        GLES32.glDetachShader(program.id(), shaderid);
        FSTools.checkGLError();
    }

    public void delete(){
        GLES32.glDeleteShader(shaderid);
        FSTools.checkGLError();
    }

    public void logDebugInfo(FSP program){
        String info = GLES32.glGetShaderInfoLog(shaderid);
        Log.d(FSControl.LOGTAG, "Shader " + shaderid + " info for type " + type + " for program " + program.id() + " : " + (info.isEmpty() ? "Success" : info));
    }

    private static String resolveShaderName(int gltype){
        switch (gltype){
            case GLES32.GL_VERTEX_SHADER:
                return "VERTEX";

            case GLES32.GL_GEOMETRY_SHADER:
                return "GEOMETRY";

            case GLES32.GL_TESS_CONTROL_SHADER:
                return "TESSEL_CONTROL";

            case GLES32.GL_TESS_EVALUATION_SHADER:
                return "TESSEL_EVAL";

            case GLES32.GL_FRAGMENT_SHADER:
                return "FRAGMENT";

            case GLES32.GL_COMPUTE_SHADER:
                return "COMPUTE";

            default:
                throw new RuntimeException("No such shader : " + gltype);
        }
    }
}
