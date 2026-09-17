package baemax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists the task list to a plain-text file so tasks survive between runs
 * of the chatbot. The file path is relative to the project root and is stored
 * in an OS-independent way.
 */
public class Storage {
    /** Location of the save file, relative to the project root. */
    private final Path file;

    /**
     * Creates a storage handler for the given file path.
     *
     * @param filePath path to the save file, relative to the project root,
     *     using {@code /} as the separator (e.g. {@code data/baemax.txt})
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the saved tasks from disk, in file order. Returns an empty list
     * when the save file does not exist yet, which is the normal case the
     * first time the chatbot runs on a computer. Lines that are not in the
     * expected format are skipped with a warning so that one bad line does
     * not discard the rest of the list.
     *
     * @return the saved tasks, or an empty list when nothing is saved
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseTask(line));
                } catch (BaemaxException exception) {
                    System.out.println("Baemax skipped an unreadable line in the save file: " + line);
                }
            }
        } catch (IOException exception) {
            System.out.println("Baemax could not read the save file. Starting with an empty list.");
        }
        return tasks;
    }

    /**
     * Writes every task to disk, one task per line, creating the data folder
     * and file if they do not exist yet. A failure to write is reported to the
     * user but does not stop the chatbot.
     *
     * @param tasks the tasks to save, in list order
     */
    public void save(List<Task> tasks) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.toFileFormat()).append(System.lineSeparator());
            }
            Files.writeString(file, content.toString());
        } catch (IOException exception) {
            System.out.println("Baemax could not save your tasks: " + exception.getMessage());
        }
    }

    /**
     * Rebuilds a task from one line of the save file.
     *
     * @param line a line produced by {@link Task#toFileFormat()}
     * @return the task described by the line
     * @throws BaemaxException when the line is missing fields or uses an
     *     unknown task type
     */
    private static Task parseTask(String line) throws BaemaxException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new BaemaxException("A saved task needs a type, a status, and a description.");
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = createTask(type, description, parts);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates the right task subtype for a save-file line, checking that the
     * extra fields each subtype needs are present.
     *
     * @param type the one-letter type tag ({@code T}, {@code D}, or {@code E})
     * @param description the task description
     * @param parts every field on the line, split on {@code |}
     * @return the reconstructed task
     * @throws BaemaxException when a required field is missing or the type is
     *     unknown
     */
    private static Task createTask(String type, String description, String[] parts) throws BaemaxException {
        if (type.equals("T")) {
            return new Todo(description);
        }
        if (type.equals("D")) {
            if (parts.length < 4) {
                throw new BaemaxException("A saved deadline needs a due date.");
            }
            return new Deadline(description, TaskDate.fromStorage(parts[3]));
        }
        if (type.equals("E")) {
            if (parts.length < 5) {
                throw new BaemaxException("A saved event needs a start and an end date.");
            }
            return new Event(description, TaskDate.fromStorage(parts[3]), TaskDate.fromStorage(parts[4]));
        }
        throw new BaemaxException("Unknown task type in the save file: " + type);
    }
}
