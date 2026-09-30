package bean.command;

import bean.exception.InvalidSyntaxException;
import bean.task.BeanList;
import bean.task.Priority;

/** Executes commands that add deadline tasks. */
public class DeadlineCommand {
    private static final String USAGE = "deadline \"TASK\" /by \"DATE\"";

    /** Adds a deadline task based on the supplied command input. */
    public static String execute(String input, BeanList taskList) {
        String[] words = input.trim().split("\\s+");
        int byIndex = -1;
        int priorityIndex = -1;

        for (int i = 1; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("/by")) {
                if (byIndex != -1) {
                    throw invalidSyntax(input);
                }
                byIndex = i;
            }
            if (words[i].equalsIgnoreCase("/priority")) {
                if (priorityIndex != -1) {
                    throw invalidSyntax(input);
                }
                priorityIndex = i;
            }
        }

        if (byIndex == -1 || (priorityIndex != -1 && priorityIndex < byIndex)) {
            throw invalidSyntax(input);
        }

        if (priorityIndex != -1 && priorityIndex + 2 != words.length) {
            throw invalidSyntax(input);
        }

        StringBuilder taskName = CommandText.joinWords(words, 1, byIndex);
        int dateEnd = priorityIndex == -1 ? words.length : priorityIndex;
        StringBuilder date = CommandText.joinWords(words, byIndex + 1, dateEnd);

        CommandValidator.checkEmpty(taskName, input, "deadline",
                "Deadline must have a name!\n\n", USAGE);
        CommandValidator.checkEmpty(date, input, "deadline",
                "Deadline must have a by date!\n\nTry the format yyyy-mm-dd!\n\n",
                USAGE);

        if (priorityIndex != -1) {
            Priority priority = Priority.fromString(words[priorityIndex + 1]);
            return taskList.addDeadline(taskName.toString().trim(),
                    date.toString().trim(), priority);
        }

        return taskList.addDeadline(taskName.toString().trim(), date.toString().trim());
    }

    private static InvalidSyntaxException invalidSyntax(String input) {
        return new InvalidSyntaxException("Uh Oh! Invalid Syntax for deadline!\n\n"
                + "Usage: deadline \"TASK\" /by \"DATE\" "
                + "[/priority \"{HIGH | MEDIUM | LOW}\"]", input);
    }
}
