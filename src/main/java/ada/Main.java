package ada;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts and displays the JavaFX GUI for Ada.
 */
public class Main extends Application {

    private final Ada ada = new Ada();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource(
                            "/view/MainWindow.fxml"));

            AnchorPane mainWindowLayout = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setAda(ada);

            Scene scene = new Scene(mainWindowLayout);
            stage.setScene(scene);
            stage.setTitle("Ada");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
