package bucket;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A GUI for Bucket using FXML.
 *
 * This class only builds the window. What the layout looks like is described in
 * MainWindow.fxml, and what the controls do is handled by MainWindow, so visual
 * tweaks do not mean touching Java code.
 */
public class Main extends Application {

    private final Bucket bucket = new Bucket();

    /**
     * Loads the main window and shows it.
     *
     * @param stage The window supplied by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // Colours, fonts and spacing all live in one stylesheet, so the look can be
            // changed without recompiling and the FXML stays about layout alone.
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Bucket");

            // Small enough to tuck into a corner of the screen, since the window is
            // meant to sit alongside whatever the user is actually working on. The
            // layout stretches from here, so anything larger works too.
            stage.setMinHeight(320.0);
            stage.setMinWidth(300.0);

            // The controller is only created while the FXML loads, so the chatbot
            // can only be handed over after that.
            fxmlLoader.<MainWindow>getController().setBucket(bucket);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
