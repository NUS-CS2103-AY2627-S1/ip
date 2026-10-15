package grug.command.parser;

import grug.command.ListTasksCommand;

/**
 * Contains parsing methods to parse the {@link ListTasksCommand}.
 */
final class ListCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link ListTasksCommand} command instance.
     * @throws GrugCommandParserException if something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static ListTasksCommand parseListCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // only `list` is valid, with no additional args
        if (args.length > 1) {
            throw new GrugCommandParserException.InvalidUsage("list (with no arguments)");
        }
        return new ListTasksCommand();
    }
}
