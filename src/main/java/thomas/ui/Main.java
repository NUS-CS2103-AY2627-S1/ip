package thomas.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import thomas.Thomas;

/**
 * The JavaFX application behind Thomas's window.
 * <p>
 * Loads the layout from {@code MainWindow.fxml} and hands the controller a
 * {@link Thomas} to talk to, so the window and the chatbot meet in exactly one
 * place.
 */
public class Main extends Application {

    /** The chatbot behind the window, shared by every line typed into it. */
    private final Thomas thomas = new Thomas();

    /**
     * Builds and shows the window.
     *
     * @param stage The window JavaFX hands the application to fill.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle("Thomas the Tank Engine");
            // Small enough for a corner of the screen, but never so narrow
            // that a task line has to wrap every few words.
            stage.setMinHeight(300);
            stage.setMinWidth(360);

            // The controller is built by the FXML loader, so the chatbot can only
            // be handed to it here, once the layout has been loaded.
            fxmlLoader.<MainWindow>getController().setThomas(thomas);
            stage.show();
        } catch (IOException e) {
            // The layout is packaged with the program, so a failure here is a
            // broken build rather than anything the user can act on.
            e.printStackTrace();
        }
    }
}
