package janet;

import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the main application that manages tasks and handles user commands.
 */
public class Janet {

    private static final String STORAGE_RECOVERY_PROMPT = """
            Storage file contains an improper format. Reason: %s.
            Enter "%s" to clear the saved tasks and get a fresh start, or enter anything else to exit!
            """;

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private JanetFileException storageFileException;

    /**
     * Creates a new Janet application and initializes its storage, task list,
     * and user interface.
     *
     * @throws IOException If the task storage cannot be initialized.
     */
    public Janet() throws IOException {
        this(new Storage());
    }

    /**
     * Creates a Janet application using the provided storage.
     * This constructor lets tests provide isolated storage without changing the text or GUI behaviour.
     *
     * @param storage Storage used to load and save tasks.
     * @throws IOException If the task storage cannot be read.
     */
    Janet(Storage storage) throws IOException {
        this.storage = storage;
        this.ui = new Ui();
        this.tasks = new TaskList();

        try {
            this.tasks = this.storage.readFromFile();
        } catch (JanetFileException e) {
            this.storageFileException = e;
        }
    }

    Janet(Storage storage, Ui ui, TaskList tasks, JanetFileException storageFileException) {
        this.storage = storage;
        this.ui = ui;
        this.tasks = tasks;
        this.storageFileException = storageFileException;
    }

    /**
     * Returns whether the application is waiting for the user to resolve malformed saved data.
     *
     * @return {@code true} when saved data must be reset before commands can be processed.
     */
    public boolean isStorageRecoveryRequired() {
        return this.storageFileException != null;
    }

    /**
     * Returns the prompt explaining how to recover from malformed saved data.
     *
     * @return The recovery prompt for the text UI or a Janet chat dialog.
     * @throws IllegalStateException If no storage recovery is currently required.
     */
    public String getStorageRecoveryPrompt() {
        if (!this.isStorageRecoveryRequired()) {
            throw new IllegalStateException("Storage recovery is not required.");
        }
        return String.format(
                Janet.STORAGE_RECOVERY_PROMPT,
                this.storageFileException.getMessage(),
                Parser.RESET_WORD
        );
    }

    /**
     * Processes the user's answer to the malformed-storage recovery prompt.
     *
     * @param input The user's response from either the text UI or the GUI chat field.
     * @return {@code true} if storage was reset and Janet can accept commands; {@code false} if the user declined.
     */
    public boolean isResolveStorageRecoveryRequest(String input) {
        return Parser.isResetCommand(Parser.formatString(input));
    }

    /**
     * Clears malformed saved tasks and creates a fresh Janet instance.
     *
     * @return A Janet instance with an empty task list and no pending storage recovery.
     * @throws IOException If the storage file cannot be cleared.
     */
    public Janet resolveStorageRecovery() throws IOException {
        this.storage.resetStorage();
        return new Janet(this.storage, this.ui, new TaskList(), null);
    }

    /**
     * Starts the Janet application.
     *
     * @param args Command-line arguments.
     * @throws IOException If the task storage cannot be initialized.
     */
    public static void main(String[] args) throws IOException {
        new Janet().run();
    }

    private void run() throws IOException {
        Scanner sc = new Scanner(System.in);
        if (!this.resolveStorageRecoveryFromTextUi(sc)) {
            return;
        }

        this.ui.showGreeting();

        assert this.tasks != null : "Janet must have a task list before accepting commands";

        while (true) {
            String formattedLine = Parser.formatString(sc.nextLine());

            if (Parser.isExitCommand(formattedLine)) {
                break;
            }
            try {
                TaskList.CommandResult res = Janet.getResponse(this, formattedLine);
                Janet.processCommandResult(this, res);
                this.ui.showMessage(res.message());
            } catch (IOException e) {
                this.ui.showError(String.format("IO Failure: %s\n", e.getMessage()));
            } catch (JanetException e) {
                this.ui.showError(String.format("Failure: %s\n", e.getMessage()));
            }

        }
        this.ui.showGoodbye();
    }

    public static TaskList.CommandResult getResponse(Janet janet, String input) throws JanetException {
        if (janet.isStorageRecoveryRequired()) {
            throw new JanetException("Storage recovery is required before commands can be processed.");
        }
        return new Parser(janet.tasks).processCommand(input);
    }

    /**
     * Writes an updated task list to storage and applies it to Janet's in-memory state.
     *
     * @throws IOException If the file cannot be written to.
     */
    public static void processCommandResult(Janet janet, TaskList.CommandResult commandResult) throws IOException {
        janet.storage.writeToFile(
                commandResult.updatedTaskList()
                        .map(x -> x.toString())
                        .orElse(new TaskList().toString())
        );
        janet.tasks = commandResult.updatedTaskList().orElse(janet.tasks);
    }

    /**
     * Displays and resolves a pending storage-recovery prompt through the console.
     *
     * @param sc Scanner that reads answers from the text UI.
     * @return {@code true} when Janet is ready to accept commands; {@code false} when the user chose to exit.
     * @throws IOException If the malformed storage file cannot be cleared.
     */
    private boolean resolveStorageRecoveryFromTextUi(Scanner sc) throws IOException {
        if (!this.isStorageRecoveryRequired()) {
            return true;
        }

        this.ui.showError(this.getStorageRecoveryPrompt());
        String input = sc.nextLine();
        if (!this.isResolveStorageRecoveryRequest(input)) {
            return false;
        }
        this.resolveStorageRecovery();
        return true;
    }
}
