package com.shayan.shapecity;

import android.util.Log;

import com.nurverek.vanguard.VLArrayFloat;
import com.nurverek.vanguard.VLListInt;

import java.util.Arrays;

public final class Game{

    private static final int GAME_MATCH_SYMBOLS = 0;
    private static final int GAME_MATCH_COLORS = 1;
    private static final int GAME_MATCH_ROTATION = 2;

    public static final int GAME_MATCHSYM_PICK_LIMIT = 2;
    public static final int GAME_MATCHSYM_REPEAT_ICON_LIMIT = 4;

    public static VLListInt activatedSymbols;
    public static boolean[] enabledPieces;
    public static boolean[] revealedPieces;
    private static int[] symbols;

    public static void initialize(Gen gen){
        Platform.initialize(gen);
        Puzzle.initialize(gen);
        City.initialize(gen);
        Light.initialize(gen);
        Camera.initialize(gen);
    }

    public static void startGame(Gen gen){
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
        revealedPieces = new boolean[BPLayer.INSTANCE_COUNT];

        symbols = gen.bppieces.prepareMatchSymTexture(gen.pieces);

        Arrays.fill(activatedSymbols.array(), -1);
        Arrays.fill(enabledPieces, true);
        Arrays.fill(revealedPieces, false);

        Puzzle.revealRepeat();

        Input.activateInputListeners(gen.pieces, new Runnable(){

            @Override
            public void run(){
                final int target = Input.closestPoint.instanceindex;
                int count = getActiveSymbolCount();

                if(enabledPieces[target] && !revealedPieces[target] && count < GAME_MATCHSYM_PICK_LIMIT){
                    activatedSymbols.set(target, symbols[target]);
                    revealedPieces[target] = true;

                    count++;

                    if(count >= GAME_MATCHSYM_PICK_LIMIT){
                        int match = checkSymbolMatch();

                        if(match != -1){
                            int counter = 0;
                            int indexbounce = 0;
                            int indexblink = 0;
                            int indextexblink = 0;

                            VLArrayFloat linkdata = ((ModColor.TextureControlLink)gen.pieces.link(0)).data;

                            for(int i = 0; i < activatedSymbols.size(); i++){
                                if(activatedSymbols.get(i) == match){
                                    enabledPieces[i] = false;
                                    activatedSymbols.set(i, -1);

                                    Puzzle.deactivate(i);
                                    linkdata.set(i, Puzzle.TEXCONTROL_ACTIVE);

                                    counter++;

                                    if(counter >= GAME_MATCHSYM_PICK_LIMIT){
                                        break;
                                    }
                                }
                            }

                            if(checkFinished()){
                                Log.d("wtf", "ALL DONE");
                            }

                            linkdata.sync();

                        }else{
                            reveal(target);
                        }

                    }else{
                        reveal(target);
                    }
                }
            }
        });
    }

    private static void startMatchColorsGame(){

    }

    private static void startMatchRotationGame(){

    }

    private static void reveal(int index){
        Puzzle.revealResetTimer();
        Puzzle.reveal(index, new Runnable(){

            @Override
            public void run(){
                revealedPieces[index] = false;
                activatedSymbols.set(index, -1);
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

    private static boolean checkFinished(){
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

            if(sym != -1 && enabledPieces[i]){
                activecount++;
            }
        }

        return activecount;
    }
}
