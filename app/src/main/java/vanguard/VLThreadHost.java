package vanguard;

import java.util.ArrayList;

public class VLThreadHost {

    private GLThread[] workers;
    private volatile Runnable post;
    private Object postlock;

    public VLThreadHost(int size){
        initialize(size);
    }

    public VLThreadHost(){

    }



    public void initialize(int size){
        workers = new GLThread[size];
        postlock = new Object();
        post = null;

        GLThread t;

        for(int i = 0; i < workers.length; i++){
            t = new GLThread();
            t.start();

            workers[i] = t;
        }
    }

    public void add(Runnable task){
        getPriorityWorker().assign(task);
    }

    public void add(ArrayList<Runnable> tasks){
        getPriorityWorker().assign(tasks);
    }

    public void add(Runnable task, Runnable post){
        add(task);

        synchronized(postlock){
            this.post = post;
        }
    }

    public void add(ArrayList<Runnable> tasks, Runnable post){
        add(tasks);

        synchronized(postlock){
            this.post = post;
        }
    }

    public void add(Runnable task, long waitmillis){
        if(task != null){
            add(task);

            synchronized (this){
                while(!areWorkersFree()){
                    try{
                        if(waitmillis > 0){
                            wait(waitmillis);
                            break;

                        }else{
                            wait();
                        }

                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void add(ArrayList<Runnable> tasks, long waitmillis){
        add(tasks);

        synchronized (this){
            while(!areWorkersFree()){
                try{
                    if(waitmillis > 0){
                        wait(waitmillis);
                        break;

                    }else{
                        wait();
                    }

                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void checkPost(){
        if(post != null && areWorkersFree()){
            synchronized(postlock){
                post.run();
                post = null;
            }
        }
    }

    private GLThread getPriorityWorker(){
        int leastbusy = Integer.MAX_VALUE;
        int index = 0;

        for(int i = 0; i < workers.length; i++){
            GLThread t = workers[i];
            int s = t.queue.size();

            if(leastbusy > s){
                leastbusy = s;
                index = i;

                break;
            }
        }

        return workers[index];
    }

    public boolean areWorkersFree(){
        for(int i = 0; i < workers.length; i++){
            if(workers[i].queue.size() > 0){
                return false;
            }
        }

        return true;
    }

    private void notifyWorkerFree(){
        synchronized(this){
            notifyAll();
        }
    }

    public void destroy(){
        for(GLThread t : workers){
            try{
                t.disable();
                t.join();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private class GLThread extends Thread{

        private volatile ArrayList<Runnable> queue;
        private Object mainlock;

        private volatile boolean enabled;

        protected GLThread(){
            queue = new ArrayList<>(5);
            mainlock = new Object();
            enabled = true;
        }

        @Override
        public void run(){
            super.run();

            ArrayList<Runnable> tasks = new ArrayList<>();

            while(isEnabled()){
                synchronized(mainlock){
                    while(queue.isEmpty() && isEnabled()){
                        try{
                            mainlock.wait();
                        }catch(InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }

                    tasks.addAll(queue);
                    queue.clear();
                }

                for(int i = 0; i < tasks.size(); i++){
                    tasks.get(i).run();
                }

                tasks.clear();
                checkPost();
                notifyWorkerFree();
            }
        }

        protected void assign(Runnable task){
            synchronized(mainlock){
                queue.add(task);
                mainlock.notify();
            }
        }

        protected void assign(ArrayList<Runnable> tasks){
            synchronized(mainlock){
                queue.addAll(tasks);
                mainlock.notify();
            }
        }

        protected void disable(){
            synchronized(mainlock){
                enabled = false;
                mainlock.notify();
            }
        }

        protected boolean isEnabled(){
            return enabled;
        }
    }
}
