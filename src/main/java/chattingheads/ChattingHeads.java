package chattingheads;

import chattingheads.command.Command;
import chattingheads.exception.ChattingHeadsException;
import chattingheads.exception.StorageException;
import chattingheads.parser.Parser;
import chattingheads.storage.Storage;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;
import chattingheads.ui.gui.CommandResult;

/**
 * Represents the main application and coordinates its components.
 */
public class ChattingHeads {

    private final Storage storage;
    private final TaskList taskList;
    private final Parser parser;
    private final Ui ui;

    /**
     * Creates the application and initialises its components.
     *
     * @throws StorageException If an existing storage file cannot be read.
     */
    public ChattingHeads() throws StorageException {
        storage = new Storage("tasks.txt");
        taskList = new TaskList(storage);
        parser = new Parser();
        ui = new Ui();
    }

    /**
     * Runs the main command loop until the user exits the application.
     */
    private void run() {
        System.out.println(ui.getStartupMessage());

        while (true) {
            try {
                String input = ui.readCommand();
                Command command = parser.parse(input);

                String response = command.execute(taskList, ui);

                if (command.shouldSave()) {
                    storage.save(taskList);
                }

                System.out.println(response);

                if (command.shouldExit()) {
                    break;
                }
            } catch (ChattingHeadsException e) {
                System.out.println(ui.getErrorMessage(e));
            }
        }
    }

    /**
     * Processes the user input and returns the resulting response for the GUI.
     *
     * @param input User input to process.
     * @return Result containing the response message and command outcome.
     */
    public CommandResult getResponse(String input) {
        try {
            Command command = parser.parse(input);

            String response = command.execute(taskList, ui);

            if (command.shouldSave()) {
                storage.save(taskList);
            }
            return new CommandResult(response, command.shouldExit());
        } catch (ChattingHeadsException e) {
            return new CommandResult(ui.getErrorMessage(e), false);
        }
    }

    public String getStartupMessage() {
        return ui.getStartupMessage();
    }

    /**
     * Starts the application.
     *
     * @param ignoredArgs Command-line arguments, which are not used.
     */
    static void main(String[] ignoredArgs) {
        try {
            new ChattingHeads().run();
        } catch (StorageException e) {
            System.err.println(e.getMessage());
        }
    }
}
