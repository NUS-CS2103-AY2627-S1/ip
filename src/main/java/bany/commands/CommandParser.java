package bany.commands;

import java.util.List;
import java.util.Map;

import bany.parsers.InputParser;
import bany.utilities.CommandValidator;
import bany.utilities.Helper;

/** Converts parsed user input into executable command objects. */
public class CommandParser {
    private final CommandValidator validator;

    /**
     * Creates a parser with the validator used by task-creation commands.
     *
     * @param validator command validation rules.
     */
    public CommandParser(CommandValidator validator) {
        this.validator = validator;
    }

    /**
     * Parses complete user input into one command object.
     *
     * @param input complete command entered by the user.
     * @return executable command.
     * @throws IllegalArgumentException if the input is blank or malformed.
     */
    public Command parse(String input) {
        Map<String, String> values = InputParser.parse(input);
        String commandName = values.get("command");

        List<String> tagNames = InputParser.getTagNames(input);

        return switch (commandName) {
            case "TODO", "DEADLINE", "EVENT" ->
                new AddCommand(values, tagNames, validator);
            case "BYE" -> new ExitCommand();
            case "LIST" -> new ListCommand();
            case "MARK", "M" -> new MarkCommand(values);
            case "UNMARK", "UNM" -> new UnmarkCommand(values);
            case "DELETE", "DEL" -> new DeleteCommand(values);
            case "FIND" -> new FindCommand(values);
            case "RESCHEDULE", "RESCHED" -> new RescheduleCommand(values, tagNames, validator);
            default -> new InvalidCommand(Helper.getClosestWordMatch(commandName));
        };
    }
}
