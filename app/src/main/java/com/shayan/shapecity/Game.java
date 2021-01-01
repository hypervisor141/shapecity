package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListInt;

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

    public static void startGame(Gen gen){
        PuzzleAnimations.initialize(gen);
        Phase1.initialize(gen);
        Phase2.initialize(gen);
        Phase3.initialize(gen);
        Outbase.initialize(gen);
        Light.initialize(gen);
        Camera.initialize(gen);

        gen.vManager().start();

        Gen.RANDOM.setSeed(System.currentTimeMillis());

        int choice = Gen.RANDOM.nextInt(3);
        choice = GAME_MATCH_SYMBOLS;

        switch(choice){
            case GAME_MATCH_SYMBOLS:
                startMatchSymbolsGame(gen);
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

    private static void startMatchSymbolsGame(Gen gen){
        activatedSymbols = new VLListInt(BPLayer.INSTANCE_COUNT, 0);
        activatedSymbols.virtualSize(BPLayer.INSTANCE_COUNT);

        enabledPieces = new boolean[BPLayer.INSTANCE_COUNT];

        PuzzleAnimations.raiseBases(1);
        PuzzleAnimations.raiseBases(2);

        PuzzleAnimations.standBy(0);
        PuzzleAnimations.standBy(1);

        activateMatchSymForLayer(gen, 2);
    }

    private static void startMatchColorsGame(){

    }

    private static void startMatchRotationGame(){

    }

    private static void activateMatchSymForLayer(Gen gen, final int layer){
        final FSMesh mesh = gen.layers[layer];
        symbols = gen.bplayer.prepareMatchSymTexture(mesh);

        Arrays.fill(activatedSymbols.array(), -1);
        Arrays.fill(enabledPieces, true);

        PuzzleAnimations.revealRepeat(layer);

        Input.activateInputListeners(mesh, new Runnable(){

            @Override
            public void run(){
                final int target = Input.closestPoint.instanceindex;
                int activecount = getActiveSymbolCount();

                if(enabledPieces[target] && activecount < 2){
                    activatedSymbols.set(target, symbols[target]);
                    activecount++;

                    PuzzleAnimations.revealResetTimer();
                    PuzzleAnimations.reveal(layer, target, new Runnable(){

                        @Override
                        public void run(){
                            activatedSymbols.set(target, -1);
                        }
                    });

                    if(activecount >= GAME_MATCHSYM_PICK_LIMIT){
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

                                    PuzzleAnimations.deactivate(layer, i);
                                    linkdata.set(i, PuzzleAnimations.TEXCONTROL_ACTIVE);

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
                                    final int nextlayer = layer - 1;

                                    PuzzleAnimations.unstandBy(nextlayer);
                                    PuzzleAnimations.lowerBases(layer, new Runnable(){

                                        @Override
                                        public void run(){
                                            PuzzleAnimations.removeDeactivationControl();
                                            activateMatchSymForLayer(gen, nextlayer);
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
