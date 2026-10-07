package drax;

import java.util.Stack;

/**
 * Tracks task-list snapshots for undo and redo.
 * The main history includes the current state.
 */
public class TaskHistory {
    private final Stack<TaskMemento> mementos;
    private final Stack<TaskMemento> undoneMementos;

    /**
     * Creates an empty undo and redo history.
     */
    public TaskHistory() {
        this.mementos = new Stack<>();
        this.undoneMementos = new Stack<>();
    }

    /**
     * Records a snapshot as the current state and discards redo history.
     *
     * @param memento snapshot of the initial state or a newly applied change.
     */
    public void addMemento(TaskMemento memento) {
        clearUndoneMementos();
        this.mementos.push(memento);
    }

    /**
     * Discards all snapshots available for redo.
     */
    private void clearUndoneMementos() {
        this.undoneMementos.clear();
    }

    /**
     * Moves the current snapshot to the redo history and returns the
     * preceding snapshot for restoration.
     *
     * @return snapshot to restore, or {@code null} if undo is unavailable
     */
    public TaskMemento getPreviousMemento() {
        if (this.mementos.size() <= 1) {
            return null;
        }
        TaskMemento currentMemento = this.mementos.pop();
        TaskMemento previousMemento = this.mementos.peek();
        this.undoneMementos.push(currentMemento);
        return previousMemento;
    }

    /**
     * Moves the most recently undone snapshot back into the main history
     * and returns it for restoration.
     *
     * @return snapshot to restore, or {@code null} if redo is unavailable
     */
    public TaskMemento getNextMemento() {
        if (this.undoneMementos.isEmpty()) {
            return null;
        }
        TaskMemento nextMemento = this.undoneMementos.pop();
        this.mementos.push(nextMemento);
        return nextMemento;
    }
}
