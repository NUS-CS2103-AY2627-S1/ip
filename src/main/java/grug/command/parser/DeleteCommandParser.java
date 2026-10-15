package grug.command.parser;

import grug.command.DeleteTaskCommand;

/**
 * Contains parsing methods to parse the {@link DeleteTaskCommand}.
 */
final class DeleteCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link DeleteTaskCommand} command instance.
     * @throws GrugCommandParserException if something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static DeleteTaskCommand parseDeleteCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // only `delete <tasknum>` is valid
        if (args.length != 2) {
            throw new GrugCommandParserException.InvalidUsage("delete <tasknum>");
        }

        String taskNumText = args[1];
        int taskNum = CommandParser.parseInt(taskNumText, "delete <tasknum>", "tasknum must be an integer");
        return new DeleteTaskCommand(taskNum);
    }
}
