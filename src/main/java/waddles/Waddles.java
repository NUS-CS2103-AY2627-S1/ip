package waddles;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the Waddles chatbot. Coordinates the Ui, Storage,
 * and TaskList to run the main command loop, and exposes
 * {@link #getResponse(String)} so a GUI (see {@link MainWindow}) can drive
 * the same command logic one line at a time.
 */

public class Waddles {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * A snapshot of the task list taken just before the most recent
     * mutating command, restored by "undo". Null means there is nothing to
     * undo (either no mutating command has run yet, or the last one was
     * already undone). This supports undoing only the single most recent
     * command, by design.
     */
    private List<Task> undoSnapshot;

    /**
     * Creates a Waddles chatbot that persists tasks to the given file path.
     *
     * @param filePath Relative path to the data file.
     */
    public Waddles(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the main command loop: greets the user, repeatedly reads and
     * executes commands until "bye" is entered, then says goodbye.
     */
    public void run() {
        ui.showWelcome();
        String input = ui.readCommand();

        while (!input.equals("bye")) {
            ui.showLine();
            System.out.println(getResponse(input));
            ui.showLine();
            input = ui.readCommand();
        }

        ui.showGoodbye();
    }

    /**
     * Returns the greeting shown when a GUI session starts.
     *
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcomeMessage();
    }

    /**
     * Executes a single line of user input and returns the resulting
     * message, without printing anything itself. This is the logic shared
     * by both the CLI ({@link #run()}) and the JavaFX GUI.
     *
     * @param input A full line of user input.
     * @return The message to show the user in response.
     */
    public String getResponse(String input) {
        try {
            String command = Parser.getCommandWord(input);
            assert command != null : "Parser#getCommandWord splits on a fixed pattern and should "
                    + "always return at least an empty string, never null";

            switch (command) {
            case "bye":
                return ui.formatGoodbyeMessage();
            case "list":
                return ui.formatTaskList(tasks);
            case "find":
                String keyword = Parser.parseFind(input);
                return ui.formatFoundTasks(tasks.find(keyword));
            case "undo":
                return undoLastCommand();
            case "schedule":
                LocalDate date = Parser.parseSchedule(input);
                return ui.formatSchedule(date, tasks.findOnDate(date));
            case "mark": {
                int index = Parser.parseTaskIndex(input, "mark", tasks.size());
                saveUndoSnapshot();
                Task task = tasks.get(index);
                String message;
                if (task.isRecurring()) {
                    // Completing an occurrence of a recurring task books the next one,
                    // rather than leaving it marked done forever. recur() immediately
                    // advances the task's own date and resets it to not-done, so the
                    // confirmation is built from a done snapshot of the occurrence that
                    // was just completed, taken before recur() moves the task on.
                    Task completedOccurrence = task.copy();
                    completedOccurrence.markAsDone();
                    task.recur();
                    message = ui.formatRecurred(completedOccurrence);
                } else {
                    task.markAsDone();
                    message = ui.formatMarked(task);
                }
                saveTasks();
                return message;
            }
            case "unmark": {
                int index = Parser.parseTaskIndex(input, "unmark", tasks.size());
                saveUndoSnapshot();
                tasks.get(index).markAsNotDone();
                String message = ui.formatUnmarked(tasks.get(index));
                saveTasks();
                return message;
            }
            case "delete": {
                int index = Parser.parseTaskIndex(input, "delete", tasks.size());
                saveUndoSnapshot();
                Task removed = tasks.delete(index);
                String message = ui.formatDeleted(removed, tasks.size());
                saveTasks();
                return message;
            }
            case "todo":
                return addTaskAndRespond(Parser.parseTodo(input));
            case "deadline":
                return addTaskAndRespond(Parser.parseDeadline(input));
            case "event":
                return addTaskAndRespond(Parser.parseEvent(input));
            default:
                throw new WaddlesException(
                        WaddlesException.ERROR_PREFIX + " Oink?! I have no idea what that means.");
            }
        } catch (WaddlesException e) {
            return e.getMessage();
        }
    }

    /**
     * Adds a task to the list, persists the change, and builds the
     * confirmation message shown to the user. Shared by the todo, deadline,
     * and event commands, which differ only in how the task is parsed.
     *
     * @param task The task to add.
     * @return The confirmation message to show the user.
     */
    private String addTaskAndRespond(Task task) {
        saveUndoSnapshot();
        tasks.add(task);
        String message = ui.formatAdded(task, tasks.size());
        saveTasks();
        return message;
    }

    /**
     * Records a deep copy of the current task list as the one "undo" will
     * restore. Called right before a mutating command takes effect. Uses
     * Task#copy() rather than a reference copy of the list, since the same
     * Task objects would otherwise still be affected by later mutations
     * (e.g. a later markAsDone() would retroactively "undo" as done too).
     */
    private void saveUndoSnapshot() {
        List<Task> snapshot = new ArrayList<>();
        for (Task task : tasks.getAll()) {
            snapshot.add(task.copy());
        }
        undoSnapshot = snapshot;
    }

    /**
     * Restores the task list to how it was just before the most recent
     * mutating command (add, delete, mark, or unmark), i.e. a single-level
     * undo. Calling this again immediately after has nothing left to undo.
     *
     * @return The message to show the user.
     */
    private String undoLastCommand() {
        if (undoSnapshot == null) {
            return WaddlesException.ERROR_PREFIX + " Oink! There's nothing to undo yet.";
        }
        tasks.setAll(undoSnapshot);
        undoSnapshot = null;
        saveTasks();
        return "Oink-kay, un-done! Rewound like a happy curly tail:\n" + ui.formatTaskList(tasks);
    }

    /**
     * Saves the current task list to disk, showing an error if it fails.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.getAll());
        } catch (IOException e) {
            ui.showSavingError();
        }
    }

    public static void main(String[] args) {
        new Waddles("./data/waddles.txt").run();
    }
}
