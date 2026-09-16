package anaconda;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import anaconda.exception.AnacondaException;
import anaconda.parser.Command;
import anaconda.parser.Parser;
import anaconda.storage.Storage;
import anaconda.task.Task;
import anaconda.task.TaskList;
import anaconda.ui.Ui;

/**
 * Coordinates user interaction, command parsing, task operations, and storage.
 */
public class Anaconda {
    private static final Path DATA_FILE = Path.of("data", "anaconda.txt");

    /**
     * Describes successful input, an added duplicate, invalid input, or an execution error.
     */
    public enum ResponseStatus {
        SUCCESS, DUPLICATE, WARNING, ERROR
    }

    /**
     * Carries response text and its presentation status without exposing GUI styling to command handlers.
     *
     * @param text Response to display.
     * @param status Outcome of processing this input.
     */
    public record CommandResponse(String text, ResponseStatus status) {
    }

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private boolean hasLoadingError;
    /**
     * States before successfully saved mutations, most recent first; history lasts for this session.
     */
    private final Deque<TaskList.Snapshot> undoHistory = new ArrayDeque<>();
    /**
     * States reversed by undo, most recent first; another command ends the current undo chain.
     */
    private final Deque<TaskList.Snapshot> redoHistory = new ArrayDeque<>();

    /**
     * Creates the chatbot using the default relative data path.
     */
    public Anaconda() {
        this(DATA_FILE);
    }

    /**
     * Creates the chatbot and loads its existing tasks.
     *
     * @param filePath Path to the task data file.
     */
    public Anaconda(Path filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    public boolean hasLoadingError() {
        return hasLoadingError;
    }

    /**
     * Starts the chatbot using the default relative data path.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Anaconda(DATA_FILE).run();
    }

    /**
     * Accepts commands until the user enters bye.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            if (parser.isExitCommand(input)) {
                redoHistory.clear();
                break;
            }

            ui.showLine();
            try {
                handleCommand(input, ui);
            } catch (AnacondaException exception) {
                showCommandError(exception, ui);
            }
            ui.showLine();
        }

        ui.close();
        ui.showGoodbye();
    }

    /**
     * Processes one GUI command and returns the same response text used by the console interface.
     * Captures only this response without changing JVM-wide streams.
     *
     * @param input Complete user input.
     * @return Response to display in the GUI.
     */
    public String getResponse(String input) {
        return getCommandResponse(input).text();
    }

    /**
     * Processes input once and returns its text and status.
     *
     * @param input Complete user input.
     * @return Response whose status reflects validation and command execution.
     */
    public CommandResponse getCommandResponse(String input) {
        if (parser.isExitCommand(input)) {
            redoHistory.clear();
            return new CommandResponse("Alright, off you go. Try to get something done.", ResponseStatus.SUCCESS);
        }

        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        ResponseStatus status;
        try (PrintStream responseOutput = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8);
                Ui responseUi = new Ui(InputStream.nullInputStream(), responseOutput)) {
            status = processGuiInput(input, responseUi);
        }
        return new CommandResponse(responseBuffer.toString(StandardCharsets.UTF_8).stripTrailing(), status);
    }

    /**
     * Processes a GUI command using the existing command handlers.
     */
    private ResponseStatus processGuiInput(String input, Ui responseUi) {
        try {
            return handleCommand(input, responseUi);
        } catch (AnacondaException exception) {
            showCommandError(exception, responseUi);
            return switch (exception.getReason()) {
                case INVALID_INPUT -> ResponseStatus.WARNING;
                case UNKNOWN_COMMAND, STORAGE_ERROR -> ResponseStatus.ERROR;
            };
        }
    }

    /**
     * Displays an error and helps users discover commands when their input is unrecognized.
     */
    private void showCommandError(AnacondaException exception, Ui responseUi) {
        String message = exception.getMessage();
        if (exception.getReason() == AnacondaException.Reason.UNKNOWN_COMMAND) {
            message += System.lineSeparator() + "You can ask for help.";
        }
        responseUi.showError(message);
    }

    /**
     * Dispatches a parsed command to the task list, storage, and user interface.
     *
     * @param input Complete user input.
     * @param responseUi Destination for this command's messages.
     * @return Outcome of the saved change or completed command.
     * @throws AnacondaException If the command is invalid or saving fails.
     */
    private ResponseStatus handleCommand(String input, Ui responseUi) throws AnacondaException {
        Parser.ParsedCommand parsedCommand = parseAndUpdateUndoChain(input);
        try {
            return executeCommand(parsedCommand, responseUi);
        } catch (AnacondaException exception) {
            String guidance = getInputGuidance(parsedCommand.command());
            if (exception.getReason() != AnacondaException.Reason.INVALID_INPUT || guidance.isEmpty()) {
                throw exception;
            }
            throw new AnacondaException(exception.getMessage() + System.lineSeparator() + guidance,
                    exception.getReason());
        }
    }

    /**
     * Returns syntax and an example for commands that accept user-specified details.
     */
    private String getInputGuidance(Command command) {
        String line = System.lineSeparator();
        String dateGuidance = line + "Dates: yyyy-MM-dd or dd-MM-yyyy.";
        return switch (command) {
            case TODO -> "Format: todo DESCRIPTION" + line + "Example: todo read book";
            case DEADLINE -> "Format: deadline DESCRIPTION /by DATE" + line
                    + "Example: deadline report /by 2026-09-20" + dateGuidance;
            case EVENT -> "Format: event DESCRIPTION /from START_DATE /to END_DATE" + line
                    + "Example: event meeting /from 2026-09-20 /to 2026-09-21" + dateGuidance;
            case MARK -> "Format: mark TASK_NUMBER" + line + "Example: mark 1" + line
                    + "Use a task number from list.";
            case UNMARK -> "Format: unmark TASK_NUMBER" + line + "Example: unmark 1" + line
                    + "Use a task number from list.";
            case DELETE -> "Format: delete TASK_NUMBER" + line + "Example: delete 1" + line
                    + "Use a task number from list.";
            case FIND -> "Format: find KEYWORD" + line + "Example: find book";
            case BY -> "Format: /by DATE [sharp]" + line + "Example: /by 2026-09-20" + dateGuidance
                    + line + "Add sharp to match only that exact date.";
            case FROM -> "Format: /from DATE [sharp]" + line + "Example: /from 2026-09-20" + dateGuidance
                    + line + "Add sharp to match only that exact date.";
            default -> "";
        };
    }

    /**
     * Executes a recognized command, preserving validation and persistence failures for the caller.
     */
    private ResponseStatus executeCommand(Parser.ParsedCommand parsedCommand, Ui responseUi) throws AnacondaException {
        Command command = parsedCommand.command();
        String arguments = parsedCommand.arguments();

        switch (command) {
            case LIST -> responseUi.showTasks(tasks.asList(), false);
            case HELP -> responseUi.showCommandList();
            case MARK, UNMARK -> changeTaskStatus(arguments, command == Command.MARK, responseUi);
            case DELETE -> deleteTask(arguments, responseUi);
            case UNDO -> {
                if (arguments.isEmpty()) {
                    undoTaskChange(responseUi);
                } else {
                    redoTaskChange(responseUi);
                }
            }
            case CLEAR -> clearTasks(responseUi);
            case FIND -> findTasks(arguments, responseUi);
            case TODO, DEADLINE, EVENT -> {
                return addTask(command, arguments, responseUi);
            }
            case BY, FROM -> filterTasksByDate(command, arguments, responseUi);
            case BYE -> {
                // Console and GUI entry points handle standalone bye commands before dispatch.
            }
            default -> throw new IllegalStateException("Unsupported command: " + command);
        }
        return ResponseStatus.SUCCESS;
    }

    /**
     * Ends the undo chain on any input other than a valid undo or undo undo command.
     * Ordinary undo history remains available after the chain ends.
     */
    private Parser.ParsedCommand parseAndUpdateUndoChain(String input) throws AnacondaException {
        try {
            Parser.ParsedCommand parsedCommand = parser.parse(input);
            if (parsedCommand.command() != Command.UNDO) {
                redoHistory.clear();
            }
            return parsedCommand;
        } catch (AnacondaException exception) {
            redoHistory.clear();
            throw exception;
        }
    }

    /**
     * Updates a task's completion state and reports success only after saving.
     */
    private void changeTaskStatus(String arguments, boolean isDone, Ui responseUi) throws AnacondaException {
        TaskList.Snapshot previousState = tasks.snapshot();
        Task task = tasks.mark(parser.parseTaskNumber(arguments), isDone);
        saveChange(previousState);
        responseUi.showMarked(task, isDone);
    }

    /**
     * Removes the selected task and reports success only after saving.
     */
    private void deleteTask(String arguments, Ui responseUi) throws AnacondaException {
        TaskList.Snapshot previousState = tasks.snapshot();
        Task removedTask = tasks.delete(parser.parseTaskNumber(arguments));
        saveChange(previousState);
        responseUi.showTaskRemoved(removedTask, tasks.size());
    }

    /**
     * Creates and saves a task, then warns about duplicates while retaining the addition for undo.
     */
    private ResponseStatus addTask(Command command, String arguments, Ui responseUi) throws AnacondaException {
        Task task = parser.parseTask(command, arguments);
        boolean isDuplicate = tasks.hasDuplicate(task);
        TaskList.Snapshot previousState = tasks.snapshot();
        tasks.add(task);
        saveChange(previousState);
        responseUi.showTaskAdded(task, tasks.size());
        if (isDuplicate) {
            responseUi.showDuplicateWarning();
            return ResponseStatus.DUPLICATE;
        }
        return ResponseStatus.SUCCESS;
    }

    /**
     * Displays tasks matching a validated description keyword.
     */
    private void findTasks(String arguments, Ui responseUi) throws AnacondaException {
        String keyword = parser.parseKeyword(arguments);
        responseUi.showFindResults(tasks.find(keyword));
    }

    /**
     * Displays tasks matching a validated date filter.
     */
    private void filterTasksByDate(Command command, String arguments, Ui responseUi) throws AnacondaException {
        Parser.DateFilter filter = parser.parseDateFilter(arguments, command);
        responseUi.showTasks(tasks.filterByDate(filter.date(), command, filter.isSharp()), true);
    }

    /**
     * Clears and saves tasks immediately, retaining a snapshot for undo or save-failure rollback.
     */
    private void clearTasks(Ui responseUi) throws AnacondaException {
        TaskList.Snapshot previousState = tasks.snapshot();
        tasks.clear();
        saveChange(previousState);
        responseUi.showCleared();
    }

    /**
     * Restores the most recent saved mutation, retaining history if the restored state cannot be saved.
     */
    private void undoTaskChange(Ui responseUi) throws AnacondaException {
        if (undoHistory.isEmpty()) {
            throw new AnacondaException("There is nothing to undo.");
        }
        TaskList.Snapshot currentState = tasks.snapshot();
        tasks.restore(undoHistory.peek());
        saveOrRestore(currentState);
        undoHistory.pop();
        redoHistory.push(currentState);
        responseUi.showUndo();
        responseUi.showTasks(tasks.asList(), false);
    }

    /**
     * Reverses the most recent undo in this chain, moving history only after saving succeeds.
     */
    private void redoTaskChange(Ui responseUi) throws AnacondaException {
        if (redoHistory.isEmpty()) {
            throw new AnacondaException("There is no undo to reverse.");
        }
        TaskList.Snapshot currentState = tasks.snapshot();
        tasks.restore(redoHistory.peek());
        saveOrRestore(currentState);
        redoHistory.pop();
        undoHistory.push(currentState);
        responseUi.showRedo();
        responseUi.showTasks(tasks.asList(), false);
    }

    /**
     * Saves a mutation before making its previous state available for undo.
     */
    private void saveChange(TaskList.Snapshot previousState) throws AnacondaException {
        saveOrRestore(previousState);
        undoHistory.push(previousState);
    }

    /**
     * Loads saved tasks, reporting file-reading errors before starting with an empty list.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException exception) {
            hasLoadingError = true;
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /**
     * Saves the current list, restoring its previous in-memory state if writing fails.
     */
    private void saveOrRestore(TaskList.Snapshot previousState) throws AnacondaException {
        try {
            storage.saveTasks(tasks.asList());
        } catch (IOException exception) {
            tasks.restore(previousState);
            throw new AnacondaException("I couldn't save your task list.", AnacondaException.Reason.STORAGE_ERROR);
        }
    }
}
