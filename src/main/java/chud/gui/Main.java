package chud.gui;

import java.io.IOException;

import chud.storage.Storage;
import chud.task.TaskList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application: builds Chud's backend (storage, task list, a GUI-buffering {@link
 * GuiUi}) once, loads {@code MainWindow.fxml}, and hands both to the {@link MainWindow}
 * controller before showing the window.
 */
public class Main extends Application {
    private final Storage storage = new Storage("data/chud.txt");
    private final TaskList tasks = new TaskList(storage.load());
    private final GuiUi ui = new GuiUi();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Chud");
            stage.setMinWidth(400);
            stage.setMinHeight(220);
            fxmlLoader.<MainWindow>getController().init(tasks, storage, ui);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Could not load MainWindow.fxml", e);
        }
    }
}
