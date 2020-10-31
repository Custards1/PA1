package edu.ucdenver.domain.request;

import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.client.ClientErrorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public interface RequestClientProtocol {
    void shutdown();
    static void sendBlankRequest(RequestClientProtocol self,RequestType type, PrintWriter output) throws ClientError {
        Request to_send = new Request(type,null,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            self.shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendMinimalRequest(RequestClientProtocol self,
                                   RequestType type,
                                   HashMap<String,String> fields,
                                   PrintWriter output
    ) throws ClientError {

        Request to_send = new Request(type,fields,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            self.shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendMinimalRequestable(RequestClientProtocol self,
                                       RequestType type,
                                       Requestable requestable,
                                       PrintWriter output) throws ClientError {
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        objs.add(requestable.asRequestable());
        Request to_send = new Request(type,null,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            self.shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendModerateRequestable(RequestClientProtocol self,
                                        RequestType type,
                                        HashMap<String,String> fields ,
                                        Requestable requestable,
                                        PrintWriter output
    ) throws ClientError {
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        objs.add(requestable.asRequestable());
        Request to_send = new Request(type,fields,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            self.shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    static void sendRequestableList(RequestClientProtocol self,
                                    RequestType type,
                                    HashMap<String,String> fields,
                                    ArrayList<HashMap<String,String>> requestable,
                                    PrintWriter output
        ) throws ClientError {
        Request to_send = new Request(type,fields,requestable);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            self.shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    default Request okOrDie(RequestClientProtocol self,BufferedReader input)throws ClientError{

        if(input == null){
            self.shutdown();
            throw new ClientError();
        }
        Request recived = new Request(input);

        if(recived.getType() == RequestType.ERROR){
            ClientErrorType error = ClientErrorType.UNKNOWN;
            try {
                error = ClientErrorType.valueOf(recived.getField("error-type"));
            }
            catch (IllegalArgumentException e){

            }
            self.shutdown();
            throw new ClientError(error);
        }
        else if(recived.getType() != RequestType.OK) {
            self.shutdown();
            throw new ClientError(ClientErrorType.UNKNOWN);
        }
        return recived;

    }

}
