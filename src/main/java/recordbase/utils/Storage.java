package recordbase.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import recordbase.exceptions.RecordException;
import recordbase.types.DeadlineItem;
import recordbase.types.EventItem;
import recordbase.types.ListItem;
import recordbase.types.Priority;
import recordbase.types.RecordList;
import recordbase.types.ToDoItem;

/**
 * Provides methods for saving and loading {@code ListItem} objects to and from files.
 *
 * <p>The class handles conversion between list items and their file-based representation.</p>
 */
public class Storage {
    private static final long MAX_SAVE_FILE_BYTES = 256L * 1024 * 1024;
    private static final String FORMAT_HEADER = "# Record save format v2";

    /**
     * Creates a storage utility instance.
     *
     * <p>Persistence methods are static. This constructor preserves the class's original public
     * construction contract.</p>
     */
    public Storage() { }

    /**
     * Saves all items in the specified list to a file.
     *
     * @param list the list whose items are saved
     * @param fileName the name of the file to save the list to
     * @throws RecordException if the file cannot be created or written to
     */
    public static void saveToFile(RecordList list, String fileName) {
        assert list != null : "List must not be null";
        assert fileName != null : "File name must not be null";

        Path path = resolvePath(fileName);
        Path parentDirectory = path.getParent();

        try {
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(FORMAT_HEADER);
                writer.newLine();
                for (ListItem item : list.getItems()) {
                    assert item != null : "List must not contain null items";

                    writer.write(item.saveString());
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (IOException | SecurityException e) {
            throw new RecordException("Unable to save the task list to " + path
                    + ". Check that the location is writable.", e);
        }
    }

    /**
     * Loads items from a file and adds them to the specified list.
     *
     * @param list the list to which the loaded items are added
     * @param fileName the name of the file to load from
     * @throws RecordException if the file does not exist or cannot be read
     */
    public static void loadFromFile(RecordList list, String fileName) {
        assert list != null : "List must not be null";
        assert fileName != null : "File name must not be null";

        Path path = resolvePath(fileName);

        if (Files.notExists(path)) {
            throw new RecordException("No save file to load from.");
        }

        try {
            if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
                throw new RecordException("The save path is not a readable file: " + path);
            }
            if (Files.size(path) > MAX_SAVE_FILE_BYTES) {
                throw new RecordException("The save file is too large to load safely (maximum 256 MB).");
            }
        } catch (IOException | SecurityException exception) {
            throw new RecordException("Unable to inspect the save file. Check its permissions.", exception);
        }

        List<ListItem> loadedItems = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            boolean hasFormatHeader = false;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    hasFormatHeader = FORMAT_HEADER.equals(line);
                    if (!hasFormatHeader) {
                        throw new RecordException("The save file does not use the supported Record v2 format."
                                + " No tasks were loaded.");
                    }
                    continue;
                }
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (loadedItems.size() >= RecordList.MAX_ITEMS) {
                    throw new RecordException("The save file contains more than "
                            + RecordList.MAX_ITEMS + " tasks. No tasks were loaded.");
                }
                try {
                    loadedItems.add(parseItem(line));
                } catch (RuntimeException exception) {
                    throw new RecordException("The save file is corrupted or uses an unsupported format"
                            + " at line " + lineNumber + ". No tasks were loaded.", exception);
                }
            }
            if (!hasFormatHeader) {
                throw new RecordException("The save file does not use the supported Record v2 format."
                        + " No tasks were loaded.");
            }
            for (ListItem item : loadedItems) {
                list.addItem(item);
            }
        } catch (IOException | SecurityException e) {
            throw new RecordException("Unable to read the save file. Check its permissions.", e);
        }

    }

    /**
     * Parses a line from a save file into a {@code ListItem}.
     *
     * @param line the line representing a saved list item
     * @return the {@code ListItem} represented by the line
     * @throws RecordException if the line contains an unknown item type
     */
    private static ListItem parseItem(String line) {
        List<String> fields = parseCsvLine(line);
        if (fields.size() < 4) {
            throw new RecordException("Saved item does not contain enough fields.");
        }

        boolean isDone = parseCompletionStatus(fields.get(1));
        Priority priority = Priority.fromString(fields.get(2));
        String description = fields.get(3);
        if (description.isBlank()) {
            throw new RecordException("Saved item has an empty description.");
        }

        ListItem item = switch (fields.get(0)) {
            case "T" -> parseToDoItem(fields, description, priority);
            case "D" -> parseDeadlineItem(fields, description, priority);
            case "E" -> parseEventItem(fields, description, priority);
            default -> throw new RecordException("Unknown item type: " + fields.get(0));
        };

        if (isDone) {
            item.setDone();
        }

        return item;
    }

    private static boolean parseCompletionStatus(String value) {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new RecordException("Completion status must be either 0 or 1.");
        };
    }

    /**
     * Parses a saved to-do item from a line in the save file.
     *
     * @param fields parsed CSV fields
     * @param description task description
     * @param priority task priority
     * @return the parsed {@code ToDoItem}
     */
    private static ListItem parseToDoItem(List<String> fields, String description, Priority priority) {
        return switch (fields.size()) {
            case 4 -> new ToDoItem(description, priority);
            case 5 -> new ToDoItem(description, LocalDateTime.parse(fields.get(4)), priority);
            default -> throw new RecordException("To-do must contain four or five fields.");
        };
    }

    /**
     * Parses a saved deadline item from a line in the save file.
     *
     * @param fields parsed CSV fields
     * @param description task description
     * @param priority task priority
     * @return the parsed {@code DeadlineItem}
     */
    private static ListItem parseDeadlineItem(List<String> fields, String description, Priority priority) {
        if (fields.size() != 5) {
            throw new RecordException("Deadline must contain five fields.");
        }
        return new DeadlineItem(description, LocalDateTime.parse(fields.get(4)), priority);
    }

    /**
     * Parses a saved event item from a line in the save file.
     *
     * @param fields parsed CSV fields
     * @param description task description
     * @param priority task priority
     * @return the parsed {@code EventItem}
     */
    private static ListItem parseEventItem(List<String> fields, String description, Priority priority) {
        if (fields.size() != 6) {
            throw new RecordException("Event must contain six fields.");
        }
        LocalDateTime startDateTime = LocalDateTime.parse(fields.get(4));
        LocalDateTime endDateTime = LocalDateTime.parse(fields.get(5));
        if (endDateTime.isBefore(startDateTime)) {
            throw new RecordException("Event end must not be before its start.");
        }
        return new EventItem(description, startDateTime, endDateTime, priority);
    }

    /**
     * Parses one CSV record, including commas and doubled quotes inside quoted fields.
     *
     * @param line CSV record
     * @return parsed field values
     * @throws RecordException if the CSV quoting is malformed
     */
    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;
        boolean quotedFieldClosed = false;

        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            if (insideQuotes) {
                if (current != '"') {
                    currentField.append(current);
                } else if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    currentField.append('"');
                    index++;
                } else {
                    insideQuotes = false;
                    quotedFieldClosed = true;
                }
            } else if (current == ',') {
                fields.add(currentField.toString());
                currentField.setLength(0);
                quotedFieldClosed = false;
            } else if (current == '"') {
                if (!currentField.isEmpty() || quotedFieldClosed) {
                    throw new RecordException("Quote found in an invalid position.");
                }
                insideQuotes = true;
            } else {
                if (quotedFieldClosed) {
                    throw new RecordException("Closing quote must be followed by a comma or line end.");
                }
                currentField.append(current);
            }
        }
        if (insideQuotes) {
            throw new RecordException("CSV field has no closing quote.");
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Resolves and normalizes a storage path.
     *
     * <p>Relative paths are resolved against the application's current working directory.
     * Normalization also handles current-folder and parent-folder path segments.</p>
     *
     * @param fileName user-supplied absolute or relative path
     * @return normalized absolute path
     * @throws RecordException if the path is blank or cannot be parsed
     */
    private static Path resolvePath(String fileName) {
        if (fileName.isBlank()) {
            throw new RecordException("Please provide a file path.");
        }
        try {
            return Paths.get(fileName).toAbsolutePath().normalize();
        } catch (RuntimeException exception) {
            throw new RecordException("The file path is invalid.", exception);
        }
    }
}
