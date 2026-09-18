package habi;

import java.util.ArrayList;
import java.util.List;

/** Holds every persisted collection managed by HABI. */
public class HabiData {
    private final ArrayList<Task> tasks;
    private final ArrayList<Note> notes;

    /**
     * Creates a data snapshot from task and note collections.
     *
     * @param tasks Tasks to include in the snapshot.
     * @param notes Notes to include in the snapshot.
     */
    public HabiData(List<Task> tasks, List<Note> notes) {
        this.tasks = new ArrayList<>(tasks);
        this.notes = new ArrayList<>(notes);
    }

    /**
     * Returns a mutable copy of the persisted tasks.
     *
     * @return Tasks in storage order.
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns a mutable copy of the persisted notes.
     *
     * @return Notes in storage order.
     */
    public ArrayList<Note> getNotes() {
        return new ArrayList<>(notes);
    }
}
