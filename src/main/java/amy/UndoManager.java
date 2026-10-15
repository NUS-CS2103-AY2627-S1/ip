package amy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import amy.task.Task;

/** Stores task-list snapshots for undoing successful changes during one session. */
public class UndoManager {
    private final Deque<UndoEntry> history = new ArrayDeque<>();

    /** Records a task-list state before a successful state-changing command. */
    public void record(ArrayList<Task> tasks, String description) {
        history.push(new UndoEntry(copyTasks(tasks), description));
    }

    /** Returns whether an undo operation is available. */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /** Returns the description of the most recent undoable change. */
    public String getLatestDescription() {
        return history.peek().description();
    }

    /** Removes and returns the most recent task-list snapshot. */
    public ArrayList<Task> removeLatestSnapshot() {
        return history.pop().tasks();
    }

    private static ArrayList<Task> copyTasks(ArrayList<Task> tasks) {
        ArrayList<Task> copy = new ArrayList<>();
        for (Task task : tasks) {
            copy.add(task.copy());
        }
        return copy;
    }

    private record UndoEntry(ArrayList<Task> tasks, String description) {
    }
}
