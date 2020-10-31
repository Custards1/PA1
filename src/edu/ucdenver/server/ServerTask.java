package edu.ucdenver.server;

import edu.ucdenver.domain.request.*;
import edu.ucdenver.domain.user.User;
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

public class ServerTask implements Runnable, RequestServerProtocol {
    private Socket socket;
    private ServerSocket serverRef;
    private int         user;
    private ItemStore   userStore;
    private User        connectedUser;

    //helper function
    private <T extends Requestable> void sendList(PrintWriter output,RequestType type,ArrayList<T> list) throws ClientError{
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        for(T entry : list){
            objs.add(entry.asRequestable());
        }
        RequestServerProtocol.sendRequestableList(type,null,objs,output);
    }
    //constructor
    public ServerTask(Socket socket, ServerSocket serverRef, ItemStore userStore) {
        this.serverRef = serverRef;
        this.socket = socket;
        this.userStore = userStore;
    }

    //Used to authenicate incoming user;
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
        
              RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
              throw new ClientError((ClientErrorType.INVALID_REQUEST));
            }
            else{
                User user = null;
                try {
                    user = new User(objs.get(0));

                }
                catch (IllegalArgumentException e){
               
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                    throw new ClientError((ClientErrorType.INVALID_REQUEST));
                }
                if(!user.validEmail()){
      
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_EMAIL);
                    throw new ClientError((ClientErrorType.INVALID_EMAIL));
                }
                if(!user.validPassword()){
           
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_PASSWORD);
                    throw new ClientError((ClientErrorType.INVALID_PASSWORD));
                }
                if(incoming.getType()== RequestType.AUTHENTICATE_USER) {
                    try {
                        user = this.userStore.getUser(user.getEmail(),user.getPassword());
                    }
                    catch (IllegalArgumentException e) {
                    
                        RequestServerProtocol.sendErrorRequest(output,ClientErrorType.USER_NOT_FOUND);
                        throw new ClientError(ClientErrorType.USER_NOT_FOUND);
                    }
                    finally {
                        RequestServerProtocol.sendOneHotRequest(output,RequestType.OK,"admin",user.isAdmin()?"true":"false");
                    }
                    return user;
                }
                else {
                    user.setAdmin(false);
                    if (!this.userStore.addUser(user)){
                       
                        throw new ClientError(ClientErrorType.INVALID_USER);
                    }
                    RequestServerProtocol.sendOneHotRequest(output,RequestType.OK,"admin","false");
                    return user;
                }
            }
        }
        else{
          
            RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
            throw new ClientError((ClientErrorType.INVALID_REQUEST));
        }
    }
    //The rest are tasks the server supports
    private RequestType removeProductFromCatalog(Request incoming,BufferedReader input,PrintWriter output){
        String temp = new String();
        try {

            temp = incoming.getTable().get("product-to-remove");

            if(temp == null||temp.isEmpty()){
                try {

                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }

            userStore.removeProduct(connectedUser,temp);
            try {

                RequestServerProtocol.sendBlankRequest(output,RequestType.OK);

                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }
    private RequestType addAdminUser(Request incoming,BufferedReader input,PrintWriter output){
        if(incoming.getObjs().isEmpty()){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }

        User user = null;
        try {
            user = new User(incoming.getObjs().get(0));

        }
        catch (IllegalArgumentException e){

            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
        if(!user.validEmail()){

            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_EMAIL);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
        if(!user.validPassword()){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_PASSWORD);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
        if (!this.userStore.addAdminUser(connectedUser,user)){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_USER);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }

        }

        try {
            RequestServerProtocol.sendOneHotRequest(output,RequestType.OK,"admin","true");
            return RequestType.OK;
        }
        catch (Exception ee){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_USER);
                return RequestType.OK;
            }
            catch (Exception eer){
                return RequestType.ERROR;
            }
        }


    }
    public RequestType searchProducts(Request incoming,BufferedReader input,PrintWriter output){
        String temp = new String();
        try {
            temp = incoming.getField("search");
            try {
                sendList(output,RequestType.OK,userStore.searchProducts(temp));
                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }
    private RequestType addCatagoryToProduct(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("catagory");
            if(temp == null||temp.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            String prod = incoming.getTable().get("product");
            if(prod == null||prod.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            Product product = userStore.addCatagoryToProduct(connectedUser,temp,prod);
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,product);
                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }
    private RequestType serverTerminate(Request incoming, BufferedReader input, PrintWriter output) {
        if(connectedUser.isAdmin()){
            RequestServerProtocol.terminate(serverRef,socket,input,output);
            return RequestType.TERMINATE;
        }
        else{
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                return RequestType.OK;
            }
            catch (Exception e){
                return RequestType.ERROR;
            }

        }

    }

    private RequestType getDefaultCatagory(Request incoming, BufferedReader input, PrintWriter output) {

        Catagory d = null;
        try{
            d = userStore.getDefaultCatagory();
        }
        catch (Exception e){

            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
        finally {

            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,d);

                return RequestType.OK;
            }
            catch (ClientError e){

                return RequestType.ERROR;
            }
        }
    }

    private RequestType getProductsFromCatagory(Request incoming, BufferedReader input, PrintWriter output) {
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
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
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
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
        finally {
            try {
                sendList(output,RequestType.OK,products);
                return RequestType.OK;
            }
            catch (Exception e){
                return RequestType.ERROR;
            }

        }
    }

    private RequestType createCatagory(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getField("catagory");
            userStore.addCatagory(connectedUser,temp);
            try {
                RequestServerProtocol.sendBlankRequest(output,RequestType.OK);
                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }

    private RequestType removeCatagory(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("catagory-to-remove");
            if(temp == null||temp.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            userStore.removeCatagory(connectedUser,temp);
            try {

                RequestServerProtocol.sendBlankRequest(output,RequestType.OK);

                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }

    private RequestType setDefaultCatagories(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getField("catagory");
            if(temp==null){
                try{
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            userStore.setDefaultCatagory(connectedUser,temp);
            try {
                RequestServerProtocol.sendBlankRequest(output,RequestType.OK);
                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }

    private RequestType getAllCatagories(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allCatagories());
            return RequestType.OK;
        }
        catch (ClientError e){
            return RequestType.ERROR;
        }
    }

    private RequestType getAllProducts(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allProducts());
            return RequestType.OK;
        }
        catch (ClientError e){
            return RequestType.ERROR;
        }
    }

    private RequestType addProductToCatalog(Request incoming, BufferedReader input, PrintWriter output) {
        ArrayList<HashMap<String,String>> objs = incoming.getObjs();
        if(objs == null || objs.isEmpty()) {

            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
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
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
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

            RequestServerProtocol.sendBlankRequest(output,RequestType.OK);

            return RequestType.OK;
        }
        catch (Exception e){

            try{
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }

        }
    }

    private RequestType removeCatagoryFromProduct(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("catagory");
            if(temp == null||temp.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            String prod = incoming.getTable().get("product");
            if(prod == null||prod.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            Product product = userStore.removeCatagoryFromProduct(connectedUser,temp,prod);
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,product);
                return RequestType.OK;
            }
            catch (ClientError e){
                return RequestType.ERROR;
            }

        }
        catch (IllegalArgumentException e){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }
        }
    }
    private RequestType getAllUsers(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allUsers(connectedUser));
            return RequestType.OK;
        }
        catch (Exception e){
            return RequestType.ERROR;
        }
    }

    public RequestType reply(Request incoming,BufferedReader input,PrintWriter output) {
        Request toSend = null;
        String temp = new String();
        switch (incoming.getType()){
            case OK: case AUTHENTICATE_USER: case ERROR:case CREATE_USER:case PICTURE:case NOOP:
                break;
            case REMOVE_PRODUCT_FROM_CATALOG:
                return removeProductFromCatalog(incoming,input,output);
            case ADD_ADMIN_USER:
                return addAdminUser(incoming,input,output);
            case SEARCH:
               return searchProducts(incoming,input,output);
            case ADD_CATAGORY_TO_PRODUCT:
                return addCatagoryToProduct(incoming,input,output);
            case REMOVE_CATAGORY_FROM_PRODUCT:
                return removeCatagoryFromProduct(incoming,input,output);
            case ADD_PRODUCT_TO_CATALOG:
                return addProductToCatalog(incoming,input,output);
            case GET_ALL_PRODUCTS:
                  return getAllProducts(output);
            case GET_ALL_CATAGORIES:
                return getAllCatagories(output);
            case SET_DEFAULT_CATAGORY:
               return setDefaultCatagories(incoming,input,output);
            case REMOVE_CATAGORY:
               return removeCatagory(incoming,input,output);
            case CREATE_CATAGORY:
              return createCatagory(incoming,input,output);
            case GET_PRODUCTS_FROM_CATAGORY:
               return getProductsFromCatagory(incoming,input,output);
            case GET_DEFAULT_CATAGORY:
              return getDefaultCatagory(incoming,input,output);
            case TERMINATE:
                return serverTerminate(incoming,input,output);
            case CURRENT_ORDER:

            case GET_ALL_USERS:
                return getAllUsers(output);
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
                        RequestServerProtocol.close(socket,input,output);
                        return;
                    }
                }
              
                if(go == RequestType.TERMINATE){

                    RequestServerProtocol.terminate(serverRef,socket,input,output);
                }
                else{
                    RequestServerProtocol.close(socket,input,output);
                }
                System.out.printf("User %s disconnected\n",connectedUser.getEmail());

            }
            catch (ClientError e){
                RequestServerProtocol.close(socket,input,output);


            }

        }
        catch (IOException e){

           // RequestServerProtocol.close(socket,input,output);
           
        }
        finally {
          //  RequestServerProtocol.close(socket,input,output);
         //
        }
    }

}
