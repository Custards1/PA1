package edu.ucdenver.domain.request;

import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.client.ClientErrorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//up to implementer to close conncetion
public interface RequestServerProtocol {
    static void close(Socket socket, BufferedReader input, PrintWriter output){

        output.close();
        try{
            input.close();
        }
        catch (IOException e){

        }
        try{
            socket.close();
        }
        catch (IOException e){

        }

    }
    static void terminate(ServerSocket serverRef, Socket socket, BufferedReader input, PrintWriter output){


        try{

            serverRef.close();
            serverRef = null;

        }
        catch (Exception e){

        }
        close(socket,input,output);

    }
    static void sendErrorRequest(PrintWriter output, ClientErrorType errorType) throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("error-type",errorType.toString());
        Request to_send = new Request(RequestType.ERROR,fields,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendBlankRequest(PrintWriter output, RequestType rtype) throws ClientError {
        Request to_send = new Request(rtype,null,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendOneHotRequest(PrintWriter output, RequestType rtype, String key, String param) throws ClientError {
        HashMap<String,String> minimal = new HashMap<>();
        minimal.put(key,param);
        Request to_send = new Request(rtype,minimal,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendRequestable(PrintWriter output, RequestType rtype, Requestable requestable)  throws ClientError{
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        objs.add(requestable.asRequestable());
        Request to_send = new Request(rtype,null,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendRequestableList(RequestType type,
                                    HashMap<String,String> fields,
                                    ArrayList<HashMap<String,String>> requestable,
                                    PrintWriter output
    ) throws ClientError {
        Request to_send = new Request(type,fields,requestable);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
}
