package littler.task.attribute;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the set of tags attached to a task, handling tag normalization
 * and the storage/display formatting of the whole set.
 */
public final class TagSet {
    public static final String TAG_PREFIX = "#";
    private static final String STORAGE_DELIMITER = ",";
    private static final String STORAGE_PREFIX = "TAGS:";

    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Gets the first tag in this set, if any. The order of tags is preserved from insertion order.
     *
     * @return an Optional containing the first tag, or empty if the set is empty
     */
    public Optional<String> getFirstTag() {
        return tags.stream().findFirst();
    }

    /**
     * Adds a tag. A leading "#" is stripped if present, and the tag is stored in
     * lowercase, so "fun" and "#Fun" are treated as the same tag.
     *
     * @param tag the tag to add
     */
    public void add(String tag) {
        tags.add(normalize(tag));
    }

    /**
     * Removes a tag, if present.
     *
     * @param tag the tag to remove
     */
    public void remove(String tag) {
        tags.remove(normalize(tag));
    }

    /**
     * Copies all tags from another TagSet into this one.
     *
     * @param other the TagSet to copy tags from
     */
    public void addAll(TagSet other) {
        tags.addAll(other.tags);
    }

    /**
     * Returns true if the given string is a storage-format tag field.
     *
     * @param field the string to check
     * @return true if the string starts with "TAGS:", false otherwise
     */
    public static boolean isTagField(String field) {
        return field.startsWith(STORAGE_PREFIX);
    }

    /**
     * Returns this set's tags as a read-only view.
     *
     * @return an unmodifiable set of tags
     */
    public Set<String> asUnmodifiableSet() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Builds the storage-format representation of this tag set.
     *
     * @return " | tag1,tag2" if any tags are present, or "" if the set is empty
     */
    public String toStorageString() {
        return tags.isEmpty() ? "" : " | " + STORAGE_PREFIX + String.join(STORAGE_DELIMITER, tags);
    }

    /**
     * Builds the display-format representation of this tag set.
     *
     * @return " #tag1 #tag2" if any tags are present, or "" if the set is empty
     */
    public String toDisplayString() {
        return tags.isEmpty() ? "" : " " + tags.stream()
            .map(tag -> TAG_PREFIX + tag)
            .collect(Collectors.joining(" "));
    }

    /**
     * Parses a comma-separated storage string into a new TagSet.
     *
     * @param field the storage-format string to parse
     * @return the reconstructed TagSet
     */
    public static TagSet fromStorageString(String field) {
        TagSet result = new TagSet();
        for (String tag : field.substring(STORAGE_PREFIX.length()).split(STORAGE_DELIMITER)) {
            result.add(tag);
        }
        return result;
    }

    private static String normalize(String tag) {
        String trimmed = tag.trim();
        String withoutPrefix = trimmed.startsWith(TAG_PREFIX) ? trimmed.substring(TAG_PREFIX.length()) : trimmed;
        return withoutPrefix.toLowerCase();
    }
}
