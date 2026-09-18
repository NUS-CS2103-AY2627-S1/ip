package plana.parser;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plana.client.Client;
import plana.command.ClientAction;
import plana.command.ClientCommand;
import plana.command.Command;
import plana.exception.PlanaException;

/**
 * Parses and validates client subcommands and their fields.
 */
class ClientCommandParser {
    private static final String CLIENT_ADD_USAGE = "Try: client add <name> /email <email>.";
    private static final String CLIENT_FIELD_USAGE = "Use /phone, /address, /preferences, or /notes.";
    private static final String CLIENT_EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final Pattern CLIENT_FIELD_PATTERN = Pattern.compile(
            "(?:^|\\s)/(name|email|phone|address|preferences|notes)(?=\\s|$)");
    private static final Pattern CLIENT_MARKER_PATTERN = Pattern.compile(
            "(?:^|\\s)/([A-Za-z][A-Za-z0-9_-]*)(?=\\s|$)");

    /**
     * Parses a client command and its subcommand arguments.
     *
     * @param arguments the text after the {@code client} keyword.
     * @return the validated client command.
     * @throws PlanaException if the client command is malformed.
     */
    Command parse(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw new PlanaException("Oops, client needs an action. " + CLIENT_ADD_USAGE);
        }
        String[] actionAndArguments = arguments.split("\\s+", 2);
        String actionText = actionAndArguments[0];
        String actionArguments = actionAndArguments.length == 2 ? actionAndArguments[1].trim() : "";
        return switch (actionText) {
            case "add" -> parseAdd(actionArguments);
            case "list" -> parseList(actionArguments);
            case "view" -> new ClientCommand(ClientAction.VIEW, parseViewReference(actionArguments));
            case "find" -> parseFind(actionArguments);
            case "edit" -> parseEdit(actionArguments);
            case "delete" -> new ClientCommand(ClientAction.DELETE, parseReference(actionArguments, "delete"));
            default -> throw new PlanaException("Oops, I don't recognize client action '" + actionText + "'."
                    + " " + CLIENT_ADD_USAGE);
        };
    }

    private Command parseAdd(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw new PlanaException("Oops, a client needs a name and an email. " + CLIENT_ADD_USAGE);
        }
        ClientFields fields = parseFields(arguments, true);
        if (fields.name().isBlank()) {
            throw new PlanaException("Oops, that client is missing its name. " + CLIENT_ADD_USAGE);
        }
        if (!fields.values().containsKey(ClientCommand.Field.EMAIL)
                || fields.values().get(ClientCommand.Field.EMAIL).isBlank()) {
            throw new PlanaException("Oops, that client is missing its email. " + CLIENT_ADD_USAGE);
        }
        validateFields(fields.name(), fields.values(), false);
        return new ClientCommand(new Client(fields.name(), fields.values().get(ClientCommand.Field.EMAIL),
                fields.values().getOrDefault(ClientCommand.Field.PHONE, ""),
                fields.values().getOrDefault(ClientCommand.Field.ADDRESS, ""),
                fields.values().getOrDefault(ClientCommand.Field.PREFERENCES, ""),
                fields.values().getOrDefault(ClientCommand.Field.NOTES, "")));
    }

    private Command parseList(String arguments) throws PlanaException {
        if (!arguments.isBlank()) {
            throw new PlanaException("Oops, client list doesn't take any arguments. Try: client list.");
        }
        return new ClientCommand(ClientAction.LIST);
    }

    private Command parseFind(String keyword) throws PlanaException {
        if (keyword.isBlank()) {
            throw new PlanaException("Oops, client find needs a keyword. Try: client find <keyword>.");
        }
        return new ClientCommand(ClientAction.FIND, keyword, true);
    }

    private Command parseEdit(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw new PlanaException("Oops, client edit needs a client position. Try: client edit C1 /phone <phone>.");
        }
        String[] referenceAndFields = arguments.split("\\s+", 2);
        String reference = parseReference(referenceAndFields[0], "edit");
        if (referenceAndFields.length == 1 || referenceAndFields[1].isBlank()) {
            throw new PlanaException("Oops, client edit needs at least one field to change."
                    + " Try: client edit <position> /phone <phone>.");
        }
        ClientFields fields = parseFields(referenceAndFields[1].trim(), false);
        if (fields.values().isEmpty()) {
            throw new PlanaException("Oops, client edit needs at least one field to change."
                    + " Try: client edit <position> /phone <phone>.");
        }
        validateFields(fields.name(), fields.values(), true);
        return new ClientCommand(reference, fields.values());
    }

    private String parseReference(String reference, String action) throws PlanaException {
        if (reference.isBlank()) {
            throw new PlanaException("Oops, client " + action + " needs a client position."
                    + " Try: client " + action + " C1.");
        }
        if (!reference.matches("C[1-9][0-9]*")) {
            throw new PlanaException("Oops, '" + reference + "' isn't a valid client position."
                    + " Use a reference like C1.");
        }
        return reference;
    }

    private String parseViewReference(String reference) throws PlanaException {
        if (reference.isBlank()) {
            throw new PlanaException("Oops, client view needs a client position. Try: client view C1.");
        }
        if (!reference.matches("[Cc]?[1-9][0-9]*")) {
            throw new PlanaException("Oops, '" + reference + "' isn't a valid client position."
                    + " Use a reference like C1.");
        }
        String digits = reference.startsWith("C") || reference.startsWith("c")
                ? reference.substring(1) : reference;
        return "C" + digits;
    }

    private ClientFields parseFields(String arguments, boolean isAddCommand) throws PlanaException {
        Set<String> knownFieldNames = Set.of("name", "email", "phone", "address", "preferences", "notes");
        Matcher unknownMarkerMatcher = CLIENT_MARKER_PATTERN.matcher(arguments);
        while (unknownMarkerMatcher.find()) {
            if (!knownFieldNames.contains(unknownMarkerMatcher.group(1))) {
                throw new PlanaException("Oops, I don't recognize the client field /"
                        + unknownMarkerMatcher.group(1) + ". " + CLIENT_FIELD_USAGE);
            }
        }
        List<ClientMarker> markers = new ArrayList<>();
        Matcher fieldMatcher = CLIENT_FIELD_PATTERN.matcher(arguments);
        while (fieldMatcher.find()) {
            markers.add(new ClientMarker(fieldMatcher.group(1), fieldMatcher.start(), fieldMatcher.end()));
        }
        EnumMap<ClientCommand.Field, String> values = new EnumMap<>(ClientCommand.Field.class);
        String name = markers.isEmpty() ? arguments.trim()
                : arguments.substring(0, markers.get(0).start()).trim();
        for (int i = 0; i < markers.size(); i++) {
            ClientMarker marker = markers.get(i);
            String fieldName = marker.name();
            ClientCommand.Field field = ClientCommand.Field.valueOf(fieldName.toUpperCase(Locale.ROOT));
            if (values.containsKey(field)) {
                throw new PlanaException("Oops, the client field /" + fieldName + " was provided more than once.");
            }
            int valueEnd = i + 1 < markers.size() ? markers.get(i + 1).start() : arguments.length();
            values.put(field, unquoteEmptyValue(arguments.substring(marker.end(), valueEnd).trim()));
        }
        if (isAddCommand && values.containsKey(ClientCommand.Field.NAME)) {
            throw new PlanaException("Oops, put the client name before the field markers. " + CLIENT_ADD_USAGE);
        }
        if (!isAddCommand && !name.isBlank()) {
            throw new PlanaException("Oops, client edit fields must use markers such as /phone or /notes.");
        }
        return new ClientFields(name, values);
    }

    private String unquoteEmptyValue(String value) {
        return value.equals("\"\"") ? "" : value;
    }

    private void validateFields(String name, EnumMap<ClientCommand.Field, String> values,
                                boolean isEditCommand) throws PlanaException {
        if (!name.isBlank() && (name.length() > 100 || containsControlCharacter(name))) {
            throw new PlanaException("Oops, that client name isn't valid. Use 100 characters or fewer.");
        }
        if (values.containsKey(ClientCommand.Field.NAME)) {
            String updatedName = values.get(ClientCommand.Field.NAME);
            if (updatedName.isBlank() || updatedName.length() > 100 || containsControlCharacter(updatedName)) {
                throw new PlanaException(updatedName.isBlank() ? "Oops, a client's name can't be empty."
                        : "Oops, that client name isn't valid. Use 100 characters or fewer.");
            }
        }
        if (values.containsKey(ClientCommand.Field.EMAIL)) {
            String email = values.get(ClientCommand.Field.EMAIL).toLowerCase(Locale.ROOT);
            if (email.length() > 254 || !email.matches(CLIENT_EMAIL_PATTERN)) {
                throw new PlanaException("Oops, that email isn't valid. Use an address like alice@example.com.");
            }
            values.put(ClientCommand.Field.EMAIL, email);
        }
        validateOptionalField(values, ClientCommand.Field.PHONE, 30, isEditCommand);
        validateOptionalField(values, ClientCommand.Field.ADDRESS, 200, isEditCommand);
        validateOptionalField(values, ClientCommand.Field.PREFERENCES, 300, isEditCommand);
        validateOptionalField(values, ClientCommand.Field.NOTES, 500, isEditCommand);
    }

    private void validateOptionalField(EnumMap<ClientCommand.Field, String> values, ClientCommand.Field field,
                                       int maximumLength, boolean isEditCommand) throws PlanaException {
        if (!values.containsKey(field)) {
            return;
        }
        String value = values.get(field);
        if (value.isBlank() && !isEditCommand) {
            throw new PlanaException("Oops, /" + field.name().toLowerCase()
                    + " needs a value or should be left out.");
        }
        if (value.length() > maximumLength || containsControlCharacter(value)) {
            throw new PlanaException("Oops, /" + field.name().toLowerCase()
                    + " is too long or contains invalid characters.");
        }
        if (field == ClientCommand.Field.PHONE && !isValidPhone(value)) {
            throw new PlanaException("Oops, that phone number isn't valid. Use 7 to 15 digits.");
        }
    }

    private boolean isValidPhone(String phone) {
        if (phone.isBlank() || !phone.matches("[0-9+(). -]+")) {
            return phone.isBlank();
        }
        long digitCount = phone.chars().filter(Character::isDigit).count();
        return digitCount >= 7 && digitCount <= 15;
    }

    private boolean containsControlCharacter(String value) {
        return value.chars().anyMatch(character -> Character.isISOControl((char) character));
    }

    private record ClientFields(String name, EnumMap<ClientCommand.Field, String> values) {
    }

    private record ClientMarker(String name, int start, int end) {
    }
}
