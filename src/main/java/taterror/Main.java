package taterror;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import taterror.gui.MainWindow;

/**
 * JavaFX entry point for the TA Terror GUI. Not run directly - see
 * {@link Launcher}, which exists as a workaround for the "missing JavaFX
 * runtime" error some JDK setups hit when an {@link Application} subclass
 * is launched as the main class directly.
 */
public class Main extends Application {

    private TaTerror taTerror = new TaTerror();

    /**
     * Loads the main window's FXML, wires it to a fresh {@link TaTerror}
     * instance, and shows the stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("TA Terror");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setTaTerror(taTerror);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the JavaFX application. Prefer {@link Launcher#main(String[])} as
     * the actual run/jar entry point.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}
