package shrekanddonkey;

import java.io.IOException;

import shrekanddonkey.command.Command;
import shrekanddonkey.command.DeadlineCommand;
import shrekanddonkey.command.DeleteCommand;
import shrekanddonkey.command.EventCommand;
import shrekanddonkey.command.ExitCommand;
import shrekanddonkey.command.FindCommand;
import shrekanddonkey.command.ListCommand;
import shrekanddonkey.command.MarkCommand;
import shrekanddonkey.command.SortCommand;
import shrekanddonkey.command.TodoCommand;
import shrekanddonkey.command.UnmarkCommand;
import shrekanddonkey.parser.Parser;
import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Runs the ShrekAndDonkey chatbot and manages the user's task list.
 */
public class ShrekAndDonkey {
    private final Storage storage;
    private final TaskList taskList;
    private final Ui ui;

    /**
     * Creates the chatbot application with default storage location.
     */
    public ShrekAndDonkey() {
        this(new Storage());
    }

    /**
     * Creates the chatbot application with the specified storage file path.
     *
     * @param filePath path to the file used for task persistence.
     */
    public ShrekAndDonkey(String filePath) {
        this(new Storage(filePath));
    }

    /**
     * Creates the chatbot application using the supplied storage.
     *
     * @param storage storage used for task persistence.
     */
    private ShrekAndDonkey(Storage storage) {
        this.ui = new Ui();
        this.storage = storage;
        try {
            this.taskList = new TaskList(storage.load());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize the task file.", exception);
        }
        assert this.ui != null : "Ui must be initialized";
        assert this.storage != null : "Storage must be initialized";
        assert this.taskList != null : "TaskList must be initialized";
    }

    /**
     * Returns the chatbot welcome message for display.
     *
     * @return the welcome message string.
     */
    public String getWelcomeMessage() {
        return "Grrr GET OUT OF MY SWAMP! Ohh, I didn't see you there stranger! I'm ShrekAndDonkey.\n"
                + "What can I do for you?";
    }

    /**
     * Generates a response for the user's chat message by parsing and executing the command.
     *
     * @param input message entered by the user.
     * @return response string from the chatbot.
     */
    public String getResponse(String input) {
        assert input != null : "User input cannot be null";
        ui.clearOutput();
        Command command = createCommand(input);
        if (command == null) {
            ui.showError("What are ye doing in my swamp?! Try: list, todo, deadline,"
                    + " event, mark, unmark, delete, find, sort, bye");
        } else {
            command.execute(taskList, ui, storage);
            if (command.modifiesTasks() && !ui.isError()) {
                saveTasks();
            }
        }
        String response = ui.getRecordedOutput();
        assert response != null : "Chatbot response cannot be null";
        return response;
    }

    /**
     * Creates the command matching the supplied user input.
     *
     * @param input message entered by the user.
     * @return command to execute, or {@code null} when the input is unsupported.
     */
    private Command createCommand(String input) {
        Parser.CommandType commandType = Parser.parseCommandType(input);
        if (commandType == Parser.CommandType.EXIT) {
            return new ExitCommand();
        } else if (commandType == Parser.CommandType.LIST) {
            return new ListCommand();
        } else if (commandType == Parser.CommandType.MARK) {
            return new MarkCommand(Parser.getArguments(input, "mark"));
        } else if (commandType == Parser.CommandType.UNMARK) {
            return new UnmarkCommand(Parser.getArguments(input, "unmark"));
        } else if (commandType == Parser.CommandType.TODO) {
            return new TodoCommand(Parser.getArguments(input, "todo"));
        } else if (commandType == Parser.CommandType.DEADLINE) {
            return new DeadlineCommand(Parser.getArguments(input, "deadline"));
        } else if (commandType == Parser.CommandType.EVENT) {
            return new EventCommand(Parser.getArguments(input, "event"));
        } else if (commandType == Parser.CommandType.DELETE) {
            return new DeleteCommand(Parser.getArguments(input, "delete"));
        } else if (commandType == Parser.CommandType.FIND) {
            return new FindCommand(Parser.getArguments(input, "find"));
        } else if (commandType == Parser.CommandType.SORT) {
            return new SortCommand();
        }
        return null;
    }

    /**
     * Saves the task list after a successful state-changing command.
     */
    private void saveTasks() {
        try {
            storage.save(taskList.getTasks());
        } catch (IOException exception) {
            ui.showError("Unable to save the task file.");
        }
    }

    /**
     * Returns whether the last processed command produced an error.
     *
     * @return {@code true} if the last response is an error.
     */
    public boolean isLastResponseError() {
        return ui.isError();
    }

    /**
     * Returns whether the given input is an exit command.
     *
     * @param input message entered by the user.
     * @return {@code true} if the input is a bye/exit command.
     */
    public boolean isExit(String input) {
        return Parser.parseCommandType(input) == Parser.CommandType.EXIT;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        ShrekAndDonkey chatbot = new ShrekAndDonkey();
        chatbot.runConsole();
    }

    /**
     * Processes console input by delegating each command to the shared response handler.
     */
    private void runConsole() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            boolean shouldExit = isExit(input);
            getResponse(input);
            if (shouldExit) {
                break;
            }
        }
    }
}
