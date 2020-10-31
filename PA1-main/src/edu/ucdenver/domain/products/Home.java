package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;

import java.time.LocalDate;
import java.util.HashMap;

public class Home extends Product implements Requestable {

    private String location;
    public Home(){
        super();
        setType("Home");
        location = new String();
    }
    public Home(
                String productName,
                String brandName,
                String description,
                LocalDate doi, //Date of Incorperation
                String location
    ){
        super(productName,brandName,description,doi);
        this.location = location;
        setType("Home");
    }
    @Override
    public HashMap<String, String> asRequestable() {
        setType("Home");
        HashMap<String, String> base = super.asRequestable();
        base.put("location",this.location);
        return base;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        super.fromRequestable(requestable);
        setType("Home");
        this.location = new String();
        this.location = argCheck(requestable,"location");
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
