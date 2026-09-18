package rem;

import java.io.IOException;
import java.nio.file.Path;

import rem.command.Command;
import rem.exception.RemException;
import rem.parser.Parser;
import rem.storage.Storage;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Coordinates Rem's user interface, task list, storage, and commands.
 */
public class Rem {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private boolean hasLoadError;
    private String loadError;

    /**
     * Creates Rem and loads tasks from the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Rem(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();

        TaskList loadedTasks;
        boolean didLoadingFail = false;
        String loadingError = "";
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (IOException e) {
            loadedTasks = new TaskList();
            didLoadingFail = true;
            loadingError = e.getMessage();
        }
        tasks = loadedTasks;
        hasLoadError = didLoadingFail;
        loadError = loadingError;
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome(hasLoadError);
        if (hasLoadError) {
            ui.showError(loadError);
        }

        boolean isExit = false;
        while (!isExit) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }

            isExit = execute(command, ui);
            ui.showSeparator();
        }

        ui.close();
    }

    /**
     * Returns the greeting and any warning about loading saved tasks.
     *
     * @return Initial graphical conversation message.
     */
    public String getWelcome() {
        return "Hi! I'm Rem.\nI can help! Then maybe a nap."
                + (hasLoadError ? "\nOh. I couldn't load your saved tasks. I'm showing an empty list.\n"
                        + loadError : "");
    }

    /**
     * Reports whether there are tasks for the graphical empty-state display.
     *
     * @return Whether at least one task exists.
     */
    public boolean hasTasks() {
        return tasks.size() > 0;
    }

    /**
     * Reports whether malformed saved data can be safely replaced after being backed up.
     *
     * @return Whether the start-fresh recovery option is available.
     */
    public boolean canStartFresh() {
        return storage.canStartFresh();
    }

    /**
     * Backs up malformed saved data and starts with an empty task list.
     *
     * @return Path of the backup containing the original data.
     * @throws IOException If the backup or empty replacement cannot be created safely.
     */
    public Path startFresh() throws IOException {
        Path backup = storage.startFresh();
        tasks = new TaskList();
        hasLoadError = false;
        loadError = "";
        return backup;
    }

    /**
     * Processes one graphical input using the same commands as the console.
     *
     * @param input User command.
     * @return Collected response, whether it is an error, and whether the conversation should end.
     */
    public Response getResponse(String input) {
        StringBuilder messages = new StringBuilder();
        Ui responseUi = new Ui(message -> {
            if (!messages.isEmpty()) {
                messages.append('\n');
            }
            messages.append(message);
        });
        boolean isExit = execute(input, responseUi);
        return new Response(messages.toString(), isExit, responseUi.hasError());
    }

    /**
     * Executes a command and translates validation or storage errors into messages.
     */
    private boolean execute(String input, Ui targetUi) {
        try {
            Command command = Parser.parse(input);
            // Apply changes to a copy; publish it only after the command has saved successfully.
            TaskList workingTasks = tasks.copy();
            command.execute(workingTasks, targetUi, storage);
            tasks = workingTasks;
            return command.isExit();
        } catch (RemException e) {
            targetUi.showError(e.getMessage());
        } catch (IOException e) {
            targetUi.showError("Your changes were not applied or saved. " + e.getMessage());
        }
        return false;
    }

    /**
     * Starts Rem using its default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Rem("data/rem.txt").run();
    }
}
