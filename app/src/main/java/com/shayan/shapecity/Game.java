package com.shayan.shapecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES32;
import android.util.Log;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSTexture;
import com.nurverek.firestorm.FSTools;
import com.nurverek.firestorm.Loader;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;

public final class Game{

    private static final int GAME_MATCH_SYMBOLS = 124;
    private static final int GAME_MATCH_COLORS = 125;
    private static final int GAME_MATCH_ROTATION = 126;

    private static final int GAME_MATCHSYM_PICK_LIMIT = 2;
    private static final int GAME_MATCHSYM_REPEAT_ICON_LIMIT = 4;

    private static final int LAYER_PIECE_TEXTURE_DIMENSION = 512;

    protected static final SecureRandom RANDOM = new SecureRandom();
    public static ByteBuffer PIXEL_BUFFER = null;

    protected static int[] symbols;
    protected static boolean[] isactive;

    public static FSTexture texArrayLayer1;
    public static FSTexture texArrayLayer2;
    public static FSTexture texArrayLayer3;
    public static FSTexture[] textures;

    public static VLListInt activatedSymbols;

    public static void initialize(){
        texArrayLayer1 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(Loader.TEXUNIT++));
        texArrayLayer1.bind();
        texArrayLayer1.storage3D(1, GLES32.GL_RGBA8, LAYER_PIECE_TEXTURE_DIMENSION, LAYER_PIECE_TEXTURE_DIMENSION, Loader.LAYER_INSTANCE_COUNT);
        texArrayLayer1.minFilter(GLES32.GL_LINEAR);
        texArrayLayer1.magFilter(GLES32.GL_LINEAR);
        texArrayLayer1.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer1.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer1.baseLevel(0);
        texArrayLayer1.maxLevel(Loader.LAYER_INSTANCE_COUNT - 1);
        texArrayLayer1.unbind();

        FSTools.checkGLError();

        texArrayLayer2 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(Loader.TEXUNIT++));
        texArrayLayer2.bind();
        texArrayLayer2.storage3D(1, GLES32.GL_RGBA8, LAYER_PIECE_TEXTURE_DIMENSION, LAYER_PIECE_TEXTURE_DIMENSION, Loader.LAYER_INSTANCE_COUNT);
        texArrayLayer2.minFilter(GLES32.GL_LINEAR);
        texArrayLayer2.magFilter(GLES32.GL_LINEAR);
        texArrayLayer2.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer2.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer2.baseLevel(0);
        texArrayLayer2.maxLevel(Loader.LAYER_INSTANCE_COUNT - 1);
        texArrayLayer2.unbind();

        FSTools.checkGLError();

        texArrayLayer3 = new FSTexture(new VLInt(GLES32.GL_TEXTURE_2D_ARRAY), new VLInt(Loader.TEXUNIT++));
        texArrayLayer3.bind();
        texArrayLayer3.storage3D(1, GLES32.GL_RGBA8, LAYER_PIECE_TEXTURE_DIMENSION, LAYER_PIECE_TEXTURE_DIMENSION, Loader.LAYER_INSTANCE_COUNT);
        texArrayLayer3.minFilter(GLES32.GL_LINEAR);
        texArrayLayer3.magFilter(GLES32.GL_LINEAR);
        texArrayLayer3.wrapS(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer3.wrapT(GLES32.GL_CLAMP_TO_EDGE);
        texArrayLayer3.baseLevel(0);
        texArrayLayer3.maxLevel(Loader.LAYER_INSTANCE_COUNT - 1);
        texArrayLayer3.unbind();

        FSTools.checkGLError();

        textures = new FSTexture[]{ texArrayLayer1, texArrayLayer2, texArrayLayer3 };
    }

    public static void startGame(){
        Animations.rotateLightSource();
        Camera.rotateCamera();

        RANDOM.setSeed(System.currentTimeMillis());

        int choice = 124 + RANDOM.nextInt(3);
        choice = GAME_MATCH_SYMBOLS;

        switch(choice){
            case GAME_MATCH_SYMBOLS:
                startMatchSymbolsGame();
                break;

            case GAME_MATCH_COLORS:
                startMatchColorsGame();
                break;

            case GAME_MATCH_ROTATION:
                startMatchRotationGame();
                break;

            default:
                new RuntimeException("Invalid game type choice : " + choice);
        }
    }

    private static void startMatchSymbolsGame(){
        activatedSymbols = new VLListInt(Loader.LAYER_INSTANCE_COUNT, 0);
        activatedSymbols.virtualSize(Loader.LAYER_INSTANCE_COUNT);

        isactive = new boolean[Loader.LAYER_INSTANCE_COUNT];

        Animations.raiseBases(1);
        Animations.raiseBases(2);

        Animations.standBy(0);
        Animations.standBy(1);

        activateMatchSymForLayer(2);
    }

    private static void startMatchColorsGame(){

    }

    private static void startMatchRotationGame(){

    }

    private static void prepareMatchSymTextureForLayer(FSTexture texArrayLayer){
        Bitmap b = null;

        int[] resources = new int[]{ R.drawable.circle, R.drawable.hex, R.drawable.square, R.drawable.triangle, R.drawable.rsquare };

        int[] timespicked = new int[resources.length];
        Arrays.fill(timespicked, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        Context cxt = FSControl.getContext();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.outConfig = Bitmap.Config.ARGB_8888;
        opts.inScaled = true;
        opts.inMutable = true;

        int choice = 0;
        int requiredchoices = Loader.LAYER_INSTANCE_COUNT / GAME_MATCHSYM_PICK_LIMIT;
        int index = 0;

        PIXEL_BUFFER = null;
        texArrayLayer.bind();

        symbols = new int[Loader.LAYER_INSTANCE_COUNT];
        Arrays.fill(symbols, -1);

        for(int i = 0; i < requiredchoices; i++){
            choice = RANDOM.nextInt(resources.length);

            while(timespicked[choice] >= GAME_MATCHSYM_REPEAT_ICON_LIMIT){
                choice = RANDOM.nextInt(resources.length);
            }

            timespicked[choice]++;

            b = BitmapFactory.decodeResource(cxt.getResources(), resources[choice], opts);

            if(PIXEL_BUFFER == null){
                PIXEL_BUFFER = ByteBuffer.allocate(b.getAllocationByteCount());
                PIXEL_BUFFER.order(ByteOrder.nativeOrder());
            }

            PIXEL_BUFFER.position(0);

            b.copyPixelsToBuffer(PIXEL_BUFFER);
            b.recycle();

            for(int i2 = 0; i2 < GAME_MATCHSYM_PICK_LIMIT; i2++){
                index = RANDOM.nextInt(Loader.LAYER_INSTANCE_COUNT);

                while(symbols[index] != -1){
                    index = RANDOM.nextInt(Loader.LAYER_INSTANCE_COUNT);
                }

                symbols[index] = choice;

                PIXEL_BUFFER.position(0);
                texArrayLayer.subImage3D(0, 0, 0, index, LAYER_PIECE_TEXTURE_DIMENSION, LAYER_PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
            }
        }

        FSTools.checkGLError();
        texArrayLayer.unbind();
    }

    private static void activateMatchSymForLayer(final int layer){
        prepareMatchSymTextureForLayer(textures[layer]);

        Arrays.fill(activatedSymbols.array(), -1);
        Arrays.fill(isactive, true);

        Animations.reveal(layer);
        Animations.revealRepeat(layer);

        final FSMesh layermesh = Loader.layers[layer];

        Input.activateInputListeners(layermesh, new Runnable(){

            @Override
            public void run(){
                final int target = Input.closestPoint.instanceindex;

                if(isactive[target]){
                    activatedSymbols.set(target, symbols[target]);

                    Animations.revealResetTimer();
                    Animations.reveal(layer, target, new Runnable(){

                        @Override
                        public void run(){
                            activatedSymbols.set(target, -1);
                        }
                    });

                    if(getActiveSymbolCount() >= GAME_MATCHSYM_PICK_LIMIT){
                        int match = checkSymbolMatch();

                        if(match != -1){
                            int counter = 0;
                            int indexbounce = 0;
                            int indexblink = 0;
                            int indextexblink = 0;

                            VLArrayFloat linkdata = ((ModColor.TextureControlLink)layermesh.link(0)).data;

                            for(int i = 0; i < activatedSymbols.size(); i++){
                                if(activatedSymbols.get(i) == match){
                                    isactive[i] = false;
                                    activatedSymbols.set(i, -1);

                                    Animations.deactivatePiece(layer, i);
                                    linkdata.set(i, Animations.TEXCONTROL_ACTIVE);

                                    counter++;

                                    if(counter >= GAME_MATCHSYM_PICK_LIMIT){
                                        break;
                                    }
                                }
                            }

                            if(checkLayerFinished()){
                                if(layer == 0){
                                    Log.d("wtf", "ALL DONE");

                                }else{
                                    Animations.lowerBases(layer, new Runnable(){

                                        @Override
                                        public void run(){
                                            int nextlayer = layer - 1;

                                            Animations.removeDeactivationControl();
                                            Animations.unstandBy(nextlayer);

                                            activateMatchSymForLayer(nextlayer);
                                        }
                                    });
                                }
                            }

                            linkdata.sync();
                        }
                    }
                }
            }
        });
    }

    private static int checkSymbolMatch(){
        int count = 0;
        int sym = 0;

        for(int i = 0; i < symbols.length; i++){
            sym = symbols[i];

            for(int i2 = 0; i2 < activatedSymbols.size(); i2++){
                if(sym == activatedSymbols.get(i2)){
                    count++;

                    if(count >= GAME_MATCHSYM_PICK_LIMIT){
                        return sym;
                    }
                }
            }

            count = 0;
        }

        return -1;
    }

    private static boolean checkLayerFinished(){
        int size = isactive.length;

        for(int i = 0; i < size; i++){
            if(isactive[i]){
                return false;
            }
        }

        return true;
    }

    private static int getActiveSymbolCount(){
        int sym = 0;
        int activecount = 0;

        for(int i = 0; i < activatedSymbols.size(); i++){
            sym = activatedSymbols.get(i);

            if(sym != -1){
                activecount++;
            }
        }

        return activecount;
    }

    public static void destroy(){
        texArrayLayer1.destroy();
        texArrayLayer2.destroy();
        texArrayLayer3.destroy();
    }
}
