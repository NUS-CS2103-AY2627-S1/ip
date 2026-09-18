package herta.parser;

/**
 * Represents one inclusive, one-based task-number range in an archive command.
 *
 * @param start the first selected task number.
 * @param end the last selected task number.
 */
public record ArchiveRange(int start, int end) {
    /** Validates the one-based inclusive range at its domain boundary. */
    public ArchiveRange {
        if (start <= 0 || end < start) {
            throw new IllegalArgumentException("Archive ranges must be positive and ascending.");
        }
    }
}
