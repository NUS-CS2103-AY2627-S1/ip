package verity.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import verity.client.Client;
import verity.exception.VerityException;
import verity.storage.StorageTextCodec;

/**
 * Represents a task that can be tracked and stored.
 */
public abstract class Task {
    private final String description;
    private final ArrayList<String> clientIds;
    private final ArrayList<String> formerClientNotes;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null.";

        this.description = description;
        this.clientIds = new ArrayList<>();
        this.formerClientNotes = new ArrayList<>();
        this.isDone = false;
    }

    /**
     * Returns a display string containing the task's completion status and description.
     *
     * @return Display string for the task.
     */
    public String getStatus() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     * Returns whether this task's description contains the keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return True if the description contains the keyword.
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    protected final String getDescription() {
        return description;
    }

    /**
     * Associates a client with this task.
     *
     * @param clientId Client ID to associate.
     * @throws VerityException If the client ID is malformed or duplicated.
     */
    public void addClientId(String clientId) throws VerityException {
        String canonicalId = Client.formatId(Client.parseId(clientId));
        if (clientIds.contains(canonicalId)) {
            throw new VerityException(
                    "Client " + canonicalId + " is already associated with this task.");
        }
        clientIds.add(canonicalId);
        clientIds.sort(Comparator.comparingInt(Task::numericClientId));
    }

    /**
     * Removes a client association from this task.
     *
     * @param clientId Client ID to remove.
     * @return True if the association existed.
     * @throws VerityException If the client ID is malformed.
     */
    public boolean removeClientId(String clientId) throws VerityException {
        String canonicalId = Client.formatId(Client.parseId(clientId));
        return clientIds.remove(canonicalId);
    }

    /**
     * Returns whether this task is associated with a client.
     *
     * @param clientId Client ID to find.
     * @return True if the client is associated.
     */
    public boolean hasClientId(String clientId) {
        try {
            return clientIds.contains(Client.formatId(Client.parseId(clientId)));
        } catch (VerityException exception) {
            return false;
        }
    }

    public List<String> getClientIds() {
        return List.copyOf(clientIds);
    }

    /**
     * Adds a note recording a deleted former client.
     *
     * @param note Former-client note.
     */
    public void addFormerClientNote(String note) {
        assert note != null && !note.isBlank()
                : "Former-client note must not be blank.";
        formerClientNotes.add(note);
    }

    public List<String> getFormerClientNotes() {
        return List.copyOf(formerClientNotes);
    }

    /**
     * Returns a line representing this task in the data file.
     *
     * @return Serialized task data.
     */
    public abstract String serialize();

    /**
     * Appends client associations and former-client notes to serialized task data.
     *
     * @param baseTaskData Existing task fields.
     * @return Extended serialized task data.
     */
    protected final String appendClientMetadata(String baseTaskData) {
        String notes = StorageTextCodec.encode(String.join("\n", formerClientNotes));
        return baseTaskData + "\t" + String.join(",", clientIds) + "\t" + notes;
    }

    /**
     * Returns whether this task occurs on the specified date.
     *
     * @param date Date to check.
     * @return True if this task occurs on the date.
     */
    public abstract boolean occursOn(LocalDate date);

    protected final String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    private static int numericClientId(String clientId) {
        return Integer.parseInt(clientId.substring(1));
    }
}
