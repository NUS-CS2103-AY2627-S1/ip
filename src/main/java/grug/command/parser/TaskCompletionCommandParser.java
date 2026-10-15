package grug.command.parser;

import grug.command.MarkTaskCommand;
import grug.command.UnmarkTaskCommand;

/**
 * Contains methods to parse the {@link MarkTaskCommand} and
 * {@link UnmarkTaskCommand}.
 */
public class TaskCompletionCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link UnmarkTaskCommand} command instance.
     * @throws GrugCommandParserException if something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static UnmarkTaskCommand parseUnmarkCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // only `unmark <tasknum>` is valid
        if (args.length != 2) {
            String reason = "must provide exactly 1 integer tasknum";
            throw new GrugCommandParserException.InvalidArgument("unmark <tasknum>", reason);
        }

        String taskNumText = args[1];
        int taskNum = CommandParser.parseInt(taskNumText, "unmark <tasknum>", "tasknum must be an integer");
        return new UnmarkTaskCommand(taskNum);
    }

    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link MarkTaskCommand} command instance.
     * @throws GrugCommandParserException if something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static MarkTaskCommand parseMarkCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // only `mark <tasknum>` is valid
        if (args.length != 2) {
            throw new GrugCommandParserException.InvalidUsage("mark <tasknum>");
        }

        String taskNumText = args[1];
        int taskNum = CommandParser.parseInt(taskNumText, "mark <tasknum>", "tasknum must be an integer");
        return new MarkTaskCommand(taskNum);
    }
}
