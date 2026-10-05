package kaykay.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import kaykay.model.ApplicationData;
import kaykay.model.Deadline;
import kaykay.model.Event;
import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.model.Task;
import kaykay.model.TaskList;
import kaykay.model.Todo;
import kaykay.parser.DateTimeParser;

/**
 * Saves and loads application data from the chatbot's data file.
 */
public final class Storage {
    /** The file used to persist tasks for this storage instance. */
    private final File dataFile;

    /** Whether reading the existing data failed during this run. */
    private boolean hasLoadFailed;

    /**
     * Creates a storage component for a specific data file.
     *
     * @param filePath path of the file used to load and save tasks.
     */
    public Storage(String filePath) {
        dataFile = new File(filePath);
    }

    /**
     * Replaces the data file with the current application data.
     *
     * @param data application data to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public void saveData(ApplicationData data) throws IOException {
        if (hasLoadFailed) {
            throw new IOException("Cannot save because the existing data could not be loaded.");
        }

        try {
            File dataDirectory = dataFile.getParentFile();
            if (dataDirectory != null && !dataDirectory.exists() && !dataDirectory.mkdirs()) {
                throw new IOException("Could not create the data directory.");
            }

            File temporaryFile = new File(dataFile.getPath() + ".tmp");
            try {
                try (BufferedWriter writer = Files.newBufferedWriter(
                        temporaryFile.toPath(), StandardCharsets.UTF_8)) {
                    TaskList tasks = data.getTasks();
                    for (int i = 0; i < tasks.size(); i += 1) {
                        Task task = tasks.getTask(i);
                        writer.write(task.toFileFormat());
                        writer.write(System.lineSeparator());
                    }
                    PlaceList places = data.getPlaces();
                    for (int i = 0; i < places.size(); i += 1) {
                        Place place = places.getPlace(i);
                        writer.write(place.toFileFormat());
                        writer.write(System.lineSeparator());
                    }
                }
                replaceDataFile(temporaryFile);
            } finally {
                Files.deleteIfExists(temporaryFile.toPath());
            }
        } catch (SecurityException exception) {
            throw new IOException("Access to the data file was denied.", exception);
        }
    }

    /** Replaces the data file atomically when the file system supports it. */
    private void replaceDataFile(File temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile.toPath(), dataFile.toPath(),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads application data, or returns empty data if the file does not exist.
     *
     * @return tasks and places stored in the data file.
     * @throws IOException if the data file contains an invalid record or cannot be read.
     */
    public ApplicationData loadData() throws IOException {
        try {
            ApplicationData data = readData();
            hasLoadFailed = false;
            return data;
        } catch (IOException exception) {
            hasLoadFailed = true;
            throw exception;
        } catch (SecurityException exception) {
            hasLoadFailed = true;
            throw new IOException("Access to the data file was denied.", exception);
        }
    }

    /** Reads and validates all records in the data file. */
    private ApplicationData readData() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Place> places = new ArrayList<>();
        if (!dataFile.exists()) {
            return new ApplicationData(new TaskList(tasks), new PlaceList(places));
        }

        try (Scanner scanner = new Scanner(dataFile, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.isBlank()) {
                    String[] parts = splitFields(line);
                    if (parts.length > 0 && parts[0].equals("P")) {
                        places.add(parsePlace(parts, line));
                    } else {
                        tasks.add(parseTask(parts, line));
                    }
                }
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        return new ApplicationData(new TaskList(tasks), new PlaceList(places));
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value text to escape.
     * @return escaped text.
     */
    public static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Creates a task from one line in the data file.
     *
     * @param line serialized task data.
     * @return the reconstructed task.
     * @throws IOException if the line does not follow the storage format.
     */
    private static Task parseTask(String[] parts, String line) throws IOException {
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new IOException("Invalid task data: " + line);
        }

        int status = parseStatus(parts[1], line);
        Task task = createTask(parts, line);
        if (status == 1) {
            task.mark();
        }
        return task;
    }

    /** Parses and validates the stored completion status. */
    private static int parseStatus(String value, String line) throws IOException {
        int status;
        try {
            status = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid task status: " + line, exception);
        }
        if (status != 0 && status != 1) {
            throw new IOException("Invalid task status: " + line);
        }
        return status;
    }

    /** Creates the task subtype identified by the stored type field. */
    private static Task createTask(String[] parts, String line) throws IOException {
        Task task;
        try {
            switch (parts[0]) {
                case "T":
                    if (parts.length != 3) {
                        throw new IOException("Invalid todo data: " + line);
                    }
                    task = new Todo(parts[2]);
                    break;
                case "D":
                    if (parts.length != 4) {
                        throw new IOException("Invalid deadline data: " + line);
                    }
                    task = new Deadline(parts[2], DateTimeParser.parse(parts[3]));
                    break;
                case "E":
                    if (parts.length != 5) {
                        throw new IOException("Invalid event data: " + line);
                    }
                    LocalDateTime startDateTime = DateTimeParser.parse(parts[3]);
                    LocalDateTime endDateTime = DateTimeParser.parse(parts[4]);
                    if (!endDateTime.isAfter(startDateTime)) {
                        throw new IOException("Invalid event date range: " + line);
                    }
                    task = new Event(parts[2], startDateTime, endDateTime);
                    break;
                default:
                    throw new IOException("Unknown task type: " + line);
            }
        } catch (DateTimeParseException exception) {
            throw new IOException("Invalid date/time data: " + line, exception);
        }
        return task;
    }

    /**
     * Creates a place from decoded fields in one data-file line.
     *
     * @param parts decoded fields from the line.
     * @param line original serialized place data.
     * @return the reconstructed place.
     * @throws IOException if the fields do not describe a valid place.
     */
    private static Place parsePlace(String[] parts, String line) throws IOException {
        if (parts.length != 7 || parts[1].isBlank()) {
            throw new IOException("Invalid place data: " + line);
        }

        LocalDate visitedOn = null;
        if (!parts[4].isBlank()) {
            try {
                visitedOn = LocalDate.parse(parts[4], Place.VISIT_DATE_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new IOException("Invalid place visit date: " + line, exception);
            }
        }

        Integer rating = null;
        if (!parts[5].isBlank()) {
            try {
                rating = Integer.parseInt(parts[5]);
            } catch (NumberFormatException exception) {
                throw new IOException("Invalid place rating: " + line, exception);
            }
            if (rating < 1 || rating > 5) {
                throw new IOException("Invalid place rating: " + line);
            }
        }
        return new Place(parts[1], parts[2], parts[3], visitedOn, rating, parts[6]);
    }

    /**
     * Splits one storage line at unescaped field separators.
     *
     * @param line serialized task data.
     * @return decoded fields from the line.
     * @throws IOException if the line ends with an incomplete escape sequence.
     */
    private static String[] splitFields(String line) throws IOException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean shouldSkipSeparatorSpace = false;

        for (int i = 0; i < line.length(); i += 1) {
            char character = line.charAt(i);
            if (shouldSkipSeparatorSpace && character == ' ') {
                shouldSkipSeparatorSpace = false;
                continue;
            }
            shouldSkipSeparatorSpace = false;

            if (character == '\\') {
                if (i + 1 >= line.length()) {
                    throw new IOException("Incomplete escape sequence: " + line);
                }
                field.append(character);
                field.append(line.charAt(i + 1));
                i += 1;
            } else if (character == '|') {
                removeSeparatorSpace(field);
                fields.add(unescape(field.toString()));
                field.setLength(0);
                shouldSkipSeparatorSpace = true;
            } else {
                field.append(character);
            }
        }

        fields.add(unescape(field.toString()));
        return fields.toArray(new String[0]);
    }

    /** Removes the one space belonging to the separator before decoding a field. */
    private static void removeSeparatorSpace(StringBuilder field) {
        if (field.length() > 0 && field.charAt(field.length() - 1) == ' ') {
            field.deleteCharAt(field.length() - 1);
        }
    }

    /** Decodes escaped storage characters. */
    private static String unescape(String value) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i += 1) {
            char character = value.charAt(i);
            if (character != '\\') {
                result.append(character);
                continue;
            }
            if (i + 1 >= value.length()) {
                throw new IOException("Incomplete escape sequence in task data.");
            }
            char escapedCharacter = value.charAt(i + 1);
            switch (escapedCharacter) {
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case '|':
                    result.append('|');
                    break;
                case '\\':
                    result.append('\\');
                    break;
                default:
                    throw new IOException("Unknown escape sequence in data: \\" + escapedCharacter);
            }
            i += 1;
        }
        return result.toString();
    }
}
