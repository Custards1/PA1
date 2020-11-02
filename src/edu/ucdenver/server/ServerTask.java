package edu.ucdenver.server;

import edu.ucdenver.domain.order.Order;
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

    //helper function, sends a list of requestable objects
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
        System.out.println("Tryina auth");
        try{
           incoming = new Request(input);
        } catch (ClientError e){
           throw  e;
        }
        System.out.printf("Gotten %s\n",incoming.toRaw());
        if(incoming.getType()== RequestType.AUTHENTICATE_USER ||incoming.getType() == RequestType.CREATE_USER) {
            ArrayList<HashMap<String,String>> objs = incoming.getObjs();
            System.out.println("is ok");
            if(objs.isEmpty()){
                System.out.println("OBJS IS EMPTY ");
              RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
              throw new ClientError((ClientErrorType.INVALID_REQUEST));
            }
            else{
                User user = null;
                try {
                    user = new User(objs.get(0));
                    System.out.println("user is ok");

                }
                catch (IllegalArgumentException e){
                    System.out.println("user is bad");
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_REQUEST);
                    throw new ClientError((ClientErrorType.INVALID_REQUEST));
                }
                if(!user.validEmail()){
                    System.out.println("user is bad email");
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_EMAIL);
                    throw new ClientError((ClientErrorType.INVALID_EMAIL));
                }
                if(!user.validPassword()){
                    System.out.println("user is bad password");
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
                    System.out.println("user is maybe ok");
                    user.setAdmin(false);
                    if (!this.userStore.addUser(user)){
                        System.out.println("user is No bueno");
                        throw new ClientError(ClientErrorType.INVALID_USER);
                    }
                    System.out.println("added");
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
    //Handles request to remove a product from the catalog
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
    //Handles request to create another user
    private RequestType createAnotherUser(Request incoming, BufferedReader input, PrintWriter output) {
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
        if (!this.userStore.addUser(user)){
            try {
                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_USER);
                return RequestType.OK;
            }
            catch (Exception ee){
                return RequestType.ERROR;
            }

        }

        try {
            RequestServerProtocol.sendOneHotRequest(output,RequestType.OK,"admin","false");
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

    //Handles request to create an Admin user, connected user must be admin or client will recive an error
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
    //Handles request to search products
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
    //Handles request to add a catagory to a prdocut
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
    //Terminates the server, must be admin or this function does nothing but send an error request
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
    //Handles request to return the default catagory
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
    //Handles request to get products from a specified catagory
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
    //Handles request to create a catagory, client must be admin
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
    //Handles request to remove a catagory, client must be admin
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
    //Handles request to set a default catagory, client must be admin
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
    //Handles request to get all catagories
    private RequestType getAllCatagories(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allCatagories());
            return RequestType.OK;
        }
        catch (ClientError e){
            return RequestType.ERROR;
        }
    }
    //Handles request to get all prodcuts
    private RequestType getAllProducts(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allProducts());
            return RequestType.OK;
        }
        catch (ClientError e){
            return RequestType.ERROR;
        }
    }
    //Handles request to addProductToTheCatalog, client must be admin
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
    //Handles request to removeCatagoryFromProduct, client must be admin
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
    //Handles request to getAllUsers, client must be admin
    private RequestType getAllUsers(PrintWriter output) {
        try {
            sendList(output,RequestType.OK,userStore.allUsers(connectedUser));
            return RequestType.OK;
        }
        catch (Exception e){
            return RequestType.ERROR;
        }
    }
    //Handles request to get the connceted users current order
    private RequestType getCurrentOrder(Request incoming, BufferedReader input, PrintWriter output) {
        try{
            System.out.println("Sent");
            Requestable r  = userStore.getCurrentOrder(connectedUser);
            System.out.println("Hoobla");
            RequestServerProtocol.sendRequestable(output,RequestType.OK,r);
            System.out.println("Sending");
            return RequestType.OK;
        }
        catch (Exception e){
            return RequestType.ERROR;
        }
    }
    //Handles request to remove a product from the order
    private RequestType removeProductFromOrder(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("product");
            if(temp == null||temp.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            userStore.getCurrentOrder(connectedUser).getProducts().remove(temp);
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,userStore.getCurrentOrder(connectedUser));
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
    //Handles request to add a product to the order
    private RequestType addProductToOrder(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("product");
            if(temp == null||temp.isEmpty()){
                try {
                    RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                    return RequestType.OK;
                }
                catch (Exception ee){
                    return RequestType.ERROR;
                }
            }
            Product prod = userStore.getProduct(temp);
            userStore.getCurrentOrder(connectedUser).getProducts().add(prod.getProductId());
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,userStore.getCurrentOrder(connectedUser));
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
    //Handles request to get all orders from specified user, must be admin
    private RequestType getUserOrders(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("id");
            if(temp==null||temp.isEmpty()){
                ArrayList<HashMap<String,String>> objs = incoming.getObjs();
                if(objs == null || objs.isEmpty()) {
                    try{
                        temp = incoming.getTable().get("email");
                        if(temp==null||temp.isEmpty()){
                            try {
                                RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_RESOURCE);
                                return RequestType.OK;
                            }
                            catch (Exception ee){
                                return RequestType.ERROR;
                            }
                        }
                        ArrayList<Order> r  = userStore.getUsersOrders(connectedUser,temp);
                        sendList(output,RequestType.OK,r);
                        return RequestType.OK;
                    }
                    catch (Exception e){
                        return RequestType.ERROR;
                    }
                }
                User user = new User();
                user.fromRequestable(objs.get(0));
                try{
                    ArrayList<Order> r  = userStore.getUsersOrders(connectedUser,user);
                    sendList(output,RequestType.OK,r);
                    return RequestType.OK;
                }
                catch (Exception e){
                    return RequestType.ERROR;
                }
            }
            try {
                Order order = userStore.getOrder(temp);
                RequestServerProtocol.sendRequestable(output,RequestType.OK,order);
                return RequestType.OK;
            }
            catch (Exception ee){
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
    //Handles request finilize products in cart
    private RequestType finalizeOrder(Request incoming, BufferedReader input, PrintWriter output) {
        try{
            Requestable r  = userStore.finalizeOrder(connectedUser);
            RequestServerProtocol.sendRequestable(output,RequestType.OK,r);
            return RequestType.OK;
        }
        catch (Exception e){
            return RequestType.ERROR;
        }
    }
    //Handles request to get users finalized orders
    private RequestType getFinalizedOrders(Request incoming, BufferedReader input, PrintWriter output) {
        try{
            ArrayList<Order> r  = userStore.getFinalizedOrders(connectedUser);
            sendList(output,RequestType.OK,r);
            return RequestType.OK;
        }
        catch (Exception e){
            return RequestType.ERROR;
        }
    }
    //Handles request to get a product by name
    private RequestType getProductByName(Request incoming, BufferedReader input, PrintWriter output) {
        String temp = new String();
        try {
            temp = incoming.getTable().get("product");
            if(temp == null||temp.isEmpty()){
                temp = incoming.getTable().get("product-id");
                if(temp == null||temp.isEmpty()){
                    try {
                        RequestServerProtocol.sendErrorRequest(output,ClientErrorType.INVALID_ACCESS);
                        return RequestType.OK;
                    }
                    catch (Exception ee){
                        return RequestType.ERROR;
                    }
                }
                Product prod = userStore.getProduct(temp);
                try {
                    RequestServerProtocol.sendRequestable(output,RequestType.OK,prod);
                    return RequestType.OK;
                }
                catch (ClientError e){
                    return RequestType.ERROR;
                }
            }
            Product prod = userStore.getProductByName(temp);
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,prod);
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
    //Handles request to clear the current order
    private RequestType clearOrder(Request incoming, BufferedReader input, PrintWriter output) {
        try {
            userStore.getCurrentOrder(connectedUser).getProducts().clear();
            try {
                RequestServerProtocol.sendRequestable(output,RequestType.OK,userStore.getCurrentOrder(connectedUser));
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
    //Replys to clients request
    public RequestType reply(Request incoming,BufferedReader input,PrintWriter output) {
        Request toSend = null;
        String temp = new String();
        switch (incoming.getType()){
            case OK: case AUTHENTICATE_USER: case ERROR:case PICTURE:case NOOP:
                break;
            case CREATE_USER:
                return createAnotherUser(incoming,input,output);
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
                return getCurrentOrder(incoming,input,output);
            case GET_ALL_USERS:
                return getAllUsers(output);
            case FINALIZE_ORDER:
                return finalizeOrder(incoming,input,output);
            case GET_USER_ORDERS:
                return getUserOrders(incoming,input,output);
            case ADD_PRODUCT_TO_ORDER:
                return addProductToOrder(incoming,input,output);
            case REMOVE_PRODUCT_FROM_ORDER:
                return removeProductFromOrder(incoming,input,output);
            case GET_FINALIZED_ORDERS:
                return getFinalizedOrders(incoming,input,output);
            case GET_PRODUCT_BY_NAME:
                return getProductByName(incoming,input,output);
            case CLEAR_ORDER:
                return clearOrder(incoming,input,output);
            default:break;
        }
        return RequestType.ERROR;
    }


    //starts the servertask
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

            RequestServerProtocol.close(socket,input,output);
           
        }
        finally {
            RequestServerProtocol.close(socket,input,output);
         //
        }
    }

}
