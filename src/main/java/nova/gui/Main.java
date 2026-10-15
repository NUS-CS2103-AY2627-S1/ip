package nova.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;

/** Starts the Nova JavaFX application window. */
public class Main extends Application {
    /** Loads the main window and injects Nova's application logic. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setTitle("Nova");
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);

            Storage storage = new Storage();
            TaskList tasks = storage.load();
            Parser parser = new Parser();
            fxmlLoader.<MainWindow>getController().bindNovaComponents(storage, tasks, parser);

            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Nova GUI.", exception);
        }
    }
}
