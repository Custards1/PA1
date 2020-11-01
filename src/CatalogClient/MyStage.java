package CatalogClient;

import edu.ucdenver.domain.user.User;
import javafx.stage.Stage;


public class MyStage extends Stage {
    public User showAndReturn(LoginController controll) {
        super.showAndWait();
        return controll.getUser();
    }
}