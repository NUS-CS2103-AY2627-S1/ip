package yokohama.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import yokohama.exceptions.YokohamaException;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.TaskType;
import yokohama.task.Todo;

/**
 * Reads tasks from and writes tasks to the application's data file.
 */
public class Storage {
    /**
     * Writes the specified tasks to the data file at the given path.
     *
     * @param filePath Path of the data file.
     * @param tasks Tasks to write.
     * @throws IOException If the file cannot be created or written.
     */
    public void writeToFile(String filePath, List<Todo> tasks) throws IOException {
        File file = new File(filePath);
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory.toPath());
        }

        String textToAdd = tasks.stream()
                .map(Todo::toDbString)
                .collect(Collectors.joining());

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(textToAdd);
        }
    }

    /**
     * Loads tasks from the specified data file.
     *
     * @param file Data file to load.
     * @return Tasks represented in the data file.
     * @throws IOException If the file cannot be read.
     * @throws YokohamaException If the file contains invalid task data.
     */
    public ArrayList<Todo> loadFile(File file) throws IOException, YokohamaException {
        if (!file.isFile()) {
            throw new IOException("Task file is not a regular file.");
        }
        ArrayList<Todo> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseLine(line));
            } catch (RuntimeException exception) {
                throw new YokohamaException("Invalid task data on line " + (lineNumber + 1) + ".");
            }
        }
        return tasks;
    }

    private Todo parseLine(String line) {
        String[] dataLine = line.split("\\|", -1);
        if (dataLine.length < 3 || dataLine.length > 5) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
        TaskType taskType = TaskType.valueOf(dataLine[0].trim());
        boolean isDone = parseCompletion(dataLine[1].trim());
        String description = dataLine[2].trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Missing description");
        }
        return switch (taskType) {
            case T -> requireFields(dataLine, 3, new Task(description, isDone));
            case D -> requireFields(dataLine, 4,
                    new Deadline(description, isDone, LocalDateTime.parse(dataLine[3].trim())));
            case E -> requireFields(dataLine, 5,
                    new Event(description, isDone, LocalDateTime.parse(dataLine[3].trim()),
                            LocalDateTime.parse(dataLine[4].trim())));
        };
    }

    private boolean parseCompletion(String value) {
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid completion value");
    }

    private Todo requireFields(String[] fields, int expected, Todo task) {
        if (fields.length != expected) {
            throw new IllegalArgumentException("Unexpected extra fields");
        }
        return task;
    }
}
