package neil;

import neil.exception.NeilException;
import neil.parser.Parser;
import neil.storage.Storage;
import neil.task.Task;
import neil.task.ToDoList;
import neil.ui.Ui;

/**
 * Processes commands for the Neil task-management application.
 */
public class Neil {
    private final Storage storage;
    private final Ui ui;
    private final ToDoList toDoList;
    private final CommandResult startupError;

    /**
     * Creates the application using the specified storage file.
     *
     * @param filePath path to the task storage file.
     */
    public Neil(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();
        this.toDoList = new ToDoList();
        CommandResult loadingError = null;

        try {
            for (Task task : storage.load()) {
                toDoList.add(task);
            }
        } catch (NeilException e) {
            loadingError = CommandResult.error(ui.showError(e.getMessage()));
        }

        this.startupError = loadingError;
    }

    /**
     * Returns the message shown when the application starts.
     *
     * @return welcome result, or a storage error if saved tasks could not be loaded.
     */
    public CommandResult getWelcomeResponse() {
        if (startupError != null) {
            return startupError;
        }
        return CommandResult.normal(ui.showWelcome());
    }

    /**
     * Processes one user command and returns Neil's response.
     *
     * @param input raw user command.
     * @return result to display in the user interface.
     */
    public CommandResult getResponse(String input) {
        if (isExitCommand(input)) {
            return CommandResult.normal(ui.showGoodbye());
        }

        String[] parts = input.trim().split("\\s+");
        try {
            if (parts[0].equals("help")) {
                if (parts.length != 1) {
                    throw new NeilException("Use: help");
                }
                return CommandResult.help(ui.showHelp());
            }

            if (startupError != null) {
                return startupError;
            }

            switch (parts[0]) {
                case "mark": {
                    int taskNumber = Parser.parseTaskNumber(parts);
                    Task task = toDoList.markTaskAsDone(taskNumber);
                    storage.save(toDoList.getTasks());
                    return CommandResult.normal(ui.showTaskMarked(task));
                }
                case "unmark": {
                    int taskNumber = Parser.parseTaskNumber(parts);
                    Task task = toDoList.unmarkTask(taskNumber);
                    storage.save(toDoList.getTasks());
                    return CommandResult.normal(ui.showTaskUnmarked(task));
                }
                case "delete": {
                    int taskNumber = Parser.parseTaskNumber(parts);
                    Task task = toDoList.remove(taskNumber);
                    storage.save(toDoList.getTasks());
                    return CommandResult.normal(ui.showTaskDeleted(task, toDoList.size()));
                }
                case "list":
                    return CommandResult.normal(ui.showTaskList(toDoList));
                case "find": {
                    String keyword = Parser.parseFindKeyword(input);
                    return CommandResult.normal(ui.showMatchingTasks(toDoList.findTasks(keyword)));
                }
                default:
                    Task task = Parser.parseTask(input);
                    toDoList.add(task);
                    storage.save(toDoList.getTasks());
                    return CommandResult.normal(ui.showTaskAdded(task, toDoList.size()));
            }
        } catch (NeilException e) {
            return CommandResult.error(ui.showError(e.getMessage()));
        }
    }

    /**
     * Returns whether the specified command should exit the application.
     *
     * @param input raw user command.
     * @return true if the command is {@code bye}, ignoring surrounding whitespace and letter case.
     */
    public boolean isExitCommand(String input) {
        return input.strip().equalsIgnoreCase("bye");
    }
}
