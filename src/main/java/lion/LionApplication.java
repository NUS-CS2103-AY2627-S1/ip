package lion;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application entry point that launches Lion's GUI.
 */
public class LionApplication extends Application {

    private static final double MIN_WINDOW_HEIGHT = 220;
    private static final double MIN_WINDOW_WIDTH = 417;

    private Lion lion = new Lion();

    /**
     * Loads the main window and shows it.
     *
     * @param stage primary stage supplied by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(LionApplication.class.getResource("/view/MainWindow.fxml"));

            AnchorPane rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);

            stage.setScene(scene);

            fxmlLoader.<MainWindow>getController().setLion(lion);

            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
