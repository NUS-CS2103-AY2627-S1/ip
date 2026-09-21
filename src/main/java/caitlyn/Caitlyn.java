package caitlyn;

/**
 * Entry point for the chatbot application.
 */
public class Caitlyn {
    /**
     * Creates the application entry-point object.
     */
    public Caitlyn() {
    }

    /**
     * Starts the application, accepts tasks, displays the task list, and exits
     * when the user enters {@code bye}.
     *
     * @param args command-line arguments supplied when the program starts.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskSession session = new TaskSession();
        if (session.hasLoadingError()) {
            ui.showLoadingError();
        }
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            assert fullCommand != null
                    : "The UI should provide a command when input is available.";

            ui.showSeparator();
            try {
                Command command = Parser.parse(fullCommand);
                assert command != null
                        : "The parser should return a command for every input.";
                session.execute(command, ui);
                isExit = command.isExit();
            } catch (CaitlynException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showSeparator();
            }
        }
    }

}
