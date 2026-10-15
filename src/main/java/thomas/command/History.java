package thomas.command;

import java.util.ArrayList;

import thomas.ThomasException;
import thomas.task.Task;
import thomas.task.TaskList;

/**
 * What it would take to reverse each change made to the task list this session.
 * <p>
 * Every command that changes the list records the reverse of what it did, and
 * {@code undo} applies the most recent such record. Recording the reverse rather
 * than a copy of the whole list is what makes many undos cheap: a step is a
 * number and a reference, not a second list.
 * <p>
 * A command that changes nothing records nothing, so {@code list}, {@code find}
 * and {@code on} are passed over without having to be told to be -- and a command
 * that fails records nothing either, since a step is pushed only after the change
 * it reverses has actually happened.
 * <p>
 * Steps are applied strictly last in, first out, and {@link #undo(TaskList)} is
 * the only way to reach one. That is what makes every number a step carries
 * valid at the moment it is replayed: the list is necessarily back in the state
 * it was in when the step was recorded. Nothing here therefore reports a bad
 * number to the user; a bad number would be a mistake in the code, and is
 * asserted against instead.
 * <p>
 * The history lives in memory and dies with the program. It is deliberately not
 * saved: it would be a second file format to keep correct in the one place where
 * a mistake silently loses tasks, for a convenience that is only ever wanted
 * within the session that earned it.
 */
public class History {
    /** Told to an {@code undo} with nothing left to undo. */
    private static final String MESSAGE_NOTHING_TO_UNDO = "I can't reverse any further! There's nothing to undo.";

    /** Which of the three reversals a step is. */
    private enum Kind {
        /** Take the task at a number back out of the list, undoing an add. */
        REMOVE,

        /** Put a task back at a number, undoing a delete. */
        INSERT,

        /** Set a task's completion back to what it was, undoing a mark or unmark. */
        SET_DONE
    }

    /**
     * One reversal, and the line that made it necessary.
     * <p>
     * Private, and built only by the {@code push} methods below, so that the rule
     * that only a {@code History} creates and applies a step is checked by the
     * compiler rather than left to whoever reads this next. Nothing outside ever
     * holds one: {@link History#undo(TaskList)} applies the step itself and hands
     * back only the line to quote at the user.
     * <p>
     * One class covers all three kinds rather than three covering one each. The
     * fields a kind does not use go unread -- {@code task} is null for a
     * {@code REMOVE}, {@code taskNumber} unused for a {@code SET_DONE} -- which
     * is the price of keeping the whole history in one list, in the one order
     * that matters.
     */
    private static class UndoStep {
        /** Which reversal to carry out. */
        private final Kind kind;

        /** The number the task carries, counting from 1. Unused by SET_DONE. */
        private final int taskNumber;

        /** The task to put back or to change. Null for REMOVE. */
        private final Task task;

        /** What the completion flag was before. Read only by SET_DONE. */
        private final boolean wasDone;

        /** The line the user typed to cause the change, quoted back on undo. */
        private final String typedLine;

        UndoStep(Kind kind, int taskNumber, Task task, boolean wasDone, String typedLine) {
            this.kind = kind;
            this.taskNumber = taskNumber;
            this.task = task;
            this.wasDone = wasDone;
            this.typedLine = typedLine;
        }

        /**
         * Carries out this reversal.
         * <p>
         * The {@code default} branch is unreachable as the three kinds stand, and
         * is here so that a fourth added later fails loudly the first time it is
         * undone rather than being quietly left un-undoable. {@link thomas.parser.Parser}
         * guards its own enum switch the same way.
         *
         * @param tasks The list to put back as it was.
         * @throws ThomasException Never in practice. {@code REMOVE} goes through
         *                         {@link TaskList#deleteByNumber(int)}, which
         *                         reports a number the user got wrong; replaying a
         *                         step, the number is one the list itself gave out.
         */
        private void applyTo(TaskList tasks) throws ThomasException {
            switch (kind) {
                case REMOVE -> tasks.deleteByNumber(taskNumber);
                case INSERT -> tasks.insertByNumber(taskNumber, task);
                case SET_DONE -> {
                    if (wasDone) {
                        task.markAsDone();
                    } else {
                        task.unmarkAsDone();
                    }
                }
                default -> throw new AssertionError("Unhandled kind of undo step: " + kind);
            }
        }
    }

    /**
     * The reversals, oldest first, so the last is the next to be applied.
     * <p>
     * An {@code ArrayList} used as a stack: it grows with the session, and the
     * end of it is the most recent change.
     */
    private final ArrayList<UndoStep> steps = new ArrayList<>();

    /**
     * The line being carried out, stamped onto whatever step it pushes.
     * <p>
     * Held here rather than passed to each {@code push} because a command knows
     * what it did but not how it was asked for: {@code delete 2} reaches
     * {@link thomas.command.DeleteCommand} as the number 2 alone, and rebuilding
     * the line from that would lose however the user actually spaced it.
     */
    private String currentLine = "";

    /**
     * Notes the line about to be carried out.
     * <p>
     * Called before every command, whether or not it turns out to change
     * anything, since which commands change the list is not this class's to know.
     *
     * @param typedLine The line exactly as the user typed it.
     */
    public void startCommand(String typedLine) {
        assert typedLine != null : "A command was run without the line that asked for it";
        this.currentLine = typedLine;
    }

    /**
     * Records that a task was added, and would be reversed by taking it away.
     *
     * @param taskNumber The number the new task carries, counting from 1.
     */
    public void pushRemove(int taskNumber) {
        steps.add(new UndoStep(Kind.REMOVE, taskNumber, null, false, currentLine));
    }

    /**
     * Records that a task was removed, and would be reversed by putting it back.
     *
     * @param taskNumber The number it carried, counting from 1.
     * @param task The task that was removed.
     */
    public void pushInsert(int taskNumber, Task task) {
        assert task != null : "A removed task must be kept if the removal is to be undone";
        steps.add(new UndoStep(Kind.INSERT, taskNumber, task, false, currentLine));
    }

    /**
     * Records what a task's completion was before it was changed.
     * <p>
     * The flag as it was, not the opposite of what it became: marking a task that
     * is already done changes nothing, and an undo that flipped the flag would
     * take away a tick the command never put there.
     * <p>
     * The task itself rather than its number, because that is what the commands
     * already work with, and because a reference cannot come to mean a different
     * task the way a number could.
     *
     * @param task The task whose completion changed.
     * @param wasDone Whether it was done before the change.
     */
    public void pushSetDone(Task task, boolean wasDone) {
        assert task != null : "A task's completion cannot be put back without the task";
        steps.add(new UndoStep(Kind.SET_DONE, 0, task, wasDone, currentLine));
    }

    /**
     * Reverses the most recent change and says which line made it.
     *
     * @param tasks The list to put back as it was.
     * @return The line that had been typed to make the change now reversed.
     * @throws ThomasException If nothing has changed the list that has not already
     *                         been undone.
     */
    public String undo(TaskList tasks) throws ThomasException {
        if (steps.isEmpty()) {
            throw new ThomasException(MESSAGE_NOTHING_TO_UNDO);
        }
        UndoStep step = steps.removeLast();
        step.applyTo(tasks);
        return step.typedLine;
    }
}
