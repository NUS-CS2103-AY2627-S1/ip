package bean.command;

import bean.exception.InvalidSyntaxException;
import bean.task.BeanList;

/** Executes commands that find tasks by name. */
public class FindCommand {

    /** Finds tasks whose names contain the supplied keyword. */
    public static String execute(String input, BeanList taskList) {
        String[] words = input.trim().split("\\s+", 2);

        if (words.length < 2) {
            throw new InvalidSyntaxException("Uh Oh! Invalid Syntax for find!\n\n"
                    + "Usage: find \"KEYWORD\"", input);
        }

        String keyword = words[1].trim();
        if (keyword.length() >= 2 && keyword.startsWith("\"") && keyword.endsWith("\"")) {
            keyword = keyword.substring(1, keyword.length() - 1).trim();
        }
        if (keyword.isEmpty()) {
            throw new InvalidSyntaxException("Uh Oh! Invalid Syntax for find!\n\n"
                    + "Usage: find \"KEYWORD\"", input);
        }
        return taskList.findTasks(keyword);
    }
}
