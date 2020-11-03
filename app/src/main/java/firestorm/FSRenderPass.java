package firestorm;

import android.opengl.GLES32;

import java.util.ArrayList;

public final class FSRenderPass{

    private ArrayList<Entry> entries;
    
    private boolean advanceprocessors;
    private boolean runtasks;
    private boolean clearcolor;
    private boolean cleardepth;
    private boolean clearstencil;
    private boolean update;
    private boolean draw;
    
    private long id;

    protected final ArrayList<Order> orders = new ArrayList<>(15);
    
    public FSRenderPass(){
        entries = new ArrayList<>();

        advanceprocessors = true;
        runtasks = true;
        clearcolor = true;
        cleardepth = true;
        clearstencil = true;
        update = true;
        draw = true;
        
        id = FSControl.getNextID();
    }




    public void add(Entry e){
        entries.add(e);
        FSControl.setRenderContinuously(true);
    }

    public void add(int index, Entry e){
        entries.add(index, e);
        FSControl.setRenderContinuously(true);
    }

    public Entry get(int index){
        return entries.get(index);
    }

    public Entry getWithID(int id){
        Entry e;

        for(int i = 0; i < entries.size(); i++){
            e = entries.get(i);
            FSLoader c = e.c;

            if(c.id() == id){
                return e;
            }
        }

        return null;
    }

    public int size(){
        return entries.size();
    }

    public void remove(FSLoader c){
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).c.id() == c.id()){
                entries.remove(i);
            }
        }
    }

    public Entry remove(int index){
        return entries.remove(index);
    }



    public FSRenderPass setAdvanceProcessors(boolean enabled){
        advanceprocessors = enabled;
        return this;
    }

    public FSRenderPass setRunTasks(boolean enabled){
        runtasks = enabled;
        return this;
    }

    public FSRenderPass setClearColor(boolean enabled){
        clearcolor = enabled;
        return this;
    }

    public FSRenderPass setClearDepth(boolean enabled){
        cleardepth = enabled;
        return this;
    }

    public FSRenderPass setClearStencil(boolean enabled){
        clearstencil = enabled;
        return this;
    }

    public FSRenderPass setUpdateMeshes(boolean enabled){
        update = enabled;
        return this;
    }

    public FSRenderPass setDrawMeshes(boolean enabled){
        draw = enabled;
        return this;
    }

    public boolean getAdvanceProcessors(){
        return advanceprocessors;
    }

    public boolean getRunTasks(){
        return runtasks;
    }

    public boolean getClearColor(){
        return clearcolor;
    }

    public boolean getClearDepth(){
        return cleardepth;
    }

    public boolean getClearStencil(){
        return clearstencil;
    }

    public boolean getUpdateMeshes(){
        return update;
    }

    public boolean getDrawMeshes(){
        return draw;
    }

    public long id(){
        return id;
    }

    public FSRenderPass build(){
        orders.add(new Order(){

            @Override
            public int execute(int orderindex, int passindex){
                FSControl.timeFrameStarted();
                return FLAG_CONTINUE;
            }
        });
        
        if(clearcolor || cleardepth || clearstencil){
            int clearbit = 0;

            if(clearcolor){
                clearbit |= GLES32.GL_COLOR_BUFFER_BIT;
            }
            if(cleardepth){
                clearbit |= GLES32.GL_DEPTH_BUFFER_BIT;
            }
            if(clearstencil){
                clearbit |= GLES32.GL_STENCIL_BUFFER_BIT;
            }
            
            if(clearbit != 0){
                final int clearbitf = clearbit;

                orders.add(new Order(){

                    @Override
                    public int execute(int orderindex, int passindex){
                        FSRenderer.clear(clearbitf);
                        return FLAG_CONTINUE;
                    }
                });
            }
            
            if(clearcolor){
                orders.add(new Order(){
                    
                    @Override
                    public int execute(int orderindex, int passindex){
                        FSRenderer.clearColor();
                        return FLAG_CONTINUE;
                    }
                });
            }
        }
        if(runtasks){
            orders.add(new Order(){
                
                @Override
                public int execute(int orderindex, int passindex){
                    FSRenderer.runTasks();
                    return FLAG_CONTINUE;
                }
            });
        }
        if(update){
            orders.add(new Order(){

                @Override
                public int execute(int orderindex, int passindex){
                    update();
                    return FLAG_CONTINUE;
                }
            });

        }
        if(draw){
            orders.add(new Order(){

                @Override
                public int execute(int orderindex, int passindex){
                    draw();
                    return FLAG_CONTINUE;
                }
            });
        }
        if(advanceprocessors){
            orders.add(new Order(){

                @Override
                public int execute(int orderindex, int passindex){
                    FSRenderer.advanceProcessors();
                    return FLAG_CONTINUE;
                }
            });
        }
        
        return this;
    }
    
    protected void execute(){
        int size = orders.size();

        for(int i = 0; i < size; i++){
            orders.get(i).execute(i, FSRenderer.CURRENT_RENDER_PASS_INDEX);
        }
    }

    public int advanceProcessors(){
        int changes = 0;

        for(int i = 0; i < entries.size(); i++){
            changes += entries.get(i).c.runProcessors();
        }

        return changes;
    }

    public void update(){
        FSControl.EVENTS.GLPreDraw();

        Entry e;

        for(int index = 0; index < entries.size(); index++){
            e = entries.get(index);

            FSRenderer.CURRENT_LOADER_INDEX = index;
            FSRenderer.CURRENT_PROGRAM_SET_INDEX = e.programsetindex;

            entries.get(index).c.update(FSRenderer.CURRENT_RENDER_PASS_INDEX, e.programsetindex);
        }

        FSControl.EVENTS.GLPostDraw();
    }

    public void draw(){
        FSControl.EVENTS.GLPreDraw();

        Entry e;

        for(int index = 0; index < entries.size(); index++){
            e = entries.get(index);

            FSRenderer.CURRENT_LOADER_INDEX = index;
            FSRenderer.CURRENT_PROGRAM_SET_INDEX = e.programsetindex;

            e.c.draw(FSRenderer.CURRENT_RENDER_PASS_INDEX, e.programsetindex);
        }

        FSControl.EVENTS.GLPostDraw();
    }

    protected void noitifyPostFrameSwap(){
        for(int i = 0; i < entries.size(); i++){
            entries.get(i).c.postFramSwap(FSRenderer.CURRENT_RENDER_PASS_INDEX);
        }
    }

    public FSRenderPass copySettings(FSRenderPass src){
        advanceprocessors = src.advanceprocessors;
        runtasks = src.runtasks;
        clearcolor = src.clearcolor;
        cleardepth = src.cleardepth;
        clearstencil = src.clearstencil;
        update = src.update;
        draw = src.draw;
        
        id = FSControl.getNextID();

        return this;
    }

    public void destroy(){
        for(int i = 0; i < entries.size(); i++){
            entries.get(i).c.destroy();
        }

        entries = null;
    }
    
    protected static interface Order{

        int FLAG_CONTINUE = 1252;
        
        int execute(int orderindex, int passindex);
    }

    public static final class Entry{

        protected FSLoader c;
        protected int programsetindex;

        public Entry(FSLoader c, int programsetindex){
            this.c = c;
            this.programsetindex = programsetindex;
        }

        public FSLoader constructor(){
            return c;
        }

        public int programSetIndex(){
            return programsetindex;
        }
    }
}
