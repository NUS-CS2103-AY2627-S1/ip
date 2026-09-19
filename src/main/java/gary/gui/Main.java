package gary.gui;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import gary.Gary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Displays the Gary chatbot using JavaFX and FXML.
 */
public class Main extends Application {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "gary.txt");

    private final Gary gary = new Gary(DEFAULT_DATA_FILE);

    @Override
    public void start(Stage stage) throws IOException {
        URL view = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml is missing");
        FXMLLoader loader = new FXMLLoader(view);
        BorderPane root = loader.load();
        loader.<MainWindow>getController().setGary(gary);

        stage.setTitle("Gary - Task and Contact Assistant");
        stage.setMinWidth(420.0);
        stage.setMinHeight(480.0);
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
