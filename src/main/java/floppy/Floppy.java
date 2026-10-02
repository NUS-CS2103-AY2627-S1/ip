package floppy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Coordinates command parsing, task operations, storage, and user interaction.
 */
public class Floppy {
    private static final String DATA_FILE_PATH = "data/floppy.txt";

    private final Parser parser;
    private final Storage storage;
    private final TaskList taskList;
    private final Ui ui;

    /**
     * Creates a chatbot backed by the default task data file.
     */
    public Floppy() {
        this(DATA_FILE_PATH);
    }

    Floppy(String dataFilePath) {
        parser = new Parser();
        storage = new Storage(dataFilePath);
        ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (FloppyException exception) {
            ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        taskList = loadedTasks;
    }

    /**
     * Runs the chatbot until the user exits or closes the input stream.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String commandText = ui.readCommand();
            ui.showLine();
            if (!processCommand(commandText, ui)) {
                ui.showLine();
                break;
            }
            ui.showLine();
        }
        ui.close();
    }

    /**
     * Starts the chatbot application.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Floppy().run();
    }

    /**
     * Processes one command and returns the response for presentation in the GUI.
     *
     * @param input Command entered by the user.
     * @return Response generated for the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBytes, true, StandardCharsets.UTF_8);
                Ui responseUi = new Ui(new Scanner(""), responseOutput)) {
            processCommand(input, responseUi);
        }
        return responseBytes.toString(StandardCharsets.UTF_8).strip();
    }

    /**
     * Returns the greeting displayed when the GUI opens.
     *
     * @return Initial chatbot greeting.
     */
    public String getWelcomeMessage() {
        return Ui.getWelcomeMessage();
    }

    /**
     * Parses and executes one command using the supplied output interface.
     *
     * @param commandText Raw command entered by the user.
     * @param responseUi User interface that receives the command response.
     * @return {@code false} for an exit command, or {@code true} otherwise.
     */
    private boolean processCommand(String commandText, Ui responseUi) {
        try {
            Command command = parser.parse(commandText);
            if (command.getCommandType() == CommandType.BYE) {
                responseUi.showGoodbye();
                return false;
            }
            execute(command, responseUi);
        } catch (FloppyException exception) {
            responseUi.showError(exception.getMessage());
        }
        return true;
    }

    /**
     * Executes a parsed command and persists every task-list change.
     *
     * @param command Parsed command to execute.
     * @param responseUi User interface that receives the command response.
     * @throws FloppyException If the task operation or save fails.
     */
    private void execute(Command command, Ui responseUi) throws FloppyException {
        assert command != null : "A parsed command must be supplied for execution";
        assert responseUi != null : "Command output requires a user interface";

        switch (command.getCommandType()) {
            case LIST -> responseUi.showTaskList(taskList.getTasks());
            case MARK -> {
                Task task = taskList.mark(command.getTaskIndex());
                storage.save(taskList.getTasks());
                responseUi.showTaskMarked(task);
            }
            case UNMARK -> {
                Task task = taskList.unmark(command.getTaskIndex());
                storage.save(taskList.getTasks());
                responseUi.showTaskUnmarked(task);
            }
            case DELETE -> {
                Task task = taskList.delete(command.getTaskIndex());
                storage.save(taskList.getTasks());
                responseUi.showTaskDeleted(task, taskList.size());
            }
            case FIND -> responseUi.showMatchingTasks(taskList.find(command.getKeyword()));
            case ADD -> {
                Task task = command.getTask();
                taskList.add(task);
                storage.save(taskList.getTasks());
                responseUi.showTaskAdded(task, taskList.size());
            }
            case BYE -> throw new IllegalStateException("Exit commands are handled by the run loop.");
            default -> throw new IllegalStateException("Unsupported command type.");
        }
    }
}
