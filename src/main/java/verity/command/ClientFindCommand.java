package verity.command;

/**
 * Represents a command that finds clients by name.
 */
public class ClientFindCommand extends Command {
    private final String keyword;

    /**
     * Creates a client-name search command.
     *
     * @param keyword Name keyword to search for.
     */
    public ClientFindCommand(String keyword) {
        super(false);
        this.keyword = keyword;
    }

    @Override
    public String execute(CommandContext context) {
        return context.getUi().getMatchingClientsMessage(
                context.getClients().findByName(keyword));
    }
}
