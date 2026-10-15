package zinc.contact;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zinc.ui.Ui;

/**
 * Parses and executes contact subcommands.
 */
public class ContactCommandHandler {
    /** The maximum number of characters accepted in a contact name. */
    private static final int MAX_NAME_LENGTH = 100;

    /** The maximum number of characters accepted in a contact description. */
    private static final int MAX_DESCRIPTION_LENGTH = 300;

    /** The field key identifying a contact name. */
    private static final String NAME_FIELD = "n";

    /** The field key identifying a contact phone number. */
    private static final String PHONE_NUMBER_FIELD = "p";

    /** The field key identifying a contact description. */
    private static final String DESCRIPTION_FIELD = "d";

    /** Identifies contact field prefixes in a command. */
    private static final Pattern CONTACT_FIELD_PATTERN = Pattern.compile("(?:^|\\s)/([a-z][a-z0-9]*)(?=\\s|$)",
            Pattern.CASE_INSENSITIVE);

    /** The contact list affected by commands. */
    private final ContactList contactList;

    /** The UI used to display validation messages. */
    private final Ui ui;

    /** Contact subcommands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /**
     * Creates a handler for commands that affect the supplied contact list.
     *
     * @param contactList The contact list to update.
     * @param ui The UI used to display validation messages.
     */
    public ContactCommandHandler(ContactList contactList, Ui ui) {
        assert contactList != null && ui != null : "Contact command dependencies must not be null";
        this.contactList = contactList;
        this.ui = ui;
        this.commands = createCommands();
    }

    /**
     * Parses and executes a contact subcommand.
     *
     * @param parameters The subcommand and its arguments.
     */
    public void execute(String parameters) {
        assert parameters != null : "Contact command input must not be null";
        String[] commandParts = parameters.split("\\s+", 2);
        String subcommand = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length > 1 ? commandParts[1].strip() : "";

        Consumer<String> selectedCommand = commands.get(subcommand);
        if (selectedCommand == null) {
            ui.printContactUsage();
            return;
        }
        selectedCommand.accept(arguments);
    }

    /** Creates the command registry used to dispatch contact operations. */
    private Map<String, Consumer<String>> createCommands() {
        return Map.of(
                "add", this::addContact,
                "del", this::deleteContact,
                "delete", this::deleteContact,
                "update", this::updateContact,
                "list", this::listContacts,
                "ls", this::listContacts);
    }

    /**
     * Lists every contact or only contacts with a supplied name.
     *
     * @param keyword The keyword to search for among contact names. Leave blank to print the entire list.
     * */
    private void listContacts(String keyword) {
        if (keyword.isEmpty()) {
            contactList.printContacts();
            return;
        }
        if (!isValidNameLength(keyword)) {
            return;
        }
        contactList.printContactsContaining(keyword);
    }

    /** Validates contact fields and adds a contact. */
    private void addContact(String arguments) {
        Map<String, String> contactFields = parseContactFields(arguments, 0);
        if (contactFields == null || !contactFields.containsKey(NAME_FIELD)
                || contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }

        String name = contactFields.get(NAME_FIELD);
        if (!isValidNameLength(name) || !isValidDescriptionLength(contactFields)) {
            return;
        }

        String phoneNumber = contactFields.getOrDefault(PHONE_NUMBER_FIELD, "");
        if (contactFields.containsKey(PHONE_NUMBER_FIELD) && !Contact.isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }
        if (contactList.containsContactNamed(name)) {
            ui.printDuplicateContactName(name);
            return;
        }

        contactList.addContact(new Contact(name, phoneNumber,
                contactFields.getOrDefault(DESCRIPTION_FIELD, "")));
    }

    /** Validates a contact name and deletes its matching contact. */
    private void deleteContact(String arguments) {
        Map<String, String> contactFields = parseContactFields(arguments, 0);
        if (contactFields == null || contactFields.size() != 1 || !contactFields.containsKey(NAME_FIELD)
                || contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }
        String name = contactFields.get(NAME_FIELD);
        if (!isValidNameLength(name)) {
            return;
        }
        contactList.deleteContact(name);
    }

    /** Validates replacement fields and updates their matching contact. */
    private void updateContact(String arguments) {
        Matcher firstField = CONTACT_FIELD_PATTERN.matcher(arguments);
        if (!firstField.find()) {
            ui.printContactUsage();
            return;
        }

        String currentName = arguments.substring(0, firstField.start()).strip();
        Map<String, String> contactFields = parseContactFields(arguments, firstField.start());
        if (currentName.isBlank() || contactFields == null || contactFields.isEmpty()) {
            ui.printContactUsage();
            return;
        }

        if (contactFields.containsKey(NAME_FIELD) && contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }
        if (!isValidNameLength(currentName) || !isValidDescriptionLength(contactFields)) {
            return;
        }

        String updatedName = contactFields.get(NAME_FIELD);
        if (updatedName != null && !isValidNameLength(updatedName)) {
            return;
        }

        String phoneNumber = contactFields.get(PHONE_NUMBER_FIELD);
        if (contactFields.containsKey(PHONE_NUMBER_FIELD) && !Contact.isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }
        if (updatedName != null && !updatedName.equals(currentName)
                && contactList.containsContactNamed(updatedName)) {
            ui.printDuplicateContactName(updatedName);
            return;
        }

        contactList.updateContact(currentName, updatedName, phoneNumber,
                contactFields.get(DESCRIPTION_FIELD));
    }

    /** Parses prefixed contact fields, returning {@code null} for invalid or duplicate fields. */
    private Map<String, String> parseContactFields(String arguments, int startIndex) {
        String fieldsText = arguments.substring(startIndex).trim();
        Matcher matcher = CONTACT_FIELD_PATTERN.matcher(fieldsText);
        Map<String, String> contactFields = new LinkedHashMap<>();
        int previousValueStart = -1;
        String previousFieldName = null;

        while (matcher.find()) {
            if (previousFieldName == null && matcher.start() != 0) {
                return null;
            }
            if (previousFieldName != null) {
                String value = fieldsText.substring(previousValueStart, matcher.start()).trim();
                contactFields.put(previousFieldName, value);
            }
            String fieldName = matcher.group(1).toLowerCase(Locale.ROOT);
            if (!fieldName.equals(NAME_FIELD) && !fieldName.equals(PHONE_NUMBER_FIELD)
                    && !fieldName.equals(DESCRIPTION_FIELD)) {
                return null;
            }
            if (contactFields.containsKey(fieldName) || fieldName.equals(previousFieldName)) {
                return null;
            }
            previousFieldName = fieldName;
            previousValueStart = matcher.end();
        }

        if (previousFieldName == null) {
            return null;
        }
        contactFields.put(previousFieldName, fieldsText.substring(previousValueStart).trim());
        return contactFields;
    }

    /** Returns whether a contact name is within the supported length. */
    private boolean isValidNameLength(String name) {
        if (name.length() <= MAX_NAME_LENGTH) {
            return true;
        }
        ui.printFieldTooLong("Contact name", MAX_NAME_LENGTH);
        return false;
    }

    /** Returns whether an optional contact description is within the supported length. */
    private boolean isValidDescriptionLength(Map<String, String> contactFields) {
        String description = contactFields.get(DESCRIPTION_FIELD);
        if (description == null || description.length() <= MAX_DESCRIPTION_LENGTH) {
            return true;
        }
        ui.printFieldTooLong("Contact description", MAX_DESCRIPTION_LENGTH);
        return false;
    }
}
