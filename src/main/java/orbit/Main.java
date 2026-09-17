package orbit;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Loads and displays Orbit's JavaFX user interface.
 */
public class Main extends Application {
    private final Orbit orbit = new Orbit();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setOrbit(orbit);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Orbit");
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.show();
    }
}
