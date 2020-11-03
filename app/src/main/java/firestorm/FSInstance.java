package firestorm;

import vanguard.VLListType;
import vanguard.VLArrayFloat;
import vanguard.VLArrayShort;

public class FSInstance{

    protected FSMesh mesh;
    protected FSSchematics schematics;
    protected FSModelCluster modelcluster;
    protected FSMeshBuffers address;

    protected FSTexture colortexture;
    protected FSLightMaterial lightmaterial;
    protected FSLightMap lightmap;

    protected States states;
    protected Data data;

    protected long id;

    public FSInstance(){
        id = FSControl.getNextID();

        data = new Data();
        states = new States();
        schematics = new FSSchematics(this);
        address = new FSMeshBuffers();
    }


    public void modelCluster(FSModelCluster set){
        modelcluster = set;
    }

    public VLArrayFloat element(int element){
        return data.element(element);
    }

    public int elementVertexCount(int element){
        return element(element).size() / FSLoader.UNIT_SIZES[element];
    }

    public void colorTexture(FSTexture texture){
        this.colortexture = texture;
    }

    public void lightMaterial(FSLightMaterial material){
        this.lightmaterial = material;
    }

    public void lightMap(FSLightMap map){
        this.lightmap = map;
    }


    public long id(){
        return id;
    }

    public int vertexSize(){
        return data.positions().size() / FSLoader.UNIT_SIZE_POSITION;
    }

    public FSMesh mesh(){
        return mesh;
    }

    public FSTexture colorTexture(){
        return colortexture;
    }

    public FSLightMaterial lightMaterial(){
        return lightmaterial;
    }

    public FSLightMap lightMap(){
        return lightmap;
    }

    public FSSchematics schematics(){
        return schematics;
    }

    public FSMeshBuffers bufferAddress(){
        return address;
    }

    public FSModelCluster modelCluster(){
        return modelcluster;
    }

    public States states(){
        return states;
    }

    public Data data(){
        return data;
    }

    public FSModelArray model(){
        return data.model();
    }

    public VLArrayFloat positions(){
        return data.positions();
    }

    public VLArrayFloat colors(){
        return data.colors();
    }

    public VLArrayFloat texCoords(){
        return data.texCoords();
    }

    public VLArrayFloat normals(){
        return data.normals();
    }

    public VLArrayShort indices(){
        return mesh.indices;
    }


    public static final class Data{

        protected VLArrayFloat[] elements;

        public Data(){
            elements = new VLArrayFloat[FSLoader.ELEMENT_TOTAL_COUNT - 1];
        }


        public void element(int element, VLArrayFloat array){
            elements[element] = array;
        }

        public void model(FSModelArray array){
            elements[FSLoader.ELEMENT_MODEL] = array;
        }

        public void positions(VLArrayFloat array){
            elements[FSLoader.ELEMENT_POSITION] = array;
        }

        public void colors(VLArrayFloat array){
            elements[FSLoader.ELEMENT_COLOR] = array;
        }

        public void texCoords(VLArrayFloat array){
            elements[FSLoader.ELEMENT_TEXCOORD] = array;
        }

        public void normals(VLArrayFloat array){
            elements[FSLoader.ELEMENT_NORMAL] = array;
        }



        public VLArrayFloat element(int element){
            return elements[element];
        }

        public FSModelArray model(){
            return (FSModelArray)elements[FSLoader.ELEMENT_MODEL];
        }

        public VLArrayFloat positions(){
            return elements[FSLoader.ELEMENT_POSITION];
        }

        public VLArrayFloat colors(){
            return elements[FSLoader.ELEMENT_COLOR];
        }

        public VLArrayFloat texCoords(){
            return elements[FSLoader.ELEMENT_TEXCOORD];
        }

        public VLArrayFloat normals(){ return elements[FSLoader.ELEMENT_NORMAL]; }
    }

    public final class States{

        protected VLListType<Data> vault;
        protected int activeindex;

        protected States(){
            activeindex = 0;
        }


        public void initialize(int initialsize, int resizer){
            vault = new VLListType<>(initialsize, resizer);
        }

        public int currentActiveIndex(){
            return activeindex;
        }

        public void activateElement(int index, int element){
            data.element(element, vault.get(index).element(element));
        }

        public void activateData(int index){
            data = vault.get(index);
            activeindex = index;
        }

        public void add(Data state){
            vault.add(state);
        }

        public Data get(int index){
            return vault.get(index);
        }

        public void set(int index, Data data){
            vault.set(index, data);
        }

        public Data remove(int index){
            return vault.remove(index);
        }

        public VLListType<Data> get(){
            return vault;
        }
    }
}
