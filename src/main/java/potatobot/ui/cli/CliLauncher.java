package potatobot.ui.cli;

import potatobot.logic.CommandResult;
import potatobot.logic.PotatoBot;

/**
 * Starts and coordinates PotatoBot's command-line frontend.
 */
public class CliLauncher {
    /**
     * Runs the command loop until the user exits or standard input ends.
     *
     * @param args Command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        PotatoBot potatoBot = new PotatoBot();

        try (ConsoleUi ui = new ConsoleUi()) {
            ui.showGreeting();
            if (potatoBot.getStartupErrorMessage() != null) {
                ui.showMessage(potatoBot.getStartupErrorMessage());
            }

            boolean shouldExit = false;
            while (!shouldExit) {
                String input = ui.readCommand();
                if (input == null) {
                    break;
                }

                CommandResult result = potatoBot.respondTo(input);
                if (result.isExit()) {
                    ui.showBlankLine();
                }
                ui.showMessage(result.message());
                shouldExit = result.isExit();
            }

            if (shouldExit) {
                ui.showFarewell();
            }
        }
    }
}
