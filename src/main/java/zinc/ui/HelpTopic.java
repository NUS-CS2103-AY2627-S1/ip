package zinc.ui;

import java.util.List;
import java.util.Objects;

/** Describes one operation displayed in Zinc's command reference. */
public final class HelpTopic {
    /** The category containing this operation. */
    private final HelpCategory category;

    /** The short title displayed in the command browser. */
    private final String title;

    /** The canonical command name. */
    private final String command;

    /** A concise explanation of the command's behavior. */
    private final String description;

    /** Supported command forms containing parameters and optional fields. */
    private final List<String> usages;

    /** Alternative command forms. */
    private final List<String> aliases;

    /**
     * Creates an immutable help topic.
     *
     * @param category The category containing the operation.
     * @param title The title displayed in the command browser.
     * @param command The canonical command name.
     * @param description A concise explanation of the command's behavior.
     * @param usages Supported command forms.
     * @param aliases Alternative command forms.
     */
    public HelpTopic(HelpCategory category, String title, String command, String description,
            List<String> usages, List<String> aliases) {
        this.category = Objects.requireNonNull(category);
        this.title = Objects.requireNonNull(title);
        this.command = Objects.requireNonNull(command);
        this.description = Objects.requireNonNull(description);
        this.usages = List.copyOf(usages);
        this.aliases = List.copyOf(aliases);
    }

    /** Returns the category containing this operation. */
    public HelpCategory getCategory() {
        return category;
    }

    /** Returns the title displayed in the command browser. */
    public String getTitle() {
        return title;
    }

    /** Returns the canonical command name. */
    public String getCommand() {
        return command;
    }

    /** Returns a concise explanation of the command's behavior. */
    public String getDescription() {
        return description;
    }

    /** Returns an immutable list of supported command forms. */
    public List<String> getUsages() {
        return usages;
    }

    /** Returns an immutable list of alternative command forms. */
    public List<String> getAliases() {
        return aliases;
    }
}
