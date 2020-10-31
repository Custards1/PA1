package edu.ucdenver.domain.order;

import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.parser.RequestObjectParser;
import edu.ucdenver.domain.products.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Order implements Requestable {
    private String orderId;
    private ArrayList<String> products;
    private LocalDate finalization;
    private boolean finalized;
    public String getId(){
        return orderId;
    }
    public void setId(String id){
        orderId=id;
    }
    public Order () {

        products = new ArrayList<>();
        finalized = false;
        orderId = "";
        finalization = LocalDate.of(2020,12,6);
    }
    public Order (String orderId) {

        products = new ArrayList<>();
        finalized = false;
        this.orderId = orderId;
        finalization = LocalDate.of(2020,12,6);
    }
    public HashMap<String, String> asRequestable() {
        HashMap<String,String> requestable = new HashMap<>();
        requestable.put("ordered-id",orderId.isEmpty()?"invalid":orderId);
        String temp = null;
        try{
            temp = RequestObjectParser.intoRaw(this.products);
            if(temp.isEmpty()){
                temp = "none";
            }

        }
        catch (Exception ignored){
            temp = "none";

        }
        requestable.put("ordered-products",temp);
        requestable.put("finalization-date",finalization.toString());
        requestable.put("finalized",isFinalized()?"true":"false");
        return requestable;
    }


    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        products = new ArrayList<>();
        finalized = false;
        orderId = "";
        finalization = LocalDate.of(2020,12,6);
        orderId = Product.argCheck(requestable,"ordered-id");
        String temp = Product.argCheck(requestable,"finalization-date");
        try {
            this.finalization = LocalDate.parse(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }

        temp = Product.argCheck(requestable,"ordered-products");
        if(!temp.isEmpty()){
            try {
                this.products = RequestObjectParser.fromRaw(temp);
            }
            catch (Exception e){
                throw new IllegalArgumentException();
            }
        }
        temp =requestable.get("finalized");
        if(temp!=null){
            finalized = temp.equals("true");
        }
    }
    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    public LocalDate getFinalization() {
        return finalization;
    }

    public void setFinalization(LocalDate finalization) {
        this.finalization = finalization;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }
}
