import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX entry point for StanVard.
 */
public class Main extends Application {
    /**
     * Creates the main StanVard window.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("StanVard");
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(getClass().getResource("/view/stanvard.css").toExternalForm());
        stage.show();
    }
}
