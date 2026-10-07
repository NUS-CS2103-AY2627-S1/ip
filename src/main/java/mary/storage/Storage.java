package mary.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import mary.exception.MaryException;
import mary.task.Deadline;
import mary.task.Event;
import mary.task.Task;
import mary.task.Todo;

/**
 * Loads and saves UTF-8 task records at a configured path.
 */
public class Storage {
    private final Path file;
    private boolean isWriteBlocked;

    /**
     * Selects the data file without creating it or its parent directories.
     *
     * @param filePath file path; relative paths resolve from the working directory.
     */
    public Storage(String filePath) {
        Path selected;
        try {
            // Use the same normalized path for validation and every subsequent file operation.
            selected = Path.of(filePath).toAbsolutePath().normalize();
        } catch (InvalidPathException exception) {
            selected = null;
        }
        file = selected;
    }

    /**
     * Loads records in file order, skipping blank lines.
     *
     * @return loaded tasks, or an empty list if the file does not exist.
     * @throws MaryException if the file cannot be read or any record is malformed.
     */
    public List<Task> load() throws MaryException {
        ArrayList<Task> tasks = new ArrayList<>();
        isWriteBlocked = true;
        validatePath();
        try {
            if (Files.notExists(file, LinkOption.NOFOLLOW_LINKS)) {
                isWriteBlocked = false;
                return tasks;
            }
            if (Files.isSymbolicLink(file)) {
                throw new IOException("Symbolic links are not supported");
            }
            List<String> records = Files.readAllLines(file);
            for (int i = 0; i < records.size(); i++) {
                if (!records.get(i).isBlank()) {
                    tasks.add(parseRecord(records.get(i), i + 1));
                }
            }
            isWriteBlocked = false;
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new MaryException("could not read " + file + ". Check that it is a readable UTF-8 file,"
                    + " not a folder or link, then restart MARY.");
        }
    }

    /**
     * Writes a sibling temporary file and atomically replaces the saved file.
     * Creates missing parent directories; failed loads block writes until reloaded.
     *
     * @param tasks tasks to serialize.
     * @throws MaryException if writing the file fails.
     */
    public void save(List<Task> tasks) throws MaryException {
        validatePath();
        if (isWriteBlocked) {
            throw new MaryException("saving is disabled because loading failed; repair or move the data file"
                    + " and restart MARY. Your original file has not been changed.");
        }
        Path temporary = null;
        try {
            if (Files.isSymbolicLink(file) || Files.isDirectory(file)
                    || (Files.exists(file) && !Files.isWritable(file))) {
                throw new IOException("Not a regular data file");
            }
            ArrayList<String> records = new ArrayList<>();
            for (Task task : tasks) {
                // Validate before touching the existing file, including tasks from non-parser callers.
                parseRecord(task.toStorageRecord(), records.size() + 1);
                records.add(task.toStorageRecord());
            }
            Path parent = file.toAbsolutePath().getParent();
            LocalPaths.validate(parent);
            Files.createDirectories(parent);
            LocalPaths.validate(file);
            temporary = Files.createTempFile(parent, ".mary-save-", ".tmp");
            LocalPaths.validate(temporary);
            Files.write(temporary, records);
            LocalPaths.validate(file);
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            throw new MaryException("this folder does not support safe atomic saves; move MARY and its data"
                    + " to a local folder and retry. No task changes were applied.");
        } catch (IOException | SecurityException exception) {
            throw new MaryException("could not save tasks to " + file
                    + ". Check folder permissions, free disk space, and file locks; then retry."
                    + " No task changes were applied.");
        } finally {
            if (temporary != null) {
                try {
                    LocalPaths.validate(temporary);
                    Files.deleteIfExists(temporary);
                } catch (IOException | SecurityException | MaryException exception) {
                    // Preserve the original error; an unused temporary file is safer than lost task data.
                }
            }
        }
    }

    /**
     * Turns invalid configured paths into a recoverable application error.
     */
    private void validatePath() throws MaryException {
        if (file == null) {
            throw new MaryException("invalid data-file path; choose a valid file name and restart MARY.");
        }
        LocalPaths.validate(file);
    }

    /**
     * Validates a pipe-delimited record and restores its subtype and completion state.
     *
     * @param record saved record containing ISO date/times for dated tasks.
     * @param lineNumber one-based file line number for error messages.
     * @return the restored task.
     * @throws MaryException if the type, field count, status, description, or dates are invalid.
     */
    private Task parseRecord(String record, int lineNumber) throws MaryException {
        // load supplies a one-based physical line number, including skipped blank lines.
        assert lineNumber > 0 : "Saved-record line numbers must be positive";
        String[] fields = record.split(" \\| ", -1);
        validateFields(fields, lineNumber);
        try {
            Task task;
            if (fields[0].equals("T")) {
                task = new Todo(fields[2]);
            } else if (fields[0].equals("D")) {
                task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
            } else {
                // Validation above leaves only an event after the todo/deadline branches.
                assert fields[0].equals("E") : "Validated remaining record type must be E";
                task = new Event(fields[2], LocalDateTime.parse(fields[3]), LocalDateTime.parse(fields[4]));
                Event event = (Event) task;
                if (!event.getFrom().isBefore(event.getTo())) {
                    throw new MaryException("event end must be after its start on line " + lineNumber + ".");
                }
            }
            task.setDone(fields[1].equals("1"));
            return task;
        } catch (DateTimeParseException exception) {
            throw new MaryException("invalid date/time on line " + lineNumber + ".");
        }
    }

    /**
     * Checks record shape before accessing common fields, preserving error precedence.
     */
    private void validateFields(String[] fields, int lineNumber) throws MaryException {
        int expectedFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new MaryException("invalid record on line " + lineNumber + ".");
        };
        if (fields.length != expectedFieldCount) {
            throw new MaryException("invalid record on line " + lineNumber + ".");
        }
        boolean isValidStatus = fields[1].equals("0") || fields[1].equals("1");
        if (!isValidStatus) {
            throw new MaryException("invalid completion status on line " + lineNumber + ".");
        }
        if (fields[2].isBlank()) {
            throw new MaryException("empty task description on line " + lineNumber + ".");
        }
        if (fields[2].contains("|") || fields[2].chars().anyMatch(Character::isISOControl)) {
            throw new MaryException("invalid description on line " + lineNumber
                    + "; remove pipes and control characters.");
        }
    }
}
