package atlas.client;

/**
 * A client record: a name, plus an optional phone number and email address.
 * An optional field that is not known is held as an empty string, so a client
 * never carries a null field.
 */
public class Client {

    /** Client's name. */
    private final String name;
    /** Client's phone number, or an empty string when unknown. */
    private final String phone;
    /** Client's email address, or an empty string when unknown. */
    private final String email;

    /**
     * Creates a client record.
     *
     * @param name client's name, which must not be blank.
     * @param phone client's phone number, or an empty string when unknown.
     * @param email client's email address, or an empty string when unknown.
     */
    public Client(String name, String phone, String email) {
        assert name != null && !name.isBlank() : "a client must have a name";
        assert phone != null : "an unknown phone must be an empty string, not null";
        assert email != null : "an unknown email must be an empty string, not null";
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Creates a client record with only a name.
     *
     * @param name client's name.
     */
    public Client(String name) {
        this(name, "", "");
    }

    /**
     * Returns this client's name.
     *
     * @return client's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this client's phone number.
     *
     * @return phone number, or an empty string when unknown.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns this client's email address.
     *
     * @return email address, or an empty string when unknown.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns whether a phone number is known for this client.
     *
     * @return {@code true} when a phone number is stored.
     */
    public boolean hasPhone() {
        return !phone.isEmpty();
    }

    /**
     * Returns whether an email address is known for this client.
     *
     * @return {@code true} when an email address is stored.
     */
    public boolean hasEmail() {
        return !email.isEmpty();
    }

    /**
     * Returns whether any field of this client contains the given keyword.
     * Matching is case-sensitive, mirroring how tasks are searched.
     *
     * @param keyword text to look for.
     * @return {@code true} when the name, phone or email contains the keyword.
     */
    public boolean matches(String keyword) {
        return name.contains(keyword) || phone.contains(keyword) || email.contains(keyword);
    }

    /**
     * Returns the display form used in client listings, for example
     * {@code [C] Bob (phone: 91234567)}. Only known fields are shown.
     *
     * @return displayable representation of this client.
     */
    @Override
    public String toString() {
        StringBuilder display = new StringBuilder("[C] ").append(name);
        if (hasPhone()) {
            display.append(" (phone: ").append(phone).append(')');
        }
        if (hasEmail()) {
            display.append(" (email: ").append(email).append(')');
        }
        return display.toString();
    }
}
