
package edu.ucdenver.domain.client;

import edu.ucdenver.domain.Request;
import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.Requestable;
import edu.ucdenver.domain.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader input = null;
    private PrintWriter output = null;
    private boolean isAdmin;
    private User user;
    public void shutdown(){
        if(this.socket!=null){
            try {

                this.socket.close();
            }
            catch (Exception e){

            }
            this.socket = null;
        }
        if(this.input!=null){
            try {

                this.input.close();
            }
            catch (Exception e){

            }
            this.input = null;
        }
        if(this.output!=null){
            try {

                this.output.close();
            }
            catch (Exception e){

            }
            this.output = null;
        }

    }

    public Client(String host,int port,User self,boolean signup)throws ClientError{
        this.host = host;
        this.port = port;
        this.isAdmin = false;
        ;
        Request recived = null;
        ArrayList<HashMap<String,String>> re = new ArrayList();
        re.add(self.asRequestable());
        Request to_send = new Request(signup?RequestType.CREATE_USER:RequestType.AUTHENTICATE_USER,null,re);
        try {
            try{
                this.socket = new Socket(host,port);
                input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                to_send.send(output);
            }
            catch (IOException e){
                throw new ClientError(ClientErrorType.INVALID_SOCKET);
            }


            try {
                recived = okOrDie();
                try {
                    String is_admin_raw = recived.getField("admin");
                    this.isAdmin = is_admin_raw.equals("true");
                }
                catch (IllegalArgumentException e){

                }


            }
            catch (ClientError e) {
                    throw e;
            }

        }
        catch (ClientError ioe) {
            this.shutdown();
            throw ioe;
        }
    }
    public void sendBlankRequest(RequestType type) throws ClientError {
        Request to_send = new Request(type,null,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    public void sendMinimalRequest(RequestType type,HashMap<String,String> fields) throws ClientError {

        Request to_send = new Request(type,fields,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    public void sendMinimalRequestable(RequestType type, Requestable requestable) throws ClientError {
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        objs.add(requestable.asRequestable());
        Request to_send = new Request(type,null,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    public void sendModerateRequestable(RequestType type,HashMap<String,String> fields ,Requestable requestable) throws ClientError {
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        objs.add(requestable.asRequestable());
        Request to_send = new Request(type,fields,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }
    public < T extends Requestable> void  sendRequestable(RequestType type, HashMap<String,String> fields, ArrayList<T> requestable) throws ClientError {
        ArrayList<HashMap<String,String>> objs = new ArrayList<>();
        for(T req : requestable){
            objs.add(req.asRequestable());
        }
        Request to_send = new Request(type,fields,objs);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public Catagory getDefaultCatagory() throws ClientError {
        sendBlankRequest(RequestType.GET_DEFAULT_CATAGORY);
        Request r = okOrDie();
        
        Catagory res = new Catagory();
        try {
            res.fromRequestable(r.getObjs().get(0));
            
            return res;
        }
        catch (IllegalArgumentException e) {
            
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
    }
    public void addProductToCatalog(Product p) throws ClientError {
        if(!this.isAdmin|| p == null){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
       
        sendMinimalRequestable(RequestType.ADD_PRODUCT_TO_CATALOG,p);
       
        Request r = okOrDie();
       
    }
    public ArrayList<Product> search(String text) throws ClientError {
        if(text == null){
            return allProducts();
        }
        HashMap<String,String> fields = new HashMap<>();
        fields.put("search",text);
        sendMinimalRequest(RequestType.SEARCH,fields);
        Request r = okOrDie();
        ArrayList<Product> products = new ArrayList<>();
        for(HashMap<String,String> requestable : r.getObjs()){
            products.add(parseProduct(requestable));
        }
        return products;
    }
    //returns updated product if succesfull
    public Product addCatagoryToProduct(String catagoryName,String productid) throws ClientError{
        if(!this.isAdmin||catagoryName==null||productid==null||catagoryName.isEmpty()||productid.isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> fields = new HashMap<>();
        fields.put("catagory",catagoryName);
        fields.put("product",productid);
        sendMinimalRequest(RequestType.ADD_CATAGORY_TO_PRODUCT,fields);
        Request r = okOrDie();
        ArrayList<HashMap<String,String>> objs = r.getObjs();
        if(objs == null || objs.isEmpty()) {
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Product p = null;
        try {
            p = parseProduct(objs.get(0));
        }
        catch (Exception e){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        finally {
            return p;
        }
    }
    public Product addCatagoryToProductByName(String catagoryName,String productName)throws ClientError{
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        return addCatagoryToProduct(catagoryName,id.toString());
    }
    public void removeProductFromCatalog(String productId) throws ClientError {
        if(!this.isAdmin || productId == null){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
       
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product-to-remove",productId);
        sendMinimalRequest(RequestType.REMOVE_PRODUCT_FROM_CATALOG,fields);
  
        Request r = okOrDie();
      
    }
    public void removeProductFromCatalogByName(String productName) throws ClientError {
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        removeProductFromCatalog(id.toString());
    }
    public Product removeCatagoryFromProduct(String catagoryName, String productid) throws ClientError{
        if(!this.isAdmin||catagoryName==null||productid==null||catagoryName.isEmpty()||productid.isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> fields = new HashMap<>();
        fields.put("catagory",catagoryName);
        fields.put("product",productid);
        sendMinimalRequest(RequestType.REMOVE_CATAGORY_FROM_PRODUCT,fields);
        Request r = okOrDie();
        ArrayList<HashMap<String,String>> objs = r.getObjs();
        if(objs == null || objs.isEmpty()) {
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Product p = null;
        try {
            p = parseProduct(objs.get(0));
        }
        catch (Exception e){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        finally {
            return p;
        }
    }
    public Product removeCatagoryFromProductByName(String catagoryName,String productName) throws ClientError{
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        return removeCatagoryFromProduct(catagoryName,id.toString());
    }
    protected static Product parseProduct(HashMap<String,String> requested) throws IllegalArgumentException{
        String type = requested.get("product-type");
        Product p = null;
        if(type.equals("Home")){
            p = new Home();
        }
        else if(type.equals("Book")){
            p = new Book();

        }
        else if(type.equals("Electronic")){
            p = new Electronic();
        }
        else if(type.equals("Phone")){
            p = new Phone();
        }
        else if(type.equals("Computer")) {
            p = new Computer();
        }
        else if(type.equals("product")) {
            p = new Product();
        }
        else{
            throw new IllegalArgumentException();
        }
        p.fromRequestable(requested);
        return p;
    }
    public ArrayList<Product> getProductsInCatagory(String catagoryName) throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("catagory",catagoryName);
        sendMinimalRequest(RequestType.GET_PRODUCTS_FROM_CATAGORY,fields);
        Request r = okOrDie();
        ArrayList<Product> products= new ArrayList<>();
        for(HashMap<String,String> requested : r.getObjs()){
            products.add(parseProduct(requested));
        }
        return products;
    }
    public ArrayList<Product> getProductsInDefaultCatagory() throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("default","true");
        sendMinimalRequest(RequestType.GET_PRODUCTS_FROM_CATAGORY,fields);
       
        Request r = okOrDie();
      
        ArrayList<Product> products= new ArrayList<>();
        for(HashMap<String,String> requested : r.getObjs()){
            products.add(parseProduct(requested));
        }
        return products;
    }
    //if not admin fails
    public void askToShutdown() throws  ClientError {
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        sendBlankRequest(RequestType.TERMINATE);
    }
    public void addCatagory(String catagory) throws ClientError {
            if(!isAdmin){
                throw new ClientError(ClientErrorType.INVALID_ACCESS);
            }
           
            HashMap<String,String> cat = new HashMap<>();
            cat.put("catagory",catagory);
            sendMinimalRequest(RequestType.CREATE_CATAGORY,cat);
       
           Request r = okOrDie();
    }
    public void removeCatagory(String catagory) throws ClientError {
        if(!isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> cat = new HashMap<>();
        cat.put("catagory-to-remove",catagory);
        sendMinimalRequest(RequestType.REMOVE_CATAGORY,cat);
        Request r = okOrDie();
    }
    public ArrayList<Catagory> allCatagories() throws ClientError {
     
        sendBlankRequest(RequestType.GET_ALL_CATAGORIES);
        
        Request r = okOrDie();
     
        ArrayList<Catagory> catagories = new ArrayList<>();
        for(HashMap<String,String> obj : r.getObjs()){
            if(obj == null){
                continue;
            }
            try{
                Catagory newC = new Catagory();
                newC.fromRequestable(obj);
                catagories.add(newC);
            }
            catch (Exception ignored){

            }

        }
        return catagories;
    }
    public ArrayList<Product> allProducts() throws ClientError {

        sendBlankRequest(RequestType.GET_ALL_PRODUCTS);
        Request r = okOrDie();
        ArrayList<Product> products = new ArrayList<>();
        for(HashMap<String,String> obj : r.getObjs()){
            if(obj == null){
                continue;
            }
            try {
                Product newP = new Product();
                newP.fromRequestable(obj);
                products.add(newP);
            }
            catch (Exception ignored){

            }

        }
        return products;
    }
    public void setDefaultCatagory(String catagory) throws ClientError {
        if(!isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> cat = new HashMap<>();
        cat.put("catagory",catagory);
        sendMinimalRequest(RequestType.SET_DEFAULT_CATAGORY,cat);
        Request r = okOrDie();
    }
    private Request okOrDie()throws ClientError{
        
        if(input == null){
            
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
           
            throw new ClientError(error);
        }
        else if(recived.getType() != RequestType.OK) {
         
            throw new ClientError(ClientErrorType.UNKNOWN);
        }
        return recived;

    }


    /*
    public boolean requestUserByEmail(String email, String password) {

    }
    public boolean requestUserByName(String name,String password) {

    }
    public boolean requestCatagory(String catagory_name) {

    }
    public boolean requestProduct(String product_name) {

}

     */


}
