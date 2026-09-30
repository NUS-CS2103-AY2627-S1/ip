package bean.command;

import bean.exception.InvalidSyntaxException;
import bean.task.BeanList;
import bean.task.Priority;

/** Executes commands that add to-do tasks. */
public class TodoCommand {

    /** Adds a to-do task based on the supplied command input. */
    public static String execute(String input, BeanList taskList) {
        String[] words = input.trim().split("\\s+");
        int priorityIndex = -1;

        for (int i = 1; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("/priority")) {
                priorityIndex = i;
                break;
            }
        }

        int taskEnd = priorityIndex == -1 ? words.length : priorityIndex;
        StringBuilder taskName = CommandText.joinWords(words, 1, taskEnd);

        CommandValidator.checkEmpty(taskName, input, "todo",
                "Todo must have a name!\n\n", "Usage: todo \"TASK\"");

        if (priorityIndex != -1) {
            if (priorityIndex + 1 >= words.length || priorityIndex + 2 != words.length) {
                throw new InvalidSyntaxException(
                        "Uh Oh! Invalid Syntax for adding todo with priority!\n\n"
                        + "Usage: todo \"TASK\" /priority \"{HIGH | MEDIUM | LOW}\"", input);
            }
            Priority priority = Priority.fromString(words[priorityIndex + 1]);
            return taskList.addTodo(taskName.toString().trim(), priority);
        }

        return taskList.addTodo(taskName.toString().trim());
    }
}
