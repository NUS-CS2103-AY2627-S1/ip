package carl.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import carl.commands.EventCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;
import carl.util.DateParser;

/**
 * Parses input arguments and creates a new EventCommand object.
 */
public class EventCommandParser implements Parser<EventCommand> {
    private static final String USAGE = "event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm";
    private static final Pattern FROM_MARKER = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_MARKER = Pattern.compile("(?<!\\S)/to(?!\\S)");

    /**
     * Parses the given input string to extract the task name, start date, and end date.
     *
     * @param input Raw input string containing the event details.
     * @return Event command initialized with the parsed name, start date, and end date.
     * @throws CarlException If the input format is invalid, missing fields, or if start date is after end date.
     */
    @Override
    public EventCommand parse(String input) throws CarlException {
        Marker fromMarker = findSingleMarker(input, FROM_MARKER, "/from");
        Marker toMarker = findSingleMarker(input, TO_MARKER, "/to");
        if (fromMarker.start() >= toMarker.start()) {
            throw new CarlCommandException(USAGE);
        }

        String name = InputValidator.normalizeTaskName(input.substring(0, fromMarker.start()), USAGE);
        String fromStr = input.substring(fromMarker.end(), toMarker.start()).strip();
        String toStr = input.substring(toMarker.end()).strip();
        if (fromStr.isEmpty() || toStr.isEmpty()) {
            throw new CarlCommandException(USAGE);
        }

        try {
            LocalDateTime from = DateParser.parseDateTime(fromStr);
            LocalDateTime to = DateParser.parseDateTime(toStr);

            if (!from.isBefore(to)) {
                throw new CarlCommandException("The event start must be earlier than the event end.");
            }

            return new EventCommand(name, from, to);
        } catch (DateTimeParseException e) {
            throw new CarlCommandException(DateParser.getDateTimeErrorMessage());
        }
    }

    /** Finds one required marker and rejects repeated parameters. */
    private Marker findSingleMarker(String input, Pattern pattern, String markerName) throws CarlException {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new CarlCommandException(USAGE);
        }

        Marker marker = new Marker(matcher.start(), matcher.end());
        if (matcher.find()) {
            throw new CarlCommandException("Specify `" + markerName + "` exactly once.");
        }
        return marker;
    }

    /** Character range occupied by a command parameter marker. */
    private record Marker(int start, int end) {
    }
}
