package pan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles persisting the task list to disk and reading it back.
 *
 * <p>The data lives in a plain text file at the relative path
 * {@code ./data/pan.txt}. Using a relative, separator-neutral path means
 * the chatbot behaves the same regardless of which folder it is run from
 * or which operating system is used.
 *
 * <p>Nothing here prints: a failure is reported back through the return value
 * or through {@link #hasLoadError()} / {@link #getSkippedLineCount()}, and
 * {@link Ui} supplies the wording. Printing here would have reached only the
 * console, leaving a GUI user with no hint that a save had failed.
 */
public class Storage {

    /**
     * Location of the save file. The two-argument {@link File} constructor
     * joins {@code "data"} and {@code "pan.txt"} with the correct separator
     * for the current OS, so no {@code '/'} or {@code '\\'} is hard-coded.
     */
    private static final File FILE = new File("data", "pan.txt");

    /** Save-file lines the last load could not understand. */
    private int skippedLineCount = 0;

    /** Whether the last load found a save file it could not read at all. */
    private boolean hasLoadError = false;

    /**
     * Loads the saved tasks from disk.
     *
     * @return the tasks stored in the file, or an empty list if the file
     *         does not exist yet (for example, the very first run on a new
     *         machine).
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        hasLoadError = false;
        if (!FILE.exists()) {
            return tasks;
        }
        try (Scanner scanner = new Scanner(FILE)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    tasks.add(parseTask(line));
                } catch (RuntimeException e) {
                    // One malformed line (e.g. an old save with a free-text
                    // date like "Sunday") should not stop the rest loading.
                    skippedLineCount++;
                }
            }
        } catch (IOException e) {
            hasLoadError = true;
        }
        return tasks;
    }

    /**
     * Writes the whole task list to disk, replacing any previous contents.
     * Creates the {@code ./data} folder first if it is missing.
     *
     * @param tasks the current task list to persist.
     * @return whether the list reached the disk. A {@code false} lets the
     *     caller warn the user instead of losing their tasks in silence.
     */
    public boolean save(ArrayList<Task> tasks) {
        try {
            File parent = FILE.getParentFile();
            if (parent != null) {
                parent.mkdirs(); // no-op if the folder already exists
            }
            try (FileWriter writer = new FileWriter(FILE)) {
                for (Task task : tasks) {
                    writer.write(task.toFileString() + System.lineSeparator());
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Returns how many save-file lines the last load had to skip. */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /** Returns whether the last load hit a save file it could not read. */
    public boolean hasLoadError() {
        return hasLoadError;
    }

    /**
     * Rebuilds a single {@link Task} from one line of the save file.
     * The expected line format is
     * {@code TYPE | STATUS | DESCRIPTION [ | EXTRA FIELDS ]}.
     */
    private Task parseTask(String line) {
        // Deliberately no `assert parts.length >= 3` here: line comes from an on-disk
        // file, which a user or a previous buggy version could have corrupted, so this
        // is a real (not just theoretical) failure case, not a programming bug. It must
        // stay a normal thrown exception - caught as a RuntimeException by load() - so
        // one bad line is skipped instead of crashing the load. An AssertionError would
        // NOT be caught there (it doesn't extend RuntimeException), and assertions are
        // off by default anyway, so an assert would silently do nothing in production.
        String[] parts = line.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
        case "D":
            task = new Deadline(description, Parser.parseStoredDateTime(parts[3]));
            break;
        case "E":
            task = new Event(description,
                    Parser.parseStoredDateTime(parts[3]),
                    Parser.parseStoredDateTime(parts[4]));
            break;
        default:
            task = new Todo(description);
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
