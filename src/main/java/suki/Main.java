package suki;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application behind Suki's GUI.
 *
 * <p>This class only builds the window: it loads the layout from
 * {@code MainWindow.fxml} and hands the controller a {@link Suki} to talk to.
 * Keeping the layout in FXML rather than in Java means the look of the window
 * can be changed without touching this code.
 */
public class Main extends Application {
    /** The chatbot the window sends its input to. */
    private final Suki suki = new Suki();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Suki");
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            fxmlLoader.<MainWindow>getController().setSuki(suki);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
