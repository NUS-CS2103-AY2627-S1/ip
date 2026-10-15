package clearblue.gui;

import java.io.IOException;

import clearblue.Clearblue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point for the Clearblue GUI, built with JavaFX and FXML.
 */
public class Main extends Application {
    private static final String SAVE_FILE_PATH = "data/clearblue.txt";
    private static final double MIN_WINDOW_WIDTH = 417;
    private static final double MIN_WINDOW_HEIGHT = 220;

    private final Clearblue clearblue = new Clearblue(SAVE_FILE_PATH);

    /**
     * Loads the main window from FXML, wires it to a {@link Clearblue}
     * instance, and shows the stage.
     *
     * @param stage primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Clearblue");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Bot.png")));
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setClearblue(clearblue);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
