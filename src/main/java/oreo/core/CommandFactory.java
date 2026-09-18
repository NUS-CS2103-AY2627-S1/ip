package oreo.core;

import oreo.command.Command;
import oreo.command.DeadlineCommand;
import oreo.command.DeleteCommand;
import oreo.command.DeleteMerchandiseCommand;
import oreo.command.EditMerchandiseCommand;
import oreo.command.EmptyCommand;
import oreo.command.EventCommand;
import oreo.command.ExitCommand;
import oreo.command.FindCommand;
import oreo.command.FindMerchandiseCommand;
import oreo.command.HelpCommand;
import oreo.command.ListAllCommand;
import oreo.command.ListCommand;
import oreo.command.MarkCommand;
import oreo.command.MerchandiseCommand;
import oreo.command.OnDateCommand;
import oreo.command.TodoCommand;
import oreo.command.UnknownCommand;
import oreo.command.UnmarkCommand;

/** Creates executable commands from parsed command types and input. */
public class CommandFactory {
    /** Builds the command represented by the supplied input and command type.
     *
     * @param type the parsed command type
     * @param input the complete user input
     * @param parser the parser used by the command
     * @return the executable command
     * @throws OreoException if the command arguments are invalid
     */
    public Command create(CommandType type, String input, Parser parser) throws OreoException {
        assert type != null : "Command creation requires a classified command type.";
        assert input != null && parser != null : "Command creation requires input and a parser.";
        return switch (type) {
            case BYE -> new ExitCommand(parser);
            case HELP -> new HelpCommand(parser);
            case LIST -> new ListCommand(parser);
            case FIND -> new FindCommand(parser.argument(input, "find"), parser);
            case MARK -> new MarkCommand(parser.argument(input, "mark"), parser);
            case UNMARK -> new UnmarkCommand(parser.argument(input, "unmark"), parser);
            case DELETE -> new DeleteCommand(parser.argument(input, "delete"), parser);
            case DEADLINE -> new DeadlineCommand(parser.argument(input, "deadline"), parser);
            case EVENT -> new EventCommand(parser.argument(input, "event"), parser);
            case TODO -> new TodoCommand(parser.argument(input, "todo"), parser);
            case ON_DATE -> new OnDateCommand(parser.argument(input, "on"), parser);
            case MERCHANDISE -> new MerchandiseCommand(parser.argument(input, "merchandise"), parser);
            case EDIT_MERCHANDISE -> new EditMerchandiseCommand(
                    parser.argument(input, "edit-merchandise"), parser);
            case DELETE_MERCHANDISE -> new DeleteMerchandiseCommand(
                    parser.argument(input, "delete-merchandise"), parser);
            case FIND_MERCHANDISE -> new FindMerchandiseCommand(
                    parser.argument(input, "find-merchandise"), parser);
            case LIST_ALL -> new ListAllCommand(parser);
            case EMPTY -> new EmptyCommand(parser);
            case UNKNOWN -> new UnknownCommand(parser);
        };
    }
}
