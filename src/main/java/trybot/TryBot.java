package trybot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import trybot.command.Command;
import trybot.exception.TryBotException;
import trybot.parser.Parser;
import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.ChatReply;
import trybot.ui.ChatUi;
import trybot.ui.Ui;

/**
 * Coordinates TryBot's user interface, parser, task list, and storage.
 */
public class TryBot {
    private static final String CONSOLE_SEPARATOR = "____________________________________________________________";
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    /**
     * Creates TryBot using the default task data file.
     */
    public TryBot() {
        this("data/trybot.txt");
    }

    /**
     * Creates TryBot using the specified task data file.
     *
     * @param filePath path to the task data file.
     */
    public TryBot(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (IOException | RuntimeException exception) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        // Both loading paths must initialise the task list before the application can run.
        assert loadedTasks != null;
        tasks = loadedTasks;
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasMoreCommands()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            Command command = executeCommand(fullCommand, ui);
            if (command != null && command.isExit()) {
                break;
            }
        }
    }

    /**
     * Processes one command for a graphical client and returns TryBot's reply.
     *
     * @param input command entered by the user.
     * @return response text without the console separator.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui responseUi = new Ui(new PrintStream(output), new PrintStream(output));
        executeCommand(input, responseUi);
        String response = output.toString().replace("\r\n", "\n").trim();
        int separatorIndex = response.lastIndexOf(CONSOLE_SEPARATOR);
        return separatorIndex < 0 ? response : response.substring(0, separatorIndex).trim();
    }

    /**
     * Executes one graphical command with friendly text and explicit error and exit states.
     *
     * @param input command entered by the user.
     * @return reply for the conversation view.
     */
    public ChatReply getChatReply(String input) {
        ChatUi responseUi = new ChatUi();
        Command command = executeCommand(input, responseUi);
        return new ChatReply(responseUi.getText(), responseUi.hasError(), responseUi.hasWarning(),
                command != null && command.isExit());
    }

    private Command executeCommand(String input, Ui targetUi) {
        Command command = null;
        try {
            command = parser.parse(input);
            // Parser.parse returns an executable command for every input, including invalid input.
            assert command != null;
            command.execute(tasks, targetUi, storage);
        } catch (TryBotException exception) {
            targetUi.showError(exception.getMessage());
        } finally {
            targetUi.showLine();
        }
        return command;
    }

    /**
     * Starts TryBot with its default task file.
     *
     * @param args command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        new TryBot().run();
    }
}
