package tianyi.command;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import tianyi.TianyiException;

/**
 * Represents a supported command type with its keyword, description, and example usage.
 */
public enum CommandType {
    /**
     * Ends the application session.
     */
    BYE(
            "bye",
            "bye",
            "Ends the application session."
    ),
    /**
     * Lists all tasks or dated tasks occurring on a specified date.
     */
    LIST(
            "list",
            "list 2-12-2019",
            "Lists all tasks or tasks occurring on a specified date."
    ),
    /**
     * Adds a todo task.
     */
    TODO(
            "todo",
            "todo borrow book",
            "Adds a todo task."
    ),
    /**
     * Adds a task with a deadline.
     */
    DEADLINE(
            "deadline",
            "deadline return book /by 2-12-2019 18:00",
            "Adds a task with a deadline."
    ),
    /**
     * Adds an event with a start and end.
     */
    EVENT(
            "event",
            "event meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00",
            "Adds an event with a start and end."
    ),
    /**
     * Marks a task as completed.
     */
    MARK(
            "mark",
            "mark 1",
            "Marks a task as completed."
    ),
    /**
     * Marks a task as incomplete.
     */
    UNMARK(
            "unmark",
            "unmark 1",
            "Marks a task as incomplete."
    ),
    /**
     * Removes a task from the task list.
     */
    DELETE(
            "delete",
            "delete 1",
            "Removes a task from the task list."
    ),
    /**
     * Finds tasks whose descriptions contain a keyword.
     */
    FIND(
            "find",
            "find book",
            "Finds tasks whose descriptions contain a keyword."
    ),
    /**
     * Displays the supported commands and their descriptions.
     */
    HELP(
            "help",
            "help",
            "Displays this list of supported commands."
    );

    private final String keyword;
    private final String example;
    private final String description;

    CommandType(String keyword, String example, String description) {
        this.keyword = keyword;
        this.example = example;
        this.description = description;
    }

    /**
     * Resolves a command keyword without regard to letter case.
     *
     * @param keyword Command keyword entered by the user.
     * @return Command type matching the keyword.
     * @throws TianyiException If the keyword is not supported.
     */
    public static CommandType from(String keyword)
            throws TianyiException {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        for (CommandType type : values()) {
            if (type.keyword.equals(normalizedKeyword)) {
                return type;
            }
        }

        throw new TianyiException("I'm sorry, but I don't know what that means.");
    }

    /**
     * Formats all supported commands with their descriptions and examples.
     *
     * @return Help text for all supported commands.
     */
    public static String getCommands() {
        return Arrays.stream(values())
                .map(CommandType::formatHelp)
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Returns an example showing valid usage of this command type.
     *
     * @return Example command input.
     */
    public String getExample() {
        return example;
    }

    /**
     * Formats the keyword for display in user-facing messages.
     *
     * @return Command keyword enclosed in square brackets.
     */
    @Override
    public String toString() {
        return "[" + keyword + "]";
    }

    /**
     * Formats this command type for display in the help response.
     *
     * @return Command keyword, description, and example.
     */
    private String formatHelp() {
        return this + " " + description + "\n"
                + "Example: " + example;
    }
}
