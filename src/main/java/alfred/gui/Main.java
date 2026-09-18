package alfred.gui;

import java.io.IOException;

import alfred.Alfred;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX application that hosts Alfred's chat window.
 */
public class Main extends Application {
    private static final int MIN_WINDOW_HEIGHT = 220;
    private static final int MIN_WINDOW_WIDTH = 417;

    private final Alfred alfred = new Alfred("data/alfred.txt", false);

    /**
     * Loads the main window and shows Alfred's GUI.
     *
     * @param stage Primary window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Alfred");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/alfred.png")));
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setAlfred(alfred);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load the Alfred window.", exception);
        }
    }
}
