package bob.command;

import bob.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents one thing the user has asked the chatbot to do, ready to be carried out.
 *
 * <p>What each command does used to be a branch of a {@code switch} in
 * {@link bob.Bob Bob}, and the arguments each one needed were picked apart in a method
 * of its own beside it. Adding a command meant editing {@code Bob} in three
 * places, and the whole of the chatbot's behavior sat in one class that grew
 * with every command added.
 *
 * <p>Each command is now a class, and carrying one out is a call to
 * {@link #execute}. Which class it is decides what happens, so {@code Bob} no
 * longer has to ask: it is handed a {@code Command} and runs it. Adding a
 * command is now writing a class and naming it in one line of {@link bob.parser.Parser Parser},
 * rather than editing the body of the class that runs the conversation.
 *
 * <p>Every command is given the same three things to work with, whether it
 * needs them or not. A listing ignores the {@link Storage} it is passed, since
 * it changes nothing to save. Handing all three to all of them is what lets the
 * loop in {@code Bob} run any command without knowing which one it holds, and
 * that is the whole point of them sharing a parent.
 *
 * <p>A command is made from what the user typed and then run once. It is not
 * kept afterwards and nothing asks it to run twice, so it holds only what its
 * own line said — a task to add, a number, a day — and is given the task list
 * to act on rather than remembering one.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks   the task list to read, or to change and then save.
     * @param ui      what to tell the user through.
     * @param storage where to write the task list, for commands that change it.
     * @throws BobException if the command cannot be carried out — because it
     *                      names a task that does not exist, or because the
     *                      changed list could not be written to disk.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BobException;

    /**
     * Returns whether the conversation should end once this command has run.
     *
     * <p>Answered here for every command, so that only the one command that ends
     * the conversation has to say anything about it. The alternative — asking
     * whether the command is an {@link ExitCommand} — would have the loop testing
     * for a particular class instead of asking a question any command can answer.
     */
    public boolean isExit() {
        return false;
    }
}
