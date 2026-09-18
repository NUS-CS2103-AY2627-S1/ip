package luna.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Deadline;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that adds a deadline task.
 */
public class DeadlineCommand extends Command {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern BY_PARAMETER = Pattern.compile("(?i)(?<!\\S)/by(?!\\S)");
    private static final Pattern DATE_FORMAT = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final String USAGE_MESSAGE = "Please use deadline <description> /by <yyyy-MM-dd>.";
    private static final String DATE_FORMAT_MESSAGE = "Please enter the deadline date in yyyy-MM-dd format, "
            + "for example 2026-09-30.";

    private final String input;

    /**
     * Creates a deadline command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public DeadlineCommand(String input) {
        this.input = input;
    }

    /**
     * Parses the deadline details, adds the task, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete, the date is invalid, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        String arguments = input.substring("deadline".length()).trim();
        if (BY_PARAMETER.matcher(arguments).results().count() != 1) {
            throw new LunaException(USAGE_MESSAGE);
        }

        String[] parts = arguments.split("(?i)\\s+/by\\s+", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new LunaException(USAGE_MESSAGE);
        }

        String description = parts[0].trim();
        String dateText = parts[1].trim();
        if (!DATE_FORMAT.matcher(dateText).matches()) {
            throw new LunaException(DATE_FORMAT_MESSAGE);
        }

        try {
            LocalDate date = LocalDate.parse(dateText, INPUT_FORMAT);
            Deadline deadline = new Deadline(description, date);
            addTask(deadline, tasks, ui, storage);
        } catch (DateTimeParseException e) {
            throw new LunaException(dateText + " is not a real calendar date. Please check the day and month.");
        }
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for a deadline command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
