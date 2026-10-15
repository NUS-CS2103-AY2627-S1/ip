package clover.command;

import java.time.LocalDate;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.parser.TaskArguments;
import clover.storage.Storage;
import clover.task.Event;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Adds an event task with a description, start date, and end date.
 */
public class EventCommand extends Command {
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    private final String arguments;

    /**
     * Creates a command from the text after the {@code event} keyword.
     */
    public EventCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Validates, adds, and saves the event task.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        Parser.rejectUnknownParameters(arguments, "/from", "/to", "/for");
        TaskArguments parsedArguments = Parser.parseTaskArguments(arguments);
        String taskArguments = parsedArguments.getTaskArguments();
        int fromIndex = Parser.findSingleMarker(taskArguments, FROM_MARKER);
        int toIndex = Parser.findSingleMarker(taskArguments, TO_MARKER);
        if (fromIndex <= 0 || toIndex <= fromIndex + FROM_MARKER.length()) {
            throw invalidFormat();
        }

        String description = taskArguments.substring(0, fromIndex).trim();
        String startDate = taskArguments.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String endDate = taskArguments.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            throw invalidFormat();
        }

        LocalDate start = Parser.parseDate(startDate);
        LocalDate end = Parser.parseDate(endDate);
        if (!end.isAfter(start)) {
            throw new CloverException("An event's end date must be after its start date.");
        }
        String tutoreeName = validateTutoreeName(parsedArguments.getTutoreeName(), tutorees);
        Event task = new Event(description, start, end, tutoreeName);
        if (tasks.containsEquivalent(task)) {
            throw new CloverException("That study quest is already in the grove.");
        }
        tasks.add(task);
        try {
            saveTasks(tasks, storage);
        } catch (CloverException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public CommandResponseStyle getResponseStyle() {
        return CommandResponseStyle.TASK_ADDED;
    }

    /**
     * Creates the shared guidance message for malformed event commands.
     */
    private CloverException invalidFormat() {
        return new CloverException("To schedule a grove event, use: event DESCRIPTION /from START /to END. "
                + "Optional: add "
                + "/for TUTOREE NAME.");
    }
}
