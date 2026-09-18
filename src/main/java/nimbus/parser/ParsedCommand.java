package nimbus.parser;

/** Contains a recognised command type and the original command text. */
public record ParsedCommand(CommandType type, String fullText) {
    /** Returns the trimmed text following the command keyword. */
    public String argument() {
        String[] parts = fullText.split("\\s+", 2);
        return parts.length < 2 ? "" : parts[1].trim();
    }
}
