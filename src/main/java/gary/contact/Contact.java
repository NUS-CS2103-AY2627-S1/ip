package gary.contact;

import java.util.Locale;

/**
 * Represents a person and their contact details.
 */
public class Contact {
    private final String email;
    private final String name;
    private final String phone;

    /**
     * Creates a contact with a name, phone number, and email address.
     *
     * @param name Name of the contact.
     * @param phone Phone number of the contact.
     * @param email Email address of the contact.
     */
    public Contact(String name, String phone, String email) {
        assert name != null && !name.isBlank() : "Contact name should not be blank";
        assert phone != null && !phone.isBlank() : "Contact phone should not be blank";
        assert email != null && !email.isBlank() : "Contact email should not be blank";

        this.name = name;
        this.phone = phone;
        this.email = email;
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

    /**
     * Returns whether any contact detail contains the keyword, ignoring case.
     *
     * @param keyword Keyword to find in the contact details.
     * @return {@code true} if a contact detail contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Contact keyword should not be blank";

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return name.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || phone.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || email.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    @Override
    public String toString() {
        return name + " (Phone: " + phone + ", Email: " + email + ")";
    }
}
