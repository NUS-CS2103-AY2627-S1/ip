package zabud.commands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * Base class for command tokens that validate and convert one input value.
 */
public abstract class Token {
    /**
     * Token name displayed in validation guidance.
     */
    protected final String name;
    /**
     * Raw value supplied after the token.
     */
    protected final String value;

    /**
     * Creates a token with a name and raw value.
     *
     * @param name token name, with or without a leading slash.
     * @param value raw value following the token name.
     */
    protected Token(String name, String value) {
        this.name = name.startsWith("/") ? name.substring(1) : name;
        this.value = value.trim();
    }

    /**
     * Builds the token portion appended to a command hint.
     *
     * @param tokens tokens in command syntax order.
     * @return token names and their value hints, separated by spaces.
     */
    public static String composeHints(Token... tokens) {
        return Arrays.stream(tokens)
                .map(token -> "/" + token.name + " " + token.hint())
                .collect(Collectors.joining(" "));
    }

    /**
     * Builds one requirement line for each distinct token value type.
     * Tokens with the same hint placeholder share one requirement line.
     *
     * @param tokens tokens whose value requirements should be described.
     * @return distinct requirement lines separated by newlines.
     */
    public static String composeRequirements(Token... tokens) {
        LinkedHashMap<String, String> requirements = new LinkedHashMap<>();
        for (Token token : tokens) {
            requirements.putIfAbsent(token.hint(), token.requirement());
        }
        return requirements.entrySet().stream()
                .map(entry -> " - " + entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns the requirement for values accepted by this token type.
     *
     * @return plain-language value requirement.
     */
    protected abstract String requirement();

    /**
     * Checks whether this typed token contains a valid value.
     *
     * @return whether the token value is valid.
     */
    public abstract boolean isValid();

    /**
     * Returns the placeholder used for this token's value.
     *
     * @return token value placeholder.
     */
    public abstract String hint();

}
