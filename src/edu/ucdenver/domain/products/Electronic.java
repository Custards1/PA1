package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
//This class represents an electronic, it implemets serializable
//so it can be saved to a file, and it implements requestable so it can
//be sent as a request and it inherits from the base Product class
public class Electronic extends Product implements Requestable, Serializable {

    private String serial;
    private LocalDate warrenty;
    public Electronic(){
      super();
        setType("Electronic");
        serial = new String();
        warrenty = LocalDate.now();
    }
    public Electronic(
            String productName,
            String brandName,
            String description,
            LocalDate   doi, //Date of Incorperation
            String serial,
            LocalDate warrenty
    ){
        super(productName,brandName,description,doi);
        this.serial = serial;
        this.warrenty = warrenty;
    }
    @Override
    public HashMap<String, String> asRequestable()  {
        HashMap<String, String> base = super.asRequestable();
        base.put("serial",serial);
        base.put("warrenty",warrenty.toString());
        return base;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
       
        super.fromRequestable(requestable);
        setType("Electronic");
        this.serial = new String();
        this.warrenty = LocalDate.now();
        this.serial = argCheck(requestable,"serial");
        String temp = argCheck(requestable,"warrenty");
        try {
            this.warrenty = LocalDate.parse(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();

        }
       
    }
    @Override
    public ArrayList<String> asDisplayable() {
        ArrayList<String> displayable = super.asDisplayable();
        displayable.add(String.format("Serial %s",serial));
        displayable.add(String.format("Warranty valid until %s",warrenty.toString()));
        return displayable;
    }
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public LocalDate getWarrenty() {
        return warrenty;
    }

    public void setWarrenty(LocalDate warrenty) {
        this.warrenty = warrenty;
    }

}
