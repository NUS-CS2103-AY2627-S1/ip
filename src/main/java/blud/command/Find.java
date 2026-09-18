package blud.command;

import java.util.List;
import java.util.Locale;

import blud.exception.CommandFormatException;
import blud.model.Task;
import blud.model.TaskList;

/** Searches the task list for tasks whose descriptions contain a keyword. */
public class Find {
    private static final String MATCHING_TASKS_PREFACE = "Here are the matching tasks in your list:";
    private static final String NO_MATCHING_TASKS_MESSAGE = "There are no matching tasks in your list.";
    private final String keyword;

    /** Creates a find command from the complete user input. */
    public Find(String userInput) {
        if (userInput == null) {
            throw new CommandFormatException("Please provide a keyword to find.");
        }

        String[] inputParts = userInput.trim().split("\\s+", 2);
        if (inputParts.length < 2 || !"find".equalsIgnoreCase(inputParts[0])) {
            throw new CommandFormatException("Please provide a keyword to find.");
        }
        this.keyword = inputParts[1].trim();
        if (this.keyword.isEmpty() || this.keyword.contains("/")) {
            throw new CommandFormatException("Please provide a keyword to find.");
        }
    }

    /** Returns the matching tasks formatted for display. */
    public String execute(TaskList taskList) {
        List<Task> matchingTasks = getMatchingTasks(taskList);
        if (matchingTasks.isEmpty()) {
            return NO_MATCHING_TASKS_MESSAGE;
        }

        StringBuilder response = new StringBuilder(MATCHING_TASKS_PREFACE);
        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(matchingTasks.get(i));
        }
        return response.toString();
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring letter case. */
    private List<Task> getMatchingTasks(TaskList taskList) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return taskList.getTaskList().stream().
                filter(task -> task.getTaskDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)).
                toList();
    }
}
