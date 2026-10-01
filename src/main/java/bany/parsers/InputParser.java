package bany.parsers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Parses chatbot commands into named values.
 *
 * <p>The parser expects input in this form:
 * {@code <command> <description> /<tag> <tag description> ...}.
 * The returned map contains lowercase keys. The value for the
 * {@code command} key is uppercase, while descriptions and tag values
 * retain the user's original casing. Tag names must not contain
 * {@code command} or {@code description}, ignoring case, so tags cannot
 * overwrite the command fields.</p>
 */
public class InputParser {
    /** Creates an input parser. */
    public InputParser() {
    }

    /**
     * Splits a command into its command name, description, and tagged values.
     *
     * <p>The first word is always treated as the command. Everything after
     * that word and before the first slash is treated as the description.
     * The first slash and each later slash start a new tag. Each tag value
     * continues until the next slash or the end of input.
     * If a tag is repeated, the last value replaces the earlier value in the
     * returned map. Use {@link #getTagNames(String)} when repeated tags must
     * be detected.</p>
     *
     * @param input complete command entered by the user.
     * @return an insertion-ordered map of keys to values.
     * @throws IllegalArgumentException if {@code input} is null or blank, or a tag name
     *     is empty or contains {@code command} or {@code description}, ignoring case.
     */
    public static Map<String, String> parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or blank");
        }

        String trimmedInput = input.trim();
        int commandEnd = firstWhitespaceIndex(trimmedInput);
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (commandEnd == -1) {
            result.put("command", trimmedInput.toUpperCase(Locale.ROOT));
            result.put("description", "");
            return result;
        }

        result.put("command", trimmedInput.substring(0, commandEnd)
                .toUpperCase(Locale.ROOT));
        String arguments = trimmedInput.substring(commandEnd).trim();
        int firstTag = findTagStart(arguments, 0);

        if (firstTag == -1) {
            result.put("description", arguments);
            return result;
        }

        result.put("description", arguments.substring(0, firstTag).trim());
        parseTags(arguments, firstTag, result);
        return result;
    }

    /**
     * Returns the tag names in the order in which they appear in the input.
     * Repeated tag names are retained in the returned list. The same tag-name
     * restrictions as {@link #parse(String)} apply.
     *
     * @param input complete command entered by the user.
     * @return lowercase tag names in input order.
     * @throws IllegalArgumentException if {@code input} is null or blank, or a tag name
     *     is empty or contains {@code command} or {@code description}, ignoring case.
     */
    public static List<String> getTagNames(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or blank");
        }

        String trimmedInput = input.trim();
        int commandEnd = firstWhitespaceIndex(trimmedInput);
        if (commandEnd == -1) {
            return List.of();
        }

        String arguments = trimmedInput.substring(commandEnd).trim();
        int tagStart = findTagStart(arguments, 0);
        if (tagStart == -1) {
            return List.of();
        }

        List<String> tagNames = new ArrayList<>();
        for (ParsedTag tag : parseTagEntries(arguments, tagStart)) {
            tagNames.add(tag.name());
        }
        return tagNames;
    }

    /**
     * Adds slash-prefixed tag values to the parsed command map.
     *
     * @param arguments command arguments containing tags.
     * @param firstTag index of the first tag slash.
     * @param result map to which parsed tags are added.
     */
    private static void parseTags(String arguments, int firstTag,
            Map<String, String> result) {
        for (ParsedTag tag : parseTagEntries(arguments, firstTag)) {
            result.put(tag.name(), tag.value());
        }
    }

    /** Parses tags for both public methods, rejecting names that contain reserved command fields. */
    private static List<ParsedTag> parseTagEntries(String arguments, int firstTag) {
        List<ParsedTag> tags = new ArrayList<>();
        int tagStart = firstTag;
        while (tagStart < arguments.length()) {
            int tagNameStart = tagStart + 1;
            int tagNameEnd = tagNameStart;

            while (tagNameEnd < arguments.length()
                    && !Character.isWhitespace(arguments.charAt(tagNameEnd))
                    && arguments.charAt(tagNameEnd) != '/') {
                tagNameEnd++;
            }

            if (tagNameEnd == tagNameStart) {
                throw new IllegalArgumentException("Tag name cannot be empty");
            }

            String tagName = arguments.substring(tagNameStart, tagNameEnd)
                    .toLowerCase(Locale.ROOT);
            if (tagName.contains("command") || tagName.contains("description")) {
                throw new IllegalArgumentException(
                        "Tag names must not contain command or description.");
            }
            int valueStart = tagNameEnd;
            while (valueStart < arguments.length()
                    && Character.isWhitespace(arguments.charAt(valueStart))) {
                valueStart++;
            }

            int nextTag = findTagStart(arguments, valueStart);
            int valueEnd = nextTag == -1 ? arguments.length() : nextTag;
            tags.add(new ParsedTag(tagName, arguments.substring(valueStart, valueEnd).trim()));

            if (nextTag == -1) {
                return tags;
            }
            tagStart = nextTag;
        }
        return tags;
    }

    /**
     * Finds the first whitespace character in a command string.
     *
     * @param input command text to inspect.
     * @return index of the first whitespace character, or {@code -1} when none exists.
     */
    private static int firstWhitespaceIndex(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isWhitespace(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the next slash that begins a tag.
     *
     * @param input command text to inspect.
     * @param fromIndex index from which to begin searching.
     * @return index of the next slash, or {@code -1} when none exists.
     */
    private static int findTagStart(String input, int fromIndex) {
        return input.indexOf('/', fromIndex);
    }

    /** Stores the normalized name and value of one parsed tag. */
    private record ParsedTag(String name, String value) {
    }

}
