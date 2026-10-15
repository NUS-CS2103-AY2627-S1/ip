package swell.storage;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import swell.exception.SwellException;
import swell.task.Deadline;
import swell.task.Event;
import swell.task.Task;
import swell.task.TaskDateTime;
import swell.task.TaskList;
import swell.task.Todo;

/**
 * Handles loading and saving Swell tasks.
 */
public class Storage {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "swell.txt");
    private static final String SEPARATOR = " | ";

    private final Path dataFile;

    /**
     * Creates a storage component that reads and writes Swell's data file.
     */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a storage component that reads and writes the given data file.
     *
     * @param dataFile data file path.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads tasks from the data file, creating the file first if needed.
     *
     * @return loaded task list.
     * @throws SwellException if the data file cannot be prepared or read.
     */
    public TaskList loadTasks() throws SwellException {
        ensureDataFileExists();

        ArrayList<Task> tasks = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(dataFile)) {
                if (!line.trim().isEmpty()) {
                    tasks.add(parseTask(line));
                }
            }
        } catch (IOException e) {
            throw new SwellException("I couldn't read the saved tasks, so I'm starting fresh.");
        }
        return new TaskList(tasks);
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks task list to save.
     * @throws SwellException if the data file cannot be prepared or written.
     */
    public void saveTasks(TaskList tasks) throws SwellException {
        ensureDataFileExists();

        ArrayList<String> lines = tasks.asList().stream()
                .map(this::formatTask)
                .collect(Collectors.toCollection(ArrayList::new));

        try {
            Files.write(dataFile, lines);
        } catch (IOException e) {
            throw new SwellException("I couldn't save the task list this time.");
        }
    }

    private void ensureDataFileExists() throws SwellException {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }
        } catch (IOException e) {
            throw new SwellException("I couldn't prepare the data file for saving tasks.");
        }
    }

    private String formatTask(Task task) {
        String type = task.getType().getSymbol();
        String doneStatus = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        String tags = formatTags(task);

        if (task instanceof Deadline) {
            return formatFields(type, doneStatus, description,
                    encode(((Deadline) task).getBy().toStorageString()),
                    tags);
        } else if (task instanceof Event) {
            return formatFields(type, doneStatus, description,
                    encode(((Event) task).getFrom().toStorageString()),
                    encode(((Event) task).getTo().toStorageString()),
                    tags);
        }

        return formatFields(type, doneStatus, description, tags);
    }

    private String formatFields(String... fields) {
        return String.join(SEPARATOR, fields);
    }

    private Task parseTask(String line) throws SwellException {
        String[] fields = line.split("\\s+\\|\\s+", -1);
        if (fields.length < 3) {
            throw new SwellException("Some saved tasks look corrupted, so I couldn't load them.");
        }

        Task task = createTaskFromFields(fields);
        assert task != null : "Saved task fields should create a task before applying done status";
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new SwellException("Some saved tasks have invalid done statuses.");
        }
        return task;
    }

    private Task createTaskFromFields(String[] fields) throws SwellException {
        String description = decode(fields[2]);
        switch (fields[0]) {
            case "T":
                if (fields.length != 3 && fields.length != 4) {
                    throw new SwellException("A saved todo task has the wrong format.");
                }
                assert fields.length == 3 || fields.length == 4
                        : "Todo storage rows should have three fields or an added tag field";
                return new Todo(description, parseTags(fields, 3));
            case "D":
                if (fields.length != 4 && fields.length != 5) {
                    throw new SwellException("A saved deadline task has the wrong format.");
                }
                assert fields.length == 4 || fields.length == 5
                        : "Deadline storage rows should have four fields or an added tag field";
                return new Deadline(description, parseSavedDateTime(fields[3]), parseTags(fields, 4));
            case "E":
                if (fields.length != 5 && fields.length != 6) {
                    throw new SwellException("A saved event task has the wrong format.");
                }
                assert fields.length == 5 || fields.length == 6
                        : "Event storage rows should have five fields or an added tag field";
                return new Event(description, parseSavedDateTime(fields[3]),
                        parseSavedDateTime(fields[4]), parseTags(fields, 5));
            default:
                throw new SwellException("A saved task has an unknown task type.");
        }
    }

    private String formatTags(Task task) {
        return task.getTags().stream()
                .map(this::encode)
                .collect(Collectors.joining(","));
    }

    private ArrayList<String> parseTags(String[] fields, int tagIndex) {
        ArrayList<String> tags = new ArrayList<>();
        if (fields.length <= tagIndex || fields[tagIndex].isEmpty()) {
            return tags;
        }

        for (String tag : fields[tagIndex].split(",")) {
            tags.add(decode(tag));
        }
        return tags;
    }

    private TaskDateTime parseSavedDateTime(String encodedDate) throws SwellException {
        try {
            return TaskDateTime.parse(decode(encodedDate));
        } catch (DateTimeParseException e) {
            throw new SwellException("A saved task has an invalid date or time.");
        }
    }

    private String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    private String decode(String text) {
        return URLDecoder.decode(text, StandardCharsets.UTF_8);
    }
}
