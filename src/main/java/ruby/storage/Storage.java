package ruby.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ruby.RubyException;
import ruby.contact.Contact;
import ruby.contact.ContactDataFormat;
import ruby.contact.ContactList;
import ruby.task.Deadline;
import ruby.task.Event;
import ruby.task.Task;
import ruby.task.TaskDataFormat;
import ruby.task.TaskList;
import ruby.task.Todo;

/**
 * Loads tasks and contacts from and saves them to a data file on the hard disk.
 */
public class Storage {
    private static final String PART_SEPARATOR_REGEX = " \\| ";
    private static final int MIN_TASK_FIELDS = 3;
    private static final int MIN_DEADLINE_FIELDS = 4;
    private static final int MIN_EVENT_FIELDS = 5;
    private static final int CONTACT_FIELDS = 5;

    private final String filePath;

    /**
     * Creates a storage handler for the specified data file.
     *
     * @param filePath Location of the data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Represents the tasks and contacts restored from the data file.
     *
     * @param tasks    Tasks restored from disk.
     * @param contacts Contacts restored from disk.
     */
    public record Data(TaskList tasks, ContactList contacts) {
    }

    /**
     * Reads the data saved in the data file.
     *
     * @return The saved tasks and contacts, or empty lists when no data file
     *         exists yet.
     * @throws RubyException If the data file exists but cannot be read.
     */
    public Data load() throws RubyException {
        return loadDataFromFile(new File(filePath));
    }

    /**
     * Writes every task and contact to the data file, overwriting any previous
     * contents.
     *
     * @param taskList    Tasks to save.
     * @param contactList Contacts to save.
     * @throws RubyException If the data file cannot be written.
     */
    public void save(TaskList taskList, ContactList contactList) throws RubyException {
        saveDataToFile(taskList, contactList, new File(filePath));
    }

    /**
     * Reads the data saved in the data file.
     *
     * @param file The data file to read.
     * @return The saved tasks and contacts, or empty lists when no data file
     *         exists yet.
     * @throws RubyException If the data file exists but cannot be read.
     */
    private Data loadDataFromFile(File file) throws RubyException {
        TaskList taskList = new TaskList();
        ContactList contactList = new ContactList();
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new RubyException("The data folder could not be created.");
        }
        if (file.isDirectory() || (file.exists() && !file.canRead())) {
            throw new RubyException("The data file cannot be read.");
        }

        if (!file.exists()) {
            return new Data(taskList, contactList);
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                parseLine(line, taskList, contactList);
            }
        } catch (FileNotFoundException | SecurityException exception) {
            throw new RubyException("The data file could not be read.");
        }

        return new Data(taskList, contactList);
    }

    /**
     * Writes every task and contact to the data file, overwriting any previous
     * contents.
     *
     * @param taskList    Tasks to save.
     * @param contactList Contacts to save.
     * @param file        The data file to write.
     * @throws RubyException If the data file cannot be written.
     */
    private static void saveDataToFile(TaskList taskList, ContactList contactList, File file) throws RubyException {
        File parentDir = file.getParentFile();

        assert taskList != null : "TaskList should not be null when saving.";
        assert contactList != null : "ContactList should not be null when saving.";

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new RubyException("The data folder could not be created.");
        }
        if (file.isDirectory() || (file.exists() && !file.canWrite())) {
            throw new RubyException("The data file cannot be written.");
        }

        String taskData = taskList.toDataString();
        String contactData = contactList.toDataString();

        try (FileWriter writer = new FileWriter(file)) {
            if (!taskData.isEmpty()) {
                writer.write(taskData);
            }
            if (!taskData.isEmpty() && !contactData.isEmpty()) {
                writer.write("\n");
            }
            if (!contactData.isEmpty()) {
                writer.write(contactData);
            }
        } catch (IOException | SecurityException exception) {
            throw new RubyException("The data could not be saved to disk.");
        }
    }

    /**
     * Rebuilds one task or contact from one line of the data file and adds it
     * to the matching list.
     *
     * @param line        One line of the data file.
     * @param taskList    List to add a parsed task to.
     * @param contactList List to add a parsed contact to.
     * @throws RubyException If the line is malformed or has an unknown type.
     */
    private static void parseLine(String line, TaskList taskList, ContactList contactList) throws RubyException {
        String[] parts = line.split(PART_SEPARATOR_REGEX, -1);
        if (parts.length > 0 && ContactDataFormat.CONTACT_TYPE.equals(parts[0])) {
            contactList.addContact(parseContact(parts));
            return;
        }
        if (parts.length < MIN_TASK_FIELDS) {
            throw new RubyException("The data file contains a malformed task line.");
        }

        String type = parts[0];
        String mark = parts[1];
        if (!TaskDataFormat.DONE_MARKER.equals(mark) && !TaskDataFormat.NOT_DONE_MARKER.equals(mark)) {
            throw new RubyException("The data file contains an invalid task status.");
        }
        boolean isDone = TaskDataFormat.DONE_MARKER.equals(mark);

        Task task;
        switch (type) {
            case TaskDataFormat.TODO_TYPE:
                task = new Todo(requireDescription(joinParts(parts, 2, parts.length)));
                break;
            case TaskDataFormat.DEADLINE_TYPE:
                if (parts.length < MIN_DEADLINE_FIELDS) {
                    throw new RubyException("The data file contains a malformed deadline.");
                }
                task = new Deadline(requireDescription(joinParts(parts, 2, parts.length - 1)),
                        parseSavedDateTime(parts[parts.length - 1]));
                break;
            case TaskDataFormat.EVENT_TYPE:
                if (parts.length < MIN_EVENT_FIELDS) {
                    throw new RubyException("The data file contains a malformed event.");
                }
                LocalDateTime start = parseSavedDateTime(parts[parts.length - 2]);
                LocalDateTime end = parseSavedDateTime(parts[parts.length - 1]);
                if (!start.isBefore(end)) {
                    throw new RubyException("The data file contains an event that does not end after it starts.");
                }
                task = new Event(requireDescription(joinParts(parts, 2, parts.length - 2)), start, end);
                break;
            default:
                throw new RubyException("The data file contains an unknown task type: " + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        taskList.restoreItem(task);
    }

    /**
     * Rebuilds a contact from the parts of a saved contact line.
     *
     * @param parts Parts of a split data line.
     * @return The reconstructed contact.
     * @throws RubyException If the line does not have exactly the required
     *                       fields or has a blank name.
     */
    private static Contact parseContact(String[] parts) throws RubyException {
        if (parts.length != CONTACT_FIELDS) {
            throw new RubyException("The data file contains a malformed contact line.");
        }
        String name = parts[1];
        if (name.isBlank()) {
            throw new RubyException("The data file contains a contact with an empty name.");
        }
        String phoneNumber = parts[2];
        String email = parts[3];
        String address = parts[4];
        return new Contact(name, phoneNumber, email, address);
    }

    /**
     * Returns a non-blank task description loaded from disk.
     *
     * @param description Description to validate.
     * @return The validated description.
     * @throws RubyException If the description is blank.
     */
    private static String requireDescription(String description) throws RubyException {
        if (description.isBlank()) {
            throw new RubyException("The data file contains a task with an empty description.");
        }
        return description;
    }

    /**
     * Joins the parts between two indexes back together with the {@code " | "}
     * separator,
     * so that descriptions containing the separator are preserved.
     *
     * @param parts Parts of a split data line.
     * @param start First index to include.
     * @param end   Index after the last part to include.
     * @return The joined text, or an empty string when the range is empty.
     */
    private static String joinParts(String[] parts, int start, int end) {
        assert start >= 0 && start <= end && end <= parts.length : "Invalid range for joining parts.";
        return IntStream.range(start, end)
                .mapToObj(i -> parts[i])
                .collect(Collectors.joining(TaskDataFormat.PART_SEPARATOR));
    }

    /**
     * Reads a date stored in the ISO format written by toDataString().
     *
     * @param text Date text from the data file.
     * @return The parsed date and time.
     * @throws RubyException If the text is not a valid ISO date and time.
     */
    private static LocalDateTime parseSavedDateTime(String text) throws RubyException {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException exception) {
            throw new RubyException("The data file contains an unreadable date.");
        }
    }
}
