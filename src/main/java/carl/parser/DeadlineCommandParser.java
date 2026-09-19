package carl.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import carl.commands.DeadlineCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;
import carl.util.DateParser;

/**
 * Parses input arguments and creates a new DeadlineCommand object.
 */
public class DeadlineCommandParser implements Parser<DeadlineCommand> {

    private static final String USAGE = "deadline <description> /by yyyy-MM-dd HHmm";
    private static final Pattern BY_MARKER = Pattern.compile("(?<!\\S)/by(?!\\S)");

    /**
     * Parses the given input string to extract the task name and deadline date.
     *
     * @param input Raw input string containing the task name and deadline.
     * @return Deadline command initialized with the parsed name and date.
     * @throws CarlException If the input format is incorrect or the date is invalid.
     */
    @Override
    public DeadlineCommand parse(String input) throws CarlException {
        Matcher marker = BY_MARKER.matcher(input);
        if (!marker.find()) {
            throw new CarlCommandException(USAGE);
        }

        int markerStart = marker.start();
        int markerEnd = marker.end();
        if (marker.find()) {
            throw new CarlCommandException("Specify `/by` exactly once.");
        }

        String name = InputValidator.normalizeTaskName(input.substring(0, markerStart), USAGE);
        String byStr = input.substring(markerEnd).strip();
        if (byStr.isEmpty()) {
            throw new CarlCommandException(USAGE);
        }

        try {
            LocalDateTime by = DateParser.parseDateTime(byStr);
            return new DeadlineCommand(name, by);
        } catch (DateTimeParseException e) {
            throw new CarlCommandException(DateParser.getDateTimeErrorMessage());
        }
    }
}
