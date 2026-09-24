package dobby;

import dobby.command.Command;
import dobby.logic.DobbyLogic;
import dobby.parser.Parser;
import dobby.ui.cli.Ui;

/**
 * Starts the Dobby chatbot application.
 */
public class Dobby {
    private final DobbyLogic logic;

    /** Creates a Dobby instance backed by the saved task list. */
    public Dobby() {
        logic = new DobbyLogic();
    }

    /**
     * Executes one user command and returns Dobby's response.
     *
     * @param input User command to execute.
     * @return Dobby's response to the command.
     */
    public String getResponse(String input) {
        Command command = Parser.parse(input);
        return command.isExit() ? "> Dobby says goodbye to master!" : command.execute(logic);
    }

    /**
     * Returns whether a user command requests that Dobby exit.
     *
     * @param input User command to inspect.
     * @return {@code true} when the command should close the application.
     */
    public boolean isExitCommand(String input) {
        return Parser.parse(input).isExit();
    }

    /**
     * Displays a welcome banner when the chatbot starts.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        DobbyLogic dobby = new DobbyLogic();
        Ui ui = new Ui();

        // Initialize chatbot
        ui.showMessage(dobby.getStartupMessage());
        ui.showWelcome();

        // Take user inputs
        ui.showPrompt();
        String input;
        while ((input = ui.readCommand()) != null) {
            Command command = Parser.parse(input);
            if (command.isExit()) {
                break;
            }
            ui.showLine();
            ui.showMessage(command.execute(dobby));
            ui.showLine();
            ui.showPrompt();
        }

        // Exit chatbot
        ui.showGoodbye();
    }
}
