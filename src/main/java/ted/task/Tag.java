package ted.task;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A label the user attaches to a task, e.g. {@code #fun}, so that related
 * tasks can be picked out of the list together.
 * <p>
 * A tag is nothing more than its name, so it is written as a record: Java
 * generates {@code equals} and {@code hashCode} from the name, which is what
 * lets a task keep its tags in a {@link java.util.Set} without repeats. The
 * name is stored in lower case, so {@code #Fun} and {@code #fun} are the same
 * tag.
 *
 * @param name the tag's name without the leading {@code #}, in lower case.
 */
public record Tag(String name) {
    /** Marks a word as a tag, both when the user types it and when Ted shows it. */
    public static final String PREFIX = "#";

    /**
     * What a tag name may contain. Letters and digits only, so that a tag can
     * never contain the spaces or {@code |} characters the save file relies on.
     */
    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9]+");

    /**
     * Creates a tag with the given name, converted to lower case.
     *
     * @throws IllegalArgumentException if the name is empty or contains
     *                                  anything other than letters and digits.
     */
    public Tag {
        if (!VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("A tag name must be letters and digits only: " + name);
        }
        name = name.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns whether the text is a tag as the user writes it: {@code #}
     * followed by letters and digits, e.g. {@code #fun} or {@code #CS2103}.
     *
     * @param text a single word, e.g. from the user's input or the save file.
     * @return {@code true} if {@link #fromText(String)} accepts the text.
     */
    public static boolean isValidText(String text) {
        return text.startsWith(PREFIX) && VALID_NAME.matcher(text.substring(PREFIX.length())).matches();
    }

    /**
     * Creates the tag written as the given text, e.g. {@code #Fun} gives {@code #fun}.
     *
     * @param text a tag as the user writes it, starting with {@code #}.
     * @return the tag the text stands for.
     * @throws IllegalArgumentException if {@link #isValidText(String)} rejects the text.
     */
    public static Tag fromText(String text) {
        if (!isValidText(text)) {
            throw new IllegalArgumentException("Not a tag: " + text);
        }
        return new Tag(text.substring(PREFIX.length()));
    }

    /**
     * Returns the tag as the user sees it, e.g. {@code #fun}.
     */
    @Override
    public String toString() {
        return PREFIX + name;
    }
}
