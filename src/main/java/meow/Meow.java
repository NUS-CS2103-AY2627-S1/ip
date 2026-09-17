package meow;

import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the Meow chatbot and coordinates user interaction,
 * command parsing, task storage, and persistence.
 */
public class Meow {
    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private boolean isLastResponseError;
    private String startupErrorMessage;

    /**
     * Creates a Meow chatbot and loads previously saved tasks.
     */
    public Meow() {
        this.ui = new Ui();
        this.parser = new Parser();
        this.storage = new Storage();
        try {
            tasks = storage.loadTasks();
        } catch (IOException e) {
            startupErrorMessage =
                    "Meow! I couldn't load your saved tasks because the data file is invalid. "
                            + "Please fix or remove data/meow.txt and restart Meow.";
            ui.showError(startupErrorMessage);
            tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot command loop until the user exits.
     */
    public void run() {
        ui.showGreeting();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            System.out.println(getResponse(input));

            if (input.strip().equals("bye")) {
                break;
            }
        }
    }

    /**
     * Processes a user command and returns the chatbot's response.
     *
     * @param input the user command
     * @return the chatbot's response
     */
    public String getResponse(String input) {
        String normalizedInput = normalizeInput(input);
        isLastResponseError = false;

        try {
            return executeCommand(normalizedInput);
        } catch (MeowException e) {
            isLastResponseError = true;
            return e.getMessage();
        } catch (IOException e) {
            isLastResponseError = true;
            return "Meow! Something went wrong while saving the tasks.";
        }
    }

    /**
     * Starts the Meow chatbot.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new Meow().run();
    }

    /**
     * Converts the task number in a user command into a zero-based task index.
     *
     * @param input the user command containing the task number
     * @return the zero-based task index
     * @throws MeowException if the task number is invalid
     */
    private int getTaskIndex(String input) throws MeowException {
        String[] parts = input.trim().split("\\s+");

        if (parts.length < 2) {
            throw new MeowException("Oops, I need a task number for that.");
        }

        if (parts.length > 2) {
            throw new MeowException(
                    "Meow! Please use the format: " + parts[0] + " TASK_NUMBER.");
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new MeowException(
                    "That doesn't look like a task number to me, meow.");
        }

        if (taskNumber <= 0) {
            throw new MeowException("Meow! Task number must be positive.");
        }

        if (taskNumber > tasks.size()) {
            throw new MeowException(
                    "Meow! I can't find task number " + taskNumber + " in your list.");
        }

        return taskNumber - 1;
    }

    /**
     * Finds tasks that match the keyword in the user command.
     *
     * @param input the find command
     * @return the formatted list of matching tasks
     * @throws MeowException if the keyword is invalid
     */
    private String findTasks(String input) throws MeowException {
        String keyword = parser.parseFindKeyword(input);
        TaskList matches = tasks.findTasks(keyword);
        return ui.getMatchingTasksMessage(matches);
    }

    /**
     * Marks the task specified by the user as completed.
     *
     * @param input the mark command
     * @return the confirmation message
     * @throws MeowException if the task number is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String markTask(String input) throws MeowException, IOException {
        ensureStorageReady();
        int taskIndex = getTaskIndex(input);
        Task task = tasks.getTask(taskIndex);

        if (task.isDone()) {
            throw new MeowException("Meow! This task is already marked as done.");
        }

        task.markAsDone();

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            task.markAsNotDone();
            throw e;
        }

        return ui.getTaskMarkedMessage(task);
    }

    /**
     * Marks the specified task as not completed.
     *
     * @param input the unmark command
     * @return the confirmation message
     * @throws MeowException if the task number is invalid
     * @throws IOException if the task list cannot be saved
     */
    private String unmarkTask(String input) throws MeowException, IOException {
        ensureStorageReady();
        int taskIndex = getTaskIndex(input);
        Task task = tasks.getTask(taskIndex);

        if (!task.isDone()) {
            throw new MeowException("Meow! This task is already unmarked.");
        }

        task.markAsNotDone();

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            task.markAsDone();
            throw e;
        }

        return ui.getTaskUnmarkedMessage(task);
    }

    /**
     * Adds a task from the user command.
     *
     * @param input the task command
     * @return the confirmation message
     * @throws MeowException if the task command is invalid
     * @throws IOException if the task list cannot be saved
     */
    private String addTask(String input) throws MeowException, IOException {
        ensureStorageReady();
        Task task = parser.parseTask(input);
        TaskList previousTasks = tasks.copy();

        tasks.add(task);
        saveOrRestore(previousTasks);

        return ui.getTaskAddedMessage(task, tasks.size());
    }

    /**
     * Deletes the task specified by the user.
     *
     * @param input the delete command
     * @return the confirmation message
     * @throws MeowException if the task number is invalid
     * @throws IOException if the task list cannot be saved
     */
    private String deleteTask(String input) throws MeowException, IOException {
        ensureStorageReady();
        int taskIndex = getTaskIndex(input);
        TaskList previousTasks = tasks.copy();

        Task deletedTask = tasks.delete(taskIndex);
        saveOrRestore(previousTasks);

        return ui.getTaskDeletedMessage(deletedTask, tasks.size());
    }

    /**
     * Sorts tasks chronologically and saves the updated order.
     *
     * @return the confirmation message
     * @throws MeowException if the saved task data could not be loaded
     * @throws IOException if the task list cannot be saved
     */
    private String sortTasks() throws MeowException, IOException {
        ensureStorageReady();
        TaskList previousTasks = tasks.copy();

        tasks.sortChronologically();
        saveOrRestore(previousTasks);

        return ui.getTasksSortedMessage();
    }

    /**
     * Checks whether the most recent response was caused by an error.
     *
     * @return true if the most recent response was an error, false otherwise
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns the error encountered while loading saved tasks.
     *
     * @return the startup error message, or null if loading succeeded
     */
    public String getStartupErrorMessage() {
        return startupErrorMessage;
    }

    /**
     * Ensures that task data was loaded successfully before allowing modifications.
     *
     * @throws MeowException if the saved task data could not be loaded
     */
    private void ensureStorageReady() throws MeowException {
        if (startupErrorMessage != null) {
            throw new MeowException(
                    "Meow! I can't modify tasks while the saved data file is invalid. "
                            + "Please fix or remove data/meow.txt and restart Meow.");
        }
    }

    /**
     * Normalizes whitespace surrounding the user command.
     *
     * @param input the raw user input
     * @return the normalized input
     */
    private String normalizeInput(String input) {
        return input.strip().replaceFirst("\\s+", " ");
    }

    /**
     * Executes the command represented by the given user input.
     *
     * @param input the normalized user input
     * @return the response produced by the command
     * @throws MeowException if the command or its arguments are invalid
     * @throws IOException if task data cannot be saved
     */
    private String executeCommand(String input) throws MeowException, IOException {
        String command = input.split(" ", 2)[0];

        return switch (command) {
            case "bye" -> {
                ensureNoArguments(input, "bye");
                yield ui.getFarewellMessage();
            }
            case "list" -> {
                ensureNoArguments(input, "list");
                yield ui.getTaskListMessage(tasks);
            }
            case "find" -> findTasks(input);
            case "sort" -> {
                ensureNoArguments(input, "sort");
                yield sortTasks();
            }
            case "mark" -> markTask(input);
            case "unmark" -> unmarkTask(input);
            case "delete" -> deleteTask(input);
            case "todo" -> {
                if (input.equals("todo")) {
                    throw new MeowException("Meow! A todo needs a description.");
                }
                yield addTask(input);
            }
            case "deadline" -> {
                if (input.equals("deadline")) {
                    throw new MeowException(
                            "Meow! A deadline needs a description and a /by date.");
                }
                yield addTask(input);
            }
            case "event" -> {
                if (input.equals("event")) {
                    throw new MeowException(
                            "Meow! An event needs a description, a /from date and a /to date.");
                }
                yield addTask(input);
            }
            default -> throw new MeowException(
                    "Meow! I'm sorry, but I don't know what that means.");
        };
    }

    /**
     * Checks that a command does not contain unexpected arguments.
     *
     * @param input the full user input
     * @param command the command being checked
     * @throws MeowException if extra arguments are present
     */
    private void ensureNoArguments(String input, String command)
            throws MeowException {
        if (!input.equals(command)) {
            throw new MeowException(
                    "Meow! The " + command + " command does not take any arguments.");
        }
    }

    /**
     * Saves the current task list and restores the previous task list if saving fails.
     *
     * @param previousTasks the task list before the modification
     * @throws IOException if the task list cannot be saved
     */
    private void saveOrRestore(TaskList previousTasks) throws IOException {
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            tasks = previousTasks;
            throw e;
        }
    }
}
