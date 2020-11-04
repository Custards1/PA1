
package edu.ucdenver.domain.store;

import edu.ucdenver.domain.request.Requestable;
import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.order.Order;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//This class is used to store a collection of users and their orders,
//This class enforces admin access to retrive certail information.
//It is also able to be saved to a file because it implements Serializable.
public class UserStore  implements Serializable  {
    ArrayList<User> users;
    ArrayList<Order> orders;
    HashMap<String,Integer> userNameMap;
    HashMap<String,Integer> orderNameMap;
    HashMap<String,Integer> finalizedOrderNameMap;

    public UserStore() {
        this.users = new ArrayList<>();
        this.userNameMap = new HashMap<>();
        orderNameMap = new HashMap<>();
        orders = new ArrayList<>();
        finalizedOrderNameMap = new HashMap<>();
        User mainAdmin = new User("admin@admin.org","admin","admin3234");
        mainAdmin.setAdmin(true);
        addUserRaw(mainAdmin);
    }
    //retrives a user with a specific email and password
    public synchronized User getUser(String email,String password) throws IllegalArgumentException {
        if(!userNameMap.containsKey(email)){
            throw new IllegalArgumentException();
        }
        try {
            User user=users.get(userNameMap.get(email));
            if(user ==null) {
                throw new IllegalArgumentException();
            }
            if(!user.getPassword().equals(password)){
                throw new IllegalArgumentException();
            }
            return user;
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }
    }
    //retrives a user with a specific email
    private synchronized User getUser(String email) throws IllegalArgumentException {
        if(!userNameMap.containsKey(email)){
            throw new IllegalArgumentException();
        }
        try {
            User user=users.get(userNameMap.get(email));
            if(user ==null) {
                throw new IllegalArgumentException();
            }

            return user;
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }
    }
    //adds a user to the store
    private synchronized void addUserRaw(User user) {
        this.users.add(user);
        userNameMap.put(user.getEmail(),this.users.size()-1);
    }
    //adds a user as admin to the store
    private synchronized void addPotentiallyPrivilagedUser(User user,boolean is_admin) {
        user.setAdmin(is_admin);
        addUserRaw(user);
    }
    //returns true if user contains a valid email,password,and is not null
    private synchronized boolean validUserConstruct(User user){
        return user != null && user.validEmail() && user.validPassword();
    }
    //returns true if store contains a specific user with specified email.
    private synchronized boolean containsUserWithEmail(String email) {
        return userNameMap.containsKey(email);
    }
    public synchronized boolean validateUser(User user) {
        return !validUserConstruct(user) || !containsUserWithEmail(user.getEmail());
    }
    //returns true if user is actually verified as an admin
    public synchronized boolean validAdminAuthentication(User user) {
        if(validateUser(user)){
            return false;
        }
        User admin = this.users.get(userNameMap.get(user.getEmail()));
        return admin.isAdmin();
    }
    //retrives all users
    public synchronized ArrayList<User>  allUsers(User admin) throws IllegalArgumentException {
        if(!validAdminAuthentication(admin)){
            throw new IllegalArgumentException();
        }
        ArrayList<User> users = new ArrayList<>();
        for(Map.Entry<String,Integer> name : userNameMap.entrySet()){
            if(!name.getKey().isEmpty()) {
                users.add(this.users.get(name.getValue()));
            }
        }
        return users;
    }
    //adds a non admin user, returns false if failed.
    public synchronized boolean addUser(User user) {
        if(!validUserConstruct(user)){
         
            return false;
        }
        addPotentiallyPrivilagedUser(user,false);
        return true;
    }
    // adds a user as an admin,returns false if failed.
    public synchronized boolean addAdminUser(User admin,User user) {
      if(!validateUser(user) ||!validAdminAuthentication(admin)){
          return false;
      }
        addPotentiallyPrivilagedUser(user,true);
        return true;
    }
    //adds an Order to the databse, returns pointer to new order
    private synchronized Order addOrderRaw(Order order) {
        this.orders.add(order);
        orderNameMap.put(order.getId(),orders.size()-1);
        return this.orders.get(orders.size()-1);
    }
    //returns order from an order id, throws if id is not found in store
    public synchronized Order getOrder(String name) throws IllegalArgumentException {
        Integer i = orderNameMap.get(name);
        if(i == null){
            throw new IllegalArgumentException();
        }
        try{
            Order order = orders.get(i);
            if(order == null){
                throw new IllegalArgumentException();
            }
            return order;
        }
        catch (Exception ignored){
            throw new IllegalArgumentException();
        }
    }
    //returns order from an order id and finalizes it, throws if id is not found in store
    public synchronized Order getFinalOrder(String name) throws IllegalArgumentException {
        Integer i = orderNameMap.get(name);
        if(i == null){
            throw new IllegalArgumentException();
        }
        try{
            Order order = orders.get(i);
            if(order == null){
                throw new IllegalArgumentException();
            }
            order.setFinalization(LocalDate.now());
            order.setFinalized(true);
            finalizedOrderNameMap.put(order.getId(), i);
            return order;
        }
        catch (Exception ignored){
            throw new IllegalArgumentException();
        }
    }
    //returns the current order for a connected user, throws if not found or user is invalid
    public synchronized Order getCurrentOrder(User connectedUser) throws IllegalArgumentException {
        if(!connectedUser.validLoginInfo()){
           
            throw new IllegalArgumentException();
        }

        String orderName = connectedUser.getCurrentOrderId();

        Integer i = orderNameMap.get(orderName);
        if(i == null){

                return addOrderRaw(new Order(orderName));
        }

        return getOrder(orderName);
    }
    //returns the users current order and finalizes it, throws if id is not found in store
    public synchronized Order getCurrentFinalOrder(User connectedUser) throws IllegalArgumentException {
        if(!connectedUser.validLoginInfo()){

            throw new IllegalArgumentException();
        }

        String orderName = connectedUser.getCurrentOrderId();

        Integer i = orderNameMap.get(orderName);
        if(i == null){
            Order id =addOrderRaw(new Order(orderName));
            finalizedOrderNameMap.put(id.getId(), orders.size()-1);
            return id;
        }
        return getFinalOrder(orderName);
    }
    public synchronized Order finalizeOrder(User connectedUser) throws IllegalArgumentException {
        if(!connectedUser.validLoginInfo()){
            throw new IllegalArgumentException();
        }

        Order temp =  getCurrentFinalOrder(connectedUser);
        User ptr = getUser(connectedUser.getEmail(),connectedUser.getPassword());
        ptr.setOrders(ptr.getOrders()+1);
        return temp;
    }
    //retrievs all orders for a specific user
    public synchronized ArrayList<Order> getUsersOrders(User connectedUser,User user) throws IllegalArgumentException {
        if(!connectedUser.validLoginInfo()){
            throw new IllegalArgumentException();
        }
        ArrayList<Order> toRet = new ArrayList<>();
        for(int i =0;i < user.getOrders();i++){
            toRet.add(getOrder(user.getOrderId(i)));
        }
        return toRet;
    }
    //retrievs all orders for a user with specified email
    public synchronized ArrayList<Order> getUsersOrders(User connectedUser,String email) throws IllegalArgumentException {
        if(!connectedUser.validLoginInfo()){
            throw new IllegalArgumentException();
        }
        ArrayList<Order> toRet = new ArrayList<>();
        for(int i =0;i <getUser(email).getOrders();i++){
            toRet.add(getOrder(getUser(email).getOrderId(i)));
        }
        return toRet;
    }
    //retrievs all finalized orders for a specifed user
    public synchronized ArrayList<Order> getFinalizedOrders(User connectedUser) throws IllegalArgumentException {
        if(!validAdminAuthentication(connectedUser)){
            throw new IllegalArgumentException();
        }
        ArrayList<Order> toRet = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : finalizedOrderNameMap.entrySet()){
            toRet.add(orders.get(entry.getValue()));
        }
        return toRet;
    }
}
