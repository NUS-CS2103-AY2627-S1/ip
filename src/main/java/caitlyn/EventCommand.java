package caitlyn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates an event task from a description and ordered date boundaries.
 */
public final class EventCommand extends Command {
    /** Recognizes standalone boundary markers without matching parts of descriptions. */
    private static final Pattern BOUNDARY_MARKER = Pattern.compile("(?:^|[ \\t]+)/(from|to)(?=[ \\t]|$)");

    /** Explains the required order and spelling of event arguments. */
    private static final String FORMAT_ERROR = "I beg your pardon, master. Please provide an event in the format: "
            + "event task /from start /to end.";

    /** The text after the {@code event} command name. */
    private final String commandArguments;

    /**
     * Creates an event command with its unparsed arguments.
     *
     * @param commandArguments the text containing the event description and date range.
     */
    public EventCommand(String commandArguments) {
        this(commandArguments, new TaskStorage());
    }

    /**
     * Creates a command with a supplied destination for task changes.
     *
     * @param commandArguments the command input.
     * @param storage the destination for task changes.
     */
    EventCommand(String commandArguments, TaskStorage storage) {
        super(storage);
        this.commandArguments = commandArguments;
    }

    /**
     * Parses the event arguments, adds the new task, and saves it.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display the result.
     * @throws CaitlynException when the command or event dates are invalid.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        Matcher markers = BOUNDARY_MARKER.matcher(commandArguments);
        if (!markers.find() || !"from".equals(markers.group(1))) {
            throw new CaitlynException(FORMAT_ERROR);
        }
        String description = commandArguments.substring(0, markers.start()).trim();
        int fromStart = markers.end();
        if (!markers.find() || !"to".equals(markers.group(1))) {
            throw new CaitlynException(FORMAT_ERROR);
        }
        String from = commandArguments.substring(fromStart, markers.start()).trim();
        String to = commandArguments.substring(markers.end()).trim();
        if (markers.find()) {
            throw new CaitlynException(FORMAT_ERROR);
        }
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please provide a description, start time, "
                            + "and end time for the event.");
        }
        try {
            Task task = new Event(description, from, to);
            addTask(tasks, task);
            ui.showTaskAdded(task, tasks.size());
        } catch (IllegalArgumentException exception) {
            throw new CaitlynException("I beg your pardon, master. " + exception.getMessage());
        }
    }
}
