package aster.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import aster.exception.AsterException;
import aster.task.Deadline;
import aster.task.Event;
import aster.task.Task;
import aster.task.TaskDates;
import aster.task.Todo;

/**
 * Reads tasks from, and writes tasks to, a plain text file on disk.
 *
 * <p>Each task is one line, in the order the tasks are held. A line is a type tag,
 * a done flag and then the fields that type needs, separated by {@code " | "}, for
 * example {@code D | 0 | return book | 2019-06-06}. Both halves of that format live in
 * this one class, so the way a task is written and the way it is read back cannot
 * drift apart.
 *
 * <p>A description may itself contain the separator, so every field is escaped when
 * written: a backslash is written as two backslashes, and a separator is written as a
 * backslash followed by the separator. Reading undoes exactly that, which means any
 * text the user can type survives a save and a load unchanged.
 *
 * <p>A blank line carries no task and is not part of the format, so the reader passes
 * over one wherever it appears. Every other line must be a complete record.
 *
 * <p>Dates are stored in the ISO form {@code yyyy-MM-dd}, which is numeric and names
 * no month, so a file written on one computer reads the same on any other whatever
 * each is configured to.
 */
public class Storage {
    private static final String SEPARATOR = " | ";
    private static final char SEPARATOR_CHAR = '|';
    private static final char ESCAPE_CHAR = '\\';
    private static final String LINE_END = "\n";
    private static final String TEMPORARY_SUFFIX = ".tmp";

    private static final String TODO_TAG = "T";
    private static final String DEADLINE_TAG = "D";
    private static final String EVENT_TAG = "E";
    private static final String DONE = "1";
    private static final String NOT_DONE = "0";

    // A line always starts with the type tag, the done flag and the description; the
    // remaining fields depend on the type.
    private static final int MINIMUM_FIELDS = 3;
    private static final int TODO_FIELDS = 3;
    private static final int DEADLINE_FIELDS = 4;
    private static final int EVENT_FIELDS = 5;
    private static final int TAG_INDEX = 0;
    private static final int DONE_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int FIRST_DETAIL_INDEX = 3;
    private static final int SECOND_DETAIL_INDEX = 4;

    private final Path file;

    /**
     * Creates a store that keeps tasks in the given file.
     *
     * @param file the file to read from and write to, used exactly as supplied; a
     *     relative path is resolved against the folder the program is started in.
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Returns the tasks held in the file, in the order they were saved.
     *
     * <p>A file confirmed not to exist is not an error: it simply means nothing has been
     * saved, so an empty list is returned and nothing is written. An empty file, and a
     * file holding only blank lines, are read as no tasks in the same way.
     *
     * @return the saved tasks, or an empty list if nothing has been saved.
     * @throws AsterException if the file cannot be read, or holds a line that is not a
     *     complete record.
     */
    public List<Task> load() throws AsterException {
        // Only a confirmed absence means nothing has been saved yet. If the file system
        // cannot tell either way, reading is still attempted, so that whatever is wrong
        // is reported as a failure to read rather than silently treated as no tasks.
        if (Files.notExists(file)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AsterException(buildUnreadableFileMessage());
        }

        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            // A blank line is not a record in this format, so it is passed over.
            if (line.isBlank()) {
                continue;
            }
            tasks.add(decode(line));
        }
        return tasks;
    }

    /**
     * Writes every task to the file, replacing whatever it held before.
     *
     * <p>The tasks are written to a temporary file next to the target first, and that
     * file replaces the target only once it is complete. Writing straight to the target
     * would empty it as soon as it was opened, so a failure part way through would leave
     * nothing behind.
     *
     * @param tasks the tasks to write, in the order they should be read back.
     * @throws AsterException if a task is of a type this format does not cover, or if
     *     the folder or the file cannot be written.
     */
    public void save(List<Task> tasks) throws AsterException {
        Path temporary = file.resolveSibling(file.getFileName() + TEMPORARY_SUFFIX);
        try {
            Path folder = file.getParent();
            if (folder != null) {
                // Creates the folder on the first save, and does nothing afterwards.
                Files.createDirectories(folder);
            }
            writeAll(tasks, temporary);
            replace(temporary, file);
        } catch (IOException e) {
            throw new AsterException(buildUnwritableFileMessage());
        } finally {
            // Nothing is left behind whether the save succeeded or failed.
            deleteQuietly(temporary);
        }
    }

    /**
     * Writes every task to the given file, one task per line.
     *
     * @param tasks the tasks to write.
     * @param target the file to write them to.
     * @throws IOException if the file cannot be written.
     * @throws AsterException if a task is of a type this format does not cover.
     */
    private void writeAll(List<Task> tasks, Path target) throws IOException, AsterException {
        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                writer.write(encode(task));
                // Written directly rather than with newLine(), so the file is the same
                // on every operating system.
                writer.write(LINE_END);
            }
        }
    }

    /**
     * Returns the line that stores a task.
     *
     * @param task the task to write down.
     * @return the line to write to the file, with every field escaped.
     * @throws AsterException if the task is of a type this format does not cover.
     */
    private String encode(Task task) throws AsterException {
        String done = task.isDone() ? DONE : NOT_DONE;
        String description = escape(task.getDescription());
        // Task is not sealed, so the compiler cannot tell that these three types are
        // the only ones; the last branch is what makes this compile, and it turns a
        // task type added without updating this format into a message rather than a
        // crash.
        return switch (task) {
            case Todo todo -> join(TODO_TAG, done, description);
            case Deadline deadline -> join(DEADLINE_TAG, done, description,
                    escape(TaskDates.toStorage(deadline.getBy())));
            case Event event -> join(EVENT_TAG, done, description,
                    escape(TaskDates.toStorage(event.getFrom())),
                    escape(TaskDates.toStorage(event.getTo())));
            default -> throw new AsterException(buildUnknownTypeMessage());
        };
    }

    /**
     * Returns the task a line stores.
     *
     * <p>The line is split into fields before anything is checked, because the number of
     * fields can only be counted once escaped separators have been recognized as part of
     * a field rather than as separators.
     *
     * @param line one line of the file, which must not be blank.
     * @return the task the line describes, with its done status restored.
     * @throws AsterException if the line cannot be understood.
     */
    private Task decode(String line) throws AsterException {
        assert !line.isBlank() : "Blank lines must be skipped before a line is decoded";
        List<String> fields = splitFields(line);
        if (fields.size() < MINIMUM_FIELDS) {
            throw new AsterException(buildUnreadableFileMessage());
        }

        String description = fields.get(DESCRIPTION_INDEX);
        Task task = switch (fields.get(TAG_INDEX)) {
            case TODO_TAG -> {
                requireFieldCount(fields, TODO_FIELDS);
                yield new Todo(requireFilled(description));
            }
            case DEADLINE_TAG -> {
                requireFieldCount(fields, DEADLINE_FIELDS);
                yield new Deadline(requireFilled(description),
                        requireDate(fields.get(FIRST_DETAIL_INDEX)));
            }
            case EVENT_TAG -> {
                requireFieldCount(fields, EVENT_FIELDS);
                yield new Event(requireFilled(description),
                        requireDate(fields.get(FIRST_DETAIL_INDEX)),
                        requireDate(fields.get(SECOND_DETAIL_INDEX)));
            }
            default -> throw new AsterException(buildUnreadableFileMessage());
        };

        // Every task is created not done, so only a stored "done" needs acting on.
        if (isDoneFlag(fields.get(DONE_INDEX))) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the fields of a line, with the escaping undone.
     *
     * <p>A backslash means the character after it is part of the field rather than a
     * separator, so a description containing a separator is read back whole.
     *
     * @param line one line of the file.
     * @return the fields of the line, each without surrounding spaces.
     * @throws AsterException if the line ends in a backslash that escapes nothing.
     */
    private List<String> splitFields(String line) throws AsterException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == ESCAPE_CHAR) {
                // Nothing this program writes ends in a lone backslash, so a line that
                // does has been edited by hand into something that cannot be read.
                if (i + 1 >= line.length()) {
                    throw new AsterException(buildUnreadableFileMessage());
                }
                field.append(line.charAt(i + 1));
                i++;
            } else if (character == SEPARATOR_CHAR) {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Returns the field with every backslash and separator marked as part of the text.
     *
     * @param field the text to write into one field.
     * @return the text with each backslash and each separator preceded by a backslash.
     */
    private static String escape(String field) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (character == ESCAPE_CHAR || character == SEPARATOR_CHAR) {
                escaped.append(ESCAPE_CHAR);
            }
            escaped.append(character);
        }
        return escaped.toString();
    }

    /**
     * Returns the fields joined into one line.
     *
     * @param fields the already escaped fields, in the order they belong.
     * @return the fields separated by the separator.
     */
    private static String join(String... fields) {
        return String.join(SEPARATOR, fields);
    }

    /**
     * Returns whether a stored done flag means the task is done.
     *
     * @param flag the field holding the done flag.
     * @return {@code true} if the flag marks the task done, {@code false} if not.
     * @throws AsterException if the flag is neither of the two values used.
     */
    private boolean isDoneFlag(String flag) throws AsterException {
        if (DONE.equals(flag)) {
            return true;
        }
        if (NOT_DONE.equals(flag)) {
            return false;
        }
        throw new AsterException(buildUnreadableFileMessage());
    }

    /**
     * Checks that a line has exactly the number of fields its type needs.
     *
     * @param fields the fields read from the line.
     * @param expected the number of fields the type needs.
     * @throws AsterException if the line has any other number of fields.
     */
    private void requireFieldCount(List<String> fields, int expected) throws AsterException {
        if (fields.size() != expected) {
            throw new AsterException(buildUnreadableFileMessage());
        }
    }

    /**
     * Returns the field, provided it holds something.
     *
     * @param field the field to check, already without surrounding spaces.
     * @return the field unchanged.
     * @throws AsterException if the field is empty.
     */
    private String requireFilled(String field) throws AsterException {
        if (field.isEmpty()) {
            throw new AsterException(buildUnreadableFileMessage());
        }
        return field;
    }

    /**
     * Returns the date a stored field holds.
     *
     * <p>A field that does not hold a date makes the whole record unreadable, which is
     * also what happens to a file written before dates were stored in this form: the
     * file is left exactly as it is for the user to look at.
     *
     * @param field the field to read, already without surrounding spaces.
     * @return the date the field holds.
     * @throws AsterException if the field does not hold a date in the stored form.
     */
    private LocalDate requireDate(String field) throws AsterException {
        LocalDate date = TaskDates.parseOrNull(field);
        if (date == null) {
            throw new AsterException(buildUnreadableFileMessage());
        }
        return date;
    }

    /**
     * Moves a finished file onto the target, replacing it.
     *
     * <p>The move is asked to be atomic, so that the target is never seen half written.
     * Not every file system can promise that, and those that cannot say so, in which
     * case a plain replacing move is used instead.
     *
     * @param source the finished file.
     * @param target the file to replace.
     * @throws IOException if the move cannot be made.
     */
    private static void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException e) {
            // Some file systems cannot move atomically, and some report the target as
            // already there instead of replacing it. Either way the atomic attempt is
            // simply unavailable, so the ordinary replacing move is used instead. Any
            // other failure is a real one and is left to reach the caller.
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes a file if it is still there, ignoring any failure to do so.
     *
     * @param path the file to remove.
     */
    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // The result of the save has already been decided, and a leftover temporary
            // file is harmless, so there is nothing useful to report here.
        }
    }

    /**
     * Returns the message shown when the saved file cannot be read or understood.
     *
     * @return the explanation to show the user.
     */
    private String buildUnreadableFileMessage() {
        return "I couldn't read your saved tasks from " + file + ", so I've stopped without "
                + "changing anything. Please check or move that file, then start me again.";
    }

    /**
     * Returns the message shown when the tasks cannot be written.
     *
     * @return the explanation to show the user.
     */
    private String buildUnwritableFileMessage() {
        return "I couldn't save your tasks. Your latest changes may not be available next time.";
    }

    /**
     * Returns the message shown when a task is of a type this format does not cover.
     *
     * @return the explanation to show the user.
     */
    private String buildUnknownTypeMessage() {
        return "I don't know how to save one of your tasks, so nothing was written.";
    }
}
