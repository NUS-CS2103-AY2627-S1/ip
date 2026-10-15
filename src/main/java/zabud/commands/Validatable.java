package zabud.commands;

import java.util.List;
import java.util.stream.IntStream;

import zabud.session.Session;

/**
 * Validates parsed command values and explains invalid input.
 */
public interface Validatable {
    /**
     * Placeholder used for a required task description.
     */
    String DESCRIPTION = "DESCRIPTION";
    /**
     * Placeholder used for a required one-based task number.
     */
    String TASK_NUMBER = "TASK_NUMBER";
    /**
     * Requirement shown for task descriptions.
     */
    String DESCRIPTION_REQUIREMENT =
            " - DESCRIPTION: enter a description containing at least one non-space character.";
    /**
     * Requirement shown for task numbers.
     */
    String TASK_NUMBER_REQUIREMENT =
            " - TASK_NUMBER: enter the number of an existing task.";

    /**
     * Checks parsed values before command construction.
     *
     * @param tokens parsed values supplied after the command name.
     * @param session current application session.
     * @return whether the values can be passed to {@link Buildable#build(List, Session)}.
     */
    boolean isValid(List<ParsedToken> tokens, Session session);

    /**
     * Returns guidance for correcting invalid input.
     *
     * @return guidance for correcting invalid input.
     */
    String hint();

    /**
     * Formats a complete invalid-input hint from syntax and requirement lines.
     *
     * @param syntax command syntax containing named placeholders.
     * @param requirements requirement lines for those placeholders.
     * @return formatted command hint.
     */
    static String formatHint(String syntax, String... requirements) {
        return " Use '" + syntax + "'.\n\n" + String.join("\n", requirements);
    }

    /**
     * Checks that parsed values have exactly the expected names and order.
     *
     * @param tokens parsed values to inspect.
     * @param names expected names in command syntax order.
     * @return whether the parsed values match the command syntax.
     */
    static boolean hasTokenNames(List<ParsedToken> tokens, String... names) {
        return tokens.size() == names.length
                && IntStream.range(0, names.length).allMatch(index -> tokens.get(index).name().equals(names[index]));
    }

    /**
     * Parses the default value as a one-based task number.
     *
     * @param tokens parsed values supplied after the command name.
     * @return parsed task number, or {@code -1} when it is not an integer.
     */
    static int taskNumber(List<ParsedToken> tokens) {
        try {
            return tokens.isEmpty() ? -1 : Integer.parseInt(tokens.getFirst().value());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Checks input containing one existing task number.
     *
     * @param tokens parsed values supplied after the command name.
     * @param session current application session.
     * @return whether the input identifies an existing task.
     */
    static boolean hasExistingTaskNumber(List<ParsedToken> tokens, Session session) {
        int taskNumber = taskNumber(tokens);
        return hasTokenNames(tokens, Parser.DEFAULT_TOKEN)
                && taskNumber > 0 && session.getTaskList().get(taskNumber) != null;
    }
}
