package vector;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Vector using FXML.
 */
public class Main extends Application {

    private Vector vector = new Vector("./data/vector.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);

            // Inject Premium Light Mode CSS
            scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);

            // Add constraints, title and icon
            stage.setTitle("Vector");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon.jpg")));
            stage.setMinHeight(220);
            stage.setMinWidth(417);

            fxmlLoader.<MainWindow>getController().setVector(vector);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
