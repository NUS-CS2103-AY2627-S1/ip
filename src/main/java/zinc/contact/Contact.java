package zinc.contact;

import java.util.regex.Pattern;

/**
 * Represents a person's contact details.
 */
public class Contact {
    /** The required layout of a supplied phone number. */
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{8}");

    /** The contact's name. */
    private final String name;

    /** The contact's eight-digit phone number, or an empty string when omitted. */
    private final String phoneNumber;

    /** Additional information about the contact, or an empty string when omitted. */
    private final String description;

    /**
     * Creates a contact with the supplied details.
     *
     * @param name The contact's non-blank name.
     * @param phoneNumber The contact's phone number, or an empty string.
     * @param description The contact's description, or an empty string.
     */
    public Contact(String name, String phoneNumber, String description) {
        assert name != null && phoneNumber != null && description != null
                : "Contact fields must not be null";
        assert !name.isBlank() : "Contact name must not be blank";
        assert phoneNumber.isEmpty() || isValidPhoneNumber(phoneNumber)
                : "Contact number must be empty or contain eight digits";
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    /**
     * Returns the contact's name.
     *
     * @return The contact's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the contact's phone number.
     *
     * @return The phone number, or an empty string when none was supplied.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the contact's description.
     *
     * @return The description, or an empty string when none was supplied.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Creates a copy containing the supplied replacement values.
     *
     * @param updatedName The replacement name.
     * @param updatedPhoneNumber The replacement phone number.
     * @param updatedDescription The replacement description.
     * @return A contact containing the updated details.
     */
    public Contact withUpdatedDetails(String updatedName, String updatedPhoneNumber, String updatedDescription) {
        return new Contact(updatedName, updatedPhoneNumber, updatedDescription);
    }

    /** Returns whether a phone number contains exactly eight digits. */
    static boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    /** Returns a user-facing representation of this contact. */
    @Override
    public String toString() {
        String displayedPhoneNumber = phoneNumber.isEmpty() ? "-" : phoneNumber;
        String displayedDescription = description.isEmpty() ? "-" : description;
        return "Name: " + name + "\n"
                + "Contact number: " + displayedPhoneNumber + "\n"
                + "Description: " + displayedDescription + "\n";
    }
}
