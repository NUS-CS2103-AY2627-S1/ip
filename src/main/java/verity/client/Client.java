package verity.client;

import java.util.Locale;

import verity.exception.VerityException;
import verity.storage.StorageTextCodec;

/**
 * Represents one client whose contact information can be managed.
 */
public class Client {
    private static final int NAME_MAX_LENGTH = 100;
    private static final int PHONE_MIN_LENGTH = 3;
    private static final int PHONE_MAX_LENGTH = 32;
    private static final int EMAIL_MIN_LENGTH = 3;
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int ADDRESS_MAX_LENGTH = 200;
    private static final int COMPANY_MAX_LENGTH = 100;
    private static final int NOTES_MAX_LENGTH = 2000;

    private final int numericId;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String address;
    private final String company;
    private final String notes;
    private final PreferredContactMethod preferredContactMethod;

    /**
     * Creates a client with validated contact information.
     *
     * @param numericId Positive numeric portion of the immutable client ID.
     * @param fullName Client's full name.
     * @param phone Client's phone number, or an empty string.
     * @param email Client's email address, or an empty string.
     * @param address Client's address, or an empty string.
     * @param company Client's company, or an empty string.
     * @param notes Client notes, or an empty string.
     * @param preferredContactMethod Preferred contact method, or null.
     * @throws VerityException If any field is invalid.
     */
    public Client(int numericId, String fullName, String phone, String email,
            String address, String company, String notes,
            PreferredContactMethod preferredContactMethod) throws VerityException {
        if (numericId <= 0) {
            throw new VerityException("client ID must be positive.");
        }

        validateName(fullName);
        validatePhone(phone);
        validateEmail(email);
        validateSingleLineField(address, ADDRESS_MAX_LENGTH, "Address");
        validateSingleLineField(company, COMPANY_MAX_LENGTH, "Company");
        validateNotes(notes);

        this.numericId = numericId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.notes = notes;
        this.preferredContactMethod = preferredContactMethod;
    }

    /**
     * Returns the canonical client ID.
     *
     * @return Client ID such as {@code C001}.
     */
    public String getId() {
        return formatId(numericId);
    }

    public int getNumericId() {
        return numericId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCompany() {
        return company;
    }

    public String getNotes() {
        return notes;
    }

    public PreferredContactMethod getPreferredContactMethod() {
        return preferredContactMethod;
    }

    /**
     * Returns whether the client's name contains a keyword.
     *
     * @param keyword Search keyword.
     * @return True if the name contains the keyword, ignoring case.
     */
    public boolean matchesName(String keyword) {
        return fullName.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this client in the tab-separated storage format.
     *
     * @return Serialized client record.
     */
    public String serialize() {
        return String.join("\t",
                "C",
                getId(),
                StorageTextCodec.encode(fullName),
                StorageTextCodec.encode(phone),
                StorageTextCodec.encode(email),
                StorageTextCodec.encode(address),
                StorageTextCodec.encode(company),
                StorageTextCodec.encode(notes),
                preferredContactMethod == null
                        ? ""
                        : preferredContactMethod.getValue());
    }

    /**
     * Formats a positive numeric client ID.
     *
     * @param numericId Numeric client ID.
     * @return Canonical ID with a {@code C} prefix.
     */
    public static String formatId(int numericId) {
        return "C" + String.format(Locale.ROOT, "%03d", numericId);
    }

    /**
     * Parses a canonical client ID.
     *
     * @param clientId Client ID text.
     * @return Positive numeric client ID.
     * @throws VerityException If the ID is invalid.
     */
    public static int parseId(String clientId) throws VerityException {
        if (!clientId.matches("(?i)C\\d{3,}")) {
            throw new VerityException("Client IDs must use the format C001.");
        }

        try {
            int numericId = Integer.parseInt(clientId.substring(1));
            if (numericId <= 0) {
                throw new VerityException("Client IDs must use the format C001.");
            }
            return numericId;
        } catch (NumberFormatException exception) {
            throw new VerityException("Client IDs must use the format C001.");
        }
    }

    static String normalizePhone(String phone) {
        return phone.replaceAll("[\\s().-]", "");
    }

    static String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    private static void validateName(String fullName) throws VerityException {
        validateBoundaryWhitespace(fullName, "Client name");
        if (fullName.isEmpty()) {
            throw new VerityException("A client name is required.");
        }
        if (fullName.length() > NAME_MAX_LENGTH) {
            throw new VerityException("Client names cannot exceed 100 characters.");
        }
        boolean isValid = fullName.codePoints()
                .allMatch(character -> Character.isLetter(character) || character == ' ');
        if (!isValid) {
            throw new VerityException("Client names may contain letters and spaces only.");
        }
    }

    private static void validatePhone(String phone) throws VerityException {
        validateBoundaryWhitespace(phone, "Phone");
        if (phone.isEmpty()) {
            return;
        }
        if (phone.length() < PHONE_MIN_LENGTH || phone.length() > PHONE_MAX_LENGTH) {
            throw new VerityException("Phone numbers must contain 3 to 32 characters.");
        }
        if (!phone.matches("[0-9+(). -]+")
                || phone.chars().filter(Character::isDigit).count() < 3) {
            throw new VerityException("The phone number format is invalid.");
        }
    }

    private static void validateEmail(String email) throws VerityException {
        validateBoundaryWhitespace(email, "Email");
        if (email.isEmpty()) {
            return;
        }
        if (email.length() < EMAIL_MIN_LENGTH || email.length() > EMAIL_MAX_LENGTH
                || email.chars().anyMatch(Character::isWhitespace)) {
            throw new VerityException("The email address format is invalid.");
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')
                || atIndex == email.length() - 1) {
            throw new VerityException("The email address format is invalid.");
        }
    }

    private static void validateSingleLineField(String value, int maxLength,
            String fieldName) throws VerityException {
        validateBoundaryWhitespace(value, fieldName);
        if (value.length() > maxLength) {
            throw new VerityException(
                    fieldName + " cannot exceed " + maxLength + " characters.");
        }
        if (value.indexOf('\t') >= 0 || value.indexOf('\n') >= 0
                || value.indexOf('\r') >= 0) {
            throw new VerityException(fieldName + " cannot contain tabs or newlines.");
        }
    }

    private static void validateNotes(String notes) throws VerityException {
        validateBoundaryWhitespace(notes, "Notes");
        if (notes.length() > NOTES_MAX_LENGTH) {
            throw new VerityException("Notes cannot exceed 2000 characters.");
        }
        if (notes.indexOf('\t') >= 0) {
            throw new VerityException("Notes cannot contain tabs.");
        }
    }

    private static void validateBoundaryWhitespace(String value, String fieldName)
            throws VerityException {
        if (!value.isEmpty() && !value.equals(value.strip())) {
            throw new VerityException(
                    fieldName + " cannot start or end with whitespace.");
        }
    }
}
