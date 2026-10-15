package windy.command;

import windy.task.TaskList;

/** Stores independent snapshots for the latest task change in this session. */
public class UndoHistory {
    /** Identifies the one history operation currently available. */
    private enum State {
        EMPTY, CAN_UNDO, CAN_REDO
    }

    private TaskList before;
    private TaskList after;
    private State state = State.EMPTY;

    /** Records a successful change, replacing any earlier history. */
    void record(TaskList previous, TaskList current) {
        before = previous.copy();
        after = current.copy();
        state = State.CAN_UNDO;
    }

    /** Returns a copy of the requested state, or null when unavailable. */
    TaskList getTarget(boolean isUndo) {
        if (state != (isUndo ? State.CAN_UNDO : State.CAN_REDO)) {
            return null;
        }
        return (isUndo ? before : after).copy();
    }

    /** Advances history only after the restored state has been saved. */
    void completeRestore(boolean isUndo) {
        state = isUndo ? State.CAN_REDO : State.CAN_UNDO;
    }
}
