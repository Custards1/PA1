
package edu.ucdenver.domain.store;

import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.order.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserStore  implements Serializable  {
    ArrayList<User> users;
    ArrayList<Order> Order;
    HashMap<String,Integer> userNameMap;
    public UserStore() {
        this.users = new ArrayList<>();
        this.userNameMap = new HashMap<>();
        User mainAdmin = new User("admin@admin.org","admin","admin3234");
        mainAdmin.setAdmin(true);
        addUserRaw(mainAdmin);
    }
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
    private synchronized void addUserRaw(User user) {
        this.users.add(user);
        userNameMap.put(user.getEmail(),this.users.size()-1);
    }
    private synchronized void addPotentiallyPrivilagedUser(User user,boolean is_admin) {
        user.setAdmin(is_admin);
        addUserRaw(user);
    }
    private synchronized boolean validUserConstruct(User user){
        return user != null && user.validEmail() && user.validPassword();
    }
    private synchronized boolean containsUserWithEmail(String email) {
        return userNameMap.containsKey(email);
    }
    public synchronized boolean validateUser(User user) {
        return !validUserConstruct(user) || !containsUserWithEmail(user.getEmail());
    }
    public synchronized boolean validAdminAuthentication(User user) {
        if(validateUser(user)){
            return false;
        }
        User admin = this.users.get(userNameMap.get(user.getEmail()));
        return admin.isAdmin();
    }
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
    public synchronized boolean addUser(User user) {
        if(validateUser(user)){
            return false;
        }
        addPotentiallyPrivilagedUser(user,false);
        return true;
    }
    public synchronized boolean addAdminUser(User admin,User user) {
      if(!validateUser(user) ||!validAdminAuthentication(admin)){
          return false;
      }
        addPotentiallyPrivilagedUser(user,true);
        return true;
    }

}
