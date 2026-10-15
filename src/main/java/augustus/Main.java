package augustus;

import java.io.IOException;

import augustus.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Augustus using FXML.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX GUI.
     *
     * @param stage primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            Augustus augustus = new Augustus();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setScene(scene);
            stage.setTitle("Augustus");
            stage.setResizable(false);

            fxmlLoader.<MainWindow>getController().setAugustus(augustus);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load main window", e);
        }
    }
}
