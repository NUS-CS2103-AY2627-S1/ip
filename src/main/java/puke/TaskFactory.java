package puke;

/** Reconstructs tasks from their serialized storage representation. */
public class TaskFactory {
    private TaskFactory() {
    }

    /**
     * Reconstructs a task from one serialized storage record.
     *
     * @param fields Serialized task fields.
     * @return Reconstructed task, or {@code null} for an unknown task type.
     */
    public static Task fromStorageFields(String... fields) {
        boolean done = fields[1].equals("1");
        return switch (fields[0]) {
            case "T" -> new Task(fields[2], done);
            case "D" -> new Deadline(fields[2], fields[3], done);
            case "E" -> new Event(fields[2], fields[3], fields[4], done);
            default -> null;
        };
    }
}
