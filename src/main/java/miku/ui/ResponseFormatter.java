package miku.ui;

import java.util.List;

import miku.task.Task;
import miku.task.TaskList;

/** Formats Miku's task responses for display in any user interface. */
public class ResponseFormatter {
    static final String ERROR_PREFIX = "OOPS!!!";

    /** Returns Miku's welcome message. */
    public String formatWelcome() {
        return "Hello! I'm Hatsune Miku ♪\nWhat can I do for you?";
    }

    /** Returns the warning shown when saved tasks cannot be restored. */
    public String formatLoadingError() {
        return ERROR_PREFIX + " Miku could not load saved tasks, so we're starting with an empty list ♪";
    }

    /** Returns the current task list as displayable text. */
    public String formatTaskList(TaskList tasks) {
        return formatTasks("Here are the tasks in your list ♫", tasks.asList());
    }

    /** Returns matching tasks as displayable text. */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        return formatTasks("Here are the matching tasks Miku found ♪", matchingTasks);
    }

    /** Returns the confirmation for an added task. */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Got it! I've added this task for you ✨\n" + task + '\n' + formatTaskCount(taskCount);
    }

    /** Returns the confirmation for a completed task. */
    public String formatTaskMarked(Task task) {
        return "Okay ★ I've marked this task as done!\n" + task;
    }

    /** Returns the confirmation for an incomplete task. */
    public String formatTaskUnmarked(Task task) {
        return "Oops... I've marked this task as not done yet ♪\n" + task;
    }

    /** Returns the confirmation for a deleted task. */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "Noted ♪ I've removed this task for you!\n" + task + '\n' + formatTaskCount(taskCount);
    }

    /** Returns the confirmation for a task with an updated schedule. */
    public String formatTaskRescheduled(Task task) {
        return "Got it! I've rescheduled this task for you \u2728\n" + task;
    }

    /** Returns Miku's farewell message. */
    public String formatGoodbye() {
        return "Bye bye! Miku hopes to see you again soon! ✨";
    }

    /** Returns Miku's friendly formatting for command errors. */
    public String formatError(String message) {
        return ERROR_PREFIX + ' ' + message;
    }

    /** Formats a heading and numbered task collection. */
    private String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append('\n').append(index + 1).append('.').append(tasks.get(index));
        }
        return response.toString();
    }

    /** Returns the task-count sentence used after task additions and deletions. */
    private String formatTaskCount(int taskCount) {
        return "Now you have " + taskCount + " task(s) in the list! ☆";
    }
}
