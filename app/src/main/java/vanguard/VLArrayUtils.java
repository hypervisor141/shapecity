package vanguard;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class VLArrayUtils{

    public static void addInPlace(int index, int dynamicsize, byte[] b, byte b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, short[] b, short b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, int[] b, int b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, long[] b, long b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, float[] b, float b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, double[] b, double b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static void addInPlace(int index, int dynamicsize, Object[] b, Object b2){
        spaceOutInPlace(b, dynamicsize, index, 1);
        b[index] = b2;
    }

    public static byte[] slice(byte[] array, int from, int to){
        byte[] sliced = new byte[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static short[] slice(short[] array, int from, int to){
        short[] sliced = new short[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static int[] slice(int[] array, int from, int to){
        int[] sliced = new int[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static long[] slice(long[] array, int from, int to){
        long[] sliced = new long[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static float[] slice(float[] array, int from, int to){
        float[] sliced = new float[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static double[] slice(double[] array, int from, int to){
        double[] sliced = new double[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static Object[] slice(Object[] array, int from, int to){
        Object[] sliced = new Object[from - to];
        System.arraycopy(array, from, sliced, 0, sliced.length);

        return sliced;
    }

    public static byte[] spaceOut(byte[] b, int index, int count){
        byte[] newarray = new byte[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static short[] spaceOut(short[] b, int index, int count){
        short[] newarray = new short[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static int[] spaceOut(int[] b, int index, int count){
        int[] newarray = new int[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static long[] spaceOut(long[] b, int index, int count){
        long[] newarray = new long[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static float[] spaceOut(float[] b, int index, int count){
        float[] newarray = new float[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static double[] spaceOut(double[] b, int index, int count){
        double[] newarray = new double[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static Object[] spaceOut(Object[] b, int index, int count){
        Object[] newarray = new Object[b.length + count];

        System.arraycopy(b, 0, newarray, 0, index);
        System.arraycopy(b, index, newarray, index + count, b.length - index);

        return newarray;
    }

    public static void spaceOutInPlace(Object b, int dynamicsize, int index, int count){
        System.arraycopy(b, index, b, index + count, dynamicsize - index);
    }

    public static int indexOf(short[] b, short element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(int[] b, int element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(long[] b, long element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(float[] b, float element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(double[] b, double element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(Object[] b, Object element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(byte[] b, byte element){
        for(int i = 0; i < b.length; i++){
            if(b[i] == element){
                return i;
            }
        }

        return -1;
    }

    public static byte[] remove(byte[] b, int index, int count){
        byte[] array = new byte[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static short[] remove(short[] b, int index, int count){
        short[] array = new short[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static int[] remove(int[] b, int index, int count){
        int[] array = new int[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static long[] remove(long[] b, int index, int count){
        long[] array = new long[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static float[] remove(float[] b, int index, int count){
        float[] array = new float[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static double[] remove(double[] b, int index, int count){
        double[] array = new double[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static Object[] remove(Object[] b, int index, int count){
        Object[] array = new Object[b.length - count];
        int lastindex = b.length - 1;

        if(index < lastindex && index > 0){
            System.arraycopy(b, 0, array, 0, index);
            System.arraycopy(b, index + count, array, index, array.length - index);

        }else if(index == lastindex){
            System.arraycopy(b, 0, array, 0, array.length);

        }else{
            System.arraycopy(b, count, array, 0, array.length);
        }

        return array;
    }

    public static byte[] remove(byte[] b, byte element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static short[] remove(short[] b, short element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static int[] remove(int[] b, Integer element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static long[] remove(long[] b, long element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static float[] remove(float[] b, float element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static double[] remove(double[] b, double element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static Object[] remove(Object[] b, Object element){
        int index = indexOf(b, element);

        if(index != -1){
            return remove(b, index, 1);

        }else{
            return b;
        }
    }

    public static void removeInPlace(Object b, int index, int count){
        System.arraycopy(b, index + count, b, index, count);
    }

    public static int removeInPlace(byte[] b, byte element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(short[] b, short element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(int[] b, int element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(long[] b, long element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(float[] b, float element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(double[] b, double element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static int removeInPlace(Object[] b, Object element){
        int index = VLArrayUtils.indexOf(b, element);

        if(index != -1){
            removeInPlace(b, index, 1);
        }

        return index;
    }

    public static byte[] resize(byte[] array, int count){
        byte[] resized = new byte[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static short[] resize(short[] array, int count){
        short[] resized = new short[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static int[] resize(int[] array, int count){
        int[] resized = new int[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static long[] resize(long[] array, int count){
        long[] resized = new long[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static float[] resize(float[] array, int count){
        float[] resized = new float[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static double[] resize(double[] array, int count){
        double[] resized = new double[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }

    public static Object[] resize(Object[] array, int count){
        Object[] resized = new Object[count];
        System.arraycopy(array, 0, resized, 0, array.length);

        return resized;
    }
}
