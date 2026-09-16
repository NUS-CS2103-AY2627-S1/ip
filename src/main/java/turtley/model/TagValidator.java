package turtley.model;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import turtley.exception.TurtleyException;

/**
 * Validates tags accepted by Turtley.
 */
public final class TagValidator {

    /** The maximum number of tags supported by one task. */
    public static final int MAX_TAG_COUNT = 10;
    private static final int MAX_TAG_LENGTH = 10;
    private static final String INVALID_TAG_MESSAGE =
            "Invalid tag. Tags must start with # and contain 1-10 non-whitespace characters.";
    private static final Set<String> RESERVED_TAGS = Set.of(
            "todo", "deadline", "event", "list", "find", "filter", "tag", "untag", "mark", "unmark",
            "delete", "timecheck", "help", "bye", "by", "from", "to");

    private TagValidator() {
        // Utility class; do not create instances.
    }

    /**
     * Validates one user-entered tag.
     *
     * @param tag the tag to validate.
     * @throws TurtleyException if the tag is malformed or reserved.
     */
    public static void validateTag(String tag) {
        String tagName = tag == null ? "" : tag.substring(Math.min(tag.length(), 1));
        if (tag == null || !tag.startsWith("#") || tag.length() == 1
                || tagName.codePoints().anyMatch(Character::isWhitespace)
                || tagName.codePointCount(0, tagName.length()) > MAX_TAG_LENGTH) {
            throw new TurtleyException(INVALID_TAG_MESSAGE);
        }
        if (RESERVED_TAGS.contains(tagName.toLowerCase(Locale.ROOT))) {
            throw new TurtleyException("Invalid tag. Tag name is reserved.");
        }
    }

    /**
     * Validates a collection of tags.
     *
     * @param tags the tags to validate.
     * @throws TurtleyException if the collection is null or contains an invalid tag.
     */
    public static void validateTags(Collection<String> tags) {
        if (tags == null) {
            throw new TurtleyException(INVALID_TAG_MESSAGE);
        }
        for (String tag : tags) {
            validateTag(tag);
        }
    }
}
