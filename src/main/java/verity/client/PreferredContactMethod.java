package verity.client;

import java.util.Locale;

import verity.exception.VerityException;

/**
 * Represents a client's preferred contact method.
 */
public enum PreferredContactMethod {
    PHONE,
    EMAIL,
    OTHER;

    /**
     * Parses a preferred contact method.
     *
     * @param value Contact method text.
     * @return Parsed contact method, or null for an empty value.
     * @throws VerityException If the value is not supported.
     */
    public static PreferredContactMethod parse(String value) throws VerityException {
        if (value.isEmpty()) {
            return null;
        }

        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new VerityException(
                    "Preferred contact must be phone, email, or other.");
        }
    }

    /**
     * Returns the lowercase storage and display value.
     *
     * @return Lowercase contact method.
     */
    public String getValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
