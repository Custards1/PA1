package edu.ucdenver.domain.user;

import edu.ucdenver.domain.request.Requestable;

import java.io.Serializable;
import java.util.HashMap;

//This class represents a user in the store databse, it is sendable in requests because
//it implements Requestable, and is able to be saved to a file because it implements
//serializable.
public class User implements Requestable, Serializable {
    private boolean isAdmin;
    private String email;
    private String name;
    private String password;
    private int orders;
    //creates an empty user
    public User(){
        clear();
    }

    //creates a user with a specified email,name,and password
    public User(String email,String name,String password) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = false;
        orders = 0;
    }
    //creates a user from a requestable hashmap
    public User(HashMap<String,String> requestable) throws IllegalArgumentException {
        fromRequestable(requestable);
    }
    //return the internal order id for a specified id
    public String getOrderId(int id){
        return  getEmail()+Integer.toString(id);
    }
    //return the internal current orderid
    public String getCurrentOrderId(){
        return  getEmail()+Integer.toString(getOrders());
    }
    public void clear(){
        this.email = new String();
        this.password = new String();
        this.name = new String();
        this.isAdmin = false;

        orders = 0;
    }
    //implents fromRequestable protocol
    @Override
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
    //implements asReqquestable protocol
    @Override
    public HashMap<String,String> asRequestable(){
        HashMap<String,String> requestable = new HashMap<>();
        requestable.put("email",this.email);
        requestable.put("name",this.name);
        requestable.put("password",this.password);
        requestable.put("is_admin",this.isAdmin ?"true":"false");
        requestable.put("orders",Integer.toString(this.orders));
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

    //checks if a string has whitespace
    private static boolean hasWhiteSpace(String msg){
        for (int i = 0; i < msg.length(); i++) {
            if(Character.isWhitespace(msg.charAt(i))){
                return true;
            }
        }
        return  false;
    }

    //counts amount of times character appears in string
    private static int countCertainChar(String str,char c){
        int count=0;
        for (int i = 0; i < str.length(); i++) {
            if (c == str.charAt(i))
                count++;
        }
        return count;
    }
    //returns true if login information is valid
    public boolean validLoginInfo() {
        return this.validEmail() && this.validPassword();
    }
    //returns true if email is valid
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
    //returns true if password is valid
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
