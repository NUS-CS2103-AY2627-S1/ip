package kopi;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Interprets user commands and their arguments. */
public class Parser {
    /** Creates a parser for Kopi commands. */
    public Parser() {
    }

    /** Extracts a supported command from a line of user input. */
    public Command parseCommand(String input) throws KopiException {
        if (input.isBlank()) {
            throw new KopiException("I don't understand an empty command.");
        }
        String commandWord = input.strip().split("\\s+", 2)[0].toUpperCase();
        try {
            return Command.valueOf(commandWord);
        } catch (IllegalArgumentException e) {
            throw new KopiException("I don't understand that command.");
        }
    }

    /** Creates a task from a validated task-creation command. */
    public Task parseTask(String input, Command command) throws KopiException {
        if (command == Command.TODO) {
            String description = input.substring(4).trim();
            requireDescription(description, "A todo needs a description.");
            return new Todo(description);
        }
        if (command == Command.DEADLINE) {
            int byIndex = input.indexOf(" /by ");
            if (byIndex < 0) {
                throw new KopiException("Use: deadline DESCRIPTION /by DATE");
            }
            String description = input.substring(8, byIndex).trim();
            String by = input.substring(byIndex + 5).trim();
            requireDescription(description, "A deadline needs a description.");
            requireText(by, "A deadline needs a date after /by.");
            return new Deadline(description, parseDate(by));
        }
        if (command == Command.EVENT) {
            int fromIndex = input.indexOf(" /from ");
            int toIndex = input.indexOf(" /to ");
            if (fromIndex < 0 || toIndex < fromIndex) {
                throw new KopiException("Use: event DESCRIPTION /from START /to END");
            }
            String description = input.substring(5, fromIndex).trim();
            String from = input.substring(fromIndex + 7, toIndex).trim();
            String to = input.substring(toIndex + 5).trim();
            requireDescription(description, "An event needs a description.");
            requireText(from, "An event needs a start date after /from.");
            requireText(to, "An event needs an end date after /to.");
            LocalDate startDate = parseDate(from);
            LocalDate endDate = parseDate(to);
            if (!startDate.isBefore(endDate)) {
                throw new KopiException("An event must end after it starts.");
            }
            return new Event(description, startDate, endDate);
        }
        throw new KopiException("That command cannot create a task.");
    }

    /** Converts a one-based task number into a valid list index. */
    public int parseTaskNumber(String input, String command, int taskCount) throws KopiException {
        String numberText = input.substring(command.length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new KopiException("Give me a valid task number for " + command + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KopiException("That task number is not in the list.");
        }
        return taskNumber - 1;
    }

    /** Parses a date in Kopi's command format. */
    public LocalDate parseDate(String text) throws KopiException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new KopiException("Use dates in yyyy-MM-dd format.");
        }
    }

    /** Extracts and validates the keyword supplied to a command. */
    public String parseKeyword(String input, String command) throws KopiException {
        String keyword = input.substring(command.length()).trim();
        requireText(keyword, "Give me a keyword to find.");
        return keyword;
    }

    private void requireText(String text, String message) throws KopiException {
        if (text.isEmpty()) {
            throw new KopiException(message);
        }
    }

    private void requireDescription(String description, String missingMessage) throws KopiException {
        requireText(description, missingMessage);
        if (description.contains("|")) {
            throw new KopiException("Task descriptions cannot contain |.");
        }
    }
}
