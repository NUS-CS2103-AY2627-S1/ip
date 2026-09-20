package bonbon.parser;

import bonbon.exception.BonBonDateTimeParseException;
import bonbon.exception.BonBonInvalidParameterException;
import bonbon.exception.BonBonUnknownCommandException;
import bonbon.tasklist.Deadline;
import bonbon.tasklist.Event;
import bonbon.tasklist.Task;
import bonbon.tasklist.ToDo;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

/**
 * Parses input string into an array of inputs.
 */
public class Parser {
    public static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HHmm";
    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    /**
     * Returns an array of arguments based on the format of the input.
     *
     * @param input the input to be parsed.
     * @return array of arguments.
     */
    public static String[] readInput(String input) throws BonBonInvalidParameterException,
            BonBonUnknownCommandException, BonBonDateTimeParseException {
        if (input.equals("")) {
            throw new BonBonUnknownCommandException("");
        }
        String[] splitInput = input.split(" ");
        String keyword = splitInput[0];

        switch (keyword) {
            case "list":
                return new String[]{"list"};

            case "mark":
                if (splitInput.length != 2) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                return splitInput;

            case "unmark":
                if (splitInput.length != 2) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                return splitInput;

            case "todo":
                if (input.length() <= 5) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                return new String[]{"todo", input.substring(5)};

            case "deadline": {
                if (input.length() <= 9) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                String substrings = input.substring(9);
                String[] splitDates = substrings.split(Pattern.quote(" /by "));
                if (splitDates.length != 2) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                if (!isDateFormat(splitDates[1])) {
                    throw new BonBonDateTimeParseException();
                }
                return new String[]{"deadline", splitDates[0], splitDates[1]};

            }

            case "event": {
                if (input.length() <= 6) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                String substrings = input.substring(6);
                String[] splitDates = substrings.split(Pattern.quote(" /to ")
                        + "|" + Pattern.quote(" /from ") + "|" + Pattern.quote(" /at "));
                if (splitDates.length != 4) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                if (!isDateFormat(splitDates[1]) || ! isDateFormat(splitDates[2])) {
                    throw new BonBonDateTimeParseException();
                }
                return new String[]{"event", splitDates[0], splitDates[1], splitDates[2], splitDates[3]};
                }

            case "delete":
                if (splitInput.length != 2) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                return splitInput;

            case "find":
                if (input.length() <= 5) {
                    throw new BonBonInvalidParameterException(keyword);
                }
                return new String[]{"find", input.substring(5)};

            default:
                throw new BonBonUnknownCommandException(keyword);
        }
    }

    /**
     * Parses a single pipe-delimited line from the storage file into a corresponding Task object.
     *
     * @param line The raw line read from the task storage file.
     * @return The constructed Task object, or null if the task type is unrecognized.
     */
    public static Task parseTaskFromFile(String line) {
        String[] parts = line.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");

        Task task;
        switch (type) {
            case "T":
                task = new ToDo(parts[2]);
                break;
            case "D":
                task = new Deadline(parts[2], LocalDateTime.parse(parts[3], INPUT_FORMATTER));
                break;
            case "E":
                task = new Event(parts[2], LocalDateTime.parse(parts[3], INPUT_FORMATTER),
                        LocalDateTime.parse(parts[4], INPUT_FORMATTER), parts[5]);
                break;
            default:
                return null;
        }

        if (isDone) {
            task.markDone();
        }
        return task;
    }

    /**
     * Validates whether a given string matches the required date-time format pattern.
     *
     * @param s The date string to validate.
     * @return True if the string matches the expected format, false otherwise.
     */
    private static boolean isDateFormat(String s)  {
        try {
            LocalDateTime.parse(s, INPUT_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}