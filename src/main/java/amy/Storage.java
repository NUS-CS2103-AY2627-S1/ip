package amy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import amy.task.Deadline;
import amy.task.Event;
import amy.task.Task;
import amy.task.Todo;

/**
 * Saves Amy's task list to a file on the hard disk.
 */
public class Storage {
    private static final String DEFAULT_DIRECTORY = ".amy";
    private static final String DEFAULT_FILE_NAME = "amy.txt";
    private final Path saveFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath path of the file used to save and load tasks
     */
    public Storage(String filePath) {
        saveFile = Path.of(filePath);
    }

    /**
     * Returns the save path shared by GUI launches from different working directories.
     *
     * @return the user's persistent Amy save path
     */
    public static String getDefaultSavePath() {
        return Path.of(System.getProperty("user.home"), DEFAULT_DIRECTORY, DEFAULT_FILE_NAME).toString();
    }

    /**
     * Rewrites the save file with the current task list.
     *
     * @param tasks the tasks to save
     * @throws IOException if the data directory or save file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        // Saving a null collection would silently produce invalid application state.
        assert tasks != null : "Tasks to save must not be null";
        Files.createDirectories(saveFile.getParent());
        ArrayList<String> savedTasks = tasks.stream()
                .filter(Objects::nonNull)
                .map(Storage::serializeTask)
                .collect(Collectors.toCollection(ArrayList::new));

        Path temporaryFile = Files.createTempFile(saveFile.getParent(), "amy-", ".tmp");
        try {
            Files.write(temporaryFile, savedTasks, StandardCharsets.UTF_8);
            replaceSaveFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads all tasks from the save file, or returns an empty list when it does not exist.
     *
     * @return the tasks stored on the hard disk
     * @throws IOException if the save file cannot be read
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(saveFile)) {
            return tasks;
        }

        for (String savedTask : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
            if (savedTask.isBlank()) {
                continue;
            }
            try {
                String[] parts = savedTask.split(" \\| ", -1);
                Task task = createTask(parts);
                if (task != null) {
                    tasks.add(task);
                }
            } catch (RuntimeException e) {
                System.out.println("Skipping corrupted save entry: " + savedTask);
            }
        }
        return tasks;
    }

    /**
     * Recreates one task from a line in Amy's save-file format.
     *
     * @param parts the pipe-separated fields from one saved line
     * @return the recreated task, or null when the line is incomplete or invalid
     */
    private static Task createTask(String[] parts) {
        if (parts.length < 3 || !parts[1].equals("0") && !parts[1].equals("1")) {
            return null;
        }

        Task task;
        switch (parts[0]) {
        case "T":
            if (parts.length != 3 || parts[2].isEmpty()) {
                return null;
            }
            task = new Todo(unescape(parts[2]));
            break;
        case "D":
            if (parts.length != 4 || parts[2].isEmpty() || parts[3].isEmpty()) {
                return null;
            }
            task = new Deadline(unescape(parts[2]), unescape(parts[3]));
            break;
        case "E":
            if (parts.length != 5 || parts[2].isEmpty() || parts[3].isEmpty()
                    || parts[4].isEmpty()) {
                return null;
            }
            task = new Event(unescape(parts[2]), unescape(parts[3]), unescape(parts[4]));
            break;
        default:
            return null;
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a task into one escaped, pipe-separated save-file line.
     *
     * @param task the task to serialize
     * @return the save-file line for the task
     */
    private static String serializeTask(Task task) {
        String header = task.getTypeIcon() + " | " + (task.isDone() ? "1" : "0")
                + " | " + escape(task.getDescription());
        if (task instanceof Deadline deadline) {
            return header + " | " + escape(deadline.getBy().format(Deadline.INPUT_FORMAT));
        }
        if (task instanceof Event event) {
            return header + " | " + escape(event.getFrom()) + " | " + escape(event.getTo());
        }
        return header;
    }

    /**
     * Replaces the save file without exposing a partly-written file to a later startup.
     *
     * @param temporaryFile the completed temporary save file
     * @throws IOException if the save file cannot be replaced
     */
    private void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Escapes backslashes and the pipe delimiter within a saved field.
     *
     * @param text the field value to escape
     * @return an escaped field value
     */
    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace(" | ", " \\p ");
    }

    /**
     * Restores escaped backslashes and pipe delimiters in a saved field.
     *
     * @param text the escaped field value
     * @return the original field value
     */
    private static String unescape(String text) {
        StringBuilder unescaped = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (character == '\\' && index + 1 < text.length()) {
                char escapedCharacter = text.charAt(++index);
                if (escapedCharacter == '\\') {
                    unescaped.append('\\');
                } else if (escapedCharacter == 'p') {
                    unescaped.append('|');
                } else {
                    unescaped.append('\\').append(escapedCharacter);
                }
            } else {
                unescaped.append(character);
            }
        }
        return unescaped.toString();
    }
}
