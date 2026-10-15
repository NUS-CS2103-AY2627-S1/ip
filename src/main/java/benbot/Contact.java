package benbot;

import java.util.Arrays;

/** Stores a contact's name, phone number, and email address. */
final class Contact implements StorableEntity {
    /** The syntax accepted when adding a contact. */
    static final String USAGE = "contact NAME /phone PHONE /email EMAIL";

    /** The contact's name. */
    private final String name;

    /** The contact's phone number. */
    private final String phone;

    /** The contact's email address. */
    private final String email;

    /** Creates a contact with the supplied details. */
    Contact(String name, String phone, String email) {
        assert name != null && !name.isBlank() : "A contact name must be provided";
        assert phone != null && !phone.isBlank() : "A contact phone number must be provided";
        assert email != null && !email.isBlank() : "A contact email address must be provided";
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    /** Creates a contact from a valid contact command. */
    static Contact createFromCommand(String[] words) throws InvalidCommandException {
        int phoneIndex = CommandWords.findMarker(words, "/phone");
        int emailIndex = CommandWords.findMarker(words, "/email");
        boolean isNameMissing = phoneIndex <= 1;
        boolean isPhoneInvalid = emailIndex != phoneIndex + 2;
        boolean isEmailInvalid = emailIndex != words.length - 2;
        if (isNameMissing || isPhoneInvalid || isEmailInvalid) {
            throw new InvalidCommandException("Use: " + USAGE);
        }

        String name = String.join(" ", Arrays.copyOfRange(words, 1, phoneIndex));
        return new Contact(name, words[phoneIndex + 1], words[emailIndex + 1]);
    }

    /** Returns the contact in BenBot's display format. */
    @Override
    public String toString() {
        return "[C] " + name + " (phone: " + phone + "; email: " + email + ")";
    }

    /** Returns the command used to recreate this contact from stored data. */
    @Override
    public String toStorageString() {
        return "contact " + name + " /phone " + phone + " /email " + email;
    }
}
