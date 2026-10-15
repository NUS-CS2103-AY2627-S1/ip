package otaku.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import otaku.exception.OtakuException;
import otaku.task.Deadline;
import otaku.task.Event;
import otaku.task.Task;
import otaku.task.Todo;

/** Loads and saves Otaku tasks in a local data file. */
public class Storage {
    private static final String FIELD_SEPARATOR = "\\|";

    private final Path filePath;

    /** Creates storage backed by the given file path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, returning an empty list when the data file does not exist yet.
     *
     * @throws OtakuException if the file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws OtakuException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(decodeTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new OtakuException("I couldn't read saved tasks from " + filePath + ".");
        }
    }

    /** Saves all tasks, creating the data directory and file when necessary. */
    public void save(List<Task> tasks) throws OtakuException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(encodeTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new OtakuException("I couldn't save tasks to " + filePath + ".");
        }
    }

    /** Converts a task to a delimiter-safe record. */
    private String encodeTask(Task task) throws OtakuException {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T|" + status + "|" + encode(task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return "D|" + status + "|" + encode(task.getDescription()) + "|" + encode(deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return "E|" + status + "|" + encode(task.getDescription()) + "|"
                    + encode(event.getFrom().toString()) + "|" + encode(event.getTo().toString());
        }
        throw new OtakuException("I couldn't save an unsupported task type.");
    }

    /** Converts one saved record back into its task object. */
    private Task decodeTask(String line, int lineNumber) throws OtakuException {
        try {
            String[] fields = line.split(FIELD_SEPARATOR, -1);
            Task task;
            if (fields[0].equals("T") && fields.length == 3) {
                task = new Todo(decode(fields[2]));
            } else if (fields[0].equals("D") && fields.length == 4) {
                task = new Deadline(decode(fields[2]), LocalDate.parse(decode(fields[3])));
            } else if (fields[0].equals("E") && fields.length == 5) {
                LocalDate from = LocalDate.parse(decode(fields[3]));
                LocalDate to = LocalDate.parse(decode(fields[4]));
                if (!to.isAfter(from)) {
                    throw new IllegalArgumentException();
                }
                task = new Event(decode(fields[2]), from, to);
            } else {
                throw new IllegalArgumentException();
            }
            if (fields[1].equals("1")) {
                task.markAsDone();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException();
            }
            return task;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new OtakuException("Saved task data is invalid on line " + lineNumber + ".");
        }
    }

    /** Encodes user-entered text so separators and Unicode characters remain safe. */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes text stored in a task record. */
    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
