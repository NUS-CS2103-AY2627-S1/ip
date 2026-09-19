package nyonbot;

import java.io.IOException;

import javafx.application.Platform;
import nyonbot.Logic.Result;
import nyonbot.command.Command;
import nyonbot.model.NyonException;
import nyonbot.model.TaskList;
import nyonbot.storage.ListStorage;

/**
 * Main driver class for NyonBot.
 */
public class NyonBot {
    private Parser parser = Parser.getInstance();
    private Logic logic = Logic.getInstance();
    private ListStorage storage = ListStorage.getInstance();
    private String startupMessage = "";
    private final String greeting = "Welcome to NyonBot! Type `help` to get started";

    /**
     * Creates a NyonBot instance.
     */
    public NyonBot() {
        try {
            TaskList loadedTasks = storage.load();
            logic.loadList(loadedTasks);
            setMalformedRecordWarning();
        } catch (IOException e) {
            startupMessage = "couldn't load your list as " + e.getMessage();
        }
    }

    private void setMalformedRecordWarning() {
        int skippedRecords = storage.getSkippedRecordCount();
        if (skippedRecords > 0) {
            startupMessage = String.format(
                    "ignored %d malformed record%s in your save file",
                    skippedRecords, skippedRecords == 1 ? "" : "s");
        }
    }

    /**
     * Returns a warning generated while the task list was loaded.
     *
     * @return Startup warning, or an empty string when loading succeeded.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Returns the greeting to be presented on startup.
     *
     * @return greeting message
     */
    public String getGreeting() {
        return greeting;
    }

    /**
     * Passes an input to NyonBot.
     *
     * @param input User input.
     * @return Response string.
     */
    public String respond(String input) {
        try {
            Command cmd = parser.parse(input);
            Result res = logic.execute(cmd);
            if (res.shouldExit()) {
                if (!this.onClose()) {
                    throw new NyonException("couldn't close file");
                }
                Platform.exit();
                return null;
            }
            if (res.shouldWrite()) {
                storage.save(logic.getList());
            }
            if (res.funValue() == 66 || res.funValue() == -1) {
                return res.out();
            }
            if (res.out() != null && !res.out().isBlank()) {
                return String.format("Nyon! (%s)", res.out());
            }
            return "";
        } catch (Exception e) {
            return String.format("Nyon... (%s)", e.getMessage());
        }
    }

    /**
     * Saves the current list and closes NyonBot.
     *
     * @return true if successfully written to file, false otherwise.
     */
    public boolean onClose() {
        try {
            storage.save(logic.getList());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Launches NyonBot from command line.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        NyonBot nyonBot = new NyonBot();
        Ui ui = Ui.getInstance();

        ui.welcome();
        if (!nyonBot.getStartupMessage().isBlank()) {
            ui.showOutput(nyonBot.getStartupMessage());
        }

        while (true) {
            System.out.println();

            String response = nyonBot.respond(ui.readCommand());

            if (response == null) {
                break;
            }
            if (!response.isBlank()) {
                ui.showOutput(response);
            }
        }

        ui.goodbye();
    }
}
