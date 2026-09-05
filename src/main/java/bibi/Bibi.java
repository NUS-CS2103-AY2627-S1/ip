package bibi;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import bibi.command.Command;
import bibi.task.TaskList;

/**
 * A chatbot that stores ToDos, deadlines, and events.
 *
 * <p>This class only wires the parts together and carries out one request at a
 * time. {@link Ui} does the talking, {@link Parser} makes sense of what the user
 * typed, a {@link Command} carries the request out, {@link TaskList} holds the
 * tasks, and {@link Storage} keeps them on disk.
 *
 * <p>Two front ends drive it. {@link #run()} is the console conversation loop.
 * {@link #getGreeting()} and {@link #getResponse(String)} serve the GUI, which
 * cannot loop on standard input because JavaFX owns the thread; they do the same
 * work and hand back the words instead of printing them.
 */
public class Bibi {
    /**
     * Where the task list is kept, relative to the working directory. Building
     * the path from separate names keeps it correct on any operating system.
     */
    public static final Path DEFAULT_SAVE_FILE_PATH = Paths.get("data", "bibi.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExitRequested;

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
        ui.showBanner();
        greetAndLoad();

        while (!isExitRequested) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExitRequested = command.isExit();
            } catch (BibiException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting the GUI shows before the user has typed anything.
     *
     * @return the welcome text, followed by any report about the restored tasks
     */
    public String getGreeting() {
        ui.startCapture();
        greetAndLoad();
        return ui.takeCapturedText();
    }

    /**
     * Carries out one typed command and returns what Bibi would have said.
     *
     * <p>A command that fails is reported in the returned text rather than
     * thrown, because the GUI has nowhere useful to send an exception and the
     * user should see the explanation in the conversation like any other reply.
     *
     * @param fullCommand one line of input, exactly as the user typed it
     * @return Bibi's reply, ready to be shown in a dialog box
     */
    public String getResponse(String fullCommand) {
        ui.startCapture();
        try {
            Command command = Parser.parse(fullCommand);
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
        } catch (BibiException exception) {
            ui.showError(exception.getMessage());
        }
        return ui.takeCapturedText();
    }

    /**
     * Reports whether the last command asked to end the session.
     *
     * @return {@code true} once the user has said goodbye
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Says hello and restores the previous session's tasks.
     *
     * <p>Loading is done here rather than in the constructor because it speaks to
     * the user, and the greeting should come first.
     */
    private void greetAndLoad() {
        ui.showWelcome();
        tasks = loadTasks();
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
     * Starts the console interface using the standard save file.
     *
     * <p>The GUI has its own entry point in {@link Launcher}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Bibi(DEFAULT_SAVE_FILE_PATH).run();
    }
}
