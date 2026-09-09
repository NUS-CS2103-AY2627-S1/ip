package trayce.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import trayce.task.Deadline;
import trayce.task.Event;
import trayce.task.Task;

/** Saves Trayce tasks to, and loads them from, a file in the project directory. */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "trayce.txt");

    /**
     * Creates a new Storage instance.
     */
    public Storage() {
    }

    /**
     * Loads the saved tasks. A missing data file is treated as an empty task list.
     *
     * @return the tasks stored in the data file
     * @throws IOException if an existing data file cannot be read
     */
    public List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
            Task task = readTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with the current task list, creating its folder if needed.
     *
     * @param tasks the tasks to save
     * @throws IOException if the data cannot be written
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(writeTask(task));
        }
        Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
    }

    /**
     * Converts one task into a tab-separated line for the data file.
     *
     * @param task the task to serialize
     * @return the serialized tab-separated line representation of the task
     */
    private String writeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D\t" + status + "\t" + escape(task.getDescription())
                    + "\t" + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E\t" + status + "\t" + escape(task.getDescription())
                    + "\t" + event.getFrom() + "\t" + event.getTo();
        }
        return "T\t" + status + "\t" + escape(task.getDescription());
    }

    /**
     * Recreates one task from a tab-separated data-file line.
     *
     * @param line the serialized line containing the task data
     * @return the deserialized task object, or {@code null} if parsing failed
     */
    private Task readTask(String line) {
        String[] parts = line.split("\\t", -1);
        if (parts.length < 3) {
            return null;
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3) {
            task = new Task(unescape(parts[2]));
        } else if (parts[0].equals("D") && parts.length == 4) {
            task = new Deadline(unescape(parts[2]), LocalDate.parse(parts[3]));
        } else if (parts[0].equals("E") && parts.length == 5) {
            task = new Event(unescape(parts[2]), LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
        } else {
            return null;
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Escapes characters that would otherwise interfere with the one-line file format.
     * Special characters handled: backslash, tab, newline.
     *
     * @param text the raw text string to escape
     * @return the escaped string safe for storage
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    /**
     * Reverses {@link #escape(String)} while retaining ordinary backslashes.
     *
     * @param text the escaped text string to unescape
     * @return the plain text string after unescaping special characters
     */
    private String unescape(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == '\\' && i + 1 < text.length()) {
                char escapedCharacter = text.charAt(i + 1);
                if (escapedCharacter == 't') {
                    result.append('\t');
                    i++;
                    continue;
                }
                if (escapedCharacter == 'n') {
                    result.append('\n');
                    i++;
                    continue;
                }
                if (escapedCharacter == '\\') {
                    result.append('\\');
                    i++;
                    continue;
                }
            }
            result.append(character);
        }
        return result.toString();
    }
}
