package jaylen;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays Jaylen in a JavaFX window defined by FXML. */
public class Main extends Application {
    private final Jaylen jaylen = new Jaylen();

    /** Creates the JavaFX application. */
    public Main() {
    }

    /**
     * Creates and displays the primary Jaylen window.
     *
     * @param stage the primary window supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setJaylen(jaylen);

            stage.setTitle("Jaylen");
            stage.setMinWidth(420);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load the Jaylen window.", exception);
        }
    }
}
