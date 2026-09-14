package toot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import toot.ui.MainWindow;

/**
 * Configures and displays Toot's main JavaFX stage.
 */
public class Main extends Application {
    private final Toot toot = new Toot();

    /**
     * Loads the FXML view, injects Toot, and displays the application window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setToot(toot);

        stage.setScene(new Scene(root));
        stage.setTitle("Toot");
        stage.setMinHeight(480.0);
        stage.setMinWidth(420.0);
        stage.show();
    }
}
