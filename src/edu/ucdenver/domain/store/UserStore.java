
package edu.ucdenver.domain.store;

import edu.ucdenver.domain.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserStore {
    ArrayList<User> users;
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
    private void addPotentiallyPrivilagedUser(User user,boolean is_admin) {
        user.setAdmin(is_admin);
        addUserRaw(user);
    }
    private boolean validUserConstruct(User user){
        return user != null && user.validEmail() && user.validPassword();
    }
    private synchronized boolean containsUserWithEmail(String email) {
        return userNameMap.containsKey(email);
    }
    private boolean validateUser(User user) {
        return !validUserConstruct(user) || !containsUserWithEmail(user.getEmail());
    }
    private boolean validateAdminUser(User user) {
        if(validateUser(user)){
            return false;
        }
        User admin = this.users.get(userNameMap.get(user.getEmail()));
        return admin.isAdmin();
    }
    public boolean addUser(User user) {
        if(validateUser(user)){
            return false;
        }
        addPotentiallyPrivilagedUser(user,false);
        return true;
    }
    public boolean addAdminUser(User admin,User user) {
        if(validateUser(user) ||!validateAdminUser(admin)){
            return false;
        }
        addPotentiallyPrivilagedUser(user,true);
        return true;
    }

}
