package chai.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import chai.ChaiException;
import chai.task.Deadline;
import chai.task.Event;
import chai.task.Task;
import chai.task.Todo;

/** Saves Chai's tasks in a text file relative to the project root. */
public class Storage {
    /** The location used to persist this storage instance's task list. */
    private final Path dataFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param dataFile File used to persist tasks.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Writes every task in the list to the data file.
     *
     * @param tasks Tasks to persist.
     * @throws ChaiException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws ChaiException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(toFileLine(task));
        }

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(dataFile, lines);
        } catch (IOException e) {
            throw new ChaiException("I could not save your tasks: " + e.getMessage());
        }
    }

    /**
     * Loads saved tasks, or returns an empty list when Chai has not been run before.
     *
     * @return Tasks reconstructed from the data file.
     * @throws ChaiException If the data file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws ChaiException {
        if (Files.notExists(dataFile)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (IOException e) {
            throw new ChaiException("I could not load your saved tasks: " + e.getMessage());
        }

        ArrayList<Task> tasks = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(fromFileLine(line));
            } catch (ChaiException e) {
                throw new ChaiException("Saved data is invalid on line " + (index + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /** Converts one task into a single line in the data file. */
    private static String toFileLine(Task task) {
        String done = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + done + " | " + escapeDescription(task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return "D | " + done + " | " + escapeDescription(deadline.getDescription())
                    + " | " + deadline.getBy();
        }
        Event event = (Event) task;
        return "E | " + done + " | " + escapeDescription(event.getDescription()) + " | " + event.getFrom()
                + " | " + event.getTo();
    }

    /** Recreates one task from a line previously written by {@link #toFileLine(Task)}. */
    private static Task fromFileLine(String line) throws ChaiException {
        String[] parts = line.split(" (?<!\\\\)\\| ", -1);
        if (parts.length < 2 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new ChaiException("the completion status must be 0 or 1.");
        }

        Task task;
        switch (parts[0]) {
            case "T":
                requireFieldCount(parts, 3, "todo");
                task = new Todo(requireDescription(unescapeDescription(parts[2])));
                break;
            case "D":
                requireFieldCount(parts, 4, "deadline");
                task = new Deadline(requireDescription(unescapeDescription(parts[2])), parseDate(parts[3]));
                break;
            case "E":
                requireFieldCount(parts, 5, "event");
                LocalDate from = parseDate(parts[3]);
                LocalDate to = parseDate(parts[4]);
                if (to.isBefore(from)) {
                    throw new ChaiException("the event end date cannot be before its start date.");
                }
                task = new Event(requireDescription(unescapeDescription(parts[2])), from, to);
                break;
            default:
                throw new ChaiException("the task type must be T, D, or E.");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Checks that a stored task line contains the expected number of fields. */
    private static void requireFieldCount(String[] parts, int expectedCount, String taskType) throws ChaiException {
        if (parts.length != expectedCount) {
            throw new ChaiException("the " + taskType + " entry has the wrong number of fields.");
        }
    }

    /** Returns a stored description after checking that it contains visible text. */
    private static String requireDescription(String description) throws ChaiException {
        if (description.isBlank()) {
            throw new ChaiException("the task description cannot be blank.");
        }
        return description;
    }

    /** Escapes description characters that have meaning in the storage format. */
    private static String escapeDescription(String description) {
        return description.replace("\\", "\\\\").replace("|", "\\|");
    }

    /** Restores escaped pipes and backslashes in a stored description. */
    private static String unescapeDescription(String description) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < description.length(); index++) {
            char current = description.charAt(index);
            boolean hasEscapedCharacter = current == '\\' && index + 1 < description.length()
                    && (description.charAt(index + 1) == '\\' || description.charAt(index + 1) == '|');
            if (hasEscapedCharacter) {
                result.append(description.charAt(index + 1));
                index++;
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }

    /** Parses an ISO date stored in the human-editable data file. */
    private static LocalDate parseDate(String value) throws ChaiException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ChaiException("the date must use yyyy-MM-dd.");
        }
    }
}
