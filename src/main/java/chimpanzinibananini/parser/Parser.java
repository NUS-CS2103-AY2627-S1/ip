package chimpanzinibananini.parser;

import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import chimpanzinibananini.exception.ChimpanziniBananiniException;
import chimpanzinibananini.task.Deadline;
import chimpanzinibananini.task.Event;
import chimpanzinibananini.task.Task;
import chimpanzinibananini.task.Todo;

/**
 * Converts raw user input into commands understood by the chatbot.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Lists the operations that the chatbot can execute.
     */
    public enum CommandType {
        LIST, MARK, ADD, DELETE, FIND, REMINDERS, BYE
    }

    /** A validated command together with any task or task number it needs. */
    public record ParsedCommand(CommandType type, Task task, int taskNumber, String keyword) {
        private static ParsedCommand simple(CommandType type) {
            assert type == CommandType.LIST
                    || type == CommandType.BYE
                    || type == CommandType.REMINDERS
                    : "Only LIST, BYE, and REMINDERS commands have no payload";
            return new ParsedCommand(type, null, 0, null);
        }

        private static ParsedCommand withTask(Task task) {
            assert task != null : "An ADD command must carry a task constructed by the parser";
            return new ParsedCommand(CommandType.ADD, task, 0, null);
        }

        private static ParsedCommand withTaskNumber(CommandType type, int taskNumber) {
            assert type == CommandType.MARK || type == CommandType.DELETE
                    : "Only MARK and DELETE commands use a task number";
            return new ParsedCommand(type, null, taskNumber, null);
        }

        private static ParsedCommand withKeyword(String keyword) {
            return new ParsedCommand(CommandType.FIND, null, 0, keyword);
        }
    }

    /**
     * Parses and validates one line of user input.
     */
    public static ParsedCommand parse(String input) throws ChimpanziniBananiniException {
        String strippedInput = input.strip();
        if (strippedInput.isEmpty()) {
            throw new ChimpanziniBananiniException("Please enter a command.");
        }
        if (strippedInput.equals("bye")) {
            return ParsedCommand.simple(CommandType.BYE);
        }

        String[] commandParts = strippedInput.split("\\s+", 2);
        String command = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1].strip() : "";

        return switch (command) {
        case "list" -> {
            requireNoArguments(arguments, "list");
            yield ParsedCommand.simple(CommandType.LIST);
        }
        case "mark" -> ParsedCommand.withTaskNumber(CommandType.MARK, parseTaskNumber(arguments));
        case "reminders" -> {
            requireNoArguments(arguments, "reminders");
            yield ParsedCommand.simple(CommandType.REMINDERS);
        }
        case "todo" -> {
            requireValue(arguments, "BROTHER WHERE IS THE TASK 💀 Todo cannot be empty.");
            yield ParsedCommand.withTask(new Todo(arguments));
        }
        case "deadline" -> ParsedCommand.withTask(parseDeadline(arguments));
        case "event" -> ParsedCommand.withTask(parseEvent(arguments));
        case "delete" -> ParsedCommand.withTaskNumber(CommandType.DELETE, parseTaskNumber(arguments));
        case "find" -> {
            requireValue(arguments, "Please provide a keyword.");
            yield ParsedCommand.withKeyword(arguments);
        }
        default -> throw new ChimpanziniBananiniException(
                "Bro is speaking enchantment table 💀 I don't know that command.");
        };
    }

    /** Parses a deadline while translating date errors into chatbot errors. */
    private static Task parseDeadline(String arguments) throws ChimpanziniBananiniException {
        String[] fields = splitRequired(arguments, " /by ",
                "Use: deadline DESCRIPTION /by DATE (for example, 2/12/2019 1800)");
        try {
            return new Deadline(fields[0], fields[1]);
        } catch (DateTimeParseException e) {
            throw new ChimpanziniBananiniException("Invalid deadline date. " + e.getMessage());
        }
    }

    /** Parses an event description and its start and end values. */
    private static Task parseEvent(String arguments) throws ChimpanziniBananiniException {
        String[] fromFields = splitRequired(arguments, " /from ",
                "Use: event DESCRIPTION /from START /to END");
        String[] toFields = splitRequired(fromFields[1], " /to ",
                "Use: event DESCRIPTION /from START /to END");
        return new Event(fromFields[0], toFields[0], toFields[1]);
    }

    /** Splits an argument and ensures values exist on both sides. */
    private static String[] splitRequired(String value, String delimiter, String usageMessage)
            throws ChimpanziniBananiniException {
        String[] fields = value.split(Pattern.quote(delimiter), 2);
        if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
            throw new ChimpanziniBananiniException(usageMessage);
        }
        fields[0] = fields[0].strip();
        fields[1] = fields[1].strip();
        return fields;
    }

    /** Parses the one-based number supplied to mark or delete. */
    private static int parseTaskNumber(String value) throws ChimpanziniBananiniException {
        requireValue(value, "Please provide a task number.");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ChimpanziniBananiniException("Bro 💀 give me an actual task number.");
        }
    }

    /** Ensures a required command argument is present. */
    private static void requireValue(String value, String message) throws ChimpanziniBananiniException {
        if (value.isBlank()) {
            throw new ChimpanziniBananiniException(message);
        }
    }

    /** Rejects unexpected arguments for commands that do not accept them. */
    private static void requireNoArguments(String value, String command)
            throws ChimpanziniBananiniException {
        if (!value.isEmpty()) {
            throw new ChimpanziniBananiniException("The " + command + " command takes no arguments.");
        }
    }
}
