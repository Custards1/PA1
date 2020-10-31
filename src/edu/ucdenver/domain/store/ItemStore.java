package edu.ucdenver.domain.store;

import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public synchronized Catagory forceAddCatagory(String catagory){
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
    public synchronized Catagory getDefaultCatagory() throws IllegalArgumentException{
        if(defaultCatagory >= catagories.size()) {
            throw new IllegalArgumentException();
        }
        return catagories.get(defaultCatagory);
    }
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
    public synchronized ArrayList<Product> allProducts(){
        ArrayList<Product> products = new ArrayList<>();
        for(Map.Entry<String,Integer> name : productNames.entrySet()){
            products.add(getProduct(name.getKey()));
        }
        return products;
    }
    public synchronized ArrayList<Catagory> allCatagories(){
        ArrayList<Catagory> catagories = new ArrayList<>();
        for(Map.Entry<String,Integer> name : catagoryNames.entrySet()){
            catagories.add(getCatagory(name.getKey()));
        }
        return catagories;
    }
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
    public synchronized void addProductsRaw(Product p ){
        products.add(p);
        productNames.put(p.getProductId(),products.size()-1);
    }
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
    public synchronized void addProduct(User admin,Product p) {
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
                Integer i = catagoryNames.get(catagory);
                if(i == null || i >=catagories.size()){
                    throw new IllegalArgumentException();
                }
                catagories.get(i).addProduct(p.getProductId());
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
    public synchronized void addCatagory(User admin,String catagory) throws IllegalArgumentException {
        if(catagory == null||!validAdminAuthentication(admin)) {
            throw new IllegalArgumentException();
        }

        forceAddCatagory(catagory);
    }
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
