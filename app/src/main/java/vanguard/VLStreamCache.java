package vanguard;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class VLStreamCache {

    private static ArrayList<Stream> streams;
    private static ArrayList<ScheduledFuture> timers;

    private static ScheduledExecutorService EXECUTOR;


    public static void initialize(){
        streams = new ArrayList<>();
        timers = new ArrayList<>();
        EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    }

    public static void addStream(Closeable stream, long id, long keepalive){
        try{
            streams.add(new Stream(stream, id));

            if(keepalive > 0){
                scheduleClose(id, keepalive);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void setStream(Closeable stream, long id, long keepalive, int index){
        try{
            streams.set(index, new Stream(stream, id));

            if(keepalive > 0){
                scheduleClose(id, keepalive);

            }else{
                timers.add(null);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static Stream getStream(int index){
        return streams.get(index);
    }

    public static Stream getStream(long id){
        return streams.get(getStreamIndex(id));
    }

    public static int getStreamIndex(long id){
        for(int i = 0; i < streams.size(); i++){
            if(streams.get(i).id() == id){
                return i;
            }
        }

        return -1;
    }

    public static int getStreamCount(){
        return streams.size();
    }

    public static void removeStream(int index){
        try{
            streams.remove(index).close();
            timers.remove(index).cancel(false);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void clearStreams(){
        try{
            for(int i = 0; i < streams.size(); i++){
                streams.get(i).close();
                timers.get(i).cancel(false);
            }

            streams.clear();
            timers.clear();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void scheduleClose(final long id, long delay){
        timers.add(EXECUTOR.schedule(new Runnable(){

            @Override
            public void run(){
                removeStream(getStreamIndex(id));
            }

        }, delay, TimeUnit.MILLISECONDS));
    }

    public static final class Stream{

        private Closeable stream;
        private long id;

        public Stream(Closeable stream, long id){
            this.stream = stream;
            this.id = id;
        }


        public Closeable stream(){
            return stream;
        }

        public FileInputStream fileInputStream(){
            return (FileInputStream)stream;
        }

        public FileOutputStream fileOutputStream(){
            return (FileOutputStream)stream;
        }

        public long id(){
            return id;
        }

        public void close() throws IOException {
            stream.close();
        }
    }
}
