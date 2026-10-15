package littler.command.request;

import java.util.Map;

/**
 * Encapsulates a fully parsed edit request: which task to edit, and the raw field updates to apply.
 */
public final class EditRequest {
    private final int index;
    private final Map<String, String> updates;

    /**
     * Constructs a new EditRequest with the specified task index and field updates.
     *
     * @param index the 1-based index of the task to edit
     * @param updates a map from field delimiter (e.g. "/name") to the new raw value for that field
     */
    public EditRequest(int index, Map<String, String> updates) {
        this.index = index;
        this.updates = updates;
    }

    /**
     * Returns the 1-based index of the task to edit.
     *
     * @return the index of the task
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the map of field updates for the task.
     *
     * @return the updates map
     */
    public Map<String, String> getUpdates() {
        return updates;
    }
}
