package firestorm;

import android.opengl.EGL14;
import android.opengl.GLES32;
import android.opengl.GLU;

public final class FSTools{

    public static void checkGLError(){
        if(FSControl.DEBUG_MODE){
            int error;

            while((error = GLES32.glGetError()) != GLES32.GL_NO_ERROR){
                throw new RuntimeException(GLU.gluErrorString(error));
            }
        }
    }

    public static void checkEGLError(String name){
        if(FSControl.DEBUG_MODE){
            int error;

            while((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS){
                throw new RuntimeException(name + " : EGLError " + error);
            }
        }
    }

    public static float[] createVerticesForTriangleFan(int sidecount, float radius, float radianoffset, float zvalue, boolean Wpadding){
        double cycle = 2 * Math.PI / sidecount;
        float[] coords;

        if(Wpadding){
            coords = new float[(sidecount + 1) * 4];

            coords[0] = 0;
            coords[1] = 0;
            coords[2] = zvalue;
            coords[3] = 1;

            for(int i = 1, i2 = 0; i < sidecount + 1; i++, i2++){
                double x, y;

                x = radius * Math.cos(radianoffset + (cycle * i2));
                y = radius * Math.sin(radianoffset + (cycle * i2));

                if(Math.abs(x) < 0.00001f){
                    x = 0;
                }
                if(Math.abs(y) < 0.00001f){
                    y = 0;
                }

                int index = (i * 4);

                coords[index] = (float)x;
                coords[index + 1] = (float)y;
                coords[index + 2] = zvalue;
                coords[index + 3] = 1;
            }

        }else{
            coords = new float[(sidecount + 1) * 3];

            coords[0] = 0;
            coords[1] = 0;
            coords[2] = zvalue;

            for(int i = 1, i2 = 0; i < sidecount + 1; i++, i2++){
                double x, y;

                x = radius * Math.cos(radianoffset + (cycle * i2));
                y = radius * Math.sin(radianoffset + (cycle * i2));

                if(Math.abs(x) < 0.00001f){
                    x = 0;
                }
                if(Math.abs(y) < 0.00001f){
                    y = 0;
                }

                int index = (i * 3);

                coords[index] = (float)x;
                coords[index + 1] = (float)y;
                coords[index + 2] = zvalue;
            }
        }

        return coords;
    }

    public static short[] createIndicesForTriangleFan(int sidecount){
        short[] indices = new short[sidecount + 2];

        for(short i = 0; i < indices.length - 1; i++){
            indices[i] = i;
        }

        indices[indices.length - 1] = 1;

        return indices;
    }

    public static float[] createTextureCoordsForTriangleFan(int sidecount){
        double cycle = Math.PI * 2f / sidecount;
        float[] texturecoords = new float[(sidecount + 1) * 2];
        texturecoords[0] = 0.5f;
        texturecoords[1] = 0.5f;

        for(int i = 1, i2 = sidecount; i < sidecount + 1; i++, i2--){
            double x, y;

            x = 0.5f + 0.5f * Math.cos(cycle * i2);
            y = 0.5f + 0.5f * Math.sin(cycle * i2);

            texturecoords[(i * 2)] = (float)x;
            texturecoords[(i * 2) + 1] = (float)y;
        }

        return texturecoords;
    }

    public static float[] createTextureCoordsForTriangleFanVertices(float[] vertices, boolean wpadded){
        float[] coords;
        int jumps;

        if(wpadded){
            coords = new float[vertices.length / 2];
            jumps = 4;

        }else{
            coords = new float[(vertices.length / 3) * 2];
            jumps = 3;
        }

        float x, y;
        float minx = Float.MAX_VALUE;
        float miny = Float.MAX_VALUE;
        float maxx = -Float.MAX_VALUE;
        float maxy = -Float.MAX_VALUE;

        for(int i = 0; i < vertices.length; i += jumps){
            x = vertices[i];
            y = vertices[i + 1];

            if(maxx < x){
                maxx = x;
            }
            if(minx > x){
                minx = x;
            }

            if(maxy < y){
                maxy = y;
            }
            if(miny > y){
                miny = y;
            }
        }

        for(int i = 0, i2 = 0; i < vertices.length; i += jumps, i2 += 2){
            coords[i2] = FSMath.range(vertices[i], minx, maxx, 0, 1);
            coords[i2 + 1] = FSMath.range(vertices[i + 1], miny, maxy, 0, 1);
        }

        return coords;
    }
}
