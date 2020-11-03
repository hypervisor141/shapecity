package firestorm;

import android.opengl.GLES32;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import vanguard.VLArray;
import vanguard.VLArrayFloat;
import vanguard.VLArrayShort;
import vanguard.VLDebug;
import vanguard.VLListFloat;
import vanguard.VLListType;
import vanguard.VLStringify;
import vanguard.VLVConst;
import vanguard.VLVProcessor;

public abstract class FSLoader{

    public static final int DEBUG_DISABLED = 0;
    public static final int DEBUG_NORMAL = 1;
    public static final int DEBUG_FULL = 2;

    public static final int ELEMENT_BYTES_MODEL = Float.SIZE / 8;
    public static final int ELEMENT_BYTES_POSITION = Float.SIZE / 8;
    public static final int ELEMENT_BYTES_COLOR = Float.SIZE / 8;
    public static final int ELEMENT_BYTES_TEXCOORD = Float.SIZE / 8;
    public static final int ELEMENT_BYTES_NORMAL = Float.SIZE / 8;
    public static final int ELEMENT_BYTES_INDEX = Short.SIZE / 8;

    public static final int UNIT_SIZE_MODEL = 16;
    public static final int UNIT_SIZE_POSITION = 4;
    public static final int UNIT_SIZE_COLOR = 4;
    public static final int UNIT_SIZE_TEXCOORD = 2;
    public static final int UNIT_SIZE_NORMAL = 3;
    public static final int UNIT_SIZE_INDEX = 1;

    public static final int UNIT_BYTES_MODEL = UNIT_SIZE_MODEL * ELEMENT_BYTES_MODEL;
    public static final int UNIT_BYTES_POSITION = UNIT_SIZE_POSITION * ELEMENT_BYTES_POSITION;
    public static final int UNIT_BYTES_COLOR = UNIT_SIZE_COLOR * ELEMENT_BYTES_COLOR;
    public static final int UNIT_BYTES_TEXCOORD = UNIT_SIZE_TEXCOORD * ELEMENT_BYTES_TEXCOORD;
    public static final int UNIT_BYTES_NORMAL = UNIT_SIZE_NORMAL * ELEMENT_BYTES_NORMAL;
    public static final int UNIT_BYTES_INDEX = UNIT_SIZE_INDEX * ELEMENT_BYTES_INDEX;

    public static final int ELEMENT_GLDATA_TYPE_MODEL = GLES32.GL_FLOAT;
    public static final int ELEMENT_GLDATA_TYPE_POSITION = GLES32.GL_FLOAT;
    public static final int ELEMENT_GLDATA_TYPE_COLOR = GLES32.GL_FLOAT;
    public static final int ELEMENT_GLDATA_TYPE_TEXCOORD = GLES32.GL_FLOAT;
    public static final int ELEMENT_GLDATA_TYPE_NORMAL = GLES32.GL_FLOAT;
    public static final int ELEMENT_GLDATA_TYPE_INDEX = GLES32.GL_UNSIGNED_SHORT;

    public static final int ELEMENT_MODEL = 0;
    public static final int ELEMENT_POSITION = 1;
    public static final int ELEMENT_COLOR = 2;
    public static final int ELEMENT_TEXCOORD = 3;
    public static final int ELEMENT_NORMAL = 4;
    public static final int ELEMENT_INDEX = 5;

    public static final int ELEMENT_TOTAL_COUNT = 7;

    public static final int[] ELEMENT_BYTES = new int[]{
            ELEMENT_BYTES_MODEL, ELEMENT_BYTES_POSITION, ELEMENT_BYTES_COLOR, ELEMENT_BYTES_TEXCOORD, ELEMENT_BYTES_NORMAL, ELEMENT_BYTES_INDEX
    };
    public static final int[] UNIT_SIZES = new int[]{
            UNIT_SIZE_MODEL, UNIT_SIZE_POSITION, UNIT_SIZE_COLOR, UNIT_SIZE_TEXCOORD, UNIT_SIZE_NORMAL, UNIT_SIZE_INDEX
    };
    public static final int[] UNIT_BYTES = new int[]{
            UNIT_BYTES_MODEL, UNIT_BYTES_POSITION, UNIT_BYTES_COLOR, UNIT_BYTES_TEXCOORD, UNIT_BYTES_NORMAL, UNIT_BYTES_INDEX
    };
    public static final int[] ELEMENT_GLDATA_TYPES = new int[]{
            ELEMENT_GLDATA_TYPE_MODEL, ELEMENT_GLDATA_TYPE_POSITION, ELEMENT_GLDATA_TYPE_COLOR, ELEMENT_GLDATA_TYPE_TEXCOORD,
            ELEMENT_GLDATA_TYPE_NORMAL, ELEMENT_GLDATA_TYPE_INDEX
    };
    public static final String[] ELEMENT_NAMES = new String[]{
            "MODEL",
            "POSITION",
            "COLOR",
            "TEXCOORD",
            "NORMAL",
            "INDEX"
    };

    protected static VLVProcessor CONTROLPROCESSOR = new VLVProcessor(1, 10);

    protected VLListType<VLListType<FSP>> PROGRAMSETS;
    protected VLListType<VLVProcessor> PROCESSORS;

    protected Automator AUTOMATOR;
    protected FSBufferManager BUFFERMANAGER;

    protected long ID;
    protected boolean isTouchable;

    public FSLoader(int programsetsize){
        isTouchable = true;

        ID = FSControl.getNextID();
        BUFFERMANAGER = new FSBufferManager(ELEMENT_TOTAL_COUNT);

        PROGRAMSETS = new VLListType<>(5, 20);
        PROCESSORS = new VLListType<>(5, 20);

        addProgramSets(programsetsize);
    }


    public void initialize(@Nullable FSActivity act){
        assemble(act);
    }

    protected abstract void assemble(FSActivity act);

    public void update(int passindex, int programsetindex){}

    public void draw(int passindex, int programsetindex){
        VLListType<FSP> p = PROGRAMSETS.get(programsetindex);
        int size = p.size();

        for(int i = 0; i < size; i++){
            p.get(i).draw(passindex);
        }
    }

    protected void postFramSwap(int passindex){}

    public int runProcessors(){
        int changes = 0;

        for(int i = 0; i < PROCESSORS.size(); i++){
            changes += PROCESSORS.get(i).next();
        }

        return changes;
    }

    public VLArrayFloat createColorArray(float[] basecolor, int count){
        float[] colors = new float[count * 4];

        for(int i = 0; i < colors.length; i += 4){
            colors[i] = basecolor[0];
            colors[i + 1] = basecolor[1];
            colors[i + 2] = basecolor[2];
            colors[i + 3] = basecolor[3];
        }

        return new VLArrayFloat(colors);
    }

    public void addProgramSets(int count){
        for(int i = 0; i < count; i++){
            PROGRAMSETS.add(new VLListType<FSP>(5, 10));
        }
    }

    public void constructAutomator(FSM fsm){
        AUTOMATOR = new Automator(fsm);
    }

    public void constructAutomator(InputStream is, ByteOrder order, boolean fullsizedposition, int estimatedsize) throws IOException{
        FSM data = new FSM();
        data.loadFromFile(is, order, fullsizedposition, estimatedsize);

        AUTOMATOR = new Automator(data);
    }

    public void constructAutomator(String path, ByteOrder order, boolean fullsizedposition, int estimatedsize) throws FileNotFoundException, SecurityException, IOException{
        constructAutomator(new FileInputStream(path), order, fullsizedposition, estimatedsize);
    }

    public void releaseAutomator(){
        AUTOMATOR = null;
    }

    public void touchable(boolean t){
        isTouchable = t;
    }

    public FSBufferManager bufferManager(){
        return BUFFERMANAGER;
    }

    public VLListType<FSP> programSet(int passindex){
        return PROGRAMSETS.get(passindex);
    }

    public VLListType<VLListType<FSP>> programSets(){
        return PROGRAMSETS;
    }

    public VLVProcessor processor(int index){
        return PROCESSORS.get(index);
    }

    public VLListType<VLVProcessor> processors(){
        return PROCESSORS;
    }

    public long id(){
        return ID;
    }

    public int programsSize(){
        return PROGRAMSETS.size();
    }

    public int processorsSize(){
        return PROCESSORS.size();
    }

    public boolean touchable(){
        return isTouchable;
    }

    public void destroy(){
        destroyAssets();

        VLListType<FSP> programs;

        for(int i = 0; i < PROGRAMSETS.size(); i++){
            programs = PROGRAMSETS.get(i);

            for(int i2 = 0; i2 < programs.size(); i2++){
                programs.get(i2).destroy();
            }
        }

        for(int i = 0; i < BUFFERMANAGER.size(); i++){
            BUFFERMANAGER.get(i).destroy();
        }

        PROGRAMSETS = null;
        BUFFERMANAGER = null;
        PROCESSORS = null;
        AUTOMATOR = null;

        isTouchable = false;
        ID = -1;
    }

    protected abstract void destroyAssets();



    public final class Automator{

        protected FSM fsm;
        protected VLListType<Scanner> scanners;

        protected Automator(FSM fsm){
            this.fsm = fsm;

            int size = fsm.data.size();
            scanners = new VLListType<>(size, size);
        }


        public Registration addScannerSingle(Assembler assembler, DataPack pack, String name, int drawmode){
            Scanner s = new ScannerSingular(assembler, new DataGroup(new VLListType<DataPack>(new DataPack[]{ pack }, 1)), name, drawmode);
            scanners.add(s);

            return new Registration(s);
        }

        public Registration addScannerInstanced(Assembler assembler, DataGroup datagroup, String prefixname, int drawmode, int estimatedsize){
            Scanner s = new ScannerInstanced(assembler, datagroup, prefixname, drawmode, estimatedsize);
            scanners.add(s);

            return new Registration(s);
        }

        public void execute(int debug){
            VLListType<FSM.Data> data = fsm.data;
            FSM.Data d;

            int size = data.size();
            int size2 = scanners.size();

            if(debug > DEBUG_DISABLED){
                VLDebug.recreate();
                
                Scanner s;
                boolean found;

                VLDebug.printDirect("[Assembler Check Stage]\n");

                for(int i = 0; i < size2; i++){
                    s = scanners.get(i);

                    if(s.assembler.checkDebug()){
                        VLDebug.append("Scanner[");
                        VLDebug.append(s.name);
                        VLDebug.append("] : invalid assembler configuration.");
                        VLDebug.append("[Assembler Configuration]\n");

                        s.assembler.stringify(VLDebug.get(), null);
                    }
                }

                VLDebug.printDirect("[DONE]\n");
                VLDebug.printDirect("[Build Stage]\n");

                FSMesh mesh;

                for(int i = 0; i < size; i++){
                    d = data.get(i);

                    for(int i2 = 0; i2 < size2; i2++){
                        s = scanners.get(i2);
                        mesh = s.mesh;
                        found = false;

                        try{
                            if(s.scan(this, d) && debug >= DEBUG_FULL){
                                VLDebug.append("Built[");
                                VLDebug.append(i);
                                VLDebug.append("] keyword[");
                                VLDebug.append(s.name);
                                VLDebug.append("] name[");
                                VLDebug.append(d.name);
                                VLDebug.append("] ");

                                found = true;
                            }

                            if(found && mesh.size() > 1 && mesh.get(mesh.size() - 1).positions().size() != mesh.first().positions().size()){
                                VLDebug.printD();
                                VLDebug.append("[WARNING] ");
                                VLDebug.append("[Attempting to do instancing on meshes with different vertex characteristics]");
                                VLDebug.printE();
                            }

                        }catch(Exception ex){
                            VLDebug.append("Error building \"");
                            VLDebug.append(s.name);
                            VLDebug.append("\"\n[Assembler Configuration]\n");

                            s.assembler.stringify(VLDebug.get(), null);
                            VLDebug.printE();

                            throw new RuntimeException(ex);
                        }

                        if(found){
                            VLDebug.printD();
                        }
                    }
                }

                VLDebug.printDirect("[DONE]\n");
                VLDebug.printD();
                VLDebug.printDirect("[Checking Scan Results]\n");

                for(int i = 0; i < size2; i++){
                    s = scanners.get(i);

                    if(s.mesh.size() == 0){
                        VLDebug.append("Scan incomplete : found no instance for mesh with keyword \"");
                        VLDebug.append(s.name);
                        VLDebug.append("\".\n");
                        VLDebug.append("[Assembler Configuration]\n");

                        s.assembler.stringify(VLDebug.get(), null);
                    }
                }

                VLDebug.printDirect("[DONE]\n");
                VLDebug.printD();
                VLDebug.printDirect("[Buffering Stage]\n");

                BUFFERMANAGER.initialize();

                for(int i = 0; i < size2; i++){
                    s = scanners.get(i);

                    VLDebug.append("Buffering[");
                    VLDebug.append(i + 1);
                    VLDebug.append("/");
                    VLDebug.append(size2);
                    VLDebug.append("]\n");

                    if(debug >= DEBUG_FULL){
                        s.debugInfo();
                    }

                    try{
                        s.bufferDebug();

                    }catch(Exception ex){
                        VLDebug.append("Error buffering \"");
                        VLDebug.append(s.name);
                        VLDebug.append("\"\n");
                        VLDebug.append("[Assembler Configuration]\n");

                        s.assembler.stringify(VLDebug.get(), null);
                        VLDebug.printE();
                        
                        throw new RuntimeException(ex);
                    }

                    VLDebug.printD();
                }

                BUFFERMANAGER.upload();

                VLDebug.printDirect("[DONE]\n");
                VLDebug.printD();

            }else{
                for(int i = 0; i < size; i++){
                    d = data.get(i);

                    for(int i2 = 0; i2 < size2; i2++){
                        scanners.get(i2).scan(this, d);
                    }
                }

                BUFFERMANAGER.initialize();

                for(int i = 0; i < size2; i++){
                    scanners.get(i).buffer();
                }

                BUFFERMANAGER.upload();
            }
        }
    }

    public static final class Registration{

        protected Scanner scanner;

        protected Registration(Scanner scanner){
            this.scanner = scanner;
        }

        public FSMesh mesh(){
            return scanner.mesh;
        }

        public FSBufferLayout bufferLayout(){
            return scanner.layout;
        }

        public void clearPrograms(){
            scanner.programs.clear();
        }

        public void addProgram(FSP program){
            scanner.programs.add(program);
        }
    }

    public static final class DataGroup{

        protected VLListType<DataPack> packs;

        public DataGroup(VLListType<DataPack> packs){
            this.packs = packs;
        }

        protected DataPack get(int index){
            return packs.get(index);
        }
    }

    public static final class DataPack{

        protected VLArrayFloat replacementcolor;
        protected FSTexture colortexture;
        protected FSLightMaterial material;
        protected FSLightMap map;

        public DataPack(VLArrayFloat replacementcolor, FSTexture colortexture, FSLightMaterial material, FSLightMap map){
            this.replacementcolor = replacementcolor;
            this.colortexture = colortexture;
            this.material = material;
            this.map = map;
        }
    }

    protected abstract class Scanner{

        protected Assembler assembler;
        protected FSBufferLayout layout;
        protected DataGroup datagroup;

        protected FSMesh mesh;
        protected String name;

        private VLListType<FSP> programs;

        private Scanner(Assembler assembler, DataGroup datagroup, FSMesh mesh, String name){
            this.mesh = mesh;
            this.datagroup = datagroup;
            this.assembler = assembler;
            this.name = name;
            this.layout = layout;

            programs = new VLListType<>(10, 20);
            layout = new FSBufferLayout(mesh, assembler);

            mesh.name(name);
        }

        protected abstract boolean scan(Automator automator, FSM.Data data);

        private void buffer(){
            layout.buffer();
        }

        private void bufferDebug(){
            layout.bufferDebug(this);
        }

        protected void addMeshToPrograms(){
            int size = programs.size();

            for(int i = 0; i < size; i++){
                programs.get(i).addMesh(mesh);
            }
        }

        protected void debugInfo(){
            VLDebug.append("[");
            VLDebug.append(getClass().getSimpleName());
            VLDebug.append("] ");
            VLDebug.append("mesh[" + mesh.name + "] ");

            int size = mesh.size();
            VLArrayFloat[] data;
            VLArrayFloat array;
            int[] requirements = new int[ELEMENT_TOTAL_COUNT];

            if(mesh.indices != null){
                requirements[ELEMENT_INDEX] = mesh.indices.size();
            }

            for(int i = 0; i < size; i++){
                data = mesh.get(i).data.elements;

                for(int i2 = 0; i2 < data.length; i2++){
                    array = data[i2];

                    if(array != null){
                        requirements[i2] += array.size();
                    }
                }
            }

            VLDebug.append("storageRequirements[");

            if(assembler.INSTANCE_SHARE_POSITIONS){
                requirements[ELEMENT_POSITION] /= size;
            }
            if(assembler.INSTANCE_SHARE_COLORS){
                requirements[ELEMENT_COLOR] /= size;
            }
            if(assembler.INSTANCE_SHARE_TEXCOORDS){
                requirements[ELEMENT_TEXCOORD] /= size;
            }
            if(assembler.INSTANCE_SHARE_NORMALS){
                requirements[ELEMENT_NORMAL] /= size;
            }

            size = ELEMENT_NAMES.length;

            for(int i = 0; i < size; i++){
                VLDebug.append(ELEMENT_NAMES[i]);
                VLDebug.append("[");
                VLDebug.append(requirements[i]);

                if(i < size - 1){
                    VLDebug.append("] ");
                }
            }

            VLDebug.append("]]\n");
        }
    }

    protected class ScannerSingular extends Scanner{

        protected ScannerSingular(Assembler assembler, DataGroup datagroup, String name, int drawmode){
            super(assembler, datagroup, new FSMesh(drawmode, 1, 1), name);
        }

        @Override
        protected boolean scan(Automator automator, FSM.Data fsm){
            if(fsm.name.equalsIgnoreCase(name)){
                if(assembler.LOAD_INDICES){
                    mesh.indices(new VLArrayShort(fsm.indices.array()));
                    assembler.buildFirst(this, fsm);

                    if(assembler.BUFFER_INDICES){
                        layout.increaseTargetCapacities(ELEMENT_INDEX, mesh.indices.size());

                        if(assembler.SYNC_INDICES_AND_BUFFER){
                            assembler.buffersteps[ELEMENT_INDEX] = Assembler.BUFFER_SYNC;
                        }
                    }

                }else{
                    assembler.buildFirst(this, fsm);
                }

                addMeshToPrograms();

                return true;
            }

            return false;
        }
    }

    protected class ScannerInstanced extends Scanner{

        protected ScannerInstanced(Assembler assembler, DataGroup datagroup, String prefixname, int drawmode, int estimatedsize){
            super(assembler, datagroup, new FSMesh(drawmode, estimatedsize, (int)Math.ceil(estimatedsize / 2f)), prefixname);
        }

        @Override
        protected boolean scan(Automator automator, FSM.Data fsm){
            if(fsm.name.contains(name)){
                if(assembler.LOAD_INDICES && mesh.indices == null){
                    mesh.indices(new VLArrayShort(fsm.indices.array()));
                    assembler.buildFirst(this, fsm);

                    addMeshToPrograms();

                    if(assembler.BUFFER_INDICES){
                        layout.increaseTargetCapacities(ELEMENT_INDEX, mesh.indices.size());

                        if(assembler.SYNC_INDICES_AND_BUFFER){
                            assembler.buffersteps[ELEMENT_INDEX] = Assembler.BUFFER_SYNC;
                        }
                    }

                }else{
                    assembler.buildRest(this, fsm);
                }

                return true;
            }

            return false;
        }
    }

    public static final class Assembler implements VLStringify{

        public boolean SYNC_MODELCLUSTER_AND_MODELARRAY = false;
        public boolean SYNC_MODELARRAY_AND_BUFFER = false;
        public boolean SYNC_MODELARRAY_AND_SCHEMATICS = false;
        public boolean SYNC_POSITION_AND_BUFFER = false;
        public boolean SYNC_POSITION_AND_SCHEMATICS = false;
        public boolean SYNC_COLOR_AND_BUFFER = false;
        public boolean SYNC_TEXCOORD_AND_BUFFER = false;
        public boolean SYNC_NORMAL_AND_BUFFER = false;
        public boolean SYNC_INDICES_AND_BUFFER = false;
        
        public boolean ENABLE_COLOR_FILL = false;
        public boolean ENABLE_DATA_PACK = false;

        public boolean BUFFER_MODELS = false;
        public boolean BUFFER_POSITIONS = false;
        public boolean BUFFER_TEXCOORDS = false;
        public boolean BUFFER_COLORS = false;
        public boolean BUFFER_NORMALS = false;
        public boolean BUFFER_INDICES = false;

        public boolean LOAD_MODELS = false;
        public boolean LOAD_POSITIONS = false;
        public boolean LOAD_TEXCOORDS = false;
        public boolean LOAD_COLORS = false;
        public boolean LOAD_NORMALS = false;
        public boolean LOAD_INDICES = false;

        public boolean INSTANCE_SHARE_POSITIONS = false;
        public boolean INSTANCE_SHARE_TEXCOORDS = false;
        public boolean INSTANCE_SHARE_COLORS = false;
        public boolean INSTANCE_SHARE_NORMALS = false;

        public boolean CONVERT_POSITIONS_TO_MODELARRAYS = false;
        public boolean DRAW_MODE_INDEXED = false;

        private BufferStep[] buffersteps;
        private VLListType<BuildStep> firstfuncs;
        private VLListType<BuildStep> restfuncs;

        public Assembler(){
            firstfuncs = new VLListType<>(10, 20);
            restfuncs = new VLListType<>(10, 20);
            buffersteps = new BufferStep[ELEMENT_TOTAL_COUNT];

            setDefaultAll();
        }


        public void setDefaultAll(){
            for(int i = 0; i < buffersteps.length; i++){
                buffersteps[i] = BUFFER_NO_SYNC;
            }

            ENABLE_DATA_PACK = true;

            SYNC_MODELCLUSTER_AND_MODELARRAY = false;
            SYNC_MODELARRAY_AND_BUFFER = true;
            SYNC_MODELARRAY_AND_SCHEMATICS = true;
            SYNC_POSITION_AND_SCHEMATICS = true;
            SYNC_POSITION_AND_BUFFER = true;
            SYNC_COLOR_AND_BUFFER = true;
            SYNC_TEXCOORD_AND_BUFFER = true;
            SYNC_NORMAL_AND_BUFFER = true;
            SYNC_INDICES_AND_BUFFER = true;
            
            ENABLE_COLOR_FILL = false;

            LOAD_MODELS = true;
            LOAD_POSITIONS = true;
            LOAD_COLORS = true;
            LOAD_TEXCOORDS = true;
            LOAD_NORMALS = true;
            LOAD_INDICES = true;
            
            BUFFER_MODELS = true;
            BUFFER_POSITIONS = true;
            BUFFER_COLORS = true;
            BUFFER_TEXCOORDS = true;
            BUFFER_NORMALS = true;
            BUFFER_INDICES = true;

            INSTANCE_SHARE_POSITIONS = false;
            INSTANCE_SHARE_COLORS = false;
            INSTANCE_SHARE_TEXCOORDS = false;
            INSTANCE_SHARE_NORMALS = false;

            CONVERT_POSITIONS_TO_MODELARRAYS = true;

            DRAW_MODE_INDEXED = true;
        }

        public void configure(){
            restfuncs.clear();
            firstfuncs.clear();

            configureModels();
            configurePositions();
            configureColors();
            configureTexCoords();
            configureNormals();
            configureMisc();
        }

        private void configureModels(){
            if(LOAD_MODELS){
                firstfuncs.add(MODEL_INITIALIZE);
                restfuncs.add(MODEL_INITIALIZE);

                if(BUFFER_MODELS){
                    firstfuncs.add(MODEL_ADJUST_BUFFER_CAPACITY);
                    restfuncs.add(MODEL_ADJUST_BUFFER_CAPACITY);
                }

                if(SYNC_MODELCLUSTER_AND_MODELARRAY){
                    firstfuncs.add(MODEL_SYNC_MODELCLUSTER_AND_MODELARRAY);
                    restfuncs.add(MODEL_SYNC_MODELCLUSTER_AND_MODELARRAY);
                }
                if(SYNC_MODELARRAY_AND_BUFFER){
                    buffersteps[ELEMENT_MODEL] = BUFFER_SYNC;
                }
                if(SYNC_MODELARRAY_AND_SCHEMATICS){
                    firstfuncs.add(MODEL_SYNC_MODELARRAY_AND_SCHEMATICS);
                    restfuncs.add(MODEL_SYNC_MODELARRAY_AND_SCHEMATICS);
                }
            }
        }

        private void configurePositions(){
            if(LOAD_POSITIONS){
                firstfuncs.add(POSITION_SET_DEFAULT);
                firstfuncs.add(POSITION_INIT_SCHEMATICS);

                if(CONVERT_POSITIONS_TO_MODELARRAYS){
                    firstfuncs.add(POSITION_BUILD_MODELSET_AND_ALL_ELSE);
                }
                if(BUFFER_POSITIONS){
                    firstfuncs.add(POSITION_ADJUST_BUFFER_CAPACITY);
                }

                if(INSTANCE_SHARE_POSITIONS){
                    if(CONVERT_POSITIONS_TO_MODELARRAYS){
                        restfuncs.add(POSITION_SET_DEFAULT);

                        if(!DRAW_MODE_INDEXED){
                            restfuncs.add(POSITION_UNINDEX);
                        }

                        restfuncs.add(POSITION_INIT_SCHEMATICS);
                        restfuncs.add(POSITION_BUILD_MODELSET);
                    }

                    restfuncs.add(POSITION_SET_SHARED);
                    restfuncs.add(POSITION_SHARE_SCHEMATICS);

                }else{
                    restfuncs.add(POSITION_SET_DEFAULT);
                    restfuncs.add(POSITION_INIT_SCHEMATICS);

                    if(!DRAW_MODE_INDEXED){
                        restfuncs.add(POSITION_UNINDEX);
                    }
                    if(CONVERT_POSITIONS_TO_MODELARRAYS){
                        restfuncs.add(POSITION_BUILD_MODELSET_AND_ALL_ELSE);
                    }
                    if(BUFFER_POSITIONS){
                        restfuncs.add(POSITION_ADJUST_BUFFER_CAPACITY);
                    }
                }

                if(SYNC_POSITION_AND_BUFFER){
                    buffersteps[ELEMENT_POSITION] = BUFFER_SYNC;
                }
                if(SYNC_POSITION_AND_SCHEMATICS){
                    firstfuncs.add(POSITION_SYNC_WITH_SCHEMATICS);
                    restfuncs.add(POSITION_SYNC_WITH_SCHEMATICS);
                }
            }
        }

        private void configureColors(){
            if(LOAD_COLORS){
                BuildStep step;
                
                if(ENABLE_COLOR_FILL){
                    if(DRAW_MODE_INDEXED){
                        step = COLOR_AUTOFILL_INDEXED;

                    }else{
                        step = COLOR_AUTOFILL_NONE_INDEXED;
                    }

                }else{
                    if(DRAW_MODE_INDEXED){
                        step = COLOR_FILE_LOADED_INDEXED;

                    }else{
                        step = COLOR_FILE_LOADED_NONE_INDEXED;
                    }
                }
                
                firstfuncs.add(step);

                if(BUFFER_COLORS){
                    firstfuncs.add(COLOR_ADJUST_BUFFER_CAPACITY);
                }

                if(INSTANCE_SHARE_COLORS){
                    restfuncs.add(COLOR_SHARE);

                }else{
                    restfuncs.add(step);

                    if(BUFFER_COLORS){
                        restfuncs.add(COLOR_ADJUST_BUFFER_CAPACITY);
                    }
                }

                if(SYNC_COLOR_AND_BUFFER){
                    buffersteps[ELEMENT_COLOR] = BUFFER_SYNC;
                }
            }
        }

        private void configureTexCoords(){
            if(LOAD_TEXCOORDS){
                BuildStep step;

                if(DRAW_MODE_INDEXED){
                    step = TEXTURE_INDEXED;

                }else{
                    step = TEXTURE_NONE_INDEXED;
                }

                firstfuncs.add(step);

                if(BUFFER_TEXCOORDS){
                    firstfuncs.add(TEXTURE_ADJUST_BUFFER_CAPACITY);
                }

                if(INSTANCE_SHARE_TEXCOORDS){
                    restfuncs.add(TEXTURE_SHARE);

                }else{
                    restfuncs.add(step);

                    if(BUFFER_TEXCOORDS){
                        restfuncs.add(TEXTURE_ADJUST_BUFFER_CAPACITY);
                    }
                }

                if(SYNC_TEXCOORD_AND_BUFFER){
                    buffersteps[ELEMENT_TEXCOORD] = BUFFER_SYNC;
                }
            }
        }

        private void configureNormals(){
            if(LOAD_NORMALS){
                BuildStep step;

                if(DRAW_MODE_INDEXED){
                    step = NORMAL_INDEXED;

                }else{
                    step = NORMAL_NONE_INDEXED;
                }

                firstfuncs.add(step);

                if(BUFFER_NORMALS){
                    firstfuncs.add(NORMAL_ADJUST_BUFFER_CAPACITY);
                }

                if(INSTANCE_SHARE_NORMALS){
                    restfuncs.add(NORMAL_SHARE);

                }else{
                    restfuncs.add(step);

                    if(BUFFER_NORMALS){
                        restfuncs.add(NORMAL_ADJUST_BUFFER_CAPACITY);
                    }
                }

                if(SYNC_NORMAL_AND_BUFFER){
                    buffersteps[ELEMENT_NORMAL] = BUFFER_SYNC;
                }
            }
        }

        private void configureMisc(){
            if(ENABLE_DATA_PACK){
                firstfuncs.add(MISC_USE_DATA_PACK);
                restfuncs.add(MISC_USE_DATA_PACK);
            }
        }

        private void buildModelSetFromSchematics(FSInstance instance){
            FSSchematics schematics = instance.schematics;
            FSModelCluster set = instance.modelCluster();

            set.addSet(4, 1);
            set.addTranslation(0, new VLVConst(schematics.rawCentroidX()), new VLVConst(schematics.rawCentroidY()), new VLVConst(schematics.rawCentroidZ()));
            set.sync();
        }

        private void centralizePositions(FSInstance instance){
            float[] positions = instance.positions().provider();
            FSSchematics schematics = instance.schematics;

            float x = schematics.rawCentroidX();
            float y = schematics.rawCentroidY();
            float z = schematics.rawCentroidZ();

            int size = positions.length;

            for(int i = 0; i < size; i += UNIT_SIZE_POSITION){
                positions[i] = positions[i] - x;
                positions[i + 1] = positions[i + 1] - y;
                positions[i + 2] = positions[i + 2] - z;
            }
        }

        protected void unIndexPositions(FSInstance.Data data, short[] indices){
            float[] positions = data.positions().provider();
            VLListFloat converted = new VLListFloat(positions.length, positions.length / 2);

            for(int i2 = 0; i2 < indices.length; i2++){
                int pindex = indices[i2] * UNIT_SIZE_POSITION;

                converted.add(positions[pindex]);
                converted.add(positions[pindex]);
                converted.add(positions[pindex]);
                converted.add(positions[pindex]);
            }

            converted.restrictSize();
            data.positions().provider(converted.array());
        }

        protected void unIndexColors(FSInstance.Data data, short[] indices){
            float[] colors = data.colors().provider();
            VLListFloat converted = new VLListFloat(colors.length, colors.length / 2);

            for(int i2 = 0; i2 < indices.length; i2++){
                int cindex = indices[i2] * UNIT_SIZE_COLOR;

                converted.add(colors[cindex]);
                converted.add(colors[cindex + 1]);
                converted.add(colors[cindex + 2]);
                converted.add(colors[cindex + 3]);
            }

            converted.restrictSize();
            data.colors().provider(converted.array());
        }

        protected void unIndexTexCoords(FSInstance.Data data, short[] indices){
            float[] texcoords = data.texCoords().provider();
            VLListFloat converted = new VLListFloat(texcoords.length, texcoords.length / 2);

            for(int i2 = 0; i2 < indices.length; i2++){
                int tindex = indices[i2] * UNIT_SIZE_TEXCOORD;

                converted.add(texcoords[tindex]);
                converted.add(texcoords[tindex + 1]);
            }

            converted.restrictSize();
            data.texCoords().provider(converted.array());
        }

        protected void unIndexNormals(FSInstance.Data data, short[] indices){
            float[] normals = data.normals().provider();
            VLListFloat converted = new VLListFloat(normals.length, normals.length / 2);

            for(int i2 = 0; i2 < indices.length; i2++){
                int nindex = indices[i2] * UNIT_SIZE_NORMAL;

                converted.add(normals[nindex]);
                converted.add(normals[nindex + 1]);
                converted.add(normals[nindex + 2]);
            }

            converted.restrictSize();
            data.normals().provider(converted.array());
        }

        protected final void buildFirst(Scanner scanner, FSM.Data fsm){
            operate(firstfuncs, scanner, fsm);
        }

        protected final void buildRest(Scanner scanner, FSM.Data fsm){
            operate(restfuncs, scanner, fsm);
        }

        protected final BufferStep bufferFunc(int element){
            return buffersteps[element];
        }

        private final void operate(VLListType<BuildStep> funcs, Scanner scanner, FSM.Data fsm){
            FSInstance instance = new FSInstance();

            DataGroup datagroup = scanner.datagroup;
            FSMesh mesh = scanner.mesh;
            FSInstance.Data data = instance.data;
            FSBufferLayout layout = scanner.layout;
            VLArrayShort indices = mesh.indices;

            mesh.add(instance);

            int size = funcs.size();
            int index = mesh.size() - 1;

            for(int i = 0; i < size; i++){
                funcs.get(i).process(this, datagroup.get(index), mesh, indices, instance, data, fsm, layout);
            }
        }

        protected final boolean checkDebug(){
            return firstfuncs.size() == 0;
        }

        public final void stringify(StringBuilder info, Object hint){
            info.append("SYNC_MODELSET_TO_MODELARRAY[");
            info.append(SYNC_MODELCLUSTER_AND_MODELARRAY);
            info.append("]\nSYNC_MODELARRAY_AND_BUFFER[");
            info.append(SYNC_MODELARRAY_AND_BUFFER);
            info.append("]\nSYNC_MODELARRAY_AND_SCHEMATICS[");
            info.append(SYNC_MODELARRAY_AND_SCHEMATICS);
            info.append("]\nSYNC_POSITION_AND_SCHEMATICS[");
            info.append(SYNC_POSITION_AND_SCHEMATICS);
            info.append("]\nSYNC_POSITION_AND_BUFFER[");
            info.append(SYNC_POSITION_AND_BUFFER);
            info.append("]\nSYNC_COLOR_AND_BUFFER[");
            info.append(SYNC_COLOR_AND_BUFFER);
            info.append("]\nSYNC_TEXCOORD_AND_BUFFER[");
            info.append(SYNC_TEXCOORD_AND_BUFFER);
            info.append("]\nSYNC_NORMAL_AND_BUFFER[");
            info.append(SYNC_NORMAL_AND_BUFFER);
            info.append("]\nSYNC_INDICES_AND_BUFFER[");
            info.append(SYNC_INDICES_AND_BUFFER);
            info.append("]\nENABLE_COLOR_FILL[");
            info.append(ENABLE_COLOR_FILL);
            info.append("]\nLOAD_MODELS[");
            info.append(LOAD_MODELS);
            info.append("]\nLOAD_POSITIONS[");
            info.append(LOAD_POSITIONS);
            info.append("]\nLOAD_TEXCOORDS[");
            info.append(LOAD_TEXCOORDS);
            info.append("]\nLOAD_COLORS[");
            info.append(LOAD_COLORS);
            info.append("]\nLOAD_NORMALS[");
            info.append(LOAD_NORMALS);
            info.append("]\nLOAD_INDICES[");
            info.append(LOAD_INDICES);
            info.append("]\nBUFFER_MODELS[");
            info.append(BUFFER_MODELS);
            info.append("]\nBUFFER_POSITIONS[");
            info.append(BUFFER_POSITIONS);
            info.append("]\nBUFFER_TEXCOORDS[");
            info.append(BUFFER_TEXCOORDS);
            info.append("]\nBUFFER_COLORS[");
            info.append(BUFFER_COLORS);
            info.append("]\nBUFFER_NORMALS[");
            info.append(BUFFER_NORMALS);
            info.append("]\nBUFFER_INDICES[");
            info.append(BUFFER_INDICES);
            info.append("]\nINSTANCE_SHARE_POSITIONS[");
            info.append(INSTANCE_SHARE_POSITIONS);
            info.append("]\nINSTANCE_SHARE_TEXCOORDS[");
            info.append(INSTANCE_SHARE_TEXCOORDS);
            info.append("]\nINSTANCE_SHARE_COLORS[");
            info.append(INSTANCE_SHARE_COLORS);
            info.append("]\nINSTANCE_SHARE_NORMALS[");
            info.append(INSTANCE_SHARE_NORMALS);
            info.append("]\nCONVERT_POSITIONS_TO_MODELARRAYS[");
            info.append(CONVERT_POSITIONS_TO_MODELARRAYS);
            info.append("]\nDRAW_MODE_INDEXED[");
            info.append(DRAW_MODE_INDEXED);
            info.append("]\n");
        }
        

        private static final BuildStep MODEL_INITIALIZE = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.data.model(new FSModelArray());
                instance.modelCluster(new FSModelCluster(2, 2));
            }
        };
        private static final BuildStep MODEL_SYNC_MODELCLUSTER_AND_MODELARRAY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.modelCluster().SYNCER.add(new FSModelArray.Definition(instance.model(), true));
            }
        };
        private static final BuildStep MODEL_SYNC_MODELARRAY_AND_SCHEMATICS = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.model().SYNCER.add(new FSSchematics.DefinitionModel(instance.schematics));
            }
        };
        private static final BuildStep MODEL_ADJUST_BUFFER_CAPACITY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                layout.increaseTargetCapacities(ELEMENT_MODEL, data.model().size());
            }
        };


        private static final BuildStep POSITION_SET_DEFAULT = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.positions(new VLArrayFloat(fsm.positions.array()));
            }
        };
        private static final BuildStep POSITION_SET_SHARED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.positions(new VLArrayFloat(fsm.positions.array()));
            }
        };
        private static final BuildStep POSITION_UNINDEX = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                assembler.unIndexPositions(data, indices.provider());
            }
        };
        private static final BuildStep POSITION_BUILD_MODELSET = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                assembler.buildModelSetFromSchematics(instance);
            }
        };
        private static final BuildStep POSITION_BUILD_MODELSET_AND_ALL_ELSE = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                assembler.buildModelSetFromSchematics(instance);
                assembler.centralizePositions(instance);
                instance.schematics.updateBoundaries();
            }
        };
        private static final BuildStep POSITION_INIT_SCHEMATICS = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                FSSchematics schematics = instance.schematics;
                schematics.initialize();
                schematics.updateBoundaries();
            }
        };
        private static final BuildStep POSITION_SHARE_SCHEMATICS = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.schematics.updateBoundaries(mesh.first().schematics);
            }
        };
        private static final BuildStep POSITION_SYNC_WITH_SCHEMATICS = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.positions().SYNCER.add(new FSSchematics.DefinitionPosition(instance.schematics));
            }
        };
        private static final BuildStep POSITION_ADJUST_BUFFER_CAPACITY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                layout.increaseTargetCapacities(ELEMENT_POSITION, data.positions().size());
            }
        };


        private static final BuildStep COLOR_AUTOFILL_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.data.colors(new VLArrayFloat(pack.replacementcolor.provider().clone()));
            }
        };
        private static final BuildStep COLOR_AUTOFILL_NONE_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                instance.data.colors(new VLArrayFloat(pack.replacementcolor.provider().clone()));
                assembler.unIndexColors(data, indices.provider());
            }
        };
        private static final BuildStep COLOR_FILE_LOADED_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.colors(new VLArrayFloat(fsm.colors.array()));
            }
        };
        private static final BuildStep COLOR_FILE_LOADED_NONE_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.colors(new VLArrayFloat(fsm.colors.array()));
                assembler.unIndexColors(data, indices.provider());
            }
        };
        private static final BuildStep COLOR_SHARE = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.colors(new VLArrayFloat(mesh.get(0).colors().provider()));
            }
        };
        private static final BuildStep COLOR_ADJUST_BUFFER_CAPACITY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                layout.increaseTargetCapacities(ELEMENT_COLOR, data.colors().size());
            }
        };


        private static final BuildStep TEXTURE_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.texCoords(new VLArrayFloat(fsm.texcoords.array()));
            }
        };
        private static final BuildStep TEXTURE_NONE_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.texCoords(new VLArrayFloat(fsm.texcoords.array()));
                assembler.unIndexTexCoords(data, indices.provider());
            }
        };
        private static final BuildStep TEXTURE_SHARE = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.texCoords(new VLArrayFloat(mesh.get(0).texCoords().provider()));
            }
        };
        private static final BuildStep TEXTURE_ADJUST_BUFFER_CAPACITY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                layout.increaseTargetCapacities(ELEMENT_TEXCOORD, data.texCoords().size());
            }
        };


        private static final BuildStep NORMAL_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.normals(new VLArrayFloat(fsm.normals.array()));
            }
        };
        private static final BuildStep NORMAL_NONE_INDEXED = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.normals(new VLArrayFloat(fsm.normals.array()));
                assembler.unIndexNormals(data, indices.provider());
            }
        };
        private static final BuildStep NORMAL_SHARE = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                data.normals(new VLArrayFloat(mesh.get(0).normals().provider()));
            }
        };
        private static final BuildStep NORMAL_ADJUST_BUFFER_CAPACITY = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout){
                layout.increaseTargetCapacities(ELEMENT_NORMAL, data.normals().size());
            }
        };


        private static final BuildStep MISC_USE_DATA_PACK = new BuildStep(){
            @Override
            protected void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout) {
                instance.colorTexture(pack.colortexture);
                instance.lightMaterial(pack.material);
                instance.lightMap(pack.map);
            }
        };


        private static final BufferStep BUFFER_NO_SYNC = new BufferStep(){
            @Override
            protected void process(FSBufferManager manager, int index, VLArray array, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
                manager.buffer(index, array, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
            }

            @Override
            protected void process(FSBufferManager manager, int index, VLArray array){
                manager.buffer(index, array);
            }
        };
        private static final BufferStep BUFFER_SYNC = new BufferStep(){
            @Override
            protected void process(FSBufferManager manager, int index, VLArray array, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride){
                manager.bufferSync(index, array, arrayoffset, arraycount, unitoffset, unitsize, unitsubcount, stride);
            }

            @Override
            protected void process(FSBufferManager manager, int index, VLArray array){
                manager.bufferSync(index, array);
            }
        };


        private abstract static class BuildStep{

            protected abstract void process(Assembler assembler, DataPack pack, FSMesh mesh, VLArrayShort indices, FSInstance instance, FSInstance.Data data, FSM.Data fsm, FSBufferLayout layout);
        }
        protected abstract static class BufferStep{

            protected abstract void process(FSBufferManager manager, int index, VLArray array, int arrayoffset, int arraycount, int unitoffset, int unitsize, int unitsubcount, int stride);

            protected abstract void process(FSBufferManager manager, int index, VLArray array);
        }
        private abstract static class PostProcessStep{
            
            protected abstract void process(Assembler assembler, FSMesh mesh, DataGroup data);
        }
    }
}