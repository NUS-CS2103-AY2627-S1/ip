package martin.core;

import martin.command.Command;
import martin.exception.IllegalCommandException;
import martin.exception.MartinException;
import martin.storage.TasksStorage;
import martin.task.Task;
import martin.task.TaskList;
import martin.ui.Ui;

/**
 * Chatbot application that coordinates task management, storage, and user
 * interactions.
 */
public class Martin {
    private final TasksStorage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isRunning = true;

    /**
     * Constructs a new {@code Martin} chatbot instance with the specified data file
     * path.
     *
     * @param filePath The file path used to load and persist task data.
     */
    public Martin(String filePath) {
        this.ui = new Ui();
        this.storage = new TasksStorage(filePath);
        this.tasks = new TaskList(this.storage.load());
    }

    /**
     * Constructs a new {@code Martin} chatbot instance with the default data file
     * path.
     */
    public Martin() {
        this("./data/martin.txt");
    }

    /**
     * Starts and runs the main execution loop until the user exits or input ends.
     */
    public void run() {
        this.ui.showWelcome();

        while (this.isRunning) {
            String input = this.ui.readCommand();
            if (input == null) {
                this.isRunning = false;
                break;
            }
            String response = this.executeCommand(input);
            if (!response.isEmpty() && this.isRunning) {
                System.out.println(response);
            }
            this.ui.showLine();
        }
        this.ui.showGoodbye();
    }

    /**
     * Executes a user command and returns Martin's response.
     *
     * @param input The user input command string.
     * @return The response to display to the user.
     */
    public String executeCommand(String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                throw new IllegalCommandException("I cannot proceed without a command.");
            }
            Command command = Command.from(input);
            return switch (command) {
                case LIST -> this.ui.formatTaskList(this.tasks.getAllTasks());
                case MARK -> handleMarkTask(input, true);
                case UNMARK -> handleMarkTask(input, false);
                case DELETE -> handleDeleteTask(input);
                case TODO, DEADLINE, EVENT -> handleAddTask(input);
                case FIND -> handleFindTask(input);
                case BYE -> {
                    this.isRunning = false;
                    yield this.ui.formatGoodbye();
                }
            };
        } catch (MartinException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Returns whether Martin is accepting further commands.
     *
     * @return {@code true} while Martin is running, or {@code false} after bye.
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Returns the initial greeting shown when Martin starts.
     *
     * @return The formatted initial greeting.
     */
    public String getWelcomeMessage() {
        return this.ui.formatWelcome();
    }

    /**
     * Returns the goodbye message shown when Martin exits.
     *
     * @return The formatted Martin goodbye message.
     */
    public String getGoodbyeMessage() {
        return this.ui.formatGoodbye();
    }

    /**
     * Returns the task selected by a one-based command index, or {@code null} when
     * it is invalid.
     *
     * @param input The user input containing the task number.
     * @return The selected {@code Task}, or {@code null} if the index is invalid.
     */
    private Task findTaskByIndexFromInput(String input) {
        try {
            String[] inputParts = input.trim().split("\\s+");
            if (inputParts.length != 2) {
                return null;
            }
            int taskNumber = Integer.parseInt(inputParts[1]);
            return this.tasks.getByOneBasedIndex(taskNumber);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Returns a response after deleting the specified task.
     *
     * @param input The user input containing the task index to delete.
     * @return The response describing the deletion or an error.
     */
    private String handleDeleteTask(String input) {
        Task task = this.findTaskByIndexFromInput(input);
        if (task == null) {
            return "I can find no such task in the present list. Pray use a number displayed by list.";
        }
        this.tasks.remove(task);
        this.storage.save(this.tasks);
        return this.ui.formatTaskDeleted(task, this.tasks.size());
    }

    /**
     * Returns a response after adding the task described by a valid command.
     *
     * @param input The task command input string.
     * @return The response describing the added task.
     */
    private String handleAddTask(String input) {
        Task newTask = Task.of(input);
        this.tasks.add(newTask);
        this.storage.save(this.tasks);
        return this.ui.formatTaskAdded(newTask, this.tasks.size());
    }

    /**
     * Returns tasks whose descriptions contain the requested keyword.
     *
     * @param input The user input containing the search keyword.
     * @return The response containing matching tasks.
     * @throws IllegalCommandException If the search keyword is missing.
     */
    private String handleFindTask(String input) {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new IllegalCommandException("A proper search requires a keyword.");
        }
        return this.ui.formatFindResults(this.tasks.find(keyword));
    }

    /**
     * Returns a response after marking or unmarking the selected task.
     *
     * @param input            The user input containing the task index.
     * @param shouldMarkAsDone {@code true} to mark as done, {@code false} to mark
     *                         as not done.
     * @return The response describing the updated task or an error.
     */
    private String handleMarkTask(String input, boolean shouldMarkAsDone) {
        Task task = this.findTaskByIndexFromInput(input);
        if (task == null) {
            return "I can find no such task in the present list. Pray use a number displayed by list.";
        }
        if (shouldMarkAsDone) {
            task.markAsDone();
            this.storage.save(this.tasks);
            return this.ui.formatTaskMarked(task);
        }
        task.markAsNotDone();
        this.storage.save(this.tasks);
        return this.ui.formatTaskUnmarked(task);
    }

}
