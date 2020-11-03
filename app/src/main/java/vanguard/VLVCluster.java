package vanguard;

public class VLVCluster extends VLV{

    protected VLListType<VLListType<VLListType<VLV>>> cluster;

    public VLVCluster(int initialcapacity, int resizercount){
        cluster = new VLListType<>(initialcapacity, resizercount);
    }

    public VLVCluster(){
        cluster = new VLListType<>(1, 5);
    }


    public void addSet(int initialcapacity, int resizercount){
        cluster.add(new VLListType<VLListType<VLV>>(initialcapacity, resizercount));
    }

    public void addSet(int index, int initialcapacity, int resizercount){
        cluster.add(index, new VLListType<VLListType<VLV>>(initialcapacity, resizercount));
    }

    public void addSet(VLListType<VLListType<VLV>> set){
        cluster.add(set);
    }

    public void addSet(int index, VLListType<VLListType<VLV>> set){
        cluster.add(index, set);
    }

    public void addRow(int setindex, int initialcapacity, int resizercount){
        cluster.get(setindex).add(new VLListType<VLV>(initialcapacity, resizercount));
    }

    public void addRow(int setindex, VLListType<VLV> row){
        cluster.get(setindex).add(row);
    }

    public void addColumn(int setindex, int rowindex, VLV element){
        cluster.get(setindex).get(rowindex).add(element);
    }

    public void setColumn(int setindex, int rowindex, int columnindex, VLV value){
        cluster.get(setindex).get(rowindex).set(columnindex, value);
    }

    public void swapSets(int index, int toindex){
        VLListType a = cluster.get(index);
        VLListType a2 = cluster.get(toindex);

        cluster.set(index, a2);
        cluster.set(toindex, a);
    }

    public void swapRows(int setindex, int index, int toindex){
        VLListType<VLListType<VLV>> set = cluster.get(setindex);

        VLListType a = set.get(index);
        VLListType a2 = set.get(toindex);

        set.set(index, a2);
        set.set(toindex, a);
    }

    public VLListType<VLListType<VLV>> removeSet(int setindex){
        return cluster.remove(setindex);
    }

    public VLListType<VLV> removeSetRow(int setindex, int rowindex){
        return cluster.get(setindex).remove(rowindex);
    }

    public VLV getColumn(int setindex, int rowindex, int columnindex){
        return cluster.get(setindex).get(rowindex).get(columnindex);
    }

    public VLListType<VLV> getRow(int setindex, int rowindex){
        return cluster.get(setindex).get(rowindex);
    }

    public VLListType<VLListType<VLV>> getSet(int setindex){
        return cluster.get(setindex);
    }

    public int sizeArray(){
        return cluster.size();
    }

    public int sizeSet(int setindex){
        return cluster.get(setindex).size();
    }

    public int sizeRow(int setindex, int rowindex){
        return cluster.get(setindex).get(rowindex).size();
    }

    @Override
    public boolean next(){
        boolean changed = false;

        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    if(row.get(i3).next()){
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    @Override
    public boolean next(int hint){
        boolean changed = false;

        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                if(row.get(i3).next()){
                    changed = true;
                }
            }
        }

        return changed;
    }

    @Override
    public void push(float amount, int cycles){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).push(amount, cycles);
                }
            }
        }
    }

    @Override
    public void push(int hint, float amount, int cycles){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                row.get(i3).push(amount, cycles);
            }
        }
    }

    @Override
    public void push(float amount){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).push(amount);
                }
            }
        }
    }

    @Override
    public void push(int hint, float amount){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                row.get(i3).push(amount);
            }
        }
    }

    @Override
    public void reset(){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).reset();
                }
            }
        }
    }

    @Override
    public void reset(int hint){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                row.get(i3).reset();
            }
        }
    }

    @Override
    public void reverse(){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).reverse();
                }
            }
        }
    }

    @Override
    public void reverse(int hint){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                row.get(i3).reverse();
            }
        }
    }

    @Override
    public void finish(){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).finish();
                }
            }
        }
    }

    @Override
    public void finish(int hint){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                row.get(i3).finish();
            }
        }
    }

    @Override
    public boolean isDone(){
        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    if(!row.get(i3).isDone()){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean isDone(int hint){
        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                if(!row.get(i3).isDone()){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int getCycles(){
        int cycles = 0;

        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    cycles += row.get(i3).getCycles();
                }
            }
        }

        return cycles;
    }

    @Override
    public int getCycles(int hint){
        int cycles = 0;

        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                cycles += row.get(i3).getCycles();
            }
        }

        return cycles;
    }

    @Override
    public int getAbsoluteCycles(){
        int cycles = 0;

        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    cycles += row.get(i3).getAbsoluteCycles();
                }
            }
        }

        return cycles;
    }

    @Override
    public int getAbsoluteCycles(int hint){
        int cycles = 0;

        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                cycles += row.get(i3).getAbsoluteCycles();
            }
        }

        return cycles;
    }

    @Override
    public int getCyclesRemaining(){
        int cycles = 0;

        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    cycles += row.get(i3).getCyclesRemaining();
                }
            }
        }

        return cycles;
    }

    @Override
    public int getCyclesRemaining(int hint){
        int cycles = 0;

        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                cycles += row.get(i3).getCyclesRemaining();
            }
        }

        return cycles;
    }

    @Override
    public int getAbsoluteCyclesRemaining(){
        int cycles = 0;

        for(int i = 0; i < cluster.size(); i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                for(int i3 = 0; i3 < row.size(); i3++){
                    cycles += row.get(i3).getAbsoluteCyclesRemaining();
                }
            }
        }

        return cycles;
    }

    @Override
    public int getAbsoluteCyclesRemaining(int hint){
        int cycles = 0;

        VLListType<VLListType<VLV>> set = cluster.get(hint);

        for(int i2 = 0; i2 < set.size(); i2++){
            VLListType<VLV> row = set.get(i2);

            for(int i3 = 0; i3 < row.size(); i3++){
                cycles += row.get(i3).getAbsoluteCyclesRemaining();
            }
        }

        return cycles;
    }

    @Override
    public void stringify(StringBuilder src, Object hint){
        boolean verbose = (boolean)hint;

        src.append("type[");
        src.append(getClass().getSimpleName());
        src.append("] array[");
        src.append(sizeArray());
        src.append("] content[");

        if(verbose){
            src.append("\n");
        }

        int size = cluster.size();

        for(int i = 0; i < size; i++){
            VLListType<VLListType<VLV>> set = cluster.get(i);
            src.append("set[");
            src.append(i);
            src.append("] size[");
            src.append(set.size());
            src.append("] :\n");

            for(int i2 = 0; i2 < set.size(); i2++){
                VLListType<VLV> row = set.get(i2);

                src.append("row[");
                src.append(i);
                src.append("] size[");
                src.append(set.size());
                src.append("] :\n");

                for(int i3 = 0; i3 < row.size(); i3++){
                    row.get(i3).stringify(src, verbose);

                    if(i3 != row.size() - 1){
                        src.append(verbose ? ",\n" : ", ");
                    }
                }

                src.append("]\n");
            }

            if(i < size - 1){
                src.append("]\n");
            }
        }

        src.append("]");
    }
}
