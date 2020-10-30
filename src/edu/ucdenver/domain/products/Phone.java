package edu.ucdenver.domain.products;

import edu.ucdenver.domain.Requestable;

import java.time.LocalDate;
import java.util.HashMap;

public class Phone extends Electronic implements Requestable {


    private String imei;
    private String os;
    public Phone() {
        super();
        setType("Phone");
        imei = new String();
        os = new String();
    }
    public Phone(
    String productName,
    String brandName,
    String description,
    LocalDate doi, //Date of Incorperation
    String serial,
    LocalDate warrenty,
    String imei,
    String os
    ) {
        super(productName,brandName,description,doi,serial,warrenty);
        this.imei = imei;
        this.os = os;
    }
    @Override
    public HashMap<String, String> asRequestable()  {
        setType("Phone");
        HashMap<String, String> base = super.asRequestable();
        base.put("imei",imei);
        base.put("os",os);
        return base;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {
        super.fromRequestable(requestable);
        setType("Phone");
        this.imei = new String();
        this.os = new String();
        this.imei = argCheck(requestable,"imei");
        this.os = argCheck(requestable,"os");
    }
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
