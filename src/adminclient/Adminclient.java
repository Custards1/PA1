package adminclient;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.*;
import edu.ucdenver.domain.client.*;

import java.time.LocalDate;
import java.util.ArrayList;

//File Upload Test

public class Adminclient {


    public Tab addUser;
    public Tab prodManagement;
    public Tab catManagement;
    public Tab ordReport;
    public Button saveAndClose;
    public TextField usernameField;
    public PasswordField passwordField;
    public Button addAdminClick;
    public Button addUserClick;
    //public ToggleGroup adminFlag;
    public TextField prodnameField;
    public Button updateCats;
    public ComboBox<String> prodCatSelBox;
    public Button addProdClick;
    public Button delProdClick;
    public TextField catNameField;
    public Button addCatClick;
    public Button delCatClick;
    public Button setDefaultCat;
    public ChoiceBox defCategorySel;
    public ListView userOrderViews;
    public ListView userSelectOrderViews;
    public DatePicker dateSelOrderView;
    public ListView dateOrderView;
    public Tab loginTab;
    public TextField usernameFieldLogin;
    public PasswordField passwordFieldLogin;
    public Button loginButton;
    public Button resetButton;
    public TextField emailField;

    private Client client = null;
    private User user;

    public Adminclient(){
        this.user = new User("admin@admin.org","admin","admin3234");
        try {
            client = new Client("127.0.1.1", 8080, user, false);
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
        }
        this.prodCatSelBox = new ComboBox<>();
    }

    public void initialize(){
        this.prodCatSelBox.setItems(FXCollections.observableArrayList("Book", "Computer", "Electronic", "Home", "Phone"));
    }

    private void cleanCatManage(){
        this.catNameField.setText(" ");
    }
    private void cleanAddUser(){
        this.usernameField.setText(" ");
        this.passwordField.setText(" ");
        this.emailField.setText(" ");
    }
    private void cleanLogin(){
        this.usernameFieldLogin.setText(" ");
        this.passwordFieldLogin.setText(" ");
    }

    public void addUserClick(Event event) { }//tab

    public void prodManClick(Event event) {//tab
    }

    public void catManClick(Event event) {//tab
    }

    /*public void ordReportClick(Event event) {//tab
    }

    public void saveCloseClick(ActionEvent actionEvent) {
        client.shutdown();//close
    }*/

   /* public void toggleAdmin(ActionEvent actionEvent) {
        //change to add admin, add user
    }*/

    public void updatedCategories(ActionEvent actionEvent) {//product tab button
    }

    public void selProdCategory(ActionEvent actionEvent) {//product tab drop down menu
    }

    public void addProduct(ActionEvent actionEvent) {//product tab add button
    }

    public void deleteProduct(ActionEvent actionEvent) {//product tab delete button
    }

    public void addCategory(ActionEvent actionEvent) {
        try {
            client.addCatagory(this.catNameField.getText());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory added");
            alert.show();
            this.prodCatSelBox.getItems().addAll(this.catNameField.getText());
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanCatManage();
    }

    public void deleteCategory(ActionEvent actionEvent) {
        try {
            client.removeCatagory(this.catNameField.getText());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory deleted");
            alert.show();
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanCatManage();
    }

    public void updateDefCat(ActionEvent actionEvent) {//catagory managment confirm button
    }

    public void selUserReportView(MouseEvent mouseEvent) {//viewer on final order reports
    }

    public void dateUpdOrderView(ActionEvent actionEvent) {//view by date on final order reports
    }

    public void ordReportClck(Event event) {//tab
    }

    public void checkAuthLogin(Event event) { }//tab

    public void loginUser(ActionEvent actionEvent) {
        //call in constructor
        this.user = new User("admin@admin.org",this.usernameFieldLogin.getText(),this.passwordFieldLogin.getText());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Loged in");
        alert.show();
        cleanLogin();
    }

    public void resetTextFieldsLogin(ActionEvent actionEvent) {
        cleanLogin();
    }

    public void addAdmin(ActionEvent actionEvent) {
        try {
            client.createAdmin(this.emailField.getText(), this.usernameField.getText(), this.passwordField.getText());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Admin created");
            alert.show();
        }
        catch (Exception e) {
            System.out.printf("Execptions %s", e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanAddUser();
    }

    public void addUser(ActionEvent actionEvent) {
        cleanAddUser();
    }
}
