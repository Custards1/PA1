package edu.ucdenver.server;

import edu.ucdenver.domain.Request;
import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.User;
import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.client.ClientErrorType;
import edu.ucdenver.domain.store.ItemStore;
import edu.ucdenver.domain.store.UserStore;
import jdk.nashorn.internal.ir.CatchNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerTask implements Runnable {
    private Socket socket;
    private ServerSocket serverRef;
    private int         user;
    private ItemStore   userStore;
    private User        connectedUser;
    public ServerTask(Socket socket, ServerSocket serverRef, ItemStore userStore) {
        this.serverRef = serverRef;
        this.socket = socket;
        this.userStore = userStore;
    }

    public void close(Socket socket, BufferedReader input, PrintWriter output){
        System.out.println("In Close");
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

        System.out.println("Outa Close");
    }
    public void terminate(Socket socket,BufferedReader input, PrintWriter output){
        System.out.println("Callion TERMINATE");

        try{
            System.out.println("Closin ref");
            serverRef.close();
            serverRef = null;
            System.out.println("Closin ref good");
        }
        catch (Exception e){
            System.out.println("Closin ref badf");
        }
        close(socket,input,output);
        System.out.println("Callion GOOD");
    }
    private static void sendClientErrorRequest(PrintWriter output,ClientErrorType errorType) throws ClientError {
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
    private static void sendClientMinimalRequest(PrintWriter output,RequestType rtype,String key,String param) throws ClientError {
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
    private User authorize(BufferedReader input, PrintWriter output) throws ClientError {
        Request incoming = null;
        try{
           incoming = new Request(input);
        } catch (ClientError e){
           throw  e;
        }
        if(incoming.getType()== RequestType.AUTHENTICATE_USER ||incoming.getType() == RequestType.CREATE_USER) {
            ArrayList<HashMap<String,String>> objs = incoming.getObjs();
            System.out.println("_In Auth");
            if(objs.isEmpty()){
                System.out.println("_In Auth_is_emtyp");
              sendClientErrorRequest(output,ClientErrorType.INVALID_REQUEST);
              throw new ClientError((ClientErrorType.INVALID_REQUEST));
            }
            else{
                User user = null;
                try {
                    user = new User(objs.get(0));

                }
                catch (IllegalArgumentException e){
                    System.out.println("_In Auth_bad_User");
                    sendClientErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                    throw new ClientError((ClientErrorType.INVALID_REQUEST));
                }
                if(!user.validEmail()){
                    System.out.println("_In Auth_bad_Email");
                    sendClientErrorRequest(output,ClientErrorType.INVALID_EMAIL);
                    throw new ClientError((ClientErrorType.INVALID_EMAIL));
                }
                if(!user.validPassword()){
                    System.out.println("_In Auth_bad_password");
                    sendClientErrorRequest(output,ClientErrorType.INVALID_PASSWORD);
                    throw new ClientError((ClientErrorType.INVALID_PASSWORD));
                }
                if(incoming.getType()== RequestType.AUTHENTICATE_USER) {
                    try {
                        user = this.userStore.getUser(user.getEmail(),user.getPassword());
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("_In Auth_user_not_found");
                        sendClientErrorRequest(output,ClientErrorType.USER_NOT_FOUND);
                        throw new ClientError(ClientErrorType.USER_NOT_FOUND);
                    }
                    finally {
                        sendClientMinimalRequest(output,RequestType.OK,"admin",user.isAdmin()?"true":"false");
                    }
                    return user;
                }
                else {
                    user.setAdmin(false);
                    if (!this.userStore.addUser(user)){
                        System.out.println("_In Auth_user_casnt asdd");
                        throw new ClientError(ClientErrorType.INVALID_USER);
                    }
                    sendClientMinimalRequest(output,RequestType.OK,"admin","false");
                    return user;
                }
            }
        }
        else{
            System.out.printf("_In Auth_user_casnt adasdsadd hibblibty %s\n",incoming.getType().toString());
            sendClientErrorRequest(output,ClientErrorType.INVALID_REQUEST);
            throw new ClientError((ClientErrorType.INVALID_REQUEST));
        }
    }
    public RequestType reply(Request incoming,BufferedReader input,PrintWriter output) {
        Request toSend = null;
        switch (incoming.getType()){
            case OK: case AUTHENTICATE_USER: case ERROR:case CREATE_USER:case PICTURE:case NOOP:
                break;
            case TERMINATE:
                if(connectedUser.isAdmin()){
                    terminate(socket,input,output);
                    return RequestType.TERMINATE;
                }
                else{
                    try {
                        sendClientErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception e){
                        return RequestType.ERROR;
                    }

                }

            default:break;
        }
        return RequestType.ERROR;
    }
    @Override
    public void run(){
        BufferedReader input = null;
        PrintWriter output = null;
        connectedUser = null;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());
            try {
                connectedUser = authorize(input,output);
                System.out.printf("\n\n_________Gotten mesa user %s\n\n",connectedUser.getEmail());
                RequestType go = RequestType.OK;
                while (go == RequestType.OK) {
                    System.out.println("reciveing");
                    Request incoming = new Request(input);
                    System.out.println("recived!");
                    try{
                        System.out.println("sending...");

                        go = reply(incoming,input,output);

                        System.out.println("sent!");
                    }
                    catch (Exception e){
                        close(socket,input,output);
                        return;
                    }
                }
                System.out.println("OUTA DA LOOP");
                if(go == RequestType.TERMINATE){
                    terminate(socket,input,output);
                }
                else{
                    close(socket,input,output);
                }
                System.out.println("DONE CLOSINA");
                return;
            }
            catch (ClientError e){
                close(socket,input,output);
                System.out.println("DONE CLOSINA ER");
                return;
            }

        }
        catch (IOException e){
            close(socket,input,output);
            System.out.println("DONE CLOSINA ER");
        }
        finally {
            close(socket,input,output);
            System.out.println("DONE CLOSINA ERLAST");
        }
    }

}
