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
            String description = input.strip().substring(4).trim();
            requireDescription(description, "A todo needs a description.");
            return new Todo(description);
        }
        if (command == Command.DEADLINE) {
            String arguments = input.strip().substring(8).trim();
            String[] parts = splitOnParameter(
                    arguments, "by", "Use: deadline DESCRIPTION /by DATE");
            String description = parts[0].trim();
            String by = parts[1].trim();
            requireDescription(description, "A deadline needs a description.");
            requireText(by, "A deadline needs a date after /by.");
            return new Deadline(description, parseDate(by));
        }
        if (command == Command.EVENT) {
            String usage = "Use: event DESCRIPTION /from START /to END";
            String arguments = input.strip().substring(5).trim();
            String[] fromParts = splitOnParameter(arguments, "from", usage);
            String[] toParts = splitOnParameter(fromParts[1], "to", usage);
            String description = fromParts[0].trim();
            String from = toParts[0].trim();
            String to = toParts[1].trim();
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

    /** Rejects arguments supplied to a command that does not accept any. */
    public void validateNoArguments(String input, String command) throws KopiException {
        if (!input.strip().equalsIgnoreCase(command)) {
            throw new KopiException("Use: " + command);
        }
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

    private String[] splitOnParameter(String input, String parameter, String usage) throws KopiException {
        String[] parts = input.split("(?i)\\s+/" + parameter + "\\s+", -1);
        if (parts.length != 2) {
            throw new KopiException(usage);
        }
        return parts;
    }
}
