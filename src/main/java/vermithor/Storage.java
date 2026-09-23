package vermithor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Saves Vermithor tasks to, and loads them from, a local text file. */
public class Storage {
    private final Path filePath;

    /** Creates storage at the given relative or absolute path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, returning an empty list when the file does not exist yet.
     * Individual corrupted records are skipped rather than failing the entire load,
     * so one bad line does not discard every other saved task.
     */
    public List<Task> load() throws VermithorException {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new VermithorException("I could not load your saved tasks.");
        }
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(deserialize(line));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                // Skip this one corrupted record; keep the rest of the saved tasks.
            }
        }
        return tasks;
    }

    /** Saves all tasks, creating the parent directory when it does not exist. */
    public void save(List<Task> tasks) throws VermithorException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialize(task));
        }
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new VermithorException("I could not save your tasks.");
        }
    }

    /** Converts one task into its saved text representation. */
    private String serialize(Task task) {
        String done = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D|" + done + "|" + escape(task.getDescription()) + "|" + deadline.getBy();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E|" + done + "|" + escape(task.getDescription())
                    + "|" + escape(event.getFrom()) + "|" + escape(event.getTo());
        }
        return "T|" + done + "|" + escape(task.getDescription());
    }

    /** Recreates one task from its saved text representation. */
    private Task deserialize(String line) {
        String[] fields = splitFields(line);
        if (fields.length < 3 || ("D".equals(fields[0]) && fields.length != 4)
                || ("E".equals(fields[0]) && fields.length != 5)) {
            throw new IllegalArgumentException("Malformed task record");
        }
        Task task;
        switch (fields[0]) {
        case "T":
            task = new ToDo(unescape(fields[2]));
            break;
        case "D":
            task = new Deadline(unescape(fields[2]), LocalDate.parse(fields[3]));
            break;
        case "E":
            task = new Event(unescape(fields[2]), unescape(fields[3]), unescape(fields[4]));
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }
        task.setDone("1".equals(fields[1]));
        return task;
    }

    /**
     * Splits a saved line on the {@code |} field delimiter, treating a backslash-escaped
     * pipe as literal content rather than a field boundary.
     */
    private String[] splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaping) {
                current.append(c);
                escaping = false;
            } else if (c == '\\') {
                current.append(c);
                escaping = true;
            } else if (c == '|') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    /** Escapes characters that would otherwise be misread as the field delimiter or a line break. */
    private String escape(String field) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            switch (c) {
            case '\\':
                result.append("\\\\");
                break;
            case '|':
                result.append("\\|");
                break;
            case '\n':
                result.append("\\n");
                break;
            case '\r':
                result.append("\\r");
                break;
            default:
                result.append(c);
            }
        }
        return result.toString();
    }

    /** Reverses {@link #escape(String)}. */
    private String unescape(String field) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            if (escaping) {
                switch (c) {
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                default:
                    result.append(c);
                }
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
