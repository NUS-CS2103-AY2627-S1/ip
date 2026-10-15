package erina.gui;

import java.io.IOException;

import erina.Erina;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application: builds the window and hands it an {@link Erina}.
 */
public class Main extends Application {
    /** The chatbot the window talks to. */
    private final Erina erina = new Erina();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Erina - your meticulous task keeper");
            // Erina's portrait doubles as the window icon, so her window is
            // recognisable in the taskbar or dock.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/erina.png")));
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            fxmlLoader.<MainWindow>getController().setErina(erina);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the application, so failing to load it
            // means the build is broken rather than anything the user did.
            System.err.println("Could not load the main window: " + e.getMessage());
        }
    }
}
