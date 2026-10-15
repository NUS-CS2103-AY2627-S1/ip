package xiaozhi.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Keeps track of the undoable commands XiaoZhi has executed, in the order
 * they happened, so {@link UndoCommand} can reverse the most recent one.
 * <p>
 * Only commands whose {@link Command#isUndoable()} returns {@code true} are
 * ever pushed here (see {@code xiaozhi.XiaoZhi}, which does the pushing right
 * after a command executes successfully); read-only commands such as
 * {@code list} and {@code find} never enter the history at all, and
 * {@link UndoCommand} itself does not push a further entry, so there is no
 * "redo".
 */
public class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();

    /**
     * Records a command as the most recent undoable action, on top of any
     * earlier ones.
     *
     * @param command The command that was just executed. Must be undoable.
     */
    public void push(Command command) {
        assert command != null && command.isUndoable()
                : "Only undoable commands should ever be pushed onto the history.";
        undoStack.push(command);
    }

    /**
     * Returns whether there is any command left to undo.
     *
     * @return {@code true} if at least one undoable command has been executed
     *         and not yet undone, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return undoStack.isEmpty();
    }

    /**
     * Removes and returns the most recently executed command that has not yet
     * been undone.
     *
     * @return The most recent undoable command.
     */
    public Command pop() {
        assert !undoStack.isEmpty() : "Caller (UndoCommand) must check isEmpty() first.";
        return undoStack.pop();
    }
}
