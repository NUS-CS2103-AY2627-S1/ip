package cassava;

import static cassava.data.TaskData.putTasks;
import static cassava.task.Task.stringToTaskPriority;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;

import cassava.task.DeadlineTask;
import cassava.task.EventTask;
import cassava.task.Task;
import cassava.task.ToDoTask;

/**
 * Handles the commands input by the user.
 * Routes each command to the appropriate handler function,
 * which adds, updates or deletes the tasks as required.
 */
public class Handlers {

    // AI used here to refactor
    // Suggested returning HandlerResult instead of a boolean,
    // and moving GUI window creation to MainWindow.fxml
    /**
     * Represents the outcome of handling a user command.
     * @param message Text to display to the user.
     * @param isError Whether this outcome represents an error/invalid input.
     * @param isExit Whether the program should exit after this outcome.
     */
    public record HandlerResult(String message, boolean isError, boolean isExit) { }

    /**
     * Routes the user input to the appropriate handler function.
     * Checks if the correct input parameters exist, if not,
     * routes to the invalid input handler.
     * @param tasks List of tasks to update.
     * @param inputArgs Parsed user input command and parameters.
     * @param validCmds List of valid commands accepted by Cassava.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleInput(
            List<Task> tasks,
            HashMap<String, String> inputArgs,
            List<String> validCmds
    ) {
        assert tasks != null;

        String command = inputArgs.getOrDefault("command", "");
        String commandOption = inputArgs.getOrDefault(command, "");

        if (command.isEmpty()) {
            return handleInvalid("You have not entered any text.");
        }
        if (!validCmds.contains(command)) {
            return handleInvalid("Sorry, I don't recognise this command.");
        }

        return switch (command) {
            case "list" -> handleList(tasks);
            case "find" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a search term to find.")
                    : handleFind(tasks, commandOption);
            case "todo" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to add.")
                    : handleAddTodo(tasks, commandOption);
            case "deadline" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to add.")
                    : !inputArgs.containsKey("by")
                    ? handleInvalid("You did not specify a date for the deadline.")
                    : handleAddDeadline(tasks, commandOption, inputArgs.get("by"));
            case "event" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to add.")
                    : !inputArgs.containsKey("from")
                    ? handleInvalid("You did not specify a time for 'from'.")
                    : !inputArgs.containsKey("to")
                    ? handleInvalid("You did not specify a time for 'to'.")
                    : handleAddEvent(tasks, commandOption, inputArgs.get("from"), inputArgs.get("to"));
            case "mark" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to mark.")
                    : handleMark(tasks, commandOption);
            case "unmark" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to unmark.")
                    : handleUnmark(tasks, commandOption);
            case "prioritise" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to prioritise.")
                    : !inputArgs.containsKey("level")
                    ? handleInvalid("You did not specify a priority level to set.")
                    : handlePrioritise(tasks, commandOption, inputArgs.get("level"));
            case "delete" -> commandOption.isEmpty()
                    ? handleInvalid("You did not specify a task to delete.")
                    : handleDelete(tasks, commandOption);
            case "bye" -> handleExit();
            default -> handleInvalid("Sorry, I don't recognise this command.");
        };
    }

    /**
     * Prints list of tasks.
     * @param tasks List of tasks to read from.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleList(
            List<Task> tasks
    ) {
        assert tasks != null;

        if (tasks.isEmpty()) {
            return new HandlerResult("No tasks have been added yet...", false, false);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(tasks.get(i).toString());
        }

        return new HandlerResult(sb.toString(), false, false);
    }

    /**
     * Filters and prints tasks according to the search term.
     * @param tasks List of tasks to search from.
     * @param search Search term to search for.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleFind(
            List<Task> tasks,
            String search
    ) {
        assert tasks != null;

        List<Task> filtered = tasks.stream()
                .filter(s -> s.getDescription().contains(search))
                .toList();

        if (filtered.isEmpty()) {
            return new HandlerResult("No tasks were found...", false, false);
        }

        StringBuilder sb = new StringBuilder("Here are the matching tasks I found:\n");
        for (int i = 0; i < filtered.size(); ++i) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(filtered.get(i).toString());
        }

        return new HandlerResult(sb.toString(), false, false);
    }

    /**
     * Adds a ToDoTask.
     * @param tasks List of tasks to update.
     * @param description Description of the ToDoTask to be added.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleAddTodo(
            List<Task> tasks,
            String description
    ) {
        assert tasks != null;

        Task task = new ToDoTask(description);
        tasks.add(task);
        saveTasks(tasks);

        return new HandlerResult("Added: " + description, false, false);
    }

    /**
     * Adds a DeadlineTask.
     * @param tasks List of tasks to update.
     * @param description Description of the DeadlineTask to be added.
     * @param byDateString Date by which the task should be completed
     *                     as a String in yyyy-MM-dd format.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleAddDeadline(
            List<Task> tasks,
            String description,
            String byDateString
    ) {
        assert tasks != null;

        LocalDate byDate = LocalDate.now();
        try {
            byDate = LocalDate.parse(byDateString);
        } catch (DateTimeParseException e) {
            return handleInvalid("You must input a date in the format 'yyyy-mm-dd' for the 'by' argument.");
        }

        Task task = new DeadlineTask(description, byDate);
        tasks.add(task);
        saveTasks(tasks);

        return new HandlerResult("Added: " + task, false, false);
    }

    /**
     * Adds a EventTask
     * @param tasks List of tasks to update.
     * @param description Description of the EventTask to be added.
     * @param fromDateString Date from which the event will start
     *                       as a String in yyyy-MM-dd format.
     * @param toDateString Date to which the event will last
     *                     as a String in yyyy-MM-dd format.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleAddEvent(
            List<Task> tasks,
            String description,
            String fromDateString,
            String toDateString
    ) {
        assert tasks != null;

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        try {
            fromDate = LocalDate.parse(fromDateString);
            toDate = LocalDate.parse(toDateString);
        } catch (DateTimeParseException e) {
            return handleInvalid("You must input a date in the format 'yyyy-mm-dd' "
                    + "for the 'from' and 'by arguments.");
        }

        Task task = new EventTask(description, fromDate, toDate);
        tasks.add(task);
        saveTasks(tasks);

        return new HandlerResult("Added: " + task, false, false);
    }

    /**
     * Marks a task as completed.
     * @param tasks List of tasks to update.
     * @param indexArg Index of the task in the list to mark as completed.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleMark(
            List<Task> tasks,
            String indexArg
    ) {
        assert tasks != null;

        int index = parseIndex(indexArg, tasks.size());
        if (index == -1) {
            return handleInvalid("There is no such Task, or your input was not a valid number.");
        }
        tasks.get(index).mark();
        saveTasks(tasks);

        return new HandlerResult(tasks.get(index).toString(), false, false);
    }

    /**
     * Marks a task as incomplete.
     * @param tasks List of tasks to update.
     * @param indexArg Index of the task in the list to mark as incomplete.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleUnmark(
            List<Task> tasks,
            String indexArg
    ) {
        assert tasks != null;

        int index = parseIndex(indexArg, tasks.size());
        if (index == -1) {
            return handleInvalid("There is no such Task, or your input was not a valid number.");
        }

        tasks.get(index).unmark();
        saveTasks(tasks);

        return new HandlerResult(tasks.get(index).toString(), false, false);
    }

    /**
     * Sets the priority level for a task.
     * @param tasks List of tasks to update.
     * @param indexArg Index of the task in the list to mark as incomplete.
     * @param taskPriorityString Priority level to set the task to.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handlePrioritise(
            List<Task> tasks,
            String indexArg,
            String taskPriorityString
    ) {
        assert tasks != null;

        int index = parseIndex(indexArg, tasks.size());
        if (index == -1) {
            return handleInvalid("There is no such Task, or your input was not a valid number.");
        }

        if (!taskPriorityString.equals("high")
            && !taskPriorityString.equals("medium")
            && !taskPriorityString.equals("low")) {
            return handleInvalid("Priority level must be one of: "
                    + "'high', 'medium', or 'low'.");
        }

        tasks.get(index).setTaskPriority(stringToTaskPriority(taskPriorityString));
        saveTasks(tasks);

        return new HandlerResult(tasks.get(index).toString(), false, false);
    }

    /**
     * Deletes a task.
     * @param tasks List of tasks to update.
     * @param indexArg Index of the task in the list to delete.
     * @return true If program should exit command loop Else false.
     */
    public static HandlerResult handleDelete(
            List<Task> tasks,
            String indexArg
    ) {
        assert tasks != null;

        int index = parseIndex(indexArg, tasks.size());
        if (index == -1) {
            return handleInvalid("There is no such Task, or your input was not a valid number.");
        }

        Task task = tasks.remove(index);
        saveTasks(tasks);

        return new HandlerResult(task.toString(), false, false);
    }

    /**
     * Prints an error message when the user enters invalid input.
     * @param msg Error message to print.
     * @return false; the program should continue running
     */
    public static HandlerResult handleInvalid(
            String msg
    ) {
        return new HandlerResult(msg, true, false);
    }

    /**
     * Prints the goodbye message when the user exits the program.
     * @return true; the program should exit here
     */
    public static HandlerResult handleExit() {
        return new HandlerResult("Bye! See you again soon.", false, true);
    }

    /**
     * Saves the updated tasks to a text file.
     * @param tasks List of tasks to be saved.
     */
    private static void saveTasks(List<Task> tasks) {
        assert tasks != null;

        try {
            putTasks(tasks);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Parses an input String argument into an integer.
     * @param indexArg Index of the task in the list to access.
     * @param tasksSize Size of task list.
     * @return -1 If unable to parse Else index of the task in the list.
     */
    private static int parseIndex(String indexArg, int tasksSize) {
        try {
            int index = Integer.parseInt(indexArg) - 1;
            if (index < 0 || index >= tasksSize) {
                return -1;
            }
            return index;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
