package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a regex-based command matcher.
 */
public class Command {
    private final String regex;
    private final Pattern pattern;

    /**
     * Creates an instance of a command matcher.
     *
     * @param regex a regular expression that matches the command format
     */
    public Command(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    /**
     * Checks whether the text matches the command from the given start index.
     *
     * @param start the index where the search begins
     * @param text the text to be searched
     * @return true if the command is present in the text; false otherwise
     */
    public boolean find(int start, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find(start);
    }
}
