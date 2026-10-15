package milo;

import milo.parser.Parser;
import milo.parser.RescheduleParser;
import milo.storage.Storage;
import milo.task.Deadline;
import milo.task.Task;
import milo.task.TaskList;

/**
 * Handles Milo's commands and task data.
 *
 * <p>This class deliberately returns messages instead of printing them. This keeps command
 * processing independent of the console user interface.</p>
 */
public class Logic {
    private final TaskList tasks;
    private final String loadingError;
    /** Loads the saved tasks and creates the command handler for them. */
    public Logic() {
        TaskList loadedTasks;
        String errorMessage = null;

        try {
            loadedTasks = Storage.loadTasks();
        } catch (MiloException e) {
            loadedTasks = new TaskList();
            errorMessage = e.getMessage();
        }

        this.tasks = loadedTasks;
        this.loadingError = errorMessage;
    }

    /** Creates the command handler using the tasks that were already loaded from storage. */
    public Logic(TaskList tasks) {
        this.tasks = tasks;
        this.loadingError = null;
    }

    /** Returns whether task loading failed during construction. */
    public boolean hasLoadingError() {
        return loadingError != null;
    }

    /** Returns the message from a task-loading failure, if one occurred. */
    public String getLoadingError() {
        return loadingError;
    }

    /** Processes one complete user command and returns the message that should be displayed. */
    public String execute(String input) {
        return executeResponse(input).message();
    }

    /** Processes a command while preserving error status and optional recovery guidance. */
    public Response executeResponse(String input) {
        try {
            return new Response(processCommand(input), false, "");
        } catch (MiloException e) {
            return new Response(e.getMessage(), true, e.getSuggestion());
        } catch (NumberFormatException e) {
            return new Response("Give me a valid task number!", true,
                    "Use list to see task numbers, then try a command such as mark 1.");
        }
    }

    /** Dispatches a command, allowing failures to reach the response boundary. */
    private String processCommand(String input) throws MiloException {
        if (input.equals("bye")) {
            return "Bye bye. Hope to see you soon!";
        } else if (input.equals("list")) {
            return listTasks();
        } else if (input.startsWith("mark")) {
            return markTask(input.substring(4), true);
        } else if (input.startsWith("unmark")) {
            return markTask(input.substring(6), false);
        } else if (input.startsWith("delete")) {
            return deleteTask(input.substring(6));
        } else if (input.startsWith("find")) {
            return findTasks(input.substring(4));
        } else if (input.startsWith("reschedule")) {
            return rescheduleTask(input);
        } else {
            return addTask(input);
        }
    }

    /** Returns whether the input should end the application after its response is shown. */
    public boolean isExitCommand(String input) {
        return input.equals("bye");
    }

    /** Replaces a dated task only after validation, restoring the original if saving fails. */
    private String rescheduleTask(String input) throws MiloException {
        input = input.substring(10);
        if (input.startsWith(" ")) {
            input = input.substring(1);
        }
        String[] parts = input.split(" ", 2);
        Task original = getTask(parts[0], "Ermm... What do you want me to reschedule?");
        if (original.isDone()) {
            throw new MiloException("You've already completed this task!");
        }
        Task updated;
        try {
            updated = RescheduleParser.parse(original, parts.length == 2 ? parts[1] : "");
        } catch (MiloException e) {
            String suggestion = "Use list to choose a deadline or event to reschedule.";
            if (original instanceof Deadline) {
                suggestion = "Try: reschedule " + parts[0] + " /by 2026-10-15 1800";
            } else if (original instanceof milo.task.Event) {
                suggestion = "Try: reschedule " + parts[0]
                        + " /from 2026-10-15 1400 /to 2026-10-15 1600";
            }
            throw new MiloException(e.getMessage(), suggestion);
        }
        assert updated != null : "Successful rescheduling parsing must return a task";
        int index = Integer.parseInt(parts[0]) - 1;
        tasks.set(index, updated);
        try {
            Storage.saveTasks(tasks);
        } catch (MiloException e) {
            tasks.set(index, original);
            throw e;
        }
        return String.format("Ok! I've rescheduled the following task:\n    %d. %s", index + 1, updated);
    }

    /** Adds a parsed task, saves it, and returns the existing confirmation wording. */
    private String addTask(String input) throws MiloException {
        Task task = Parser.parseTask(input);
        // Invalid commands throw MiloException; a successful parse must produce a task.
        assert task != null : "Successful task parsing must return a task";
        tasks.add(task);
        Storage.saveTasks(tasks);
        return String.format("Ok, I've added the following task:\n"
                + "      %s\n"
                + "    You've got %d tasks in your list!", task, tasks.size());
    }

    /** Returns the numbered task list using the existing wording. */
    private String listTasks() {
        return formatTaskList("Here is your to-do list:", tasks);
    }

    /** Marks or unmarks the selected task, saves it, and returns its confirmation. */
    private String markTask(String input, boolean markDone) throws MiloException {
        Task task = getTask(input, "Which tasks are we tackling? I can't read your mind you know!");
        if (markDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        Storage.saveTasks(tasks);
        return markDone
                ? "Yay! I've marked this task as done!\n      " + task
                : "Hmm.... Why was it marked as done in the first place?\n      " + task;
    }

    /** Deletes a selected task or every task, then returns the existing confirmation. */
    private String deleteTask(String input) throws MiloException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new MiloException("Ok, deleting nothing!");
        }
        if (trimmedInput.equals("all")) {
            tasks.clear();
            Storage.saveTasks(tasks);
            return "POOOOOFFF\n    Your to-do list is gone! Sure hope you meant that!";
        }

        Task task = getTask(trimmedInput, "Ok, deleting nothing!");
        int index = Integer.parseInt(trimmedInput) - 1;
        tasks.remove(index);
        Storage.saveTasks(tasks);
        return String.format("Ok, I've removed this task:\n"
                + "      %s\n"
                + "    You've got %d tasks in your list!", task, tasks.size());
    }

    /** Searches descriptions for a keyword and returns the matching-task message. */
    private String findTasks(String keyword) throws MiloException {
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            throw new MiloException("Give me a keyword and I'll do the digging!", "Try: find book");
        }

        TaskList results = tasks.find(trimmedKeyword);
        if (results.size() == 0) {
            return "No matches this time! Try a different keyword.";
        }

        return formatTaskList("Here are the tasks I found:", results);
    }

    /** Formats a task list with a heading and one-based numbering. */
    private String formatTaskList(String heading, TaskList taskList) {
        String message = heading;
        for (int i = 0; i < taskList.size(); i++) {
            message += String.format("\n    %d. %s", i + 1, taskList.get(i));
        }
        return message;
    }

    /** Validates a one-based task number and returns its task. */
    private Task getTask(String input, String emptyMessage) throws MiloException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new MiloException(emptyMessage, "Use list to see task numbers first.");
        }

        int index = Integer.parseInt(trimmedInput) - 1;
        if (index < 0) {
            throw new MiloException("There can't be a negative task number!", "Use list to see valid task numbers.");
        }
        if (index >= tasks.size()) {
            throw new MiloException("You don't even have that many tasks!", "Use list to see valid task numbers.");
        }
        // The user-facing checks above must establish a valid zero-based index.
        assert index >= 0 && index < tasks.size() : "Validated task index must be within the task list";
        return tasks.get(index);
    }
}
