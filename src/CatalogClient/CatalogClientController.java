package CatalogClient;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.order.Order;
import edu.ucdenver.domain.products.Product;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CatalogClientController {
    public Tab browseTab;
    public ListView browseCategories;
    public ListView browseProductsInCategory;
    public Button addToCartButton;
    public TextField prodPriceDisplay;
    public TextField prodNameDisplay;
    public Tab searchTab;
    public TextField searchProducts;
    public Button searchButton;
    public ListView searchResultsPane;
    public Button addFromSearchButton;
    public TextField prodPriceSearchDisplay;
    public TextField prodNameSearchDisplay;
    public Tab orderTab;
    public ListView productsInOrder;
    public ListView searchProductDetails;
    public Button submitOrderButton;
    public Button cancelOrderButton;
    public TextField orderTotalCost;
    public Tab loginTab;
    public TextField usernameFieldLogin;
    public PasswordField passwordFieldLogin;
    public Button loginButton;
    public Button resetButton;
    public ListView prdouctDetails;
    public TextField viewProdProperties;
    private String selectedProduct;
    private Client client;
    private boolean called;
    public CatalogClientController(){
        this.client = null;
        called = false;
        selectedProduct = new String();
    }
    public void initializeMe() {
        browseCategories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateProducts(newValue);

                prdouctDetails.getItems().clear();
            }
        });

        browseProductsInCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedProduct = newValue;
                updateProductDetails(newValue);
            }
        });
        searchProductDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateProductSearchDetails(newValue);
            }
        });

        updateCatagories();
    }
    public void browseTabSel(Event event) {

        updateCatagories();
    }
    private synchronized void updateProducts(String catagory){
        ArrayList<String> names= new ArrayList<>();
        try {
            ArrayList<Product> products = client.getProductsInCatagory(catagory);
            for(Product entry : products){
                names.add(entry.getProductName());
            }
            browseProductsInCategory.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to update prods cuz %s\n",e.getMessage());
        }
    }
    private synchronized void updateProductDetails(String name){
        System.out.println("Updating details");
        ArrayList<String> names= new ArrayList<>();
        try {
            System.out.println("Updating details");
            Product p = client.getProductByName(name);
            System.out.println(p);
            names = p.asDisplayable();
            System.out.println(names);
            prdouctDetails.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to update prod details cuz %s\n",e.getMessage());
        }
    }
    private synchronized void updateProductSearchDetails(String name){
        System.out.println("Updatings details");
        ArrayList<String> names= new ArrayList<>();
        try {
            System.out.println("Updating details");
            Product p = client.getProductByName(name);
            System.out.println(p);
            names = p.asDisplayable();
            System.out.println(names);
            searchProductDetails.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to update prod search details cuz %s\n",e.getMessage());
        }
    }
    private synchronized  void updateCatagories(){
        ArrayList<String> names= new ArrayList<>();
        try {
            ArrayList<Catagory> catagories = client.allCatagories();
            for(Catagory entry : catagories){
                names.add(entry.getName());
            }
            browseCategories.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to update cata cuz %s\n",e.getMessage());
        }
    }
    public void addToOrder(ActionEvent actionEvent) {
        if(selectedProduct.isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a product to add");
            a.show();
            return;
        }
        try {
            client.addProductToOrder(client.getProductByName(selectedProduct));
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(String.format("Successfully added %s",selectedProduct));
            a.show();
            return;
        }
        catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(e.getMessage());
            a.show();
            return;
        }
    }
    public void setClient(Client client){
        this.client = client;
    }
    public void searchTabSel(Event event) {
    }

    public synchronized void doSearchAndUpdateList(ActionEvent actionEvent) {
        String msg = searchProducts.getText();
        searchProducts.setText("");
        if(msg == null || msg.isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please enter something to search");
            a.show();
            return;
        }
        ArrayList<String> names= new ArrayList<>();
        try {
            ArrayList<Product> products = client.search(msg);
            for(Product entry : products){
                names.add(entry.getProductName());
            }
            searchResultsPane.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to init cuz search %s\n",e.getMessage());
        }

    }

    public void selectProductFromSearch(MouseEvent mouseEvent) {
        //updateProductSearchDetails("IPhone");
       /* searchProductDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                updateProductSearchDetails(newValue);
            }
        });*/
        //System.out.println(searchProductDetails.getSelectionModel().selectionModeProperty());
    }
    public void login(ActionEvent event){


    }
    public void orderTabSel(Event event) {
        updateOrderRaw();
    }
    public void updateOrder(ActionEvent e){
       updateOrderRaw();
    }
    private void updateOrderRaw(){
        try {
            ArrayList<String>names = new ArrayList<>();
            Order products = client.currentOrder();
            names.add(String.format("ID: %s",products.getId()));
            System.out.printf("Current order %s\n",products.getId());
            for(String entry : products.getProducts()){
                if(entry==null||entry.isEmpty()||entry.equals("none")){
                    continue;
                }
                System.out.printf("Current order is %s\n",entry);
                Product p = client.getProduct(entry);
                names.add(String.format("%s by %s",p.getProductName(),p.getBrandName()));
            }
            productsInOrder.setItems(FXCollections.observableList(names));
        }
        catch (Exception ee){
            System.out.printf("Failed to init cuz search %s\n",ee.getMessage());
        }
    }
    public void submitOrder(ActionEvent actionEvent) {
        try{
            client.finalizeOrder();
            updateOrderRaw();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(String.format("Successfully ordered products!"));
            a.show();
        }
        catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(String.format("Failed ordering products!"));
            a.show();
        }
    }

    public void cancelOrder(ActionEvent actionEvent) {
        try{
            client.clearOrder();
            updateOrderRaw();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(String.format("Successfully canceled order!"));
            a.show();
        }
        catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(String.format("Failed canceling order!"));
            a.show();
        }
    }

    public void checkAuthLogin(Event event) {
    }

    public void loginUser(ActionEvent actionEvent) {


    }

    public void resetTextFieldsLogin(ActionEvent actionEvent) {
    }

    public void showProdDetails(ActionEvent actionEvent) {
        String name = this.viewProdProperties.getText();
        ArrayList<String> names= new ArrayList<>();
        try {
            System.out.println("Updating details");
            Product p = client.getProductByName(name);
            System.out.println(p);
            names = p.asDisplayable();
            System.out.println(names);
            searchProductDetails.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to update prod search details cuz %s\n",e.getMessage());
        }
    }
}
