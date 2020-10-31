package CatalogClient;

import edu.ucdenver.domain.client.Client;
import edu.ucdenver.domain.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;

public class CatalogClientMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage temp = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        FXMLLoader fxmlLoader2 = new FXMLLoader();
        Pane login = fxmlLoader.load(getClass().getResource("login.fxml"));
        temp.setTitle("Login");
        temp.setScene(new Scene(login));
        temp.showAndWait();
        LoginController l = fxmlLoader.getController();
        //FAILING ON THIS
        User client = l.getUser();
        try {

            if(l.isGood()){

                Parent root = fxmlLoader2.load(getClass().getResource("UserClient.fxml"));
                CatalogClientController controller = (CatalogClientController)fxmlLoader2.getController();
                controller.setClient(new Client("127.0.1.1",8080,client,false));
                primaryStage.setTitle("Catalog");
                primaryStage.setScene(new Scene(root, 1920, 1080));
                primaryStage.show();
            }

        }
        catch (Exception ee){

        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
