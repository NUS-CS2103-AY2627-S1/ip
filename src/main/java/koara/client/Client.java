package koara.client;

import java.util.Locale;

/**
 * Represents a gym client and the information needed by a trainer.
 */
public class Client {
    private static final String PHONE_PATTERN = "\\+?[0-9]{7,16}";

    private final String name;
    private final String phone;
    private final String goal;
    private final String notes;

    /**
     * Creates a client with contact and coaching information.
     *
     * @param name Client's name.
     * @param phone Client's phone number.
     * @param goal Client's fitness goal.
     * @param notes Important coaching notes about the client.
     */
    public Client(String name, String phone, String goal, String notes) {
        assert isPresent(name) : "Client name must not be blank";
        assert isPresent(phone) : "Client phone must not be blank";
        assert isValidPhone(phone) : "Client phone must contain 7–16 digits with an optional +";
        assert isPresent(goal) : "Client goal must not be blank";
        assert isPresent(notes) : "Client notes must not be blank";
        this.name = name;
        this.phone = phone;
        this.goal = goal;
        this.notes = notes;
    }

    /**
     * Returns whether any client field contains the keyword, ignoring case.
     *
     * @param keyword Keyword to search for.
     * @return True when the keyword occurs in any client field.
     */
    public boolean containsKeyword(String keyword) {
        assert isPresent(keyword) : "Client search keyword must not be blank";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return name.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || phone.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || goal.toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || notes.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    /**
     * Returns whether another client has the same name or phone number.
     *
     * @param other Other client to compare.
     * @return True when either identifying field matches.
     */
    public boolean hasSameIdentity(Client other) {
        assert other != null : "Client to compare must not be null";
        return name.equalsIgnoreCase(other.name)
                || normalizePhone(phone).equals(normalizePhone(other.phone));
    }

    /**
     * Returns whether a phone number has an optional leading {@code +} and 7–16 digits.
     * Spaces used to group the digits are ignored.
     *
     * @param phone Phone number to validate.
     * @return True when the phone number uses the supported format.
     */
    public static boolean isValidPhone(String phone) {
        return isPresent(phone) && normalizePhone(phone).matches(PHONE_PATTERN);
    }

    /**
     * Returns this client in the tab-separated storage format.
     *
     * @return Serialized client data.
     */
    public String toDataString() {
        return String.join("\t", name, phone, goal, notes);
    }

    /**
     * Returns the client details in a user-facing format.
     *
     * @return Display representation of the client.
     */
    @Override
    public String toString() {
        return name + " | Phone: " + phone + " | Goal: " + goal + " | Notes: " + notes;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizePhone(String phone) {
        return phone.replace(" ", "");
    }
}
