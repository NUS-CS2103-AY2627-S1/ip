package jaylen;

/** A task assistant that can be used from either the console or JavaFX GUI. */
public class Jaylen {
    private final Storage storage;
    private final Parser parser;
    private final Ui ui;
    private final TaskList tasks;
    private String startupErrorMessage;

    /** Creates Jaylen with its user interface, parser, and file storage components. */
    public Jaylen() {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage();
        tasks = loadTasks();
    }

    /** Starts Jaylen and processes commands until the user says goodbye. */
    public void run() {
        ui.showWelcome();
        if (hasStartupError()) {
            ui.showError(startupErrorMessage);
            ui.showLine();
        }

        while (true) {
            String input = ui.readCommand();
            ui.showLine();
            try {
                ParsedCommand command = parser.parse(input);
                if (command.getType() == ParsedCommand.Type.BYE) {
                    ui.showGoodbye();
                    ui.showLine();
                    return;
                }
                System.out.println(execute(command));
            } catch (JaylenException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Processes one GUI command and returns the text Jaylen should display.
     *
     * @param input the complete command entered by the user
     * @return Jaylen's response to the command
     */
    public String getResponse(String input) {
        try {
            ParsedCommand command = parser.parse(input);
            if (command.getType() == ParsedCommand.Type.BYE) {
                return ui.getGoodbyeMessage();
            }
            return execute(command);
        } catch (JaylenException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    /**
     * Returns the short welcome message displayed when the GUI opens.
     *
     * @return Jaylen's welcome message
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns the startup storage warning, or {@code null} when loading succeeded.
     *
     * @return the startup storage warning, or {@code null}
     */
    public String getStartupErrorMessage() {
        return startupErrorMessage;
    }

    /**
     * Returns whether a response represents a user-facing error.
     *
     * @param response the formatted response
     * @return {@code true} when the response is an error
     */
    public boolean isErrorResponse(String response) {
        return ui.isErrorMessage(response);
    }

    /** Loads saved tasks without risking replacement of a malformed save file. */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (JaylenException exception) {
            startupErrorMessage = exception.getMessage()
                    + " Your existing data will not be changed. Fix the save file and restart Jaylen.";
            return new TaskList();
        }
    }

    private String execute(ParsedCommand command) {
        assert command != null : "command to execute should not be null";
        assert command.getType() != ParsedCommand.Type.BYE
                : "bye should be handled before command execution";
        return switch (command.getType()) {
            case LIST -> ui.getTasksMessage(tasks);
            case FIND -> ui.getMatchingTasksMessage(tasks, command.getDescription());
            case MARK -> markTask(command.getTaskNumber());
            case UNMARK -> unmarkTask(command.getTaskNumber());
            case DELETE -> deleteTask(command.getTaskNumber());
            case TODO -> addTask(new Todo(command.getDescription()));
            case DEADLINE -> addTask(new Deadline(command.getDescription(),
                    command.getFirstDetail()));
            case EVENT -> addTask(new Event(command.getDescription(), command.getFirstDetail(),
                    command.getSecondDetail()));
            case BYE -> throw new AssertionError("bye is handled before execution");
        };
    }

    private String markTask(int taskNumber) {
        ensureStorageAvailable();
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks(tasks);
        } catch (JaylenException exception) {
            if (!wasDone) {
                task.markAsUndone();
            }
            throw exception;
        }
        return ui.getMarkedMessage(task);
    }

    private String unmarkTask(int taskNumber) {
        ensureStorageAvailable();
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsUndone();
        try {
            saveTasks(tasks);
        } catch (JaylenException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        return ui.getUnmarkedMessage(task);
    }

    private String deleteTask(int taskNumber) {
        ensureStorageAvailable();
        Task removedTask = tasks.remove(taskNumber);
        try {
            saveTasks(tasks);
        } catch (JaylenException exception) {
            tasks.add(taskNumber, removedTask);
            throw exception;
        }
        return ui.getDeletedMessage(removedTask, tasks.size());
    }

    private String addTask(Task task) {
        ensureStorageAvailable();
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (JaylenException exception) {
            tasks.remove(tasks.size());
            throw exception;
        }
        return ui.getTaskAddedMessage(task, tasks.size());
    }

    private void saveTasks(TaskList tasks) {
        storage.save(tasks.getTasks());
    }

    private void ensureStorageAvailable() {
        if (hasStartupError()) {
            throw new JaylenException("I can't change tasks because the save file could not be "
                    + "loaded. Fix the file and restart Jaylen.");
        }
    }

    private boolean hasStartupError() {
        return startupErrorMessage != null;
    }

    /**
     * Program entry point.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        new Jaylen().run();
    }
}
