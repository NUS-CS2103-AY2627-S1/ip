package zabud.commands;

/**
 * Stores one named value extracted from a command line.
 *
 * @param name token name without a leading slash.
 * @param value trimmed text associated with the token.
 */
public record ParsedToken(String name, String value) {
    /**
     * Creates a normalized, immutable parser result.
     */
    public ParsedToken {
        name = name.trim();
        value = value.trim();
    }
}
