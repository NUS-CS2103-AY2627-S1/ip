package bob.command;

import bob.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Says goodbye and ends the conversation, first saving any change an earlier save
 * failed to write.
 *
 * <p>This is the one command that answers {@link #isExit()} with {@code true}.
 * Everything about ending the conversation is therefore in this class: the loop
 * in {@link bob.Bob Bob} asks every command it runs whether that was the last one, and
 * only this one ever says yes.
 *
 * <p>A change whose save failed is still in the list, but quitting would lose it, so
 * the save is tried once more here. If it fails again, quitting is held back once:
 * the error is shown and the conversation carries on, so the user can fix whatever
 * stopped the save and try again. {@link #execute} throws in that case, and the loop
 * only asks {@link #isExit()} of a command that ran to the end. The next goodbye quits
 * even if the save still fails, so a problem the user cannot fix does not keep them
 * from quitting.
 */
public class ExitCommand extends Command {

    /** Added to the save error when quitting is held back, so the user knows how to go on. */
    private static final String RETRY_NOTE = "\nFix that and type " + CommandWord.BYE.getKeyword()
            + " to try again. If it still can't be saved, " + CommandWord.BYE.getKeyword()
            + " will quit without saving.";

    /**
     * Saves any change an earlier save failed to write, then prints the farewell.
     *
     * @throws BobException if that save fails again and the user has not yet been
     *                      warned about it on trying to quit.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobException {
        if (storage.hasUnsavedChanges()) {
            saveBeforeQuitting(tasks, ui, storage);
        }
        ui.showFarewell();
    }

    /**
     * Tries once more to save the list, and says whether the changes are now saved or
     * are being left behind.
     *
     * @throws BobException if the save fails and the user has not yet been warned.
     */
    private static void saveBeforeQuitting(TaskList tasks, Ui ui, Storage storage) throws BobException {
        try {
            storage.save(tasks.asList());
        } catch (BobException e) {
            if (!storage.isUnsavedChangesWarningGiven()) {
                storage.markUnsavedChangesWarningGiven();
                throw new BobException(e.getMessage() + RETRY_NOTE);
            }
            ui.showQuitWithoutSaving();
            return;
        }
        ui.showSavedBeforeQuitting();
    }

    /** Returns {@code true}: this is the one command that ends the conversation. */
    @Override
    public boolean isExit() {
        return true;
    }
}
