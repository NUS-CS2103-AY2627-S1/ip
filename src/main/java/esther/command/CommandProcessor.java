package esther.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import esther.exception.EstherException;
import esther.parser.Parser;
import esther.storage.Storage;
import esther.task.Task;
import esther.task.TaskList;

/**
 * Processes Esther commands independently of the user interface.
 */
public class CommandProcessor {
    private static final String SAVE_ERROR =
            "Uh-oh, I couldn't save your tasks. Please try again before they escape!";

    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a command processor with the specified task state.
     *
     * @param storage Storage used for persistent task data.
     * @param tasks In-memory task list to modify.
     */
    public CommandProcessor(Storage storage, TaskList tasks) {
        this.storage = storage;
        this.tasks = tasks;
    }

    /**
     * Processes one command and returns messages for a user interface.
     *
     * @param command Command entered by the user.
     * @return Messages and exit status produced by the command.
     */
    public CommandResult process(String command) {
        command = command.trim();

        try {
            if (command.equals("bye")) {
                return buildResult(List.of(
                        "Byeee! Hope to see you soooon!"), true);
            } else if (command.equals("list")) {
                return buildResult(getTaskListMessages(), false);
            } else if (command.equals("find")
                    || command.startsWith("find ")) {
                String keyword = Parser.parseFindKeyword(command);
                return buildResult(getMatchingTaskMessages(tasks.find(keyword)), false);
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                return buildResult(markTask(command), false);
            } else if (command.equals("unmark")
                    || command.startsWith("unmark ")) {
                return buildResult(unmarkTask(command), false);
            } else if (command.equals("delete")
                    || command.startsWith("delete ")) {
                return buildResult(deleteTask(command), false);
            } else if (command.equals("sort")) {
                return buildResult(sortTasks(), false);
            }

            Task task = Parser.parseTask(command);
            tasks.add(task);
            storage.save(tasks);
            return buildResult(List.of(
                    "Yesss, consider it added:",
                    "  " + task,
                    "Your list now has " + tasks.size() + " "
                            + getTaskWord(tasks.size())
                            + ". I'm keeping track, obviously."), false);
        } catch (EstherException exception) {
            return buildErrorResult(List.of(exception.getMessage()));
        } catch (IOException exception) {
            return buildErrorResult(List.of(SAVE_ERROR));
        }
    }

    private List<String> markTask(String command) throws EstherException, IOException {
        int taskIndex = Parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        storage.save(tasks);

        return List.of(
                "Nice! I've marked this task as done:",
                " " + task);
    }

    private List<String> unmarkTask(String command)
            throws EstherException, IOException {
        int taskIndex = Parser.parseTaskIndex(
                command, "unmark", tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        storage.save(tasks);

        return List.of(
                "Oops, back onto the unfinished pile it goes:",
                " " + task);
    }

    private List<String> deleteTask(String command)
            throws EstherException, IOException {
        int taskIndex = Parser.parseTaskIndex(
                command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks);

        return List.of(
                "Poof! This task is officially gone:",
                "  " + removedTask,
                "Your list now has " + tasks.size() + " "
                        + getTaskWord(tasks.size())
                        + ". I'm keeping track, obviously.");
    }

    private List<String> sortTasks() throws IOException {
        tasks.sortByCompletionStatus();
        storage.save(tasks);

        List<String> messages = getTaskListMessages();
        messages.set(0,
                "Tadaa! Unfinished business first, completed victories after:");
        return messages;
    }

    private List<String> getTaskListMessages() {
        int tasksLeft = tasks.countNotDone();
        List<String> messages = new ArrayList<>();

        if (tasksLeft == 0) {
            messages.add("WOW! You have no tasks left, good work!");
        } else if (tasksLeft <= 5) {
            messages.add(String.format(
                    "Only %d %s left! Should be a piece of cake for you!",
                    tasksLeft,
                    getTaskWord(tasksLeft)));
        } else {
            messages.add(String.format(
                    "%d tasks left?? What have you been doing this whole "
                            + "time?? You better focus up!",
                    tasksLeft));
        }

        for (int i = 0; i < tasks.size(); i++) {
            messages.add(String.format("%d.%s", i + 1, tasks.get(i)));
        }

        return messages;
    }

    private List<String> getMatchingTaskMessages(List<Task> matchingTasks) {
        List<String> messages = new ArrayList<>();

        if (matchingTasks.isEmpty()) {
            messages.add(
                    "I searched everywhere, but that task is playing hide-and-seek.");
        } else {
            messages.add("Found them! Here's what matched:");

            for (int i = 0; i < matchingTasks.size(); i++) {
                messages.add(String.format(
                        "%d.%s", i + 1, matchingTasks.get(i)));
            }
        }

        return messages;
    }

    private CommandResult buildResult(List<String> messages, boolean shouldExit) {
        return new CommandResult(messages, shouldExit, false);
    }

    private CommandResult buildErrorResult(List<String> messages) {
        return new CommandResult(messages, false, true);
    }

    private String getTaskWord(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }
}
