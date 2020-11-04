package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.parser.RequestObjectParser;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
//This class represents a computer, it implemets serializable
//so it can be saved to a file, and it implements requestable so it can
//be sent as a request and it inherits from the base Product class and the Electronic class
public class Computer extends Electronic implements Requestable, Serializable {


    private ArrayList<String> technicalSpecs;
    public Computer(
            String productName,
            String brandName,
            String description,
            LocalDate doi, //Date of Incorperation
            String serial,
            LocalDate warrenty,
            ArrayList<String> technicalSpecs
    ){
        super(productName,brandName,description,doi,serial,warrenty);
        setType("Computer");
        this.technicalSpecs =technicalSpecs;
    }
    public Computer() {
        super();
        setType("Computer");
        this.technicalSpecs =new ArrayList<>();
    }
    @Override
    public HashMap<String, String> asRequestable() {
        
        setType("Computer");
        HashMap<String, String> base = super.asRequestable();
       
        base.put("specs", technicalSpecs.isEmpty()?"None":RequestObjectParser.intoRaw(technicalSpecs));
     
        return base;
    }
    @Override
    public ArrayList<String> asDisplayable() {
        ArrayList<String> displayable = super.asDisplayable();
        if(technicalSpecs.isEmpty()){
            displayable.add(String.format("Specs: None"));
        }
        else{
                displayable.add("Specs:");
            for(String name : technicalSpecs){
                displayable.add(String.format("      %s",name));
            }
        }
        return displayable;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        super.fromRequestable(requestable);
        setType("Computer");
        this.technicalSpecs =new ArrayList<>();
        String temp = argCheck(requestable,"specs");
        if(temp!=null && !temp.isEmpty()){
            try {
                this.technicalSpecs = RequestObjectParser.fromRaw(temp);
            }
            catch (Exception e){
                throw new IllegalArgumentException();
            }
        }

    }
    public ArrayList<String> getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(ArrayList<String> technicalSpecs) {
        this.technicalSpecs = technicalSpecs;
    }
}
