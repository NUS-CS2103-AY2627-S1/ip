package rex.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rex.Rex;

/**
 * Builds Rex's window and puts it on screen.
 *
 * The layout itself lives in MainWindow.fxml; this class loads that file, hands
 * the controller a Rex to talk to, and shows the result.
 */
public class Main extends Application {
    private final Rex rex = new Rex("data", "rex.txt");

    /**
     * Loads the window and shows it.
     *
     * @param stage the window JavaFX provides.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Rex");
            stage.setMinHeight(220);
            stage.setMinWidth(417);

            // The controller is only available once the FXML has been loaded,
            // and it needs Rex before it can greet the user.
            fxmlLoader.<MainWindow>getController().setRex(rex);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
