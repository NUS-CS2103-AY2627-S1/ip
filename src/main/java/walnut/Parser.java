package walnut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Converts user input and stored task data into Walnut objects.
 */
public class Parser {

    public static final DateTimeFormatter STORAGE_FMT =
            DateTimeFormatter.ofPattern("uuuu/MM/dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter USER_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("MMM d uuuu h a");

    /**
     * Returns the command represented by the specified command string.
     *
     * @param command Command string to parse.
     * @return Corresponding command, or {@code null} if the command is not recognized.
     */
    public static Command parseCommand(String command) {

        assert command != null && !command.isBlank()
                : "Command cannot be null or blank";
        switch (command.toLowerCase()) {
            case "todo":
                return Command.TODO;

            case "deadline":
                return Command.DEADLINE;

            case "event":
                return Command.EVENT;

            case "mark":
                return Command.MARK;

            case "unmark":
                return Command.UNMARK;

            case "list":
                return Command.LIST;

            case "bye":
                return Command.BYE;

            case "remove":
                return Command.REMOVE;

            case "find":
                return Command.FIND;

            default:
                return null;
        }
    }

    /**
     * Returns the task represented by a stored task record.
     *
     * <p>Malformed records are ignored instead of crashing the application.</p>
     *
     * @param task stored task record
     * @return parsed task, or null if the record is invalid
     */
    public static Task parseTask(String task) {
        if (task == null || task.isBlank()) {
            return null;
        }

        try {
            String[] data = task.split(" \\| ", -1);

            if (data.length < 3) {
                return null;
            }

            String taskType = data[0].trim();
            String completionStatus = data[1].trim();
            String description = data[2].trim();

            if (!completionStatus.equals("0") && !completionStatus.equals("1")) {
                return null;
            }

            if (description.isEmpty()) {
                return null;
            }

            Task newTask;

            switch (taskType) {
                case "T":
                    if (data.length != 3) {
                        return null;
                    }
                    newTask = new ToDo(description);
                    break;

                case "D":
                    if (data.length != 4) {
                        return null;
                    }
                    newTask = new Deadline(description, parseDateTime(data[3].trim()));
                    break;

                case "E":
                    if (data.length != 4) {
                        return null;
                    }

                    String[] dateTime = data[3].split("-", -1);

                    if (dateTime.length != 2
                            || dateTime[0].isBlank()
                            || dateTime[1].isBlank()) {
                        return null;
                    }

                    LocalDateTime start = Parser.parseDateTime(dateTime[0].trim());
                    LocalDateTime end = Parser.parseDateTime(dateTime[1].trim());

                    if (!end.isAfter(start)) {
                        return null;
                    }

                    newTask = new Event(description, start, end);
                    break;

                default:
                    return null;
            }

            if (completionStatus.equals("1")) {
                newTask.markAsDone();
            }

            return newTask;
        } catch (DateTimeParseException | NumberFormatException
                 | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the date and time represented by a stored date-time string.
     *
     * @param input Stored date-time string to parse.
     * @return Parsed date and time.
     */
    public static LocalDateTime parseDateTime(String input) {
        if (input == null || input.isBlank()) {
            throw new DateTimeParseException(
                    "Date-time cannot be blank",
                    input == null ? "" : input,
                    0
            );
        }

        return LocalDateTime.parse(input.trim(), STORAGE_FMT);
    }

    /**
     * Returns the date and time represented by a user-entered date-time string.
     *
     * @param input User-entered date-time string to parse.
     * @return Parsed date and time.
     */
    public static LocalDateTime parseUserDateTime(String input) {
        if (input == null || input.isBlank()) {
            throw new DateTimeParseException(
                    "Date-time cannot be blank",
                    input == null ? "" : input,
                    0
            );
        }

        return LocalDateTime.parse(input.trim(), USER_FMT);
    }
}
