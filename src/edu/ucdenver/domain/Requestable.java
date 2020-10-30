package edu.ucdenver.domain;

import java.util.HashMap;

public interface Requestable{
    HashMap<String,String> asRequestable();
    void fromRequestable(HashMap<String,String> requestable) throws IllegalArgumentException;

}
