
package edu.ucdenver.domain.client;

import edu.ucdenver.domain.order.Order;
import edu.ucdenver.domain.request.Request;
import edu.ucdenver.domain.request.RequestClientProtocol;
import edu.ucdenver.domain.request.RequestType;
import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client implements RequestClientProtocol {
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
                recived = okOrDie(this,input);
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

    public boolean isAdmin() {
        return isAdmin;
    }
    public Catagory getDefaultCatagory() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_DEFAULT_CATAGORY,output);
        Request r = okOrDie(this,input);
        
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

        RequestClientProtocol.sendMinimalRequestable(this,RequestType.ADD_PRODUCT_TO_CATALOG,p,output);
       
        Request r = okOrDie(this,input);
       
    }
    public ArrayList<Product> search(String text) throws ClientError {
        if(text == null){
            return allProducts();
        }
        HashMap<String,String> fields = new HashMap<>();
        fields.put("search",text);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.SEARCH,fields,output);
        Request r = okOrDie(this,input);
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
        RequestClientProtocol.sendMinimalRequest(this,RequestType.ADD_CATAGORY_TO_PRODUCT,fields,output);
        Request r = okOrDie(this,input);
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

    public void createAdmin(String email,String name,String password)throws ClientError{
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }

        User self = new User(email,name,password);
        RequestClientProtocol.sendMinimalRequestable(this,RequestType.ADD_ADMIN_USER,self,output);
        Request r = okOrDie(this,input);
    }
    public void removeUser(String email)throws ClientError{
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        //TODO
       //
       // sendMinimalRequestable(RequestType.ADD_ADMIN_USER,self);
        //Request r = okOrDie(this,input);
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
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_PRODUCT_FROM_CATALOG,fields,output);
  
        Request r = okOrDie(this,input);
      
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
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_CATAGORY_FROM_PRODUCT,fields,output);
        Request r = okOrDie(this,input);
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
        RequestClientProtocol.sendMinimalRequest(this,RequestType.GET_PRODUCTS_FROM_CATAGORY,fields,output);
        Request r = okOrDie(this,input);
        ArrayList<Product> products= new ArrayList<>();
        for(HashMap<String,String> requested : r.getObjs()){
            products.add(parseProduct(requested));
        }
        return products;
    }
    public ArrayList<Product> getProductsInDefaultCatagory() throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("default","true");
        RequestClientProtocol.sendMinimalRequest(this,RequestType.GET_PRODUCTS_FROM_CATAGORY,fields,output);
       
        Request r = okOrDie(this,input);
      
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
        RequestClientProtocol.sendBlankRequest(this,RequestType.TERMINATE,output);
    }
    public void addCatagory(String catagory) throws ClientError {
            if(!isAdmin){
                throw new ClientError(ClientErrorType.INVALID_ACCESS);
            }
           
            HashMap<String,String> cat = new HashMap<>();
            cat.put("catagory",catagory);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.CREATE_CATAGORY,cat,output);
       
           Request r = okOrDie(this,input);
    }
    public void removeCatagory(String catagory) throws ClientError {
        if(!isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> cat = new HashMap<>();
        cat.put("catagory-to-remove",catagory);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_CATAGORY,cat,output);
        Request r = okOrDie(this,input);
    }
    public ArrayList<Catagory> allCatagories() throws ClientError {

        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_ALL_CATAGORIES,output);
        
        Request r = okOrDie(this,input);
     
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
    public ArrayList<User> allUsers() throws ClientError {
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_ALL_USERS,output);
        Request r = okOrDie(this,input);
        ArrayList<User> users = new ArrayList<>();
        for(HashMap<String,String> obj : r.getObjs()){
            if(obj == null){
                continue;
            }
            try {

                User user = new User();
                user.fromRequestable(obj);
                users.add(user);
            }
            catch (Exception ignored){

            }

        }
        return users;
    }
    public void addAnotherUser(User u) throws ClientError{
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        u.setAdmin(false);
        RequestClientProtocol.sendMinimalRequestable(this,RequestType.CREATE_USER,u,output);
        Request r = okOrDie(this,input);
    }
    public Order currentOrder() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.CURRENT_ORDER,output);
        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        try {
           Order order = new Order();
            order.fromRequestable(r.getObjs().get(0));
            return order;
        }
        catch (Exception ignored){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
    }
    public ArrayList<Order> clientsOrders() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_USER_ORDERS,output);
        ArrayList<Order> orders = new ArrayList<>();
        Request r = okOrDie(this,input);
        for(HashMap<String,String> objs : r.getObjs() ){
            Order temp = new Order();
            temp.fromRequestable(objs);
            orders.add(temp);
        }
        return orders;
    }
    public Order finalizeOrder() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.FINALIZE_ORDER,output);

        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Order order = new Order();
        order.fromRequestable(r.getObjs().get(0));
        return order;
    }
    //returns updated order
    public Order addProductToOrder(Product p) throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product",p.getProductId());
        RequestClientProtocol.sendMinimalRequest(this,RequestType.ADD_PRODUCT_TO_ORDER,fields,output);
        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Order order = new Order();
        order.fromRequestable(r.getObjs().get(0));
        return order;
    }
    //needs admin access
    public ArrayList<Order> allFinalizedOrders() throws ClientError {
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_FINALIZED_ORDERS,output);
        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        ArrayList<Order> orders = new ArrayList<>();
        for(HashMap<String,String> objs : r.getObjs() ){
            Order temp = new Order();
            temp.fromRequestable(objs);
            orders.add(temp);
        }
        return orders;
    }
    //returns updated order
    public Order removeProductFromOrder(Product p) throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product",p.getProductId());
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_PRODUCT_FROM_ORDER,fields,output);
        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Order order = new Order();
        order.fromRequestable(r.getObjs().get(0));
        return order;
    }
    public ArrayList<Product> allProducts() throws ClientError {

        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_ALL_PRODUCTS,output);
        Request r = okOrDie(this,input);
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
        RequestClientProtocol.sendMinimalRequest(this,RequestType.SET_DEFAULT_CATAGORY,cat,output);
        Request r = okOrDie(this,input);
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
