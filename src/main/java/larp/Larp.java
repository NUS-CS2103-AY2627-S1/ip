package larp;

/**
 * Coordinates the Larp chatbot's user interface, task list, parser, and storage.
 */
public class Larp {
    private final Storage storage;
    private final Ui ui;
    private final String loadErrorMessage;
    private TaskList tasks;

    /**
     * Creates a Larp chatbot that stores tasks at the specified file path.
     *
     * @param filePath Path of the task data file.
     */
    public Larp(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();
        this.tasks = new TaskList();
        this.loadErrorMessage = loadTasks();
    }

    /**
     * Starts the command loop and processes input until the user exits.
     */
    public void run() {
        this.ui.showWelcome();
        if (this.loadErrorMessage != null) {
            this.ui.showMessage(this.loadErrorMessage);
        }

        boolean isExit = false;
        while (!isExit && this.ui.hasNextCommand()) {
            String input = this.ui.readCommand();
            this.ui.showMessage(getResponse(input));
            isExit = isExitCommand(input);
        }
    }

    /**
     * Returns the greeting shown when the graphical interface starts.
     *
     * @return Greeting and any storage warning encountered during startup.
     */
    public String getWelcomeMessage() {
        String welcomeMessage = "Hello, I'm Larp.\n"
                + "Ready when you are. Add a task, check your list, or find something.";
        if (this.loadErrorMessage == null) {
            return welcomeMessage;
        }
        return welcomeMessage + "\n\n" + this.loadErrorMessage;
    }

    /**
     * Processes one command and returns the response for a graphical interface.
     *
     * @param input User command to process.
     * @return Response that should be displayed to the user.
     */
    public String getResponse(String input) {
        if (input == null) {
            return this.ui.getErrorMessage("Please enter a command.");
        }
        String trimmedInput = input.trim();
        try {
            return executeCommand(trimmedInput);
        } catch (LarpException e) {
            return this.ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Returns whether the supplied command asks Larp to exit.
     *
     * @param input User command to inspect.
     * @return {@code true} when the command is exactly {@code bye}.
     */
    public boolean isExitCommand(String input) {
        return input.trim().equals("bye");
    }

    /**
     * Loads saved tasks, falling back to an empty task list if loading fails.
     *
     * @return Error message for the user, or {@code null} when loading succeeds.
     */
    private String loadTasks() {
        try {
            this.tasks = new TaskList(this.storage.load());
            return null;
        } catch (LarpException e) {
            this.tasks = new TaskList();
            return this.ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Executes one user command and reports its result through the user interface.
     *
     * @param input User command to execute.
     * @return Response describing the result of the command.
     * @throws LarpException If the command or its arguments are invalid.
     */
    private String executeCommand(String input) throws LarpException {
        if (input.isEmpty()) {
            throw new LarpException("Please enter a command.");
        }

        if (input.equals("bye")) {
            return this.ui.getGoodbyeMessage();
        }

        if (input.equals("list")) {
            return this.ui.getTaskListMessage(this.tasks);
        }

        if (input.equals("find") || input.startsWith("find ")) {
            String keyword = Parser.parseFindKeyword(input);
            TaskList matchingTasks = this.tasks.find(keyword);
            return this.ui.getMatchingTasksMessage(matchingTasks);
        }

        if (input.equals("mark") || input.startsWith("mark ")) {
            ensureStorageReady();
            int taskIndex = Parser.parseTaskIndex(input, "mark", this.tasks.size());
            Task task = this.tasks.markAsDone(taskIndex);
            this.storage.save(this.tasks);
            return this.ui.getMarkedTaskMessage(task);
        }

        if (input.equals("unmark") || input.startsWith("unmark ")) {
            ensureStorageReady();
            int taskIndex = Parser.parseTaskIndex(input, "unmark", this.tasks.size());
            Task task = this.tasks.markAsNotDone(taskIndex);
            this.storage.save(this.tasks);
            return this.ui.getUnmarkedTaskMessage(task);
        }

        if (input.equals("delete") || input.startsWith("delete ")) {
            ensureStorageReady();
            int taskIndex = Parser.parseTaskIndex(input, "delete", this.tasks.size());
            Task deletedTask = this.tasks.delete(taskIndex);
            this.storage.save(this.tasks);
            return this.ui.getDeletedTaskMessage(deletedTask, this.tasks.size());
        }

        ensureStorageReady();
        Task task = Parser.parseTask(input);
        this.tasks.add(task);
        this.storage.save(this.tasks);
        return this.ui.getAddedTaskMessage(task, this.tasks.size());
    }

    /**
     * Prevents a failed load from being followed by a save that overwrites the original file.
     *
     * @throws LarpException If Larp could not load its task data during startup.
     */
    private void ensureStorageReady() throws LarpException {
        if (this.loadErrorMessage != null) {
            throw new LarpException("I can't change tasks while the task data file has a problem. "
                    + "Please fix or remove the file, then restart Larp.");
        }
    }

    /**
     * Starts Larp using its default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Larp("data/larp.txt").run();
    }
}
