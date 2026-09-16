package turtley.command;

import java.util.List;

import turtley.exception.TurtleyException;
import turtley.model.TagValidator;

/**
 * Parses tag modifiers and tag arguments shared by tagging commands.
 */
final class TagParser {

    private static final String TAG_MODIFIER = "/tag";

    private TagParser() {
        // Utility class; do not create instances.
    }

    /**
     * Separates an optional trailing tag modifier from an add-command argument.
     *
     * @param input the complete add-command argument.
     * @param format the command's format message.
     * @return the description or structured arguments and parsed tags.
     */
    static ParsedTags parseOptionalModifier(String input, String format) {
        if (input == null) {
            return new ParsedTags("", List.of());
        }

        int modifierIndex = -1;
        int searchStart = 0;
        while (searchStart < input.length()) {
            int candidateIndex = input.indexOf(TAG_MODIFIER, searchStart);
            if (candidateIndex < 0) {
                break;
            }
            boolean startsToken = candidateIndex == 0
                    || Character.isWhitespace(input.charAt(candidateIndex - 1));
            int modifierEnd = candidateIndex + TAG_MODIFIER.length();
            boolean endsToken = modifierEnd == input.length()
                    || Character.isWhitespace(input.charAt(modifierEnd));
            if (startsToken && endsToken) {
                if (modifierIndex >= 0) {
                    throw invalidFormat(format);
                }
                modifierIndex = candidateIndex;
            }
            searchStart = modifierEnd;
        }

        if (modifierIndex < 0) {
            return new ParsedTags(input.trim(), List.of());
        }

        String content = input.substring(0, modifierIndex).trim();
        String tagText = input.substring(modifierIndex + TAG_MODIFIER.length()).trim();
        if (content.isEmpty() || tagText.isEmpty()) {
            throw invalidFormat(format);
        }
        return new ParsedTags(content, parseTags(tagText, format));
    }

    /**
     * Counts occurrences of a modifier as a complete whitespace-delimited token.
     *
     * @param input the command argument.
     * @param modifier the modifier to count.
     * @return the number of complete modifier tokens.
     */
    static int countModifier(String input, String modifier) {
        if (input == null || modifier == null || modifier.isEmpty()) {
            return 0;
        }
        int count = 0;
        int searchStart = 0;
        while (searchStart < input.length()) {
            int candidateIndex = input.indexOf(modifier, searchStart);
            if (candidateIndex < 0) {
                break;
            }
            int modifierEnd = candidateIndex + modifier.length();
            boolean startsToken = candidateIndex == 0
                    || Character.isWhitespace(input.charAt(candidateIndex - 1));
            boolean endsToken = modifierEnd == input.length()
                    || Character.isWhitespace(input.charAt(modifierEnd));
            if (startsToken && endsToken) {
                count++;
            }
            searchStart = modifierEnd;
        }
        return count;
    }

    /**
     * Parses the task number and tags in a tag mutation command.
     *
     * @param input the command argument.
     * @param format the command's format message.
     * @return the task number and parsed tags.
     */
    static IndexedTags parseIndexedTags(String input, String format) {
        if (input == null || input.isBlank()) {
            throw invalidFormat(format);
        }
        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length != 2 || parts[1].isBlank()) {
            throw invalidFormat(format);
        }
        return new IndexedTags(parts[0], parseTags(parts[1], format));
    }

    /**
     * Parses a tag-filter query.
     *
     * @param input the filter argument.
     * @return the validated query.
     */
    static String parseFilterQuery(String input) {
        if (input == null || input.isBlank() || input.trim().indexOf(' ') >= 0
                || input.trim().indexOf('\t') >= 0 || !input.trim().startsWith("#")
                || input.trim().equals("#")) {
            throw new TurtleyException("Invalid format. Use: filter #tag-substring");
        }
        String query = input.trim();
        if (query.codePoints().anyMatch(Character::isWhitespace)) {
            throw new TurtleyException("Invalid format. Use: filter #tag-substring");
        }
        return query;
    }

    /**
     * Parses and validates whitespace-separated tags.
     *
     * @param input the tag text.
     * @param format the command's format message.
     * @return the parsed tags.
     */
    private static List<String> parseTags(String input, String format) {
        if (input.isBlank()) {
            throw invalidFormat(format);
        }
        List<String> tags = List.of(input.trim().split("\\s+"));
        TagValidator.validateTags(tags);
        return tags;
    }

    /**
     * Creates a standard command-format exception.
     *
     * @param format the command's format message.
     * @return the format exception.
     */
    private static TurtleyException invalidFormat(String format) {
        return new TurtleyException(format);
    }

    /**
     * Stores structured command content and its tags.
     *
     * @param content the non-tag command content.
     * @param tags the parsed tags.
     */
    record ParsedTags(String content, List<String> tags) {
    }

    /**
     * Stores a task number and its parsed tags.
     *
     * @param taskNumber the user-entered task number.
     * @param tags the parsed tags.
     */
    record IndexedTags(String taskNumber, List<String> tags) {
    }
}
