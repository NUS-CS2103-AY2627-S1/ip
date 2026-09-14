package arthur.messages;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Confirms that a command alias has changed or displays stored aliases.
 */
public class AliasMessage extends Message {
    /*
        AI-assisted:
        * Added confirmation formats for alias additions and removals.
        * Added sorted display formatting for stored aliases.
     */
    private static final String ADD_CONFIRMATION_FORMAT = "%s\n\nalias added:\n%s -> %s";
    private static final String REMOVE_CONFIRMATION_FORMAT = "%s\n\nalias removed:\n%s";
    private static final String LIST_FORMAT = "%s\n\naliases:\n%s";
    private static final String NO_ALIASES = "No aliases to display.";
    private static final String ALIAS_FORMAT = "%s -> %s";

    private final String alias;
    private final String targetCommand;
    private final Map<String, String> aliases;

    /**
     * Creates a message confirming an added alias.
     *
     * @param alias         the alias name.
     * @param targetCommand the command mapped to the alias.
     */
    public AliasMessage(String alias, String targetCommand) {
        this.alias = alias;
        this.targetCommand = targetCommand;
        this.aliases = null;
    }

    /**
     * Creates a message confirming a removed alias.
     *
     * @param alias the alias name.
     */
    public AliasMessage(String alias) {
        this(alias, null);
    }

    /**
     * Creates a message displaying a snapshot of stored aliases.
     *
     * @param aliases the aliases to display.
     */
    public AliasMessage(Map<String, String> aliases) {
        this.alias = null;
        this.targetCommand = null;
        this.aliases = Map.copyOf(aliases);
    }

    @Override
    public String toString() {
        if (aliases != null) {
            return String.format(LIST_FORMAT, getRandomQuote(), formatAliases());
        }
        if (targetCommand == null) {
            return String.format(REMOVE_CONFIRMATION_FORMAT, getRandomQuote(), alias);
        }
        return String.format(ADD_CONFIRMATION_FORMAT, getRandomQuote(), alias, targetCommand);
    }

    // AI-assisted: Sorted aliases by name to make list output predictable.
    /**
     * Formats stored aliases in a stable alphabetical order.
     *
     * @return the formatted aliases or an empty-list message.
     */
    private String formatAliases() {
        if (aliases.isEmpty()) {
            return NO_ALIASES;
        }
        return aliases.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format(ALIAS_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
