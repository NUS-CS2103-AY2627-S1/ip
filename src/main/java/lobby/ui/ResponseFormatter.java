package lobby.ui;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lobby.task.Task;
import lobby.task.TaskList;

/**
 * Formats Lobby responses for presentation by either the console or graphical UI.
 */
public class ResponseFormatter {

    /**
     * Formats a numbered task list with the given introductory sentence.
     *
     * @param introduction sentence shown before the tasks.
     * @param tasks tasks to include.
     * @return the formatted response
     */
    public String formatTaskList(String introduction, TaskList tasks) {
        return introduction + IntStream.rangeClosed(1, tasks.size())
                .mapToObj(taskNumber -> System.lineSeparator() + " " + taskNumber + "." + tasks.get(taskNumber))
                .collect(Collectors.joining());
    }

    /**
     * Formats a successful task addition.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     * @return the formatted response
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return " Got it. I've added this task:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Formats a successful task deletion.
     *
     * @param task task that was removed.
     * @param taskCount number of tasks after the deletion.
     * @return the formatted response
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + formatTaskCount(taskCount);
    }

    /**
     * Formats a task that has been marked as completed.
     *
     * @param task updated task.
     * @return the formatted response
     */
    public String formatTaskMarked(Task task) {
        return " Nice! I've marked this task as done:" + System.lineSeparator() + "   " + task;
    }

    /**
     * Formats a task that has been marked as incomplete.
     *
     * @param task updated task.
     * @return the formatted response
     */
    public String formatTaskUnmarked(Task task) {
        return " OK, I've marked this task as not done yet:" + System.lineSeparator() + "   " + task;
    }

    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return " Now you have " + taskCount + " " + taskWord + " in the list.";
    }
}
