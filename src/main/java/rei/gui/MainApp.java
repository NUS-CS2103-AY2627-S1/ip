package rei.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rei.Rei;

/** Displays Rei's JavaFX interface. */
public class MainApp extends Application {
    private static final double MINIMUM_WIDTH = 480;
    private static final double MINIMUM_HEIGHT = 640;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            MainWindow controller = loader.getController();
            controller.setRei(new Rei());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    MainApp.class.getResource("/css/main.css")).toExternalForm());
            stage.setTitle("Rei");
            stage.setScene(scene);
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Rei's interface.", exception);
        }
    }
}
