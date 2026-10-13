package ruby.contact;

/**
 * Represents one contact and the details Ruby stores about them.
 */
public class Contact {
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final String address;

    /**
     * Creates a contact with the given details.
     *
     * @param name        Name of the contact.
     * @param phoneNumber Phone number of the contact, or an empty string.
     * @param email       Email of the contact, or an empty string.
     * @param address     Address of the contact, or an empty string.
     */
    public Contact(String name, String phoneNumber, String email, String address) {
        assert name != null : "Contact name cannot be null";
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        String response = name;
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            response += " | " + phoneNumber;
        }
        if (email != null && !email.isEmpty()) {
            response += " | " + email;
        }
        if (address != null && !address.isEmpty()) {
            response += " | " + address;
        }
        return response;
    }

    /**
     * Returns this contact as one line of the saved data file.
     *
     * @return The type, name, phone, email, and address separated by
     *         {@code " | "}; absent optional fields are empty.
     */
    public String toDataString() {
        return ContactDataFormat.CONTACT_TYPE
                + ContactDataFormat.PART_SEPARATOR
                + name
                + ContactDataFormat.PART_SEPARATOR
                + phoneNumber
                + ContactDataFormat.PART_SEPARATOR
                + email
                + ContactDataFormat.PART_SEPARATOR
                + address;
    }

    /**
     * Returns whether this contact has the same stored details as another contact.
     *
     * @param other Contact to compare with.
     * @return Whether every contact field matches.
     */
    public boolean hasSameDetails(Contact other) {
        return other != null && name.equals(other.name) && phoneNumber.equals(other.phoneNumber)
                && email.equals(other.email) && address.equals(other.address);
    }
}
