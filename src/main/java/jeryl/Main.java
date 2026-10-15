package jeryl;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point: loads the main window from FXML, creates the
 * backing Jeryl instance, and shows the stage.
 */
public class Main extends Application {
    private final Jeryl jeryl = new Jeryl();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            stage.setTitle("Jeryl");
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            stage.setScene(scene);

            fxmlLoader.<MainWindow>getController().setJeryl(jeryl);

            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load /view/MainWindow.fxml", e);
        }
    }
}
