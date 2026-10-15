package littler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import littler.command.Command;
import littler.command.Parser;
import littler.command.request.EditRequest;
import littler.command.request.IndicesAndValue;
import littler.command.sort.SortOrder;
import littler.command.sort.SortRequest;
import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;
import littler.storage.Storage;
import littler.task.Task;
import littler.task.TaskList;
import littler.task.attribute.PriorityLevel;
import littler.ui.UI;

/**
 * Represents the main entry point for the LittleR task management application.
 * Manages the interaction loop between user input, task storage, and user interface display.
 */
public class LittleR {

    private static final Set<Command> MUTATING_COMMANDS = Set.of(
        Command.MARK, Command.UNMARK, Command.DELETE, Command.TODO, Command.DEADLINE, Command.EVENT,
        Command.EDIT, Command.DUPLICATE, Command.TAG, Command.UNTAG, Command.PRIORITY, Command.ARCHIVE);

    private final Storage storage;
    private final TaskList tasks;

    /**
     * Constructs a new LittleR application instance and initializes the storage,
     * user interface, and task list from the specified file path.
     *
     * @param filePath the file path where tasks are saved and loaded from
     */
    public LittleR(String filePath) {
        storage = new Storage(filePath);

        ArrayList<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (LittleRException e) {
            System.err.println(UI.error("Could not load tasks: " + e.getMessage()));
            loadedTasks = new ArrayList<>();
        }
        tasks = new TaskList(loadedTasks);
    }

    /**
     * Handles the interactive conversation loop by reading user input, parsing commands,
     * executing the corresponding actions, and persisting data changes.
     *
     * @param rawInput the raw user input string to process
     * @return the formatted output string to display to the user
     */
    public String converse(String rawInput) {
        StringBuilder output = new StringBuilder();
        String input = Command.normalizeAlias(rawInput);

        try {
            Command command = Command.fromInput(input);

            if (command == null) {
                output.append(UI.commandNotFoundError());
                return output.toString();
            }

            ArrayList<Task> preCommandSnapshot = MUTATING_COMMANDS.contains(command)
                ? tasks.snapshotTasks() : null;

            switch (command) {
                // Undo Previous Command
                case UNDO:
                    tasks.undo();
                    output.append(UI.undoSuccessful());
                    break;

                // Exit application
                case EXIT:
                    output.append(UI.goodbye());
                    break;

                // Display help
                case HELP:
                    output.append(UI.help());
                    break;

                // List tasks
                case LIST:
                    output.append(UI.taskList(tasks.getTasks()));
                    break;

                // View statistics
                case STATS:
                    output.append(UI.statistics(tasks.getStatistics()));
                    break;

                // Mark or unmark a task
                case MARK:
                    output.append(markItems(getIndices(input, command)));
                    break;

                case UNMARK:
                    output.append(unmarkItems(getIndices(input, command)));
                    break;

                case TAG:
                    output.append(tagItems(Parser.parseIndicesAndValue(input, command)));
                    break;

                case UNTAG:
                    output.append(untagItems(Parser.parseIndicesAndValue(input, command)));
                    break;

                case PRIORITY:
                    output.append(priorityItems(Parser.parseIndicesAndValue(input, command)));
                    break;

                case EDIT:
                    output.append(editItem(Parser.parseEdit(input, command)));
                    break;

                case DUPLICATE:
                    output.append(duplicateItem(Parser.parseDuplicate(input, command)));
                    break;

                // Delete a task
                case DELETE:
                    output.append(deleteItems(getIndices(input, command)));
                    break;

                // Archive all tasks
                case ARCHIVE:
                    output.append(archiveTasks());
                    break;

                // Add a new specified task (Todo, Deadline, or Event)
                case TODO:
                case DEADLINE:
                case EVENT:
                    output.append(addItem(input, command));
                    break;

                case ON:
                    output.append(printTasksOnDate(Parser.parseDate(input, command)));
                    break;

                case FIND:
                    output.append(printMatchingTasks(Parser.parseKeyword(input, command)));
                    break;

                case SORT:
                    output.append(printSortedTasks(Parser.parseSort(input, command)));
                    break;

                default:
                    throw new LittleRException("Unrecognized command: " + command.getKeyword());
            }
            if (preCommandSnapshot != null) {
                tasks.setUndoSnapshot(preCommandSnapshot);
            }
        } catch (LittleRException e) {
            output.append(UI.error(e.getMessage()));
        }
        output.append(saveQuietly());
        return output.toString();
    }

    /**
     * Saves the current task list to disk, catching and displaying any exceptions
     * to avoid terminating the application unexpectedly.
     */
    private String saveQuietly() {
        try {
            storage.save(tasks.getTasks());
            return "";
        } catch (LittleRException e) {
            return UI.error("Could not save: " + e.getMessage());
        }
    }

    /**
     * Prints all tasks sorted by the specified criteria and order.
     *
     * @param sortRequest the sort request containing the criteria and order
     * @return a formatted string of sorted tasks
     */
    private String printSortedTasks(SortRequest sortRequest) {
        boolean descending = sortRequest.getOrder() == SortOrder.DESCENDING;
        ArrayList<Task> sorted;
        switch (sortRequest.getCriteria()) {
            case DATE:
                sorted = tasks.getSortedByDate(descending);
                break;
            case PRIORITY:
                sorted = tasks.getSortedByPriority(descending);
                break;
            case TAG:
                sorted = tasks.getSortedByTag(descending);
                break;
            case NAME:
            default:
                sorted = tasks.getSortedByName(descending);
                break;
        }
        return UI.taskList(sorted);
    }

    /**
     * Prints all tasks whose description contains the given keyword.
     *
     * @param keyword the search term to match against task descriptions
     * @return a formatted string of matching tasks or a message if none are found
     */
    private String printMatchingTasks(String keyword) {
        ArrayList<Task> matches = tasks.findByKeyword(keyword);
        if (matches.isEmpty()) {
            return UI.noMatchingTasksFound();
        }
        StringBuilder output = new StringBuilder();
        output.append(UI.findResultsHeader());
        appendTaskLines(output, matches);
        return output.toString();
    }

    /**
     * Prints all tasks that are due or occurring on the specified date.
     *
     * @param dateInput the parsed date object to check tasks against
     * @return a formatted string of tasks or a message if none are found
     */
    private String printTasksOnDate(ParsedDateTime dateInput) {
        StringBuilder output = new StringBuilder();
        output.append(UI.tasksOnDateHeader(dateInput));

        ArrayList<Task> matches = tasks.getTasksOn(dateInput);
        if (matches.isEmpty()) {
            output.append(UI.noTasksFound());
        }
        appendTaskLines(output, matches);
        return output.toString();
    }

    /**
     * Appends a formatted list of tasks to the provided StringBuilder, each with its index.
     *
     * @param output the StringBuilder to append the task lines to
     * @param taskList the list of tasks to format and append
     */
    private void appendTaskLines(StringBuilder output, ArrayList<Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            output.append(UI.taskWithIndex(i + 1, taskList.get(i)));
        }
    }

    /**
     * Creates and adds a new task to the task list based on the input string and task command type.
     *
     * @param input the full raw user input string
     * @param type the type of task to create (TODO, DEADLINE, or EVENT)
     * @return a confirmation message indicating the task was added and the updated task count
     * @throws LittleRException if the task parameters or formatting are invalid
     */
    private String addItem(String input, Command type) throws LittleRException {
        Task task = Parser.parseTask(input, type);
        boolean isDuplicate = tasks.containsDuplicate(task);
        tasks.add(task);
        assert tasks.getLast() == task : "the just-added task should be the last task in the list";
        String confirmation = UI.taskAdded(tasks.getLast(), tasks.size());
        return isDuplicate ? UI.duplicateTaskWarning() + confirmation : confirmation;
    }

    private String archiveTasks() throws LittleRException {
        int archivedCount = tasks.size();
        storage.archive(tasks.getTasks());
        tasks.clear();
        return UI.tasksArchived(archivedCount);
    }

    /**
     * Edits an existing task in the task list based on the provided edit request.
     *
     * @param editRequest the request containing the index and updates for the task
     * @return a confirmation message indicating the task was updated
     * @throws LittleRException if the index is invalid or the updates are malformed
     */
    private String editItem(EditRequest editRequest) throws LittleRException {
        Task existing = tasks.get(editRequest.getIndex());
        Task updated = existing.withUpdates(editRequest.getUpdates());
        tasks.update(editRequest.getIndex(), updated);
        return UI.taskEdited(updated);
    }

    /**
     * Duplicates an existing task in the task list based on the provided duplicate request,
     * optionally applying field overrides to the new task.
     *
     * @param duplicateRequest the request containing the index of the task to duplicate and any field overrides
     * @return a confirmation message indicating the task was duplicated and the updated task count
     * @throws LittleRException if the index is invalid or the overrides are malformed
     */
    private String duplicateItem(EditRequest duplicateRequest) throws LittleRException {
        Task source = tasks.get(duplicateRequest.getIndex());
        Task duplicate = source.withUpdates(duplicateRequest.getUpdates());
        duplicate.unmark();
        boolean isDuplicate = tasks.containsDuplicate(duplicate);
        tasks.add(duplicate);
        String confirmation = UI.taskDuplicated(duplicate, tasks.size());
        return isDuplicate ? UI.duplicateTaskWarning() + confirmation : confirmation;
    }

    /**
     * Extracts and validates the list of target task indices from the user input string.
     *
     * @param input the user input string containing the target indices
     * @param command the command keyword to strip from the input
     * @return a list of parsed task indices
     * @throws LittleRException if the task list is empty, any index is invalid, or out of bounds
     */
    private List<Integer> getIndices(String input, Command command) throws LittleRException {
        if (tasks.isEmpty()) {
            throw new LittleRException("There are no tasks yet.");
        }
        return Parser.parseIndices(input, command);
    }

    /**
     * Marks the specified tasks as completed and returns a confirmation message.
     *
     * @param indices the list of task indices to mark
     * @return a formatted string confirming the tasks have been marked
     * @throws LittleRException if any index is invalid or out of bounds
     */
    private String markItems(List<Integer> indices) throws LittleRException {
        for (int index : indices) {
            tasks.get(index); // validate all indices before mutating any
        }
        StringBuilder output = new StringBuilder();
        for (int index : indices) {
            output.append(UI.taskMarked(tasks.mark(index))).append("\n");
        }
        return output.toString();
    }

    /**
     * Unmarks the specified tasks as not completed and returns a confirmation message.
     *
     * @param indices the list of task indices to unmark
     * @return a formatted string confirming the tasks have been unmarked
     * @throws LittleRException if any index is invalid or out of bounds
     */
    private String unmarkItems(List<Integer> indices) throws LittleRException {
        for (int index : indices) {
            tasks.get(index);
        }
        StringBuilder output = new StringBuilder();
        for (int index : indices) {
            output.append(UI.taskUnmarked(tasks.unmark(index))).append("\n");
        }
        return output.toString();
    }

    /**
     * Deletes the specified tasks from the task list and returns a confirmation message.
     *
     * @param indices the list of task indices to delete
     * @return a formatted string confirming the tasks have been deleted
     * @throws LittleRException if any index is invalid or out of bounds
     */
    private String deleteItems(List<Integer> indices) throws LittleRException {
        for (int index : indices) {
            tasks.get(index);
        }
        List<Integer> highestFirst = new ArrayList<>(indices);
        highestFirst.sort(Collections.reverseOrder());
        StringBuilder output = new StringBuilder();
        for (int index : highestFirst) {
            output.append(UI.taskDeleted(tasks.delete(index), tasks.size())).append("\n");
        }
        return output.toString();
    }

    /**
     * Tags the specified tasks with the provided tag value and returns a confirmation message.
     *
     * @param request the request containing the list of task indices and the tag value
     * @return a formatted string confirming the tasks have been tagged
     * @throws LittleRException if any index is invalid or out of bounds
     */
    private String tagItems(IndicesAndValue request) throws LittleRException {
        for (int index : request.getIndices()) {
            tasks.get(index);
        }
        StringBuilder output = new StringBuilder();
        for (int index : request.getIndices()) {
            Task task = tasks.get(index);
            task.addTag(request.getValue());
            output.append(UI.taskTagged(task)).append("\n");
        }
        return output.toString();
    }

    /**
     * Untags the specified tasks by removing the provided tag value and returns a confirmation message.
     *
     * @param request the request containing the list of task indices and the tag value
     * @return a formatted string confirming the tasks have been untagged
     * @throws LittleRException if any index is invalid or out of bounds
     */
    private String untagItems(IndicesAndValue request) throws LittleRException {
        for (int index : request.getIndices()) {
            tasks.get(index);
        }
        StringBuilder output = new StringBuilder();
        for (int index : request.getIndices()) {
            Task task = tasks.get(index);
            task.removeTag(request.getValue());
            output.append(UI.taskUntagged(task)).append("\n");
        }
        return output.toString();
    }

    /**
     * Sets the priority level of the specified tasks and returns a confirmation message.
     *
     * @param request the request containing the list of task indices and the priority value
     * @return a formatted string confirming the tasks' priority levels have been updated
     * @throws LittleRException if any index is invalid, out of bounds, or the priority value is invalid
     */
    private String priorityItems(IndicesAndValue request) throws LittleRException {
        PriorityLevel level = PriorityLevel.fromInput(request.getValue());
        if (level == null) {
            throw new LittleRException("Priority must be one of: high, medium, low.");
        }
        for (int index : request.getIndices()) {
            tasks.get(index);
        }
        StringBuilder output = new StringBuilder();
        for (int index : request.getIndices()) {
            Task task = tasks.get(index);
            task.setPriority(level);
            output.append(UI.taskPriorityUpdated(task)).append("\n");
        }
        return output.toString();
    }
}
