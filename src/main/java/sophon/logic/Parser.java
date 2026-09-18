package sophon.logic;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import sophon.exception.SophonException;
import sophon.model.Deadline;
import sophon.model.Event;
import sophon.model.Todo;

/**
 * Makes sense of user command text.
 */
public class Parser {
    /**
     * Parses user input into a command that Sophon can execute.
     *
     * @param input raw user input.
     * @return parsed command.
     * @throws SophonException if the command is recognized but malformed.
     */
    public static Command parse(String input) throws SophonException {
        if (input.equals("bye")) {
            return new Command(Command.Type.BYE);
        } else if (input.equals("list")) {
            return new Command(Command.Type.LIST);
        } else if (input.equals("find") || input.startsWith("find ")) {
            return new Command(Command.Type.FIND, parseKeyword(input));
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            return new Command(Command.Type.ADD_TODO, parseTodo(input));
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return new Command(Command.Type.ADD_DEADLINE, parseDeadline(input));
        } else if (input.equals("event") || input.startsWith("event ")) {
            return new Command(Command.Type.ADD_EVENT, parseEvent(input));
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            return new Command(Command.Type.MARK, parseTaskIndex(input, "mark",
                    "Tell me which task has completed its observation."));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            return new Command(Command.Type.UNMARK, parseTaskIndex(input, "unmark",
                    "Tell me which task has returned to observation."));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            return new Command(Command.Type.DELETE, parseTaskIndex(input, "delete", "Tell me which task to remove."));
        } else {
            return new Command(Command.Type.UNKNOWN);
        }
    }

    /**
     * Parses a one-based task number from a command and returns its zero-based index.
     *
     * @param input full user command.
     * @param commandWord command word before the task number.
     * @param missingTaskMessage message to show if the task number is missing.
     * @return zero-based task index.
     * @throws SophonException if the task number is missing or not a numeral.
     */
    public static int parseTaskIndex(String input, String commandWord, String missingTaskMessage)
            throws SophonException {
        String taskNumberText = input.substring(commandWord.length()).trim();
        if (taskNumberText.isBlank()) {
            throw new SophonException(missingTaskMessage);
        }

        try {
            return Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException e) {
            throw new SophonException("Task numbers must be written as numerals.");
        }
    }

    private static String parseKeyword(String input) throws SophonException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isBlank()) {
            throw new SophonException("Tell me what signal to search for.");
        }

        return keyword;
    }

    private static Todo parseTodo(String input) throws SophonException {
        String description = input.length() == 4 ? "" : input.substring(5).trim();
        if (description.isBlank()) {
            throw new SophonException("You have given me nothing to observe.\n"
                    + "A todo requires a description.");
        }

        checkFileSafe(description);
        return new Todo(description);
    }

    private static Deadline parseDeadline(String input) throws SophonException {
        String details = input.length() == 8 ? "" : input.substring(8).trim();
        int byIndex = details.indexOf("/by");
        if (details.isBlank()) {
            throw new SophonException("You have told me neither what must be done nor when.\n"
                    + "A deadline requires both.");
        } else if (byIndex == -1) {
            throw new SophonException("I know what must be done, but not when.\n"
                    + "Specify when it is due using /by.");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 3).trim();
        if (description.isBlank() && by.isBlank()) {
            throw new SophonException("You have given me a boundary, but nothing to bind to it.\n"
                    + "Tell me what must be done, and when.");
        } else if (description.isBlank()) {
            throw new SophonException("I know when, but not what.\n"
                    + "Give the deadline a description.");
        } else if (by.isBlank()) {
            throw new SophonException("I see the task, but its deadline remains unknown.\n"
                    + "Tell me when it is due.");
        }

        checkFileSafe(description);
        checkFileSafe(by);
        return new Deadline(description, convertDate(by));
    }

    private static Event parseEvent(String input) throws SophonException {
        String details = input.length() == 5 ? "" : input.substring(5).trim();
        int fromIndex = details.indexOf("/from");
        int toIndex = details.indexOf("/to");
        if (details.isBlank()) {
            throw new SophonException("You have told me neither what will happen nor when.\n"
                    + "An event requires both.");
        } else if (fromIndex == -1 && toIndex == -1) {
            throw new SophonException("I know what will happen, but not when.\n"
                    + "Tell me when it begins and when it ends.");
        } else if (fromIndex == -1) {
            throw new SophonException("I see when it ends, but not when it begins.\n"
                    + "Tell me when it begins.");
        } else if (toIndex == -1 || toIndex < fromIndex) {
            throw new SophonException("I see when it begins, but not when it ends.\n"
                    + "Specify an end time using /to.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 5, toIndex).trim();
        String to = details.substring(toIndex + 3).trim();
        if (description.isBlank()) {
            throw new SophonException("I know when, but not what.\n"
                    + "Give the event a description.");
        } else if (from.isBlank()) {
            throw new SophonException("I see when it ends, but not when it begins.\n"
                    + "Tell me when it begins.");
        } else if (to.isBlank()) {
            throw new SophonException("I see when it begins, but its end remains unknown.\n"
                    + "Tell me when it ends.");
        }

        checkFileSafe(description);
        checkFileSafe(from);
        checkFileSafe(to);
        return new Event(description, convertDate(from), convertDate(to));
    }

    private static void checkFileSafe(String text) throws SophonException {
        if (text.contains(" | ")) {
            throw new SophonException("Please do not use \" | \" in task details.");
        }
    }

    private static LocalDate convertDate(String text) throws SophonException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new SophonException("Please enter the date in yyyy-MM-dd format.");
        }
    }
}
