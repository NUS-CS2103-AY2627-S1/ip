package aster.command;

import aster.exception.AsterException;
import aster.task.TaskList;

/**
 * A command that names one task by its number, such as {@code mark 2}.
 *
 * <p>Whether the argument names a task depends partly on the list as it stands when
 * the command runs, so the checks cannot all be made while the command is being built.
 * Rather than splitting them, all four are kept together here and run at that point,
 * which is what preserves the order they have always run in and the messages they
 * produce:
 *
 * <ol>
 *   <li>no number was given at all;</li>
 *   <li>the list is empty, so no number could name a task;</li>
 *   <li>what was given is not a number;</li>
 *   <li>the number is outside the list.</li>
 * </ol>
 *
 * <p>The second check comes before the third on purpose. Telling someone with an empty
 * list that their list is empty is more useful than telling them their word is not a
 * number, and keeping the four checks together is what keeps that order.
 */
abstract class IndexedCommand extends Command {
    private static final int EXAMPLE_TASK_NUMBER = 2;
    // Spelled out rather than \d, so that no reading of the pattern can let in digits
    // from other scripts, which Integer.parseInt would otherwise accept.
    private static final String PLAIN_DIGITS = "[0-9]+";

    private final CommandType type;
    private final String arguments;

    /**
     * Creates a command that acts on the task the given text names.
     *
     * @param type the command being carried out, named in the messages.
     * @param arguments everything the user typed after the keyword.
     */
    protected IndexedCommand(CommandType type, String arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    /**
     * Returns the position of the task this command names.
     *
     * <p>Every way the number can be unusable is turned into an {@link AsterException},
     * so no {@code NumberFormatException} or index exception reaches the user.
     *
     * <p>Only plain ASCII digits make a number. A sign, a decimal point, a space or a digit
     * from another script is not a number, even though {@code Integer.parseInt} would read
     * some of them. A digit string too long to fit an {@code int} is still a number, just
     * one too large for any list, so it is reported as out of range.
     *
     * @param tasks the task list the number refers to.
     * @return the 0-based position of the task the command refers to.
     * @throws AsterException if the number is missing, not a number, or outside the list.
     */
    protected int resolveIndex(TaskList tasks) throws AsterException {
        String keyword = type.getKeyword();
        String usageExample = "Try: " + keyword + " " + EXAMPLE_TASK_NUMBER;
        int taskCount = tasks.size();
        if (arguments.isEmpty()) {
            throw new AsterException("Tell me which task to " + keyword + ". " + usageExample);
        }
        if (taskCount == 0) {
            throw new AsterException("Your list is empty, so there is nothing to " + keyword
                    + " yet.");
        }
        if (!arguments.matches(PLAIN_DIGITS)) {
            throw new AsterException("\"" + arguments + "\" is not a task number. " + usageExample);
        }
        int number;
        try {
            number = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            // Only plain digits reach here, so the number is simply too large for an int.
            throw new AsterException(buildOutOfRangeMessage(arguments, taskCount));
        }
        if (number < 1 || number > taskCount) {
            throw new AsterException(buildOutOfRangeMessage(String.valueOf(number), taskCount));
        }
        return number - 1;
    }

    /**
     * Returns the message for a task number that names no task in the list.
     *
     * @param numberText the number as it is to be shown.
     * @param taskCount the number of tasks in the list.
     * @return the explanation, naming the range of numbers that would be accepted.
     */
    private static String buildOutOfRangeMessage(String numberText, int taskCount) {
        return "You have " + taskCount + " " + getTaskNoun(taskCount) + ", so " + numberText
                + " is out of range. Pick a number from 1 to " + taskCount + ".";
    }

    /**
     * Returns the singular or plural form of {@code task} for a count.
     *
     * <p>Kept here rather than shared with the user interface, so that deciding a word
     * for a message does not make this class depend on how anything is displayed.
     *
     * @param count the number of tasks.
     * @return {@code "task"} if the count is one, otherwise {@code "tasks"}.
     */
    private static String getTaskNoun(int count) {
        return count == 1 ? "task" : "tasks";
    }
}
