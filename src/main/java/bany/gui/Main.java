package bany.gui;

import java.io.IOException;
import java.nio.file.Path;

import bany.BanyService;
import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.commands.CommandParser;
import bany.commands.ResponseMessage;
import bany.utilities.CommandValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX version of Bany and wires its shared dependencies together.
 */
public class Main extends Application {
    /** Creates the application instance launched by JavaFX. */
    public Main() {
    }

    /**
     * Loads the main-window layout, injects the command service, and shows the stage.
     *
     * @param stage primary JavaFX stage for the application.
     */
    @Override
    public void start(Stage stage) {
        try {
            TaskStorage taskStorage = new TaskStorage();
            TaskFileRepository repository = new TaskFileRepository(
                    Path.of("data", "bany.txt"));
            BanyService banyService = new BanyService(
                    repository,
                    taskStorage,
                    new CommandParser(new CommandValidator()),
                    new Responder());
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/bany/gui/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setBanyService(banyService);
            mainWindow.showWelcomeMessage();
            try {
                if (repository.loadWithRecovery(taskStorage, Path.of("data", "report"))) {
                    mainWindow.showStartupMessage(ResponseMessage.warning(
                            Responder.ErrorResponder.respondStartupFileWarning()));
                }
            } catch (IOException e) {
                mainWindow.showStartupMessage(ResponseMessage.warning(
                        Responder.ErrorResponder.respondStartupFileWarning()));
                mainWindow.showStartupMessage(ResponseMessage.error(
                        Responder.ErrorResponder.respondStartupRecoveryError()));
                mainWindow.disableInput();
            }
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
