package com.shayan.shapecity;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class Asset implements Externalizable{


    private static final long serialVersionUID = 5326326436521523L;

    private String name;
    private byte[] data;
    private long creationdate;
    private int flag;

    protected Asset(int flag, long date, String name, byte[] data){
        this.flag = flag;
        this.creationdate = date;
        this.name = name;
        this.data = data;
    }

    public Asset(){

    }



    protected void setDate(long s){
        creationdate = s;
    }

    protected void setName(String s){
        name = s;
    }

    protected void setFlag(int s){
        flag = s;
    }

    protected void setData(byte[] s){
        data = s;
    }
    
    


    protected long getDate(){
        return creationdate;
    }

    protected String getName(){
        return name;
    }

    protected int getFlag(){
        return flag;
    }

    protected byte[] getData(){
        return data;
    }





    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(flag);
        out.writeLong(creationdate);
        out.writeObject(data);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        flag = in.readInt();
        creationdate = in.readLong();
        data = (byte[])in.readObject();
    }
}
