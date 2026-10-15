package probe.handler;

import probe.ProbeException;
import probe.parser.Parser;
import probe.storage.Storage;
import probe.task.Task;
import probe.task.TaskList;

/**
 * Executes user commands using the application's core components.
 */
public class CommandHandler {
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates a command handler with the supplied application components.
     *
     * @param storage Storage used to persist task changes.
     * @param tasks Task list to modify and search.
     * @param parser Parser used to interpret commands.
     */
    public CommandHandler(Storage storage, TaskList tasks, Parser parser) {
        this.storage = storage;
        this.tasks = tasks;
        this.parser = parser;
    }

    /**
     * Executes one command and returns a response for the user interface.
     *
     * @param command Command entered by the user.
     * @return Response describing the command result.
     * @throws ProbeException If the command contains invalid arguments.
     */
    public String execute(String command) throws ProbeException {
        if (command.equals("list")) {
            return formatTasks(tasks.asList());
        } else if (command.startsWith("todo") || command.startsWith("deadline")
                || command.startsWith("event")) {
            return addTask(command);
        } else if (command.startsWith("delete ")) {
            return deleteTask(command);
        } else if (command.startsWith("mark ") || command.startsWith("unmark ")) {
            return updateTaskStatus(command);
        } else if (command.equals("tag clear")) {
            return clearTags();
        } else if (command.startsWith("tag ") || command.startsWith("untag ")) {
            return updateTags(command);
        } else if (command.equals("find") || command.startsWith("find ")) {
            return findTasks(command);
        } else {
            throw new ProbeException("I do not understand that command.");
        }
    }

    /**
     * Adds a task described by a command and persists the updated list.
     *
     * @param command Task-creation command.
     * @throws ProbeException If the task command is invalid.
     */
    private String addTask(String command) throws ProbeException {
        Task task = parser.parseTask(command);
        tasks.add(task);
        storage.save(tasks.asList());
        return "Added: " + task;
    }

    /**
     * Deletes the task identified by a command and persists the updated list.
     *
     * @param command Task-deletion command.
     * @throws ProbeException If the task number is invalid.
     */
    private String deleteTask(String command) throws ProbeException {
        Task removed = tasks.delete(parser.parseNumber(command,
                "Please provide a task number to delete."));
        storage.save(tasks.asList());
        return "Removed: " + removed;
    }

    /**
     * Changes a task's completion status and persists the updated list.
     *
     * @param command Mark or unmark command.
     * @throws ProbeException If the task number is invalid.
     */
    private String updateTaskStatus(String command) throws ProbeException {
        boolean isMark = command.startsWith("mark ");
        Task task = tasks.get(parser.parseNumber(command, "Please provide a task number."));
        if (isMark) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        storage.save(tasks.asList());
        return isMark ? "Marked as done: " + task : "Marked as not done: " + task;
    }

    /**
     * Displays tasks whose descriptions contain the search keyword.
     *
     * @param command Search command.
     * @throws ProbeException If no search keyword is provided.
     */
    private String findTasks(String command) throws ProbeException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isBlank()) {
            throw new ProbeException("Enter some keyword to search.");
        }
        return formatTasks(tasks.search(keyword).asList());
    }

    /**
     * Adds or removes the tags specified by a command.
     *
     * @param command Tag or untag command.
     * @return Response describing the tag update.
     * @throws ProbeException If the command format or task number is invalid.
     */
    private String updateTags(String command) throws ProbeException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 3) {
            throw new ProbeException("Please provide a task number and at least one tag.");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new ProbeException("The task number must be a number.");
        }
        Task task = tasks.get(taskNumber);
        String[] tagNames = java.util.Arrays.copyOfRange(parts, 2, parts.length);
        boolean adding = parts[0].equals("tag");
        if (adding) {
            task.addTag(tagNames);
        } else {
            task.deleteTag(tagNames);
        }
        storage.save(tasks.asList());
        return (adding ? "Updated tags: " : "Removed tags: ") + task;
    }

    /**
     * Removes all tags from every task and persists the updated list.
     *
     * @return Response confirming that all tags were removed.
     */
    private String clearTags() {
        for (Task task : tasks.asList()) {
            task.clearTags();
        }
        storage.save(tasks.asList());
        return "Cleared all tags.";
    }

    /**
     * Returns a numbered response containing the supplied tasks.
     *
     * @param taskItems Tasks to include in the response.
     * @return Formatted task response.
     */
    private String formatTasks(java.util.List<Task> taskItems) {
        if (taskItems.isEmpty()) {
            return "There are no matching tasks.";
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < taskItems.size(); i++) {
            response.append(i + 1).append(". ").append(taskItems.get(i));
            if (i < taskItems.size() - 1) {
                response.append("\n");
            }
        }
        return response.toString();
    }
}
