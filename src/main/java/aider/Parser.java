package aider;

import java.time.LocalDate;
import java.time.LocalDateTime;

import aider.model.Deadline;
import aider.model.Event;
import aider.model.Task;
import aider.model.Todo;
import aider.util.DateTimeParser;

/** Interprets user commands and creates the requested task objects. */
public class Parser {
    /** Creates a task from a todo, deadline, or event command. */
    public Task parseTask(String command) throws AiderException {
        command = normalizeCommand(command);

        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new AiderException("A todo needs a description, for example: todo read book.");
            }
            rejectControlCharacters(description);
            return new Todo(description);
        }

        if (command.equals("deadline") || command.startsWith("deadline ")) {
            String details = command.substring("deadline".length()).trim();
            if (countOccurrences(details, " /by ") != 1) {
                throw new AiderException("A deadline must contain exactly one /by marker.");
            }
            int byIndex = details.indexOf(" /by ");
            String description = details.substring(0, byIndex).trim();
            String by = details.substring(byIndex + " /by ".length()).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new AiderException("A deadline needs a description and a date or time after /by.");
            }
            rejectControlCharacters(description);
            return new Deadline(description, DateTimeParser.parse(by), by);
        }

        if (command.equals("event") || command.startsWith("event ")) {
            String details = command.substring("event".length()).trim();
            if (countOccurrences(details, " /from ") != 1
                    || countOccurrences(details, " /to ") != 1) {
                throw new AiderException("An event must contain exactly one /from and one /to marker.");
            }
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ");
            if (toIndex <= fromIndex) {
                throw new AiderException("An event needs a description, /from time, and /to time.");
            }
            String description = details.substring(0, fromIndex).trim();
            String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
            String to = details.substring(toIndex + " /to ".length()).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new AiderException("An event needs text after its description, /from, and /to markers.");
            }
            rejectControlCharacters(description);
            LocalDateTime fromDate = DateTimeParser.parse(from);
            LocalDateTime toDate = DateTimeParser.parse(to);
            if (!toDate.isAfter(fromDate)) {
                throw new AiderException("An event must end after it starts.");
            }
            return new Event(description, fromDate, toDate, from, to);
        }

        throw new AiderException("I don't recognize that command. Try todo, deadline, event, list, mark, "
                + "unmark, or delete.");
    }

    /** Parses the date used by the on command. */
    public LocalDate parseDate(String command) throws AiderException {
        command = normalizeCommand(command);
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new AiderException("The on command needs a date, for example: on 2019-12-02.");
        }
        return DateTimeParser.parse(dateText).toLocalDate();
    }

    /** Normalizes harmless whitespace differences before command parsing. */
    private static String normalizeCommand(String command) throws AiderException {
        if (command == null || command.isBlank()) {
            throw new AiderException("Please enter a command.");
        }
        return command.trim().replaceAll("\\s+", " ");
    }

    /** Counts exact marker occurrences in a command fragment. */
    private static int countOccurrences(String text, String marker) {
        int count = 0;
        int position = 0;
        while ((position = text.indexOf(marker, position)) >= 0) {
            count++;
            position += marker.length();
        }
        return count;
    }

    /** Rejects control characters that should never be stored in a task description. */
    private static void rejectControlCharacters(String text) throws AiderException {
        for (int index = 0; index < text.length(); index++) {
            if (Character.isISOControl(text.charAt(index))) {
                throw new AiderException("Task text contains an unsupported control character.");
            }
        }
    }
}
