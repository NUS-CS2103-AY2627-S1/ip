package nimbus.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nimbus.exception.NimbusException;
import nimbus.task.Deadline;
import nimbus.task.Event;
import nimbus.task.Todo;

/**
 * Parser to help with parsing commands.
 */
public class Parser {

    /**
     * Parses the command from the user input.
     *
     * @param input the user input.
     * @return the corresponding command.
     * @throws NimbusException if the command is not recognized.
     */
    public static Command parseCommand(String input) throws NimbusException {
        String commandWord = input.trim().split(" ", 2)[0];
        try {
            return Command.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NimbusException("I'm sorry, I don't know what that means.");
        }
    }

    /**
     * Parses the arguments for a todo command.
     *
     * @param arguments the arguments for the todo command.
     * @return the parsed todo task.
     * @throws NimbusException if the arguments are invalid.
     */
    public static Todo parseTodo(String arguments) throws NimbusException {
        String name = arguments.trim();
        if (name.isEmpty()) {
            throw new NimbusException("The description of a todo cannot be empty.");
        }
        return new Todo(name);
    }

    /**
     * Parses the arguments for a deadline command.
     *
     * @param arguments the arguments for the deadline command.
     * @return the parsed deadline task.
     * @throws NimbusException if the arguments are invalid.
     */
    public static Deadline parseDeadline(String arguments) throws NimbusException {
        String[] parts = arguments.split("/by", 2);
        String name = parts[0].trim();
        if (name.isEmpty()) {
            throw new NimbusException("The description of a deadline cannot be empty.");
        }
        if (parts.length < 2) {
            throw new NimbusException("The deadline must have a due date.");
        }
        String by = parts[1].trim();
        try {
            return new Deadline(name, LocalDate.parse(by));
        } catch (DateTimeParseException e) {
            throw new NimbusException("The due date must be in the format YYYY-MM-DD.");
        }
    }

    /**
     * Parses the arguments for an event command.
     *
     * @param arguments the arguments for the event command.
     * @return the parsed event task.
     * @throws NimbusException if the arguments are invalid.
     */
    public static Event parseEvent(String arguments) throws NimbusException {
        String[] fromSplit = arguments.split("/from", 2);
        String name = fromSplit[0].trim();
        if (name.isEmpty()) {
            throw new NimbusException("The description of an event cannot be empty.");
        }
        if (fromSplit.length < 2) {
            throw new NimbusException("The event must have a start time.");
        }
        String[] toSplit = fromSplit[1].split("/to", 2);
        if (toSplit.length < 2) {
            throw new NimbusException("The event must have an end time.");
        }
        String from = toSplit[0].trim();
        String to = toSplit[1].trim();
        try {
            return new Event(name, LocalDate.parse(from), LocalDate.parse(to));
        } catch (DateTimeParseException e) {
            throw new NimbusException("The event times must be in the format YYYY-MM-DD.");
        }
    }

    /**
     * Parses the index from the arguments.
     *
     * @param arguments the arguments containing the index.
     * @return the parsed index.
     * @throws NimbusException if the index is invalid.
     */
    public static int parseIndex(String arguments) throws NimbusException {
        try {
            return Integer.parseInt(arguments.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new NimbusException("Please provide a valid task number.");
        }
    }

    /**
     * Parses the arguments from the user input.
     *
     * @param input the user input.
     * @return the arguments part of the input.
     */
    public static String parseArguments(String input) {
        String[] parts = input.trim().split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
