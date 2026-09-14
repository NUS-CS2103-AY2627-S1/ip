package arthur.commands;

import java.util.Map;

import arthur.exceptions.InvalidArgumentException;
import arthur.messages.AliasMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;

/**
 * Adds, removes, and lists user-defined command aliases.
 */
public class AliasCommand extends Command {
    /*
        AI-assisted:
        * Added command-alias creation and removal without alias resolution.
        * Added display of stored command aliases without alias resolution.
     */
    /**
     * Adds, removes, or lists aliases in the shared command context.
     *
     * @param context the shared command state.
     * @param arg     the alias operation and its arguments.
     * @return a confirmation message for the completed operation.
     */
    @Override
    public Message handle(CommandContext context, String arg) {
        String[] arguments = arg.split(" ", 2);
        String operation = arguments[0];
        String options = arguments.length > 1 ? arguments[1] : UNNAMED_ARGUMENT;

        return switch (operation) {
            case "add" -> addAlias(context, options);
            case "remove" -> removeAlias(context, options);
            case "list" -> listAliases(context);
            default -> throw new InvalidArgumentException(operation);
        };
    }

    // AI-assisted: Parsed and stored an alias-to-command mapping.
    /**
     * Stores a new alias and its target command.
     *
     * @param context the shared command state.
     * @param options the {@code /from} and {@code /to} arguments.
     * @return a confirmation message for the new alias.
     */
    private Message addAlias(CommandContext context, String options) {
        Map<String, String> args = parseArgs(options, "/from", "/to");
        String alias = args.get("/from").trim();
        String command = args.get("/to").trim();
        context.commandAliases().put(alias, command);
        return new AliasMessage(alias, command);
    }

    // AI-assisted: Parsed and removed an alias mapping.
    /**
     * Removes an alias from the shared command context.
     *
     * @param context the shared command state.
     * @param options the {@code /name} argument.
     * @return a confirmation message for the removed alias.
     */
    private Message removeAlias(CommandContext context, String options) {
        Map<String, String> args = parseArgs(options, "/name");
        String alias = args.get("/name").trim();
        String removedAlias = context.commandAliases().remove(alias);
        if (removedAlias == null) {
            throw new InvalidArgumentException("Alias not found: " + alias);
        }
        return new AliasMessage(alias);
    }

    // AI-assisted: Added display of aliases stored in the command context.
    /**
     * Displays the aliases stored in the shared command context.
     *
     * @param context the shared command state.
     * @return a message containing the stored aliases.
     */
    private Message listAliases(CommandContext context) {
        return new AliasMessage(context.commandAliases());
    }
}
