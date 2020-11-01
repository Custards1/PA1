package CatalogClient;

import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.user.User;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;


public class CatalogClientMain extends Application {
    private Parent root;
    private Stage window;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        root  = fxmlLoader.load(getClass().getResource("login.fxml"));;
        primaryStage.setTitle("Catalog");
        primaryStage.setScene(new Scene(root, 500, 500));
        window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
