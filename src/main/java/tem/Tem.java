package tem;

/**
 * A simple assistant that stores tasks until asked to exit.
 */
public class Tem {
    private static final String DATA_FILE_PATH = "data/tem.txt";

    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;
    private final String loadingErrorMessage;
    private boolean wasLastResponseAnError;

    /**
     * Creates Tem using the default save-file location.
     */
    public Tem() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Tem using the given relative save-file path.
     *
     * @param filePath relative path to the task save file, for example {@code data/tem.txt}.
     */
    public Tem(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        String loadError = null;
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TemException exception) {
            loadedTasks = new TaskList();
            loadError = exception.getMessage();
        }
        tasks = loadedTasks;
        loadingErrorMessage = loadError;
    }

    /**
     * Starts Tem and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();
        if (loadingErrorMessage != null) {
            ui.showLoadingError(loadingErrorMessage);
        }
        while (true) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }
            String response = getResponse(command);
            System.out.println(response);
            if (isExit(command)) {
                ui.showLine();
                break;
            }
            ui.showLine();
        }
    }

    /**
     * Starts Tem with the default save file location.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Tem().run();
    }

    /**
     * Returns the welcome message shown once at startup.
     *
     * @return welcome text
     */
    public String welcomeMessage() {
        if (loadingErrorMessage != null) {
            return ui.welcomeMessage() + "\n\n" + ui.loadingErrorMessage(loadingErrorMessage);
        }
        return ui.welcomeMessage();
    }

    /**
     * Returns true if the given input is the bye command.
     *
     * @param input raw user input.
     * @return whether the input requests an exit
     */
    public boolean isExit(String input) {
        return input != null && normalizeSpaces(input).equals("bye");
    }

    /**
     * Returns whether the most recent response corrected an invalid command.
     *
     * @return whether the latest response is an error message
     */
    public boolean wasLastResponseAnError() {
        return wasLastResponseAnError;
    }

    /**
     * Processes one line of user input and returns Tem's response text.
     *
     * @param input raw user input.
     * @return response to show the user
     */
    public String getResponse(String input) {
        wasLastResponseAnError = false;
        String command = input == null ? "" : normalizeSpaces(input);
        if (command.isEmpty()) {
            wasLastResponseAnError = true;
            return "Please enter a command.";
        }
        if (command.equals("bye")) {
            return ui.goodbyeMessage();
        }

        try {
            return executeCommand(command);
        } catch (TemException exception) {
            wasLastResponseAnError = true;
            return exception.getMessage();
        }
    }

    private String executeCommand(String command) throws TemException {
        String commandWord = command.split(" ", 2)[0];
        switch (commandWord) {
            case "list":
                ensureNoArguments(command, "list");
                return ui.taskListMessage(tasks);
            case "mark":
                return withSave(markTask(command));
            case "unmark":
                return withSave(unmarkTask(command));
            case "delete":
                return withSave(deleteTask(command));
            case "find": {
                String keyword = Parser.parseFindKeyword(command);
                return ui.matchingTasksMessage(tasks, tasks.findMatchingIndices(keyword));
            }
            case "sort":
                ensureNoArguments(command, "sort");
                return withSave(sortTasks());
            default:
                Task task = Parser.parseTask(command);
                if (tasks.containsEquivalent(task)) {
                    throw new TemException(ui.duplicateTaskMessage(task));
                }
                tasks.add(task);
                storage.save(tasks.getTasks());
                return ui.taskAddedMessage(task, tasks.size());
        }
    }

    private String withSave(String response) throws TemException {
        storage.save(tasks.getTasks());
        return response;
    }

    private String markTask(String command) throws TemException {
        Task task = tasks.get(Parser.parseTaskIndex(command, tasks, "mark as done"));
        task.markAsDone();
        return ui.taskMarkedMessage(task);
    }

    private String unmarkTask(String command) throws TemException {
        Task task = tasks.get(Parser.parseTaskIndex(command, tasks, "mark as not done"));
        task.unmarkAsDone();
        return ui.taskUnmarkedMessage(task);
    }

    private String deleteTask(String command) throws TemException {
        Task deletedTask = tasks.delete(Parser.parseTaskIndex(command, tasks, "delete"));
        return ui.taskDeletedMessage(deletedTask, tasks.size());
    }

    private String sortTasks() {
        tasks.sortChronologically();
        return ui.tasksSortedMessage(tasks);
    }

    /**
     * Rejects unnecessary words after a command that has no parameters.
     *
     * @param command full command entered by the user.
     * @param commandWord command that accepts no arguments.
     * @throws TemException if the command contains arguments
     */
    private void ensureNoArguments(String command, String commandWord) throws TemException {
        if (!command.equals(commandWord)) {
            throw new TemException("The " + commandWord + " command does not take any arguments.");
        }
    }

    /**
     * Returns a command with each word separated by one space.
     *
     * @param input raw text entered by the user.
     * @return normalized command text
     */
    private String normalizeSpaces(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
}
