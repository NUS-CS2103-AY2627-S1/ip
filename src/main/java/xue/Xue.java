package xue;

import xue.storage.Storage;
import xue.ui.Ui;

/**
 * The main entry point for Xue.
 */
public class Xue {
    /**
     * Starts Xue and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        CommandProcessor processor = new CommandProcessor(new Storage());
        ui.showWelcome();
        if (processor.getLoadError() != null) {
            ui.showError(processor.getLoadError());
        }

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showLine();

            String response = processor.process(command);
            System.out.println(response);
            ui.showLine();
            if (command.trim().equals("bye")) {
                break;
            }
        }
    }
}
