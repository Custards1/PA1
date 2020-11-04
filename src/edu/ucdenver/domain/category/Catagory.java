package edu.ucdenver.domain.category;

import edu.ucdenver.domain.parser.RequestObjectParser;
import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.products.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


//This class represents a catagory in the store, it can be sent as part of a request because
//it implents Requestable, and it can be saved to a file because it implements,
//serializable.
public class Catagory implements Requestable, Serializable {
    private String name;
    private ArrayList<String> products;
    private boolean isDefault;
    public Catagory(String name){
        this.name = name;
        this.products = new ArrayList<>();
        isDefault=false;
    }
    public Catagory(){
        this.name = new String();
        this.products = new ArrayList<>();
        isDefault=false;
    }
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getName() {
        return name;
    }
    public void print(){
        System.out.println(name);
        for(String product : products){
            System.out.print('\t');
            System.out.print(product);
        }
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    @Override
    public HashMap<String, String> asRequestable() {
        HashMap<String, String> requestable = new HashMap<>();
        requestable.put("catagory-name",this.name);
        requestable.put("catagory-is-default",this.isDefault?"true":"false");
        requestable.put("product-list", RequestObjectParser.intoRaw(this.products));
        return requestable;
    }

    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        this.name = new String();
        this.products = new ArrayList<>();
        String temp = requestable.get("catagory-is-default");
        if(temp!=null ){
            this.isDefault = temp.equals("true");
        }
        this.name = Product.argCheck(requestable,"catagory-name");
        try{
            this.products = RequestObjectParser.fromRaw(Product.argCheck(requestable,"product-list"));
        }
        catch (Exception e){
           throw e;
        }
    }

    public void addProduct(String name) {
        if(!this.products.contains(name)){
            this.products.add(name);
        }
    }
}
