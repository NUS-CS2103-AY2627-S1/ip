package sunny.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import sunny.utility.DateParser;
import task.Deadline;
import task.Event;
import task.Task;
import task.ToDo;

/**
 * Handles storing and retrieving data related to the taskboard.
 * <p>This class follows the singleton pattern and can be accessed
 * through {@link #getInstance()}.</p>
 */
public class Storage {
    private Storage() {}
    private static class Holder {
        private static final Storage INSTANCE = new Storage();
    }
    /**
     * Returns the singleton instance of Storage.
     *
     * @return the Storage singleton instance
     */
    public static Storage getInstance() {
        return Holder.INSTANCE;
    }

    private static String filePath = "./data/Sunny'sAmazingTaskboard(ForHerBothersomeUser).txt";
    static DateParser dateParser = DateParser.getInstance();

    /**
     * Writes each task in the taskboard to the designated filepath based on the
     * format specified in convertTaskToFileFormat.
     * If an IOException occurs when writing, a message will be printed.
     *
     * @param tasks the taskboard as an ArrayList<Task> to write into a file.
     */
    public void saveTasks(ArrayList<Task> tasks) throws AccessDeniedException {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            for (Task task : tasks) {
                writer.write(convertTaskToFileFormat(task));
                writer.newLine();
            }
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("What do you mean 'Access Denied?' I can't write to your data file!");
        } catch (IOException e) {
            System.out.println("Huh, there's some error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Returns the task object formatted as a string for writing into a file.
     *
     * @param task the task object to be formatted.
     * @return a String that is the formatted task object.
     */
    private static String convertTaskToFileFormat(Task task) {
        if (task instanceof ToDo) {
            return "T | " + task.getDesc() + " | " + (task.isDone() ? "1" : "0");
        } else if (task instanceof Deadline deadline) {
            return "D | " + deadline.getDesc() + deadline.getFileFormat();
        } else if (task instanceof Event event) {
            return "E | " + event.getDesc() + event.getFileFormat();
        }
        assert false : "Tasks must be ToDo, Deadline or Event";
        return "";
    }

    /**
     * Loads pre-existing tasks from the data file into the given task list.
     * It creates and appends task objects based on convertTaskFromFileFormat.
     * If the data file is corrupted, it is deleted using deleteDateFile() and an empty task list is used.
     * If an IOException occurs while reading the file, an error message is printed.
     *
     * @param tasks the taskboard as an ArrayList<Task> to be written to from a file.
     */
    public void loadTasks(ArrayList<Task> tasks) {
        ArrayList<Task> loadedTasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Task task = convertTaskFromFileFormat(line);
                loadedTasks.add(task);
            }
            tasks.addAll(loadedTasks);// Only modify the real task list if the ENTIRE file was valid

        } catch (FileNotFoundException e) {
            // Fine to be empty on first time
        } catch (IllegalArgumentException e) {
            System.out.println("Data file is corrupted.");
            System.out.println("Starting with an empty task list.");

            deleteDataFile();
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    /**
     * Reads a String and returns a task based on the formatting described in the method.
     *
     * @param line the String to be read and formatted into a task object.
     * @return the task the String is formatted into.
     * @throws IllegalArgumentException if the line has an invalid or unknown task format.
     */
    private static Task convertTaskFromFileFormat(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty task line");
        }

        String type = parts[0];
        return switch (type) {
            case "T" -> convertToDoFromFileFormat(parts);
            case "D" -> convertDeadlineFromFileFormat(parts);
            case "E" -> convertEventFromFileFormat(parts);
            default -> throw new IllegalArgumentException("Unknown task type");
        };
    }

    private static Event convertEventFromFileFormat(String[] parts) {
        checkIfValidFormat(parts, 5);

        Event event = parseDatesAndCreateEvent(parts);

        checkAndMark(event, parts, 4);

        return event;
    }

    private static Event parseDatesAndCreateEvent(String[] parts) {
        Optional<LocalDateTime> startOpt = dateParser.parse(parts[2]);
        Optional<LocalDateTime> endOpt = dateParser.parse(parts[3]);

        if (startOpt.isPresent() && endOpt.isPresent()) {
            LocalDateTime startActual = startOpt.get();
            LocalDateTime endActual = endOpt.get();
            return new Event(parts[1], startActual, endActual, "", "");
        } else if (startOpt.isPresent()) {
            LocalDateTime startActual = startOpt.get();
            return new Event(parts[1], startActual, null, "", parts[3]);
        } else if (endOpt.isPresent()) {
            LocalDateTime endActual = endOpt.get();
            return new Event(parts[1], null, endActual, parts[2], "");
        } else {
            return new Event(parts[1], null, null, parts[2], parts[3]);
        }
    }

    private static Deadline convertDeadlineFromFileFormat(String[] parts) {
        checkIfValidFormat(parts, 4);

        Optional<LocalDateTime> dateOpt = dateParser.parse(parts[2]);

        Deadline deadline = parseDatesAndCreateDeadline(parts, dateOpt);

        checkAndMark(deadline, parts, 3);

        return deadline;
    }

    private static Deadline parseDatesAndCreateDeadline(String[] parts, Optional<LocalDateTime> dateOpt) {
        if (dateOpt.isPresent()) {
            LocalDateTime actualDate = dateOpt.get();
            return new Deadline(parts[1], actualDate, "");
        } else {
            return new Deadline(parts[1], null, parts[2]);
        }
    }

    private static ToDo convertToDoFromFileFormat(String[] parts) {
        checkIfValidFormat(parts, 3);
        ToDo todo = new ToDo(parts[1]);

        checkAndMark(todo, parts, 2);
        return todo;
    }

    private static void checkAndMark(Task task, String[] parts, int index) {
        if (parts[index].equals("1")) {
            task.mark();
        }
    }

    private static void checkIfValidFormat(String[] parts, int expectedPartsLength) {
        if (parts.length != expectedPartsLength) {
            switch(parts[0]) {
                case "T" -> throw new IllegalArgumentException("Invalid ToDo format");
                case "D" -> throw new IllegalArgumentException("Invalid Deadline format");
                case "E" -> throw new IllegalArgumentException("Invalid Event format");
                default -> throw new IllegalArgumentException("Invalid format");
            }
        }
        if (!parts[expectedPartsLength - 1].equals("0") && !parts[expectedPartsLength - 1].equals("1")) {
            throw new IllegalArgumentException("Invalid completion status");
        }
    }

    /**
     * Creates a folder for a file to contain tasks.
     * If it cannot be created, an error message is printed.
     */
    public void createDataDirectory() {
        try {
            Path dataDirectory = Paths.get("./data");
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            System.out.println("Could not create data directory: " + e);
        }
    }

    /**
     * Removes the file at the specified path.
     * If it cannot be removed, an error message is printed.
     */
    private static void deleteDataFile() {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Could not delete corrupted data file: " + e);
        }
    }

    /**
     * Sets a filepath to be used for testing purposes.
     *
     * @param filePath the path that a file will be created for storing task objects.
     */
    public void setFilePath(String filePath) {
        assert filePath != null : "filePath cannot be null";
        Storage.filePath = filePath;
    }
}
