package nova.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nova.Nova;

/**
 * The JavaFX application: builds the window from FXML and hands it a chatbot
 * to talk to.
 */
public class Main extends Application {
    private final Nova nova = new Nova();

    /** Creates the application. JavaFX requires a public no-argument constructor. */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/view/nova.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Nova");
            // Resizable, with a floor low enough to sit beside other windows
            // but high enough that a reply is not reduced to one word a line.
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);
            loader.<MainWindow>getController().setNova(nova);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the app, so a failure here means the
            // build is broken rather than anything the user can act on.
            throw new IllegalStateException("Could not load the main window", e);
        }
    }
}
