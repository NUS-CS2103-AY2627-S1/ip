package duchess.ui.cli;

import java.io.IOException;

import duchess.ui.Duchess;

/**
 * [Legacy] Retains the CLI interface for Duchess.
 */
public class CliLauncher {
    public static final String BANNER =
            " ____  _   _  _____    _ ____  ___  ___\n"
                    + "|  _ \\| | | |/ __/ |  | | ___|/   \\/   \\\n"
                    + "| | | | | | | |  | |__| | |__|  | |  | |\n"
                    + "| | | | | | | |  |  __  |  __|\\  \\/\\  \\/\n"
                    + "| |_| | \\_/ | |__| |  | | |__ /\\  \\/\\  \\\n"
                    + "|____/ \\___/ \\___\\_|  |_|____|\\___/\\___/\n";

    // 40 underscores
    public static final String SEPARATOR = "*________________________________________*";

    public static void main(String[] args) {
        Duchess duchess;
        try {
            duchess = new Duchess("data", "duchess.txt");
        } catch (IOException i) {
            System.out.println("Oh no! The duchess was unable to find, nor create,\n"
                    + "the file /data/duchess.txt\n\n"
                    + "It appears she is unwelcome in the premises. :-(");
            return;
        }

        Ui ui = new Ui(Duchess.NAME, BANNER, SEPARATOR);
        ui.greet();
        while (true) {
            String input = ui.listen();
            String response = duchess.respondTo(input);
            ui.say(response);

            /* Exit once user wants to. */
            if (response.equals(Duchess.getExitMessage())) {
                ui.exit();
                break;
            }
        }
    }
}
