package glennon;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.Task;
import glennon.task.TaskList;
import glennon.task.Todo;

/**
 * Reads and writes Glennon's mission data using a relative, OS-independent
 * file path.
 */
public class Storage {
    /** Default location of Glennon's saved mission data. */
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "glennon.txt");

    /** Separates fields without conflicting with Base64-encoded task text. */
    private static final String FIELD_SEPARATOR = "\t";

    /** Location of the mission data managed by this storage instance. */
    private final Path dataPath;

    /**
     * Creates storage that uses Glennon's default data file.
     */
    public Storage() {
        this(DEFAULT_DATA_PATH);
    }

    /**
     * Creates storage that uses the specified data file.
     *
     * @param dataPath path of the data file.
     */
    public Storage(Path dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * Returns all saved missions, or an empty list when the data file does not
     * exist.
     *
     * @return saved missions.
     * @throws GlennonException if the data file cannot be read or parsed.
     */
    public List<Task> loadMissions() throws GlennonException {
        try {
            requireValidLoadPath();
            TaskList missions = new TaskList();
            // Stored fields are ASCII. Preserve invalid bytes so parsing reports their line number.
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.ISO_8859_1);
            for (int i = 0; i < lines.size(); i++) {
                Task mission = parseMission(lines.get(i), i + 1);
                try {
                    missions.add(mission);
                } catch (GlennonException e) {
                    throw new GlennonException("Mission data is corrupted at line " + (i + 1)
                            + ": duplicate mission.", e);
                }
            }
            return new ArrayList<>(missions.asList());
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        } catch (AccessDeniedException e) {
            throw new GlennonException(
                    "Glennon cannot read the mission data. Check the file and folder permissions.", e);
        } catch (IOException e) {
            throw new GlennonException("Glennon could not load the mission data.", e);
        }
    }

    /**
     * Distinguishes missing data from invalid paths before OS-specific read errors occur.
     *
     * @throws IOException if an existing ancestor is not a directory or the target is not a regular file.
     */
    private void requireValidLoadPath() throws IOException {
        Path parentPath = dataPath.toAbsolutePath().getParent();
        while (parentPath != null) {
            try {
                if (!Files.readAttributes(parentPath, BasicFileAttributes.class).isDirectory()) {
                    throw new IOException("The mission data parent path is not a directory.");
                }
                break;
            } catch (NoSuchFileException e) {
                // Windows also reports missing paths when a higher ancestor is a file.
                parentPath = parentPath.getParent();
            }
        }
        if (!Files.readAttributes(dataPath, BasicFileAttributes.class).isRegularFile()) {
            throw new IOException("The mission data path is not a regular file.");
        }
    }

    /**
     * Atomically replaces the data file with the specified missions, creating
     * its parent folder when necessary and preserving old data if saving fails.
     *
     * @param missions missions to save.
     * @throws GlennonException if the folder or data file cannot be written.
     */
    public void saveMissions(List<Task> missions) throws GlennonException {
        try {
            List<String> lines = new ArrayList<>();
            for (Task mission : missions) {
                lines.add(formatMission(mission));
            }
            requireRegularSaveTarget();
            Path parentPath = dataPath.toAbsolutePath().getParent();
            Files.createDirectories(parentPath);
            Path temporaryPath = Files.createTempFile(parentPath, ".glennon-", ".tmp");
            try {
                Files.write(temporaryPath, lines, StandardCharsets.UTF_8);
                requireRegularSaveTarget();
                replaceDataFile(temporaryPath);
            } catch (IOException | RuntimeException e) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException cleanupError) {
                    e.addSuppressed(cleanupError);
                }
                throw e;
            }
        } catch (AccessDeniedException e) {
            throw new GlennonException(
                    "Glennon cannot save the mission data. Check the file and folder permissions.", e);
        } catch (IOException e) {
            throw new GlennonException("Glennon could not save the mission data.", e);
        }
    }

    /**
     * Rejects existing paths that are not ordinary data files.
     *
     * @throws IOException if the target is inaccessible, a directory, or a symbolic link.
     */
    private void requireRegularSaveTarget() throws IOException {
        try {
            BasicFileAttributes attributes = Files.readAttributes(
                    dataPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (!attributes.isRegularFile()) {
                throw new IOException("The mission data path is not a regular file.");
            }
        } catch (NoSuchFileException e) {
            // A first save creates the data file once its complete contents are ready.
        }
    }

    /**
     * Atomically installs a fully written file without a destructive fallback.
     *
     * @param temporaryPath completed file in the data file's parent folder.
     * @throws IOException if atomic replacement is unavailable or fails.
     */
    void replaceDataFile(Path temporaryPath) throws IOException {
        Files.move(temporaryPath, dataPath,
                StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Converts one task into Glennon's storage format.
     *
     * @param mission mission to convert.
     * @return storage-ready line.
     * @throws GlennonException if the task type is unsupported.
     */
    private String formatMission(Task mission) throws GlennonException {
        String status = mission.isDone() ? "1" : "0";
        String description = encode(mission.getDescription());
        if (mission instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", status, description);
        }
        if (mission instanceof Deadline deadline) {
            return String.join(
                    FIELD_SEPARATOR,
                    "D",
                    status,
                    description,
                    encode(deadline.getDueDateTime().toString()));
        }
        if (mission instanceof Event event) {
            return String.join(
                    FIELD_SEPARATOR,
                    "E",
                    status,
                    description,
                    encode(event.getStartDateTime().toString()),
                    encode(event.getEndDateTime().toString()));
        }
        throw new GlennonException("Glennon cannot save an unsupported mission type.");
    }

    /**
     * Converts one stored line into its task subtype and completion state.
     *
     * @param line stored mission data.
     * @param lineNumber one-based line number used in error messages.
     * @return restored mission.
     * @throws GlennonException if the line is not valid mission data.
     */
    private Task parseMission(String line, int lineNumber) throws GlennonException {
        try {
            String[] fields = line.split(FIELD_SEPARATOR, -1);
            requireFieldCount(fields);
            String description = decode(fields[2]);
            Task mission = switch (fields[0]) {
                case "T" -> new Todo(description);
                case "D" -> new Deadline(
                        description, LocalDateTime.parse(decode(fields[3])));
                case "E" -> new Event(
                        description,
                        LocalDateTime.parse(decode(fields[3])),
                        LocalDateTime.parse(decode(fields[4])));
                default -> throw new IllegalArgumentException();
            };

            if (fields[1].equals("1")) {
                mission.markAsDone();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException();
            }
            return mission;
        } catch (IllegalArgumentException | DateTimeParseException | CharacterCodingException e) {
            throw new GlennonException(
                    "Mission data is corrupted at line " + lineNumber + ".", e);
        }
    }

    /**
     * Checks that a stored mission has the number of fields required by its
     * type marker.
     *
     * @param fields stored mission fields.
     */
    private void requireFieldCount(String[] fields) {
        int expectedCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException();
        };
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Encodes user-provided text so field separators remain unambiguous.
     *
     * @param value text to encode.
     * @return Base64-encoded text.
     */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes text stored in one mission field.
     *
     * @param value Base64-encoded text.
     * @return decoded user-provided text.
     * @throws CharacterCodingException if the decoded bytes are not valid UTF-8.
     */
    private String decode(String value) throws CharacterCodingException {
        byte[] bytes = Base64.getDecoder().decode(value);
        return StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
    }
}
