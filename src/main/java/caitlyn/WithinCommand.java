package caitlyn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and creates a task that can be completed within a specified window.
 */
public final class WithinCommand extends Command {
    /**
     * Recognizes only complete boundary tokens, including their preceding whitespace.
     */
    private static final Pattern BOUNDARY_MARKER = Pattern.compile("(?:^|[ \\t]+)/(from|to)(?=[ \\t]|$)");

    /** Explains the required description, boundary count, and boundary order. */
    private static final String FORMAT_ERROR = "I beg your pardon, master. Please use: "
            + "within task /from start /to end. Provide a description and both boundaries, "
            + "with /from followed by /to exactly once.";

    /** The unparsed text after the command name. */
    private final String commandArguments;

    /**
     * Creates a command with its unparsed arguments.
     *
     * @param commandArguments the description and window text.
     */
    public WithinCommand(String commandArguments) {
        this(commandArguments, new TaskStorage());
    }

    /**
     * Creates a within command with supplied storage.
     *
     * @param commandArguments the description and window text.
     * @param storage the destination for task changes.
     */
    WithinCommand(String commandArguments, TaskStorage storage) {
        super(storage);
        this.commandArguments = commandArguments;
    }

    /**
     * Validates the arguments and saves a new task before confirming success.
     *
     * @param tasks the current task list.
     * @param ui the destination for the confirmation.
     * @throws CaitlynException if the command, window, or save is invalid.
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
        if (markers.find() || description.isBlank() || from.isEmpty() || to.isEmpty()) {
            throw new CaitlynException(FORMAT_ERROR);
        }

        WithinTask task;
        try {
            task = new WithinTask(description, from.replaceAll("[ \\t]+", " "),
                    to.replaceAll("[ \\t]+", " "));
        } catch (IllegalArgumentException exception) {
            throw new CaitlynException(exception.getMessage());
        }
        addTask(tasks, task);
        ui.showTaskAdded(task, tasks.size());
    }
}
