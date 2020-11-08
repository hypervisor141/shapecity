package com.shayan.shapecity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PhoneContact implements Externalizable{

    private static final long serialVersionUID = 968793864L;

    private String num = "";
    private String name = "";
    private int localversion;
    private int id = -1;

    protected PhoneContact(String n, String name, int id, int localversion){
        num = n;
        this.name = name;
        this.id = id;
        this.localversion = localversion;
    }

    protected PhoneContact(PhoneContact src){
        copyFrom(src);
    }

    protected PhoneContact(){

    }



    protected void setName(String s){
        name = s;
    }

    protected void setNumber(String s){
        num = s;
    }

    protected void setID(int id){
        this.id = id;
    }

    protected void setLVersion(int v){
        localversion = v;
    }

    protected String getName(){
        return name;
    }

    protected String getNumber(){
        return num;
    }

    protected int getID(){
        return id;
    }

    protected int getLVersion(){
        return localversion;
    }




    protected void copyFrom(PhoneContact src){
        num = new String(src.num);
        name = new String(src.name);
        id = src.id;
        localversion = src.localversion;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeInt(id);
        out.writeUTF(name);
        out.writeUTF(num);
        out.writeInt(localversion);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        id = in.readInt();
        name = in.readUTF();
        num = in.readUTF();
        localversion = in.readInt();
    }
}
