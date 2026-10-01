package bo;

import bo.command.CommandException;
import bo.parser.Parser;
import bo.ui.Ui;

/**
 * Starts Bo, a simple personal assistant.
 */
public class Bo {
    /**
     * Starts Bo and processes commands until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        String banner = """
                 ____
                | __ )  ___
                |  _ \\ / _ \\
                | |_) | (_) |
                |____/ \\___/
                """;

        Ui ui = new Ui();
        ui.showWelcome(banner);

        Tracker tracker = new Tracker(ui);

        Parser parser = new Parser();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            if (command.equals("bye")) {
                ui.showGoodbye();
                ui.showSeparator();
                return;
            }

            try {
                parser.executeCommand(tracker, command);
            } catch (CommandException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showSeparator();
        }
    }
}
