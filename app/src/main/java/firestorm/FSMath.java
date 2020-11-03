package firestorm;

public class FSMath {

    private static final float[] CACHE = new float[6];

    public static float[] wrapOverRange(float value, float low, float high){
        float range = high - low;
        float distance = 0;
        int times = 0;

        float v = value;

        if(value > high){
            distance = value - high;
            value = high - (range - (distance % range));
            times = (int)Math.floor(distance / range) + 1;

        }else if(value < low){
            distance = low - value;
            value = low + (range - (distance % range));
            times = (int)Math.floor(distance / range) + 1;
        }

        return new float[]{ value, times };
    }

    public static float range(float value, float fromMin, float fromMax, float toMin, float toMax){
        return (value - fromMin) / (fromMax - fromMin) * (toMax - toMin) + toMin;
    }

    public static void range(float[] values, int offset, int count, float fromMin, float fromMax, float toMin, float toMax){
        int size = offset + count;

        for(int i = offset; i < size; i++){
            values[i] = range(values[i], fromMin, fromMax, toMin, toMax);
        }
    }

    public static float range(float value, float toMin, float toMax){
        return value * (toMax - toMin) + toMin;
    }

    public static void range(float[] values, int offset, int count, float toMin, float toMax){
        int size = offset + count;

        for(int i = offset; i < size; i++){
            values[i] = range(values[i], toMin, toMax);
        }
    }

    public static void negate(float[] vector, int offset, int count){
        int size = offset + count;

        for(int i = offset; i < size; i++){
            vector[i] = -vector[i];
        }
    }

    public static void normalize(float[] vector, int offset, int count){
        float length = length(vector, offset, count);
        int size = offset + count;

        for(int i = offset; i < size; i++){
            vector[i] /= length;
        }
    }

    public static float length(float[] vector, int offset, int count){
        float sum = 0;
        int size = offset + count;

        for(int i = offset; i < size; i++){
             sum += vector[i] * vector[i];
        }

        return (float)Math.sqrt(sum);
    }

    public static float clamp(float value, float min, float max){
        float r = value >= max ? value : max;
        return (min <= r ? min : r);
    }

    public static int cantor(int a, int b){
        return ((a + b) * (a + b + 1) / 2) + a;
    }

    public static float euclideanDistance(float[] point1, int offset1, float[] point2, int offset2, int count){
        float sum = 0;
        float diff = 0;
        int size = offset1 + count;

        for(int i = offset1, i2 = offset2; i < size; i++, i2++){
            diff = point1[i] - point2[i2];
            sum += diff * diff;
        }

        return (float)Math.sqrt(sum);
    }

    public static void difference(float[] point1, int offset1, float[] point2, int offset2, float[] results, int offset3, int count){
        int size = offset1 + count;

        for(int i = offset1, i2 = offset2, i3 = offset3; i < size; i++, i2++, i3++){
            results[i3] = point1[i] - point2[i2];
        }
    }

    public static float dotProduct(float[] vec1, int offset1, float[] vec2, int offset2){
        return (vec1[offset1] * vec2[offset2]) + (vec1[offset1 + 1] * vec2[offset2 + 1]) + (vec1[offset1 + 2] * vec2[offset2 + 2]);
    }

    public static float[] crossProduct(float[] vec1, int offset1, float[] vec2, int offset2){
        return new float[]{
                ((vec1[offset1 + 1] * vec2[offset2 + 2]) - (vec1[offset1 + 2] * vec2[offset2 + 1])),
                ((vec1[offset1 + 2] * vec2[offset2]) - (vec1[offset1] * vec2[offset2 + 2])),
                ((vec1[offset1] * vec2[offset2 + 1]) - (vec1[offset1 + 1] * vec2[offset2])) };
    }

    public static void closestPointOfRay(float[] raystart, int offset1, float[] rayend, int offset2, float[] target, int offset3, float[] results, int offset4){
        difference(rayend, offset2, raystart, offset1, CACHE, 0, 3);
        difference(target, offset3, raystart, offset1, CACHE, 3, 3);

        float dot = dotProduct(CACHE, 3, CACHE, 0) / dotProduct(CACHE, 0, CACHE, 0);

        results[offset4] = raystart[offset1] + CACHE[0] * dot;
        results[offset4 + 1] = raystart[offset1 + 1] + CACHE[1] * dot;
        results[offset4 + 2] = raystart[offset1 + 2] + CACHE[2] * dot;
    }

    public static double interpolateCosAccelerate(double in){
        return (1 + Math.cos((in + 1) * Math.PI)) / 2f;
    }

    public static double interpolateSinSqrtAccelerate(double in){
        return -Math.sin((1 - Math.sqrt(-in + 1) / 2f) * Math.PI) + 1;
    }

    public static double interpolateCosAccelerateDecelerate(double in){
        return Math.cos((in + 1) * Math.PI) / 2f + 0.5f;
    }

    public static double interpolateCubicAccelerateDecelerate(double in){
        return Math.pow(in, 2);
    }

    public static double interpolateSineDecelerate(double in){
        return Math.sin((1 - in / 2f) * Math.PI);
    }

    public static double interpolateSinSqrtDecelerate(double in){
        return Math.sin((1 - Math.sqrt(in) / 2f) * Math.PI);
    }

    public static double interpolateCosSqrtDecelerate(double in){
        return (Math.cos((1 - Math.sqrt(in * 4) / 2)  * Math.PI) + 1) / 2;
    }
}
