package vanguard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class VLTimer{

    private static long NANOTIME;
    private static long ACCUMULATETIME;
    private static String TARGET;

    public static void startTiming(String target){
        TARGET = target;
        NANOTIME = System.nanoTime();
    }

    public static String finishAccumulateTime(String prefix, String tag){
        String str = "(" + prefix + ")" + "ACCUMULATE(" + ACCUMULATETIME + "ns, " + (long)Math.floor(ACCUMULATETIME / 1000000f) + "ms) ";
        ACCUMULATETIME = 0;

        if(tag != null){
            Log.d(tag, str);
        }

        return str;
    }

    public static String finishTiming(String prefix, String tag, boolean accumulate){
        long time = System.nanoTime();
        long diff = time - NANOTIME;
        String str = "(" + prefix + ")" + TARGET + "(" + diff + "ns, " + (long)Math.floor(diff / 1000000f) + "ms) ";

        if(accumulate){
            ACCUMULATETIME += diff;
        }

        if(tag != null){
            Log.d(tag, str);
        }

        return str;
    }

    public static long timeFunction(Runnable task, int testcount, int reportpercetile, boolean log){
        long time;
        long diff;
        long avg = 0;
        long max = -Long.MAX_VALUE;
        long min = Long.MAX_VALUE;
        long testperc = 0;
        long threshold = reportpercetile == 0 ? 0 : (long)(testcount * (reportpercetile / 100f));

        if(log && threshold != 0){
            Log.w("wtf", "Test progress : " + testperc + "%");
        }

        for(int e = 0; e < testcount; e++){
            time = System.nanoTime();
            task.run();
            diff = System.nanoTime() - time;

            if(max < diff){
                max = diff;
            }

            if(min > diff){
                min = diff;
            }

            if(e == 0){
                avg = diff;

            }else{
                avg = (avg + diff) / 2;
            }

            if(log && threshold != 0 && e != 0 && e % threshold == 0){
                testperc += reportpercetile;
                Log.w("wtf", "Test progress : " + testperc + "%");
            }
        }

        if(log){
            Log.w("wtf", "Test progress : 100%");
            Log.w("wtf", "Avg : " + avg + "ns (" + (avg / 1000000f) + "ms)"
                    + " Max : " + max + "ns (" + (max / 1000000f) + "ms)"
                    + " Min : " + min + "ns (" + (min / 1000000f) + "ms)");
        }

        return avg;
    }

    public static ByteBuffer makeDirectByteBuffer(int capacity){
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity);
        bb.order(ByteOrder.nativeOrder());
        bb.position(0);

        return bb;
    }
}
