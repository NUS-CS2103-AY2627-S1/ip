package laby;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import laby.contact.Contact;
import laby.contact.ContactList;
import laby.task.Task;
import laby.task.TaskList;

/** Reads tasks and contacts from and writes them to the application's data file. */
public class Storage {
    private final String filePath;

    /**
     * Creates storage backed by the file at {@code filePath}.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Creates the data file and its parent directories when they do not exist.
     *
     * @throws LabyException If the file or its parent directories cannot be created.
     */
    private void createFile() throws LabyException {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                boolean hasParentFile = file.getParentFile() != null && file.getParentFile().exists();
                if (!hasParentFile) {
                    boolean isSuccess = file.getParentFile().mkdirs();
                    if (!isSuccess) {
                        throw new LabyException("cannot create file");
                    }
                }
                if (!file.createNewFile()) {
                    throw new LabyException("cannot create file");
                }
            }
        } catch (IOException e) {
            throw new LabyException("cannot create file");
        }
    }

    /**
     * Persists every task and contact in the supplied lists.
     *
     * @param taskList Task list to persist.
     * @param contactList Contact list to persist.
     * @throws LabyException If the data file cannot be created or written.
     */
    public void writeFile(TaskList taskList, ContactList contactList) throws LabyException {
        this.createFile();

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(taskList.toFileString());
            fileWriter.write(contactList.toFileString());
        } catch (IOException e) {
            throw new LabyException("cannot write to file");
        }
    }

    /**
     * Loads all persisted tasks, validating their stored format.
     *
     * @return Tasks loaded from the data file.
     * @throws LabyException If the data file cannot be read or has invalid content.
     */
    public List<Task> readTasks() throws LabyException {
        List<Task> tasks = new ArrayList<>();

        this.createFile();
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String current = scanner.nextLine();
                if (!isContactRecord(current)) {
                    Task currentTask = Parser.parseTaskFromFile(current);
                    tasks.add(currentTask);
                }
            }
        } catch (IOException e) {
            throw new LabyException("cannot read from file");
        }

        return tasks;
    }

    /**
     * Loads all persisted contacts, validating their stored format.
     *
     * @return Contacts loaded from the data file.
     * @throws LabyException If the data file cannot be read or has invalid content.
     */
    public List<Contact> readContacts() throws LabyException {
        List<Contact> contacts = new ArrayList<>();

        this.createFile();
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String current = scanner.nextLine();
                if (isContactRecord(current)) {
                    Contact currentContact = Parser.parseContactFromFile(current);
                    contacts.add(currentContact);
                }
            }
        } catch (IOException e) {
            throw new LabyException("cannot read from file");
        }

        return contacts;
    }

    /**
     * Checks whether a serialized record represents a contact.
     *
     * @param record Serialized record to inspect.
     * @return Whether the record has the contact type marker.
     */
    private static boolean isContactRecord(String record) {
        return record.startsWith("C" + Parser.FIELD_SEPARATOR);
    }
}
