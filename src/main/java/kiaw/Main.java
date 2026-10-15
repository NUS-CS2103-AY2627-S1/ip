package kiaw;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kiaw.ui.MainWindow;

/**
 * JavaFX GUI for Kiaw.
 */
public class Main extends Application {

    private final Kiaw kiaw = new Kiaw();

    /**
     * Starts the Kiaw JavaFX interface.
     *
     * @param stage primary application stage
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource(
                            "/view/MainWindow.fxml"));

            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setScene(scene);
            stage.setTitle("Kiaw");

            fxmlLoader.<MainWindow>getController().setKiaw(kiaw);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
