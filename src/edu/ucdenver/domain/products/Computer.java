package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.category.CatagoryParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Computer extends Electronic implements Requestable {


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
        base.put("specs", CatagoryParser.intoRaw(technicalSpecs));
        return base;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        super.fromRequestable(requestable);
        setType("Computer");
        this.technicalSpecs =new ArrayList<>();
        String temp = argCheck(requestable,"specs");
        if(temp!=null && !temp.isEmpty()){
            try {
                this.technicalSpecs = CatagoryParser.fromRaw(temp);
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
