package goat.gui;

import java.io.IOException;

import goat.Goat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application: builds the window and hands it a chatbot to talk to.
 *
 * Nothing about the chatbot lives here. This class loads the layout, creates
 * one {@link Goat}, gives it to the controller, and shows the window.
 */
public class Main extends Application {

    private final Goat goat = Goat.forGui(Goat.DEFAULT_SAVE_PATH);

    /**
     * {@inheritDoc}
     *
     * <p>Loads the window from FXML, hands the chatbot to its controller, and
     * shows it.
     *
     * @param stage the window JavaFX has created for us
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Goat");
            // Shown in the dock, the taskbar and the window switcher, which is
            // where a small app is actually recognized from.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/goat-icon.png")));
            // Small enough to tuck beside another window, since that is how a
            // task list is actually used, but not so small that a task listing
            // wraps every line.
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);
            fxmlLoader.<MainWindow>getController().setGoat(goat);
            stage.show();
        } catch (IOException e) {
            // Only reachable if the FXML is missing from the JAR, which is a
            // packaging fault rather than something a user can act on.
            System.err.println("Could not load the window layout: " + e.getMessage());
        }
    }
}
