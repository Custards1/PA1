
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

//This class communicates with a server to get objects from the store.
public class Client implements RequestClientProtocol {

    private Socket socket;
    private BufferedReader input = null;
    private PrintWriter output = null;
    private boolean isAdmin;
    private User user;

    //Acceses server on specified host and port, using the specified user. If signup is true,
    //the server will attempt to create a new user, if false it will attempt to log the user in.
    //Throws an error if unable to authenticate or communicate with the server.
    public Client(String host,int port,User self,boolean signup)throws ClientError{

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

    //retrives the default catagory, throws error if unable to communicate with server.
    public synchronized Catagory getDefaultCatagory() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.GET_DEFAULT_CATAGORY,output);
        Request r = okOrDie(this,input);
        
        Catagory res = new Catagory();
        try {
            res.fromRequestable(r.getObjs().get(0));
            
            return res;
        }
        catch (Exception e) {
            
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
    }
    //retrives a product by its name, throws error if unable to communicate with server.
    public synchronized Product getProductByName(String name) throws  ClientError{
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product",name);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.GET_PRODUCT_BY_NAME,fields,output);
        Request r = okOrDie(this,input);
        try {
            return parseProduct(r.getObjs().get(0));
        }
        catch (Exception e) {

            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }

    }
    //retrives a product by its id, throws error if unable to communicate with server.
    public synchronized Product getProduct(String id) throws  ClientError{
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product-id",id);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.GET_PRODUCT_BY_NAME,fields,output);
        Request r = okOrDie(this,input);
        try {
            return parseProduct(r.getObjs().get(0));
        }
        catch (Exception e) {

            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }

    }
    //adds a product to the catalog, throws error if unable to communicate with server.
     public synchronized void addProductToCatalog(Product p) throws ClientError {

        if(!this.isAdmin|| p == null){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }

        RequestClientProtocol.sendMinimalRequestable(this,RequestType.ADD_PRODUCT_TO_CATALOG,p,output);

        Request r = okOrDie(this,input);

       
    }
    //reterns a list of products that match given search, throws error if unable to communicate with server.
    public synchronized ArrayList<Product> search(String text) throws ClientError {
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
    //returns updated product if succesfull,throws error if unable to communicate with server.
    public synchronized Product addCatagoryToProduct(String catagoryName,String productid) throws ClientError{
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
    //creates a new admin user,throws error if unable to communicate with server.
    //OR if the clients connceted user is not admin
    public synchronized void createAdmin(String email,String name,String password)throws ClientError{
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }

        User self = new User(email,name,password);
        RequestClientProtocol.sendMinimalRequestable(this,RequestType.ADD_ADMIN_USER,self,output);
        Request r = okOrDie(this,input);
    }
   //adds a catagory to a products,throws error if unable to communicate with server.
   //OR if the clients connceted user is not admin
    public synchronized void addCatagoryToProductByName(String catagoryName, String productName)throws ClientError{
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        addCatagoryToProduct(catagoryName, id.toString());
    }
    //removes a product from the catalog by id,throws error if unable to communicate with server.
    //OR if the clients connceted user is not admin
    public synchronized void removeProductFromCatalog(String productId) throws ClientError {
        
        if(!this.isAdmin || productId == null){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
      
        HashMap<String,String> fields = new HashMap<>();
      
        fields.put("product-to-remove",productId);
        
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_PRODUCT_FROM_CATALOG,fields,output);
       
        Request r = okOrDie(this,input);
      
    }
    //removes a product from the catalog by name,throws error if unable to communicate with server.
    //OR if the clients connceted user is not admin
    public synchronized  void removeProductFromCatalogByName(String productName) throws ClientError {
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        removeProductFromCatalog(id.toString());
    }
    //removes a catagory from a product by id,throws error if unable to communicate with server.
    //OR if the clients connceted user is not admin
    public synchronized Product removeCatagoryFromProduct(String catagoryName, String productid) throws ClientError{
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
        return p;
    }
    //removes a catagory from a product by name,throws error if unable to communicate with server.
    //OR if the clients connceted user is not admin
    public synchronized void removeCatagoryFromProductByName(String catagoryName, String productName) throws ClientError{
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        removeCatagoryFromProduct(catagoryName, id.toString());
    }
    //Parses a product from the given raw product, throws error if unable to parse.
    protected synchronized static Product parseProduct(HashMap<String,String> requested) throws IllegalArgumentException{
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
    //Parses a product from the given raw product, throws error if unable to parse.
    public synchronized ArrayList<Product> getProductsInCatagory(String catagoryName) throws ClientError {
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
    //gets products in the default catagory,throws error if unable to communicate with server.
    public synchronized  ArrayList<Product> getProductsInDefaultCatagory() throws ClientError {
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
    //asks server to shutdown,throws error if unable to communicate with server OR if not admin fails
    public synchronized void askToShutdown() throws  ClientError {
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        RequestClientProtocol.sendBlankRequest(this,RequestType.TERMINATE,output);
    }
    //adds a catagory to the catalog,throws error if unable to communicate with server OR if not admin fails
    public synchronized void addCatagory(String catagory) throws ClientError {
            if(!isAdmin){
                throw new ClientError(ClientErrorType.INVALID_ACCESS);
            }
           
            HashMap<String,String> cat = new HashMap<>();
            cat.put("catagory",catagory);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.CREATE_CATAGORY,cat,output);
       
           Request r = okOrDie(this,input);
    }
    //removes a catagory to the catalog,throws error if unable to communicate with server OR if not admin fails
    public synchronized void removeCatagory(String catagory) throws ClientError {
        if(!isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> cat = new HashMap<>();
        cat.put("catagory-to-remove",catagory);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.REMOVE_CATAGORY,cat,output);
        Request r = okOrDie(this,input);
    }
    //retives all catagories,throws error if unable to communicate with server
    public synchronized ArrayList<Catagory> allCatagories() throws ClientError {

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
    //retives all users,throws error if unable to communicate with server OR if not admin
    public synchronized ArrayList<User> allUsers() throws ClientError {
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
    //adds another user,throws error if unable to communicate with server
    public synchronized void addAnotherUser(User u) throws ClientError{
        if(!this.isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        u.setAdmin(false);
        RequestClientProtocol.sendMinimalRequestable(this,RequestType.CREATE_USER,u,output);
        Request r = okOrDie(this,input);
    }
    //retives the current order,throws error if unable to communicate with server
    public synchronized Order currentOrder() throws ClientError {

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
    //clears current order,throws error if unable to communicate with server
    public synchronized Order clearOrder() throws ClientError {

        RequestClientProtocol.sendBlankRequest(this,RequestType.CLEAR_ORDER,output);

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
    //retrives currents users orders,throws error if unable to communicate with server
    public synchronized ArrayList<Order> clientsOrders() throws ClientError {
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
    //retrives a specific users orders,throws error if unable to communicate with server
    public synchronized ArrayList<Order> clientsOrders(User user) throws ClientError {
        RequestClientProtocol.sendMinimalRequestable(this,RequestType.GET_USER_ORDERS,user,output);
        ArrayList<Order> orders = new ArrayList<>();
        Request r = okOrDie(this,input);
        for(HashMap<String,String> objs : r.getObjs() ){
            Order temp = new Order();
            temp.fromRequestable(objs);
            orders.add(temp);
        }
        return orders;
    }
    //retrives a specific users orders by email,throws error if unable to communicate with server
    public synchronized ArrayList<Order> clientsOrdersByEmail(String email) throws ClientError {
        HashMap<String,String> ss = new HashMap<>();
        ss.put("email",email);
        RequestClientProtocol.sendMinimalRequest (this,RequestType.GET_USER_ORDERS,ss,output);
        ArrayList<Order> orders = new ArrayList<>();
        Request r = okOrDie(this,input);
        for(HashMap<String,String> objs : r.getObjs() ){
            Order temp = new Order();
            temp.fromRequestable(objs);
            orders.add(temp);
        }
        return orders;
    }
    //retrives a specific users orders by id,throws error if unable to communicate with server
    public Order clientsOrderById(String newValue) throws ClientError {
        HashMap<String,String> ss = new HashMap<>();
        ss.put("id",newValue);
       
        RequestClientProtocol.sendMinimalRequest (this,RequestType.GET_USER_ORDERS,ss,output);
        ArrayList<Order> orders = new ArrayList<>();
        
        Request r = okOrDie(this,input);
       
        Order o = new Order();
        try {
            o.fromRequestable(r.getObjs().get(0));
        }
        catch (Exception e){
            throw new ClientError(ClientErrorType.INVALID_REQUEST);
        }
        return o;
    }
    //finalizes and returns current order,throws error if unable to communicate with server
    public synchronized Order finalizeOrder() throws ClientError {
        RequestClientProtocol.sendBlankRequest(this,RequestType.FINALIZE_ORDER,output);

        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Order order = new Order();
        order.fromRequestable(r.getObjs().get(0));
        return order;
    }
    //returns updated order,throws error if unable to communicate with server
    public synchronized void addProductToOrder(Product p) throws ClientError {
        HashMap<String,String> fields = new HashMap<>();
        fields.put("product",p.getProductId());
        RequestClientProtocol.sendMinimalRequest(this,RequestType.ADD_PRODUCT_TO_ORDER,fields,output);
        Request r = okOrDie(this,input);
        if(r.getObjs().isEmpty()){
            throw new ClientError(ClientErrorType.INVALID_RESOURCE);
        }
        Order order = new Order();
        order.fromRequestable(r.getObjs().get(0));
    }
    //returns updated order,throws error if unable to communicate with server OR if not admin
    public synchronized ArrayList<Order> allFinalizedOrders() throws ClientError {
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
    //removes a product from an order and returns removed product,throws error if unable to communicate with server
    public synchronized Order removeProductFromOrder(Product p) throws ClientError {
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
    //returns all products,throws error if unable to communicate with server
    public synchronized ArrayList<Product> allProducts() throws ClientError {

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
    //sets the default catagory,throws error if unable to communicate with server OR if not admin
    public synchronized void setDefaultCatagory(String catagory) throws ClientError {
        if(!isAdmin){
            throw new ClientError(ClientErrorType.INVALID_ACCESS);
        }
        HashMap<String,String> cat = new HashMap<>();
        cat.put("catagory",catagory);
        RequestClientProtocol.sendMinimalRequest(this,RequestType.SET_DEFAULT_CATAGORY,cat,output);
        Request r = okOrDie(this,input);
    }
    //shutsdown conncetion
    public void shutdown(){
        if(this.socket!=null){
            try {

                this.socket.close();
            }
            catch (Exception ignored){

            }
            this.socket = null;
        }
        if(this.input!=null){
            try {

                this.input.close();
            }
            catch (Exception ignored){

            }
            this.input = null;
        }
        if(this.output!=null){
            try {

                this.output.close();
            }
            catch (Exception ignored){

            }
            this.output = null;
        }

    }



}
