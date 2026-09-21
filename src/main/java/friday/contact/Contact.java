package friday.contact;

import java.util.Locale;

/**
 * Stores immutable contact details after trimming and validating user-supplied values.
 * Empty phone or email strings represent omitted details; at least one must be supplied.
 */
public final class Contact {
    public static final String INVALID_FORMAT_MESSAGE = "Invalid contact add format. Use: contact add NAME "
            + "/phone PHONE [/email EMAIL] or contact add NAME /email EMAIL.";
    private final String name;
    private final String phone;
    private final String email;

    /**
     * Creates a contact, validating structure before phone and email syntax.
     *
     * @throws IllegalArgumentException if any required detail is missing or invalid.
     */
    public Contact(String name, String phone, String email) {
        if (name == null || phone == null || email == null
                || hasControlCharacters(name) || hasControlCharacters(phone) || hasControlCharacters(email)) {
            throw new IllegalArgumentException(INVALID_FORMAT_MESSAGE);
        }
        this.name = name.strip();
        this.phone = phone.strip();
        this.email = email.strip();
        if (this.name.isBlank() || (this.phone.isEmpty() && this.email.isEmpty())) {
            throw new IllegalArgumentException(INVALID_FORMAT_MESSAGE);
        }
        if (!this.phone.isEmpty() && !this.phone.matches("[689][0-9]{7}")) {
            throw new IllegalArgumentException("Invalid phone number. Use 8 digits starting with 6, 8, or 9.");
        }
        if (!this.email.isEmpty() && !this.email.matches("[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+")) {
            throw new IllegalArgumentException("Invalid email address. Use a format like alice@example.com.");
        }
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    /** Returns a locale-independent key for enforcing case-insensitive name uniqueness. */
    public String getNameKey() {
        return name.toLowerCase(Locale.ROOT);
    }

    /** Checks for a literal, case-insensitive substring within any individual contact field. */
    public boolean contains(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return name.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || phone.contains(normalizedKeyword)
                || email.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    @Override
    public String toString() {
        return name + " (phone: " + (phone.isEmpty() ? "-" : phone)
                + ", email: " + (email.isEmpty() ? "-" : email) + ")";
    }

    /** Prevents stored fields from injecting extra lines or terminal control characters. */
    private static boolean hasControlCharacters(String value) {
        return value.codePoints().anyMatch(Character::isISOControl);
    }
}
