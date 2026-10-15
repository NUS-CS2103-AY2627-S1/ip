package laby.contact;

import laby.Parser;

/** Represents a contact's name, phone number, and email address. */
public class Contact {
    private final String name;
    private final String phone;
    private final String email;

    /**
     * Creates a contact with the supplied details.
     *
     * @param name Contact's name.
     * @param phone Contact's phone number.
     * @param email Contact's email address.
     */
    public Contact(String name, String phone, String email) {
        assert Parser.isValidInput(name) : "contact name cannot be empty or contain separators";
        assert Parser.isValidInput(phone) : "contact phone cannot be empty or contain separators";
        assert Parser.isValidInput(email) : "contact email cannot be empty or contain separators";

        this.name = name.trim();
        this.phone = phone.trim();
        this.email = email.trim();
    }

    /**
     * Returns the contact's name.
     *
     * @return Contact name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the contact's phone number.
     *
     * @return Contact phone number.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Returns the contact's email address.
     *
     * @return Contact email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the contact in the format shown in the console.
     *
     * @return Display representation of the contact.
     */
    @Override
    public String toString() {
        return this.name + " " + Parser.FIELD_SEPARATOR + " Phone: " + this.phone
                + " " + Parser.FIELD_SEPARATOR + " Email: " + this.email;
    }

    /**
     * Returns the contact in the format used for persistence.
     *
     * @return Serialized contact record without a trailing newline.
     */
    public String toFileString() {
        return String.join(Parser.FIELD_SEPARATOR, "C", this.name, this.phone, this.email);
    }
}
