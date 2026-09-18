package phin;

import java.io.IOException;
import java.util.List;

/**
 * Processes Phin commands independently of the console or graphical interface.
 */
public class Phin {
    private static final String LOADING_ERROR = "Couldn't load data/phin.txt. Check the file before restarting;"
            + " it has not been changed.";
    private static final String HELP_MESSAGE = String.join(System.lineSeparator(),
            "    PHIN COMMAND GUIDE",
            "    Add tasks",
            "      todo (t) DESCRIPTION",
            "      deadline (dl) DESCRIPTION /by yyyy-MM-dd",
            "      event (e) DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd",
            "    Manage tasks",
            "      list (l)",
            "      mark (m) NUMBER",
            "      unmark (um) NUMBER",
            "      delete (del) NUMBER",
            "      update (u) NUMBER /description TEXT [/by DATE | /from DATE /to DATE]",
            "      find (f) KEYWORD",
            "    Other",
            "      help (h)    Show this guide",
            "      bye (q)     Exit Phin",
            "    Text in parentheses is the shortcut. Replace CAPITALIZED words with your details.");

    private final Storage storage = new Storage();
    private final TaskList tasks;

    /**
     * Loads saved tasks for a new chatbot session.
     */
    public Phin() {
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException | SecurityException exception) {
            loadedTasks = null;
        }
        tasks = loadedTasks;
    }

    /**
     * Runs Phin through its original text interface.
     *
     * @param args Command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Phin phin = new Phin();

        ui.showWelcome();
        if (!phin.isReady()) {
            ui.showLoadingError();
            ui.showLine();
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showResponse(phin.getResponse(command));
            ui.showLine();
            if (command.strip().equals("bye") || command.strip().equals("q")) {
                break;
            }
        }
    }

    /**
     * Executes one user command and returns the response for any user interface.
     *
     * @param command Complete, untrimmed user command.
     * @return Response text without a trailing line separator.
     */
    public String getResponse(String command) {
        if (!isReady()) {
            return "    " + LOADING_ERROR;
        }

        try {
            command = Parser.expandShortcut(command);
            String commandWord = Parser.parseCommandWord(command);
            String response = execute(command, commandWord);
            if (!commandWord.equals("help") && !commandWord.equals("list")
                    && !commandWord.equals("find") && !commandWord.equals("bye")) {
                try {
                    storage.save(tasks.asList());
                } catch (IOException | SecurityException exception) {
                    response += System.lineSeparator()
                            + "    Couldn't save data/phin.txt. Changes are only in memory; check the data folder.";
                }
            }
            return response;
        } catch (PhinException | IllegalArgumentException exception) {
            return "    Seriously? " + exception.getMessage();
        }
    }

    /**
     * Checks whether saved tasks were loaded safely.
     *
     * @return True when commands can be processed.
     */
    public boolean isReady() {
        return tasks != null;
    }

    /**
     * Dispatches a recognized command to its action and returns the response.
     *
     * @throws PhinException If the command arguments are invalid.
     */
    private String execute(String command, String commandWord) throws PhinException {
        assert isReady() : "Commands must not execute after a failed load";
        switch (commandWord) {
            case "help":
                return HELP_MESSAGE;
            case "bye":
                return "    Finally. Bye.";
            case "list":
                return formatTasks("Here are the tasks in your list:", tasks.asList());
            case "find":
                String keyword = Parser.parseFindKeyword(command);
                return formatTasks("Here are the matching tasks in your list:", tasks.find(keyword));
            case "mark":
                // Fallthrough
            case "unmark":
                return changeTaskStatus(command, commandWord);
            case "delete":
                return deleteTask(command);
            case "update":
                return updateTask(command);
            default:
                return addTask(command);
        }
    }

    /**
     * Validates a task number, changes its status, and formats the confirmation.
     *
     * @throws PhinException If the task number is invalid.
     */
    private String changeTaskStatus(String command, String commandWord) throws PhinException {
        int taskIndex = Parser.parseTaskIndex(command, commandWord, tasks.size());
        boolean isDone = commandWord.equals("mark");
        Task updatedTask = tasks.setDone(taskIndex, isDone);
        return "    Fine. I've marked this task as " + (isDone ? "done" : "not done") + ":"
                + System.lineSeparator() + "      " + updatedTask;
    }

    /**
     * Validates a task number, removes the task, and reports the remaining count.
     *
     * @throws PhinException If the task number is invalid.
     */
    private String deleteTask(String command) throws PhinException {
        int deletedIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(deletedIndex);
        return "    Noted. I've removed this task:" + System.lineSeparator() + "      " + removedTask
                + System.lineSeparator() + formatTaskCount();
    }

    /**
     * Validates requested fields, replaces one task, and confirms its complete updated form.
     *
     * @throws PhinException If the task number or update fields are invalid.
     * @throws IllegalArgumentException If an updated date or event range is invalid.
     */
    private String updateTask(String command) throws PhinException {
        int taskIndex = Parser.parseUpdateIndex(command, tasks.size());
        Task updatedTask = Parser.parseUpdatedTask(command, tasks.asList().get(taskIndex));
        tasks.update(taskIndex, updatedTask);
        return "    Fine. I've updated this task:" + System.lineSeparator() + "      " + updatedTask;
    }

    /**
     * Validates a creation command, adds its task, and reports the new count.
     *
     * @throws PhinException If required command fields are missing.
     * @throws IllegalArgumentException If a date or event range is invalid.
     */
    private String addTask(String command) throws PhinException {
        Task task = Parser.parseTask(command);
        if (tasks.containsEquivalent(task)) {
            throw new PhinException("That task is already in the list.");
        }
        tasks.add(task);
        return "    Fine. I've added this task:" + System.lineSeparator() + "      " + task
                + System.lineSeparator() + formatTaskCount();
    }

    private String formatTasks(String heading, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder("    ").append(heading);
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append(System.lineSeparator()).append("    ").append(i + 1).append('.')
                    .append(displayedTasks.get(i));
        }
        return response.toString();
    }

    private String formatTaskCount() {
        return "    Now you have " + tasks.size() + " tasks in the list.";
    }
}
