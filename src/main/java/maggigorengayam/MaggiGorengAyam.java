package maggigorengayam;

import maggigorengayam.command.Command;
import maggigorengayam.parser.Parser;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/**
 * Entry point for the Maggi Goreng Ayam task-list chatbot. Wires together
 * the {@link Ui}, {@link Storage}, and {@link Parser}, then runs the
 * read-parse-execute loop until an {@code ExitCommand} is parsed or input
 * runs out.
 */
public class MaggiGorengAyam {
    private static final String DATA_FILE_PATH = "data/maggigorengayam.txt";

    /**
     * Loads any previously saved tasks, then repeatedly reads a command
     * line, parses it, and executes it against the live task list until
     * the user types {@code bye} or standard input is exhausted.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        System.out.println(ui.showWelcome());

        Storage storage = new Storage(DATA_FILE_PATH);
        // Tasks saved by a previous run (if any) are loaded back in here;
        // a missing/first-time data file just means an empty starting list.
        Storage.StartupResult startup = storage.loadOrEmpty(ui);
        TaskList tasks = startup.tasks;
        if (startup.message != null) {
            System.out.println(startup.message);
        }

        boolean isExit = false;
        // A blank line is checked for, and silently skipped, before the
        // per-command showLine() bracket below - so pressing Enter with
        // nothing typed produces no output at all, rather than an empty
        // divider pair.
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.isEmpty()) {
                continue;
            }
            try {
                System.out.println(ui.showLine());
                Command c = Parser.parse(command);
                System.out.println(c.execute(tasks, ui, storage));
                isExit = c.isExit();
            } catch (MaggiGorengAyamException e) {
                System.out.println(ui.showError(e.getMessage()));
            } catch (RuntimeException e) {
                // A safety net against an unanticipated bug, not a feature: keeps the
                // session alive (and the already-saved data intact) instead of crashing
                // the whole program with a raw stack trace on an unforeseen error.
                System.out.println(ui.showError("something went wrong sia, can try that again?"));
            } finally {
                System.out.println(ui.showLine());
            }
        }
        ui.close();
    }
}
