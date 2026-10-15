package ff15.contact;

/**
 * Someone the user wants to keep details of, e.g. a friend.
 *
 * <p>Only the name is required. A phone number and an email address may each be
 * given or left out, and a field that was left out is never shown back to the
 * user rather than being displayed as blank.
 */
public class Contact {
    private final String name;
    private final String phone;
    private final String email;

    /**
     * Creates a contact. {@code phone} and {@code email} may each be empty,
     * meaning the user did not give one.
     *
     * @param name what the user calls this person.
     * @param phone their phone number, or empty if not given.
     * @param email their email address, or empty if not given.
     */
    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone == null ? "" : phone;
        this.email = email == null ? "" : email;
    }

    /** Returns the name the user gave this contact. */
    public String getName() {
        return name;
    }

    /** Returns the phone number, or an empty string if none was given. */
    public String getPhone() {
        return phone;
    }

    /** Returns the email address, or an empty string if none was given. */
    public String getEmail() {
        return email;
    }

    /**
     * Returns whether this contact's name contains {@code keyword}, ignoring the
     * difference between upper and lower case so that {@code contact find john}
     * still turns up a contact named {@code John}. Only the name is searched, so
     * a phone number or an email address is never matched.
     *
     * @param keyword the text the user is searching for.
     * @return true if the name contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether {@code other} is the same contact: the same name, phone and
     * email, exactly. Two people may share a name, so a name alone is not enough;
     * only a contact identical in every field is a repeat of this one.
     */
    public boolean isSameAs(Contact other) {
        return name.equals(other.name) && phone.equals(other.phone) && email.equals(other.email);
    }

    /**
     * Returns this contact as shown to the user, e.g.
     * {@code John (phone: 91234567, email: john@example.com)}. Fields the user
     * did not give are left out altogether, so a contact with only a name shows
     * as just that name.
     */
    @Override
    public String toString() {
        StringBuilder details = new StringBuilder();
        if (!phone.isEmpty()) {
            details.append("phone: ").append(phone);
        }
        if (!email.isEmpty()) {
            if (details.length() > 0) {
                details.append(", ");
            }
            details.append("email: ").append(email);
        }
        return details.length() == 0 ? name : name + " (" + details + ")";
    }

    /**
     * Returns this contact's representation for the save file, e.g.
     * {@code "John | 91234567 | john@example.com"}. All three fields are always
     * written, so a field that was left out is saved as an empty one.
     */
    public String toFileFormat() {
        return name + " | " + phone + " | " + email;
    }
}
