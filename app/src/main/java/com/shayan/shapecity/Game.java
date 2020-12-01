package com.shayan.shapecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES32;

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
    protected static boolean[] activated;

    public static FSTexture texArrayLayer1;
    public static FSTexture texArrayLayer2;
    public static FSTexture texArrayLayer3;
    
    public static VLListInt activePieces;

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
    }

    public static void startGame(){
        Animation.rotateLightSource();
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
        Bitmap b = null;

        int[] resources = new int[]{
                R.drawable.circle,
                R.drawable.hex,
                R.drawable.square,
                R.drawable.triangle,
                R.drawable.rsquare
        };

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
        texArrayLayer3.bind();

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
                texArrayLayer3.subImage3D(0, 0, 0, index, LAYER_PIECE_TEXTURE_DIMENSION, LAYER_PIECE_TEXTURE_DIMENSION, 1, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, PIXEL_BUFFER);
            }
        }

        FSTools.checkGLError();
        texArrayLayer3.unbind();

        activePieces = new VLListInt(Loader.LAYER_INSTANCE_COUNT, 0);
        activePieces.virtualSize(Loader.LAYER_INSTANCE_COUNT);

        activated = new boolean[Loader.LAYER_INSTANCE_COUNT];

        activateMatchSymForLayer();
    }

    private static void startMatchColorsGame(){

    }

    private static void startMatchRotationGame(){

    }

    private static void activateMatchSymForLayer(){
        Arrays.fill(activePieces.array(), -1);
        Arrays.fill(activated, true);

        final int layer = 2;

        Animation.clearRaiseBaseProcessors();

        Animation.raiseBases(1);
        Animation.raiseBases(2);

        Animation.clearStandbyProcessors();

        Animation.standBy(0);
        Animation.standBy(1);

        Animation.clearDeactivationProcessors();
        Animation.clearRevealProcessors();

        Animation.reveal(layer);
        Animation.revealRepeat(layer);

        final FSMesh layermesh = Loader.layers[layer];

        Input.activateInputListeners(layermesh, new Runnable(){

            @Override
            public void run(){
                final int target = Input.closestPoint.instanceindex;

                if(activated[target]){
                    activePieces.set(target, symbols[target]);

                    Animation.clearRevealProcessors();
                    Animation.reveal(layer, target);

                    if(getActiveSymbolCount() >= GAME_MATCHSYM_PICK_LIMIT){
                        int match = checkSymbolMatch();

                        if(match != -1){
                            int counter = 0;
                            int indexbounce = 0;
                            int indexblink = 0;
                            int indextexblink = 0;

                            VLArrayFloat linkdata = ((ModColor.TextureControlLink)layermesh.link(0)).data;

                            for(int i = 0; i < activePieces.size(); i++){
                                if(activePieces.get(i) == match){
                                    activated[i] = false;
                                    activePieces.set(i, -1);

                                    Animation.deactivatePiece(layer * Loader.LAYER_INSTANCE_COUNT + i);
                                    linkdata.set(i, Animation.TEXCONTROL_ACTIVE);

                                    counter++;

                                    if(counter >= GAME_MATCHSYM_PICK_LIMIT){
                                        break;
                                    }
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

            for(int i2 = 0; i2 < activePieces.size(); i2++){
                if(sym == activePieces.get(i2)){
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

    private static int getActiveSymbolCount(){
        int sym = 0;
        int activecount = 0;

        for(int i = 0; i < activePieces.size(); i++){
            sym = activePieces.get(i);

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
