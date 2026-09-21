package ted;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import ted.task.Deadline;
import ted.task.Event;
import ted.task.Tag;
import ted.task.Task;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Loads the task list from a human-editable text file and saves it back, so
 * that Ted's tasks survive between runs.
 * <p>
 * The file path is given to the constructor rather than hard-coded, which lets
 * tests point Ted at a scratch file instead of the real one. A relative path
 * such as {@code data/ted.txt} is built with {@link Path}, so it works the same
 * on any computer and any operating system. Each task is one line, formatted
 * by {@link Task#toSaveFormat()}. A tagged task carries its tags as an extra
 * field straight after the done flag:
 *
 * <pre>
 * T | 0 | borrow book
 * T | 1 | #fun #school | read book
 * D | 0 | 2019-06-06T18:00 | return book
 * E | 0 | #cs2103 | 2019-08-06T14:00 | 2019-08-06T16:00 | project meeting
 * </pre>
 * <p>
 * Because the file can be edited by hand, it may hold lines Ted cannot read.
 * Those lines would be lost the next time Ted saves, so the file is first
 * copied aside, e.g. to {@code data/ted.txt.bak}, for the user to recover them.
 */
public class Storage {
    /**
     * The save-field separator, ready to use as a regex in {@link String#split}.
     * {@link Pattern#quote(String)} ensures the pipe is treated as text rather
     * than as the regex "or" operator.
     */
    private static final String FIELD_SEPARATOR_REGEX = Pattern.quote(Task.SAVE_FIELD_SEPARATOR);

    /** Added to the data file's name to name the copy kept of an unreadable file. */
    private static final String BACKUP_SUFFIX = ".bak";

    /** File the task list is persisted to. */
    private final Path dataFile;

    /** Where the data file is copied before Ted overwrites content it could not read. */
    private final Path backupFile;

    /** How many lines the last {@link #load()} could not make sense of. */
    private int skippedLineCount = 0;

    /** Whether the last {@link #load()} copied the data file to {@link #backupFile}. */
    private boolean hasBackup = false;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath where to keep the tasks, e.g. {@code data/ted.txt}.
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
        this.backupFile = Path.of(filePath + BACKUP_SUFFIX);
    }

    /**
     * Writes every task to the data file, replacing its previous content.
     * The file is always written even when the list is empty, so that deleting
     * all tasks is remembered. Any missing parent folder is created first,
     * which is what makes the very first run work on a fresh computer.
     *
     * @param tasks the tasks to persist.
     * @throws TedException if the file cannot be written.
     */
    public void save(TaskList tasks) throws TedException {
        try {
            Path parentDir = dataFile.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            List<String> lines = tasks.asList().stream()
                    .map(Task::toSaveFormat)
                    .toList();
            Files.write(dataFile, lines);
        } catch (IOException e) {
            throw new TedException("Uh oh. I couldn't save your tasks to " + dataFile + ": " + describe(e) + ".");
        }
    }

    /**
     * Reads the task list back from the data file.
     * A line that is not in the expected format is skipped rather than allowed
     * to take down startup; {@link #getSkippedLineCount()} reports how many.
     * Whenever something could not be read, the file is copied aside first;
     * {@link #getBackupFile()} reports where.
     *
     * @return the tasks in the order they were saved, or an empty list if the
     *         file does not exist yet (first run).
     * @throws TedException if the file exists but cannot be read.
     */
    public List<Task> load() throws TedException {
        List<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        hasBackup = false;

        if (Files.notExists(dataFile)) {
            return tasks;
        }

        for (String line : readDataFile()) {
            if (line.isBlank()) {
                // A stray blank line (e.g. at the end of the file) is not a task.
                continue;
            }

            Task task = parseLine(line);
            if (task == null) {
                skippedLineCount++;
            } else {
                tasks.add(task);
            }
        }

        if (skippedLineCount > 0) {
            // The next save writes only the tasks that were read, so the
            // skipped lines would be gone for good without a copy.
            backUpDataFile();
        }
        return tasks;
    }

    /**
     * Returns how many lines the last {@link #load()} had to skip.
     *
     * @return the number of unreadable lines, or 0 if the file was clean.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Returns where the last {@link #load()} copied the data file, if it found
     * something it could not read.
     *
     * @return the copy's location, or an empty {@code Optional} if no copy
     *         was needed or the copy could not be made.
     */
    public Optional<Path> getBackupFile() {
        return hasBackup ? Optional.of(backupFile) : Optional.empty();
    }

    /**
     * Reads every line of the data file.
     *
     * @return the file's lines, in order.
     * @throws TedException if the file is a folder or cannot be read.
     */
    private List<String> readDataFile() throws TedException {
        if (Files.isDirectory(dataFile)) {
            throw new TedException("I couldn't read your saved tasks: " + dataFile + " is a folder, not a file.");
        }

        try {
            return Files.readAllLines(dataFile);
        } catch (IOException e) {
            // Ted carries on with an empty list and overwrites the file on the
            // first change, so the whole file is copied aside before that.
            backUpDataFile();
            throw new TedException("I couldn't read your saved tasks in " + dataFile + ": " + describe(e) + ".");
        }
    }

    /**
     * Copies the data file to {@link #backupFile}, replacing any older copy.
     * A failed copy is not treated as an error of its own: the user is already
     * being warned about the file, and {@link #getBackupFile()} stays empty so
     * that no copy is promised.
     */
    private void backUpDataFile() {
        try {
            Files.copy(dataFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            hasBackup = true;
        } catch (IOException e) {
            hasBackup = false;
        }
    }

    /**
     * Explains a file problem in words the user can act on.
     * Some exceptions carry nothing but the file's path as their message,
     * which says where the problem is but not what it is.
     *
     * @param e the problem that stopped a read or write.
     * @return a short explanation, e.g. {@code permission denied}.
     */
    private static String describe(IOException e) {
        if (e instanceof AccessDeniedException) {
            return "permission denied";
        }
        if (e instanceof CharacterCodingException) {
            return "it is not saved as UTF-8 text";
        }
        if (e instanceof FileAlreadyExistsException) {
            // Thrown when a file sits where the data folder should be.
            return e.getMessage() + " is a file, but I need a folder there";
        }
        if (e instanceof FileSystemException fileSystemException && fileSystemException.getReason() != null) {
            return fileSystemException.getReason();
        }
        return e.getMessage();
    }

    /**
     * Rebuilds a single task from one line of the data file.
     * <p>
     * The line has the shape
     * {@code <icon> | <done> | [<tags> |] <date fields...> | <description>}.
     * Pipes and backslashes inside fields are escaped. The limited split also
     * preserves descriptions containing raw separators from save files created
     * before escaping was introduced.
     *
     * @param line one non-blank line of the data file.
     * @return the rebuilt task, or {@code null} if the line is not in the expected format.
     */
    private static Task parseLine(String line) {
        assert !line.isBlank() : "load() skips blank lines, so they are never parsed";

        // A first, unlimited split to read the type icon and look for a tags
        // field: even a corrupted line must have at least its icon before
        // anything can be parsed.
        String[] rawFields = line.split(FIELD_SEPARATOR_REGEX);
        if (rawFields.length == 0) {
            // A line of nothing but separators, e.g. " | ", splits into no
            // fields at all, since split() drops trailing empty strings.
            return null;
        }
        String icon = rawFields[0];
        int fieldCount = fieldCountFor(icon);
        if (fieldCount == -1) {
            return null;
        }

        // A tagged task has one extra field, straight after the done flag. The
        // field must also hold valid tags, so that an older line whose
        // description contains a raw separator is still read as it always was.
        boolean hasTags = rawFields.length > fieldCount && isTagsField(rawFields[2]);
        if (hasTags) {
            fieldCount++;
        }

        // A limited split keeps legacy raw separators inside the final description.
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, fieldCount);
        if (fields.length != fieldCount) {
            return null;
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            return null;
        }

        String description = decodeSaveField(fields[fieldCount - 1]);
        boolean isDone = fields[1].equals("1");
        List<Tag> tags = hasTags ? parseSavedTags(fields[2]) : List.of();
        // The tags field, when present, pushes the date fields one place along.
        int firstDateField = hasTags ? 3 : 2;
        try {
            switch (icon) {
                case "T":
                    return withSavedState(new Todo(description), isDone, tags);
                case "D":
                    return withSavedState(new Deadline(
                            description, parseSavedDateTime(fields[firstDateField])), isDone, tags);
                case "E":
                    return withSavedState(new Event(
                            description,
                            parseSavedDateTime(fields[firstDateField]),
                            parseSavedDateTime(fields[firstDateField + 1])),
                            isDone, tags);
                default:
                    assert false : "unreachable, as fieldCountFor accepts only T, D and E";
                    return null;
            }
        } catch (DateTimeParseException e) {
            // A line whose date cannot be read is corrupted as far as Ted is
            // concerned; the caller counts it and carries on with the rest.
            return null;
        }
    }

    /**
     * Reads back a date and time written by {@link LocalDateTime#toString()}.
     * The saved form is ISO-8601 rather than the format shown to the user, so
     * that the file stays unambiguous and independent of Ted's display format.
     *
     * @param field one encoded date-time field from the save file.
     * @return the date and time it stands for.
     * @throws DateTimeParseException if the field is not a valid ISO date-time.
     */
    private static LocalDateTime parseSavedDateTime(String field) {
        return LocalDateTime.parse(decodeSaveField(field));
    }

    /**
     * Returns whether a saved field holds tags: one or more tags separated by
     * single spaces, e.g. {@code #fun #school}.
     *
     * @param field one field from the save file.
     * @return {@code true} if every word in the field is a valid tag.
     */
    private static boolean isTagsField(String field) {
        // The -1 keeps empty strings from stray spaces, so they fail the check
        // instead of being silently dropped.
        return Arrays.stream(field.split(" ", -1)).allMatch(Tag::isValidText);
    }

    /**
     * Reads back the tags written by {@link Task#toSaveFormat()}.
     *
     * @param field a field that {@link #isTagsField(String)} accepts.
     * @return the tags, in the order they were saved.
     */
    private static List<Tag> parseSavedTags(String field) {
        return Arrays.stream(field.split(" "))
                .map(Tag::fromText)
                .toList();
    }

    /**
     * Number of fields a valid untagged save line has for a given task type:
     * the icon and the done flag, plus the date fields, plus the description.
     * A tagged line has one more, for its tags.
     *
     * @return 3 for a todo, 4 for a deadline, 5 for an event, or -1 if the icon is unknown.
     */
    private static int fieldCountFor(String icon) {
        return switch (icon) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> -1;
        };
    }

    /**
     * Restores escaped backslashes and pipe characters in one saved field.
     * An unknown escape sequence is left unchanged so that older save files
     * containing ordinary backslashes remain readable.
     *
     * @param field one encoded field from the save file.
     * @return the field's original text.
     */
    private static String decodeSaveField(String field) {
        StringBuilder decodedField = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char currentCharacter = field.charAt(i);
            boolean hasEscapedCharacter = currentCharacter == '\\' && i + 1 < field.length();
            if (hasEscapedCharacter) {
                char nextCharacter = field.charAt(i + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    decodedField.append(nextCharacter);
                    i++;
                    continue;
                }
            }
            decodedField.append(currentCharacter);
        }
        return decodedField.toString();
    }

    /**
     * Applies the saved done flag and tags to a freshly created task, which
     * always starts out undone and untagged.
     */
    private static Task withSavedState(Task task, boolean isDone, List<Tag> tags) {
        if (isDone) {
            task.markAsDone();
        }
        task.addTags(tags);
        return task;
    }
}
