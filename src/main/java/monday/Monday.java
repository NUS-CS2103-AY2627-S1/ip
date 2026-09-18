package monday;

import monday.command.Parser;
import monday.exception.MondayException;
import monday.storage.Storage;
import monday.task.TaskList;
import monday.ui.Ui;

/**
 * Runs the Monday task-management chatbot.
 */
public class Monday {
    private TaskList tasks;
    private Ui ui;
    private Parser parser;
    private boolean isRunning = true;

    /**
     * Creates a Monday chatbot with the saved tasks and required collaborators.
     */
    public Monday() {
        tasks = new TaskList(Storage.loadTask());
        ui = new Ui();
        parser = new Parser();
    }

    /**
     * Returns a response from Monday in String format given a user input.
     */
    public String getResponse(String input) {
        try {
            isRunning = parser.executeCommand(input, tasks, ui);
            String response = ui.getResponse();
            assert response != null : "There needs to be a response from the bot!";
            return response;
        } catch (MondayException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns Monday's welcome message.
     */
    public String getWelcome() {
        ui.showWelcome();
        return ui.getResponse();
    }

    /**
     * Returns whether Monday should continue running.
     *
     * @return true if Monday should continue; false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        TaskList tasks = new TaskList(Storage.loadTask());
        Ui ui = new Ui();
        ui.showWelcome();
        Parser parser = new Parser();
        while (true) {
            try {
                String command = ui.readCommand();
                if (!parser.executeCommand(command, tasks, ui)) {
                    break;
                }

            } catch (MondayException e) {
                ui.showResponse(e.getMessage());
            }
        }
    }
}
