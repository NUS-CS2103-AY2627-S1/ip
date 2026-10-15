package carlo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import carlo.exception.CarloException;
import carlo.task.Deadline;
import carlo.task.Event;
import carlo.task.Task;
import carlo.task.Todo;

/**
 * Loads tasks from, and saves tasks to, a file on the hard disk so that
 * a user's task list persists between runs of the application.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage that reads from and writes to the given path.
     *
     * @param filePath the relative path to the save file, e.g. {@code ./data/carlo.txt}
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads the saved tasks from disk.
     *
     * <p>If the data file or its parent folder does not exist yet -- for
     * example, when the application is run for the first time -- an empty
     * list is returned instead of throwing an error. Any line that does not
     * match the expected save format is skipped and reported, rather than
     * causing the whole load to fail.
     *
     * @return the list of tasks read from disk, or an empty list if none exist
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        assert tasks != null : "The load operation must use a task list";

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(parseLine(line));
                } catch (CarloException e) {
                    System.out.println(" I couldn't understand a saved task on line "
                            + (i + 1) + ", so I'm skipping it: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(" Uh oh, I couldn't read your saved tasks! Starting with an empty list...");
        }

        return tasks;
    }

    /**
     * Saves the given tasks to disk, overwriting any previous save file.
     *
     * <p>The parent folder of the save file is created automatically if it
     * does not already exist.
     *
     * @param tasks the tasks to save
     */
    public void save(List<Task> tasks) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.toFileFormat()).append(System.lineSeparator());
            }

            Files.writeString(filePath, content.toString());
        } catch (IOException e) {
            System.out.println(" Uh oh, I couldn't save your tasks to disk!");
        }
    }

    /**
     * Parses one line of the save file into a task. This is the reverse of
     * {@link Task#toFileFormat()}, which is also what makes undo possible:
     * a task list saved as lines of text can be rebuilt with this method.
     *
     * @param line the raw line read from the save file
     * @return the task described by the line
     * @throws CarloException if the line does not match the expected format
     */
    public static Task parseLine(String line) throws CarloException {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new CarloException("not enough fields");
        }

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();

        boolean isDone;
        if (doneFlag.equals("1")) {
            isDone = true;
        } else if (doneFlag.equals("0")) {
            isDone = false;
        } else {
            throw new CarloException("invalid completion flag '" + doneFlag + "'");
        }

        if (description.isEmpty()) {
            throw new CarloException("missing description");
        }

        Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> {
                if (parts.length < 4 || parts[3].trim().isEmpty()) {
                    throw new CarloException("missing deadline time");
                }
                yield new Deadline(description, parts[3].trim());
            }
            case "E" -> {
                if (parts.length < 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                    throw new CarloException("missing event start/end time");
                }
                yield new Event(description, parts[3].trim(), parts[4].trim());
            }
            default -> throw new CarloException("unknown task type '" + type + "'");
        };

        if (isDone) {
            task.markAsDone();
        }

        assert task != null : "A valid save-file line must produce a task";
        return task;
    }
}
