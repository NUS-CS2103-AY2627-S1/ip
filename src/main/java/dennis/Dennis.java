package dennis;

import dennis.command.Command;
import dennis.parser.Parser;
import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Entry point and orchestrator of the chatbot.
 *
 * <p>{@code Dennis} owns the three collaborators the program is built from
 * (a {@link Ui} for console input/output, a {@link Storage} for the save
 * file, and a {@link TaskList} for the tasks in memory) and runs the
 * read-parse-execute loop that ties them together. It holds no command
 * logic of its own: {@link Parser} turns each line into a {@link Command}
 * and the command carries itself out.</p>
 */
public class Dennis {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Sets up the collaborators and loads any previously saved tasks so the
     * session resumes where the last one left off.
     */
    public Dennis() {
        ui = new Ui();
        storage = new Storage();
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the main loop: show the greeting, then repeatedly read a command,
     * parse it, and execute it until the user types {@code bye} or the input
     * runs out. A {@link DennisException} from a single command is reported to
     * the user and the loop continues.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                Command command = Parser.parse(fullCommand);

                // Parser.parse never returns null: every branch either returns
                // a command or throws DennisException. The loop relies on this
                // when it calls execute without a null check.
                assert command != null : "Parser.parse returned null";

                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (DennisException e) {
                ui.showError(e.getMessage());
                ui.showLine();
            }
        }
    }

    /**
     * Starts the chatbot.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        new Dennis().run();
    }
}
