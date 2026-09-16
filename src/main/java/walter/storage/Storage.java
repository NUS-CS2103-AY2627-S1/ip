package walter.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import walter.DukeException;
import walter.place.Place;
import walter.task.Deadline;
import walter.task.Event;
import walter.task.Task;
import walter.task.Todo;

/**
 * Loads and saves Walter tasks using the Level-7 line-based format.
 * A default instance uses {@code data/walter.txt}; an alternate path can be supplied for
 * isolated environments such as tests.
 */
public class Storage {
    private static final Path DEFAULT_SAVE_FILE = Path.of("data", "walter.txt");
    private static final Path DEFAULT_PLACE_FILE = Path.of("data", "places.txt");
    private static final String FIELD_SEPARATOR = "\t";
    private static final String TODO_RECORD_TYPE = "T";
    private static final String DEADLINE_RECORD_TYPE = "D";
    private static final String EVENT_RECORD_TYPE = "E";
    private static final String PLACE_RECORD_TYPE = "P";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";
    private static final String EVENT_AT_FORMAT = "AT";
    private static final String EVENT_FROM_TO_FORMAT = "FROM_TO";

    private final Path saveFile;
    private final Path placeFile;

    /**
     * Creates storage using Walter's production save-file location.
     */
    public Storage() {
        this(DEFAULT_SAVE_FILE, DEFAULT_PLACE_FILE);
    }

    /**
     * Creates storage using a specified save-file location.
     *
     * @param saveFile Location used to load and save tasks.
     */
    public Storage(Path saveFile) {
        this(saveFile, saveFile.resolveSibling("places.txt"));
    }

    /**
     * Creates storage using specified task and place save-file locations.
     *
     * @param saveFile Location used to load and save tasks.
     * @param placeFile Location used to load and save places.
     */
    public Storage(Path saveFile, Path placeFile) {
        this.saveFile = saveFile;
        this.placeFile = placeFile;
    }

    /**
     * Loads every persisted task, or returns an empty list when no save file exists.
     *
     * @return Tasks reconstructed from the save file in their stored order.
     * @throws DukeException If the save file cannot be read or any stored task record is
     *         malformed or unsupported.
     */
    public List<Task> load() throws DukeException {
        if (!Files.exists(saveFile)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
                tasks.add(parseStoredTask(line));
            }
            return tasks;
        } catch (IOException exception) {
            throw new DukeException("Walter could not load saved tasks.", exception);
        }
    }

    /**
     * Saves all tasks after creating the parent data directory when needed.
     *
     * @param tasks Tasks to persist in their current order.
     * @throws DukeException If a task type is unsupported or the save file cannot be written.
     */
    public void save(List<Task> tasks) throws DukeException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(toStoredTask(task));
        }

        try {
            Path parentDirectory = saveFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(saveFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DukeException("Walter could not save your tasks.", exception);
        }
    }

    /**
     * Loads saved places in insertion order, or an empty list when no place file exists.
     *
     * @return Places reconstructed from the separate place save file.
     * @throws DukeException If the place file cannot be read or contains a malformed record.
     */
    public List<Place> loadPlaces() throws DukeException {
        if (!Files.exists(placeFile)) {
            return new ArrayList<>();
        }

        try {
            List<Place> places = new ArrayList<>();
            for (String line : Files.readAllLines(placeFile, StandardCharsets.UTF_8)) {
                places.add(parseStoredPlace(line));
            }
            return places;
        } catch (IOException exception) {
            throw new DukeException("Walter could not load saved places.", exception);
        }
    }

    /**
     * Saves places to their separate save file in insertion order.
     *
     * @param places Places to persist.
     * @throws DukeException If the place file cannot be written.
     */
    public void savePlaces(List<Place> places) throws DukeException {
        List<String> lines = new ArrayList<>();
        for (Place place : places) {
            lines.add(String.join(
                    FIELD_SEPARATOR,
                    PLACE_RECORD_TYPE,
                    escapeField(place.getName()),
                    escapeField(place.getAddress())));
        }

        try {
            Path parentDirectory = placeFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(placeFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DukeException("Walter could not save your places.", exception);
        }
    }

    /**
     * Reconstructs one place from its tab-separated storage record.
     */
    private Place parseStoredPlace(String line) throws DukeException {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length != 3 || !fields[0].equals(PLACE_RECORD_TYPE)) {
            throw new DukeException("Malformed saved place record.");
        }
        return new Place(
                requireStoredPlaceText(fields[1]),
                requireStoredPlaceText(fields[2]));
    }

    /**
     * Reconstructs one task while rejecting malformed or unknown records.
     *
     * @param line Encoded save-file record.
     * @return Task reconstructed from the record.
     * @throws DukeException If the record has invalid fields, status, type, or escaped text.
     */
    private Task parseStoredTask(String line) throws DukeException {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 3) {
            throw new DukeException("Malformed saved task record.");
        }

        boolean isDone = parseStoredStatus(fields[1]);
        Task task = createStoredTask(fields);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the completion-status field of a saved task record.
     *
     * @param status Stored completion-status token.
     * @return Whether the saved task was completed.
     * @throws DukeException If the token is not a supported task status.
     */
    private boolean parseStoredStatus(String status) throws DukeException {
        if (status.equals(DONE_STATUS)) {
            return true;
        }
        if (status.equals(NOT_DONE_STATUS)) {
            return false;
        }
        throw new DukeException("Malformed saved task status.");
    }

    /**
     * Creates a task from validated storage fields for one supported record type.
     *
     * @param fields Fields from one saved task record.
     * @return Task represented by the fields.
     * @throws DukeException If the task type, field count, or field content is invalid.
     */
    private Task createStoredTask(String[] fields) throws DukeException {
        if (fields[0].equals(TODO_RECORD_TYPE) && fields.length == 3) {
            return new Todo(requireStoredText(fields[2]));
        }
        if (fields[0].equals(DEADLINE_RECORD_TYPE) && fields.length == 4) {
            return new Deadline(
                    requireStoredText(fields[2]), parseStoredDate(requireStoredText(fields[3])));
        }
        if (fields[0].equals(EVENT_RECORD_TYPE) && fields.length == 5
                && fields[2].equals(EVENT_AT_FORMAT)) {
            return new Event(requireStoredText(fields[3]), requireStoredText(fields[4]));
        }
        if (fields[0].equals(EVENT_RECORD_TYPE) && fields.length == 6
                && fields[2].equals(EVENT_FROM_TO_FORMAT)) {
            return new Event(
                    requireStoredText(fields[3]),
                    requireStoredText(fields[4]),
                    requireStoredText(fields[5]));
        }
        throw new DukeException("Unknown saved task record.");
    }

    /**
     * Converts one task to the existing deterministic storage representation.
     *
     * @param task Task to encode.
     * @return Tab-separated storage record for the task.
     * @throws DukeException If the task has an unsupported runtime type.
     */
    private String toStoredTask(Task task) throws DukeException {
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        String description = escapeField(task.getDescription());
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, TODO_RECORD_TYPE, status, description);
        }
        if (task instanceof Deadline deadline) {
            return String.join(
                    FIELD_SEPARATOR, DEADLINE_RECORD_TYPE, status, description,
                    deadline.getBy().toString());
        }
        if (task instanceof Event event && event.isAtFormat()) {
            return String.join(
                    FIELD_SEPARATOR, EVENT_RECORD_TYPE, status, EVENT_AT_FORMAT, description,
                    escapeField(event.getAt()));
        }
        if (task instanceof Event event) {
            return String.join(
                    FIELD_SEPARATOR,
                    EVENT_RECORD_TYPE,
                    status,
                    EVENT_FROM_TO_FORMAT,
                    description,
                    escapeField(event.getFrom()),
                    escapeField(event.getTo()));
        }
        throw new DukeException("Walter could not save an unknown task type.");
    }

    /**
     * Parses a stored ISO date without leaking date parsing exceptions.
     *
     * @param dateText Stored ISO date text.
     * @return Parsed deadline date.
     * @throws DukeException If the stored date is invalid.
     */
    private LocalDate parseStoredDate(String dateText) throws DukeException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new DukeException("Malformed saved deadline date.", exception);
        }
    }

    /**
     * Decodes one required text field and rejects empty persisted values.
     *
     * @param field Encoded storage field.
     * @return Decoded non-empty text.
     * @throws DukeException If the field is empty or contains malformed escape syntax.
     */
    private String requireStoredText(String field) throws DukeException {
        String text = unescapeField(field, "Malformed saved task text.");
        if (text.isEmpty()) {
            throw new DukeException("Saved task text cannot be empty.");
        }
        return text;
    }

    /**
     * Decodes one required place field and rejects empty persisted values.
     */
    private String requireStoredPlaceText(String field) throws DukeException {
        String text = unescapeField(field, "Malformed saved place text.");
        if (text.isEmpty()) {
            throw new DukeException("Saved place text cannot be empty.");
        }
        return text;
    }

    /**
     * Escapes control characters that would otherwise interfere with the storage format.
     *
     * @param field Text to encode for storage.
     * @return Text with backslashes and supported control characters escaped.
     */
    private String escapeField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Restores a text field escaped by {@link #escapeField(String)}.
     *
     * @param field Encoded storage field.
     * @return Decoded text.
     * @throws DukeException If an escape sequence is incomplete or unsupported.
     */
    private String unescapeField(String field, String malformedTextMessage) throws DukeException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char current = field.charAt(i);
            if (current != '\\') {
                result.append(current);
                continue;
            }
            if (i + 1 >= field.length()) {
                throw new DukeException(malformedTextMessage);
            }

            char escaped = field.charAt(++i);
            switch (escaped) {
                case '\\' -> result.append('\\');
                case 't' -> result.append('\t');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                default -> throw new DukeException(malformedTextMessage);
            }
        }
        return result.toString();
    }
}
