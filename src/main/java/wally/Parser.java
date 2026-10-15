package wally;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Processes commands and returns the corresponding responses.
 */
public class Parser {
    /**
     * Loads a task command with an optional completion prefix from storage.
     * Legacy entries without a prefix remain incomplete. Rejected entries do not
     * change any existing task's completion status.
     *
     * @param entry Saved task entry.
     * @param tasklist List receiving the task.
     */
    public static void parseSavedTask(String entry, Tasklist tasklist) {
        boolean completed = entry.startsWith("[X] ");
        String command = completed ? entry.substring(4) : entry;
        if (!command.startsWith("todo ") && !command.startsWith("deadline ")
                && !command.startsWith("event ")) {
            return;
        }
        int previousSize = tasklist.getSize();
        processCommand(command, tasklist);
        if (tasklist.getSize() > previousSize) {
            tasklist.getTask(tasklist.getSize()).setCompleted(completed);
        }
    }

    /**
     * Validates a background colour and returns its CSS colour name.
     *
     * @param colour Colour argument from the background command.
     * @return Supported CSS colour name, with blue mapped to light blue.
     * @throws InvalidBackgroundColourException If the colour is unsupported or missing.
     */
    public static String parseBackgroundColour(String colour) throws InvalidBackgroundColourException {
        switch (colour.trim().toLowerCase(Locale.ENGLISH)) {
            case "black":
                return "black";
            case "white":
                return "white";
            case "blue":
            case "light blue":
                return "lightblue";
            default:
                throw new InvalidBackgroundColourException();
        }
    }

    /**
     * Processes user input and performs the corresponding action.
     * Handles errors caused by invalid commands.
     *
     * @param command  User input.
     * @param tasklist List of tasks to update.
     * @return Wally's response.
     */
    public static String processCommand(String command, Tasklist tasklist) {
        assert command != null : "Commands passed to the parser must not be null";
        try {
            return executeCommand(command, tasklist);
        } catch (InvalidCommandException e) {
            return "Invalid Command Entered!";
        } catch (InvalidDeadlineException e) {
            return "Format: deadline <name> /by <date: yyyy-MM-dd> <time: HH:mm>";
        } catch (InvalidEventException e) {
            return e.getMessage();
        } catch (DuplicateTaskException e) {
            return e.getMessage();
        } catch (DateTimeParseException e) {
            return "Invalid date or time. Use a valid date and time in yyyy-MM-dd HH:mm format.";
        } catch (IndexOutOfBoundsException e) {
            return "Enter an index between 1 and " + tasklist.getSize();
        } catch (EmptyTaskingsException e) {
            return "You have no tasklist yet!";
        }
    }

    /**
     * Routes a valid command to its command-specific operation.
     *
     * @param command  User input.
     * @param tasklist List of tasks to update.
     * @return Response for the executed command.
     * @throws InvalidCommandException  If the command is unsupported.
     * @throws DuplicateTaskException   If the task description already exists.
     * @throws InvalidDeadlineException If a deadline command has an invalid format.
     * @throws InvalidEventException    If an event command has an invalid format or time range.
     * @throws EmptyTaskingsException   If an indexed operation is attempted on an
     *                                  empty list.
     */
    private static String executeCommand(String command, Tasklist tasklist)
            throws InvalidCommandException, InvalidDeadlineException, InvalidEventException,
            EmptyTaskingsException, DuplicateTaskException {
        if (command.equals("bye")) {
            return "TERMINATE_PROGRAM";
        } else if (command.equals("list")) {
            return listTasks(tasklist);
        } else if (command.equals("schedule")) {
            return listTasksByDate(tasklist);
        } else if (command.matches("mark \\d+")) {
            return updateTaskCompletion(command, tasklist, true);
        } else if (command.matches("unmark \\d+")) {
            return updateTaskCompletion(command, tasklist, false);
        } else if (command.matches("todo .*")) {
            return addTodo(command, tasklist);
        } else if (command.matches("deadline .*")) {
            return addDeadline(command, tasklist);
        } else if (command.matches("event .*")) {
            return addEvent(command, tasklist);
        } else if (command.matches("delete \\d+")) {
            return deleteTask(command, tasklist);
        } else if (command.matches("find .*")) {
            return findTasks(command, tasklist);
        }

        throw new InvalidCommandException();
    }

    /** Returns a numbered list of all tasks. */
    private static String listTasks(Tasklist tasklist) {
        String output = "Here are the tasks in your list:";
        for (int i = 0; i < tasklist.getSize(); i++) {
            output += "\n" + (i + 1) + ". " + tasklist.getTask(i + 1);
        }
        return output;
    }

    /**
     * Returns tasks ordered by their relevant date-time, followed by to-do tasks.
     * The displayed numbers remain the tasks' original positions so that they can
     * be used directly with commands such as {@code mark} and {@code delete}.
     */
    private static String listTasksByDate(Tasklist tasklist) {
        List<Integer> taskPositions = new ArrayList<>();
        for (int i = 1; i <= tasklist.getSize(); i++) {
            taskPositions.add(i);
        }

        taskPositions.sort(Comparator.comparing(
                position -> tasklist.getTask(position).getScheduleDateTime(),
                Comparator.nullsLast(Comparator.naturalOrder())));

        String output = "Here are the tasks in your schedule:";
        for (int position : taskPositions) {
            output += "\n" + position + ". " + tasklist.getTask(position);
        }
        return output;
    }

    /** Updates the completion status of the task identified by a command. */
    private static String updateTaskCompletion(String command, Tasklist tasklist, boolean isCompleted)
            throws EmptyTaskingsException {
        int taskIndex = getTaskIndex(command, tasklist);
        Task task = tasklist.getTask(taskIndex);
        task.setCompleted(isCompleted);
        String status = isCompleted ? "done" : "not done yet";
        return "The following task has been marked as " + status + ":\n" + task;
    }

    /** Adds a to-do task described by a command. */
    private static String addTodo(String command, Tasklist tasklist) throws DuplicateTaskException {
        String taskDescription = command.split("todo ")[1];
        tasklist.addTask(new ToDo(taskDescription));
        return getTaskAddedResponse(tasklist);
    }

    /** Adds a deadline task described by a command. */
    private static String addDeadline(String command, Tasklist tasklist)
            throws InvalidDeadlineException, DuplicateTaskException {
        if (!command.matches("deadline .* /by \\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d")) {
            throw new InvalidDeadlineException();
        }

        String[] commandParts = command.split("deadline ")[1].split(" /by ");
        tasklist.addTask(new Deadline(commandParts[0], commandParts[1]));
        return getTaskAddedResponse(tasklist);
    }

    /** Adds an event task described by a command. */
    private static String addEvent(String command, Tasklist tasklist)
            throws InvalidEventException, DuplicateTaskException {
        if (!command.matches("event .* /from \\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d"
                + " /to \\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d")) {
            throw new InvalidEventException();
        }

        String[] commandParts = command.split("event ")[1].split(" /from | /to ");
        tasklist.addTask(new Event(commandParts[0], commandParts[1], commandParts[2]));
        return getTaskAddedResponse(tasklist);
    }

    /** Deletes the task identified by a command. */
    private static String deleteTask(String command, Tasklist tasklist) throws EmptyTaskingsException {
        int taskIndex = getTaskIndex(command, tasklist);
        Task task = tasklist.getTask(taskIndex);
        tasklist.removeTask(taskIndex);
        return "The following task has been removed:\n" + task
                + "\nNow you have " + tasklist.getSize() + " tasks in the list.";
    }

    /**
     * Returns the tasks whose string representation contains the requested search
     * term.
     */
    private static String findTasks(String command, Tasklist tasklist) {
        String searchTerm = command.split("find ")[1];
        String output = "Here are the matching tasks in your list:";
        for (int i = 1; i <= tasklist.getSize(); i++) {
            if (tasklist.getTask(i).toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                output += "\n" + i + "." + tasklist.getTask(i);
            }
        }
        return output;
    }

    /** Validates and returns the one-based task index specified by a command. */
    private static int getTaskIndex(String command, Tasklist tasklist) throws EmptyTaskingsException {
        if (tasklist.getSize() == 0) {
            throw new EmptyTaskingsException();
        }

        int taskIndex = Integer.parseInt(command.split(" ")[1]);
        if (taskIndex < 1 || taskIndex > tasklist.getSize()) {
            throw new IndexOutOfBoundsException();
        }
        return taskIndex;
    }

    /** Returns the shared response after a task is added. */
    private static String getTaskAddedResponse(Tasklist tasklist) {
        return "The following task has been added:\n" + tasklist.getTask(tasklist.getSize())
                + "\nNow you have " + tasklist.getSize() + " tasks in the list.";
    }
}
