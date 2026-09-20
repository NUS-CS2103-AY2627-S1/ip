package woofer;

import woofer.exception.WooferException;
import woofer.ui.Ui;

/**
 * Coordinates Woofer's user interface, command parser, storage, and task list.
 */
public class Woofer {
    private final Ui ui;
    private final WooferService service;

    /**
     * Creates a Woofer application with its supporting components.
     */
    public Woofer() {
        ui = new Ui();
        service = new WooferService();
    }

    /**
     * Starts Woofer.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Woofer().run();
    }

    /**
     * Runs Woofer until the user enters the exit command.
     */
    public void run() {
        ui.showWelcome();
        if (service.hasLoadingError()) {
            ui.showError(service.getLoadingWarning());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                WooferService.Response response = service.execute(command);
                if (response.exits()) {
                    ui.showBye();
                    break;
                }
                ui.showResponse(response.message());
            } catch (WooferException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showLine();
        }
    }
}
