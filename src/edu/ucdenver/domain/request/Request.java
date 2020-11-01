package edu.ucdenver.domain.request;


import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.client.ClientErrorType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Request {
    private RequestType type;
    private HashMap<String,String> fields;
    private ArrayList<HashMap<String,String>> objs;
    public static String seperater = "(?<!\\|)\\|(?!\\|)";
    public Request(RequestType type,HashMap<String,String> fields,ArrayList<HashMap<String,String>> objs){
        if(type==null) {
            this.type = RequestType.ERROR;
        }
        else{
            this.type = type;
        }
        if (fields == null){
            this.fields = new HashMap<>();
        }
        else{
            this.fields = fields;
        }
        this.fields.put("rtype",type.toString());
        if(objs == null){
            this.objs = new ArrayList<>();
        }
        else{
            this.objs=objs;
        }
    }
    public HashMap<String,String> getTable() {
        return this.fields;
    }
    public void setTable(HashMap<String,String> table) {
        this.fields = table;
    }

    public ArrayList<HashMap<String,String>> getObjs(){
        return this.objs;
    }
    public String toRaw() {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String,String> entry : this.fields.entrySet()) {
            builder.append((entry.getKey().isEmpty()?" ":entry.getKey()).replace("|","||"));
            builder.append("|");
            builder.append((entry.getValue().isEmpty()?" ":entry.getValue()).replace("|","||"));
            builder.append("|");
        }
        for(HashMap<String,String> object : this.objs){
            builder.append("store-request-obj|");
            StringBuilder innerBuilder = new StringBuilder();
            int fields = 0;
            for (HashMap.Entry<String,String> entry : object.entrySet()) {
                innerBuilder.append((entry.getKey().isEmpty()?" ":entry.getKey()).replace("|","||"));
                innerBuilder.append("|");
                innerBuilder.append((entry.getValue().isEmpty()?" ":entry.getValue()).replace("|","||"));
                innerBuilder.append("|");
                fields++;
            }
            builder.append(fields);
            builder.append('|');
            builder.append(innerBuilder);
        }
        return builder.toString();
    }
    public void send(PrintWriter output) throws IOException{
        if(output.checkError()){
            throw new IOException();
        }
        String S=this.toRaw();
      
        output.printf("%d|",S.length());
        if(output.checkError()){
            throw new IOException();
        }
        output.print(S);
        if(output.checkError()){
            throw new IOException();
        }
        output.flush();
        if(output.checkError()){
            throw new IOException();
        }
    }
    public Request(BufferedReader stream) throws ClientError {
        this.initFields();
        StringBuilder size = new StringBuilder(new String());
        int bytes = 0;
      
        try{
            while ((bytes=stream.read())>0 && (char)bytes!='|' && stream.ready()){
                size.append((char) bytes);
            }

        }
        catch(IOException e) {

            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
        String b=size.toString();
        if(b.isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
        
        int rsize = Integer.parseInt(b);
        
        if(rsize<=0){
            if(bytes <=0) {
                throw new ClientError(ClientErrorType.INVALID_SOCKET);
            }
            else{
                throw new ClientError(ClientErrorType.INVALID_REQUEST);
            }
        }
       
        char[] chars = new char[rsize];
        int charsRead = 0;
        try {
            //charsRead = stream.read();
            charsRead= stream.read(chars);



            String s = String.valueOf(chars);

            try{
               
                this.fromString(s);
                
                this.validateType();
            }
            catch (IllegalArgumentException e) {

                throw new ClientError(ClientErrorType.INVALID_REQUEST);
            }
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_REQUEST);
        }


    }
    private void initFields() {
        this.fields = new HashMap<>();
        this.type = RequestType.NOOP;
        this.objs = new ArrayList<>();
    }
    public Request(String raw) throws IllegalArgumentException {
        
        this.initFields();
        
        this.fromString(raw);
        ;
        this.validateType();
       
    }
    public void validateType()throws IllegalArgumentException {

        if(!this.fields.containsKey("rtype")) {
            throw new IllegalArgumentException("No type");
        }


        String temp = this.fields.get("rtype").toUpperCase().trim();
        if (temp!=null) {
           
        }

        this.type = RequestType.valueOf(temp);

    }
    private void fromObjString(String raw)throws IllegalArgumentException {

    }

    public void fromString(String raw) throws IllegalArgumentException {
        
        if (raw == null||raw.isEmpty() ){
            throw new IllegalArgumentException("Null request.");
        }
        
        int i = 2;
        String last= new String();
        int total = 0;

        boolean isObj=false;
        int objFields = 0;
        for(String splice : raw.split(seperater)) {
            if (splice.length() ==0 ||splice.isEmpty() || splice.equals("")||(int)splice.charAt(0)==0){
                break;
            }

            if ((i & 1) == 0 ){
                last = splice.replace("||","|");

            } else{

                if(last.equals("store-request-obj")) {

                    isObj = true;
                    this.objs.add(new HashMap<>());
                    try {
                        objFields  = Integer.parseInt(splice);
                    }
                    catch (Exception e){
                        throw new IllegalArgumentException();
                    }
                }
                else if(isObj){

                    int size = this.objs.size()-1;
                    objFields--;
                    isObj=objFields>0;
                    this.objs.get(size).put(last,splice.replace("||","|"));
                }
                else{

                    this.fields.put(last,splice.replace("||","|"));
                }

            }
            i++;
        }
        
        total+=1;

    }
    public String getField(String field) throws IllegalArgumentException {
        String res = this.fields.get(field);
        if (res == null) {
            throw new IllegalArgumentException(String.format("No field %s",field));
        }
        return res;
    }

    public RequestType getType() {
        return this.type;
    }
    public void setField(String field,String resource) {
        this.fields.put(field.replace("|","||"),resource.replace("|","||"));
    }
    public void setType(RequestType t) {
        this.type = type;
    }



}
