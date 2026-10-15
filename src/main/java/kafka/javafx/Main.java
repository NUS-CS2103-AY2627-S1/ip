package kafka.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import kafka.Kafka;
import kafka.exception.KafkaException;
import kafka.storage.InstanceLock;
import kafka.storage.TaskStorage;

/**
 * Displays the JavaFX user interface for Kafka.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_HEIGHT = 220.0;
    private static final double MIN_WINDOW_WIDTH = 417.0;

    private InstanceLock instanceLock;

    @Override
    public void start(Stage stage) {
        try {
            instanceLock = InstanceLock.acquire(new TaskStorage().getFilePath());
            showMainWindow(stage);
        } catch (KafkaException | IOException exception) {
            Alert error = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            error.setTitle("Kafka could not start");
            error.setHeaderText("Unable to open Kafka");
            error.showAndWait();
            Platform.exit();
        }
    }

    /**
     * Loads and displays the main window after acquiring exclusive access to storage.
     */
    private void showMainWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setKafka(new Kafka());

        stage.setTitle("Kafka");
        stage.setResizable(true);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws IOException {
        if (instanceLock != null) {
            instanceLock.close();
        }
    }
}
