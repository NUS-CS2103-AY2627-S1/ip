package bany.tags;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents one task tag.
 *
 * <p>A tag may either have a value, such as {@code /priority high}, or be a
 * flag without a value, such as {@code /urgent}.</p>
 *
 * @param name tag name without the leading slash.
 * @param value optional tag value.
 */
public record Tag(String name, Optional<String> value) {

    /**
     * Creates a validated tag and normalizes its name for case-insensitive
     * lookup and duplicate detection.
     *
     * @param name tag name without the leading slash.
     * @param value optional tag value.
     */
    public Tag {
        Objects.requireNonNull(name, "Tag name cannot be null.");
        Objects.requireNonNull(value, "Tag value cannot be null.");

        String normalisedName = name.trim().toLowerCase(Locale.ROOT);
        if (normalisedName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be blank.");
        }

        name = normalisedName;
        value = value.map(String::trim).filter(currentValue -> !currentValue.isBlank());
    }

    /**
     * Creates a flag tag without a value.
     *
     * @param name tag name without the leading slash.
     */
    public Tag(String name) {
        this(name, Optional.empty());
    }

    /**
     * Creates a tag from a possibly blank value. A blank value is treated as
     * an absent value, which supports tags that do not require a body.
     *
     * @param name tag name without the leading slash.
     * @param value tag value, or {@code null} for a flag tag.
     */
    public Tag(String name, String value) {
        this(name, value == null || value.isBlank()
                ? Optional.empty()
                : Optional.of(value));
    }
}
