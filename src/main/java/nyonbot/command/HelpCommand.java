package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;

/**
 * Displays concise usage information for NyonBot commands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a help command with parsed arguments.
     *
     * @param arguments parsed command arguments.
     */
    public HelpCommand(HashMap<String, String> arguments) {
        super(arguments);
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        String requestedCommand = arguments.get(DESCRIPTION_KEY);
        if (requestedCommand == null || requestedCommand.isBlank()) {
            return new Result(allCommandSummaries());
        }

        CommandType type = CommandType.toCommandType(requestedCommand.strip());
        if (type == CommandType.UNKNOWN) {
            throw new NyonException("unknown command: " + requestedCommand.strip());
        }
        return new Result(commandSummary(type));
    }

    private String allCommandSummaries() {
        StringBuilder output = new StringBuilder(
                "Available commands (use help <command> for details):");
        for (CommandType type : CommandType.values()) {
            if (type != CommandType.UNKNOWN) {
                output.append(System.lineSeparator()).append(commandSummary(type));
            }
        }
        return output.toString();
    }

    private String commandSummary(CommandType type) {
        return String.format("%s - %s", commandNames(type), commandDescription(type));
    }

    private String commandNames(CommandType type) {
        return String.join(", ", type.keywords());
    }

    private String commandDescription(CommandType type) {
        return switch (type) {
            case EXIT -> "save tasks and close NyonBot";
            case ECHO -> "repeat the supplied text";
            case NYON -> "show a Nyon message";
            case LIST -> "display every task";
            case TODO -> "add a task: todo <description>";
            case DEADLINE -> "add a deadline: deadline <description> --by dd/MM/yyyy HHmm";
            case EVENT -> "add an event: event <description> --from dd/MM/yyyy HHmm "
                    + "--to dd/MM/yyyy HHmm";
            case MARK -> "mark a task complete by number or exact description";
            case UNMARK -> "mark a task incomplete by number or exact description";
            case DELETE -> "delete a task by number or exact description";
            case FIND -> "find tasks containing one or more terms";
            case HELP -> "show this summary or help for one command";
            case ON -> "list deadlines and events occurring on a day: on dd/MM/yyyy";
            case UNKNOWN -> "";
        };
    }
}
