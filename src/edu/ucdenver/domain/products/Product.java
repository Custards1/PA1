
package edu.ucdenver.domain.products;
import edu.ucdenver.domain.Requestable;
import edu.ucdenver.domain.category.CatagoryParser;

import java.time.LocalDate;
import java.util.*;

public class Product implements Requestable {

    private String productId;
    private String productName;
    private String brandName;
    private String description;


    private String type;
    private ArrayList<String> catagories;
    private LocalDate doi; //Date of Incorperation
    //get/set for products

    public Product() {
      clear();
        this.catagories = new ArrayList<>();
    }
    public void clear(){
        this.productId=new String();
        this.productName=new String();
        this.brandName=new String();
        this.description=new String();
        this.doi= LocalDate.now();
        this.type = "product";
    }

    public Product(
                    String productName,
                    String brandName,
                    String description,
                    LocalDate   doi //Date of Incorperation
                    ) {
        StringBuilder id = new StringBuilder();
        for(String word : productName.split("\\s")) {
           id.append(word);
        }
        this.productId=id.toString();
        this.productName=productName;
        this.brandName=brandName;
        this.description=description;
        this.doi=doi;
        this.catagories = new ArrayList<>();
    }

    public HashMap<String, String> asRequestable() {
        HashMap<String,String> requestable = new HashMap<>();
        requestable.put("product-id",this.productId);
        requestable.put("product-name",this.productName);
        requestable.put("product-brand-name",this.brandName);
        requestable.put("product-description",this.description);
        requestable.put("product-doi",this.doi.toString());
        requestable.put("product-type",this.type);
        String temp = null;
        try{
            temp = CatagoryParser.intoRaw(this.catagories);
            if(temp == null || temp.isEmpty()){
                temp = "default";
            }

        }
        catch (Exception ignored){
            temp = "default";

        }
        requestable.put("product-catagory", temp);
        return requestable;
    }
    public static String argCheck(HashMap<String, String> requestable,String arg)throws IllegalArgumentException  {
        String temp = null;
        temp = requestable.get(arg);
        if(temp==null){
            throw new IllegalArgumentException(String.format("null %s",arg));
        }
        return temp;
    }
    public static Product fromObj(HashMap<String,String> obj) throws IllegalArgumentException{
        Product product = new Product();
        product.fromRequestable(obj);
        return product;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getCatagories() {
        return catagories;
    }

    public void setCatagories(ArrayList<String> catagories) {
        this.catagories = catagories;
    }

    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        clear();

        this.catagories = new ArrayList<>();


        this.productId = argCheck(requestable,"product-id");

        this.productName = argCheck(requestable,"product-name");

        this.brandName = argCheck(requestable,"product-brand-name");

        this.description = argCheck(requestable,"product-description");

        this.type = argCheck(requestable,"product-type");

        String temp = argCheck(requestable,"product-doi");

        try {
            this.doi = LocalDate.parse(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }

        temp = argCheck(requestable,"product-catagory");
        if(temp!=null && !temp.isEmpty()){
            try {
                this.catagories = CatagoryParser.fromRaw(temp);
            }
            catch (Exception e){
                throw new IllegalArgumentException();
            }
        }


    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDoi() {
        return doi;
    }

    public void setDoi(LocalDate doi) {
        this.doi = doi;
    }

}