package chai;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import chai.storage.Storage;
import chai.task.Task;
import chai.ui.Ui;

/**
 * Runs the Chai task manager and coordinates commands, task operations,
 * and persistent storage.
 */
public class Chai {
    /** Default location of Chai's human-editable task data. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "chai.txt");

    /** Chai's task list for the current session. */
    private final ArrayList<Task> tasks;

    /** Persists task changes for the current session. */
    private final Storage storage;

    /** An error encountered while loading saved tasks, or an empty string. */
    private final String startupError;

    /** Loads Chai's persisted tasks and prepares a new session. */
    public Chai() {
        this(new Storage(DEFAULT_DATA_FILE));
    }

    /**
     * Loads a Chai session using the specified storage location.
     *
     * @param storage Storage used to load and save tasks.
     */
    public Chai(Storage storage) {
        this.storage = storage;
        ArrayList<Task> loadedTasks;
        String loadError = "";
        try {
            loadedTasks = storage.load();
        } catch (ChaiException e) {
            loadedTasks = new ArrayList<>();
            loadError = formatError(e.getMessage());
        }
        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Starts Chai's command-line interface.
     *
     * @param args Command-line arguments, which Chai does not use.
     */
    public static void main(String[] args) {
        new Ui().run(new Chai());
    }

    /**
     * Returns the greeting shown when a user starts Chai.
     *
     * @return Greeting, preceded by any saved-data error encountered at startup.
     */
    public String getWelcomeMessage() {
        String greeting = "Welcome to Chai ☕\nLet's brew a calmer day. What needs doing?";
        return startupError.isEmpty() ? greeting : startupError + "\n" + greeting;
    }

    /**
     * Processes one command and returns Chai's response.
     *
     * @param command User command to process.
     * @return Response suitable for either the console or graphical interface.
     */
    public String getResponse(String command) {
        String normalizedCommand = command.trim();
        CommandType commandType = Parser.parseCommandType(normalizedCommand);

        try {
            switch (commandType) {
                case BYE:
                    Parser.requireNoArguments(normalizedCommand, "bye");
                    return "All brewed for now. Take care!";
                case LIST:
                    Parser.requireNoArguments(normalizedCommand, "list");
                    return formatTaskList(tasks, "Here's what's brewing:");
                case MARK:
                    return setTaskCompletion(normalizedCommand, true);
                case UNMARK:
                    return setTaskCompletion(normalizedCommand, false);
                case TODO:
                    return addTask(Parser.parseTodo(normalizedCommand));
                case DEADLINE:
                    return addTask(Parser.parseDeadline(normalizedCommand));
                case EVENT:
                    return addTask(Parser.parseEvent(normalizedCommand));
                case DELETE:
                    return deleteTask(normalizedCommand);
                case FIND:
                    return findTasks(normalizedCommand);
                case SORT:
                    return sortTasks(normalizedCommand);
                case UNKNOWN:
                default:
                    throw new ChaiException(
                            "That blend isn't on my menu. Try: todo <description>");
            }
        } catch (ChaiException e) {
            return formatError(e.getMessage());
        }
    }

    /** Marks or unmarks the numbered task. */
    private String setTaskCompletion(String command, boolean isDone) throws ChaiException {
        int taskIndex = Parser.parseTaskIndex(command, isDone ? "mark" : "unmark", tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        saveOrRollback(() -> {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsUndone();
            }
        });

        String state = isDone ? "done" : "not done";
        return "Nice progress — task " + (taskIndex + 1) + " is now " + state + ":\n  " + task;
    }

    /** Parses and removes a task in the form {@code delete <task number>}. */
    private String deleteTask(String command) throws ChaiException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removed = tasks.remove(taskIndex);
        saveOrRollback(() -> tasks.add(taskIndex, removed));
        return "Cleared from the tray. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /** Finds tasks whose descriptions contain the command's keyword. */
    private String findTasks(String command) throws ChaiException {
        String keyword = Parser.parseFindKeyword(command);

        List<Task> matchingTasks = tasks.stream().
                filter(task -> task.containsKeyword(keyword)).
                toList();
        return formatTaskList(matchingTasks, "I found these matching tasks:");
    }

    /** Sorts dated tasks chronologically and places undated tasks afterward. */
    private String sortTasks(String command) throws ChaiException {
        Parser.requireNoArguments(command, "sort");
        ArrayList<Task> originalOrder = new ArrayList<>(tasks);
        tasks.sort(Comparator.comparing(Task::getSortDate));
        saveOrRollback(() -> {
            tasks.clear();
            tasks.addAll(originalOrder);
        });
        return formatTaskList(tasks, "Freshly arranged, earliest first:");
    }

    /** Adds and saves a task, then returns the standard confirmation. */
    private String addTask(Task task) throws ChaiException {
        tasks.add(task);
        saveOrRollback(() -> tasks.remove(tasks.size() - 1));
        return "Steeped and saved. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /** Saves task changes, reverting the in-memory mutation when saving fails. */
    private void saveOrRollback(Runnable rollback) throws ChaiException {
        try {
            storage.save(tasks);
        } catch (ChaiException e) {
            rollback.run();
            throw e;
        }
    }

    /** Formats a numbered task list with the given heading. */
    private String formatTaskList(List<Task> taskList, String heading) {
        if (taskList.isEmpty()) {
            return heading + "\n  (none)";
        }

        StringBuilder output = new StringBuilder(heading);
        for (int index = 0; index < taskList.size(); index++) {
            output.append("\n").append(index + 1).append(". ").append(taskList.get(index));
        }
        return output.toString();
    }

    /** Adds Chai's standard prefix to an error explanation. */
    private static String formatError(String message) {
        return "Oops — " + message;
    }
}
