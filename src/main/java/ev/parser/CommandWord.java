package ev.parser;

import java.util.Arrays;
import java.util.stream.Collectors;

import ev.EvException;

/**
 * The first word of a command, and the keyword the user has to type for it.
 *
 * <p>This is the one place that knows the vocabulary of the chatbot, which is also why
 * it can list the keywords back when the user types something unknown.
 */
public enum CommandWord {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    ON("on"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    UPDATE("update"),
    BYE("bye");

    private final String keyword;

    CommandWord(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command word the user typed.
     *
     * @param keyword the first word of the line, already trimmed.
     * @return the matching command word.
     * @throws EvException if no command uses that keyword. The message lists the ones that exist.
     */
    public static CommandWord fromKeyword(String keyword) throws EvException {
        for (CommandWord command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        throw new EvException("No such command: \"" + keyword + "\"",
                "I take: " + listKeywords() + ".");
    }

    /**
     * Returns every keyword the chatbot understands, in the order they are declared.
     *
     * @return the keywords, separated by commas.
     */
    public static String listKeywords() {
        return Arrays.stream(values())
                .map(command -> command.keyword)
                .collect(Collectors.joining(", "));
    }
}
