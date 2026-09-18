package luna.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Event;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that adds an event task.
 */
public class EventCommand extends Command {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern TIME_PARAMETER = Pattern.compile("(?i)(?<!\\S)/(from|to)(?!\\S)");
    private static final String USAGE_MESSAGE = "Please use event <description> "
            + "/from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>.";

    private final String input;

    /**
     * Creates an event command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public EventCommand(String input) {
        this.input = input;
    }

    /**
     * Parses the event details, adds the task, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete, a date-time is invalid, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        String arguments = input.substring("event".length()).trim();
        if (!hasOneOrderedParameterPair(arguments)) {
            throw new LunaException(USAGE_MESSAGE);
        }

        String[] fromParts = arguments.split("(?i)\\s+/from\\s+", -1);
        if (fromParts.length != 2) {
            throw new LunaException(USAGE_MESSAGE);
        }
        String[] toParts = fromParts[1].split("(?i)\\s+/to\\s+", -1);
        if (toParts.length != 2 || fromParts[0].isBlank()
                || toParts[0].isBlank() || toParts[1].isBlank()) {
            throw new LunaException(USAGE_MESSAGE);
        }

        String description = fromParts[0].trim();

        try {
            LocalDateTime from = LocalDateTime.parse(toParts[0].trim(), INPUT_FORMAT);
            LocalDateTime to = LocalDateTime.parse(toParts[1].trim(), INPUT_FORMAT);
            if (!from.isBefore(to)) {
                throw new LunaException("The event end time must be later than its start time.");
            }

            Event event = new Event(description, from, to);
            addTask(event, tasks, ui, storage);
        } catch (DateTimeParseException e) {
            throw new LunaException("That event date or time is not valid. Please use yyyy-MM-dd HHmm, "
                    + "for example 2026-09-30 1830.");
        }
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for an event command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }

    /**
     * Returns whether the arguments contain exactly one {@code /from} followed by one {@code /to}.
     */
    private boolean hasOneOrderedParameterPair(String arguments) {
        Matcher matcher = TIME_PARAMETER.matcher(arguments);
        boolean hasFromFirst = matcher.find() && matcher.group(1).equalsIgnoreCase("from");
        boolean hasToSecond = matcher.find() && matcher.group(1).equalsIgnoreCase("to");
        return hasFromFirst && hasToSecond && !matcher.find();
    }
}
