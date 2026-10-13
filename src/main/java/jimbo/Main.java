package jimbo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jimbo.ui.MainWindow;

/**
 * A GUI for Jimbo using FXML.
 */
public class Main extends Application {

    private Jimbo jimbo = new Jimbo("./tasks.txt");

    /**
     * Starts the GUI app.
     *
     * @param stage JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        try {
            assert Main.class.getResource("/view/MainWindow.fxml") != null  :
                    "MainWindow.fxml should exist";
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Jimbo");
            assert jimbo != null : "Jimbo should have been initialised";
            fxmlLoader.<MainWindow>getController().setJimbo(jimbo); // inject the Jimbo instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
