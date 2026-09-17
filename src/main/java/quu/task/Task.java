package quu.task;

import java.util.Map;
import java.util.Set;

import quu.exception.MissingArgumentException;
import quu.exception.QuuException;
import quu.exception.UnsupportedEditException;

/**
 * A single item in the task list.
 *
 * <p>{@code Task} holds the parts every kind of task shares: a description and
 * whether it has been completed. Subclasses such as {@link ToDo}, {@link Deadline}
 * and {@link Event} add their own extra details and their own display prefix.
 */
public class Task {
    /** Key under which {@link #withEdits(Map)} looks up a new description. */
    public static final String KEY_DESCRIPTION = "description";

    private static final Set<String> EDIT_FLAGS = Set.of();
    private static final String EDIT_USAGE = "edit <task number> <task>";

    private final String description;
    private boolean isDone = false;

    /**
     * Creates a task that is not yet done.
     *
     * @param description text describing what the task is
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the text describing this task, without any status or type markers.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Marks this task as done. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns a copy of this task with the requested edits applied.
     *
     * <p>Each subclass overrides this to rebuild itself through its own constructor, so an
     * edit is checked exactly like the command that creates the task in the first place.
     * Details the edits do not mention are carried over unchanged, which is what lets the
     * user change one detail without restating the others.
     *
     * <p>The copy comes back not done, because it is built fresh. Carrying the done status
     * over is left to the caller so that it is done in one place rather than in every
     * subclass.
     *
     * @param edits the requested edits, keyed by {@link #KEY_DESCRIPTION} for the
     *     description and by flag name, without its slash, for everything else
     * @return a new task holding the edited details
     * @throws QuuException if an edit names a detail this kind of task does not have, or
     *     leaves the task with a detail it cannot accept
     */
    public Task withEdits(Map<String, String> edits) throws QuuException {
        requireSupportedEdits(edits, EDIT_FLAGS, EDIT_USAGE);
        return new Task(resolveEditedDescription(edits, EDIT_USAGE));
    }

    /**
     * Rejects any edit naming a detail this kind of task does not have.
     *
     * <p>Without this check an edit such as {@code /by} on an event would simply never be
     * read, and the user would be told the edit succeeded when nothing had changed.
     *
     * @param edits the requested edits, as passed to {@link #withEdits(Map)}
     * @param supportedFlags the flag names this kind of task accepts, without their slashes
     * @param usage the expected form of the command, shown to the user on failure
     * @throws UnsupportedEditException if an edit names an unsupported flag
     */
    protected void requireSupportedEdits(Map<String, String> edits, Set<String> supportedFlags, String usage)
            throws UnsupportedEditException {
        for (String editKey : edits.keySet()) {
            boolean isSupported = editKey.equals(KEY_DESCRIPTION) || supportedFlags.contains(editKey);
            if (!isSupported) {
                throw new UnsupportedEditException(editKey, usage);
            }
        }
    }

    /**
     * Returns the description the edits ask for, or the current one if they leave it alone.
     *
     * @param edits the requested edits, as passed to {@link #withEdits(Map)}
     * @param usage the expected form of the command, shown to the user on failure
     * @return the description the edited task should carry
     * @throws MissingArgumentException if the edits ask for a blank description
     */
    protected String resolveEditedDescription(Map<String, String> edits, String usage)
            throws MissingArgumentException {
        String editedDescription = edits.getOrDefault(KEY_DESCRIPTION, description);
        if (editedDescription.trim().isEmpty()) {
            throw new MissingArgumentException(usage);
        }
        return editedDescription;
    }

    /**
     * Returns the task as shown to the user, for example {@code [X] read book}.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return String.format("[%s] %s", statusIcon, description);
    }

    /**
     * Returns the task in the format used in the save file, for example
     * {@code | 1 | read book}.
     *
     * @return the save-file representation of this task
     */
    public String toFileString() {
        String doneFlag = isDone ? "1" : "0";
        return String.format("| %s | %s", doneFlag, description);
    }
}
