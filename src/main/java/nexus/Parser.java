package nexus;

import java.time.format.DateTimeParseException;

/** Converts user commands into tasks. */
public class Parser {
    /** Creates the appropriate task subtype for a task command. */
    public static Task createTask(String command) throws NexusException {
        command = normalizeWhitespace(command);
        if (command.equals("todo")) {
            throw new NexusException("The description of a todo cannot be empty.");
        }
        if (command.startsWith("todo ")) {
            String description = command.substring(5);
            validateDescription(description);
            return new Todo(description);
        }
        if (command.equals("deadline")) {
            throw new NexusException("A deadline needs a description and /by date.");
        }
        if (command.startsWith("deadline ")) {
            String details = command.substring(9);
            if (details.startsWith("/by ")) {
                throw new NexusException("A deadline needs a description before /by.");
            }
            if (details.endsWith(" /by")) {
                throw new NexusException("A deadline needs a date after /by.");
            }
            if (countOccurrences(details, " /by ") != 1) {
                throw new NexusException(
                        "Use exactly one /by followed by a date in YYYY-MM-DD format.");
            }
            String[] parts = details.split(" /by ", -1);
            if (parts[0].isBlank()) {
                throw new NexusException("A deadline needs a description before /by.");
            }
            if (parts[1].isBlank()) {
                throw new NexusException("A deadline needs a date after /by.");
            }
            validateDescription(parts[0]);
            return new Deadline(parts[0], parts[1]);
        }
        if (command.equals("event")) {
            throw new NexusException(
                    "An event needs a description, /from date, and /to date.");
        }
        if (command.startsWith("event ")) {
            String details = command.substring(6);
            if (details.startsWith("/from ")) {
                throw new NexusException("An event needs a description before /from.");
            }
            if (details.contains(" /from /to ")) {
                throw new NexusException("An event needs a date after /from.");
            }
            if (details.endsWith(" /to")) {
                throw new NexusException("An event needs a date after /to.");
            }
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ");
            boolean hasOneFrom = countOccurrences(details, " /from ") == 1;
            boolean hasOneTo = countOccurrences(details, " /to ") == 1;
            if (!hasOneFrom || !hasOneTo) {
                throw new NexusException(
                        "Use exactly one /from and one /to, each followed by a date.");
            }
            if (fromIndex < 1 || toIndex <= fromIndex) {
                throw new NexusException(
                        "Use /from before /to, with one date after each marker.");
            }

            String description = details.substring(0, fromIndex);
            String from = details.substring(fromIndex + 7, toIndex);
            String to = details.substring(toIndex + 5);
            if (description.isBlank()) {
                throw new NexusException("An event needs a description before /from.");
            }
            if (from.isBlank() || to.isBlank()) {
                throw new NexusException(
                        "Use /from before /to, with one date after each marker.");
            }
            validateDescription(description);
            try {
                return new Event(description, from, to);
            } catch (DateTimeParseException exception) {
                throw exception;
            } catch (IllegalArgumentException exception) {
                throw new NexusException(exception.getMessage());
            }
        }
        throw new NexusException("I don't recognise that command. "
                + "Try todo, deadline, event, list, find, sort, mark, unmark, delete, or bye.");
    }

    /** Trims input and collapses repeated whitespace to a single space. */
    private static String normalizeWhitespace(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }

    /** Counts non-overlapping appearances of a command marker. */
    private static int countOccurrences(String text, String marker) {
        int count = 0;
        int nextIndex = 0;
        while ((nextIndex = text.indexOf(marker, nextIndex)) >= 0) {
            count++;
            nextIndex += marker.length();
        }
        return count;
    }

    /** Rejects characters that cannot be represented in the saved-data format. */
    private static void validateDescription(String description) throws NexusException {
        if (description.contains("|")) {
            throw new NexusException(
                    "Descriptions cannot contain | because Nexus uses it to save tasks.");
        }
    }
}
