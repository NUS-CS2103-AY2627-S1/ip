package baby;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving tasks to the data file and loading tasks from it.
 */
public class Storage {
    private static final String LINE = "____________________________________________________________";
    private static final String DEFAULT_FILE_PATH = "./data/baby.txt";

    /**
     * Saves the current tasks to the data file.
     *
     * @param tasks The tasks to save.
     * @return True if all tasks were saved successfully.
     */
    public static boolean saveTasks(ArrayList<Task> tasks) {
        try {
            return saveTasks(tasks, getDataPath());
        } catch (InvalidPathException e) {
            return false;
        }
    }

    static boolean saveTasks(ArrayList<Task> tasks, Path dataPath) {
        try {
            Path dataFolder = dataPath.getParent();
            if (dataFolder != null) {
                Files.createDirectories(dataFolder);
            }

            List<String> lines = tasks.stream().map(Task::toFileString).toList();
            Files.write(dataPath, lines);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return The valid saved tasks, or an empty list if the data file cannot be read.
     */
    public static ArrayList<Task> loadTasks() {
        try {
            return loadTasks(getDataPath());
        } catch (InvalidPathException e) {
            printError("Oh snow! The data file path is invalid, so I couldn't load your tasks.");
            return new ArrayList<>();
        }
    }

    static ArrayList<Task> loadTasks(Path dataPath) {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataPath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(dataPath);
            for (int i = 0; i < lines.size(); i++) {
                try {
                    tasks.add(parseTask(lines.get(i)));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    printError("Oh snow! I skipped corrupted data on line " + (i + 1) + ".");
                }
            }
        } catch (IOException | SecurityException e) {
            printError("Oh snow! I couldn't read the data file, so I started with an empty list.");
        }

        return tasks;
    }

    private static Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new IllegalArgumentException("Missing task fields");
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new IllegalArgumentException("Invalid completion status");
        }

        Task task;
        switch (parts[0]) {
        case "T":
            requireFieldCount(parts, 3);
            task = new Todo(parts[2]);
            break;
        case "D":
            requireFieldCount(parts, 4);
            task = new Deadline(parts[2], Parser.parseDateTime(parts[3]));
            break;
        case "E":
            requireFieldCount(parts, 5);
            LocalDateTime from = Parser.parseDateTime(parts[3]);
            LocalDateTime to = Parser.parseDateTime(parts[4]);
            if (!to.isAfter(from)) {
                throw new IllegalArgumentException("Event end time is not after start time");
            }
            task = new Event(parts[2], from, to);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private static void requireFieldCount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect number of task fields");
        }
    }

    private static Path getDataPath() {
        return Path.of(System.getProperty("baby.filePath", DEFAULT_FILE_PATH));
    }

    private static void printError(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}
