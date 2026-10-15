package kojispawn.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import kojispawn.exception.KojisPawnException;
import kojispawn.task.Deadline;
import kojispawn.task.Event;
import kojispawn.task.Task;
import kojispawn.task.Todo;

/**
 * Reconstructs task objects from their stored data representation.
 */
final class TaskDataParser {
    /**
     * Reconstructs one task from the format produced by {@link Task#toDataString()}.
     *
     * @param taskLine Stored representation of one task.
     * @return Reconstructed task with its saved completion state.
     * @throws KojisPawnException If a field is missing or invalid.
     */
    Task parse(String taskLine) throws KojisPawnException {
        String[] fields = taskLine.split(" \\| ", -1);
        validateFields(fields);
        Task task = createTask(fields);

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private void validateFields(String[] fields) throws KojisPawnException {
        int expectedCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new KojisPawnException("Unknown task type: " + fields[0] + ".");
        };
        if (fields.length != expectedCount) {
            throw new KojisPawnException("Expected " + expectedCount + " fields for " + fields[0] + ".");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new KojisPawnException("Completion status must be 0 or 1.");
        }
        for (int index = 2; index < fields.length; index++) {
            if (fields[index].isBlank()) {
                throw new KojisPawnException("Task descriptions and date values must not be blank.");
            }
        }
    }

    private Task createTask(String[] fields) throws KojisPawnException {
        try {
            return switch (fields[0]) {
                case "T" -> new Todo(fields[2]);
                case "D" -> new Deadline(fields[2], LocalDate.parse(fields[3]));
                case "E" -> new Event(fields[2], fields[3], fields[4]);
                default -> throw new AssertionError("Stored task type must already be validated");
            };
        } catch (DateTimeParseException exception) {
            throw new KojisPawnException("Deadline date must be a real date in yyyy-MM-dd format.");
        }
    }
}
