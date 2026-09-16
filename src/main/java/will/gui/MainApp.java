package will.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import will.Will;

/**
 * The GUI entry point: loads {@code MainWindow.fxml}, wires it up to a
 * {@link Will} instance, and shows the window. Started indirectly via
 * {@link Launcher} rather than being run directly.
 */
public class MainApp extends Application {
    private static final String DATA_FILE_PATH = "data/will.txt";

    private final Will will = new Will(DATA_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(MainApp.class.getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Will");
            stage.setMinHeight(600.0);
            stage.setMinWidth(480.0);
            fxmlLoader.<MainWindow>getController().setWill(will);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
