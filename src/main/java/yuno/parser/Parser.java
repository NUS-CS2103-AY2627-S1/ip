package yuno.parser;

import yuno.command.ByeCommand;
import yuno.command.ClearCommand;
import yuno.command.Command;
import yuno.command.DeadlineCommand;
import yuno.command.DeleteCommand;
import yuno.command.EventCommand;
import yuno.command.FindByDateCommand;
import yuno.command.FindByNameCommand;
import yuno.command.HelpCommand;
import yuno.command.ListCommand;
import yuno.command.MarkCommand;
import yuno.command.SortCommand;
import yuno.command.TodoCommand;
import yuno.command.UnmarkCommand;
import yuno.exception.UnknownCommandException;

/**
 * Converts user input into commands that the chatbot can execute.
 */
public class Parser {
    /**
     * Creates a parser for converting user input into commands.
     */
    public Parser() {
    }

    /**
     * Represents a command recognized by Yuno.
     */
    private enum CommandType {
        CLEAR("clear"),
        LIST("list"),
        HELP("help"),
        BYE("bye"),

        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event"),

        MARK("mark"),
        UNMARK("unmark"),
        DELETE("delete"),

        FIND("find"),
        SORT("sort");

        /** Command keyword entered by the user. */
        private final String keyword;

        CommandType(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Returns the command type matching the specified command keyword.
         *
         * @param keyword Command keyword entered by the user.
         * @return Command type matching the keyword.
         * @throws UnknownCommandException If the command keyword is not recognized.
         */
        private static CommandType from(String keyword) throws UnknownCommandException {
            for (CommandType commandType : values()) {
                if (commandType.keyword.equals(keyword)) {
                    return commandType;
                }
            }
            throw new UnknownCommandException(
                    "Did you look at what you typed? That's just a random command.");
        }
    }

    /**
     * Parses the specified user input into a command.
     *
     * @param input User input to parse.
     * @return Command that represents the parsed input.
     * @throws UnknownCommandException If the command keyword is not recognized.
     */
    public Command parse(String input) throws UnknownCommandException {
        String strippedInput = input.strip();
        CommandType commandType = CommandType.from(strippedInput.split(" ")[0].strip());
        int separatorIndex = strippedInput.indexOf(" ");
        String commandArguments = separatorIndex == -1 ? "" : strippedInput.substring(separatorIndex + 1);
        return switch (commandType) {
            case CLEAR -> new ClearCommand(commandArguments);
            case LIST -> new ListCommand(commandArguments);
            case HELP -> new HelpCommand(commandArguments);
            case BYE -> new ByeCommand(commandArguments);
            case TODO -> new TodoCommand(commandArguments);
            case DEADLINE -> new DeadlineCommand(commandArguments);
            case EVENT -> new EventCommand(commandArguments);
            case MARK -> new MarkCommand(commandArguments);
            case UNMARK -> new UnmarkCommand(commandArguments);
            case DELETE -> new DeleteCommand(commandArguments);
            case FIND -> parseFindCommand(commandArguments);
            case SORT -> new SortCommand(commandArguments);
        };
    }

    /**
     * Returns a name or date search command based on the optional {@code /date} modifier.
     *
     * @param commandArguments Search arguments supplied after the {@code find} keyword.
     * @return Command for the requested type of search.
     */
    private Command parseFindCommand(String commandArguments) {
        String strippedArguments = commandArguments.strip();
        if (strippedArguments.equals("/date")) {
            return new FindByDateCommand("");
        } else if (strippedArguments.startsWith("/date ")) {
            String dateText = strippedArguments.substring("/date".length()).strip();
            return new FindByDateCommand(dateText);
        }
        return new FindByNameCommand(commandArguments);
    }
}
