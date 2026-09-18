package blud.command;

import java.util.Locale;

import blud.exception.CommandFormatException;
import blud.model.TaskList;

/** Parses a sort command, updates task order, and formats its response. */
public class SortTasks {
    private final TaskList taskList;

    /** Creates a sort operation for the supplied task list. */
    public SortTasks(TaskList taskList) {
        this.taskList = taskList;
    }

    /** Sorts tasks according to command arguments and returns a response. */
    public String execute(String[] splitInput) {
        if (splitInput.length < 2) {
            throw new CommandFormatException(
                    "Please specify a sort criterion: deadline, event, or status.");
        }
        if (splitInput.length > 3) {
            throw new CommandFormatException(
                    "Invalid sort command. Usage: sort <deadline|event|status> [asc|desc].");
        }

        TaskList.SortCriterion criterion = parseSortCriterion(splitInput[1]);
        TaskList.SortDirection direction = splitInput.length == 3
                ? parseSortDirection(splitInput[2]) : TaskList.SortDirection.ASCENDING;
        if (taskList.getSize() == 0) {
            return "There are no tasks to sort.";
        }

        taskList.sort(criterion, direction);
        String directionName = direction == TaskList.SortDirection.ASCENDING ? "ascending" : "descending";
        return String.format("Tasks sorted by %s in %s order.", getDisplayName(criterion), directionName);
    }

    /** Converts a user-entered criterion to its task-list representation. */
    private TaskList.SortCriterion parseSortCriterion(String criterion) {
        return switch (criterion.toLowerCase(Locale.ROOT)) {
            case "deadline" -> TaskList.SortCriterion.DEADLINE;
            case "event" -> TaskList.SortCriterion.EVENT;
            case "status" -> TaskList.SortCriterion.STATUS;
            default -> throw new CommandFormatException(
                    "Invalid sort criterion. Please use deadline, event, or status.");
        };
    }

    /** Converts a user-entered direction to its task-list representation. */
    private TaskList.SortDirection parseSortDirection(String direction) {
        return switch (direction.toLowerCase(Locale.ROOT)) {
            case "asc" -> TaskList.SortDirection.ASCENDING;
            case "desc" -> TaskList.SortDirection.DESCENDING;
            default -> throw new CommandFormatException("Invalid sort direction. Please use asc or desc.");
        };
    }

    /** Returns the user-facing name for a sort criterion. */
    private String getDisplayName(TaskList.SortCriterion criterion) {
        return switch (criterion) {
            case DEADLINE -> "deadline";
            case EVENT -> "event start date";
            case STATUS -> "completion status";
        };
    }
}
