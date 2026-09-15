package tem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads and saves Tem's task list using a text file under the project folder.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage helper for the given relative file path.
     *
     * @param filePath relative path from the project root, for example {@code data/tem.txt}.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Reads tasks from disk. Missing folders or files are treated as an empty task list.
     * Lines that do not match the expected format are skipped.
     *
     * @return tasks loaded from the save file
     * @throws TemException if the file exists but cannot be read
     */
    public List<Task> load() throws TemException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(filePath).stream()
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException exception) {
            throw new TemException("Could not read saved tasks from " + filePath + ".");
        }
    }

    /**
     * Writes the full task list to disk, creating parent folders when needed.
     *
     * @param tasks tasks to save.
     * @throws TemException if the file or its parent folder cannot be written
     */
    public void save(List<Task> tasks) throws TemException {
        assert tasks != null : "Tasks to save should not be null";
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.stream()
                    .peek(task -> {
                        assert task != null : "Saved task list should not contain null entries";
                    })
                    .map(Task::toStorageString)
                    .collect(Collectors.toList());
            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new TemException("Could not save tasks to " + filePath + ".");
        }
    }

    /**
     * Converts one storage line into a task, or {@code null} when the line is blank or corrupt.
     *
     * @param line raw line from the save file.
     * @return parsed task, or {@code null} if the line should be ignored
     */
    private Task parseLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = splitFields(trimmed);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = unescapeStorageField(parts[2]);
        if (description.isEmpty()) {
            return null;
        }

        Task task;
        switch (type) {
            case "T":
                if (parts.length != 3) {
                    return null;
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 || parts[3].isEmpty()) {
                    return null;
                }
                try {
                    task = new Deadline(description, LocalDate.parse(unescapeStorageField(parts[3])));
                } catch (DateTimeParseException exception) {
                    return null;
                }
                break;
            case "E":
                if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                    return null;
                }
                task = new Event(description, unescapeStorageField(parts[3]), unescapeStorageField(parts[4]));
                break;
            default:
                return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a storage line into fields, respecting escaped pipe characters.
     *
     * @param line trimmed storage line.
     * @return field values without surrounding whitespace
     */
    private static String[] splitFields(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\' && i + 1 < line.length() && line.charAt(i + 1) == '|') {
                current.append('\\').append('|');
                i++;
                continue;
            }
            if (character == '|') {
                parts.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(character);
        }
        parts.add(current.toString().trim());
        return parts.toArray(new String[0]);
    }

    /**
     * Reverses {@link Task#escapeStorageField(String)} for one loaded field.
     *
     * @param field escaped field text from disk.
     * @return original field text
     */
    private static String unescapeStorageField(String field) {
        StringBuilder unescaped = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (character == '\\' && i + 1 < field.length()) {
                char next = field.charAt(i + 1);
                switch (next) {
                    case '\\':
                        unescaped.append('\\');
                        break;
                    case '|':
                        unescaped.append('|');
                        break;
                    case 'n':
                        unescaped.append('\n');
                        break;
                    case 'r':
                        unescaped.append('\r');
                        break;
                    default:
                        unescaped.append(next);
                }
                i++;
            } else {
                unescaped.append(character);
            }
        }
        return unescaped.toString();
    }
}
