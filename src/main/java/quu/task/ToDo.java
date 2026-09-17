package quu.task;

import java.util.Map;
import java.util.Set;

import quu.exception.MissingArgumentException;
import quu.exception.QuuException;

/**
 * A task with only a description and no associated date.
 */
public class ToDo extends Task {
    private static final Set<String> EDIT_FLAGS = Set.of();
    private static final String EDIT_USAGE = "edit <task number> <task>";

    /**
     * Creates a to-do.
     *
     * @param description text describing what the task is
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Rebuilds a to-do from a line of the save file.
     *
     * <p>The caller must have checked that {@code fields} holds all three parts;
     * {@link quu.storage.Storage#readFile()} rejects a shorter line before calling this.
     *
     * @param fields the save-file line split into type, done flag and description
     * @return the reconstructed to-do
     * @throws MissingArgumentException if the description is missing or blank
     */
    public static ToDo fromFileString(String[] fields) throws MissingArgumentException {
        assert fields.length >= 3 : "Storage checks the field count before rebuilding a task";
        try {
            if (fields[2].trim().isEmpty()) {
                throw new MissingArgumentException(fields[0] + " <task>");
            }
            return new ToDo(fields[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MissingArgumentException(fields[0] + " <task>");
        }
    }

    /**
     * Returns a copy of this to-do with the requested edits applied.
     *
     * <p>A to-do has no flags of its own, so the only detail an edit can change is the
     * description.
     *
     * @param edits the requested edits, keyed by {@link Task#KEY_DESCRIPTION}
     * @return a new to-do holding the edited description
     * @throws QuuException if an edit names a flag, or asks for a blank description
     */
    @Override
    public ToDo withEdits(Map<String, String> edits) throws QuuException {
        requireSupportedEdits(edits, EDIT_FLAGS, EDIT_USAGE);
        return new ToDo(resolveEditedDescription(edits, EDIT_USAGE));
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return "T " + super.toFileString();
    }
}
