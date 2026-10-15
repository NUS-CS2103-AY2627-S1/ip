package greenchonk.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.StorageException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.Todo;

/**
 * Converts tasks to and from Green Chonk's line-based storage format.
 */
final class TaskCodec {
    private static final String DATA_SEPARATOR = " | ";
    private static final int FIELD_TYPE_INDEX = 0;
    private static final int FIELD_STATUS_INDEX = 1;
    private static final int FIELD_DESCRIPTION_INDEX = 2;
    private static final int FIELD_FIRST_DATE_INDEX = 3;
    private static final int FIELD_SECOND_DATE_INDEX = 4;
    private static final int FIELD_COUNT_MINIMUM = 3;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;
    private static final String STATUS_NOT_DONE = "0";
    private static final String STATUS_DONE = "1";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    /**
     * Encodes one task as a line in the data file.
     *
     * @param task the task to encode.
     * @return the task's canonical storage representation.
     * @throws StorageException if the task type is unsupported.
     */
    String encode(Task task) throws StorageException {
        String status = task.isDone() ? STATUS_DONE : STATUS_NOT_DONE;
        String commonFields = task.getTypeIcon() + DATA_SEPARATOR + status
                + DATA_SEPARATOR + escapeDataField(task.getDescription());
        if (task instanceof Deadline deadline) {
            return commonFields + DATA_SEPARATOR + deadline.getDueDate();
        }
        if (task instanceof Event event) {
            return commonFields + DATA_SEPARATOR + event.getStartDate()
                    + DATA_SEPARATOR + event.getEndDate();
        }
        if (task instanceof Todo) {
            return commonFields;
        }
        throw new StorageException("I couldn't save an unsupported task type.");
    }

    /**
     * Decodes one task from a line in the data file.
     *
     * @param line the stored task line.
     * @param lineNumber the physical line number used in error messages.
     * @return the restored task.
     * @throws StorageException if the line does not use the expected format.
     */
    Task decode(String line, int lineNumber) throws StorageException {
        List<String> fields = splitDataLine(line, lineNumber);
        if (fields.size() < FIELD_COUNT_MINIMUM) {
            throw invalidDataLine(lineNumber);
        }
        for (String field : fields) {
            if (field.isEmpty()) {
                throw invalidDataLine(lineNumber);
            }
        }

        String status = fields.get(FIELD_STATUS_INDEX);
        if (!status.equals(STATUS_NOT_DONE) && !status.equals(STATUS_DONE)) {
            throw invalidDataLine(lineNumber);
        }

        Task task = decodeTaskFields(fields, lineNumber);
        if (status.equals(STATUS_DONE)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Constructs the task represented by validated, non-empty fields.
     *
     * @param fields the decoded storage fields.
     * @param lineNumber the physical line number used in error messages.
     * @return the restored task.
     * @throws StorageException if the type, field count, or task details are invalid.
     */
    private static Task decodeTaskFields(List<String> fields, int lineNumber)
            throws StorageException {
        assert fields.size() >= FIELD_COUNT_MINIMUM
                : "Validated task data must contain type, status, and description fields";

        String type = fields.get(FIELD_TYPE_INDEX);
        String description = fields.get(FIELD_DESCRIPTION_INDEX);
        switch (type) {
            case TYPE_TODO:
                if (fields.size() != FIELD_COUNT_TODO) {
                    throw invalidDataLine(lineNumber);
                }
                return new Todo(description);
            case TYPE_DEADLINE:
                if (fields.size() != FIELD_COUNT_DEADLINE) {
                    throw invalidDataLine(lineNumber);
                }
                return new Deadline(description,
                        parseSavedDate(fields.get(FIELD_FIRST_DATE_INDEX), lineNumber));
            case TYPE_EVENT:
                if (fields.size() != FIELD_COUNT_EVENT) {
                    throw invalidDataLine(lineNumber);
                }
                try {
                    return new Event(description,
                            parseSavedDate(fields.get(FIELD_FIRST_DATE_INDEX), lineNumber),
                            parseSavedDate(fields.get(FIELD_SECOND_DATE_INDEX), lineNumber));
                } catch (IllegalArgumentException exception) {
                    throw invalidDataLine(lineNumber);
                }
            default:
                throw invalidDataLine(lineNumber);
        }
    }

    /**
     * Parses a canonical date stored in the data file.
     *
     * @param dateText the stored date text.
     * @param lineNumber the physical line number used in error messages.
     * @return the parsed date.
     * @throws StorageException if the stored date is invalid.
     */
    private static LocalDate parseSavedDate(String dateText, int lineNumber)
            throws StorageException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw invalidDataLine(lineNumber);
        }
    }

    /**
     * Splits a stored line while preserving escaped pipe and backslash characters.
     *
     * @param line the stored line to split.
     * @param lineNumber the physical line number used in error messages.
     * @return the unescaped fields.
     * @throws StorageException if the line ends with an incomplete escape.
     */
    private static List<String> splitDataLine(String line, int lineNumber)
            throws StorageException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidDataLine(lineNumber);
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw invalidDataLine(lineNumber);
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes storage delimiter characters inside a user-provided value.
     *
     * @param value the value to escape.
     * @return a value safe to store as one field.
     */
    private static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Creates a consistent exception for a malformed physical data-file line.
     *
     * @param lineNumber the one-based physical line number.
     * @return an exception identifying the malformed line.
     */
    private static StorageException invalidDataLine(int lineNumber) {
        return new StorageException("The data file has an invalid task on line " + lineNumber + ".");
    }
}
