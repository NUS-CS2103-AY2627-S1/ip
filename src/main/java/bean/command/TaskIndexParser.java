package bean.command;

import bean.exception.BeanListOutOfBoundsException;
import bean.exception.InvalidSyntaxException;

/** Parses and validates task indices supplied to task-manipulation commands. */
final class TaskIndexParser {

    private TaskIndexParser() {
    }

    /** Returns the one-based task index, or {@code -1} when no index is supplied. */
    static int parse(String input, int taskCount) {
        String[] words = input.trim().split("\\s+");
        if (words.length == 1) {
            return -1;
        }

        if (words.length > 2) {
            throw new InvalidSyntaxException(
                    "Oops, please provide only one task index. Usage: " + words[0] + " INDEX", input);
        }

        int index;
        try {
            index = Integer.parseInt(words[1]);
        } catch (NumberFormatException e) {
            throw new InvalidSyntaxException(
                    "Oops, the task index must be a whole number. Usage: " + words[0] + " INDEX", input);
        }

        if (index <= 0 || index > taskCount) {
            throw new BeanListOutOfBoundsException(
                    "Oops, you've keyed in an invalid Task index! (" + index + " of "
                            + taskCount + ")", index);
        }
        return index;
    }
}
