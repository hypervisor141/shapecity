//package com.shayan.hive;
//
//import android.content.res.AssetManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.openFS.FSES30;
//
//import firestone.FSActivity;
//import firestone.FSConstructor;
//import firestone.FSControl;
//import firestone.FSInstance;
//import firestone.FSModelArray;
//import firestone.FSModelSetArray;
//import firestone.FSProgram;
//import firestone.FSRenderer;
//import firestone.FSSchematics;
//import firestone.FSTexture;
//import firestone.FSVModelSetArray;
//import firestone.FSWaveFront;
//import vanguard.VLFloatArray;
//import vanguard.VLShortArray;
//import vanguard.VLSyncer;
//import vanguard.VLVConstant;
//
//public class Terrain extends FSConstructor {
//
//    private static VLFloatArray VERTICES;
//    private static VLFloatArray TEXCOORDS;
//    private static VLShortArray INDICES;
//    private static Bitmap TEXTURE;
//
//    private static final float TERRAIN_SCALE = 1;
//    private static final int MODEL_SCALE_INDEX = 0;
//
//    private static final int VERTEX_BUFFER_INDQEX = 0;
//    private static final int TEXCOORDS_BUFFER_INDEX = 1;
//    private static final int INDEX_BUFFER_INDEX = 2;
//
//    protected Terrain(){
//
//    }
//
//    @Override
//    protected void initializeStaticResources(FSActivity act){
//        if(VERTICES == null){
//            try{
//                AssetManager assets = act.getAssets();
//                FSWaveFront object = new FSWaveFront(1, 100, 100);
//                object.load(assets.open("sanddunes.obj"), true);
//                FSWaveFront.Data data = object.objects.get(0);
//
//                TEXTURE = BitmapFactory.decodeStream(assets.open("tex4.jpg"));
//                VERTICES = new VLFloatArray(data.vertices.array());
//                TEXCOORDS = new VLFloatArray(data.texcoords.array());
//                INDICES = new VLShortArray(data.indices.array());
//
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    protected void initializePrograms(FSActivity act){
//        FSProgram program = new FSProgram(new String[]{
//                "#version 300 es\n" +
//                        "layout(location = 0) in vec4 position;" +
//                        "layout(location = 1) in vec2 tcoords;" +
//                        "layout(location = 0) uniform mat4 model;" +
//                        "out vec2 texcoords;" +
//
//                        "layout(std140) uniform VIEWPERS{" +
//                        "   mat4 vp;" +
//                        "};" +
//
//                        "void main(){" +
//                        "   FS_Position = vp * model * position;" +
//                        "   texcoords = tcoords;" +
//                        "}",
//
//                "#version 300 es\n" +
//                        "precision mediump float;" +
//                        "in vec2 texcoords;" +
//                        "layout(location = 1) uniform sampler2D unit;" +
//                        "out vec4 fragColor;" +
//
//                        "void main(){" +
//                        "   fragColor = texture(unit, texcoords);" +
//                        "}"
//        }, new int[]{
//                FSES30.FS_VERTEX_SHADER,
//                FSES30.FS_FRAGMENT_SHADER
//        });
//
//        FSControl.getSceneConfig().bindViewPerspectiveUBO(program, program.getUniformBlockIndex("VIEWPERS"));
//        programs.add(program);
//    }
//
//    @Override
//    protected void initializeInstances(FSActivity act){
//        setVertexFullSize(true);
//        instances.resizerCount(1);
//
//        FSInstance instance = new FSInstance();
//        FSInstance.Primitives prims = instance.primitives();
//
//        prims.model(new FSModelArray());
//        prims.positions(new VLFloatArray(VERTICES.provider().clone()));
//        prims.indices(INDICES);
//        prims.texcoords(TEXCOORDS);
//
//        FSSchematics schematics = instance.schematics();
//        schematics.SYNCER.add(new VLSyncer.SimpleEntry(new FSSchematics.Definition(), prims.positions(), schematics));
//        schematics.initialize();
//
//        FSModelSetArray modelset = instance.modelSet();
//        modelset.addSet(4, 1);
//        modelset.addSet(4, 1);
//
//        VLVConstant scale = new VLVConstant(TERRAIN_SCALE);
//
//        modelset.addScale(MODEL_SCALE_INDEX, scale, scale, scale);
//        modelset.addTranslation(1, VLVConstant.ZERO, VLVConstant.ZERO, VLVConstant.ZERO);
//    }
//
//    @Override
//    protected void initializeBuffers(FSActivity act){
//        FSProgram p = programs.get(0);
//        int instancecount = getInstanceSize();
//
//        buffermanager.addVertexBufferFloat(VERTICES.size(), FSES30.FS_ARRAY_BUFFER, FSES30.FS_STATIC_DRAW);
//        buffermanager.addVertexBufferFloat(TEXCOORDS.size(), FSES30.FS_ARRAY_BUFFER, FSES30.FS_STATIC_DRAW);
//        buffermanager.addVertexBufferShort(INDICES.size(), FSES30.FS_ELEMENT_ARRAY_BUFFER, FSES30.FS_STATIC_DRAW);
//
//        buffermanager.buffer(VERTEX_BUFFER_INDEX, VERTICES);
//        buffermanager.buffer(TEXCOORDS_BUFFER_INDEX, TEXCOORDS);
//        buffermanager.buffer(INDEX_BUFFER_INDEX, INDICES);
//
//        buffermanager.upload();
//    }
//
//    @Override
//    protected void initializeTextures(FSActivity act){
//        FSTexture texture = new FSTexture(FSES30.FS_TEXTURE_2D, 0);
//
//        texture.set(0, TEXTURE, true);
//        texture.minFilter(FSES30.FS_NEAREST);
//        texture.magFilter(FSES30.FS_NEAREST);
//        texture.wrapS(FSES30.FS_CLAMP_TO_EDGE);
//        texture.wrapT(FSES30.FS_CLAMP_TO_EDGE);
//        texture.unbind();
//        texture.deactivateUnit();
//
//        textures.add(texture);
//    }
//
//    @Override
//    protected void initializeSyncLinks(FSActivity act){
//        FSInstance instance = instances.get(0);
//        FSVModelSetArray set = instance.getModelSet();
//
//        set.SYNCER.add(new VLSyncer.SimpleEntry(new FSModelArray.Definition(true), set, instance.getModel()));
//        set.SYNCER.sync();
//    }
//
//    @Override
//    protected void update(){
//
//    }
//
//    @Override
//    protected void draw(){
//        FSInstance instance =  getFirstInstance();
//
//        FSProgram program = programs.get(0);
//        FSTexture texture = textures.get(0);
//
//        program.use();
//        texture.bind();
//
//        buffermanager.bind(VERTEX_BUFFER_INDEX);
//        program.enableVertexAttribArray(0);
//        program.vertexAttribPointer(0, FSConstructor.UNIT_SIZE_POSITION_FULLSIZE, FSES30.FS_FLOAT, false, FSConstructor.UNIT_BYTES_POSITION_FULLSIZE, 0);
//
//        buffermanager.bind(TEXCOORDS_BUFFER_INDEX);
//        program.enableVertexAttribArray(1);
//        program.vertexAttribPointer(1, FSConstructor.UNIT_SIZE_TEXCOORD, FSES30.FS_FLOAT, false, FSConstructor.UNIT_BYTES_TEXCOORD, 0);
//
//        program.setUniformMatrix4fv(0, 1, false, instance.getModel().provider(), 0);
//        program.setUniform1i(1, texture.getTextureUnit());
//
//        buffermanager.bind(INDEX_BUFFER_INDEX);
//        FSRenderer.drawElements(FSES30.FS_TRIANFSES, instance.getIndices().size(), FSES30.FS_UNSIGNED_SHORT, 0);
//
//        buffermanager.unbindArrayBuffer();
//        buffermanager.unbindElementBuffer();
//
//        program.unuse();
//        texture.unbind();
//    }
//
//    @Override
//    protected void postFrameSwap(){
//
//    }
//}
//
//
