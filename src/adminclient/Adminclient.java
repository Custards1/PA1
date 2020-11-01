package adminclient;

import edu.ucdenver.domain.order.Order;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.application.*;

import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.*;
import edu.ucdenver.domain.client.*;
import edu.ucdenver.domain.order.*;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;

import java.time.LocalDate;
import java.util.ArrayList;

//File Upload Test
import javafx.util.converter.NumberStringConverter;
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
    public Button addCatsToProd;
    public Button removeCatsFromProd;
    public ComboBox<String> prodCatSelBox;
    public Button addProdClick;
    public Button delProdClick;
    public TextField catNameField;
    public TextField brandName;
    public TextField prdDescription;
    public Button addCatClick;
    public Button delCatClick;
    public Button setDefaultCat;
    public ChoiceBox<String> defCategorySel;
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
    public TextField numOfPages;
    public ChoiceBox productType;
    public GridPane gridPane;
    private Label author;
    private Label location;
    private Label imei;
    private Label os;
    private Label pusblication;
    private Label warrantyN;
    private Label numPages;
    private Label serial;
    public TextField locationField;
    public TextField authorField;
    public TextField serialField;
    public TextField imeiField;
    public TextField osField;
    public DatePicker publicationDate;
    public DatePicker warranty;
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
        this.defCategorySel = new ChoiceBox<>();
        locationField = new TextField();
        authorField = new TextField();
        publicationDate = new DatePicker();
        warranty = new DatePicker();
        numOfPages = new TextField();
        imeiField = new TextField();
        osField = new TextField();
        author = new Label();
         location= new Label();
         imei= new Label();
         os= new Label();
         pusblication= new Label();
         warrantyN= new Label();
         numPages = new Label();
         serial = new Label();
        locationField = new TextField();
        authorField = new TextField();
        publicationDate = new DatePicker();
        warranty = new DatePicker();
        numOfPages = new TextField();
        imeiField = new TextField();
        osField = new TextField();
        author = new Label();
        location= new Label();
        imei= new Label();
        os= new Label();
        pusblication= new Label();
        warrantyN= new Label();
        numPages = new Label();
        serialField = new TextField();
        serial = new Label();
        dateSelOrderView = new DatePicker();
    }
    private Client getClient() throws ClientError {
        return new Client("127.0.1.1", 8080, user, false);
    }
    public void initialize(){
        this.prodCatSelBox.setItems(FXCollections.observableArrayList("Book", "Computer", "Electronic", "Home", "Phone"));
        this.defCategorySel.setItems(FXCollections.observableArrayList("Book", "Computer", "Electronic", "Home", "Phone"));
        this.productType.setItems(FXCollections.observableArrayList("Book", "Computer", "Electronic", "Home", "Phone"));
        productType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                for(Node n : gridPane.getChildren()) {
                    Integer i = GridPane.getRowIndex(n);
                    if(i==null || i<0){
                        continue;
                    }
                    if(i > 5){
                        n.setVisible(false);
                    }
                }
            try{
                switch ((String)productType.getItems().get((Integer) number2)){
                    case "Home":
                        for(Node n : gridPane.getChildren()) {
                            Integer i = GridPane.getRowIndex(n);
                            if(i==null || i==0){
                                continue;
                            }
                            if(i == 6){
                                n.setVisible(true);
                            }
                        }

                        location.setText("Location:");
                        author         .setText("");
                        imei          .setText("");
                        os            .setText("");;
                        pusblication  .setText("");
                        warrantyN.setText("");
                        numPages .setText("");
                        serial .setText("");
                        gridPane.add(location,0,6);
                        gridPane.add(locationField,1,6);
                        break;
                    case "Book":
                        for(Node n : gridPane.getChildren()) {
                            Integer i = GridPane.getRowIndex(n);
                            if(i==null || i==0){
                                continue;
                            }
                            if(i == 6||i == 7||i == 8){
                                n.setVisible(true);
                            }
                        }


                        location      .setText("");
                        imei          .setText("");
                        os            .setText("");;

                        warrantyN.setText("");

                        serial .setText("");
                        author.setText("Author:");
                        pusblication.setText("Publication Date:");
                        numPages.setText("Number of pages:");
                        gridPane.add(author,0,6);
                        gridPane.add(authorField,1,6);

                        gridPane.add(pusblication,0,7);
                        gridPane.add(publicationDate,1,7);

                        gridPane.add(numPages,0,8);
                        gridPane.add(numOfPages,1,8);

                        break;
                    case "Electronic":



                        author         .setText("");
                        location      .setText("");
                        imei          .setText("");
                        os            .setText("");;
                        pusblication  .setText("");

                        numPages .setText("");

                        serial.setText("Serial:");
                        warrantyN.setText("Warranty:");
                        gridPane.add(serial,0,6);
                        gridPane.add(authorField,1,6);

                        gridPane.add(warrantyN,0,7);
                        gridPane.add(warranty,1,7);

                        break;
                    case "Computer":
                        break;
                    case "Phone":

                        author         .setText("");
                        location      .setText("");

                        pusblication  .setText("");

                        numPages .setText("");
                        imei.setText("IMEI:");
                        os.setText("OS:");
                        serial.setText("Serial:");
                        warrantyN.setText("Warranty:");
                        gridPane.add(serial,0,6);
                        gridPane.add(serialField,1,6);
                        gridPane.add(warrantyN,0,7);
                        gridPane.add(warranty,1,7);
                        gridPane.add(imei,0,8);
                        gridPane.add(imeiField,1,8);
                        gridPane.add(os,0,9);
                        gridPane.add(osField,1,9);


                    default:break;
                }
            }
            catch (Exception ignored){

            }

            }
        });

    }

    private void cleanCatManage(){
        this.catNameField.setText("");
    }
    private void cleanAddUser(){
        this.usernameField.setText("");
        this.passwordField.setText("");
        this.emailField.setText("");
    }
    private void cleanLogin(){
        this.usernameFieldLogin.setText("");
        this.passwordFieldLogin.setText("");
    }

    public void addUserClick(Event event) { }//tab

    public void prodManClick(Event event) { }//tab

    public void catManClick(Event event) { }//tab

    public void ordReportClick(Event event) { }//tab

    public void saveCloseClick(ActionEvent actionEvent) {
        try {
            client = getClient();
            client.askToShutdown();
            client.shutdown();
        }
        catch (Exception e){
            client.shutdown();
        }
        Platform.exit();
    }

   /* public void toggleAdmin(ActionEvent actionEvent) {
        //change to add admin, add user
    }*/

    public void updatedCategories(ActionEvent actionEvent) {//product tab button
    }

    public void selProdCategory(ActionEvent actionEvent) {//product tab drop down menu
    }

    public void addProduct(ActionEvent actionEvent) {//product tab add button
        try{
            Object o = productType.getValue();
            String s = (String)o;
            client = getClient();
            switch (s){
                case "Home":
                    Home home = new Home(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now(),locationField.getText());
                    home.getCatagories().add(prodCatSelBox.getValue());
                    client.addProductToCatalog(home);

                    break;
                case "Book":
                    Book book = new Book(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now(),authorField.getText(),publicationDate.getValue(),30);
                    book.getCatagories().add(prodCatSelBox.getValue());
                    client.addProductToCatalog(book);
                    break;
                case "Phone":
                    Phone phone = new Phone(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now(),serialField.getText(),warranty.getValue(),imeiField.getText(),osField.getText());
                    phone.getCatagories().add(prodCatSelBox.getValue());
                    client.addProductToCatalog(phone);
                    break;
                case "Computer":
                    Computer c = new Computer(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now(),serialField.getText(),warranty.getValue(),new ArrayList<>());
                    c.getCatagories().add(prodCatSelBox.getValue());
                    client.addProductToCatalog(c);
                    break;
                case "Electronic":
                    Electronic el = new Electronic(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now(),serialField.getText(),warranty.getValue());
                    el.getCatagories().add(prodCatSelBox.getValue());
                    client.addProductToCatalog(el);
                    break;
                default:
                    Product product = new Product(prodnameField.getText(),brandName.getText(),prdDescription.getText(),LocalDate.now());
                    client.addProductToCatalog(product);
                    break;
            }
            client.shutdown();
            prodnameField.setText("");
            brandName.setText("");
            prdDescription.setText("");
            serialField.setText("");
            imeiField.setText("");
            osField.setText("");
            authorField.setText("");
            locationField.setText("");
            //client.addProductToCatalog();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Product added");
            alert.show();
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            client.shutdown();
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void deleteProduct(ActionEvent actionEvent) {//product tab delete button
        try{
           // client.removeProductFromCatalog();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Product deleted");
            alert.show();
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void addCategory(ActionEvent actionEvent) {
        try {
            client = getClient();
            client.addCatagory(this.catNameField.getText());
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory added");
            alert.show();
            this.prodCatSelBox.getItems().addAll(this.catNameField.getText());
        }
        catch (Exception e){
            client.shutdown();
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanCatManage();
    }

    public void deleteCategory(ActionEvent actionEvent) {
        try {
            client = getClient();
            client.removeCatagory(this.catNameField.getText());
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory deleted");
            alert.show();
            this.prodCatSelBox.getItems().remove(this.catNameField.getText());
        }
        catch (Exception e){
            client.shutdown();
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanCatManage();
    }

    public void updateDefCat(ActionEvent actionEvent) {//catagory managment confirm button
        try {
            client = getClient();
            client.setDefaultCatagory(this.defCategorySel.getValue());
            client.shutdown();
        }
        catch (Exception e){
            client.shutdown();
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void selUserReportView(MouseEvent mouseEvent) {//viewer on final order reports
        try {
            for (User user : client.allUsers()) {
                ArrayList<Order> orders = client.clientsOrders(user);
            }
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void dateUpdOrderView(ActionEvent actionEvent) {//view by date on final order reports
        try {
            for (User user : client.allUsers()) {
                ArrayList<Order> orders = client.clientsOrders(user);
            }
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void ordReportClck(Event event) {//tab
    }

    public void checkAuthLogin(Event event) { }//tab

    public void loginUser(ActionEvent actionEvent) {
        //call in constructor
        if(this.emailField.getText().isEmpty() ||this.usernameFieldLogin.getText().isEmpty()||this.passwordFieldLogin.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill out all fields.");
            alert.show();
            cleanLogin();
            return;
        }
        User user = new User(this.emailField.getText(),this.usernameFieldLogin.getText(),this.passwordFieldLogin.getText());
        try{
            client = getClient();
            client.addAnotherUser(user);
            client.shutdown();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add user");
            alert.show();
            cleanLogin();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Success");
        alert.show();
        cleanLogin();
    }

    public void resetTextFieldsLogin(ActionEvent actionEvent) {
        cleanLogin();
    }

    public void addAdmin(ActionEvent actionEvent) {
        try {
            client = getClient();
            client.createAdmin(this.emailField.getText(), this.usernameField.getText(), this.passwordField.getText());
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Admin created");
            alert.show();
        }
        catch (Exception e) {
            client.shutdown();
            System.out.printf("Execptions %s", e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanAddUser();
    }

    public void addUser(ActionEvent actionEvent) {
        try {
            User temp = new User(this.emailField.getText(), this.usernameField.getText(), this.passwordField.getText());
            client = getClient();
            client.addAnotherUser(temp);
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "User created");
            alert.show();
        }
        catch (Exception e) {
            client.shutdown();
            System.out.printf("Execptions %s", e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
        cleanAddUser();
    }

    public void addCatToProd(ActionEvent actionEvent) {
        try{
            client = getClient();
            client.addCatagoryToProductByName(this.prodCatSelBox.getValue(), this.prodnameField.getText());
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory added to product");
            alert.show();
        }
        catch (Exception e) {
            client.shutdown();
            System.out.printf("Execptions %s", e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }

    public void removeCatFromProd(ActionEvent actionEvent) {
        try{
            client = getClient();
            client.removeCatagoryFromProductByName(this.prodCatSelBox.getValue(), this.prodnameField.getText());
            client.shutdown();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Catagory removed from product");
            alert.show();
        }
        catch (Exception e) {
            client.shutdown();
            System.out.printf("Execptions %s", e.getMessage());
            Alert eAlert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            eAlert.show();
        }
    }
}
