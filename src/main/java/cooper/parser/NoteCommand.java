package cooper.parser;

/** Parsed note operation; number and text are used only by operations that require them. */
public record NoteCommand(Operation operation, int number, String text) {
    /** Supported standalone note operations. */
    public enum Operation {
        ADD, LIST, FIND, EDIT, DELETE
    }
}
