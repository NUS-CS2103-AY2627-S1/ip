package samantha.model;

import samantha.exception.TaskValidationException;

/**
 * Represents a short piece of textual information saved by the user.
 */
public class Note {
    private String content;

    /**
     * Creates a note with the supplied content.
     *
     * @param content note content
     * @throws TaskValidationException if the content is blank or spans multiple lines
     */
    public Note(String content) throws TaskValidationException {
        this.content = validateContent(content);
    }

    /**
     * Updates this note's content.
     *
     * @param content replacement note content
     * @throws TaskValidationException if the content is blank or spans multiple lines
     */
    public void edit(String content) throws TaskValidationException {
        this.content = validateContent(content);
    }

    /**
     * Returns this note's content.
     *
     * @return note content
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns this note in the format used by note storage.
     *
     * @return one serialized note record
     */
    public String toFileString() {
        return content;
    }

    /**
     * Returns the note content for display.
     *
     * @return note content
     */
    @Override
    public String toString() {
        return content;
    }

    /**
     * Validates and normalizes note content before storing it.
     *
     * @param content raw note content
     * @return trimmed, valid note content
     * @throws TaskValidationException if the content is invalid
     */
    private String validateContent(String content) throws TaskValidationException {
        assert content != null : "Note content must not be null";
        if (content.isBlank()) {
            throw new TaskValidationException("The content of a note cannot be empty.");
        }
        if (content.contains("\n") || content.contains("\r")) {
            throw new TaskValidationException("A note must fit on one line.");
        }
        return content.trim();
    }
}
