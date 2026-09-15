package tem;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for Tem.
 */
public class Main extends Application {
    private final Tem tem = new Tem();

    /**
     * Starts the primary Tem window.
     *
     * @param stage application window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            scene.getStylesheets().add(Main.class.getResource("/view/tem.css").toExternalForm());
            stage.setTitle("Tem");
            stage.setMinHeight(320.0);
            stage.setMinWidth(480.0);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setTem(tem);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load /view/MainWindow.fxml", exception);
        }
    }
}
