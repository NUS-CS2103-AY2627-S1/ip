package alice;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Configures and shows Alice's JavaFX window.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Alice";
    private final Alice alice = new Alice();

    /**
     * Starts the JavaFX runtime.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates and displays the main chat window.
     *
     * @param stage The primary window supplied by JavaFX.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<alice.ui.MainWindow>getController().setAlice(alice);
        Scene scene = new Scene(root);

        scene.getStylesheets().add(Main.class.getResource("/view/styles.css").toExternalForm());
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(420);
        stage.setMinHeight(500);

        stage.show();
    }
}
