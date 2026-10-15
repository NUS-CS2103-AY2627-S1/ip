package ff15;

import java.io.IOException;

import ff15.ai.AiAssistant;
import ff15.ai.AiHelper;
import ff15.ai.UnavailableAiHelper;
import ff15.command.Command;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/**
 * The chatbot itself: it wires together the parts of the program and runs the
 * command loop.
 *
 * <p>{@link Ui} talks to the user, {@link Parser} makes sense of what they type,
 * {@link TaskList} holds the tasks, {@link ContactList} holds the contacts, and
 * {@link Storage} keeps both on disk, and {@link AiAssistant} asks the AI for
 * the {@code @ai} and {@code @do} commands. This class owns one of each and
 * does nothing but pass work between them.
 */
public class FF15 {
    /** Where the tasks are kept between sessions. */
    private static final String DATA_FILE = "data/ff15.txt";

    /** Where the contacts are kept between sessions. */
    private static final String CONTACT_FILE = "data/contacts.txt";

    /**
     * Why the console session has no AI. The window version passes a real
     * {@link AiHelper} in; the console session is kept free of the AI library,
     * so it still builds and runs with nothing but the JDK.
     */
    private static final String NO_AI_REASON = "There's no AI here. It lives in the window version of me.";

    private final Ui ui;
    private final Storage storage;
    private final AiAssistant assistant;
    private TaskList tasks;
    private ContactList contacts;

    /** Whether the last command handed to {@link #getResponse(String)} ended the session. */
    private boolean isFinished;

    /** Whether the last reply from {@link #getResponse(String)} was an error rather than a result. */
    private boolean isLastReplyError;

    /** The command the AI suggested in the last reply from {@link #getResponse(String)}, if any. */
    private String suggestedCommand = "";

    /**
     * Greets the user and loads the tasks and contacts kept in the default save
     * files. The AI commands report that there is no AI.
     */
    public FF15() {
        this(new UnavailableAiHelper(NO_AI_REASON));
    }

    /**
     * Greets the user and loads the tasks and contacts kept in the default save
     * files, with the AI commands answered through {@code aiHelper}.
     *
     * @param aiHelper what the {@code @ai} and {@code @do} commands send their prompts through.
     */
    public FF15(AiHelper aiHelper) {
        this(DATA_FILE, CONTACT_FILE, aiHelper);
    }

    /**
     * Greets the user and loads the tasks saved at {@code filePath} and the
     * contacts saved at {@code contactFilePath}. The AI commands report that
     * there is no AI.
     *
     * @see #FF15(String, String, AiHelper)
     */
    public FF15(String filePath, String contactFilePath) {
        this(filePath, contactFilePath, new UnavailableAiHelper(NO_AI_REASON));
    }

    /**
     * Greets the user and loads the tasks saved at {@code filePath} and the
     * contacts saved at {@code contactFilePath}. A missing file is normal and
     * starts an empty list; a file that cannot be read is reported, and the
     * session starts empty rather than stopping; a file with lines that cannot be
     * understood keeps the rest and reports the ones it skipped. The two files
     * are read independently, so a bad one does not cost the user the other.
     *
     * @param filePath where the tasks are saved.
     * @param contactFilePath where the contacts are saved.
     * @param aiHelper what the {@code @ai} and {@code @do} commands send their prompts through.
     */
    public FF15(String filePath, String contactFilePath, AiHelper aiHelper) {
        ui = new Ui();
        storage = new Storage(filePath, contactFilePath);
        assistant = new AiAssistant(aiHelper);

        ui.startBlock();
        ui.showWelcome();
        try {
            Storage.Loaded<Task> loaded = storage.load();
            tasks = new TaskList(loaded.items());
            ui.showSkippedLines(filePath, loaded.skipped());
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
        try {
            Storage.Loaded<Contact> loaded = storage.loadContacts();
            contacts = new ContactList(loaded.items());
            ui.showSkippedLines(contactFilePath, loaded.skipped());
        } catch (IOException e) {
            ui.showContactLoadingError(e.getMessage());
            contacts = new ContactList();
        }
        ui.endBlock();
    }

    /**
     * Reads commands and carries them out until the user says bye. Each command
     * decides for itself what to do; this loop only frames the output, hands the
     * command the three things it may need, and reports anything that went wrong.
     */
    public void run() {
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            ui.startBlock();
            try {
                Command command = Parser.parse(input, assistant);
                command.execute(tasks, contacts, ui, storage);
                isExit = command.isExit();
            } catch (FF15Exception e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Couldn't save your tasks: " + e.getMessage());
            }
            if (isExit) {
                ui.endFinalBlock();
            } else {
                ui.endBlock();
            }
        }
    }

    /**
     * Returns the greeting for a new session, which the constructor has already
     * put together: the welcome, plus a loading error if the save file could not
     * be read.
     */
    public String getStartupMessage() {
        return ui.drainTranscript();
    }

    /**
     * Returns whether the last command carried out ended the session, so a caller
     * showing a window knows when to shut it. The console session reads the same
     * answer from the command itself, in {@link #run()}.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Returns whether the last reply from {@link #getResponse(String)} reported
     * an error, so a caller showing a window can draw it to catch the eye rather
     * than letting it pass as one more reply.
     */
    public boolean isLastReplyError() {
        return isLastReplyError;
    }

    /**
     * Returns the command the AI suggested in the last reply from
     * {@link #getResponse(String)}, or an empty string if it suggested none, so a
     * caller showing a window can offer it to the user ready to send.
     */
    public String getSuggestedCommand() {
        return suggestedCommand;
    }

    /**
     * Carries out one command and returns what the chatbot would have said. This
     * is the same work the loop in {@link #run()} does for one line of input,
     * with the reply handed back instead of being left on the console.
     */
    public String getResponse(String input) {
        isLastReplyError = false;
        try {
            Command command = Parser.parse(input, assistant);
            command.execute(tasks, contacts, ui, storage);
            isFinished = command.isExit();
        } catch (FF15Exception e) {
            ui.showError(e.getMessage());
            isLastReplyError = true;
        } catch (IOException e) {
            ui.showError("Couldn't save your tasks: " + e.getMessage());
            isLastReplyError = true;
        }
        suggestedCommand = ui.takeSuggestion();
        String reply = ui.drainTranscript();
        // An empty reply would surface in the GUI as an empty speech bubble, so every
        // path above must leave the user something to read.
        assert !reply.isEmpty() : "no reply was produced for the input: " + input;
        return reply;
    }

    /**
     * Starts the chatbot, reading any previously saved tasks from
     * {@code data/ff15.txt} relative to the working directory.
     *
     * @param args ignored; the data file location is fixed.
     */
    public static void main(String[] args) {
        new FF15().run();
    }
}
