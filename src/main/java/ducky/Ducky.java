package ducky;

/**
 * Coordinates the user interface, command parser, task list, and storage.
 */
public class Ducky {
    private static final String DATA_FILE_PATH = "data/ducky.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private boolean isExit;
    private boolean lastResponseWasError;

    /**
     * Creates a chatbot using the default data file.
     */
    public Ducky() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a chatbot using the given data file.
     *
     * @param filePath the path of the task data file.
     */
    public Ducky(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    /**
     * Loads saved tasks, falling back to an empty task list if loading fails.
     *
     * @return the loaded task list.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (DuckyException e) {
            System.out.println(ui.showError(e.getMessage()));
            return new TaskList();
        }
    }

    /**
     * Runs the chatbot on the console until the user enters {@code bye}.
     */
    public void run() {
        System.out.println(ui.showWelcome());
        while (!isExit) {
            String command = ui.readCommand();
            try {
                System.out.println(processCommand(command));
            } catch (DuckyException e) {
                System.out.println(ui.showError(e.getMessage()));
            }
        }
    }

    /**
     * Returns the chatbot's welcome message, for display when a GUI session starts.
     *
     * @return the welcome message.
     */
    public String getWelcomeMessage() {
        return ui.showWelcome();
    }

    /**
     * Parses and executes one user command, returning the chatbot's reply.
     * Used by the GUI, which displays one reply per user input rather than printing to the console.
     *
     * @param input the user's command.
     * @return the chatbot's reply.
     */
    public String getResponse(String input) {
        try {
            String response = processCommand(input);
            lastResponseWasError = false;
            return response;
        } catch (DuckyException e) {
            lastResponseWasError = true;
            return ui.showError(e.getMessage());
        }
    }

    /**
     * Returns whether the most recent GUI command produced an error.
     *
     * @return {@code true} if the most recent response is an error, otherwise {@code false}.
     */
    public boolean lastResponseWasError() {
        return lastResponseWasError;
    }

    /**
     * Returns whether the most recently processed command was {@code bye}.
     * Used by the GUI to decide when to close the application window.
     *
     * @return {@code true} if the chatbot should exit, otherwise {@code false}.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Parses and executes one user command.
     *
     * @param command the user's command.
     * @return the chatbot's reply to the command.
     * @throws DuckyException if the command is invalid or cannot be saved.
     */
    private String processCommand(String command) throws DuckyException {
        Parser.ParsedCommand parsedCommand = parser.parse(command);
        switch (parsedCommand.getType()) {
            case BYE:
                isExit = true;
                return ui.showExitMessage();
            case LIST:
                return ui.showTasks(tasks);
            case FIND:
                return ui.showMatchingTasks(tasks.find(parsedCommand.getKeyword()));
            case ADD:
                return addTask(parsedCommand.getTask());
            case MARK:
                return markTask(parsedCommand.getTaskIndex());
            case UNMARK:
                return unmarkTask(parsedCommand.getTaskIndex());
            case DELETE:
                return deleteTask(parsedCommand.getTaskIndex());
            default:
                assert false : "Every parsed command type must be handled";
                throw new DuckyException("I didn't get what you said.");
        }
    }

    /**
     * Adds a task and saves the updated task list.
     *
     * @param task the task to add.
     * @return the task-added message.
     * @throws DuckyException if the updated task list cannot be saved.
     */
    private String addTask(Task task) throws DuckyException {
        if (tasks.containsEquivalent(task)) {
            throw new DuckyException("That task is already in your pond.");
        }
        tasks.add(task);
        String response = ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
        return response;
    }

    /**
     * Marks a task as done and saves the updated task list.
     *
     * @param index the zero-based index of the task to mark.
     * @return the task-marked message.
     * @throws DuckyException if the task does not exist or the updated task list cannot be saved.
     */
    private String markTask(int index) throws DuckyException {
        Task task = getTask(index);
        task.markAsDone();
        String response = ui.showTaskMarkedAsDone(task);
        storage.save(tasks);
        return response;
    }

    /**
     * Marks a task as incomplete and saves the updated task list.
     *
     * @param index the zero-based index of the task to unmark.
     * @return the task-unmarked message.
     * @throws DuckyException if the task does not exist or the updated task list cannot be saved.
     */
    private String unmarkTask(int index) throws DuckyException {
        Task task = getTask(index);
        task.unmark();
        String response = ui.showTaskUnmarked(task);
        storage.save(tasks);
        return response;
    }

    /**
     * Deletes a task and saves the updated task list.
     *
     * @param index the zero-based index of the task to delete.
     * @return the task-deleted message.
     * @throws DuckyException if the updated task list cannot be saved.
     */
    private String deleteTask(int index) throws DuckyException {
        getTask(index);
        Task deletedTask = tasks.delete(index);
        String response = ui.showTaskDeleted(deletedTask, tasks.size());
        storage.save(tasks);
        return response;
    }

    /**
     * Returns the task at a parsed index after checking that it exists.
     *
     * @param index the zero-based task index.
     * @return the task at the index.
     * @throws DuckyException if the index does not identify a task.
     */
    private Task getTask(int index) throws DuckyException {
        if (index < 0 || index >= tasks.size()) {
            throw new DuckyException("That task number does not exist.");
        }
        return tasks.get(index);
    }

    /**
     * Starts Ducky with the default data file, using the console-based UI.
     *
     * @param args command-line arguments, which are currently unused.
     */
    public static void main(String[] args) {
        new Ducky(DATA_FILE_PATH).run();
    }
}
