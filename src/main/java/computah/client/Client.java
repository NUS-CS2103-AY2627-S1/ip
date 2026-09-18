package computah.client;

import computah.exception.ComputahException;

/**
 * Represents a client whose contact information is managed by Computah.
 */
public class Client {
    private static final String FILE_DELIMITER = " | ";
    private static final int MAXIMUM_NAME_LENGTH = 100;
    private static final int MAXIMUM_PHONE_LENGTH = 30;
    private static final int MAXIMUM_EMAIL_LENGTH = 254;
    private static final String PHONE_PATTERN = "\\+?[0-9][0-9 ()-]*";
    private static final String EMAIL_PATTERN = "[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+";

    private String name;
    private String phone;
    private String email;

    /**
     * Creates a client with a required name and optional contact details.
     *
     * @param name client's name.
     * @param phone client's phone number, or an empty string if absent.
     * @param email client's email address, or an empty string if absent.
     * @throws ComputahException if a field cannot be represented safely.
     */
    public Client(String name, String phone, String email) throws ComputahException {
        update(name, phone, email);
    }

    /**
     * Updates all supplied fields after validating them together.
     *
     * @param name new name, or null if unchanged.
     * @param phone new phone number, or null if unchanged.
     * @param email new email address, or null if unchanged.
     * @throws ComputahException if a supplied value is invalid.
     */
    public void update(String name, String phone, String email) throws ComputahException {
        if (name != null) {
            validateField(name);
            if (name.isBlank()) {
                throw new ComputahException("The name of a client cannot be empty.");
            }
            if (name.length() > MAXIMUM_NAME_LENGTH) {
                throw new ComputahException("The name of a client cannot exceed "
                        + MAXIMUM_NAME_LENGTH + " characters.");
            }
            if (name.codePoints().noneMatch(Character::isLetterOrDigit)) {
                throw new ComputahException("The name of a client must contain a letter or number.");
            }
        }
        if (phone != null) {
            validateField(phone);
            validatePhone(phone.trim());
        }
        if (email != null) {
            validateField(email);
            validateEmail(email.trim());
        }

        if (name != null) {
            this.name = name.trim();
        }
        if (phone != null) {
            this.phone = phone.trim();
        }
        if (email != null) {
            this.email = email.trim();
        }
    }

    /**
     * Converts this client into one line for the save file.
     *
     * @return save-file representation of this client.
     */
    public String toFileString() {
        return "C" + FILE_DELIMITER + name + FILE_DELIMITER + phone + FILE_DELIMITER + email;
    }

    /**
     * Returns the compact display representation of this client.
     *
     * @return client name, phone number, and email address.
     */
    @Override
    public String toString() {
        return name + " [phone: " + valueOrPlaceholder(phone) + "] [email: "
                + valueOrPlaceholder(email) + "]";
    }

    /**
     * Returns a stored value or the placeholder used for an absent value.
     *
     * @param value stored field value.
     * @return stored value, or {@code -} when it is empty.
     */
    private static String valueOrPlaceholder(String value) {
        return value.isEmpty() ? "-" : value;
    }

    /**
     * Checks that a client field can be represented safely in one save-file line.
     *
     * @param value client field value.
     * @throws ComputahException if the value contains a reserved delimiter or line break.
     */
    private static void validateField(String value) throws ComputahException {
        if (value.contains(FILE_DELIMITER)) {
            throw new ComputahException("Client details cannot contain \" | \".");
        }
        if (value.contains("\n") || value.contains("\r")) {
            throw new ComputahException("Client details cannot contain line breaks.");
        }
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new ComputahException("Client details cannot contain control characters.");
        }
    }

    /**
     * Checks that a supplied phone number contains only conventional phone characters.
     *
     * @param phone trimmed phone number, or an empty string when absent.
     * @throws ComputahException if the phone number is too long or malformed.
     */
    private static void validatePhone(String phone) throws ComputahException {
        if (phone.length() > MAXIMUM_PHONE_LENGTH) {
            throw new ComputahException("The phone of a client cannot exceed "
                    + MAXIMUM_PHONE_LENGTH + " characters.");
        }
        if (!phone.isEmpty() && !phone.matches(PHONE_PATTERN)) {
            throw new ComputahException("The phone of a client may contain only digits, spaces, +, -, "
                    + "and parentheses.");
        }
    }

    /**
     * Checks that a supplied email address has a basic local-part and domain structure.
     *
     * @param email trimmed email address, or an empty string when absent.
     * @throws ComputahException if the email address is too long or malformed.
     */
    private static void validateEmail(String email) throws ComputahException {
        if (email.length() > MAXIMUM_EMAIL_LENGTH) {
            throw new ComputahException("The email of a client cannot exceed "
                    + MAXIMUM_EMAIL_LENGTH + " characters.");
        }
        if (!email.isEmpty() && !email.matches(EMAIL_PATTERN)) {
            throw new ComputahException("The email of a client must be in the format name@example.com.");
        }
    }
}
