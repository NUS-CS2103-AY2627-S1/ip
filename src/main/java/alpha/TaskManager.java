package alpha;

import alpha.command.Parser;
import alpha.storage.Storage;
import alpha.storage.StorageException;
import alpha.task.Task;
import alpha.task.TaskList;
import java.nio.file.Path;
import java.util.List;

/** Coordinates command execution, task updates, and task persistence. */
public class TaskManager {
    private static final Path DATA_FILE = Path.of("data", "moss.txt");
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;
    private final String loadingWarning;

    /** Creates a task manager using the default relative task-data path. */
    public TaskManager() {
        this(TaskManager.DATA_FILE);
    }

    /** Creates a task manager using the supplied task-data path. */
    public TaskManager(Path filePath) {
        assert filePath != null : "A task manager must have a data-file path";
        this.storage = new Storage(filePath);
        this.parser = new Parser();

        TaskList loadedTasks;
        String warning;
        try {
            loadedTasks = new TaskList(this.storage.load());
            warning = null;
        } catch (StorageException exception) {
            loadedTasks = new TaskList();
            warning = "Moss couldn't reopen your task garden, so we're starting with a fresh patch.";
        }
        this.tasks = loadedTasks;
        this.loadingWarning = warning;
    }

    /** Represents the text and exit state produced by one valid command. */
    public static class CommandResult {
        private final String message;
        private final boolean exit;

        /** Creates a command result. */
        public CommandResult(String message, boolean exit) {
            assert message != null : "A command result must contain a message";
            this.message = message;
            this.exit = exit;
        }

        /** Returns the response text for the command. */
        public String getMessage() {
            return this.message;
        }

        /** Returns whether the application should close after this command. */
        public boolean isExit() {
            return this.exit;
        }
    }

    /** Returns a warning produced while loading saved tasks, or {@code null}. */
    public String getLoadingWarning() {
        return this.loadingWarning;
    }

    /** Returns a read-only view of the current tasks. */
    public List<Task> getTasks() {
        return this.tasks.getTasks();
    }

    /** Executes one command and returns the response that should be shown to the user. */
    public CommandResult execute(String input) throws AlphaException {
        Parser.Command command = this.parser.parse(input);
        assert command != null : "The parser must return a command for valid input";
        switch (command.getType()) {
            case ADD:
                return this.addTask(command.getTask());
            case LIST:
                return new CommandResult(this.tasks.toString(), false);
            case FIND:
                return this.findTasks(command.getKeyword());
            case MARK:
                return this.updateTaskStatus(command.getTaskNumber(), true);
            case UNMARK:
                return this.updateTaskStatus(command.getTaskNumber(), false);
            case SORT:
                return this.sortTasks();
            case DELETE:
                return this.deleteTask(command.getTaskNumber());
            case BYE:
                return new CommandResult("The garden can rest now. See you next time!", true);
            default:
                throw new AlphaException("I don't recognise that command yet.");
        }
    }

    /** Adds a task, saves it, and creates the corresponding response. */
    private CommandResult addTask(Task task) {
        assert task != null : "An add command must produce a task";
        Task addedTask = this.tasks.addTask(task);
        String message = String.format("Planted your task:%n  %s%nYour garden now has %d tasks.",
                addedTask, this.tasks.size());
        return new CommandResult(this.withSavingWarning(message), false);
    }

    /** Finds tasks by keyword and creates the corresponding response. */
    private CommandResult findTasks(String keyword) {
        List<Task> matchingTasks = this.tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            return new CommandResult("Nothing sprouted for that search.", false);
        }

        StringBuilder builder = new StringBuilder("Here's what Moss found in your garden:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            builder.append(String.format("%n%d.%s", i + 1, matchingTasks.get(i)));
        }
        return new CommandResult(builder.toString(), false);
    }

    /** Marks or unmarks a task, saves the changed list, and creates a response. */
    private CommandResult updateTaskStatus(int number, boolean done) throws AlphaException {
        Task task = this.requireTask(number);
        if (done) {
            this.tasks.markDone(number);
        } else {
            this.tasks.markUndone(number);
        }

        String message = done
                ? "Nicely tended—this task is complete:"
                : "No worries—I've reopened this task:";
        message = String.format("%s%n  %s", message, task);
        return new CommandResult(this.withSavingWarning(message), false);
    }

    /** Deletes a task, saves the changed list, and creates a response. */
    private CommandResult deleteTask(int number) throws AlphaException {
        Task task = this.requireTask(number);
        this.tasks.deleteTask(number);
        String message = String.format("Pruned this task:%n  %s%nYour garden now has %d tasks.",
                task, this.tasks.size());
        return new CommandResult(this.withSavingWarning(message), false);
    }

    /** Sorts dated tasks chronologically and saves the new garden order. */
    private CommandResult sortTasks() {
        this.tasks.sortByDateTime();
        String message = "Moss arranged your garden by date and time.";
        return new CommandResult(this.withSavingWarning(message), false);
    }

    /** Returns a numbered task or reports that the number is invalid. */
    private Task requireTask(int number) throws AlphaException {
        Task task = this.tasks.getTask(number);
        if (task == null) {
            throw new AlphaException("That task number does not exist.");
        }
        return task;
    }

    /** Saves tasks and appends a warning if persistence fails. */
    private String withSavingWarning(String message) {
        try {
            this.storage.save(this.tasks);
            return message;
        } catch (StorageException exception) {
            return message + System.lineSeparator() + "Moss couldn't save the latest garden changes.";
        }
    }
}
