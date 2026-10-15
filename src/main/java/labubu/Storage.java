package labubu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving tasks in the application's save file.
 */
public class Storage {
    private final Path filePath;
    /**
     * Creates a storage handler for the specified file.
     *
     * @param filePath Path to the save file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves the taskList variable as a file.
     *
     * @param taskList Task list.
     */
    public void saveTasks(TaskList taskList) {
        List<String> lines = taskList.toSaveFormat();

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.out.println("Oops! Unable to save tasks.");
        }
    }

    /**
     * Loads tasks from the save file.
     *
     * @return Tasks reconstructed from the save file.
     * @throws IOException If the save file cannot be read.
     * @throws IllegalArgumentException If the save file contains invalid data.
     */
    public List<Task> loadTasks() throws IOException, IllegalArgumentException {
        if (Files.notExists(filePath)) { // If save file doesn't exist
            System.out.println("Hmm, save file not found. Starting with an empty task list.");
            return new ArrayList<>();
        }

        List<String> lines;

        try { // If save file cannot be read
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.out.println("Oops! Unable to load the save file. Starting with an empty task list.");
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            tasks.add(loadTask(line));
        }

        if (!tasks.isEmpty()) {
            System.out.println("Got it! Save file loaded.");
        }

        return tasks;
    }

    private Task loadTask(String line) throws IOException {
        String[] parts = line.split("\\|", -1);
        Task.Status status;
        Task task;

        try {
            validateParts(parts);
            status = parseStatus(parts[1]);
            switch (parts[0]) {
                case "T":
                    task = new ToDo(parts[2]);
                    break;
                case "D":
                    task = new Deadline(parts[2], LocalDateTime.parse(parts[3]));
                    break;
                case "E":
                    task = new Event(
                            parts[2],
                            LocalDateTime.parse(parts[3]),
                            LocalDateTime.parse(parts[4])
                    );
                    break;
                default:
                    Files.deleteIfExists(filePath);
                    throw new IllegalArgumentException("Unknown task marker. Resetting save file.");
            }
        } catch (DateTimeParseException e) {
            Files.deleteIfExists(filePath);
            throw new IllegalArgumentException("Invalid date/time format. Resetting save file.", e);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            Files.deleteIfExists(filePath);
            String message = e.getMessage();
            if (message == null || !message.startsWith("Unknown task marker")) {
                message = "Malformed save file. Resetting save file.";
            }
            throw new IllegalArgumentException(message, e);
        }

        task.setStatus(status);
        return task;
    }

    private void validateParts(String[] parts) {
        int expectedPartCount;
        switch (parts[0]) {
            case "T":
                expectedPartCount = 3;
                break;
            case "D":
                expectedPartCount = 4;
                break;
            case "E":
                expectedPartCount = 5;
                break;
            default:
                throw new IllegalArgumentException("Unknown task marker. Resetting save file.");
        }

        if (parts.length != expectedPartCount) {
            throw new IllegalArgumentException("Malformed save file. Resetting save file.");
        }
    }

    private Task.Status parseStatus(String status) {
        switch (status) {
            case "X":
                return Task.Status.COMPLETED;
            case "-":
                return Task.Status.IN_PROGRESS;
            case " ":
                return Task.Status.INCOMPLETE;
            default:
                return Task.Status.valueOf(status);
        }
    }
}
