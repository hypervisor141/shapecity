//package com.shayan.hive;
//
//import android.opengl.GLES30;
//import android.view.MotionEvent;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//import firestone.FSActivity;
//import firestone.FSBounds;
//import firestone.FSCircleBounds;
//import firestone.FSConstructor;
//import firestone.FSControl;
//import firestone.FSInput;
//import firestone.FSInstance;
//import firestone.FSMath;
//import firestone.FSMesh;
//import firestone.FSModelArray;
//import firestone.FSModelSetArray;
//import firestone.FSProgram;
//import firestone.FSRenderer;
//import firestone.FSSchematics;
//import firestone.FSState;
//import firestone.FSTools;
//import firestone.FSVertexBuffer;
//import vanguard.VLArray;
//import vanguard.VLFloatArray;
//import vanguard.VLFloatBuffer;
//import vanguard.VLShortArray;
//import vanguard.VLStrictArray;
//import vanguard.VLSyncer;
//import vanguard.VLV;
//import vanguard.VLVConstant;
//import vanguard.VLVProcessor;
//import vanguard.VLVSetArray;
//
//public final class CellHive extends FSConstructor{
//
//    protected static final int SIDE_COUNTS = 6;
//
//    protected static final int BUFFER_MODEL_INDEX = 0;
//    protected static final int BUFFER_COLOR_INDEX = 1;
//    protected static final int BUFFER_POSITION_INDEX = 2;
//    protected static final int BUFFER_INDEX_INDEX = 3;
//
//    protected static final int MODEL_POSITION_INDEX = 0;
//    protected static final int MODEL_WAVE_INDEX = 1;
//    protected static final int MODEL_SCROLL_INDEX = 2;
//
//    protected static final int COLORSET_WAVE_INDEX = 0;
//
//    protected static final int PROCESSOR_MODEL_WAVER_INDEX = 0;
//    protected static final int PROCESSOR_COLOR_WAVER_INDEX = 1;
//
//    private static VLFloatArray CELLVERTICES;
//    private static VLShortArray CELLINDICES;
//
//    private static Random RAND = new Random();
//
//    private ArrayList<Integer> elevated;
//    private float[][] coordinates;
//    private float[][] colors;
//    private float cellradius;
//    private float radianoffset;
//    private float zvalue;
//
//    private int modelbindpoint;
//    private int colorbindpoint;
//
//    protected CellHive(float[][] coordinates, float[][] colors, float cellradius, float radianoffset, float zvalue, int modelbindpoint, int colorbindpoint){
//        this.coordinates = coordinates;
//        this.colors = colors;
//        this.cellradius = cellradius;
//        this.radianoffset = radianoffset;
//        this.zvalue = zvalue;
//        this.modelbindpoint = modelbindpoint;
//        this.colorbindpoint = colorbindpoint;
//
//        elevated = new ArrayList<>(10);
//    }
//
//    @Override
//    protected void initializeStaticResources(FSActivity act){
//        if(CELLVERTICES == null){
//            CELLVERTICES = new VLFloatArray(FSTools.createVerticesForTriangleFan(SIDE_COUNTS, cellradius, radianoffset, zvalue, true));
//            CELLINDICES = new VLShortArray(FSTools.createIndicesForTriangleFan(SIDE_COUNTS));
//        }
//    }
//
//    @Override
//    protected void initializePrograms(FSActivity act){
//        FSProgram program = new FSProgram(new String[]{
//                "#version 300 es\n" +
//                        "layout(location = 0) in vec4 position;" +
//
//                        "layout(std140) uniform MODELS{" +
//                        "   mat4 model[" + coordinates[0].length + "];" +
//                        "};" +
//                        "layout(std140) uniform COLORS{" +
//                        "   vec4 color[" + coordinates[0].length + "];" +
//                        "};" +
//                        "layout(std140) uniform VIEWPERS{" +
//                        "   mat4 vp;" +
//                        "};" +
//
//                        "out vec4 vcolor;" +
//
//                        "void main(){" +
//                        "   gl_Position = vp * model[gl_InstanceID] * position;" +
//                        "   vcolor = color[gl_InstanceID];" +
//                        "}",
//
//                "#version 300 es\n" +
//                        "precision mediump float;" +
//                        "in vec4 vcolor;" +
//                        "out vec4 fragColor;" +
//
//                        "void main(){" +
//                        "   fragColor = vcolor;" +
//                        "}"
//        }, new int[]{
//                FSGLES.GL_VERTEX_SHADER,
//                FSGLES.GL_FRAGMENT_SHADER
//        });
//
//        FSControl.getSceneConfig().bindViewPerspectiveUBO(program, program.getUniformBlockIndex("VIEWPERS"));
//        programs.add(program);
//    }
//
//    @Override
//    protected void initializeMeshes(FSActivity act){
//        meshes.resizerCount(50);
//
//        float[] x = coordinates[0];
//        float[] y = coordinates[1];
//
//        VLVProcessor modelwaver = new VLVProcessor(x.length, 1);
//        VLVProcessor colorwaver = new VLVProcessor(x.length, 1);
//        VLVProcessor modelelevator = new VLVProcessor(x.length, 1);
//        VLVProcessor colorelevator = new VLVProcessor(x.length, 1);
//
//        FSMesh mesh = new FSMesh(x.length);
//        mesh.fullSizePosition(true);
//        mesh.indices(CELLINDICES);
//        add(mesh);
//
//        for(int i = 0; i < x.length; i++){
//            FSInstance instance = new FSInstance();
//            FSInstance.Data prims = instance.data();
//
//            prims.model(new FSModelArray());
//            prims.positions(new VLFloatArray(CELLVERTICES.provider().clone()));
//            prims.colors(new VLFloatArray(colors[i]));
//
//            FSSchematics schematics = instance.schematics();
//            schematics.inputBounds().add(new FSCircleBounds(schematics, 50, 50, FSBounds.MODE_DIMENSION_PERCENTAGE, FSBounds.MODE_DIMENSION_PERCENTAGE, 50));
//            schematics.SYNCER.add(new VLSyncer.SimpleEntry(new FSSchematics.Definition(), prims.positions(), schematics));
//            schematics.initialize();
//
//            FSModelSetArray modelset = instance.modelSet();
//            modelset.addSet(4, 1);
//            modelset.addSet(4, 1);
//            modelset.addSet(4, 1);
//
//            modelset.addTranslation(MODEL_POSITION_INDEX, new VLVConstant(x[i]), new VLVConstant(y[i]), VLVConstant.ZERO);
//            modelset.addScale(MODEL_WAVE_INDEX, VLVConstant.ONE, VLVConstant.ONE, VLVConstant.ONE);
//            modelset.addTranslation(MODEL_SCROLL_INDEX, VLVConstant.ZERO, VLVConstant.ZERO, VLVConstant.ZERO);
//
//            VLVSetArray colorset = new VLVSetArray(2, 1);
//            colorset.addSet(1, 1);
//
//            modelwaver.addSetArray(modelset, MODEL_WAVE_INDEX, 0);
//            colorwaver.addSetArray(colorset, COLORSET_WAVE_INDEX, 0);
//
//            mesh.add(instance);
//        }
//
//        processors.add(modelwaver);
//        processors.add(colorwaver);
//        processors.add(modelelevator);
//        processors.add(colorelevator);
//
//        coordinates = null;
//    }
//
//    @Override
//    protected void initializeBuffers(FSActivity act){
//        FSProgram program = programs.get(0);
//        VLStrictArray<FSInstance> instances = first().instances();
//
//        buffermanager.addUniformBufferFloat(UNIT_SIZE_MODEL * instances.size(), FSGLES.GL_DYNAMIC_DRAW);
//        buffermanager.addUniformBufferFloat(UNIT_SIZE_COLOR * instances.size(), FSGLES.GL_DYNAMIC_DRAW);
//        buffermanager.addVertexBufferFloat(CELLVERTICES.size(), FSGLES.GL_ARRAY_BUFFER, FSGLES.GL_DYNAMIC_DRAW);
//        buffermanager.addVertexBufferShort(CELLINDICES.size(), FSGLES.GL_ELEMENT_ARRAY_BUFFER, FSGLES.GL_STATIC_DRAW);
//
//        for(int i = 0; i < instances.size(); i++){
//            FSInstance.Data prims = instances.get(i).data();
//
//            buffermanager.buffer(BUFFER_MODEL_INDEX, prims.model());
//            buffermanager.buffer(BUFFER_COLOR_INDEX, prims.colors());
//        }
//
//        buffermanager.buffer(BUFFER_POSITION_INDEX, CELLVERTICES);
//        buffermanager.buffer(BUFFER_INDEX_INDEX, CELLINDICES);
//        buffermanager.upload();
//
//        buffermanager.bindUBO(BUFFER_MODEL_INDEX, program, program.getUniformBlockIndex("MODELS"), modelbindpoint);
//        buffermanager.bindUBO(BUFFER_COLOR_INDEX, program, program.getUniformBlockIndex("COLORS"), colorbindpoint);
//    }
//
//    @Override
//    protected void initializeTextures(FSActivity act){
//
//    }
//
//    @Override
//    protected void initializeSyncLinks(FSActivity act){
//        VLStrictArray<FSInstance> instances = first().instances();
//        FSVertexBuffer modelbuffer = buffermanager.get(BUFFER_MODEL_INDEX);
//        FSVertexBuffer.Definition vbodefinition = new FSVertexBuffer.Definition();
//        int instancecount = instances.size();
//
//        for(int i = 0; i < instancecount; i++){
//            FSInstance instance = instances.get(i);
//            FSInstance.Data prims = instance.data();
//            FSModelSetArray modelset = instance.modelSet();
//
//            VLSyncer.ChainedEntry entry = new VLSyncer.ChainedEntry();
//            entry.add(modelset);
//            entry.add(prims.model());
//            entry.add(modelbuffer.provider());
//            entry.add(modelbuffer);
//
//            entry.add(new FSModelArray.Definition(true));
//            entry.add(new VLFloatBuffer.DefinitionArray(UNIT_SIZE_MODEL * i));
//            entry.add(vbodefinition);
//
//            modelset.SYNCER.add(entry);
//            modelset.SYNCER.sync();
//        }
//    }
//
//    @Override
//    protected void update(){
//        buffermanager.updateIfNeeded();
//    }
//
//    @Override
//    protected void draw(){
//        FSState.depthMask(false);
//        FSProgram program = programs.get(0);
//        FSMesh mesh = first();
//        program.use();
//
//        buffermanager.bind(BUFFER_POSITION_INDEX);
//        program.enableVertexAttribArray(0);
//        program.vertexAttribPointer(0, FSConstructor.UNIT_SIZE_FULLSIZED_POSITION, FSGLES.GL_FLOAT, false, FSConstructor.UNIT_BYTES_FULLSIZED_POSITION, 0);
//
//        buffermanager.bind(BUFFER_INDEX_INDEX);
//
//        int indexcount = first().instances().size();
//        int index = 0;
//
//        FSRenderer.drawElementsInstanced(FSGLES.GL_TRIANGLE_FAN, indexcount, FSGLES.GL_UNSIGNED_SHORT, 0, mesh.size());
//
//        buffermanager.unbindArrayBuffer();
//        buffermanager.unbindElementBuffer();
//        program.unuse();
//    }
//
//    @Override
//    protected void postFrameSwap(){
//
//    }
//
//    protected float[] getColor(int index){
//        return colors[index];
//    }
//
//
//    protected void addWaveAnimations(){
//        FSVertexBuffer colorbuffer = bufferManager().get(BUFFER_COLOR_INDEX);
//        VLVProcessor colorwaver = processors.get(PROCESSOR_COLOR_WAVER_INDEX);
//        int cycles = calculateDelays(first().instances());
//
//        VLStrictArray<FSInstance> instances = first().instances();
//        int instancecount = instances.size();
//
//        for(int i = 0; i < instances.size(); i++){
//            float[] color = getColor(i);
//
//            FSInstance ins = instances.get(i);
//            FSModelSetArray modelset = ins.modelSet();
//            VLVSetArray colorset = (VLVSetArray)colorwaver.getTarget(i);
//
//            VLSyncer.ChainedEntry entry = new VLSyncer.ChainedEntry();
//            entry.add(colorset);
//            entry.add(ins.colors());
//            entry.add(colorbuffer.provider());
//            entry.add(colorbuffer);
//            entry.add(new VLArray.DefinitionSet(COLORSET_WAVE_INDEX, 0, 0));
//            entry.add(new VLFloatBuffer.DefinitionArray(color.length * i));
//            entry.add(new FSVertexBuffer.Definition());
//
//            colorset.SYNCER.add(entry);
//
//            Animations.animateScale(modelset,
//                    MODEL_WAVE_INDEX,
//                    Animations.ANIMATION_WAVE_SCALE_START,
//                    Animations.ANIMATION_WAVE_SCALE_END,
//                    Animations.ANIMATION_WAVE_SCALE_CYCLES,
//                    VLV.LOOP_RETURN_ONCE,
//                    Animations.ANIMATION_WAVE_INTERPOLATOR);
//
//            Animations.animateColor(colorset,
//                    ins.colors().size(),
//                    COLORSET_WAVE_INDEX,
//                    color,
//                    Animations.ANIMATION_WAVE_CELLCOLOR,
//                    Animations.ANIMATION_WAVE_COLOR_CYCLES,
//                    VLV.LOOP_RETURN_ONCE,
//                    Animations.ANIMATION_WAVE_INTERPOLATOR);
//        }
//
//        FSInput.addTouchTrigger(new FSInput.GLTouchTrigger(this, new FSInput.GLTouchListener(){
//
//            @Override
//            public int onGLTouch(FSConstructor m, MotionEvent motionEvent, int i, int i1){
//                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                    startWaveAnimation(i);
//                    return FSInput.COLLISION_CHECK_FULL_STOP;
//                }
//
//                return FSInput.COLLISION_CHECK_CONTINUE;
//            }
//        }));
//    }
//
//    private int calculateDelays(VLStrictArray<FSInstance> instances){
//        int[] MAXES = new int[Animations.DELAYS.length];
//        int cycles = 0;
//
//        for(int i = 0; i < instances.size(); i++){
//            FSSchematics s = instances.get(i).schematics();
//
//            for(int i2 = 0; i2 < instances.size(); i2++){
//                if(i == i2){
//                    Animations.DELAYS[i][i2] = 0;
//
//                }else{
//                    FSSchematics s2 = instances.get(i2).schematics();
//                    s2.modelCenterPoint(Animations.CenterPointCache);
//                    int delay = (int)(s.modelCenterLengthFromPoint(Animations.CenterPointCache) * Animations.ANIMATION_WAVE_DELAY_BASE_MULTIPLIER);
//
//                    if(MAXES[i] < delay){
//                        MAXES[i] = delay;
//                    }
//
//                    Animations.DELAYS[i][i2] = delay;
//                }
//            }
//        }
//
//        for(int i = 0; i < instances.size(); i++){
//            for(int i2 = 0; i2 < instances.size(); i2++){
//                if(i != i2){
//                    int max = MAXES[i];
//                    float delay = FSMath.range(Animations.DELAYS[i][i2], 0, max, 0, 1);
//                    int finaldelay = (int) FSMath.range((float) Animations.ANIMATION_WAVE_INTERPOLATOR.process(delay), 0, max);
//                    Animations.DELAYS[i][i2] = finaldelay;
//
//                    if(cycles < finaldelay){
//                        cycles = finaldelay;
//                    }
//                }
//            }
//        }
//
//        return cycles;
//    }
//
//    protected void startWaveAnimation(final int touchedcell){
//        VLVProcessor modelwaver = processors.get(PROCESSOR_MODEL_WAVER_INDEX);
//        VLVProcessor colorwaver = processors.get(PROCESSOR_COLOR_WAVER_INDEX);
//
//        calculateDelays(first().instances());
//        int[] relativedelays = Animations.DELAYS[touchedcell];
//
//        for(int i = 0; i < meshes.size(); i++){
//            int delay = relativedelays[i];
//
//            modelwaver.setDelay(i, (int)(delay * Animations.ANIMATION_WAVE_SCALE_DELAY_MULTIPLIER));
//            colorwaver.setDelay(i, (int)(delay * Animations.ANIMATION_WAVE_COLOR_DELAY_MULTIPLIER));
//        }
//
//        modelwaver.reset();
//        colorwaver.reset();
//
//        modelwaver.sync();
//        colorwaver.sync();
//
//        modelwaver.start();
//        colorwaver.start();
//    }
//}
//
