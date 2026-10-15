package peter;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Peter's JavaFX graphical interface.
 */
public class Main extends Application {
    private static final double WINDOW_MIN_HEIGHT = 600.0;
    private static final double WINDOW_MIN_WIDTH = 400.0;

    private final Peter peter = new Peter();

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Peter - Your Friendly Neighbourhood Task Manager");
            stage.setMinHeight(WINDOW_MIN_HEIGHT);
            stage.setMinWidth(WINDOW_MIN_WIDTH);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Peter.png")));
            fxmlLoader.<MainWindow>getController().setPeter(peter);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Peter's main window.", exception);
        }
    }
}
