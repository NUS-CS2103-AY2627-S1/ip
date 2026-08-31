import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A command-line chatbot that stores ToDos, deadlines, and events.
 *
 * <p>This class only wires the parts together and runs the conversation loop.
 * {@link Ui} talks to the user, {@link Parser} makes sense of what they typed,
 * a {@link Command} carries the request out, {@link TaskList} holds the tasks,
 * and {@link Storage} keeps them on disk.
 */
public class Bibi {
    /**
     * Where the task list is kept, relative to the project root. Building the
     * path from separate names keeps it correct on any operating system.
     */
    private static final Path SAVE_FILE_PATH = Paths.get("data", "bibi.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Prepares Bibi to work with the given save file.
     *
     * @param saveFilePath where the task list is read from and written to
     */
    public Bibi(Path saveFilePath) {
        ui = new Ui();
        storage = new Storage(saveFilePath);
        tasks = new TaskList();
    }

    /**
     * Greets the user, restores any saved tasks, then handles commands until
     * one of them ends the session.
     */
    public void run() {
        ui.showWelcome();

        // Loading is done here rather than in the constructor because it speaks
        // to the user, and the greeting should come first.
        tasks = loadTasks();

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (BibiException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Reads the tasks saved by an earlier session and reports what was restored.
     *
     * @return the restored task list, or an empty list when nothing was saved yet
     */
    private TaskList loadTasks() {
        try {
            Storage.LoadReport report = storage.load();

            if (!report.tasks().isEmpty()) {
                ui.showLoaded(report.tasks().size());
            }
            if (!report.warnings().isEmpty()) {
                ui.showLoadWarnings(report.warnings());
            }
            return new TaskList(report.tasks());
        } catch (IOException exception) {
            // Reading failed outright, so continue with an empty list rather than
            // refusing to start. Saving later replaces the unreadable file.
            ui.showLoadingError(storage.getFilePath(), exception);
            return new TaskList();
        }
    }

    /**
     * Starts Bibi using the standard save file.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Bibi(SAVE_FILE_PATH).run();
    }
}
