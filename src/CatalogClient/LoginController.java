package CatalogClient;

import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.client.ClientError;
import edu.ucdenver.domain.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
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
        return new User(email.getText(),userName.getText(),password.getText());
    }
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
        try{
            Client client = new Client("127.0.1.1",8080,getUser(),false);
            good = true;
            client.shutdown();
        }
        catch (Exception ee){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid username,email,or password");
            a.show();
            return;
        }
        newUser = false;
        Stage stage =(Stage) loginButton.getScene().getWindow();
        stage.close();
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
        try{
            Client client = new Client("127.0.1.1",8080,getUser(),true);
            good = true;
            client.shutdown();
        }
        catch (Exception ee){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText(ee.getMessage());
            a.show();
            return;
        }
        newUser = true;
        Stage stage =(Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}
