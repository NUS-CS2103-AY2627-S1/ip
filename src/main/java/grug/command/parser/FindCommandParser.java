package grug.command.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import grug.command.FindTasksByDateCommand;
import grug.command.FindTasksByDetailsCommand;
import grug.task.Task;

/**
 * Contains methods to parse the {@link FindTasksByDateCommand} and
 * {@link FindTasksByDetailsCommand}.
 * FindCommandParser
 */
public class FindCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link FindTasksByDateCommand} command instance.
     * @throws GrugCommandParserException if something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static FindTasksByDateCommand parseFindByDateCommand(String[] args)
            throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // command without any arguments passed
        if (args.length == 1) {
            String usage = "find-on <%s>".formatted(Task.DATE_TIME_INPUT_PATTERN);
            throw new GrugCommandParserException.InvalidUsage(usage);
        }

        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of());
        List<String> datetimeTokens = parsedArgs.inputs();
        String target = String.join(" ", datetimeTokens);

        try {
            LocalDate targetDateTime = LocalDate.parse(target, Task.DATE_TIME_INPUT_FORMATTER);
            return new FindTasksByDateCommand(targetDateTime);
        } catch (DateTimeParseException e) {
            String reason = "must be a valid date in the format %s\n(but the time provided is ignored)"
                    .formatted(Task.DATE_TIME_INPUT_PATTERN);
            throw new GrugCommandParserException.InvalidArgument("find-on", reason);
        }
    }

    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link FindTasksByDetailsCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static FindTasksByDetailsCommand parseFindByDetailsCommand(String[] args)
            throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of());
        if (parsedArgs.inputs().isEmpty()) {
            throw new GrugCommandParserException.InvalidUsage("find <details>");
        }
        String details = String.join(" ", parsedArgs.inputs());

        return new FindTasksByDetailsCommand(details);
    }
}
