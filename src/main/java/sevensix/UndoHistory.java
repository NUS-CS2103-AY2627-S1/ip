package sevensix;

import java.util.ArrayDeque;
import java.util.Deque;

/** Remembers successful task changes in reverse order for session-scoped undo. */
public class UndoHistory {
    private final Deque<UndoEntry> entries = new ArrayDeque<>();

    /** Remembers a change before its action modifies the live tasks.
     *
     * @param tasks the task list before the change.
     * @param type the action about to be performed.
     * @param task the affected task before the action.
     */
    public void save(Iterable<Task> tasks, ChangeType type, Task task) {
        entries.push(new UndoEntry(tasks, type, task));
    }

    /** Checks whether a change is available to undo.
     *
     * @return true when an entry is available.
     */
    public boolean hasSnapshot() {
        return !entries.isEmpty();
    }

    /** Copies the history for recovery after a failed command.
     *
     * @return an independently consumable history sharing only immutable entries.
     */
    public UndoHistory copy() {
        UndoHistory copy = new UndoHistory();
        copy.entries.addAll(entries);
        return copy;
    }

    /** Consumes the most recent entry without changing earlier entries.
     *
     * @return the change to reverse.
     */
    public UndoEntry takeEntry() {
        assert hasSnapshot() : "Callers check hasSnapshot before taking an entry";
        return entries.pop();
    }
}
