package yapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates command parsing, task operations, persistence, and the console UI.
 */
public class Yapper {
    /** Default save file relative to the working directory. */
    private static final Path STORAGE_PATH = Path.of("data", "yapper.txt");

    /** Per-instance response buffer, independent of global standard output. */
    private final ByteArrayOutputStream response = new ByteArrayOutputStream();
    /** Whether initialization has already been attempted. */
    private boolean initialized;
    /** Persistent startup error; commands are blocked if loading failed. */
    private String loadingError;
    /** Whether the user has requested application exit. */
    private boolean exitRequested;

    /** Console input and output handler. */
    private final Ui ui;
    /** Command and argument parser. */
    private final Parser parser;
    /** Persistence handler for the configured save file. */
    private final Storage storage;
    /** Save file path included in startup error messages. */
    private final Path storagePath;
    /** Working task list, initialized after a successful load. */
    private TaskList tasks;

    /**
     * Creates an application that stores tasks at the supplied path.
     *
     * @param storagePath File used to load and save tasks.
     */
    public Yapper(Path storagePath) {
        this.storagePath = storagePath;
        ui = new Ui(new PrintStream(response, true, StandardCharsets.UTF_8));
        parser = new Parser();
        storage = new Storage(storagePath);
    }

    /**
     * Creates an application using the default save file.
     */
    public Yapper() {
        this(STORAGE_PATH);
    }

    /**
     * Loads saved tasks once, retaining errors so damaged saves cannot be overwritten.
     */
    private void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | YapperException exception) {
            loadingError = exception.getMessage();
        }
        assert (tasks != null) != (loadingError != null) : "Loading must produce tasks or an error";
    }

    /**
     * Returns the greeting and any startup error for a console or graphical view.
     *
     * @return Welcome text, including a load failure when applicable.
     */
    public String getWelcome() {
        response.reset();
        ui.showWelcome();
        initialize();
        if (loadingError != null) {
            ui.showLoadingError(loadingError, storagePath);
        }
        return response.toString(StandardCharsets.UTF_8);
    }

    /**
     * Processes one command without reading console input or redirecting global output.
     * Failed saves restore both task order and completion status.
     *
     * @param input Command supplied by the user.
     * @return Response text ready for display.
     */
    public String getResponse(String input) {
        response.reset();
        initialize();
        String command = input.trim();
        CommandType commandType = parser.parseCommandType(command);
        if (exitRequested || commandType == CommandType.BYE) {
            exitRequested = true;
            ui.showGoodbye();
        } else if (loadingError != null) {
            ui.showLoadingError(loadingError, storagePath);
        } else {
            List<Task> previousTasks = tasks.asList();
            List<Boolean> previousStatus = new ArrayList<>();
            for (Task task : previousTasks) {
                previousStatus.add("X".equals(task.getStatusIcon()));
            }
            try {
                execute(commandType, command);
            } catch (YapperException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException exception) {
                assert previousTasks.size() == previousStatus.size() : "Rollback snapshot must stay aligned";
                tasks = new TaskList(previousTasks);
                for (int i = 0; i < previousStatus.size(); i++) {
                    if (previousStatus.get(i)) {
                        tasks.mark(i);
                    } else {
                        tasks.unmark(i);
                    }
                }
                ui.showSavingError(exception.getMessage());
            }
            ui.showSeparator();
        }
        return response.toString(StandardCharsets.UTF_8);
    }

    /**
     * Returns whether bye has been requested.
     *
     * @return Whether the view should stop accepting commands.
     */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /**
     * Loads saved tasks and processes console commands until bye or the end of input.
     */
    public void run() {
        System.out.print(getWelcome());
        if (loadingError != null) {
            return;
        }
        while (!exitRequested && ui.hasNextCommand()) {
            System.out.print(getResponse(ui.readCommand()));
        }
        if (!exitRequested) {
            System.out.print(getResponse("bye"));
        }
    }

    /**
     * Dispatches one command, saving changes before displaying their confirmation.
     *
     * @param commandType Recognized command type.
     * @param command Trimmed command line including its arguments.
     * @throws YapperException If the command or its arguments are invalid.
     * @throws IOException If saving fails; the caller restores the previous state.
     * @throws AssertionError If BYE reaches dispatch instead of being handled by the input loop.
     */
    private void execute(CommandType commandType, String command) throws YapperException, IOException {
        assert tasks != null : "Commands require a successfully loaded task list";
        assert commandType != CommandType.BYE : "Exit is handled before command dispatch";
        switch (commandType) {
            case LIST -> ui.printTaskList(tasks.asList());
            case STATS -> ui.printStatistics(tasks.asList());
            case HELP -> ui.printHelp();
            case MARK -> updateTaskStatus(command, true);
            case UNMARK -> updateTaskStatus(command, false);
            case DELETE -> {
                Task task = tasks.delete(parser.parseTaskIndex(command, "delete", tasks.size()));
                storage.save(tasks.asList());
                ui.printTaskDeleted(task, tasks.size());
            }
            case TODO -> addTask(parser.parseTodo(command));
            case DEADLINE -> addTask(parser.parseDeadline(command));
            case EVENT -> addTask(parser.parseEvent(command));
            case ON -> ui.printTasksOnDate(parser.parseDate(command), tasks);
            case FIND -> ui.printMatchingTasks(parser.parseKeyword(command), tasks);
            case UNKNOWN -> throw new YapperException("Please precede a task with a command to add it. "
                    + "Otherwise, type 'help' for a list of commands.");
            case BYE -> throw new AssertionError("The bye command should be handled before dispatch.");
            default -> throw new AssertionError("Unhandled command type: " + commandType);
        }
    }

    /**
     * Applies either completion command through the same validation and save sequence.
     *
     * @param command Full mark or unmark command.
     * @param isDone Requested completion state.
     * @throws YapperException If the task number is invalid.
     * @throws IOException If saving fails; the caller restores the previous state.
     */
    private void updateTaskStatus(String command, boolean isDone) throws YapperException, IOException {
        String keyword = isDone ? "mark" : "unmark";
        int index = parser.parseTaskIndex(command, keyword, tasks.size());
        Task task = isDone ? tasks.mark(index) : tasks.unmark(index);
        storage.save(tasks.asList());
        ui.showTaskStatus(task, isDone);
    }

    /**
     * Adds and persists a parsed task before showing its confirmation.
     *
     * @param task Parsed task to append.
     * @throws IOException If saving fails; the caller restores the previous state.
     */
    private void addTask(Task task) throws IOException {
        tasks.add(task);
        storage.save(tasks.asList());
        ui.printTaskAdded(task, tasks.size());
    }

    /**
     * Starts Yapper using its default relative storage path.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Yapper(STORAGE_PATH).run();
    }
}
