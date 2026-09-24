package murphy;

import java.nio.file.Path;
import java.nio.file.Paths;

import murphy.ui.Ui;

/** Runs Murphy's command-line interface using the same service as the GUI. */
public class Murphy {
    /** The location of Murphy's saved task data. */
    private static final Path DATA_FILE_PATH = Paths.get("data", "murphy.txt");

    /** Starts Murphy's command-line conversation. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        MurphyService service = new MurphyService(DATA_FILE_PATH, ui::showMessage);
        ui.showWelcome();
        try (ui) {
            while (ui.hasNextCommand()) {
                String command = ui.readCommand().trim();
                ui.showSeparator();
                String response = service.respond(command);
                ui.showMessage("     " + response.replace("\n", "\n     "));
                ui.showSeparator();
                if (command.equalsIgnoreCase("bye")) {
                    break;
                }
            }
        }
    }
}
