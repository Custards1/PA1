package edu.ucdenver.server;

import edu.ucdenver.domain.Request;
import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.Requestable;
import edu.ucdenver.domain.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.client.ClientErrorType;
import edu.ucdenver.domain.products.*;
import edu.ucdenver.domain.store.ItemStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public void terminate(Socket socket,BufferedReader input, PrintWriter output){
   

        try{
         
            serverRef.close();
            serverRef = null;
      
        }
        catch (Exception e){
        
        }
        close(socket,input,output);
      
    }
    private static void sendErrorRequest(PrintWriter output, ClientErrorType errorType) throws ClientError {
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
    private static void sendBlankRequest(PrintWriter output, RequestType rtype) throws ClientError {
        Request to_send = new Request(rtype,null,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    private static void sendOneHotRequest(PrintWriter output, RequestType rtype, String key, String param) throws ClientError {
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
    private static void sendRequestable(PrintWriter output, RequestType rtype, Requestable requestable)  throws ClientError{
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

    private <T extends Requestable> void sendRequestableList(PrintWriter output, RequestType rtype, ArrayList<T> requestable)  throws ClientError{
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        for(T req : requestable){
            objs.add(req.asRequestable());
        }
        Request to_send = new Request(rtype,null,objs);
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
        
            if(objs.isEmpty()){
        
              sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
              throw new ClientError((ClientErrorType.INVALID_REQUEST));
            }
            else{
                User user = null;
                try {
                    user = new User(objs.get(0));

                }
                catch (IllegalArgumentException e){
               
                    sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                    throw new ClientError((ClientErrorType.INVALID_REQUEST));
                }
                if(!user.validEmail()){
      
                    sendErrorRequest(output,ClientErrorType.INVALID_EMAIL);
                    throw new ClientError((ClientErrorType.INVALID_EMAIL));
                }
                if(!user.validPassword()){
           
                    sendErrorRequest(output,ClientErrorType.INVALID_PASSWORD);
                    throw new ClientError((ClientErrorType.INVALID_PASSWORD));
                }
                if(incoming.getType()== RequestType.AUTHENTICATE_USER) {
                    try {
                        user = this.userStore.getUser(user.getEmail(),user.getPassword());
                    }
                    catch (IllegalArgumentException e) {
                    
                        sendErrorRequest(output,ClientErrorType.USER_NOT_FOUND);
                        throw new ClientError(ClientErrorType.USER_NOT_FOUND);
                    }
                    finally {
                        sendOneHotRequest(output,RequestType.OK,"admin",user.isAdmin()?"true":"false");
                    }
                    return user;
                }
                else {
                    user.setAdmin(false);
                    if (!this.userStore.addUser(user)){
                       
                        throw new ClientError(ClientErrorType.INVALID_USER);
                    }
                    sendOneHotRequest(output,RequestType.OK,"admin","false");
                    return user;
                }
            }
        }
        else{
          
            sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
            throw new ClientError((ClientErrorType.INVALID_REQUEST));
        }
    }
    public RequestType reply(Request incoming,BufferedReader input,PrintWriter output) {
        Request toSend = null;
        String temp = new String();
        switch (incoming.getType()){
            case OK: case AUTHENTICATE_USER: case ERROR:case CREATE_USER:case PICTURE:case NOOP:
                break;
            case REMOVE_PRODUCT_FROM_CATALOG:

                try {

                    temp = incoming.getTable().get("product-to-remove");

                    if(temp == null||temp.isEmpty()){
                        try {

                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }

                    userStore.removeProduct(connectedUser,temp);
                    try {

                        sendBlankRequest(output,RequestType.OK);

                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case SEARCH:
                try {
                    temp = incoming.getField("search");
                    try {
                        sendRequestableList(output,RequestType.OK,userStore.searchProducts(temp));
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case ADD_CATAGORY_TO_PRODUCT:
                try {
                    temp = incoming.getTable().get("catagory");
                    if(temp == null||temp.isEmpty()){
                        try {
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    String prod = incoming.getTable().get("product");
                    if(prod == null||prod.isEmpty()){
                        try {
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    Product product = userStore.addCatagoryToProduct(connectedUser,temp,prod);
                    try {
                        sendRequestable(output,RequestType.OK,product);
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case REMOVE_CATAGORY_FROM_PRODUCT:
                try {
                    temp = incoming.getTable().get("catagory");
                    if(temp == null||temp.isEmpty()){
                        try {
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    String prod = incoming.getTable().get("product");
                    if(prod == null||prod.isEmpty()){
                        try {
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    Product product = userStore.removeCatagoryFromProduct(connectedUser,temp,prod);
                    try {
                        sendRequestable(output,RequestType.OK,product);
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case ADD_PRODUCT_TO_CATALOG:
               
                ArrayList<HashMap<String,String>> objs = incoming.getObjs();
                if(objs == null || objs.isEmpty()) {
                   
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
               
                HashMap<String,String> requested = objs.get(0);
                String type = requested.get("product-type");
                Product product = null;
               
                if(type.equals("Home")){
                    product = new Home();

                }
                else if(type.equals("Book")){
                    product = new Book();

                }
                else if(type.equals("Electronic")){
                    product = new Electronic();
                }
                else if(type.equals("Phone")){
                    product = new Phone();
                }
                else if(type.equals("Computer")) {
                    product = new Computer();
                }
                else{
                   
                    System.out.println(type);
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
                
                for( Map.Entry<String,String> entry : requested.entrySet()){
                  
                }
                product.fromRequestable(requested);
                userStore.addProduct(connectedUser,product);
                try{
                    
                    sendBlankRequest(output,RequestType.OK);
                    
                    return RequestType.OK;
                }
               catch (Exception e){
                  
                    try{
                        sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }

               }
            case GET_ALL_PRODUCTS:
                    try {
                        sendRequestableList(output,RequestType.OK,userStore.allProducts());
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }
            case GET_ALL_CATAGORIES:
                try {
                    sendRequestableList(output,RequestType.OK,userStore.allCatagories());
                    return RequestType.OK;
                }
                catch (ClientError e){
                    return RequestType.ERROR;
                }

            case SET_DEFAULT_CATAGORY:
                try {
                    temp = incoming.getField("catagory");
                    if(temp==null){
                        try{
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    userStore.setDefaultCatagory(connectedUser,temp);
                    try {
                        sendBlankRequest(output,RequestType.OK);
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case REMOVE_CATAGORY:
                try {
                    temp = incoming.getTable().get("catagory-to-remove");
                    if(temp == null||temp.isEmpty()){
                        try {
                            sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                            return RequestType.OK;
                        }
                        catch (Exception ee){
                            return RequestType.ERROR;
                        }
                    }
                    userStore.removeCatagory(connectedUser,temp);
                    try {

                        sendBlankRequest(output,RequestType.OK);

                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        return RequestType.ERROR;
                    }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case CREATE_CATAGORY:
                try {
                    temp = incoming.getField("catagory");
                   userStore.addCatagory(connectedUser,temp);
                   try {
                       sendBlankRequest(output,RequestType.OK);
                       return RequestType.OK;
                   }
                   catch (ClientError e){
                       return RequestType.ERROR;
                   }

                }
                catch (IllegalArgumentException e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
            case GET_PRODUCTS_FROM_CATAGORY:
                String catagory = null;
                try{
                   catagory = incoming.getField("catagory");
                }
                catch (Exception ignored){
                }

                if(catagory == null){
                    catagory = incoming.getField("default");
                    if(catagory==null||!catagory.equals("true"))
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                    catagory = userStore.getDefaultCatagory().getName();
                }
                ArrayList<Product> products = new ArrayList<>();
                try {
                    products = userStore.getProductsFromCatagory(catagory);
                }
                catch (Exception e){
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
                finally {
                    try {
                        sendRequestableList(output,RequestType.OK,products);
                        return RequestType.OK;
                    }
                    catch (Exception e){
                        return RequestType.ERROR;
                    }

                }
            case GET_DEFAULT_CATAGORY:
              
                Catagory d = null;
                try{
                    d = userStore.getDefaultCatagory();
                }
                catch (Exception e){
                  
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
                finally {

                    try {
                        sendRequestable(output,RequestType.OK,d);
                        
                        return RequestType.OK;
                    }
                    catch (ClientError e){
                        
                        return RequestType.ERROR;
                    }
                }
            case TERMINATE:
                if(connectedUser.isAdmin()){
                    terminate(socket,input,output);
                    return RequestType.TERMINATE;
                }
                else{
                    try {
                        sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
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
                System.out.printf("Connected user: %s\n",connectedUser.getName());
                RequestType go = RequestType.OK;
                while (go == RequestType.OK) {
                    
                    Request incoming = new Request(input);
                   
                    try{

                        System.out.printf("Waiting for user: %s\n",connectedUser.getName());
                        go = reply(incoming,input,output);
                        System.out.printf("Replying to user: %s\n",connectedUser.getName());
                       
                    }
                    catch (Exception e){
                        System.out.printf("User %s disconnected\n",connectedUser.getName());
                        close(socket,input,output);
                        return;
                    }
                }
              
                if(go == RequestType.TERMINATE){

                    terminate(socket,input,output);
                }
                else{
                    close(socket,input,output);
                }
                System.out.printf("User %s disconnected\n",connectedUser.getEmail());
                return;
            }
            catch (ClientError e){
                close(socket,input,output);

                return;
            }

        }
        catch (IOException e){
            close(socket,input,output);
           
        }
        finally {
            close(socket,input,output);
         
        }
    }

}
