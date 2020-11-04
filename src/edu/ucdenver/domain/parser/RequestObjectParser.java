package edu.ucdenver.domain.parser;

import edu.ucdenver.domain.request.Request;

import java.util.ArrayList;
//This class is used to convert lists of strings into a format acceptable to send in a request and back
public class RequestObjectParser {
    //converts array of strings into a request friendly format.
    public static String intoRaw(ArrayList<String> listing) {
        StringBuilder bob = new StringBuilder();
        for (String entry : listing) {
            if (entry == null){
                continue;
            }
            bob.append(entry.replace("|","||"));
            bob.append("|");
        }
        if(bob.length() > 0){
            bob.deleteCharAt(bob.length()-1);
        }
        return bob.toString().replace("|","||");
    }
    //retrives an array of strings into a request friendly format.
    public static ArrayList<String> fromRaw(String raw) throws IllegalArgumentException{
        if (raw == null||raw.isEmpty() ){
            throw new IllegalArgumentException("Null request.");
        }
        ArrayList<String> toRet = new ArrayList<>();
        for(String splice : raw.replace("||","|").split(Request.seperater)) {
            toRet.add(splice.trim());
        }
        if(toRet.isEmpty()) {
            throw  new IllegalArgumentException();
        }
        return toRet;
    }

}
