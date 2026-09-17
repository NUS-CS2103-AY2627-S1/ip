package notmarth.model;

/** Represents a person whose contact details are stored by NotMarth. */
public final class Contact {
    private final String name;
    private final String phone;
    private final String address;

    /**
     * Creates a contact.
     *
     * @param name the contact's name.
     * @param phone the contact's phone number.
     * @param address the contact's address.
     */
    public Contact(String name, String phone, String address) {
        if (name == null || name.isBlank() || phone == null || phone.isBlank()
                || address == null || address.isBlank()) {
            throw new IllegalArgumentException("Contact fields must not be blank.");
        }
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    /** @return the contact's name */
    public String getName() {
        return name;
    }

    /** @return the contact's phone number */
    public String getPhone() {
        return phone;
    }

    /** @return the contact's address */
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return name + " (phone: " + phone + ", address: " + address + ")";
    }
}
