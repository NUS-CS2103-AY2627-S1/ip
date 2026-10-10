package jasper;

import jasper.command.CommandResult;
import jasper.ui.Ui;

/**
 * Serves as command-line interface entry point for Jasper application.
 */
public class CliMain {
    /**
     * Runs main CLI execution loop to continuously process user commands.
     */
    public static void main(String[] args) {
        Jasper jasper = new Jasper("data", "jasper.txt");
        Ui ui = new Ui(); // User interface component handling CLI input and output

        ui.showWelcome();
        jasper.getStartupWarning().ifPresent(ui::showError);
        CommandResult result;
        do {
            result = jasper.getResult(ui.readCommand());
            ui.showResponse(result);
            ui.showLine();
        } while (!result.isQuit());
        ui.close();
    }
}
