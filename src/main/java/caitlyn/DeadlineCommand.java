package caitlyn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a deadline task from a description and a standalone date marker.
 */
public final class DeadlineCommand extends Command {
    /** Recognizes complete date markers rather than substrings inside descriptions. */
    private static final Pattern DEADLINE_MARKER = Pattern.compile("(?:^|[ \\t]+)/by(?=[ \\t]|$)");

    /** Explains the required spelling and placement of the deadline marker. */
    private static final String FORMAT_ERROR = "I beg your pardon, master. Please provide a deadline in the format: "
            + "deadline task /by date.";

    /** The text after the {@code deadline} command name. */
    private final String commandArguments;

    /**
     * Creates a deadline command with its unparsed arguments.
     *
     * @param commandArguments the text containing the task description and deadline.
     */
    public DeadlineCommand(String commandArguments) {
        this(commandArguments, new TaskStorage());
    }

    /**
     * Creates a command with a supplied destination for task changes.
     *
     * @param commandArguments the command input.
     * @param storage the destination for task changes.
     */
    DeadlineCommand(String commandArguments, TaskStorage storage) {
        super(storage);
        this.commandArguments = commandArguments;
    }

    /**
     * Parses the deadline arguments, adds the new task, and saves it.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display the result.
     * @throws CaitlynException when the command or deadline is invalid.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        Matcher markers = DEADLINE_MARKER.matcher(commandArguments);
        if (!markers.find()) {
            throw new CaitlynException(FORMAT_ERROR);
        }
        String description = commandArguments.substring(0, markers.start()).trim();
        String by = commandArguments.substring(markers.end()).trim();
        if (markers.find()) {
            throw new CaitlynException(FORMAT_ERROR);
        }
        if (description.isEmpty() || by.isEmpty()) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please provide both a task description and a deadline.");
        }
        try {
            Task task = new Deadline(description, by);
            addTask(tasks, task);
            ui.showTaskAdded(task, tasks.size());
        } catch (IllegalArgumentException exception) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please use a valid date such as 2019-10-15 "
                            + "or 2/12/2019 1800.");
        }
    }
}
