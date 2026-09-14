package groot.ui;

import java.io.IOException;

import groot.Groot;
import groot.exception.GrootException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays the Groot graphical interface using JavaFX and FXML.
 */
public class Main extends Application {

    /**
     * Loads and displays Groot's main window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            Groot groot = new Groot();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setGroot(groot);

            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setTitle("Groot");
            stage.setScene(new Scene(root));
            stage.setWidth(1000);
            stage.setHeight(720);
            stage.show();
        } catch (IOException | GrootException error) {
            throw new IllegalStateException("Unable to start Groot", error);
        }
    }
}
