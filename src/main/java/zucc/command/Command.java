package zucc.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Represents a parsed user command that validates itself before changing application state.
 * Callers create commands through {@link #parse(String)} and then interact with them solely
 * through {@link #execute(TaskList, Ui, Storage)} and {@link #isExit()}.
 */
public abstract class Command {
    /** Error shown when a task number is missing or malformed. */
    private static final String INVALID_TASK_NUMBER_ERROR =
            "Zucc couldn't find that task in the records. Use list to check its number.";

    /** User-entered keyword represented by this command. */
    private final String keyword;

    /** Named options accepted by this command. */
    private final Set<String> allowedOptions;

    /** Main text between the command keyword and its first named option. */
    private String argument = "";

    /** Raw option values indexed by separators such as {@code /by}. */
    private final Map<String, String> options = new HashMap<>();

    /**
     * Creates a command before Parser supplies its raw values.
     *
     * @param keyword user-entered keyword represented by this command.
     * @param allowedOptions named options accepted by this command.
     */
    protected Command(String keyword, String... allowedOptions) {
        this.keyword = keyword;
        this.allowedOptions = Set.of(allowedOptions);
    }

    /**
     * Parses one complete input line into the appropriate concrete command.
     * This is the public creation boundary, so callers cannot observe a command
     * while Parser is still populating it.
     *
     * @param input complete line entered by the user.
     * @return completely parsed command.
     * @throws ZuccException if the keyword is unknown or an option is unsupported
     *         or duplicated.
     */
    public static Command parse(String input) throws ZuccException {
        return Parser.parse(input);
    }

    /**
     * Supplies the command's free-form main argument during parsing.
     *
     * @param argument parsed main argument.
     */
    final void setArgument(String argument) {
        this.argument = argument;
    }

    /**
     * Supplies a named option during parsing after rejecting duplicate or unsupported names.
     *
     * @param name option separator, including its leading slash.
     * @param value raw option value.
     * @throws ZuccException if the option is duplicated or unsupported.
     */
    final void addOption(String name, String value) throws ZuccException {
        if (options.containsKey(name)) {
            throw new ZuccException("Zucc can't use " + name
                    + " more than once in one command.");
        }
        if (!allowedOptions.contains(name)) {
            throw new ZuccException("Zucc doesn't recognize option " + name
                    + " for the " + keyword + " command.");
        }
        options.put(name, value);
    }

    /**
     * Returns a required option value after ensuring it contains data.
     *
     * @param optionName required option separator.
     * @return supplied option value.
     * @throws ZuccException if the option was absent or blank.
     */
    protected final String require(String optionName) throws ZuccException {
        // Subclasses must declare every option they require so the parser can accept it.
        assert optionName != null && allowedOptions.contains(optionName)
                : "Required option must be declared by the command: " + optionName;
        return requireValue(getOption(optionName), optionName);
    }

    /**
     * Returns a named value supplied to this command.
     *
     * @param optionName option separator.
     * @return supplied value, or {@code null} when the option was omitted.
     */
    protected final String getOption(String optionName) {
        return options.get(optionName);
    }

    /**
     * Returns the main argument after ensuring it contains data.
     *
     * @param label user-facing description of the required argument.
     * @return supplied main argument.
     * @throws ZuccException if the argument was absent or blank.
     */
    protected final String requireArgument(String label) throws ZuccException {
        return requireValue(argument, label);
    }

    /**
     * Validates and returns one required piece of command data.
     *
     * @param value supplied value, or {@code null} when absent.
     * @param label user-facing name of the required data.
     * @return nonblank supplied value.
     * @throws ZuccException if the value is absent or blank.
     */
    private String requireValue(String value, String label) throws ZuccException {
        if (value == null || value.isBlank()) {
            throw new ZuccException("Zucc needs more data: the "
                    + keyword + " command requires " + label + ".");
        }
        return value;
    }

    /**
     * Ensures that a command that takes no positional argument did not receive one.
     * Unsupported options are rejected separately while Parser populates the command.
     *
     * @throws ZuccException if the user supplied a positional argument.
     */
    protected final void requireNoArgument() throws ZuccException {
        if (!argument.isEmpty()) {
            throw new ZuccException("Zucc doesn't expect additional data for the "
                    + keyword + " command.");
        }
    }

    /**
     * Converts this command's one-based task number to a zero-based list index.
     * Bounds validation remains in {@link TaskList}, which knows the current size.
     *
     * @return corresponding zero-based list index.
     * @throws ZuccException if the argument is absent or is not an integer.
     */
    protected final int parseTaskIndex() throws ZuccException {
        String taskNumber = requireArgument("a task number");
        try {
            return Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException ignored) {
            // Malformed input and unavailable task numbers use the same response.
        }
        throw new ZuccException(INVALID_TASK_NUMBER_ERROR);
    }

    /**
     * Performs this command's behavior using the application's collaborators.
     *
     * @param tasks task collection for the current session.
     * @param ui user interface through which responses are shown.
     * @param storage persistent storage for task changes.
     * @throws ZuccException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws ZuccException;

    /**
     * Reports whether the application should stop after executing this command.
     *
     * @return {@code true} only for a command that ends the session.
     */
    public boolean isExit() {
        return false;
    }
}
