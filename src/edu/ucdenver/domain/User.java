package edu.ucdenver.domain;

import java.util.HashMap;
import java.util.function.IntPredicate;

public class User implements Requestable {
    private boolean is_admin;
    private String email;
    private String name;
    private String password;
    //boolean authenticated;
    //**//
    public User(String email,String name,String password) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.is_admin = false;
    }
    public User(HashMap<String,String> requestable) throws IllegalArgumentException {
        this.fromRequestable(requestable);
    }
    public void fromRequestable(HashMap<String,String> requestable) throws IllegalArgumentException {
        if(!requestable.containsKey("email")||!requestable.containsKey("name")||!requestable.containsKey("password")||!requestable.containsKey("is_admin")){
            throw new IllegalArgumentException("Bad requestable");
        }
        email =requestable.get("email");
        name = requestable.get("name");
        password = requestable.get("password");
        is_admin = requestable.get("is_admin")=="true";
    }
    public HashMap<String,String> asRequestable(){
        HashMap<String,String> requestable = new HashMap<>();
        requestable.put("email",this.email);
        requestable.put("name",this.name);
        requestable.put("password",this.password);
        requestable.put("is_admin",this.is_admin?"true":"false");
        return requestable;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setName(String name){
        this.email = email;
    }
    public String getName(){
        return this.name;
    }
    private static boolean hasWhiteSpace(String msg){
        for (int i = 0; i < msg.length(); i++) {
            if(Character.isWhitespace(msg.charAt(i))){
                return true;
            }
        }
        return  false;
    }
    private static int countCertainChar(String str,char c){
        int count=0;
        for (int i = 0; i < str.length(); i++) {
            if (c == str.charAt(i))
                count++;
        }
        return count;
    }
    public boolean validLoginInfo() {
        return this.validEmail() && this.validPassword();
    }
    public boolean validEmail(){
        if(this.email==null||this.email.isEmpty()||!this.email.contains("@")|| hasWhiteSpace(this.email)){
            return false;
        }
        int count=0;
        for(String msg : this.email.split("@")){
            if (count==0) {
                count++;
                continue;
            }
            if(countCertainChar(msg,'.')<1){
                return false;
            }
           break;
        }
        return true;
    }
    public boolean validPassword(){
        return this.password != null && !this.password.isEmpty() && this.password.length() >= 8 && this.password.chars().allMatch(Character::isLetterOrDigit);
    }
    public boolean isAdmin(){
        return this.is_admin;
    }
    public void setAdmin(boolean is_admin){
        this.is_admin = is_admin;
    }


}
