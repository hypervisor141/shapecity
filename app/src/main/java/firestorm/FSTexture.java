package firestorm;

import android.graphics.Bitmap;
import android.opengl.GLES32;
import android.util.Log;

import java.nio.Buffer;

import vanguard.VLFactory;
import vanguard.VLInt;

public class FSTexture extends VLFactory{

    public static int LIB_BIND = 0;
    public static int LIB_UPLOAD_UNIT = 1;

    private VLInt target;
    private VLInt texunit;
    private int id;
    
    public FSTexture(VLInt target, VLInt texunit){
        initialize(target, texunit);
    }


    public void initialize(VLInt target, VLInt texunit){
        this.target = target;
        this.texunit = texunit;

        id = FSRenderer.createTexture(1)[0];
    }

    public void activateUnit(){
        FSRenderer.textureActive(GLES32.GL_TEXTURE0 + texunit.get());
    }

    public void bind(){
        FSRenderer.textureBind(target.get(), id);
    }

    public void unbind(){
        FSRenderer.textureBind(target.get(), 0);
    }

    public void loadBitmap(int level, Bitmap bitmap){
        android.opengl.GLUtils.texImage2D(target.get(), level, bitmap, 0);
        bitmap.recycle();
    }

    public void loadImage2D(int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels){
        FSRenderer.texImage2D(target.get(), level, internalformat, width, height, border, format, type, pixels);
    }

    public void loadStorage2D(int levels, int internalformat, int width, int height){
        FSRenderer.texStorage2D(target.get(), levels, internalformat, width, height);
    }

    public void loadCubemapBitmap(int level, Bitmap right, Bitmap left, Bitmap top, Bitmap bottom, Bitmap front, Bitmap back){
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_X, level, right, 0);
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, level, left, 0);
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, level, top, 0);
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, level, bottom, 0);
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, level, front, 0);
        android.opengl.GLUtils.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, level, back, 0);

        right.recycle();
        left.recycle();
        top.recycle();
        bottom.recycle();
        front.recycle();
        back.recycle();
    }

    public void loadCubemapBuffer(int level, int internalformat, int width, int height,
                                   int border, int format, int type, Buffer face1, Buffer face2,
                                   Buffer face3, Buffer face4, Buffer face5, Buffer face6){

        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_X, level, internalformat, width, height, border, format, type, face1);
        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, level, internalformat, width, height, border, format, type, face2);
        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, level, internalformat, width, height, border, format, type, face3);
        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, level, internalformat, width, height, border, format, type, face4);
        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, level, internalformat, width, height, border, format, type, face5);
        FSRenderer.texImage2D(GLES32.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, level, internalformat, width, height, border, format, type, face6);
    }

    public void generateMipMap(){
        FSRenderer.generateMipMap(target.get());
    }
    
    public void wrapS(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_WRAP_S, mode);
    }

    public void wrapT(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_WRAP_T, mode);
    }

    public void wrapR(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_WRAP_R, mode);
    }

    public void minFilter(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_MIN_FILTER, mode);
    }

    public void magFilter(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_MAG_FILTER, mode);
    }

    public void compareMode(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_COMPARE_MODE, mode);
    }

    public void compareFunc(int mode){
        FSRenderer.textureParameteri(target.get(), GLES32.GL_TEXTURE_COMPARE_FUNC, mode);
    }

    public int id(){
        return id;
    }

    public VLInt target(){
        return target;
    }

    public VLInt unit(){
        return texunit;
    }

    public void destroy(){
        target = null;
        texunit = null;

        FSRenderer.deleteTextures(new int[]{ id });
    }
}
