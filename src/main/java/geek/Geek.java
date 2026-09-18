package geek;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;

import geek.exception.GeekException;
import geek.parser.Parser;
import geek.storage.Storage;
import geek.task.Task;
import geek.task.TaskList;
import geek.ui.MessageFormatter;
import geek.ui.Ui;

/**
 * Coordinates command parsing, task management, persistence, and console output.
 */
public class Geek {
    private static final String DEFAULT_FILE_PATH = "data/geek.txt";

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private boolean hasLoadedTasks;
    private String pendingStartupMessage;

    /**
     * Creates a Geek application using the default task data file.
     */
    public Geek() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a Geek application that stores tasks at the specified path.
     *
     * @param filePath Path to the task data file, either relative or absolute.
     */
    public Geek(String filePath) {
        this.storage = new Storage(Path.of(filePath));
        this.ui = new Ui();
        this.tasks = new TaskList();
        this.hasLoadedTasks = false;
    }

    /**
     * Runs the command loop until the user enters {@code bye}.
     *
     * Invalid input and persistence failures are reported through the UI so that
     * the application can continue when possible.
     */
    public void run() {
        ui.showWelcome();
        loadTasksIfNeeded();
        showPendingStartupMessage();

        boolean isRunning = true;

        while (isRunning) {
            CommandResult result = processInput(ui.readCommand());
            ui.showResponse(result.message());
            isRunning = result.shouldContinue();
        }

        ui.close();
    }

    /**
     * Processes one command and returns the response for a graphical UI.
     *
     * The task list is loaded lazily on the first request so constructing the
     * JavaFX application does not perform file operations before it is ready.
     *
     * @param input Command entered by the user.
     * @return User-facing response to the command.
     */
    public String getResponse(String input) {
        loadTasksIfNeeded();
        CommandResult result = processInput(input);
        String startupMessage = takePendingStartupMessage();

        if (startupMessage == null) {
            return result.message();
        }

        return startupMessage + "\n\n" + result.message();
    }

    private CommandResult processInput(String input) {
        try {
            return execute(Parser.parse(input));
        } catch (GeekException e) {
            return continuingError(e.getMessage());
        } catch (DateTimeParseException e) {
            return continuingError(
                    "Use a supported date or time format, "
                            + "such as 2019-12-02, "
                            + "2/12/2019 1800, or "
                            + "Dec 2 2019 6:00 PM."
            );
        }
    }

    private CommandResult execute(Parser.Command command) {
        assert command != null : "Parser must return a command.";
        assert command.type() != null
                : "A parsed command must have a type.";

        return switch (command.type()) {
            case BYE -> new CommandResult(
                    MessageFormatter.goodbye(),
                    false
            );
            case LIST -> continuingResponse(
                    MessageFormatter.taskList(tasks.getTasks())
            );
            case SORT -> {
                tasks.sortChronologically();

                yield continuingSavedResponse(
                        MessageFormatter.tasksSorted(
                                tasks.getTasks()
                        )
                );
            }
            case FIND -> {
                assert command.keyword() != null
                        : "A find command must have a keyword.";

                yield continuingResponse(
                        MessageFormatter.matchingTasks(
                                tasks.find(command.keyword())
                        )
                );
            }
            case ON -> {
                assert command.date() != null
                        : "An on command must have a date.";

                yield continuingResponse(
                        MessageFormatter.tasksOnDate(
                            command.date(),
                            tasks.findOn(command.date())
                        )
                );
            }
            case MARK -> {
                Task task = tasks.mark(command.taskNumber());

                yield continuingSavedResponse(
                        MessageFormatter.taskMarked(task)
                );
            }
            case UNMARK -> {
                Task task = tasks.unmark(command.taskNumber());

                yield continuingSavedResponse(
                        MessageFormatter.taskUnmarked(task)
                );
            }
            case DELETE -> {
                Task task = tasks.delete(command.taskNumber());

                yield continuingSavedResponse(
                        MessageFormatter.taskDeleted(
                                task,
                                tasks.size()
                        )
                );
            }
            case ADD -> {
                Task task = command.task();
                assert task != null
                        : "An add command must have a task.";

                tasks.add(task);

                yield continuingSavedResponse(
                        MessageFormatter.taskAdded(
                                task,
                                tasks.size()
                        )
                );
            }
        };
    }

    private CommandResult continuingResponse(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult continuingError(String message) {
        return continuingResponse(MessageFormatter.error(message));
    }

    private CommandResult continuingSavedResponse(
            String successMessage
    ) {
        try {
            storage.saveTasks(tasks.getTasks());
            return continuingResponse(successMessage);
        } catch (IOException e) {
            return continuingResponse(
                    successMessage + "\n\n"
                            + MessageFormatter.error(
                                    "I could not save the tasks."
                            )
            );
        }
    }

    private void loadTasksIfNeeded() {
        if (hasLoadedTasks) {
            return;
        }

        hasLoadedTasks = true;

        try {
            Storage.LoadResult result = storage.loadTasks();
            tasks = new TaskList(result.tasks());

            if (!result.corruptedLineNumbers().isEmpty()) {
                String lineNumbers = result.corruptedLineNumbers()
                        .stream()
                        .map(String::valueOf)
                        .reduce((first, second) -> first + ", " + second)
                        .orElse("");

                pendingStartupMessage = "Warning: Skipped corrupted "
                        + "saved tasks on lines " + lineNumbers + ".";
            }
        } catch (IOException e) {
            tasks = new TaskList();
            pendingStartupMessage = MessageFormatter.error(
                    "I could not load the saved tasks."
            );
        }
    }

    private void showPendingStartupMessage() {
        String startupMessage = takePendingStartupMessage();

        if (startupMessage != null) {
            ui.showResponse(startupMessage);
        }
    }

    private String takePendingStartupMessage() {
        String startupMessage = pendingStartupMessage;
        pendingStartupMessage = null;
        return startupMessage;
    }

    /**
     * Starts Geek using {@code data/geek.txt} as its task data file.
     *
     * @param args Command-line arguments; they are not used.
     */
    public static void main(String[] args) {
        new Geek().run();
    }

    private record CommandResult(
            String message,
            boolean shouldContinue
    ) {
    }
}
