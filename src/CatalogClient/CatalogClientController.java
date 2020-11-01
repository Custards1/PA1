package CatalogClient;

import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.user.User;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    private Client client;
    public CatalogClientController(){

    }
    @FXML
    public void initialize() {
        // Step 1
      //  Stage stage = (Stage) loginButton.getScene().getWindow();
        // Step 2
       // client = (Client) stage.getUserData();
        // Step 3
    }
    public void browseTabSel(Event event) {
    }

    public void addToOrder(ActionEvent actionEvent) {
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
