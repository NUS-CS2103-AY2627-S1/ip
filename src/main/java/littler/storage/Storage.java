package littler.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import littler.datetime.StringDateTimeConverter;
import littler.exception.LittleRException;
import littler.task.Deadline;
import littler.task.Event;
import littler.task.Task;
import littler.task.Todo;
import littler.task.attribute.PriorityLevel;
import littler.task.attribute.TagSet;

/**
 * Handles reading tasks from disk and persisting tasks back to disk storage.
 */
public class Storage {
    private static final int TODO_BASE_FIELDS = 3;
    private static final int DEADLINE_BASE_FIELDS = 4;
    private static final int EVENT_BASE_FIELDS = 5;

    private final Path filePath;
    private final Path archiveFilePath;

    /**
     * Constructs a new Storage instance initialized with the target file path.
     *
     * @param filePath the string path pointing to the data storage file
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
        this.archiveFilePath = this.filePath.resolveSibling(this.filePath.getFileName() + ".archive");
    }

    /**
     * Loads saved tasks from disk storage into an ArrayList.
     *
     * @return an ArrayList of tasks loaded from the file, or an empty list if the file does not exist yet
     * @throws LittleRException if the target file exists but cannot be read due to an I/O error
     */
    public ArrayList<Task> load() throws LittleRException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            // Not an error: this is a normal first run on a new machine.
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(filePath)) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseLine(line));
                } catch (LittleRException e) {
                    // Skips corrupted lines instead of failing the whole load.
                }
            }
        } catch (IOException e) {
            throw new LittleRException("Could not read save file: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Writes the provided task list to disk, creating missing parent directories if necessary.
     *
     * @param tasks the list of tasks to be saved to file storage
     * @throws LittleRException if parent directories cannot be created or file writing fails
     */
    public void save(ArrayList<Task> tasks) throws LittleRException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = tasks.stream()
                .map(Task::toFileString)
                .toList();
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new LittleRException("Could not save tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Reconstructs a concrete Task instance from a single formatted line of the save file.
     *
     * @param line a single formatted line read from storage
     * @return the reconstructed Task object (Todo, Deadline, or Event)
     * @throws LittleRException if the line is malformed, missing required fields, or has an unknown task type
     */
    // Storage.java
    private Task parseLine(String line) throws LittleRException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new LittleRException("Malformed line: " + line);
        }

        String type = parts[0];
        boolean isMarked = parts[1].equals("1");
        String name = parts[2];
        Task task;

        switch (type) {
            case Todo.TYPE_CODE:
                task = new Todo(name);
                applyMetadataField(task, parts);
                break;
            case Deadline.TYPE_CODE:
                if (parts.length < DEADLINE_BASE_FIELDS) {
                    throw new LittleRException("Missing due date in line: " + line);
                }
                task = new Deadline(name, StringDateTimeConverter.fromStorageString(parts[3]));
                applyMetadataField(task, parts);
                break;
            case Event.TYPE_CODE:
                if (parts.length < EVENT_BASE_FIELDS) {
                    throw new LittleRException("Missing from/to date in line: " + line);
                }
                task = new Event(name,
                    StringDateTimeConverter.fromStorageString(parts[3]),
                    StringDateTimeConverter.fromStorageString(parts[4]));
                applyMetadataField(task, parts);
                break;
            default:
                throw new LittleRException("Unknown task type: " + type);
        }

        if (isMarked) {
            task.mark();
        }
        return task;
    }

    private void applyMetadataField(Task task, String[] fields) {
        int baseFields = task instanceof Todo ? TODO_BASE_FIELDS
            : task instanceof Deadline ? DEADLINE_BASE_FIELDS : EVENT_BASE_FIELDS;
        for (int i = baseFields; i < fields.length; i++) {
            String field = fields[i];
            if (TagSet.isTagField(field)) {
                for (String tag : field.substring("TAGS:".length()).split(",")) {
                    task.addTag(tag);
                }
            } else if (PriorityLevel.isPriorityField(field)) {
                task.setPriority(PriorityLevel.fromStorageField(field));
            }
        }
        // unrecognized prefixes are ignored, so a future save-file format change doesn't
        // break loading of files written by this version
    }

    /**
     * Appends the given tasks to the archive file, creating it if it doesn't exist yet.
     * Does nothing if the given list is empty.
     *
     * @param tasksToArchive the tasks to append to the archive file
     * @throws LittleRException if the archive file cannot be written to
     */
    public void archive(ArrayList<Task> tasksToArchive) throws LittleRException {
        if (tasksToArchive.isEmpty()) {
            return;
        }
        try {
            if (archiveFilePath.getParent() != null) {
                Files.createDirectories(archiveFilePath.getParent());
            }
            List<String> lines = tasksToArchive.stream().map(Task::toFileString).toList();
            Files.write(archiveFilePath, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LittleRException("Could not write to archive file: " + e.getMessage());
        }
    }
}
