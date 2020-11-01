package CatalogClient;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.client.Client;
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
    public Button doSearchButton;
    public ListView searchResultsPane;
    public Button addFromSearchButton;
    public TextField prodPriceSearchDisplay;
    public TextField prodNameSearchDisplay;
    public Tab orderTab;
    public ListView productsInOrder;
    public Button submitOrderButton;
    public Button cancelOrderButton;
    public TextField orderTotalCost;
    public Tab loginTab;
    public TextField usernameFieldLogin;
    public PasswordField passwordFieldLogin;
    public Button loginButton;
    public Button resetButton;
    public ListView prdouctDetails;
    private Client client;
    private boolean called;
    public CatalogClientController(){
        this.client = null;
        called = false;
    }
    public void initializeMe() {
        Stage stage = (Stage)  prdouctDetails.getScene().getWindow();
        browseCategories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateProducts(newValue);
            }
        });
        browseProductsInCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateProductDetails(newValue);
            }
        });
        updateCatagories();
    }
    public void browseTabSel(Event event) {
        updateCatagories();
    }
    private void updateProducts(String catagory){
        ArrayList<String> names= new ArrayList<>();
        try {
            ArrayList<Product> products = client.getProductsInCatagory(catagory);
            for(Product entry : products){
                names.add(entry.getProductName());
            }
            browseProductsInCategory.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to init cuz %s\n",e.getMessage());
        }
    }
    private void updateProductDetails(String name){
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
            System.out.printf("Failed to init cuz %s\n",e.getMessage());
        }
    }
    private void updateCatagories(){
        ArrayList<String> names= new ArrayList<>();
        try {
            ArrayList<Catagory> catagories = client.allCatagories();
            for(Catagory entry : catagories){
                names.add(entry.getName());
            }
            browseCategories.setItems(FXCollections.observableList(names));
        }
        catch (Exception e){
            System.out.printf("Failed to init cuz %s\n",e.getMessage());
        }
    }
    public void addToOrder(ActionEvent actionEvent) {
        System.out.print("Tryina init ");
        System.out.print(client);
        System.out.println();
    }
    public void setClient(Client client){
        this.client = client;
    }
    public void searchTabSel(Event event) {
    }

    public void doSearchAndUpdateList(ActionEvent actionEvent) {
    }

    public void selectProductFromSearch(MouseEvent mouseEvent) {
    }
    public void login(ActionEvent event){


    }
    public void orderTabSel(Event event) {
    }

    public void submitOrder(ActionEvent actionEvent) {
    }

    public void cancelOrder(ActionEvent actionEvent) {
    }

    public void checkAuthLogin(Event event) {
    }

    public void loginUser(ActionEvent actionEvent) {
        Stage stageTheLabelBelongs = (Stage) loginButton.getScene().getWindow();

    }

    public void resetTextFieldsLogin(ActionEvent actionEvent) {
    }
}
