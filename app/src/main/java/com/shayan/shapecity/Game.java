package com.shayan.shapecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES32;
import android.util.Log;

import com.nurverek.firestorm.FSControl;
import com.nurverek.firestorm.FSM;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.firestorm.FSTexture;
import com.nurverek.firestorm.FSTools;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLInt;
import com.nurverek.vanguard.VLListInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;

public final class Game{

    private static final int GAME_MATCH_SYMBOLS = 0;
    private static final int GAME_MATCH_COLORS = 1;
    private static final int GAME_MATCH_ROTATION = 2;

    public static final int GAME_MATCHSYM_PICK_LIMIT = 2;
    public static final int GAME_MATCHSYM_REPEAT_ICON_LIMIT = 4;

    public static boolean[] enabledPieces;

    public static VLListInt activatedSymbols;
    private static int[] symbols;

    public static void startGame(Loader loader){
        Animations.initialize(loader);

        Light.rotateDirectLight();
        Light.rotatePointLight();

        Camera.rotateCamera();

        Loader.RANDOM.setSeed(System.currentTimeMillis());

        int choice = Loader.RANDOM.nextInt(3);
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
        activatedSymbols = new VLListInt(Layer.INSTANCE_COUNT, 0);
        activatedSymbols.virtualSize(Layer.INSTANCE_COUNT);

        enabledPieces = new boolean[Layer.INSTANCE_COUNT];

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

    private static void activateMatchSymForLayer(final int layer){
        symbols = Loader.layers[layer].prepareMatchSymTexture();

        Arrays.fill(activatedSymbols.array(), -1);
        Arrays.fill(enabledPieces, true);

        Animations.revealRepeat(layer);

        final FSMesh mesh = Loader.layers[layer].mesh();

        Input.activateInputListeners(mesh, new Runnable(){

            @Override
            public void run(){
                final int target = Input.closestPoint.instanceindex;

                if(enabledPieces[target]){
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

                            VLArrayFloat linkdata = ((ModColor.TextureControlLink)mesh.link(0)).data;

                            for(int i = 0; i < activatedSymbols.size(); i++){
                                if(activatedSymbols.get(i) == match){
                                    enabledPieces[i] = false;
                                    activatedSymbols.set(i, -1);

                                    Animations.deactivate(layer, i);
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
        int size = enabledPieces.length;

        for(int i = 0; i < size; i++){
            if(enabledPieces[i]){
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
}
