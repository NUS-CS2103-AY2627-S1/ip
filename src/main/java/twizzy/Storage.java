package twizzy;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Twizzy tasks in a human-readable text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks, returning an empty list when no data file exists yet.
     *
     * @return tasks reconstructed from the data file
     * @throws IOException if the file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid task data on line " + (i + 1) + ".", exception);
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     * The replacement data is written completely before it replaces the existing file.
     *
     * @param tasks tasks to persist
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path targetPath = filePath.toAbsolutePath();
        Path parentDirectory = targetPath.getParent();
        Files.createDirectories(parentDirectory);

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        Path temporaryFile = Files.createTempFile(parentDirectory, targetPath.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardOpenOption.TRUNCATE_EXISTING);
            replaceDataFile(temporaryFile, targetPath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D" + FIELD_SEPARATOR + status + FIELD_SEPARATOR
                    + escape(deadline.getDescription()) + FIELD_SEPARATOR + deadline.getBy()
                    + formatSnoozeDate(task);
        }
        if (task instanceof Event event) {
            return "E" + FIELD_SEPARATOR + status + FIELD_SEPARATOR
                    + escape(event.getDescription()) + FIELD_SEPARATOR
                    + event.getFrom() + FIELD_SEPARATOR + event.getTo() + formatSnoozeDate(task);
        }
        return "T" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + escape(task.getDescription())
                + formatSnoozeDate(task);
    }

    private Task parseTask(String line) {
        List<String> fields = splitFields(line);
        if (fields.size() < 3) {
            throw new IllegalArgumentException("Too few fields");
        }

        Task task;
        switch (fields.get(0)) {
        case "T":
            requireFieldCount(fields, 3, 4);
            task = new Todo(requireDescription(fields.get(2)));
            break;
        case "D":
            requireFieldCount(fields, 4, 5);
            task = new Deadline(requireDescription(fields.get(2)), LocalDate.parse(fields.get(3)));
            break;
        case "E":
            requireFieldCount(fields, 5, 6);
            LocalDate from = LocalDate.parse(fields.get(3));
            LocalDate to = LocalDate.parse(fields.get(4));
            if (!to.isAfter(from)) {
                throw new IllegalArgumentException("Event must end after it starts");
            }
            task = new Event(requireDescription(fields.get(2)), from, to);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (fields.get(1).equals("1")) {
            task.markAsDone();
        } else if (!fields.get(1).equals("0")) {
            throw new IllegalArgumentException("Invalid task status");
        }
        restoreSnoozeDate(task, fields);
        return task;
    }

    private String formatSnoozeDate(Task task) {
        return task.getSnoozedUntil() == null ? "" : FIELD_SEPARATOR + task.getSnoozedUntil();
    }

    private void restoreSnoozeDate(Task task, List<String> fields) {
        int snoozeFieldIndex = switch (fields.get(0)) {
        case "T" -> 3;
        case "D" -> 4;
        case "E" -> 5;
        default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (fields.size() > snoozeFieldIndex) {
            task.snoozeUntil(LocalDate.parse(fields.get(snoozeFieldIndex)));
        }
    }

    private void requireFieldCount(List<String> fields, int minimumCount, int maximumCount) {
        if (fields.size() < minimumCount || fields.size() > maximumCount) {
            throw new IllegalArgumentException("Unexpected number of fields");
        }
    }

    private String requireDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank");
        }
        return description;
    }

    private void replaceDataFile(Path temporaryFile, Path targetPath) throws IOException {
        try {
            Files.move(temporaryFile, targetPath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private List<String> splitFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
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
            throw new IllegalArgumentException("Incomplete escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }
}
