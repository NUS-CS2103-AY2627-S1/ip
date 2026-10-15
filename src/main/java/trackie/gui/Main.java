package trackie.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays Trackie's FXML-based JavaFX interface. */
public class Main extends Application {
    private final TrackieGui trackie = new TrackieGui();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);
            stage.setTitle("Trackie");
            stage.setMinHeight(400.0);
            stage.setMinWidth(500.0);
            loader.<MainWindow>getController().setTrackie(trackie);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load Trackie's main window", exception);
        }
    }
}
