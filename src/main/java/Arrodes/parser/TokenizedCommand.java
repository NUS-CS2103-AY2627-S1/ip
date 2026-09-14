package arrodes.parser;

import java.util.Collections;
import java.util.Map;

/** Immutable result of splitting a raw command into its logical components. */
public class TokenizedCommand {
    /** Command keyword. */
    private final String command;
    /** Free-text description, if present. */
    private final String description;
    /** Slash-prefixed command parameters. */
    private final Map<String, String> parameters;

    /**
     * Creates a tokenized command.
     * @param command command keyword
     * @param description free-text description
     * @param parameters parsed parameter mappings, or {@code null} for no parameters
     */
    public TokenizedCommand(String command, String description, Map<String, String> parameters) {
        this.command = command;
        this.description = description;
        this.parameters = parameters == null ? Collections.emptyMap() : Map.copyOf(parameters);
    }

    /**
     * Returns the command keyword.
     * @return command keyword
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the command description.
     * @return description text
     */
    public String getDescription() {
        return description;
    }
    /**
     * Returns whether a non-blank description was supplied.
     * @return {@code true} when a description is present
     */
    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
    /**
     * Returns the parsed parameter mappings.
     * @return command parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Returns whether at least one parameter was supplied.
     * @return {@code true} when parameters are present
     */
    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
}
