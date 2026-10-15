package clearblue.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import clearblue.ClearblueException;
import clearblue.task.Deadline;
import clearblue.task.Event;
import clearblue.task.Task;
import clearblue.task.Todo;

/**
 * Saves tasks to, and loads tasks from, a file on disk so that the task
 * list survives between runs of the chatbot.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path dataFile;

    /**
     * Creates a Storage backed by the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    /**
     * Returns whether text contains this Storage's field separator. Saving
     * such text verbatim would let it be mistaken for a field boundary,
     * corrupting the line on the next {@link #load()} — so callers that
     * accept free-form text for a task (e.g. {@link clearblue.parser.Parser})
     * should reject it before it ever reaches {@link #save}.
     *
     * @param text text to check, e.g. a task description or date/time value
     * @return {@code true} if text contains the field separator
     */
    public static boolean containsFieldSeparator(String text) {
        return text.contains(FIELD_SEPARATOR);
    }

    /**
     * Writes the given tasks to this Storage's file, creating the
     * containing folder first if it does not already exist.
     *
     * @param tasks tasks to save
     * @throws ClearblueException if the tasks could not be written to disk
     */
    public void save(List<Task> tasks) throws ClearblueException {
        List<String> lines = tasks.stream()
                .map(Storage::encode)
                .collect(Collectors.toList());

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(dataFile, lines);
        } catch (IOException exception) {
            throw new ClearblueException(
                    "Could not save tasks to disk: " + exception.getMessage(), exception);
        }
    }

    /**
     * Loads previously saved tasks from this Storage's file. If the file
     * or its folder does not exist yet (e.g. on a fresh install), this
     * returns an empty result without treating that as an error. Any line
     * that cannot be parsed (corrupted data) is skipped instead of causing
     * the chatbot to crash on startup; the caller can check
     * {@link LoadResult#skippedLineCount()} to let the user know some
     * saved data was lost, rather than tasks silently disappearing.
     *
     * @return the loaded tasks plus a count of lines that had to be skipped
     * @throws ClearblueException if the save file exists but could not be read
     */
    public LoadResult load() throws ClearblueException {
        if (!Files.exists(dataFile)) {
            return new LoadResult(new ArrayList<>(), 0);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (IOException exception) {
            throw new ClearblueException("Could not load saved tasks: " + exception.getMessage(), exception);
        }

        List<Task> decoded = lines.stream()
                .map(Storage::decode)
                .collect(Collectors.toList());
        List<Task> tasks = decoded.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        int skippedLineCount = decoded.size() - tasks.size();
        return new LoadResult(tasks, skippedLineCount);
    }

    /**
     * The outcome of {@link #load()}: the tasks that were successfully
     * parsed, plus how many lines had to be skipped because they were
     * corrupted.
     *
     * @param tasks tasks successfully loaded, in file order
     * @param skippedLineCount number of lines that could not be parsed
     */
    public record LoadResult(List<Task> tasks, int skippedLineCount) {
    }

    /**
     * Encodes a single task as one line of the save-file format
     * {@code TYPE | doneFlag | description[ | extra fields...]}.
     *
     * @param task task to encode
     * @return encoded line
     */
    private static String encode(Task task) {
        String doneFlag = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", doneFlag, task.getDescription());
        } else if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", doneFlag, deadline.getDescription(),
                    deadline.getBy().toStorageString());
        } else if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", doneFlag, event.getDescription(),
                    event.getFrom().toStorageString(), event.getTo().toStorageString());
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass());
    }

    /**
     * Decodes one line of the save-file format back into a task.
     *
     * @param line encoded line
     * @return decoded task, or {@code null} if the line is corrupted
     */
    private static Task decode(String line) {
        String[] fields = line.split(Pattern.quote(FIELD_SEPARATOR), -1);
        if (fields.length < 3) {
            return null;
        }

        String type = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        Task task;
        if (type.equals("T")) {
            task = new Todo(description);
        } else if (type.equals("D") && fields.length >= 4) {
            task = new Deadline(description, fields[3]);
        } else if (type.equals("E") && fields.length >= 5) {
            task = new Event(description, fields[3], fields[4]);
        } else {
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
