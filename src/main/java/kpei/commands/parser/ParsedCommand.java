package kpei.commands.parser;

import java.util.List;
import java.util.Map;

/**
 * Represents the structured result of parsing a raw Command string.
 * Holds the Command type, the primary argument, and any associated named flags.
 */
public class ParsedCommand {
    private String commandType;
    private List<String> arguments;
    private Map<String, String> flags;

    /**
     * Constructs a {@code ParsedCommand} with the given Command type, positional arguments, and flags.
     *
     * @param commandType The primary Command word in lowercase (e.g., "deadline", "list").
     * @param positionalParameters The primary positional argument (e.g., task description or index).
     * @param flaggedParameters A map of Flag names to their corresponding values.
     */
    public ParsedCommand(String commandType, List<String> positionalParameters, Map<String, String> flaggedParameters) {
        this.commandType = commandType;
        this.arguments = positionalParameters;
        this.flags = flaggedParameters;
    }


    public String getCommandType() {
        return commandType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public Map<String, String> getFlags() {
        return flags;
    }
}
