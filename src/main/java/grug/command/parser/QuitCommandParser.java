package grug.command.parser;

import grug.command.QuitCommand;

/**
 * Contains parsing methods to parse the {@link QuitCommand}.
 */
final class QuitCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link QuitCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static QuitCommand parseQuitCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        // only `bye` is valid, with no additional args
        if (args.length > 1) {
            throw new GrugCommandParserException.InvalidUsage("bye (with no arguments)");
        }
        return new QuitCommand();
    }
}
