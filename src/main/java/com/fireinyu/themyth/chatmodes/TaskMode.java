package com.fireinyu.themyth.chatmodes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.requests.AtRequest;
import com.fireinyu.themyth.requests.DeadlineRequest;
import com.fireinyu.themyth.requests.DeleteRequest;
import com.fireinyu.themyth.requests.DueRequest;
import com.fireinyu.themyth.requests.EventRequest;
import com.fireinyu.themyth.requests.FindRequest;
import com.fireinyu.themyth.requests.ListRequest;
import com.fireinyu.themyth.requests.MarkRequest;
import com.fireinyu.themyth.requests.TodoRequest;
import com.fireinyu.themyth.requests.UnmarkRequest;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.Response;
import com.fireinyu.themyth.storage.TaskList;
import com.fireinyu.themyth.tasks.DeadlineTask;
import com.fireinyu.themyth.tasks.EventTask;
import com.fireinyu.themyth.tasks.Task;
import com.fireinyu.themyth.tasks.TaskOrder;
import com.fireinyu.themyth.tasks.TodoTask;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Handles requests to create, find, sort, complete, and delete tasks.
 * Loads tasks when the mode starts and saves them when it closes.
 *
 * @see Task
 * @see com.fireinyu.themyth.requests.Request
 * @see Response
 */
public class TaskMode extends ChatMode {
    private final TaskList taskList = new TaskList();
    private final Path taskFile;

    /**
     * Initializes a TaskMode which syncs created Tasks with a given task file.
     *
     * @param taskFile Path to task file that syncs with this TaskMode.
     * @see Path
     */
    public TaskMode(Path taskFile) {
        this.taskFile = taskFile;
    }

    /**
     * Initializes a TaskMode which syncs created Tasks with a task file at the default path.
     *
     * @see Defaults
     * @see Path
     */
    public TaskMode() {
        this(Path.of(Defaults.TASKFILE));
    }

    @Override
    protected Response respondToInit(InitRequest request) {
        taskList.open(taskFile);
        return new Response("The archives are unlocked and looking drop-dead gorgeous! All tasks loaded, honey! 💅✨");
    }

    @Override
    protected Response respondToCloseEvent(CloseRequest request) {
        taskList.close();
        return super.respondToCloseEvent(request);
    }

    @Override
    protected Response respondToFind(FindRequest request) {
        return listTasks(
                "Found them! These matching gems were practically begging for my spotlight:",
                new ArrayList<>(
                        taskList.stream()
                                .filter(task -> task.toString().contains(request.getArg(1, String.class)))
                                .toList()
                ),
                request.getArg("sort", TaskOrder.class)
        );
    }

    @Override
    protected Response respondToTodo(TodoRequest request) {
        return addTask(new TodoTask(request.getArg(1, String.class)));
    }

    @Override
    protected Response respondToDeadline(DeadlineRequest request) {
        return addTask(new DeadlineTask(
                request.getArg(1, String.class),
                request.getArg("by", MythDateTime.class)
        ));
    }

    @Override
    protected Response respondToEvent(EventRequest request) {
        return addTask(new EventTask(
                request.getArg(1, String.class),
                request.getArg("from", MythDateTime.class),
                request.getArg("to", MythDateTime.class)
        ));
    }

    @Override
    protected Response respondToList(ListRequest request) {
        return listTasks(
                "Feast your eyes, darling! Here is your glamorous itinerary:",
                taskList,
                request.getArg("sort", TaskOrder.class)
        );
    }

    @Override
    protected Response respondToAt(AtRequest request) {
        MythDateTime at = request.getArg(1, MythDateTime.class);
        return listTasks(
                String.format("Clear the runway! Here is the fabulous drama scheduled on %s:", at),
                new ArrayList<>(
                        taskList.stream().filter(task -> task instanceof EventTask eventTask && eventTask.contains(at))
                                .toList()
                ),
                request.getArg("sort", TaskOrder.class)
        );
    }

    @Override
    protected Response respondToDue(DueRequest request) {
        MythDateTime due = request.getArg(1, MythDateTime.class);
        return listTasks(
                String.format("Tick-tock, gorgeous! Here are the deadlines looming over you by %s:", due),
                new ArrayList<>(
                        taskList.stream()
                                .filter(task -> task instanceof DeadlineTask deadlineTask && deadlineTask.isDueBy(due))
                                .toList()
                ),
                request.getArg("sort", TaskOrder.class)
        );
    }

    @Override
    protected Response respondToMark(MarkRequest request) {
        int itemIndex = request.getArg(1, Integer.class) - 1;
        return mark(itemIndex);
    }

    /**
     * Marks the task at a zero-based index as complete and returns its confirmation.
     */
    Response mark(int index) {
        Task task = taskList.get(index);
        task.mark();
        String message = "Slay, honey! Slay! That task is officially conquered and looking iconic:\n\t" + task;
        return new Response(message, Response.Mood.HAPPY);
    }

    @Override
    protected Response respondToUnmark(UnmarkRequest request) {
        int itemIndex = request.getArg(1, Integer.class) - 1;
        return respondToUnmark(itemIndex);
    }

    /**
     * Marks the task at a zero-based index as incomplete and returns its confirmation.
     */
    Response respondToUnmark(int itemIndex) {
        Task task = taskList.get(itemIndex);
        task.unmark();
        String message = "Ugh, backpedaling? Fine, darling. Marked it as not done yet:\n\t" + task;
        return new Response(message);
    }

    TaskList getTaskList() {
        return taskList;
    }

    @Override
    protected Response respondToDelete(DeleteRequest request) {
        int itemIndex = request.getArg(1, Integer.class) - 1;
        Task task = taskList.remove(itemIndex);
        String message = String.format(
                "Trash it, burn it, make room for fabulous! I kicked this task to the curb:\n\t%s\n"
                + "You're down to %d tasks, babe. Keep it chic! 💅",
                task,
                taskList.size()
        );
        return new Response(message);
    }

    /**
     * Adds a task to the task list and returns a response.
     *
     * @param task The task to add.
     * @return The response to the user.
     */
    private Response addTask(Task task) {
        taskList.add(task);
        String message = String.format(
                "Fabulous! I slipped this masterpiece into your list:\n\t%s\n"
                + "Now you're juggling %d tasks, superstar! Don't trip! 💅✨",
                task,
                taskList.size()
        );
        return new Response(message);
    }

    /**
     * Sorts the supplied list in place and formats it into a numbered response.
     *
     * @param header The header message for the list.
     * @param tasks The list of tasks to format.
     * @param order The ordering to apply before numbering the tasks.
     * @return The response containing the formatted list.
     */
    Response listTasks(String header, List<Task> tasks, TaskOrder order) {
        order.apply(tasks);
        StringBuilder body = new StringBuilder(header);
        int itemNumber = 1;
        for (Task item : tasks) {
            body.append('\n');
            body.append(itemNumber++);
            body.append(". ");
            body.append(item.toString());
        }
        return new Response(body.toString());
    }
}
