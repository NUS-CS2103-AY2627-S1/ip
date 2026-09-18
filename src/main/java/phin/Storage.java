package phin;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks as UTF-8 text, using URL encoding to preserve separators in task fields.
 */
public class Storage {
    /**
     * Creates storage using the default data file location.
     */
    public Storage() {
        this(Path.of("data", "phin.txt"));
    }

    /**
     * Creates storage at a supplied location for isolated tests.
     *
     * @param file Data file to read and replace.
     */
    Storage(Path file) {
        this.file = file;
    }

    /**
     * Location of the saved tasks, relative to the working directory.
     */
    private final Path file;

    /**
     * Loads saved tasks, rejecting corrupt data to prevent a later accidental overwrite.
     *
     * @return Tasks in saved order, or an empty list if the data file does not exist.
     * @throws IOException If the file cannot be read or contains an invalid task.
     * @throws SecurityException If access to the data file is denied.
     */
    public ArrayList<Task> load() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(decode(lines.get(i)));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid task at line " + (i + 1), exception);
            }
        }
        return tasks;
    }

    /**
     * Writes a complete replacement before replacing the saved task file.
     *
     * @param tasks Tasks to save in their current order.
     * @throws IOException If creating, writing, replacing, or cleaning up a file fails.
     * @throws SecurityException If access to a required file or directory is denied.
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(file.getParent());
        Path temporary = Files.createTempFile(file.getParent(), "phin-", ".tmp");
        try {
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                String type = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
                String line = type + "|" + (task.isDone ? "1" : "0") + "|" + encode(task.description);
                if (task instanceof Deadline deadline) {
                    line += "|" + encode(deadline.by.toString());
                } else if (task instanceof Event event) {
                    line += "|" + encode(event.from.toString()) + "|" + encode(event.to.toString());
                }
                lines.add(line);
            }
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Encodes field text so pipes and line breaks cannot become record separators.
     *
     * @param text Field value to encode.
     * @return The URL-encoded UTF-8 field value.
     */
    private static String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    /**
     * Validates and reconstructs one saved task, including its completion status.
     *
     * @param line Encoded task record from the data file.
     * @return The reconstructed task.
     * @throws IllegalArgumentException If the record, encoded fields, or dates are invalid.
     */
    private static Task decode(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid record or status");
        }
        for (int i = 2; i < fields.length; i++) {
            fields[i] = URLDecoder.decode(fields[i], StandardCharsets.UTF_8);
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("Empty task field");
            }
        }
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(fields[2]);
        } else if (fields[0].equals("D") && fields.length == 4) {
            task = new Deadline(fields[2], fields[3]);
        } else if (fields[0].equals("E") && fields.length == 5) {
            task = new Event(fields[2], fields[3], fields[4]);
        } else {
            throw new IllegalArgumentException("Invalid task type or field count");
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
