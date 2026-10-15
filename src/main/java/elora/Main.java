package elora;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX entry point for Elora's GUI. Loads the main window from FXML
 * and connects it to a backing {@link Elora} instance.
 */
public class Main extends Application {

    private Elora elora = new Elora();

    /**
     * Builds the scene from MainWindow.fxml and shows it.
     *
     * @param stage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Elora");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/elora.png")));
            stage.setMinWidth(380);
            stage.setMinHeight(500);
            stage.setWidth(460);
            stage.setHeight(640);
            fxmlLoader.<MainWindow>getController().setElora(elora);
            stage.show();
        } catch (IOException e) {
            throw new AssertionError("MainWindow.fxml should always be on the classpath", e);
        }
    }

    /**
     * Launches the JavaFX GUI.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
