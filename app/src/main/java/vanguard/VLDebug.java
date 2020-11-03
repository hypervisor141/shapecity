package vanguard;

import android.util.Log;

import firestorm.FSControl;

public final class VLDebug{

    private static StringBuilder BUILDER = new StringBuilder();
    private static String[] LINES;
    private static String TAG = "VLDEBUG";
    
    public static void append(Object s){
        BUILDER.append(s);
    }

    public static void append(String s){
        BUILDER.append(s);
    }

    public static void append(StringBuffer s){
        BUILDER.append(s);
    }

    public static void append(CharSequence s){
        BUILDER.append(s);
    }

    public static void append(CharSequence s, int start, int end){
        BUILDER.append(s, start, end);
    }

    public static void append(char[] s){
        BUILDER.append(s);
    }

    public static void append(char[] s, int offset, int length){
        BUILDER.append(s, offset, length);
    }

    public static void append(boolean s){
        BUILDER.append(s);
    }

    public static void append(char s){
        BUILDER.append(s);
    }

    public static void append(int s){
        BUILDER.append(s);
    }

    public static void append(long s){
        BUILDER.append(s);
    }

    public static void append(float s){
        BUILDER.append(s);
    }

    public static void append(double s){
        BUILDER.append(s);
    }
    
    public static StringBuilder get(){
        return BUILDER;
    }

    public static void recreate(){
        BUILDER = new StringBuilder(BUILDER.capacity());
    }

    public static void tag(String tag){
        TAG = tag;
    }

    public static final void printDirect(String text){
        Log.d(FSControl.LOGTAG, text);
    }

    public static final void printD(){
        LINES = BUILDER.toString().split("\n");

        for(int i = 0; i < LINES.length; i++){
            Log.d(FSControl.LOGTAG, LINES[i]);
        }

        recreate();
    }

    public static final void printE(){
        LINES = BUILDER.toString().split("\n");

        for(int i = 0; i < LINES.length; i++){
            Log.e(FSControl.LOGTAG, LINES[i]);
        }

        recreate();
    }

    public static void release(){
        BUILDER = null;
    }
}
