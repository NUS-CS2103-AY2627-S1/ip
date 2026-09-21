package thaisbot.gui;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.command.Command;
import thaisbot.command.Parser;
import thaisbot.task.TaskList;

/**
 * JavaFX application that presents Thai's Bot as a general-purpose chatbot.
 */
public class StudyPlannerApp extends Application {
    private static final Path DATA_FILE = Paths.get("data", "tasks.txt");

    private final Parser parser = new Parser();
    private final StudyPlannerUi ui = new StudyPlannerUi();
    private final Storage storage = new Storage(DATA_FILE.toString(), parser);

    private TaskList tasks;
    private StudyPlannerWindow window;

    @Override
    public void start(Stage stage) {
        assert stage != null;
        tasks = loadTasks();
        window = new StudyPlannerWindow(this::handleCommand);

        Scene scene = new Scene(window, 920, 740);
        String stylesheet = getClass().getResource("/css/study.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("Thai's Bot");
        stage.setScene(scene);
        stage.show();

        ui.showWelcome();
        window.appendMessages(ui.drainMessages());
        window.appendMessages(
                "Try `todo read book #fun` or `deadline return book /by 2026-09-10`.",
                "You can also type `list`, `find book`, or `bye`."
        );
    }

    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (ThaisBotException e) {
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    private void handleCommand(String userInput) {
        assert tasks != null;
        assert window != null;
        try {
            Command command = parser.parse(userInput);
            command.execute(tasks, ui, storage);
            window.appendMessages(ui.drainMessages());
            if (command.isExit()) {
                Platform.exit();
            }
        } catch (ThaisBotException e) {
            ui.showError(e.getMessage());
            window.appendMessages(ui.drainMessages());
        }
    }
}
