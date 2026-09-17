package della;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Della using FXML.
 */
public class Main extends Application {

    private Della della = new Della("./data/della.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(
                    Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Della");
            stage.setMinHeight(500);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setDella(this.della); // inject the Della instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
