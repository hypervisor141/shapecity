package vanguard;

import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class VLIOUtils {

    private static final int FILE_HASH_SIZE = 32;
    private static final int BUFFER_SIZE = 256000;

    public static boolean replaceRangeInFile(RandomAccessFile f, long from, long count, byte[] data){
        try{
            if(count > 0){
                FileChannel c = f.getChannel();
                long diff = data.length - count;

                if(diff == 0){
                    c.position(from);
                    f.write(data);

                }else{
                    long end = from + count;
                    relocateSegmentInFileBuffered(f, end, from + data.length, c.size() - end, BUFFER_SIZE);

                    c.position(from);
                    f.write(data);
                    f.setLength(f.length() + diff);
                }

                return true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean relocateSegmentInFileBuffered(RandomAccessFile f, long from, long to, long count, int buffersize){
        try{
            if(count <= buffersize){
                relocateSegmentInFile(f, from, to, count);

            }else{
                FileChannel c = f.getChannel();
                long chunk = buffersize;
                long srcend = from + count;
                long readpos = from;
                long writepos = to;
                long left = count;
                boolean confliction = to < srcend && to > (from + chunk);

                if(confliction){
                    readpos = srcend - 1;
                    writepos = to + count - 1;

                }else{
                    readpos = from;
                    writepos = to;
                }

                while(left > 0){
                    if(confliction){
                        left -= relocateSegmentInFile(f, readpos, writepos, -chunk);
                        readpos -= chunk;
                        writepos -= chunk;

                    }else{
                        left -= relocateSegmentInFile(f, readpos, writepos, chunk);
                        readpos += chunk;
                        writepos += chunk;
                    }

                    if(left >= buffersize){
                        chunk = buffersize;

                    }else{
                        chunk = left;
                    }
                }
            }

            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public static int relocateSegmentInFile(RandomAccessFile f, long from, long to, long count){
        if(count != 0){
            try{
                FileChannel c = f.getChannel();
                byte[] data = new byte[(int) Math.abs(count)];

                if(count < 0){
                    c.position(from + count);
                    f.readFully(data);
                    c.position(to + count);
                    f.write(data);

                }else{
                    c.position(from);
                    f.readFully(data);
                    c.position(to);
                    f.write(data);
                }

                return data.length;
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return -1;
    }

    public static void bufferedCopyToStream(InputStream in, OutputStream out, int buffersize, long left){
        try{
            byte[] data = new byte[buffersize];
            byte[] active;

            while(left > 0){
                int done = 0;

                if(left > buffersize){
                    active = data;

                }else{
                    active = new byte[buffersize];
                }

                done = in.read(active);
                out.write(active, 0, done);
                left -= done;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void getFilesWithExtention(ArrayList<File> results, File dir, String extention){
        File[] subs = dir.listFiles();

        if(subs != null){
            for(int i = 0; i < subs.length; i++){
                File f = subs[i];

                if(f.isDirectory()){
                    getFilesWithExtention(results, dir, extention);

                }else{
                    if(f.getName().endsWith(extention)){
                        results.add(f);
                    }
                }
            }
        }
    }

    public static byte[] getFileHashSum(File file, long upto){
        try{
            VLSecurity.DigestProfile digest = new VLSecurity.DigestProfile("SHA256");
            FileInputStream fis = new FileInputStream(file);

            long left = (upto == -1 ? file.length() : upto);
            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] active = null;

            while(left > 0){
                if(left >= BUFFER_SIZE){
                    active = buffer;

                }else{
                    active = new byte[(int)left];
                }

                left -= fis.read(active);

                if(left <= 0){
                    active = digest.finalize(active);

                }else{
                    digest.update(active);
                }
            }

            fis.close();

            return active;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static void attachHashToFile(File file, byte[] hash){
        try{
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(hash);
            fos.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static byte[] getHashSegmentFromFile(File file){
        try{
            byte[] hash = new byte[FILE_HASH_SIZE];
            FileInputStream fis = new FileInputStream(file);
            fis.getChannel().position(file.length() - FILE_HASH_SIZE);
            fis.read(hash);
            fis.close();

            return hash;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean isHashedFileHealthy(File file){
        try{
            return Arrays.equals(getFileHashSum(file, file.length() - FILE_HASH_SIZE), getHashSegmentFromFile(file));

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public static long[][] getElementsInfoInFile(RandomAccessFile f, int indexfromend, int count){
        try{
            if(indexfromend >= 0){
                FileChannel channel = f.getChannel();
                long csize = channel.size();
                long[][] info = new long[count][];
                int end = indexfromend + count;
                long pos = csize;
                long size = 0;

                for(int i = 0, tracker = 0; i < end; i++){
                    pos -= 4;

                    if(pos > 0){
                        channel.position(pos);
                        size = f.readInt();
                        pos -= size;

                    }else if(i < end - 1){
                        return null;
                    }

                    if(i >= indexfromend){
                        info[tracker++] = new long[]{ pos, size };
                    }
                }

                return info;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static void deleteDirectory(File dir){
        File[] subs = dir.listFiles();

        if(subs != null){
            for(int i = 0; i < subs.length; i++){
                File f = subs[i];

                if(f.isDirectory()){
                    deleteDirectory(dir);
                    f.delete();

                }else{
                    f.delete();
                }
            }
        }

        dir.delete();
    }

    public static byte[] convertBitmapToBytes(Bitmap b){
        ByteBuffer buffer = ByteBuffer.allocateDirect(12 + b.getByteCount());
        buffer.order(ByteOrder.nativeOrder());
        buffer.position(0);
        buffer.putInt(b.getWidth());
        buffer.putInt(b.getHeight());
        b.copyPixelsToBuffer(buffer);

        return buffer.array();
    }


    public static Bitmap convertBytesToBitmap(byte[] data, Bitmap.Config config){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0);
        int width = buffer.getInt();
        int height = buffer.getInt();

        Bitmap b = Bitmap.createBitmap(width, height, config);
        b.copyPixelsFromBuffer(buffer);

        return b;
    }

    public static Object convertToObject(byte[] data){
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInput in = new ObjectInputStream(bais);
            Object obj = in.readObject();
            in.close();

            return obj;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] convertToBytes(Object obj){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            byte[] data = baos.toByteArray();
            out.close();

            return data;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }


    public static short makeShort(byte[] data, int offset){
        return (short)(((data[offset + 0] & 0xFF) <<  8) | (data[offset + 1] & 0xFF));
    }

    public static int makeInt(byte[] data, int offset){
        return ((data[offset + 0] << 24) | ((data[offset + 1] & 0xFF) << 16) | ((data[offset + 2] & 0xFF) <<  8) | (data[offset + 3] & 0xFF));
    }

    public static long makeLong(byte[] data, int offset){
        return ((data[offset + 0] & 0xFFL) << 56) |
                ((data[offset + 1] & 0xFFL) << 48) |
                ((data[offset + 2] & 0xFFL) << 40) |
                ((data[offset + 3] & 0xFFL) << 32) |
                ((data[offset + 4] & 0xFFL) << 24) |
                ((data[offset + 5] & 0xFFL) << 16) |
                ((data[offset + 6] & 0xFFL) <<  8) |
                ((data[offset + 7] & 0xFFL) <<  0) ;
    }

    public static void getShortBytes(short input, byte[] data, int offset){
        data[offset + 0] = (byte)(input >> 8);
        data[offset + 1] = (byte)(input);
    }

    public static void getIntBytes(int input, byte[] data, int offset){
        data[offset + 0] = (byte)(input >> 24);
        data[offset + 1] = (byte)(input >> 16);
        data[offset + 2] = (byte)(input >> 8);
        data[offset + 3] = (byte)(input);
    }

    public static void getLongBytes(long input, byte[] data, int offset){
        data[offset + 0] = (byte)(input >> 56);
        data[offset + 1] = (byte)(input >> 48);
        data[offset + 2] = (byte)(input >> 40);
        data[offset + 3] = (byte)(input >> 32);
        data[offset + 4] = (byte)(input >> 24);
        data[offset + 5] = (byte)(input >> 16);
        data[offset + 6] = (byte)(input >> 8);
        data[offset + 7] = (byte)(input);
    }

    public static byte[] getShortBytes(short input){
        byte[] data = new byte[2];
        getShortBytes(input, data, 0);
        return data;
    }

    public static byte[] getIntBytes(int input){
        byte[] data = new byte[4];
        getIntBytes(input, data, 0);
        return data;
    }

    public static byte[] getLongBytes(long input){
        byte[] data = new byte[8];
        getLongBytes(input, data, 0);
        return data;
    }
}
