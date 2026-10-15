package amy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import amy.task.Task;

/**
 * Coordinates Amy's user interface, command parsing, task management, and storage.
 */
public class Amy {
    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final UndoManager undoManager = new UndoManager();
    private boolean awaitingUndoConfirmation;

    /**
     * Creates Amy with tasks loaded from the specified file.
     *
     * @param filePath path of the task save file
     */
    public Amy(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        tasks = new TaskList(loadTasks());
    }

    /** Runs Amy's command loop. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            if (command.trim().equals("bye")) {
                ui.showFarewell();
                break;
            }

            ui.showMessage(getResponse(command));

            ui.showSeparator();
        }
    }

    /**
     * Saves the task list and reports an error if the hard disk cannot be written.
     *
     * @param tasks the current task list
     */
    private void saveTasks(ArrayList<Task> tasks) {
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException exception) {
            System.out.println("Unable to save tasks to the hard disk.");
        }
    }

    /**
     * Loads the previous task list and starts with an empty list if it cannot be read.
     *
     * @return the tasks loaded from the hard disk
     */
    private ArrayList<Task> loadTasks() {
        try {
            return storage.load();
        } catch (IOException | SecurityException exception) {
            System.out.println("Unable to load tasks from the hard disk.");
            return new ArrayList<>();
        }
    }

    /**
     * Starts Amy using the default task save file.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Amy(Storage.getDefaultSavePath()).run();
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input command entered through the graphical interface
     * @return response to display in the chat window
     */
    public String getResponse(String input) {
        // GUI commands originate from the text field and must be represented by a string.
        assert input != null : "GUI command input must not be null";
        String command = input.trim();
        if (awaitingUndoConfirmation) {
            return handleUndoConfirmation(command);
        }
        if (command.equals("bye")) {
            return "Lovely to see you! Until next time.";
        }

        if (command.equals("dark mode")) {
            return "Switched to dark mode.";
        }

        if (command.equals("light mode")) {
            return "Switched to light mode.";
        }

        try {
            if (command.equals("list")) {
                return getListResponse();
            }

            if (command.equals("undo")) {
                return requestUndo();
            }

            if (command.equals("find") || command.equals("mark")
                    || command.equals("unmark") || command.equals("delete")) {
                return command.equals("find") ? "Please provide a keyword."
                        : "Please provide a task number.";
            }

            if (command.startsWith("find ")) {
                return getFindResponse(command.substring(5).trim());
            }

            if (command.startsWith("mark ") || command.startsWith("unmark ")
                    || command.startsWith("delete ")) {
                return updateTask(command);
            }

            if (parser.isTaskCommand(command)) {
                return addTask(command, input);
            }
        } catch (NumberFormatException exception) {
            return "Please specify a valid task number.";
        } catch (AmyException exception) {
            return exception.getMessage();
        }
        return "I'm sorry, but I don't know what that means.";
    }

    /** Returns the response for a list command. */
    private String getListResponse() {
        if (tasks.isEmpty()) {
            return "Your list is empty for now!";
        }
        StringBuilder response = new StringBuilder("Amy's got your list right here!");
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i).getFullDisplayText());
        }
        return response.toString();
    }

    /** Returns the response for a find command. */
    private String getFindResponse(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        boolean foundMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                response.append("\n").append(i + 1).append(".").append(tasks.get(i).getFullDisplayText());
                foundMatch = true;
            }
        }
        return foundMatch ? response.toString()
                : "I couldn't find anything matching that. Maybe try again with a different keyword!";
    }

    /** Updates a task's completion state or removes it. */
    private String updateTask(String command) {
        String[] parts = command.split(" ", 2);
        int taskIndex = Integer.parseInt(parts[1].trim()) - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            return "That task does not exist.";
        }
        if (parts[0].equals("delete")) {
            ArrayList<Task> snapshot = copyTasks();
            Task deletedTask = tasks.get(taskIndex);
            String deletedTaskDetails = deletedTask.getFullDisplayText();
            tasks.remove(taskIndex);
            saveTasks(tasks.asList());
            undoManager.record(snapshot, "restore the task \"" + deletedTaskDetails + "\"");
            return "Gone for good! I've removed this task:\n  " + deletedTask.getFullDisplayText()
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        if (parts[0].equals("mark")) {
            ArrayList<Task> snapshot = copyTasks();
            tasks.mark(taskIndex);
            saveTasks(tasks.asList());
            undoManager.record(snapshot, "mark the task \"" + tasks.get(taskIndex).getFullDisplayText()
                    + "\" as not done");
            return "Great job! One more task conquered:\n  " + tasks.get(taskIndex).getFullDisplayText();
        }
        ArrayList<Task> snapshot = copyTasks();
        tasks.unmark(taskIndex);
        saveTasks(tasks.asList());
        undoManager.record(snapshot, "mark the task \"" + tasks.get(taskIndex).getFullDisplayText()
                + "\" as done");
        return "No worries, plans change. I've marked it as incomplete again:\n  "
                + tasks.get(taskIndex).getFullDisplayText();
    }

    /**
     * Creates and persists a task from a task command.
     *
     * @param command trimmed task command
     * @param input original user input
     * @return response describing the created task or the validation error
     * @throws AmyException when the task command contains invalid data
     */
    private String addTask(String command, String input) throws AmyException {
        Task task = parser.createTask(command, input);
        if (task == null) {
            return command.startsWith("deadline ")
                    ? "Please specify a deadline in the format: deadline <description> /by <date/time>."
                    : "Please specify an event in the format: event <description> /from <start> /to <end>.";
        }
        ArrayList<Task> snapshot = copyTasks();
        tasks.add(task);
        saveTasks(tasks.asList());
        undoManager.record(snapshot, "remove the task \"" + task.getFullDisplayText() + "\"");
        return "On it! I've added this task:\n  " + task.getFullDisplayText()
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private ArrayList<Task> copyTasks() {
        ArrayList<Task> copy = new ArrayList<>();
        for (Task task : tasks.asList()) {
            copy.add(task.copy());
        }
        return copy;
    }

    private String requestUndo() {
        if (!undoManager.canUndo()) {
            return "There is no command to undo.";
        }
        awaitingUndoConfirmation = true;
        return "Confirm undo: " + undoManager.getLatestDescription() + "? [yes/no]";
    }

    private String handleUndoConfirmation(String response) {
        if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
            awaitingUndoConfirmation = false;
            return "Undo cancelled.";
        }
        if (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("yes")) {
            return "Please answer yes or no.";
        }
        ArrayList<Task> snapshot = undoManager.removeLatestSnapshot();
        tasks.restore(snapshot);
        saveTasks(tasks.asList());
        awaitingUndoConfirmation = false;
        return "Undone. I've restored the previous task list.";
    }

}
