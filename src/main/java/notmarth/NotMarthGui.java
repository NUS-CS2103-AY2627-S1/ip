package notmarth;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import notmarth.command.Command;
import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.parser.Parser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Provides the command-processing backend for the NotMarth graphical interface. */
public final class NotMarthGui {
    private static final int MAX_TASKS = 100;
    private static final String DEFAULT_FILE_PATH = "data/notmarth.txt";
    private static final String WELCOME_MESSAGE = "Greetings, Divine One. I'm NotMarth, your tactical companion.\n"
            + "Issue a mission order below, and we'll move your battle plan forward.\n\n";

    private final Parser parser = new Parser();
    private final Storage storage = new Storage(DEFAULT_FILE_PATH);
    private final Ui ui = new Ui();
    private final TaskList tasks;
    private final ContactList contacts;
    private final String startupWarning;
    private boolean isExitRequested;

    /** Creates the command-processing backend and loads the saved battle plan. */
    public NotMarthGui() {
        Storage.LoadResult loadResult = storage.load(MAX_TASKS);
        tasks = new TaskList(loadResult.getTasks(), MAX_TASKS);
        contacts = new ContactList(loadResult.getContacts());
        startupWarning = loadResult.getWarning();
    }

    /** Returns the opening message shown in the graphical conversation. */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /** Executes a command and returns the same response used by the console UI. */
    public String getResponse(String input) {
        isExitRequested = false;
        if (startupWarning != null) {
            return "I couldn't process that, Divine One: " + startupWarning;
        }
        try {
            Command command = parser.parse(input);
            isExitRequested = command.isExit();
            return captureCommandOutput(command);
        } catch (NotMarthException exception) {
            return "I couldn't process that, Divine One: " + exception.getMessage();
        }
    }

    /** Returns whether the most recent command requested that the window close. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    private String captureCommandOutput(Command command) throws NotMarthException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            command.execute(tasks, contacts, ui, storage);
        } finally {
            System.setOut(originalOutput);
        }
        // Bubble padding supplies the margin for both the first and wrapped lines.
        return output.toString(StandardCharsets.UTF_8).lines()
                .map(String::stripLeading)
                .collect(Collectors.joining("\n"));
    }
}
