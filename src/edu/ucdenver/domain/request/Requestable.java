package edu.ucdenver.domain.request;

import java.util.HashMap;
//prototcol used to turn objects into values that can be sent and parsed in a request.
public interface Requestable{
    HashMap<String,String> asRequestable();
    void fromRequestable(HashMap<String,String> requestable) throws IllegalArgumentException;
}
