package nova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Loads tasks from the save file and writes them back to it.
 *
 * <p>The file holds one task per line, with fields separated by
 * {@code " | "}, for example {@code D | 0 | return book | 2019-06-06T00:00}.
 * This class knows that format and nothing else about the chatbot; it never
 * prints anything, so the caller decides how problems are reported.
 */
public class Storage {
    /** Fewest fields any line can have: type, done flag, description. */
    private static final int MIN_FIELD_COUNT = 3;

    /** Index at which the description starts. */
    private static final int DESCRIPTION_FIELD = 2;

    private final Path file;

    /** Number of unreadable lines skipped during the most recent load. */
    private int skippedLineCount = 0;

    /**
     * Creates a storage backed by the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the task list back from the save file.
     *
     * <p>A missing file is not an error: it simply means this is the first
     * run on this machine. Lines that cannot be understood are skipped, and
     * counted in {@link #getSkippedLineCount()}, so that one damaged line
     * does not cost the user the rest of the list.
     *
     * @return the tasks recovered from disk, or an empty list if there are none
     * @throws NovaException if the file exists but cannot be read
     */
    public List<Task> load() throws NovaException {
        skippedLineCount = 0;
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new NovaException("I couldn't read " + file + ": " + e.getMessage()
                    + " Starting with an empty task list.");
        }

        List<String> taskLines = lines.stream()
                .filter(line -> !line.isBlank())
                .toList();

        for (String line : taskLines) {
            try {
                tasks.add(parseSavedTask(line));
            } catch (NovaException e) {
                skippedLineCount++;
            }
        }
        assert tasks.size() + skippedLineCount == taskLines.size()
                : "every non-blank line should be either loaded or counted as skipped";
        return tasks;
    }

    /**
     * Writes the whole task list to the save file, creating the enclosing
     * folder first if it does not exist yet. Rewriting the entire file rather
     * than appending keeps the on-disk copy in step with edits and deletions.
     *
     * @param tasks the tasks to persist
     * @throws NovaException if the file cannot be written
     */
    public void save(List<Task> tasks) throws NovaException {
        List<String> lines = tasks.stream()
                .map(Task::toFileString)
                .toList();

        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            throw new NovaException("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Returns how many unreadable lines were skipped by the most recent
     * {@link #load()}.
     *
     * @return the number of skipped lines, zero if the file was intact
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Rebuilds a single task from its save-file line.
     *
     * @param line one line of the save file
     * @return the reconstructed task
     * @throws NovaException if the line does not match the expected format
     */
    private static Task parseSavedTask(String line) throws NovaException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < MIN_FIELD_COUNT) {
            throw new NovaException("Line has too few fields.");
        }

        String doneFlag = parts[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new NovaException("Done flag must be 0 or 1.");
        }

        int trailingCount = trailingFieldCount(parts[0]);
        int descriptionEnd = parts.length - trailingCount;
        if (descriptionEnd <= DESCRIPTION_FIELD) {
            throw new NovaException("Line has too few fields.");
        }

        // A description may itself contain " | ", which the split above will have
        // broken apart. The type says how many fields trail the description, so
        // everything between the done flag and those is description; put the
        // separators back rather than losing the task.
        String description = String.join(" | ",
                Arrays.copyOfRange(parts, DESCRIPTION_FIELD, descriptionEnd));
        if (description.isBlank()) {
            throw new NovaException("Description is empty.");
        }

        String[] dates = Arrays.copyOfRange(parts, descriptionEnd, parts.length);
        for (String date : dates) {
            if (date.isBlank()) {
                throw new NovaException("Date field is empty.");
            }
        }

        Task task = switch (parts[0]) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, DateTimes.parse(dates[0]));
            case "E" -> new Event(description, DateTimes.parse(dates[0]), DateTimes.parse(dates[1]));
            default -> throw new NovaException("Unknown task type '" + parts[0] + "'.");
        };

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns how many fields follow the description for the given task type.
     *
     * @param type the type letter from the start of the line
     * @return 0 for a todo, 1 for a deadline, 2 for an event
     * @throws NovaException if the type letter is not one this app writes
     */
    private static int trailingFieldCount(String type) throws NovaException {
        return switch (type) {
            case "T" -> 0;
            case "D" -> 1;
            case "E" -> 2;
            default -> throw new NovaException("Unknown task type '" + type + "'.");
        };
    }
}
