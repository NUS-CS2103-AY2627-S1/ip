package apollo;

import apollo.ui.Ui;

/** Runs Apollo using the original command-line interface. */
// CHECKSTYLE.OFF: AbbreviationAsWordInName
public final class CLIApplication {
    // CHECKSTYLE.ON: AbbreviationAsWordInName
    private CLIApplication() {
    }

    /**
     * Starts the console front end and forwards each entered command to Apollo.
     *
     * @param args command-line arguments, which are not currently used
     */
    public static void main(String[] args) {
        Apollo apollo = new Apollo();
        Ui ui = new Ui();

        try {
            ui.showGreeting();
            if (apollo.getLoadingError() != null) {
                ui.showResponse(apollo.getLoadingError());
            }

            while (!apollo.isExitRequested() && ui.hasNextCommand()) {
                String input = ui.readCommand();
                ui.showSeparator();
                ui.showResponse(apollo.getResponse(input));
                ui.showSeparator();
            }
        } finally {
            ui.close();
        }
    }
}
