package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts and configures Nova's JavaFX interface.
 */
public class Main extends Application {

    private static final String STORAGE_FILE_PATH = "data/nova.txt";

    private final Nova nova = new Nova(STORAGE_FILE_PATH);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setNova(nova);

        stage.setScene(new Scene(root));
        stage.setTitle("Nova");
        stage.setMinHeight(420);
        stage.setMinWidth(420);
        stage.show();
    }
}
