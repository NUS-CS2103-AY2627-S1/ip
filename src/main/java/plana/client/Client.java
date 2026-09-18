package plana.client;

import java.util.Locale;

import plana.storage.Storage;

/**
 * Represents a client record managed by Plana.
 */
public class Client {
    private final String name;
    private final String email;
    private final String phone;
    private final String address;
    private final String preferences;
    private final String notes;

    /**
     * Creates a client with the supplied contact information.
     *
     * @param name the client's name.
     * @param email the client's normalized unique email address.
     * @param phone the client's phone number, or an empty string.
     * @param address the client's address, or an empty string.
     * @param preferences the client's baking-product preferences, or an empty string.
     * @param notes additional notes about the client, or an empty string.
     */
    public Client(String name, String email, String phone, String address,
                  String preferences, String notes) {
        assert name != null && !name.isBlank() : "A client must have a name.";
        assert email != null && !email.isBlank() : "A client must have an email.";
        this.name = name.trim();
        this.email = email.trim().toLowerCase(Locale.ROOT);
        this.phone = normalizeOptional(phone);
        this.address = normalizeOptional(address);
        this.preferences = normalizeOptional(preferences);
        this.notes = normalizeOptional(notes);
    }

    /**
     * Returns the client's name.
     *
     * @return the client name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the client's unique email address.
     *
     * @return the normalized email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the client's phone number.
     *
     * @return the phone number, or an empty string when it was not provided.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the client's address.
     *
     * @return the address, or an empty string when it was not provided.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the client's baking-product preferences.
     *
     * @return the preferences, or an empty string when they were not provided.
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * Returns the client's notes.
     *
     * @return the notes, or an empty string when they were not provided.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Checks whether any client field contains a keyword.
     *
     * @param keyword the keyword to search for.
     * @return true when any client field contains the keyword, ignoring case.
     */
    public boolean matchesKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return name.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || email.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || phone.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || address.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || preferences.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || notes.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    /**
     * Returns a compact summary suitable for client lists and confirmations.
     *
     * @return the client's name and email address.
     */
    public String getSummary() {
        return name + " <" + email + ">";
    }

    /**
     * Returns the client in the format used by the client storage file.
     *
     * @return the escaped client storage record.
     */
    public String toStorageString() {
        return "C | " + Storage.escapeField(email) + " | " + Storage.escapeField(name)
                + " | " + Storage.escapeField(phone) + " | " + Storage.escapeField(address)
                + " | " + Storage.escapeField(preferences) + " | " + Storage.escapeField(notes);
    }

    private static String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }
}
