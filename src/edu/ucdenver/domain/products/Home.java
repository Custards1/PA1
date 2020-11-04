package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

//This class represents a home, it implemets serializable
//so it can be saved to a file, and it implements requestable so it can
//be sent as a request and it inherits from the base Product class
public class Home extends Product implements Requestable , Serializable {

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
    public ArrayList<String> asDisplayable(){
        ArrayList<String> diplayable = super.asDisplayable();
        diplayable.add(String.format("Location: %s",location));
        return diplayable;
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
