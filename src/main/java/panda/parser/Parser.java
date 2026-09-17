package panda.parser;

import panda.command.Command;
import panda.exception.parser.NoCommandFoundException;
import panda.exception.parser.ParseException;
import panda.parser.commandparser.AddDeadlineCommandParser;
import panda.parser.commandparser.AddEventCommandParser;
import panda.parser.commandparser.AddTodoCommandParser;
import panda.parser.commandparser.ByeCommandParser;
import panda.parser.commandparser.CommandParser;
import panda.parser.commandparser.DeleteTaskCommandParser;
import panda.parser.commandparser.DisplayDateCommandParser;
import panda.parser.commandparser.FindCommandParser;
import panda.parser.commandparser.ListTasksCommandParser;
import panda.parser.commandparser.MarkTaskCommandParser;
import panda.parser.commandparser.SaveCommandParser;
import panda.parser.commandparser.TodayCommandParser;
import panda.parser.commandparser.UnmarkTaskCommandParser;

/**
 * Converts user-entered text into commands Panda can execute.
 */
public final class Parser {

    private Parser() {
        // Utility class: prevent instantiation.
    }

    /**
     * Maps supported command keywords to parsers that validate their arguments.
     */
    private enum CommandType {
        BYE("bye", new ByeCommandParser()),
        SAVE("save", new SaveCommandParser()),
        LIST("list", new ListTasksCommandParser()),
        MARK("mark", new MarkTaskCommandParser()),
        UNMARK("unmark", new UnmarkTaskCommandParser()),
        DELETE("delete", new DeleteTaskCommandParser()),
        TODO("todo", new AddTodoCommandParser()),
        DEADLINE("deadline", new AddDeadlineCommandParser()),
        EVENT("event", new AddEventCommandParser()),
        TODAY("today", new TodayCommandParser()),
        DISPLAY("display", new DisplayDateCommandParser()),
        FIND("find", new FindCommandParser());

        private final String keyword;
        private final CommandParser commandParser;

        CommandType(String keyword, CommandParser commandParser) {
            this.keyword = keyword;
            this.commandParser = commandParser;
        }

        /**
         * Returns the argument parser for the supplied command keyword.
         *
         * @param keyword Command keyword entered by the user.
         * @return Matching command parser, or {@code null} if the keyword is not supported.
         */
        static CommandParser getCommandParser(String keyword) {
            for (CommandType command : values()) {
                if (command.keyword.equals(keyword)) {
                    return command.commandParser;
                }
            }
            return null;
        }
    }

    /**
     * Removes surrounding whitespace before matching the input to a command.
     *
     * @param input Text entered by the user.
     * @return Command that corresponds to the input.
     * @throws ParseException If the command is unrecognized or has invalid arguments.
     */
    public static Command parse(String input) throws ParseException {
        return matchCommand(input.strip());
    }

    /**
     * Matches the command keyword, then delegates argument validation to its command parser.
     *
     * @param input User input without surrounding whitespace.
     * @return Command that handles the input.
     * @throws ParseException If no matching command is found or arguments are invalid.
     */
    private static Command matchCommand(String input) throws ParseException {
        if (input.isEmpty()) {
            throw new NoCommandFoundException(input);
        }
        String[] commandParts = input.split("\\s+", 2);
        String command = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1] : "";

        CommandParser commandParser = CommandType.getCommandParser(command);
        if (commandParser == null) {
            throw new NoCommandFoundException(input);
        }
        Command parsedCommand = commandParser.parseArguments(arguments);
        assert parsedCommand != null : "Command parser must return a command or throw an exception";
        return parsedCommand;
    }
}
