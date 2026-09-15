package tianyi.command;

import java.util.List;
import java.util.stream.Collectors;

import tianyi.Response;
import tianyi.storage.Storage;
import tianyi.task.IndexedTask;
import tianyi.task.TaskList;
import tianyi.task.TaskTime;

/**
 * Lists all tasks or tasks occurring on a specified date.
 */
public class ListCommand extends Command {
    private final TaskTime date;

    /**
     * Creates a command that lists all tasks or those occurring on a date.
     *
     * @param date Date used to filter tasks, or {@code null} to list all tasks.
     */
    public ListCommand(TaskTime date) {
        this.date = date;
    }

    /**
     * Formats and returns the requested tasks.
     *
     * @param tasks Task list to read.
     * @param storage Storage instance, which is not used.
     * @return Formatted tasks matching the command.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage) {
        if (date == null) {
            return createAllTasksResponse(tasks);
        }
        return createDatedTasksResponse(tasks);
    }

    /**
     * Builds the complete list response, including encouragement when every task is done.
     *
     * @param tasks Complete task list.
     * @return Response for the complete task list.
     */
    private Response createAllTasksResponse(TaskList tasks) {
        if (tasks.isEmpty()) {
            return new Response("", "Your list is empty. What would you like to add?");
        }

        String message = formatTasks(tasks.listTasks());
        String footer = "";

        if (tasks.countIncompleteTasks() == 0) {
            footer = "All done. Enjoy a little time for yourself!";
        }

        return new Response("Here's your task list. Let's have a look:", message, footer);
    }

    /**
     * Builds a date-filtered response whose completion message applies only to the displayed tasks.
     *
     * @param tasks Complete task list to filter using this command's date.
     * @return Response for the matching dated tasks.
     */
    private Response createDatedTasksResponse(TaskList tasks) {
        List<IndexedTask> listedTasks = tasks.listTasks(date);

        if (listedTasks.isEmpty()) {
            return new Response("", "No tasks found.");
        }

        String message = formatTasks(listedTasks);
        String footer = "";

        if (tasks.countIncompleteTasks(date) == 0) {
            footer = "All tasks listed for " + date.getData() + " are done. Nicely done!";
        }

        return new Response("Here are your deadlines and events for " + date.getData() + ":", message, footer);
    }

    /**
     * Formats numbered tasks as separate lines for either list view.
     *
     * @param indexedTasks Tasks to display with their original list numbers.
     * @return Task lines joined by newline characters.
     */
    private String formatTasks(List<IndexedTask> indexedTasks) {
        return indexedTasks.stream()
                .map(IndexedTask::toString)
                .collect(Collectors.joining("\n"));
    }
}
