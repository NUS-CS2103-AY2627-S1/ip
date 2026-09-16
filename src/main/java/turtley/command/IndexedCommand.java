package turtley.command;

import turtley.exception.TurtleyException;
import turtley.model.TaskList;

/**
 * Base class for commands that address a task by its one-based user index.
 */
public abstract class IndexedCommand extends Command {

    /**
     * Creates a command that addresses a task by index.
     */
    protected IndexedCommand() {
    }

    /**
     * Parses a one-based task number into a zero-based index.
     *
     * @param input the task number entered by the user.
     * @return the corresponding zero-based task index.
     */
    protected int parseTaskIndex(String input) {
        if (input == null || input.isEmpty()) {
            throw new TurtleyException("Please provide a valid task number.");
        }

        int sign = 1;
        int digitStart = 0;
        char firstCharacter = input.charAt(0);
        if (firstCharacter == '-' || firstCharacter == '+') {
            sign = firstCharacter == '-' ? -1 : 1;
            digitStart = 1;
        }
        if (digitStart == input.length()) {
            throw new TurtleyException("Please provide a valid task number.");
        }

        long maximumAbsoluteValue = sign < 0 ? 2_147_483_648L : Integer.MAX_VALUE;
        long absoluteValue = 0;
        for (int i = digitStart; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);
            if (currentCharacter < '0' || currentCharacter > '9') {
                throw new TurtleyException("Please provide a valid task number.");
            }
            int digit = currentCharacter - '0';
            if (absoluteValue > (maximumAbsoluteValue - digit) / 10) {
                throw new TurtleyException("Please provide a valid task number.");
            }
            absoluteValue = absoluteValue * 10 + digit;
        }

        return (int) (sign * absoluteValue) - 1;
    }

    /**
     * Gets a valid task index or throws the standard range error.
     *
     * @param input the user-supplied task number.
     * @param tasks the task list.
     * @return the zero-based task index.
     */
    protected int requireTaskIndex(String input, TaskList tasks) {
        int taskIndex = parseTaskIndex(input);
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new TurtleyException("Task number is not in your list.");
        }
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A required task index must refer to an existing task.";
        return taskIndex;
    }
}
