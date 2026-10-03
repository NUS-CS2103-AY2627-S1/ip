package verity.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import verity.client.Client;
import verity.client.PreferredContactMethod;
import verity.command.ClientAddCommand;
import verity.command.ClientAssociateCommand;
import verity.command.ClientDeleteCommand;
import verity.command.ClientDissociateCommand;
import verity.command.ClientEditCommand;
import verity.command.ClientFindCommand;
import verity.command.ClientListCommand;
import verity.command.ClientViewCommand;
import verity.command.Command;
import verity.exception.VerityException;
import verity.storage.StorageTextCodec;

/**
 * Parses commands in the {@code client} command namespace.
 */
class ClientCommandParser {
    private static final Set<String> CLIENT_FIELDS = Set.of(
            "name", "phone", "email", "address", "company", "notes", "preferred");
    private static final Set<String> CLEARABLE_FIELDS = Set.of(
            "phone", "email", "address", "company", "notes", "preferred");
    private static final Pattern MARKER_PATTERN = Pattern.compile(
            "(?i)(^|\\s)/(name|phone|email|address|company|notes|preferred|clear|task)(?=\\s|$)");
    private static final Pattern UNKNOWN_MARKER_PATTERN = Pattern.compile(
            "(?i)(^|\\s)/([a-z]+)(?=\\s|$)");

    /**
     * Parses one complete client command.
     *
     * @param fullCommand Complete user command.
     * @param taskCount Number of currently stored tasks.
     * @return Parsed client command.
     * @throws VerityException If syntax or arguments are invalid.
     */
    Command parse(String fullCommand, int taskCount) throws VerityException {
        String[] commandParts = fullCommand.stripLeading().split("\\s+", 3);
        if (commandParts.length < 2) {
            throw new VerityException("Please provide a client command.");
        }

        String action = commandParts[1].toLowerCase(Locale.ROOT);
        String remainder = commandParts.length == 3 ? commandParts[2] : "";
        return switch (action) {
            case "add" -> parseAdd(remainder);
            case "list" -> parseList(remainder);
            case "view" -> new ClientViewCommand(parseSingleClientId(remainder));
            case "find" -> parseFind(remainder);
            case "edit" -> parseEdit(remainder);
            case "associate" -> parseTaskAssociation(remainder, taskCount, true);
            case "dissociate" -> parseTaskAssociation(remainder, taskCount, false);
            case "delete" -> parseDelete(remainder);
            default -> throw new VerityException("I don't know that client command.");
        };
    }

    private Command parseAdd(String remainder) throws VerityException {
        Map<String, List<String>> markers = parseMarkers(
                remainder, CLIENT_FIELDS, Set.of());
        if (!markers.containsKey("name")) {
            throw new VerityException("A client name is required.");
        }

        PreferredContactMethod preferredContactMethod = PreferredContactMethod.parse(
                getField(markers, "preferred"));
        return new ClientAddCommand(
                getField(markers, "name"),
                getField(markers, "phone"),
                getField(markers, "email"),
                getField(markers, "address"),
                getField(markers, "company"),
                decodeNotes(getField(markers, "notes")),
                preferredContactMethod);
    }

    private Command parseList(String remainder) throws VerityException {
        if (!remainder.isEmpty()) {
            throw new VerityException("The client list command does not accept arguments.");
        }
        return new ClientListCommand();
    }

    private Command parseFind(String remainder) throws VerityException {
        if (remainder.isBlank()) {
            throw new VerityException("Please provide a client name to search for.");
        }
        return new ClientFindCommand(remainder);
    }

    private Command parseEdit(String remainder) throws VerityException {
        IdAndBody idAndBody = splitClientIdAndBody(remainder);
        if (idAndBody.body().isEmpty()) {
            throw new VerityException("Please provide at least one field to update.");
        }

        Set<String> allowedMarkers = new HashSet<>(CLIENT_FIELDS);
        allowedMarkers.add("clear");
        Map<String, List<String>> markers = parseMarkers(
                idAndBody.body(), allowedMarkers, Set.of("clear"));
        Map<String, String> changes = new HashMap<>();
        for (String field : CLIENT_FIELDS) {
            if (markers.containsKey(field)) {
                String value = getField(markers, field);
                changes.put(field, field.equals("notes") ? decodeNotes(value) : value);
            }
        }

        Set<String> clearedFields = new HashSet<>();
        for (String field : markers.getOrDefault("clear", List.of())) {
            String normalizedField = field.toLowerCase(Locale.ROOT);
            if (!CLEARABLE_FIELDS.contains(normalizedField)) {
                throw new VerityException("The field '" + field + "' cannot be cleared.");
            }
            if (!clearedFields.add(normalizedField) || changes.containsKey(normalizedField)) {
                throw new VerityException(
                        "A client field cannot be changed more than once.");
            }
        }
        return new ClientEditCommand(idAndBody.clientId(), changes, clearedFields);
    }

    private Command parseTaskAssociation(String remainder, int taskCount,
            boolean isAssociation) throws VerityException {
        IdAndBody idAndBody = splitClientIdAndBody(remainder);
        Map<String, List<String>> markers = parseMarkers(
                idAndBody.body(), Set.of("task"), Set.of("task"));
        List<Integer> taskIndexes = parseTaskIndexes(
                markers.getOrDefault("task", List.of()), taskCount);
        if (isAssociation) {
            return new ClientAssociateCommand(idAndBody.clientId(), taskIndexes);
        }
        return new ClientDissociateCommand(idAndBody.clientId(), taskIndexes);
    }

    private Command parseDelete(String remainder) throws VerityException {
        String[] arguments = remainder.split("\\s+");
        if (remainder.isEmpty() || arguments.length > 2) {
            throw new VerityException(
                    "Use client delete CLIENT_ID followed optionally by confirm.");
        }
        String clientId = canonicalClientId(arguments[0]);
        boolean isConfirmed = arguments.length == 2;
        if (isConfirmed && !arguments[1].equalsIgnoreCase("confirm")) {
            throw new VerityException(
                    "Use client delete CLIENT_ID followed optionally by confirm.");
        }
        return new ClientDeleteCommand(clientId, isConfirmed);
    }

    private String parseSingleClientId(String remainder) throws VerityException {
        if (remainder.isEmpty() || remainder.contains(" ")) {
            throw new VerityException("Please provide exactly one client ID.");
        }
        return canonicalClientId(remainder);
    }

    private IdAndBody splitClientIdAndBody(String remainder) throws VerityException {
        if (remainder.isEmpty()) {
            throw new VerityException("Please provide a client ID.");
        }
        String[] parts = remainder.split("\\s+", 2);
        String body = parts.length == 2 ? parts[1] : "";
        return new IdAndBody(canonicalClientId(parts[0]), body);
    }

    private List<Integer> parseTaskIndexes(List<String> taskNumbers, int taskCount)
            throws VerityException {
        if (taskNumbers.isEmpty()) {
            throw new VerityException("Please provide at least one /task number.");
        }

        List<Integer> taskIndexes = new ArrayList<>();
        for (String taskNumber : taskNumbers) {
            int taskIndex;
            try {
                taskIndex = Integer.parseInt(taskNumber) - 1;
            } catch (NumberFormatException exception) {
                throw new VerityException("The task number must be a number.");
            }
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new VerityException("That task number does not exist.");
            }
            if (taskIndexes.contains(taskIndex)) {
                throw new VerityException("Task numbers must not be repeated.");
            }
            taskIndexes.add(taskIndex);
        }
        return taskIndexes;
    }

    private Map<String, List<String>> parseMarkers(String body,
            Set<String> allowedMarkers, Set<String> repeatableMarkers)
            throws VerityException {
        if (body.isEmpty()) {
            return Map.of();
        }

        List<MarkerPosition> positions = findMarkerPositions(body);
        if (positions.isEmpty()) {
            Matcher unknownMarker = UNKNOWN_MARKER_PATTERN.matcher(body);
            if (unknownMarker.find()) {
                throw new VerityException(
                        "Unknown client field '/" + unknownMarker.group(2) + "'.");
            }
            throw new VerityException("Client fields must use slash-prefixed markers.");
        }
        if (positions.get(0).markerStart() != 0) {
            throw new VerityException("Client fields must use slash-prefixed markers.");
        }

        Map<String, List<String>> values = new HashMap<>();
        for (int i = 0; i < positions.size(); i++) {
            MarkerPosition current = positions.get(i);
            String marker = current.name();
            if (!allowedMarkers.contains(marker)) {
                throw new VerityException("Unknown client field '/" + marker + "'.");
            }
            if (values.containsKey(marker) && !repeatableMarkers.contains(marker)) {
                throw new VerityException(
                        "A client field cannot be changed more than once.");
            }

            int valueEnd = i + 1 < positions.size()
                    ? positions.get(i + 1).markerStart() - 1
                    : body.length();
            String value = body.substring(current.valueStart(), valueEnd);
            if (value.isEmpty()) {
                throw new VerityException("The /" + marker + " value cannot be empty.");
            }
            Matcher unknownMarker = UNKNOWN_MARKER_PATTERN.matcher(value);
            if (unknownMarker.find()) {
                throw new VerityException(
                        "Unknown client field '/" + unknownMarker.group(2) + "'.");
            }
            values.computeIfAbsent(marker, ignored -> new ArrayList<>()).add(value);
        }
        return values;
    }

    private List<MarkerPosition> findMarkerPositions(String body) throws VerityException {
        List<MarkerPosition> positions = new ArrayList<>();
        Matcher matcher = MARKER_PATTERN.matcher(body);
        while (matcher.find()) {
            int markerStart = matcher.start() + matcher.group(1).length();
            int valueStart = matcher.end();
            if (valueStart >= body.length() || !Character.isWhitespace(body.charAt(valueStart))) {
                throw new VerityException(
                        "The /" + matcher.group(2).toLowerCase(Locale.ROOT)
                                + " value cannot be empty.");
            }
            positions.add(new MarkerPosition(
                    matcher.group(2).toLowerCase(Locale.ROOT), markerStart, valueStart + 1));
        }
        return positions;
    }

    private String getField(Map<String, List<String>> markers, String field) {
        return markers.getOrDefault(field, List.of("")).get(0);
    }

    private String decodeNotes(String notes) throws VerityException {
        return StorageTextCodec.decode(notes);
    }

    private String canonicalClientId(String clientId) throws VerityException {
        return Client.formatId(Client.parseId(clientId));
    }

    private record MarkerPosition(String name, int markerStart, int valueStart) {
    }

    private record IdAndBody(String clientId, String body) {
    }
}
