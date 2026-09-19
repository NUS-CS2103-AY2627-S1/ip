package yuno;

import yuno.command.Command;
import yuno.command.CommandResult;
import yuno.exception.FileStorageException;
import yuno.exception.YunoException;
import yuno.parser.Parser;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Starts the Yuno chatbot and coordinates user input with command execution.
 */
public class Yuno {
    /** User interface used to read commands and display messages. */
    private final Ui ui;
    /** Parser used to convert user input into executable commands. */
    private final Parser parser;
    /** Storage used to load and save tasks. */
    private final Storage storage;
    /** Task list managed during the chatbot session. */
    private final TaskList taskList;

    /**
     * Creates a chatbot using the specified user interface and loads saved tasks.
     *
     * @param ui User interface used for the chatbot session.
     * @throws FileStorageException If storage cannot be initialized or saved tasks cannot be loaded.
     */
    public Yuno(Ui ui) throws FileStorageException {
        this(ui, new Parser(), new Storage(), new TaskList());
        storage.load(taskList);
    }

    /**
     * Creates a chatbot with the specified collaborators.
     * This constructor supports isolated testing without changing normal startup behavior.
     *
     * @param ui User interface used for the chatbot session.
     * @param parser Parser used to interpret commands.
     * @param storage Storage used to load and save tasks.
     * @param taskList Task list managed during the session.
     */
    Yuno(Ui ui, Parser parser, Storage storage, TaskList taskList) {
        assert ui != null : "Yuno requires a user interface";
        assert parser != null : "Yuno requires a parser";
        assert storage != null : "Yuno requires storage";
        assert taskList != null : "Yuno requires a task list";
        this.ui = ui;
        this.parser = parser;
        this.storage = storage;
        this.taskList = taskList;
    }

    /**
     * Processes one command and reports whether the chatbot should continue running.
     *
     * @param commandText Command entered by the user.
     * @return Result indicating whether the chatbot should continue accepting commands.
     */
    public CommandResult handleCommand(String commandText) {
        CommandResult commandResult = CommandResult.CONTINUE;
        try {
            Command command = parser.parse(commandText);
            commandResult = command.execute(taskList, ui, storage);
            assert commandResult != null : "Command execution must return a result";
        } catch (YunoException exception) {
            ui.printException(exception.getMessage());
        }
        return commandResult;
    }

    /**
     * Runs the command-processing loop until a command ends the session.
     */
    public void run() {
        CommandResult commandResult = CommandResult.CONTINUE;
        while (commandResult == CommandResult.CONTINUE) {
            String commandText = ui.readCommand();
            commandResult = handleCommand(commandText);
        }
    }

    /**
     * Starts the chatbot and runs it until a command ends the session or storage initialization fails.
     *
     * @param args Command-line arguments, which are not used.
     */
    static void main(String[] args) {
        Ui ui = new Ui();
        ui.printGreeting();
        try {
            new Yuno(ui).run();
        } catch (FileStorageException exception) {
            ui.printException(exception.getMessage());
        }
    }
}
