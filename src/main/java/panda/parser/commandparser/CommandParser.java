package panda.parser.commandparser;

import panda.command.Command;
import panda.exception.parser.InvalidArgumentException;

/**
 * Converts the arguments for one command keyword into an executable command.
 */
public interface CommandParser {

    /**
     * Validates command arguments and creates the corresponding command.
     *
     * @param arguments Text following the command keyword.
     * @return Command ready to execute.
     * @throws InvalidArgumentException If the arguments do not match the command's usage.
     */
    Command parseArguments(String arguments) throws InvalidArgumentException;
}
