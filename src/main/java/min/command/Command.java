package min.command;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a command recognized by Min.
 */
public enum Command {
    HELP("help", false),
    BYE("bye", false),
    LIST("list", false),
    LISTTASKS("listtasks", false),
    LISTNOTES("listnotes", false),
    FIND("find", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    NOTE("note", true),
    DELETENOTE("deletenote", true);

    private final String word;
    private final boolean canHaveArguments;

    Command(String word, boolean canHaveArguments) {
        this.word = word;
        this.canHaveArguments = canHaveArguments;
    }

    /**
     * Returns every command word, separated by commas.
     *
     * <p>Deriving the list here keeps messages that list the commands from going
     * stale when a command is added to this enum.
     */
    public static String getAllWords() {
        return Arrays.stream(values())
                .map(Command::getWord)
                .collect(Collectors.joining(", "));
    }

    /** Returns the word used to enter this command. */
    public String getWord() {
        return this.word;
    }

    /**
     * Checks whether the input begins with this command's valid command word.
     *
     * @param input The command entered by the user.
     * @return Whether the input matches this command.
     */
    public boolean matches(String input) {
        if (!this.canHaveArguments) {
            return input.equals(this.word);
        }
        return input.equals(this.word) || input.startsWith(this.word + " ");
    }
}
