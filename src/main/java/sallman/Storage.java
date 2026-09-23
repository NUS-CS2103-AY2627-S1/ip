package sallman;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import sallman.task.Deadline;
import sallman.task.Event;
import sallman.task.Task;
import sallman.task.Todo;


/**
 * Loads the task list from disk when the chatbot starts, and writes it back
 * whenever the list changes.
 * <p>
 * Each task occupies one line, with fields separated by {@code " | "}:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-10-15
 * E | 0 | project meeting | 2019-10-15 | 2019-10-16
 * T | 0 | read book | #fun,books
 * </pre>
 * The first field is the task type, the second is 1 when the task is done.
 * A line may end with a tag field, recognized by its {@code #} prefix; a task
 * with no tags simply leaves it out, so files written before tags existed are
 * still read correctly.
 */
public class Storage {

    /**
     * What separates the fields on one line of the data file. Taken from the
     * task classes that write those lines, so the reader and the writer cannot
     * disagree about the format.
     */
    private static final String SEPARATOR = Task.SEPARATOR;

    /**
     * The separator as a regular expression, for splitting a line into fields.
     * Quoted rather than written out by hand, so it keeps matching SEPARATOR
     * even if that is ever changed to something containing regex characters.
     */
    private static final String SEPARATOR_PATTERN = Pattern.quote(SEPARATOR);

    /** Where the task list is kept, relative to the folder the app runs in. */
    private final Path file;

    /**
     * Descriptions of any lines the last {@link #load()} could not understand.
     * Kept so the caller can tell the user what was dropped, rather than
     * failing the whole load because of one bad line.
     */
    private final List<String> skippedLines = new ArrayList<>();

    /**
     * Whether the last {@link #load()} found the data file but could not read
     * it. Saving would then replace every task in that file with this session's
     * list, which starts empty, so saving is refused until a load succeeds.
     */
    private boolean isSavingBlocked;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath path to the data file, written with {@code /} separators;
     *                 {@link Path#of} converts them to whatever the current OS
     *                 uses, so the same string works everywhere
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the saved tasks.
     *
     * @return the saved tasks, or an empty list when there is no data file yet
     * @throws SallmanException if the file exists but cannot be read
     */
    public List<Task> load() throws SallmanException {
        List<Task> tasks = new ArrayList<>();
        skippedLines.clear();
        isSavingBlocked = false;
        if (!Files.exists(file)) {
            // First run on this computer: no data file is not an error.
            return tasks;
        }
        byte[] contents;
        try {
            contents = Files.readAllBytes(file);
        } catch (IOException e) {
            isSavingBlocked = true;
            throw new SallmanException("I couldn't read your saved tasks from " + file + ".",
                    "To keep them safe, I won't save any changes over that file this time.");
        }
        List<byte[]> lines = splitLines(contents);
        for (int i = 0; i < lines.size(); i++) {
            String line;
            try {
                line = decodeLine(lines.get(i), i == 0);
            } catch (CharacterCodingException e) {
                // Decoded line by line, so text that is not UTF-8, such as an
                // accented letter saved by an editor using a Windows code page,
                // costs only its own line rather than making the whole file unreadable.
                skippedLines.add("line " + (i + 1) + ": the line is not valid UTF-8 text");
                continue;
            }
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseLine(line));
            } catch (SallmanException e) {
                // One damaged line should not cost the user every other task,
                // so record it and carry on with the rest of the file.
                skippedLines.add("line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Splits a file's bytes into lines, dropping the carriage return that ends
     * each line in a file saved on Windows.
     * <p>
     * Splitting happens before decoding, which is safe because the newline byte
     * never occurs inside a multi-byte UTF-8 character.
     *
     * @param contents the whole file
     * @return the bytes of each line, in order, without line endings
     */
    private static List<byte[]> splitLines(byte[] contents) {
        List<byte[]> lines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= contents.length; i++) {
            if (i == contents.length || contents[i] == '\n') {
                int end = (i > start && contents[i - 1] == '\r') ? i - 1 : i;
                lines.add(Arrays.copyOfRange(contents, start, end));
                start = i + 1;
            }
        }
        return lines;
    }

    /**
     * Decodes one line as UTF-8, refusing bytes that are not valid UTF-8 text
     * rather than quietly replacing them.
     *
     * @param line        the bytes of the line
     * @param isFirstLine whether a byte order mark may begin it
     * @return the line's text
     * @throws CharacterCodingException if the bytes are not valid UTF-8
     */
    private static String decodeLine(byte[] line, boolean isFirstLine) throws CharacterCodingException {
        String text = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(line)).toString();
        // Some Windows editors begin a UTF-8 file with a byte order mark, which is not part of the text.
        return isFirstLine && text.startsWith("﻿") ? text.substring(1) : text;
    }

    /**
     * Returns what went wrong with any lines skipped by the last load.
     *
     * @return one description per skipped line, empty when the file was clean
     */
    public List<String> getSkippedLines() {
        return skippedLines;
    }

    /**
     * Writes the tasks to disk, creating the containing folder if needed.
     *
     * @param tasks the tasks to save
     * @throws SallmanException if the file cannot be written
     */
    public void save(List<Task> tasks) throws SallmanException {
        if (isSavingBlocked) {
            throw new SallmanException("I'm sorry, but I won't save over " + file + ", since I couldn't read it.",
                    "Your change still applies until you exit. Fix or move that file, then restart me.");
        }
        try {
            Path folder = file.getParent();
            if (folder != null) {
                // Creating the folder is harmless when it already exists.
                Files.createDirectories(folder);
            }
            Files.write(file, tasks.stream()
                    .map(Task::toFileFormat)
                    .toList());
        } catch (IOException e) {
            throw new SallmanException("I couldn't save your tasks to " + file + ".",
                    "The change is still in this session, but may be lost when you exit.");
        }
    }

    /**
     * Rebuilds one task from its saved line.
     * <p>
     * Only the type and done flag are fixed fields. A description may itself
     * contain the separator, so it is taken as everything left over once the
     * trailing date fields have been split off from the right.
     *
     * @param line one non-blank line of the data file
     * @return the task that line describes
     * @throws SallmanException if the line is not in the expected format; the
     *                          message is a fragment naming the specific fault,
     *                          for the caller to report against a line number
     */
    private static Task parseLine(String line) throws SallmanException {
        String[] fields = line.split(SEPARATOR_PATTERN, 3);
        if (fields.length < 3) {
            throw new SallmanException("expected at least 3 fields, found " + fields.length);
        }
        // The check above rules out fewer than three fields, and the split was
        // capped at three, so exactly three is all that is left.
        assert fields.length == 3 : "expected 3 fields, found " + fields.length;
        String type = fields[0];
        String doneFlag = fields[1];
        String rest = fields[2];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new SallmanException("the done flag should be 0 or 1, found \""
                    + doneFlag + "\"");
        }

        String[] fieldsAndTags = splitOffTags(rest);
        Task task = readTask(type, fieldsAndTags[0]);
        applyTags(task, fieldsAndTags[1]);
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits the optional tag field off the end of a line's remaining fields.
     * <p>
     * The field is recognized by its {@code #} prefix rather than by its
     * position, because a task may carry dates, tags, both or neither, and
     * because a file saved before tags existed has no such field at all.
     *
     * @param rest everything on the line after the done flag
     * @return the remaining fields at index 0, and the tag field without its
     *         prefix at index 1, the latter empty when there is no tag field
     */
    private static String[] splitOffTags(String rest) {
        int cut = rest.lastIndexOf(SEPARATOR);
        if (cut < 0) {
            return new String[] {rest, ""};
        }
        String last = rest.substring(cut + SEPARATOR.length()).trim();
        if (!last.startsWith(Task.TAG_FIELD_PREFIX)) {
            return new String[] {rest, ""};
        }
        return new String[] {rest.substring(0, cut),
                last.substring(Task.TAG_FIELD_PREFIX.length())};
    }

    /**
     * Attaches the tags read from a saved line to the task built from it.
     *
     * @param task     the task that line describes
     * @param tagField the tag field without its prefix, possibly empty
     * @throws SallmanException if a tag is not one that could have been typed
     */
    private static void applyTags(Task task, String tagField) throws SallmanException {
        if (tagField.isEmpty()) {
            return;
        }
        List<String> tags = Arrays.stream(tagField.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
        for (String tag : tags) {
            if (!Task.isValidTag(tag)) {
                // Loading it would leave a tag that untag refuses to remove.
                throw new SallmanException("the tag \"" + tag + "\" is not a valid tag");
            }
            task.addTag(tag);
        }
    }

    /**
     * Builds the task named by a type marker from the fields that follow the
     * done flag.
     *
     * @param type the type marker from the start of the line
     * @param rest everything on the line after the done flag
     * @return the task those fields describe
     * @throws SallmanException if the type is unknown, or its fields are not
     *                          in the expected shape
     */
    private static Task readTask(String type, String rest) throws SallmanException {
        return switch (type) {
            case "T" -> new Todo(requireDescription(rest));
            case "D" -> readDeadline(rest);
            case "E" -> readEvent(rest);
            default -> throw new SallmanException("unknown task type \"" + type + "\"");
        };
    }

    /**
     * Builds a deadline from its saved fields.
     *
     * @param rest the description followed by the due date
     * @return the deadline that describes
     * @throws SallmanException if the due date is missing or is not a date
     */
    private static Task readDeadline(String rest) throws SallmanException {
        int cut = rest.lastIndexOf(SEPARATOR);
        if (cut < 0) {
            throw new SallmanException("a deadline needs a due date");
        }
        return new Deadline(requireDescription(rest.substring(0, cut)),
                parseStoredDate(after(rest, cut), "due date"));
    }

    /**
     * Builds an event from its saved fields.
     *
     * @param rest the description followed by the start and end dates
     * @return the event that describes
     * @throws SallmanException if either date is missing or is not a date
     */
    private static Task readEvent(String rest) throws SallmanException {
        int endCut = rest.lastIndexOf(SEPARATOR);
        int startCut = endCut < 0 ? -1 : rest.lastIndexOf(SEPARATOR, endCut - 1);
        if (startCut < 0) {
            throw new SallmanException("an event needs both a start and an end");
        }
        LocalDate start = parseStoredDate(rest.substring(startCut + SEPARATOR.length(), endCut)
                .trim(), "start date");
        LocalDate end = parseStoredDate(after(rest, endCut), "end date");
        if (end.isBefore(start)) {
            // The parser never creates such an event, so this line was edited by
            // hand; loading it would put an event in the list no date can find.
            throw new SallmanException("the event ends before it starts");
        }
        return new Event(requireDescription(rest.substring(0, startCut)), start, end);
    }

    /**
     * Returns the text following the separator at the given position.
     *
     * @param text  the field being split
     * @param index where the separator starts
     * @return the trimmed remainder after that separator
     */
    private static String after(String text, int index) {
        return text.substring(index + SEPARATOR.length()).trim();
    }

    /**
     * Checks that a saved description is not blank.
     *
     * @param text the description field as read from the file
     * @return the trimmed description
     * @throws SallmanException if nothing is left after trimming
     */
    private static String requireDescription(String text) throws SallmanException {
        String description = text.trim();
        if (description.isEmpty()) {
            throw new SallmanException("the description is empty");
        }
        return description;
    }

    /**
     * Reads a date stored in the data file.
     *
     * @param text  the saved field
     * @param field what the field represents, for the error message
     * @return the date it represents
     * @throws SallmanException if the field is not a valid date
     */
    private static LocalDate parseStoredDate(String text, String field)
            throws SallmanException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new SallmanException("the " + field + " \"" + text + "\" is not a date");
        }
    }
}
