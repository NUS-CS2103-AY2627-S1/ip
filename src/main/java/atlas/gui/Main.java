package atlas.gui;

import java.io.IOException;

import atlas.AtlasException;
import atlas.AtlasSession;
import atlas.client.ClientList;
import atlas.storage.AtlasData;
import atlas.storage.Storage;
import atlas.task.TaskList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point of the Atlas GUI. Loads the main window from
 * view/MainWindow.fxml and connects it to a chat session.
 */
public class Main extends Application {

    /**
     * Builds and displays the application's primary window.
     *
     * @param stage the primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);

            Storage storage = new Storage(AtlasSession.DEFAULT_DATA_FILE);
            TaskList tasks;
            ClientList clients;
            String warning;
            try {
                AtlasData data = storage.load();
                tasks = new TaskList(data.getTasks());
                clients = new ClientList(data.getClients());
                warning = data.getWarnings().isEmpty() ? null : String.join("\n", data.getWarnings());
            } catch (AtlasException e) {
                warning = e.getMessage();
                tasks = new TaskList();
                clients = new ClientList();
            }

            ReplyCollector collector = new ReplyCollector();
            AtlasSession session = new AtlasSession(storage, tasks, clients, collector);
            fxmlLoader.<MainWindow>getController().setUp(session, collector, stage, warning);

            stage.setTitle("Atlas");
            stage.setMinHeight(480);
            stage.setMinWidth(340);
            stage.show();
            fxmlLoader.<MainWindow>getController().focusInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
