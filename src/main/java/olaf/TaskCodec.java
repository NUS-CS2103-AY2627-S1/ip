package olaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts tasks to and from Olaf's escaped, pipe-delimited storage format.
 */
final class TaskCodec {
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";
    private static final String FIELD_SEPARATOR = " | ";
    private static final int FIELD_INDEX_TYPE = 0;
    private static final int FIELD_INDEX_STATUS = 1;
    private static final int FIELD_INDEX_DESCRIPTION = 2;
    private static final int FIELD_INDEX_DEADLINE = 3;
    private static final int FIELD_INDEX_EVENT_START = 3;
    private static final int FIELD_INDEX_EVENT_END = 4;
    private static final int FIELD_COUNT_HEADER = 2;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    /**
     * Encodes one task as a storage record.
     *
     * @param task task to encode.
     * @return escaped storage record
     */
    String encode(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getTypeCode());
        fields.add(task.isDone() ? STATUS_DONE : STATUS_NOT_DONE);
        fields.add(task.getDescription());
        fields.addAll(task.getAdditionalStorageFields());

        List<String> escapedFields = fields.stream()
                .map(this::escape)
                .toList();
        return String.join(FIELD_SEPARATOR, escapedFields);
    }

    /**
     * Decodes one storage record into a task.
     *
     * @param record stored task record.
     * @param lineNumber one-based source line number used in error messages.
     * @return decoded task
     * @throws StorageException if the record is malformed
     */
    Task decode(String record, int lineNumber) throws StorageException {
        List<String> fields = splitFields(record, lineNumber);
        if (fields.size() < FIELD_COUNT_HEADER) {
            throw createInvalidRecordException(lineNumber, "missing task type or status");
        }

        String type = fields.get(FIELD_INDEX_TYPE);
        String status = fields.get(FIELD_INDEX_STATUS);
        validateStatus(status, lineNumber);

        Task task = switch (type) {
            case Todo.TYPE_CODE -> decodeTodo(fields, lineNumber);
            case Deadline.TYPE_CODE -> decodeDeadline(fields, lineNumber);
            case Event.TYPE_CODE -> decodeEvent(fields, lineNumber);
            default -> throw createInvalidRecordException(lineNumber, "unknown task type '" + type + "'");
        };

        if (STATUS_DONE.equals(status)) {
            task.markAsDone();
        }
        assert task.isDone() == STATUS_DONE.equals(status)
                : "Decoded task completion must match the stored status";
        return task;
    }

    private Task decodeTodo(List<String> fields, int lineNumber) throws StorageException {
        validateFieldCount(fields, FIELD_COUNT_TODO, lineNumber);
        return new Todo(requireText(fields.get(FIELD_INDEX_DESCRIPTION), "description", lineNumber));
    }

    private Task decodeDeadline(List<String> fields, int lineNumber) throws StorageException {
        validateFieldCount(fields, FIELD_COUNT_DEADLINE, lineNumber);
        String description = requireText(fields.get(FIELD_INDEX_DESCRIPTION), "description", lineNumber);
        LocalDate dueDate = parseDate(fields.get(FIELD_INDEX_DEADLINE), "deadline", lineNumber);
        return new Deadline(description, dueDate);
    }

    private Task decodeEvent(List<String> fields, int lineNumber) throws StorageException {
        validateFieldCount(fields, FIELD_COUNT_EVENT, lineNumber);
        String description = requireText(fields.get(FIELD_INDEX_DESCRIPTION), "description", lineNumber);
        LocalDate startDate = parseDate(fields.get(FIELD_INDEX_EVENT_START), "event start", lineNumber);
        LocalDate endDate = parseDate(fields.get(FIELD_INDEX_EVENT_END), "event end", lineNumber);
        return new Event(description, startDate, endDate);
    }

    private LocalDate parseDate(String value, String fieldName, int lineNumber)
            throws StorageException {
        String dateText = requireText(value, fieldName, lineNumber);
        try {
            return TaskDateFormat.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw createInvalidRecordException(lineNumber, fieldName + " must use yyyy-MM-dd");
        }
    }

    private List<String> splitFields(String record, int lineNumber) throws StorageException {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaping = false;

        for (int index = 0; index < record.length(); index++) {
            char character = record.charAt(index);
            if (isEscaping) {
                validateEscapedCharacter(character, lineNumber);
                currentField.append(character);
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaping) {
            throw createInvalidRecordException(lineNumber, "unfinished escape sequence");
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    private String escape(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    private void validateEscapedCharacter(char character, int lineNumber) throws StorageException {
        if (character != '\\' && character != '|') {
            throw createInvalidRecordException(lineNumber, "invalid escape sequence");
        }
    }

    private void validateStatus(String status, int lineNumber) throws StorageException {
        if (!STATUS_DONE.equals(status) && !STATUS_NOT_DONE.equals(status)) {
            throw createInvalidRecordException(lineNumber, "status must be 0 or 1");
        }
    }

    private void validateFieldCount(List<String> fields, int expectedCount, int lineNumber)
            throws StorageException {
        if (fields.size() != expectedCount) {
            throw createInvalidRecordException(lineNumber, "incorrect number of fields");
        }
    }

    private String requireText(String value, String fieldName, int lineNumber) throws StorageException {
        if (value.isBlank()) {
            throw createInvalidRecordException(lineNumber, fieldName + " cannot be empty");
        }
        return value;
    }

    private StorageException createInvalidRecordException(int lineNumber, String reason) {
        return new StorageException("Invalid task data on line " + lineNumber + ": " + reason + ".");
    }
}
