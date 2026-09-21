package janet;

import java.io.IOException;

import janet.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays Janet's graphical user interface.
 */
public class Main extends Application {
    /** Minimum supported window width. */
    private static final double MINIMUM_WIDTH = 480.0;

    /** Minimum supported window height. */
    private static final double MINIMUM_HEIGHT = 420.0;

    /** Janet instance shared with the main-window controller. */
    private final Janet janet = new Janet();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setJanet(janet);

        stage.setTitle("Janet");
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
