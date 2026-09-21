package friday.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import friday.contact.Contact;

/** Parses contact subcommands without changing contact state or accessing storage. */
public final class ContactParser {
    private static final String INVALID_COMMAND_MESSAGE = "Invalid contact command. Use: contact add, "
            + "contact list, contact find, or contact delete.";
    private static final Pattern FIELD_PREFIX = Pattern.compile("(?U)(?<!\\S)/\\S*");

    /** Identifies the four supported contact operations. */
    public enum CommandType {
        ADD, LIST, FIND, DELETE
    }

    /** Holds optional fields extracted from one contact-add command. */
    private record ContactFields(String phone, String email) {
    }

    private ContactParser() {
        // Parsing has no state.
    }

    /** Identifies the subcommand and rejects extra list arguments or unknown operations. */
    public static CommandType parseCommandType(String command) {
        String body = contactBody(command);
        String[] parts = body.split("(?U)\\s+", 2);
        return switch (parts[0]) {
            case "add" -> CommandType.ADD;
            case "find" -> CommandType.FIND;
            case "delete" -> CommandType.DELETE;
            case "list" -> {
                if (parts.length != 1) {
                    throw new IllegalArgumentException(INVALID_COMMAND_MESSAGE);
                }
                yield CommandType.LIST;
            }
            default -> throw new IllegalArgumentException(INVALID_COMMAND_MESSAGE);
        };
    }

    /**
     * Parses named contact fields in either order, validating structure before field values.
     * Repeated, unknown, or empty fields are rejected instead of silently losing input.
     */
    public static Contact parseContact(String command) {
        String body = arguments(command, CommandType.ADD);
        Matcher prefixes = FIELD_PREFIX.matcher(body);
        if (!prefixes.find()) {
            throw new IllegalArgumentException(Contact.INVALID_FORMAT_MESSAGE);
        }
        String name = body.substring(0, prefixes.start()).strip();
        ContactFields fields = parseFields(body, prefixes);
        return new Contact(name, fields.phone(), fields.email());
    }

    /** Extracts unique, nonblank phone and email fields after the first field prefix has been found. */
    private static ContactFields parseFields(String body, Matcher prefixes) {
        String phone = "";
        String email = "";
        boolean hasPhone = false;
        boolean hasEmail = false;
        boolean hasNext = true;
        while (hasNext) {
            String prefix = prefixes.group();
            int valueStart = prefixes.end();
            hasNext = prefixes.find();
            String value = body.substring(valueStart, hasNext ? prefixes.start() : body.length()).strip();
            if (value.isEmpty()) {
                throw new IllegalArgumentException(Contact.INVALID_FORMAT_MESSAGE);
            }
            switch (prefix) {
                case "/phone" -> {
                    if (hasPhone) {
                        throw new IllegalArgumentException(Contact.INVALID_FORMAT_MESSAGE);
                    }
                    phone = value;
                    hasPhone = true;
                }
                case "/email" -> {
                    if (hasEmail) {
                        throw new IllegalArgumentException(Contact.INVALID_FORMAT_MESSAGE);
                    }
                    email = value;
                    hasEmail = true;
                }
                default -> throw new IllegalArgumentException(Contact.INVALID_FORMAT_MESSAGE);
            }
        }
        return new ContactFields(phone, email);
    }

    /** Returns the trimmed, nonblank search phrase, preserving internal whitespace and case. */
    public static String parseFindKeyword(String command) {
        String keyword = arguments(command, CommandType.FIND);
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Invalid contact find format. Use: contact find KEYWORD");
        }
        return keyword;
    }

    /** Parses a positive decimal contact number; the contact list checks the upper bound. */
    public static int parseContactNumber(String command) {
        String number = arguments(command, CommandType.DELETE);
        try {
            if (!number.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int contactNumber = Integer.parseInt(number);
            if (contactNumber < 1) {
                throw new NumberFormatException();
            }
            return contactNumber;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The contact number is invalid.");
        }
    }

    /** Extracts arguments only for the requested operation, preventing accidental parser misuse. */
    private static String arguments(String command, CommandType expectedType) {
        if (parseCommandType(command) != expectedType) {
            throw new IllegalArgumentException(INVALID_COMMAND_MESSAGE);
        }
        String[] parts = contactBody(command).split("(?U)\\s+", 2);
        return parts.length == 1 ? "" : parts[1].strip();
    }

    /** Preserves the existing top-level command boundary while trimming contact-specific arguments. */
    private static String contactBody(String command) {
        if (!command.equals("contact") && !command.startsWith("contact ")) {
            throw new IllegalArgumentException(INVALID_COMMAND_MESSAGE);
        }
        return command.substring("contact".length()).strip();
    }
}
