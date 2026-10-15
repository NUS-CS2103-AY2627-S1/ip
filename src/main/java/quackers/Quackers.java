package quackers;

import java.nio.file.Path;

/**
 * Coordinates command parsing, task operations, storage, and user interaction.
 *
 * <p>This class is front-end agnostic: {@link #run()} drives a console session, while
 * {@link #getResponse(String)} answers a single command and hands the reply back to a caller
 * such as the JavaFX GUI.
 */
public class Quackers {
    /** Default location of the task data file, relative to the working directory. */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "quackers.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String loadingError;
    private boolean isExitRequested;

    /**
     * Creates Quackers using the specified file for persistent task data.
     *
     * @param filePath the task data file.
     */
    public Quackers(Path filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();

        TaskList loadedTasks;
        String errorMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (QuackersException error) {
            loadedTasks = new TaskList();
            errorMessage = error.getMessage();
        }
        this.tasks = loadedTasks;
        this.loadingError = errorMessage;
    }

    /**
     * Creates Quackers using the default task data file.
     */
    public Quackers() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Runs the console command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.show(ui.getErrorMessage(loadingError));
        }

        try {
            while (!isExitRequested && ui.hasNextCommand()) {
                String command = ui.readCommand();
                ui.showLine();
                ui.show(getResponse(command));
                ui.showLine();
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Answers a single command.
     *
     * <p>Unlike {@link #executeCommand(String)}, this never throws: an invalid command is
     * reported as an ordinary reply, because both front ends want to show the problem to the
     * user and carry on.
     *
     * @param command the complete command entered by the user.
     * @return the reply to show to the user.
     */
    public String getResponse(String command) {
        try {
            return executeCommand(command);
        } catch (QuackersException error) {
            return ui.getErrorMessage(error.getMessage());
        }
    }

    /**
     * Returns the greeting to show when a session starts.
     *
     * @return the greeting text.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns the problem encountered while loading saved tasks, if any.
     *
     * @return the error text, or {@code null} when the tasks loaded cleanly.
     */
    public String getLoadingError() {
        return loadingError;
    }

    /**
     * Returns whether the user has asked to exit (i.e., entered {@code bye}).
     *
     * @return {@code true} once an exit has been requested.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Starts Quackers with its default relative task-file path.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Quackers().run();
    }

    /**
     * Executes one command and returns the reply to show.
     *
     * @param command the complete command entered by the user.
     * @return the reply to show to the user.
     * @throws QuackersException if the command is invalid or a save fails.
     */
    private String executeCommand(String command) throws QuackersException {
        CommandType commandType = Parser.parseCommandType(command);
        switch (commandType) {
            case BYE:
                isExitRequested = true;
                return ui.getGoodbyeMessage();
            case LIST:
                return ui.getTasksMessage(tasks.getTasks());
            case FIND:
                return ui.getMatchingTasksMessage(tasks.find(Parser.parseFindKeyword(command)));
            case MARK:
                return updateTaskStatus(command, "mark", true);
            case UNMARK:
                return updateTaskStatus(command, "unmark", false);
            case DELETE:
                return deleteTask(command);
            case TODO:
                return addTask(Parser.parseTodo(command));
            case NOTE:
                return addTask(Parser.parseNote(command));
            case DEADLINE:
                return addTask(Parser.parseDeadline(command));
            case EVENT:
                return addTask(Parser.parseEvent(command));
            default:
                throw new QuackersException("Quack? I don't know what that means :-(");
        }
    }

    private String updateTaskStatus(String command, String keyword, boolean isDone)
            throws QuackersException {
        int taskIndex = Parser.parseTaskIndex(command, keyword);
        Task task = tasks.updateStatus(taskIndex, isDone);
        storage.save(tasks.getTasks());
        return ui.getTaskStatusChangedMessage(task, isDone);
    }

    private String deleteTask(String command) throws QuackersException {
        int taskIndex = Parser.parseTaskIndex(command, "delete");
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks.getTasks());
        return ui.getTaskDeletedMessage(removedTask, tasks.size());
    }

    private String addTask(Task task) throws QuackersException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.getTaskAddedMessage(task, tasks.size());
    }
}
