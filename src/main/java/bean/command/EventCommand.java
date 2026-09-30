package bean.command;

import bean.exception.InvalidSyntaxException;
import bean.task.BeanList;
import bean.task.Priority;

/** Executes commands that add event tasks. */
public class EventCommand {
    private static final String USAGE = "event \"TASK\" /from \"DATE\" /to \"DATE\"";

    /** Adds an event task based on the supplied command input. */
    public static String execute(String input, BeanList taskList) {
        String[] words = input.trim().split("\\s+");
        int fromIndex = -1;
        int toIndex = -1;
        int priorityIndex = -1;

        for (int i = 1; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("/from")) {
                if (fromIndex != -1) {
                    throw invalidSyntax(input);
                }
                fromIndex = i;
            }
            if (words[i].equalsIgnoreCase("/to")) {
                if (toIndex != -1) {
                    throw invalidSyntax(input);
                }
                toIndex = i;
            }
            if (words[i].equalsIgnoreCase("/priority")) {
                if (priorityIndex != -1) {
                    throw invalidSyntax(input);
                }
                priorityIndex = i;
            }
        }

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex
                || (priorityIndex != -1 && priorityIndex < toIndex)) {
            throw invalidSyntax(input);
        }

        if (priorityIndex != -1 && priorityIndex + 2 != words.length) {
            throw invalidSyntax(input);
        }

        StringBuilder taskName = CommandText.joinWords(words, 1, fromIndex);
        StringBuilder from = CommandText.joinWords(words, fromIndex + 1, toIndex);
        int toEnd = priorityIndex == -1 ? words.length : priorityIndex;
        StringBuilder to = CommandText.joinWords(words, toIndex + 1, toEnd);

        CommandValidator.checkEmpty(taskName, input, "event",
                "Event must have a name!\n\n", USAGE);
        CommandValidator.checkEmpty(from, input, "event",
                "Event must have a from date!\n\nTry the format yyyy-mm-dd!\n\n",
                USAGE);
        CommandValidator.checkEmpty(to, input, "event",
                "Event must have a to date!\n\nTry the format yyyy-mm-dd!\n\n",
                USAGE);

        if (priorityIndex != -1) {
            Priority priority = Priority.fromString(words[priorityIndex + 1]);
            return taskList.addEvent(taskName.toString().trim(),
                    from.toString().trim(), to.toString().trim(), priority);
        }

        return taskList.addEvent(taskName.toString().trim(), from.toString().trim(),
                to.toString().trim());
    }

    private static InvalidSyntaxException invalidSyntax(String input) {
        return new InvalidSyntaxException("Uh Oh! Invalid Syntax for event!\n\n"
                + "Usage: event \"TASK\" /from \"DATE\" /to \"DATE\" "
                + "[/priority \"{HIGH | MEDIUM | LOW}\"]", input);
    }
}
