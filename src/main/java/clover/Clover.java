package clover;

import java.io.IOException;

import clover.command.Command;
import clover.command.CommandResponseStyle;
import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.Task;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Coordinates Clover's user interface, task list, command parser, and storage.
 */
public class Clover {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private TutoreeList tutorees;
    private CommandResponseStyle responseStyle;
    private boolean isExitRequested;

    /**
     * Creates Clover and loads its previously saved task list.
     */
    public Clover() {
        this(new Storage(), new Ui());
    }

    /**
     * Creates Clover with its collaborators, primarily to isolate automated integration tests.
     */
    Clover(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;

        try {
            if (!storage.acquireApplicationLock()) {
                ui.showError("Another Clover window is using these data files. "
                        + "This window is read-only.");
            }
        } catch (IOException | SecurityException exception) {
            storage.disableWriting();
            ui.showError("Clover could not lock the data files. "
                    + "This window is read-only.");
        }

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException exception) {
            backupTaskData();
            ui.showError("The saved quest journal could not be opened. "
                    + "A backup was kept. Starting with an empty grove.");
            tasks = new TaskList();
        }
        try {
            tutorees = new TutoreeList(storage.loadTutorees());
        } catch (IOException | SecurityException exception) {
            backupTutoreeData();
            ui.showError("The learning companion journal could not be opened. "
                    + "A backup was kept. Starting with an empty grove.");
            tutorees = new TutoreeList();
        }
        rejectOrphanedTaskLinks();
    }

    /**
     * Runs the command loop until the user enters {@code bye} or input ends.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, tutorees, ui, storage);
                isExit = command.isExit();
            } catch (CloverException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
        storage.releaseApplicationLock();
    }

    /**
     * Generates a response for a user's chat message.
     *
     * @param input the message entered by the user
     * @return Clover's response to the message
     */
    public String getResponse(String input) {
        ui.clearResponse();
        responseStyle = CommandResponseStyle.STANDARD;
        isExitRequested = false;
        try {
            Command command = Parser.parse(input);
            responseStyle = command.getResponseStyle();
            command.execute(tasks, tutorees, ui, storage);
            isExitRequested = command.isExit();
        } catch (CloverException exception) {
            responseStyle = CommandResponseStyle.ERROR;
            ui.showError(exception.getMessage());
        }
        return ui.buildResponse();
    }

    /**
     * Returns the visual style for the latest response.
     *
     * @return the visual style associated with the latest response
     */
    public CommandResponseStyle getResponseStyle() {
        return responseStyle;
    }

    /**
     * Returns whether the most recently processed GUI command requested that Clover close.
     *
     * @return true if the latest command was {@code bye}
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Starts the Clover application.
     */
    public static void main(String[] args) {
        new Clover().run();
    }

    /** Releases the storage lock when the graphical application closes. */
    public void close() {
        storage.releaseApplicationLock();
    }

    /** Rejects loaded tasks that reference a missing tutoree record. */
    private void rejectOrphanedTaskLinks() {
        boolean hasOrphanedLink = tasks.asList().stream()
                .map(Task::getTutoreeName)
                .filter(name -> name != null)
                .anyMatch(name -> tutorees.findExactName(name).isEmpty());
        if (hasOrphanedLink) {
            backupTaskData();
            ui.showError("The saved quest journal contains a link to a missing learning companion. "
                    + "A backup was kept. Starting with an empty grove.");
            tasks = new TaskList();
        }
    }

    /** Creates a backup when possible without masking the original data-recovery message. */
    private void backupTaskData() {
        try {
            storage.backupTaskData();
        } catch (IOException | SecurityException exception) {
            // Clover can still safely start with an empty in-memory task list.
        }
    }

    /** Creates a backup when possible without masking the original data-recovery message. */
    private void backupTutoreeData() {
        try {
            storage.backupTutoreeData();
        } catch (IOException | SecurityException exception) {
            // Clover can still safely start with an empty in-memory tutoree list.
        }
    }
}
