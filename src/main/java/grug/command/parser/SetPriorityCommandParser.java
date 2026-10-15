package grug.command.parser;

import java.util.Set;

import grug.command.SetPriorityCommand;
import grug.task.TaskPriority;

/**
 * Contains parsing methods to parse the {@link SetPriorityCommand}.
 */
final class SetPriorityCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link SetPriorityCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static SetPriorityCommand parseSetPriorityCommand(String[] args)
            throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of("/priority"));

        TaskPriority newPriority = CommandParser.parseTaskPriorityFrom(parsedArgs, null);
        if (parsedArgs.inputs().size() != 1 || newPriority == null) {
            String usage = "set-priority <tasknum> " + CommandParser.getPriorityFlagUsage();
            throw new GrugCommandParserException.InvalidUsage(usage);
        }

        String taskNumText = parsedArgs.inputs().get(0);
        String errorReason = "tasknum %s is not an integer".formatted(taskNumText);
        int taskNum = CommandParser.parseInt(taskNumText, "set-priority <tasknum>", errorReason);

        return new SetPriorityCommand(taskNum, newPriority);
    }
}
