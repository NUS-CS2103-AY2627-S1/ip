package gary.contact;

import java.util.Locale;

/**
 * Parses contact commands and their arguments.
 */
public class ContactParser {
    private static final String CONTACT_FORMAT =
            "Meow? Use: contact add NAME /phone PHONE /email EMAIL.";

    /**
     * Returns the operation represented by a contact command.
     *
     * @param input Contact command to classify.
     * @return Matching contact command type.
     */
    public ContactCommandType parseCommandType(String input) {
        assert input != null : "Contact input should not be null";

        ContactCommandType commandType = ContactCommandType.from(input);
        if (commandType == ContactCommandType.LIST && !getActionArguments(input).isEmpty()) {
            return ContactCommandType.UNKNOWN;
        }
        return commandType;
    }

    /**
     * Returns the contact described by an add command.
     *
     * @param input Contact add command containing the details.
     * @return Contact parsed from the command.
     * @throws IllegalArgumentException If any required detail is missing.
     */
    public Contact parseContact(String input) throws IllegalArgumentException {
        String details = getActionArguments(input);
        String lowercaseDetails = details.toLowerCase(Locale.ENGLISH);
        int phoneIndex = lowercaseDetails.indexOf("/phone");
        int emailIndex = lowercaseDetails.indexOf("/email");
        if (phoneIndex == -1 || emailIndex == -1 || phoneIndex > emailIndex) {
            throw new IllegalArgumentException(CONTACT_FORMAT);
        }

        String name = details.substring(0, phoneIndex).trim();
        String phone = details.substring(phoneIndex + 6, emailIndex).trim();
        String email = details.substring(emailIndex + 6).trim();
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()
                || name.contains(" | ") || phone.contains(" | ") || email.contains(" | ")) {
            throw new IllegalArgumentException(CONTACT_FORMAT);
        }
        return new Contact(name, phone, email);
    }

    /**
     * Returns a valid one-based contact number from a delete command.
     *
     * @param input Contact delete command containing a number.
     * @param contactCount Number of contacts currently stored.
     * @return Parsed contact number, or -1 when it is invalid.
     */
    public int parseContactNumber(String input, int contactCount) {
        assert contactCount >= 0 : "Contact count should not be negative";

        try {
            int contactNumber = Integer.parseInt(getActionArguments(input));
            return contactNumber >= 1 && contactNumber <= contactCount ? contactNumber : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the non-empty keyword supplied to a contact find command.
     *
     * @param input Contact find command containing the keyword.
     * @return Keyword to match against contact details.
     * @throws IllegalArgumentException If the keyword is empty.
     */
    public String parseKeyword(String input) throws IllegalArgumentException {
        String keyword = getActionArguments(input);
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("Meow? The contact keyword cannot be empty.");
        }
        return keyword;
    }

    private String getActionArguments(String input) {
        assert input != null : "Contact input should not be null";

        String[] commandParts = input.strip().split("\\s+", 3);
        return commandParts.length < 3 ? "" : commandParts[2].trim();
    }
}
