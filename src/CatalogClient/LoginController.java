package CatalogClient;

import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField email;
    @FXML
    TextField userName;
    @FXML
    PasswordField password;
    @FXML
    Button loginButton;
    @FXML
    Button createUserButton;
    boolean newUser;
    boolean good;
    public LoginController(){
        good = false;
        newUser = false;
    }
    public User getUser(){
        return new User(email.getText().isEmpty()? " " : email.getText(),userName.getText().isEmpty()? " ": userName.getText(),password.getText()
        .isEmpty()?" ":password.getText());
    }
    TextField getEmail() {return email;}
    TextField getUserName(){return userName;}
    PasswordField getPassword() {return password;}
    public boolean isNewUser(){
        return newUser;
    }
    public boolean allFieldsFull() {
        return  !email.getText().isEmpty() && !password.getText().isEmpty() && !userName.getText().isEmpty();
    }
    public void login(ActionEvent e){
        good = false;
        if(!allFieldsFull()){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please fill out all fields");
            a.show();
            return;
        }
        Client client = null;
        try{
            client = new Client("127.0.1.1",8080,getUser(),false);
            good = true;
        }
        catch (Exception ee){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid username,email,or password");
            a.show();
            return;
        }
        newUser = false;
        ;
        Stage stage =(Stage) loginButton.getScene().getWindow();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserClient.fxml"));
            Parent root = (Parent) loader.load();
            CatalogClientController load = (CatalogClientController)loader.getController();
            load.setClient(client);
            load.initializeMe();
            stage.setScene(new Scene(root));
        }
        catch (Exception ee){
            stage.close();
        }

    }
    public boolean isGood(){return good;}
    public Client getClient() throws ClientError {return new Client("127.0.1.1",8080,getUser(),isNewUser());}

    public void createUser(ActionEvent e) {
        good = false;
        if(!allFieldsFull()){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please fill out all fields");
            a.show();
            return;
        }
        Client client = null;
        try{
            client = new Client("127.0.1.1",8080,getUser(),true);
            good = true;
        }
        catch (Exception ee){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText(ee.getMessage());
            a.show();
            return;
        }
        newUser = true;
        Stage stage =(Stage) loginButton.getScene().getWindow();
        Scene scene = (Scene)loginButton.getScene();

        //stage.setUserData(client);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserClient.fxml"));
            Parent root = (Parent) loader.load();
            CatalogClientController load = (CatalogClientController)loader.getController();
            load.setClient(client);
            load.initializeMe();
            stage.setScene(new Scene(root));
        }
        catch (Exception ee){
            System.out.println("Failed");
            stage.close();
        }
    }
}
