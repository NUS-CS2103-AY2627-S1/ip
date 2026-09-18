package herta.command;

import java.util.List;
import java.util.function.Predicate;

import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Base class for commands that display tasks selected by a condition.
 */
public abstract class QueryCommand extends Command {

    /**
     * Displays the matching tasks and an explanatory message when there are no matches.
     *
     * @param tasks the task list to search.
     * @param matcher the condition a task must satisfy.
     * @param heading the heading to print before the results.
     * @param emptyMessage the message to print when there are no matches.
     * @param ui the output interface used to display responses.
     */
    protected void showMatchingTasks(TaskList tasks, Predicate<Task> matcher,
                                      String heading, String emptyMessage, UiOutput ui) {
        showMatchingTasks(tasks, matcher, heading, emptyMessage, true, ui);
    }

    /**
     * Displays matching tasks with configurable heading behavior for empty results.
     *
     * @param tasks the task list to search.
     * @param matcher the condition a task must satisfy.
     * @param heading the heading to print before the results.
     * @param emptyMessage the message to print when there are no matches.
     * @param shouldShowHeadingWhenEmpty whether to print the heading when there are no matches.
     * @param ui the output interface used to display responses.
     */
    private void showMatchingTasks(TaskList tasks, Predicate<Task> matcher,
                                   String heading, String emptyMessage,
                                   boolean shouldShowHeadingWhenEmpty, UiOutput ui) {
        List<Integer> matchingIndices = tasks.findMatchingIndices(matcher);
        if (shouldShowHeadingWhenEmpty || !matchingIndices.isEmpty()) {
            ui.showMessage(heading);
        }
        for (int index : matchingIndices) {
            if (index < 0 || index >= tasks.size()) {
                throw new IllegalStateException("Query produced an invalid task index.");
            }
            ui.showTask(index + 1, tasks.get(index));
        }
        if (matchingIndices.isEmpty()) {
            ui.showMessage(emptyMessage);
        }
    }

    /**
     * Displays matching tasks without a heading when there are no matches.
     *
     * @param tasks the task list to search.
     * @param matcher the condition a task must satisfy.
     * @param heading the heading to print before the results.
     * @param emptyMessage the message to print when there are no matches.
     * @param ui the output interface used to display responses.
     */
    protected void showMatchingTasksWithoutEmptyHeading(TaskList tasks, Predicate<Task> matcher,
                                                         String heading, String emptyMessage,
                                                         UiOutput ui) {
        showMatchingTasks(tasks, matcher, heading, emptyMessage, false, ui);
    }
}
