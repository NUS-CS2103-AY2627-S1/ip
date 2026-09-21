package sel.storage;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sel.exception.SelException;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.ToDo;

/**
 * Deals with loading tasks from the save file and saving tasks back to it.
 *
 * <p>Problems with the file itself are reported two different ways, because
 * they need different treatment:
 * <ul>
 *   <li>Problems that stop the whole operation (the file cannot be read,
 *       created or written) are thrown as a {@link SelException} so the
 *       caller can show them to the user.</li>
 *   <li>Problems with individual lines (unreadable or duplicated data) are
 *       collected in {@link #getLoadWarnings()}. The rest of the file still
 *       loads, and the caller can mention what was skipped.</li>
 * </ul>
 */
public class Storage {
    private final Path filePath;

    /** Notes about lines skipped by the most recent {@link #load()}. */
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Creates a Storage that reads from and writes to the given file path.
     *
     * @param filePath the path to the save file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the save file, creating it (and its parent
     * directory) if it does not already exist. Lines that cannot be parsed,
     * and lines that repeat a task already loaded, are skipped and recorded
     * in {@link #getLoadWarnings()} rather than aborting the load.
     *
     * @return the list of tasks read from the save file.
     * @throws SelException if the file cannot be read or created at all.
     */
    public List<Task> load() throws SelException {
        List<Task> tasks = new ArrayList<>();
        loadWarnings.clear();

        if (Files.isDirectory(filePath)) {
            throw new SelException("Bro, my save file path (" + filePath
                    + ") is a folder, not a file. Move it out of the way and restart me.");
        }

        try {
            createFileIfMissing();
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                readLineInto(tasks, lines.get(i).trim(), i + 1);
            }
        } catch (AccessDeniedException e) {
            throw new SelException("Bro, I'm not allowed to open " + filePath
                    + ". Check who owns the file and whether it's read-only.");
        } catch (IOException e) {
            throw new SelException("Bro, I couldn't read my save file (" + filePath + "): "
                    + e.getMessage());
        }

        return tasks;
    }

    /**
     * Returns notes about any lines the most recent {@link #load()} had to
     * skip. Empty if the whole file loaded cleanly.
     *
     * @return an unmodifiable list of warning messages.
     */
    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    /**
     * Saves the given tasks to the save file, replacing its previous
     * contents entirely.
     *
     * <p>The tasks are written to a temporary file which is then moved into
     * place. If the write fails part way through (out of disk space, say),
     * the previous save file is left untouched instead of being left
     * half-written.
     *
     * @param tasks the tasks to save.
     * @throws SelException if the tasks could not be written to disk.
     */
    public void save(List<Task> tasks) throws SelException {
        List<String> lines = new ArrayList<>();
        for (Task currentTask : tasks) {
            lines.add(toSaveFormat(currentTask));
        }

        Path directory = filePath.toAbsolutePath().getParent();
        Path temporaryFile = null;

        try {
            Files.createDirectories(directory);
            temporaryFile = Files.createTempFile(directory, "sel", ".tmp");
            Files.write(temporaryFile, lines);
            moveIntoPlace(temporaryFile, filePath);
            temporaryFile = null;
        } catch (AccessDeniedException e) {
            throw new SelException("Bro, I'm not allowed to write to " + filePath
                    + ". Your list is fine in this window, but I can't save it - "
                    + "check the file permissions.");
        } catch (IOException e) {
            throw new SelException("Bro, I couldn't save your tasks to " + filePath + ": "
                    + e.getMessage() + " Your list is fine in this window, but it may not "
                    + "survive a restart.");
        } finally {
            deleteQuietly(temporaryFile);
        }
    }

    /**
     * Creates the save file, and any missing directories above it, if it is
     * not already there.
     *
     * @throws IOException if the file or its directories cannot be created.
     */
    private void createFileIfMissing() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Parses one line of the save file and adds the task it describes,
     * recording a warning instead if the line is unusable or duplicated.
     *
     * @param tasks the tasks loaded so far; the new task is added to this.
     * @param line the trimmed contents of the line.
     * @param lineNumber the 1-based line number, used in warnings.
     */
    private void readLineInto(List<Task> tasks, String line, int lineNumber) {
        if (line.isEmpty()) {
            return;
        }

        Task loadedTask;
        try {
            loadedTask = parseTask(line);
        } catch (IllegalArgumentException e) {
            loadWarnings.add("line " + lineNumber + ": " + e.getMessage());
            return;
        }

        boolean isDuplicate = tasks.stream().anyMatch(task -> task.hasSameDetailsAs(loadedTask));
        if (isDuplicate) {
            loadWarnings.add("line " + lineNumber + ": repeats a task already on the list");
            return;
        }

        tasks.add(loadedTask);
    }

    /**
     * Moves the freshly written temporary file over the real save file,
     * atomically where the filesystem supports it.
     *
     * @param source the temporary file holding the new contents.
     * @param target the save file to replace.
     * @throws IOException if the file cannot be moved.
     */
    private void moveIntoPlace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            // Some filesystems cannot move atomically; a plain replace is
            // the best that can be done there.
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Deletes a leftover temporary file, ignoring any failure. Called from
     * a {@code finally} block, where throwing would hide the real error.
     *
     * @param file the file to delete; may be {@code null}.
     */
    private void deleteQuietly(Path file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Nothing useful to do: the save has already failed, and a
            // stray .tmp file is harmless.
        }
    }

    /**
     * Renders a task as the single save-file line that represents it.
     *
     * @param task the task to render.
     * @return the line to write to the save file.
     */
    private String toSaveFormat(Task task) {
        String status = task.isDone() ? "1" : "0";

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + status + " | " + deadline.getDescription()
                    + " | " + deadline.getDdl();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + status + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        return "T | " + status + " | " + task.getDescription();
    }

    /**
     * Parses a single save-file line into a {@link Task}.
     *
     * @param line a single line from the save file, in the format
     *     {@code TYPE | STATUS | DESCRIPTION | ...}.
     * @return the task represented by the line.
     * @throws IllegalArgumentException if the line is malformed or its
     *     type/status/fields are invalid.
     */
    private Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException("not enough fields");
        }

        String type = parts[0];
        String status = parts[1];

        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("'" + status + "' is not a done/not-done flag");
        }

        Task loadedTask = buildTask(type, parts);

        if (status.equals("1")) {
            loadedTask.mark();
        }
        return loadedTask;
    }

    /**
     * Builds the task described by the fields of a save-file line.
     *
     * <p>Descriptions are rejoined from the middle fields rather than taken
     * from a fixed position, so a description that somehow contains the
     * field separator (from an older version, or a hand-edited file) is
     * still read back in one piece instead of being dropped.
     *
     * @param type the task-type marker, e.g. {@code "T"}.
     * @param parts every field on the line, separators removed.
     * @return the task the line describes.
     * @throws IllegalArgumentException if the fields do not fit the type.
     */
    private Task buildTask(String type, String[] parts) {
        try {
            switch (type) {
                case "T":
                    return new ToDo(requireDescription(rejoin(parts, 2, parts.length)));

                case "D":
                    if (parts.length < 4 || parts[parts.length - 1].isEmpty()) {
                        throw new IllegalArgumentException("a deadline needs a due date");
                    }
                    return new Deadline(
                            requireDescription(rejoin(parts, 2, parts.length - 1)),
                            parseStoredDateTime(parts[parts.length - 1]));

                case "E":
                    return buildEvent(parts);

                default:
                    throw new IllegalArgumentException("'" + type + "' is not a task type I know");
            }
        } catch (SelException e) {
            // Event rejects impossible time ranges. From the save file's
            // point of view that is just another unusable line.
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Builds an {@link Event} from the fields of a save-file line, handling
     * both the current five-field format and the older format that packed
     * the whole time range into one field.
     *
     * @param parts every field on the line, separators removed.
     * @return the event the line describes.
     * @throws IllegalArgumentException if the fields are unusable.
     * @throws SelException if the stored time range is impossible.
     */
    private Event buildEvent(String[] parts) throws SelException {
        if (parts.length == 4 && !parts[3].isEmpty()) {
            String[] range = splitLegacyEventRange(parts[3]);
            return new Event(requireDescription(parts[2]),
                    parseStoredDateTime(range[0]), parseStoredDateTime(range[1]));
        }

        if (parts.length < 5
                || parts[parts.length - 1].isEmpty()
                || parts[parts.length - 2].isEmpty()) {
            throw new IllegalArgumentException("an event needs a start and an end date");
        }

        return new Event(
                requireDescription(rejoin(parts, 2, parts.length - 2)),
                parseStoredDateTime(parts[parts.length - 2]),
                parseStoredDateTime(parts[parts.length - 1]));
    }

    /**
     * Joins the given range of fields back together with the separator that
     * split them.
     *
     * @param parts every field on the line.
     * @param fromIndex first field to include.
     * @param toIndex one past the last field to include.
     * @return the rejoined text.
     */
    private String rejoin(String[] parts, int fromIndex, int toIndex) {
        return String.join(" | ", List.of(parts).subList(fromIndex, toIndex));
    }

    /**
     * Checks that a description read from the save file is not blank.
     *
     * @param description the description read from the file.
     * @return the same description.
     * @throws IllegalArgumentException if it is empty or only whitespace.
     */
    private String requireDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("the description is missing");
        }
        return description;
    }

    /**
     * Splits a legacy single-field event time range (e.g. from an older
     * save format) into its from/to components.
     *
     * <p>Only the {@code "FROM to TO"} form is recognised. A dash-separated
     * form used to be accepted here as well, but it split on the last dash
     * in the text, and every stored date contains dashes of its own
     * ({@code 2019-12-02T16:00}), so the right-hand half was always a
     * fragment like {@code 02T16:00} that could never be parsed. The branch
     * could only ever end in the line being skipped, so it is gone.
     *
     * @param timeRange the combined time range string.
     * @return a two-element array of {@code {from, to}} strings.
     * @throws IllegalArgumentException if the range cannot be split into
     *     two non-empty parts.
     */
    private String[] splitLegacyEventRange(String timeRange) {
        int toIndex = timeRange.indexOf(" to ");
        if (toIndex >= 0) {
            String from = timeRange.substring(0, toIndex).trim();
            String to = timeRange.substring(toIndex + " to ".length()).trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                return new String[] {from, to};
            }
        }

        throw new IllegalArgumentException("'" + timeRange + "' is not a time range");
    }

    /**
     * Parses a date/time string as stored in the save file (ISO format)
     * into a {@link LocalDateTime}.
     *
     * @param input the stored date/time string.
     * @return the parsed date/time.
     * @throws IllegalArgumentException if the string is not a valid ISO
     *     date/time.
     */
    private LocalDateTime parseStoredDateTime(String input) {
        try {
            return LocalDateTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'" + input + "' is not a stored date/time");
        }
    }
}
