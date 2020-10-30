package edu.ucdenver.domain.order;

import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.category.CatagoryParser;
import edu.ucdenver.domain.products.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Order implements Requestable {

    private ArrayList<String> products;
    private LocalDate finalization;
    private boolean finalized;
    public Order () {
        products = new ArrayList<>();
        finalized = false;
        finalization = LocalDate.of(2020,12,6);
    }
    public HashMap<String, String> asRequestable() {
        HashMap<String,String> requestable = new HashMap<>();
        String temp = null;
        try{
            temp = CatagoryParser.intoRaw(this.products);
            if(temp == null || temp.isEmpty()){
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
        finalization = LocalDate.of(2020,12,6);
        String temp = Product.argCheck(requestable,"finalization-date");
        try {
            this.finalization = LocalDate.parse(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }

        temp = Product.argCheck(requestable,"ordered-products");
        if(temp!=null && !temp.isEmpty()){
            try {
                this.products = CatagoryParser.fromRaw(temp);
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
