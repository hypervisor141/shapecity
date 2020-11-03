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

public class VLTools{

    public static final int LOCATION_MID_LEFT = 2351;
    public static final int LOCATION_MID_RIGHT = 2352;
    public static final int LOCATION_MID_CENTER = 2353;
    public static final int LOCATION_TOP_LEFT = 2354;
    public static final int LOCATION_TOP_RIGHT = 2355;
    public static final int LOCATION_TOP_CENTER = 2356;
    public static final int LOCATION_BOTTOM_LEFT = 2357;
    public static final int LOCATION_BOTTOM_CENTER = 2358;
    public static final int LOCATION_BOTTOM_RIGHT = 2359;

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

    public static Bitmap generateTextBitmap(Context cxt, String text, int dpisize, int color, int textcolor,
                                            boolean bold, int width, int height, int location, Bitmap.Config config){
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();

        Bitmap b = Bitmap.createBitmap(metrics, width, height, config);
        Canvas c = new Canvas(b);

        TextPaint paint = new TextPaint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpisize, metrics));
        paint.setColor(textcolor);
        paint.setAntiAlias(true);
        paint.setDither(false);
        if(bold){
            paint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        }else{
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        }

        float textw = paint.measureText(text, 0, text.length());
        float texth = paint.descent() + paint.ascent();

        c.drawColor(color);

        if(location == LOCATION_TOP_LEFT){
            c.drawText(text, 0, 0, paint);

        }else if(location == LOCATION_TOP_CENTER){
            c.drawText(text, width / 2f - textw / 2f, 0, paint);

        }else if(location == LOCATION_TOP_RIGHT){
            c.drawText(text, width - textw, 0, paint);

        }else if(location == LOCATION_MID_LEFT){
            c.drawText(text, 0, height / 2f - texth / 2f, paint);

        }else if(location == LOCATION_MID_CENTER){
            c.drawText(text, width / 2f - textw / 2f, height / 2 - texth / 2f, paint);

        }else if(location == LOCATION_MID_RIGHT){
            c.drawText(text, width - textw, height / 2f - texth / 2f, paint);

        }else if(location == LOCATION_BOTTOM_LEFT){
            c.drawText(text, 0, height - texth, paint);

        }else if(location == LOCATION_BOTTOM_CENTER){
            c.drawText(text, width / 2f - textw / 2f, height - texth, paint);

        }else if(location == LOCATION_BOTTOM_RIGHT){
            c.drawText(text, width - textw, height - texth, paint);
        }

        return b;
    }

    public static void addTextOverBitmap(Context cxt, Bitmap b, String text, int dip, int textcolor, boolean bold, int location){
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();
        Canvas c = new Canvas(b);

        int width = b.getWidth();
        int height = b.getHeight();

        TextPaint paint = new TextPaint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics));
        paint.setColor(textcolor);
        paint.setAntiAlias(true);
        paint.setDither(false);
        if(bold){
            paint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        }else{
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        }

        float textw = paint.measureText(text, 0, text.length());
        float texth = paint.descent() + paint.ascent();

        if(location == LOCATION_TOP_LEFT){
            c.drawText(text, 0, 0, paint);

        }else if(location == LOCATION_TOP_CENTER){
            c.drawText(text, width / 2f - textw / 2f, 0, paint);

        }else if(location == LOCATION_TOP_RIGHT){
            c.drawText(text, width - textw, 0, paint);

        }else if(location == LOCATION_MID_LEFT){
            c.drawText(text, 0, height / 2f - texth / 2f, paint);

        }else if(location == LOCATION_MID_CENTER){
            c.drawText(text, width / 2f - textw / 2f, height / 2 - texth / 2f, paint);

        }else if(location == LOCATION_MID_RIGHT){
            c.drawText(text, width - textw, height / 2f - texth / 2f, paint);

        }else if(location == LOCATION_BOTTOM_LEFT){
            c.drawText(text, 0, height - texth, paint);

        }else if(location == LOCATION_BOTTOM_CENTER){
            c.drawText(text, width / 2f - textw / 2f, height - texth, paint);

        }else if(location == LOCATION_BOTTOM_RIGHT){
            c.drawText(text, width - textw, height - texth, paint);
        }
    }

    public static Bitmap getCircleCroppedBitmap(Bitmap bitmap, Bitmap.Config config){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getCenterCroppedBitmap(Bitmap bitmap, int width, int height, Bitmap.Config config){
        Bitmap output = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(output);

        int diffw = (bitmap.getWidth() - width) / 2;
        int diffh = (bitmap.getHeight() - height) / 2;

        final Paint paint = new Paint();
        final Rect src = new Rect(diffw, diffh, diffw + width, diffh + height);
        final Rect dst = new Rect(0, 0, output.getWidth(), output.getHeight());
        canvas.drawBitmap(bitmap, src, dst, paint);

        return output;
    }

    public static void addBackgroundToBitmap(Bitmap bitmap, int color){
        Canvas c = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        c.drawColor(color);
        c.drawBitmap(bitmap, 0, 0, paint);
    }

    public static void addTriangleFanBorderToBitmap(Bitmap bitmap, float xradius, float yradius, float radianoffset, int sidecount, float strokewidth, int color){
        Canvas c = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokewidth);
        paint.setColor(color);

        float xoffset = bitmap.getWidth() / 2;
        float yoffset = bitmap.getHeight() / 2;

        double cycle = 2 * Math.PI / sidecount;
        double x, y;

        Path path = new Path();

        for(int i = 0; i < sidecount + 1; i++){
            x = xoffset + xradius * Math.cos(radianoffset + (cycle * i));
            y = yoffset + yradius * Math.sin(radianoffset + (cycle * i));

            if(Math.abs(x) < 0.00001f){
                x = 0;
            }
            if(Math.abs(y) < 0.00001f){
                y = 0;
            }

            if(i == 0){
                path.moveTo((float)x, (float)y);
            }

            path.lineTo((float)x, (float)y);
        }

        c.drawPath(path, paint);
    }

    public static void addBitmapOverBitmap(Bitmap bitmap, Bitmap bitmap2){
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        c.drawBitmap(bitmap2, 0, 0, paint);
    }

    public static Bitmap replaceBitmapColor(Context cxt, Bitmap bitmap, int[] targets, int[] replacement){
        Bitmap result = convertBitmapToMutable(cxt, bitmap);

        int[] pixels = new int[result.getWidth() * result.getHeight()];
        result.getPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        int replace = Color.argb(replacement[0], replacement[1], replacement[2], replacement[3]);

        for(int index = 0; index < pixels.length; index++){
            int color = pixels[index];

            for(int i = 0; i < targets.length; i++){
                if(color == targets[i]){
                    pixels[index] = replace;
                    break;
                }
            }
        }

        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static Bitmap convertBitmapToMutable(final Context context, final Bitmap bitmap) {
        final int width = bitmap.getWidth(), height = bitmap.getHeight();
        final Bitmap.Config type = bitmap.getConfig();
        File outputFile = null;
        final File outputDir = context.getCacheDir();

        try{
            outputFile = File.createTempFile(Long.toString(System.currentTimeMillis()), null, outputDir);
            outputFile.deleteOnExit();
            RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile, "rw");

            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, bitmap.getRowBytes() * height);

            bitmap.copyPixelsToBuffer(map);
            bitmap.recycle();
            map.position(0);

            Bitmap result = Bitmap.createBitmap(width, height, type);
            result.copyPixelsFromBuffer(map);

            channel.close();
            randomAccessFile.close();
            outputFile.delete();

            return result;
        }catch(final Exception e){

        }finally{
            if(outputFile != null){
                outputFile.delete();
            }
        }

        return null;
    }

    public static void hideKeyboard(Activity act){
        InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideSystemUI(Activity act){
        act.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void showSystemUI(Activity act){
        act.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
