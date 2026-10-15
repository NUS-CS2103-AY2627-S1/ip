package neil;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import neil.ui.MainWindow;

/**
 * Starts the JavaFX user interface for Neil.
 */
public class Main extends Application {
    private final Neil neil = new Neil("data/neil.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setNeil(neil);

        stage.setMinHeight(480);
        stage.setMinWidth(360);
        stage.setScene(scene);
        stage.setTitle("Neil");
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments supplied by the launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
