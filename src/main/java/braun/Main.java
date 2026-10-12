package braun;

import java.io.IOException;

import braun.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A graphical user interface application for Braun using JavaFX and FXML.
 *
 * Adapted from the SE-EDU JavaFX tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
 */
public class Main extends Application {

    private final Braun braun = new Braun();

    /**
     * Constructs a new {@code Main} instance.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Braun — Late-Night Broadcast");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/braun.png")));
            stage.setMinWidth(420.0);
            stage.setMinHeight(500.0);
            fxmlLoader.<MainWindow>getController().setBraun(braun);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
