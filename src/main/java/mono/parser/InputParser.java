package mono.parser;

import mono.exception.EmptyInputException;

/**
 * Splits a line of user input into a tool name and its arguments.
 */
public class InputParser {
    /** Creates an input parser. */
    public InputParser() {
    }

    /**
     * Parses the tool name and arguments from a line entered by the user.
     *
     * @param input complete input entered by the user
     * @return parsed tool input
     * @throws EmptyInputException if the input contains no non-whitespace characters
     */
    public static ToolInput parse(String input) throws EmptyInputException {
        if (input == null) {
            throw new EmptyInputException("Input cannot be empty!");
        }
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new EmptyInputException("Input cannot be empty!");
        }

        int toolEnd = trimmedInput.indexOf(' ');
        if (toolEnd == -1) {
            return new ToolInput(trimmedInput, "");
        }

        String toolName = trimmedInput.substring(0, toolEnd);
        String arguments = trimmedInput.substring(toolEnd + 1).trim();
        return new ToolInput(toolName, arguments);
    }

    /**
     * Contains the tool name and the text passed to that tool.
     *
     * @param name tool name
     * @param arguments tool arguments
     */
    public record ToolInput(String name, String arguments) {
        /** Verifies the invariant shared by all parsed command values. */
        public ToolInput {
            assert name != null && !name.isEmpty()
                    : "A tool input must have a non-empty tool name";
            assert arguments != null : "A tool input must have non-null arguments";
        }
    }
}
