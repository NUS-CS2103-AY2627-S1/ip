import anniechat.logic.CommandHandler;
import anniechat.logic.CommandResult;
import anniechat.storage.Storage;
import anniechat.ui.Ui;

/** Runs the Anniechat command-line task manager. */
public class Anniechat {
    private static final String DATA_FILE_PATH = "data/anniechat.txt";

    private Anniechat() {
        // Prevent instantiation of the entry-point class.
    }

    /**
     * Starts Anniechat and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        CommandHandler commandHandler = new CommandHandler(new Storage(DATA_FILE_PATH));
        if (!commandHandler.getStartupMessage().isEmpty()) {
            ui.showResponse(commandHandler.getStartupMessage());
        }

        while (true) {
            CommandResult result = commandHandler.handle(ui.readCommand());
            ui.showResponse(result.getMessage());
            if (result.isExitRequested()) {
                break;
            }
        }
    }
}
