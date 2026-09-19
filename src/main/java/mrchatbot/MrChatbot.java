package mrchatbot;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.ui.Ui;

/**
 * Runs Mr Chatbot and coordinates parsing, storage, task operations, and UI output.
 */
public class MrChatbot {
    /**
     * Starts the chatbot and processes commands until the user exits or input ends.
     */
    public static void main(String[] args) {
        String name = "User";
        Ui ui = new Ui(name);
        ui.showWelcome();

        MrChatbotEngine engine = new MrChatbotEngine();
        if (engine.getStartupError() != null) {
            ui.showError(engine.getStartupError());
        }

        // read inputs
        while (ui.hasNextInput()) {
            String input = ui.readInput();
            ui.showLine();

            try {
                if (engine.processCommand(input, ui)) {
                    ui.showLine();
                    break;
                }
            } catch (MrChatbotException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }
}
