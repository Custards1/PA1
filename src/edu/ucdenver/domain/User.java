package edu.ucdenver.domain;

import edu.ucdenver.domain.category.CatagoryParser;
import edu.ucdenver.domain.order.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Requestable {
    private boolean isAdmin;
    private String email;
    private String name;
    private String password;
    private int orders;
    //boolean authenticated;
    //**//
    public User(){
        clear();
    }
    public User(String email,String name,String password) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = false;
        orders = 0;
    }
    public User(HashMap<String,String> requestable) throws IllegalArgumentException {
        fromRequestable(requestable);
    }
    public void clear(){
        this.email = new String();
        this.password = new String();
        this.name = new String();
        this.isAdmin = false;
        orders = 0;
    }
    public void fromRequestable(HashMap<String,String> requestable) throws IllegalArgumentException {
        if(!requestable.containsKey("email")||!requestable.containsKey("name")||!requestable.containsKey("password")||!requestable.containsKey("is_admin")){
            throw new IllegalArgumentException("Bad requestable");
        }
        clear();
        this.email=requestable.get("email");
        this.name=requestable.get("name");
        this.password=requestable.get("password");
        this.isAdmin=requestable.get("is_admin")=="true";
        String temp = requestable.get("orders");
        if(temp == null || temp.isEmpty()){
            throw new IllegalArgumentException();
        }
        try{
            orders = Integer.parseInt(temp);
        }
        catch (Exception ignored){

        }
    }
    public HashMap<String,String> asRequestable(){
        HashMap<String,String> requestable = new HashMap<>();
        requestable.put("email",this.email);
        requestable.put("name",this.name);
        requestable.put("password",this.password);
        requestable.put("is_admin",this.isAdmin ?"true":"false");
        requestable.put("orders",Integer.toString(this.orders));
        return requestable;
    }
    public static User fromObj(HashMap<String,String> obj) throws IllegalArgumentException {
        User user = new User();
        user.fromRequestable(obj);
        return user;
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
        return this.isAdmin;
    }
    public void setAdmin(boolean is_admin){
        this.isAdmin = is_admin;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }
}
