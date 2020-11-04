package edu.ucdenver.domain.store;

import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//This class is used to store products and catagories for a store.
//It is also used to store a collection of users and their orders,
//because its parent class is the user store.
//This class enforces admin access to retrive certail information.
//It is also able to be saved to a file because it implements Serializable.
public class ItemStore extends UserStore implements Serializable {
    private ArrayList<Product> products;
    private ArrayList<Catagory> catagories;
    private HashMap<String,Integer> catagoryNames;
    private HashMap<String,Integer> productNames;
    private int defaultCatagory;
    public ItemStore(){
        super();
        defaultCatagory=0;
        this.products = new ArrayList<>();
        this.catagories = new ArrayList<>();
        this.catagoryNames = new HashMap<>();
        this.productNames = new HashMap<>();
    }

    // forces a cotagory to be added or retrived, if string catatory is null, returns null
    private synchronized Catagory forceAddCatagory(String catagory){
        if(catagory == null){
            return null;
        }
        if(!catagoryNames.containsKey(catagory)) {
            Catagory cat = new Catagory(catagory);
            catagories.add(cat);
            catagoryNames.put(cat.getName(),catagories.size()-1);
            return catagories.get(catagories.size()-1);
        }
        else {
            return catagories.get(catagoryNames.get(catagory));
        }
    }
    // returns the defult catagory
    public synchronized Catagory getDefaultCatagory() throws IllegalArgumentException{
        if(defaultCatagory >= catagories.size()) {
            throw new IllegalArgumentException();
        }
        return catagories.get(defaultCatagory);
    }
    // removes prduct from catagory and returns the modifed procut, throws on failure
    public synchronized Product removeCatagoryFromProduct(User admin, String catagory, String productId) throws IllegalArgumentException {
        if(productId == null||catagory == null ||!validAdminAuthentication(admin)) {
            throw new IllegalArgumentException();
        }
        Product product = getProduct(productId);

        Catagory cat = getCatagory(catagory);
        cat.getProducts().remove(productId);
        product.getCatagories().remove(cat.getName());
        if(product.getCatagories().isEmpty()){
            product.getCatagories().add(getDefaultCatagory().getName());
        }
        return product;
    }
    // returns all products in the database
    public synchronized ArrayList<Product> allProducts(){
        ArrayList<Product> products = new ArrayList<>();
        for(Map.Entry<String,Integer> name : productNames.entrySet()){
            products.add(getProduct(name.getKey()));
        }
        return products;
    }
    // returns all catagories in the database
    public synchronized ArrayList<Catagory> allCatagories(){
        ArrayList<Catagory> catagories = new ArrayList<>();
        for(Map.Entry<String,Integer> name : catagoryNames.entrySet()){
            catagories.add(getCatagory(name.getKey()));
        }
        return catagories;
    }
    // searches product name and description for the specified text.
    public synchronized ArrayList<Product> searchProducts(String search){
        ArrayList<Product> catagories = new ArrayList<>();
        HashMap<String,Integer> addedProducts = new HashMap<>();

        String s = search.toLowerCase();
        for(Map.Entry<String,Integer> name : productNames.entrySet()){

            if(name.getKey().toLowerCase().contains(s)){

                if(!addedProducts.containsKey(name.getKey())){
                    catagories.add(products.get(name.getValue()));
                    addedProducts.put(name.getKey(),name.getValue());
                }
            }
            else {

                Product maybe = products.get(name.getValue());

                if(maybe.getProductName().toLowerCase().contains(s)) {

                    catagories.add(maybe);
                    addedProducts.put(name.getKey(),name.getValue());
                }
                else{
                    if(maybe.getDescription().toLowerCase().contains(s)) {

                        catagories.add(maybe);
                        addedProducts.put(name.getKey(),name.getValue());
                    }


                }
            }
        }
        return catagories;
    }
    // adds a catagory to a product, returns the modifed product, throws on failure
    public synchronized Product addCatagoryToProduct(User admin,String catagory,String productId) throws IllegalArgumentException{
        if(productId == null||catagory == null ||!validAdminAuthentication(admin)) {
            throw new IllegalArgumentException();
        }
        Product product = getProduct(productId);

        Catagory cat = null;
        try {
            cat = getCatagory(catagory);
            if(cat ==null){
                throw new IllegalArgumentException();
            }
        }
        catch (Exception e){
            cat = forceAddCatagory(catagory);
            if(cat ==null){
                throw new IllegalArgumentException();
            }
        }
        cat.addProduct(productId);
        if(!product.getCatagories().contains(cat.getName())){
            product.getCatagories().add(cat.getName());
        }
        return product;
    }
    //returns a product with specified id, throws if not found
    public Product getProduct(String productId) throws IllegalArgumentException {
        Integer h = productNames.get(productId);
        if(h != null) {
            Product k = products.get(h);
            if(k!=null){
                return k;
            }
        }
        throw new IllegalArgumentException();
    }
    //returns a product with specified name, throws if not found
    public Product getProductByName(String productName) throws IllegalArgumentException {
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
            id.append(word);
        }
        String productId = id.toString();
        Integer h = productNames.get(productId);
        if(h != null) {
            Product k = products.get(h);
            if(k!=null){
                return k;
            }
        }
        throw new IllegalArgumentException();
    }
    //returns a catagory with specified name, throws on failure.
    public synchronized Catagory getCatagory(String catagory) throws IllegalArgumentException{
        Integer g = catagoryNames.get(catagory);
        if(g==null||g >= catagories.size()){
            throw new IllegalArgumentException();
        }
        Catagory c = catagories.get(g);
        if(c ==null){
            throw new IllegalArgumentException();
        }
        return c;
    }
    //returns all products with a specified catagory name.
    public ArrayList<Product> getProductsFromCatagory(String catagoryName) throws IllegalArgumentException {
        Integer i = catagoryNames.get(catagoryName);
        if(i == null || i >=catagories.size()){
            throw new IllegalArgumentException();
        }

        ArrayList<Product> p = new ArrayList<>();
        for (String product : catagories.get(i).getProducts()) {
            Integer h = productNames.get(product);
            if(h != null) {
                Product k = products.get(h);
                if(k!=null){
                    p.add(k);
                }
            }

        }
        return p;
    }
    //adds product to the database.
    private synchronized void addProductsRaw(Product p){
        products.add(p);
        productNames.put(p.getProductId(),products.size()-1);
    }
    //removes product from the database, requires admin authentication
    public synchronized void removeProduct(User admin,String productId) throws IllegalArgumentException{
        if(productId == null||!validAdminAuthentication(admin)) {

            throw new IllegalArgumentException();
        }

        Integer h = productNames.get(productId);

        if(h == null) {

            throw new IllegalArgumentException();
        }

        Product temp = products.get(h);
        for(String catagoyName : temp.getCatagories()){
            Catagory g = getCatagory(catagoyName);
            g.getProducts().remove(temp.getProductId());
        }

        try{
            products.set(h,new Product());

        }
        catch (Exception e){

        }

        try{
            productNames.remove(productId);

        }
        catch (Exception e){

        }


    }
    //adds a product to the store, failes if user is not admin
    public synchronized void addProduct(User admin,Product p) throws IllegalArgumentException {

        if(p == null||!validAdminAuthentication(admin)) {

            throw new IllegalArgumentException();
        }
        ArrayList<String> catalog =p.getCatagories();
        int iter= 0;
        for(String catagory :catalog ){
            if(catagory.equals("default")) {
                Catagory defaultC = getDefaultCatagory();
                defaultC.addProduct(p.getProductId());

                p.getCatagories().set(iter,defaultC.getName());
            }
            else {
                if(!catagory.isEmpty()){
                    Integer i = catagoryNames.get(catagory);
                    if(i == null || i >=catagories.size()){
                        addCatagory(admin,catagory);
                        Integer ii = catagoryNames.get(catagory);
                        catagories.get(ii).addProduct(p.getProductId());
                    }
                    else{
                        catagories.get(i).addProduct(p.getProductId());
                    }

                }

            }
            iter++;
        }
        if(catalog.isEmpty()) {
            try{
                Catagory c = getDefaultCatagory();
                if(c !=null){
                    c.addProduct(p.getProductId());
                    catalog.add(c.getName());
                }
            }
            catch (Exception e){

            }
        }
        addProductsRaw(p);
    }
    //sets the default catagory, failes if user is not admin
    public synchronized void setDefaultCatagory(User admin,String name) throws IllegalArgumentException{
        Integer i = catagoryNames.get(name);
        if(i == null || i >=catagories.size()||!validAdminAuthentication(admin)){
            throw new IllegalArgumentException();
        }
        try{
            Catagory old = catagories.get(defaultCatagory);
            if(old!=null){
                old.setDefault(false);
            }
        }
        catch (Exception ignored){

        }

        defaultCatagory = i;
        try{
            Catagory newC = catagories.get(defaultCatagory);
            if(newC!=null){
                newC.setDefault(true);
            }
        }
        catch (Exception ignored){

        }
    }
    //adds a catagory, failes if user is not admin
    public synchronized void addCatagory(User admin,String catagory) throws IllegalArgumentException {
        if(catagory == null||!validAdminAuthentication(admin)) {
            throw new IllegalArgumentException();
        }

        forceAddCatagory(catagory);
    }
    //removes a catagory, failes if user is not admin
    public synchronized void removeCatagory(User admin,String catagory) throws IllegalArgumentException {
        if(catagory == null||!validAdminAuthentication(admin)) {

            throw new IllegalArgumentException();
        }

        Integer h = catagoryNames.get(catagory);
        if(h == null) {

            throw new IllegalArgumentException();
        }

        Catagory temp = catagories.get(h);
        Catagory def =getDefaultCatagory();
        if(def.getName().equals(temp.getName())) {
            if(catagoryNames.size()<=1){
                addCatagory(admin, "default");
                setDefaultCatagory(admin,"default");
            }
            else {
                boolean good = false;
                for(Map.Entry<String,Integer> entry : catagoryNames.entrySet()) {
                    if(!entry.getKey().equals(def.getName())) {
                        setDefaultCatagory(admin,entry.getKey());
                        good = true;
                        break;
                    }
                }
                if(!good){
                    addCatagory(admin, "default");
                    setDefaultCatagory(admin,"default");
                }
            }
            def = getDefaultCatagory();
            for(String productName : temp.getProducts()){
                Product p = getProduct(productName);
                int i  = p.getCatagories().indexOf(temp.getName());
                if(i!=-1){
                    p.getCatagories().set(i,def.getName());
                }
            }
        }
        else {
            for(String productName : temp.getProducts()){
                Product p = getProduct(productName);
                p.getCatagories().remove(temp.getName());
            }
        }


        try{
            catagories.set(h,new Catagory());

        }
        catch (Exception ignored){

        }
        try{
            catagoryNames.remove(catagory);

        }
        catch (Exception ignored){

        }
    }

}
