package adminclient;

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
    public ComboBox prodCatSelBox;
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
    }

    private void cleanCatManage(){
        this.catNameField.setText(" ");
    }
    private void cleanAddUser(){
        this.usernameField.setText(" ");
        this.passwordField.setText(" ");
    }

    public void addUserClick(Event event) {
    }

    public void prodManClick(Event event) {
    }

    public void catManClick(Event event) {
    }

    public void ordReportClick(Event event) {
    }

    public void saveCloseClick(ActionEvent actionEvent) {
        client.shutdown();//close
    }

    public void toggleAdmin(ActionEvent actionEvent) {
        //change to add admin, add user
    }

    public void updatedCategories(ActionEvent actionEvent) {
    }

    public void selProdCategory(ActionEvent actionEvent) {
    }

    public void addProduct(ActionEvent actionEvent) {
    }

    public void deleteProduct(ActionEvent actionEvent) {
    }

    public void addCategory(ActionEvent actionEvent) {
        try {
            client.addCatagory(this.catNameField.getText());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory added");
            alert.show();
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

    public void updateDefCat(ActionEvent actionEvent) {
    }

    public void selUserReportView(MouseEvent mouseEvent) {
    }

    public void dateUpdOrderView(ActionEvent actionEvent) {
    }

    public void ordReportClck(Event event) {
    }

    public void checkAuthLogin(Event event) {
    }

    public void loginUser(ActionEvent actionEvent) {
        //call in constructor
        this.user = new User("admin@admin.org",this.usernameFieldLogin.getText(),this.passwordFieldLogin.getText());
    }

    public void resetTextFieldsLogin(ActionEvent actionEvent) {
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
