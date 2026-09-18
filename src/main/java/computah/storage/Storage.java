package computah.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import computah.client.Client;
import computah.exception.ComputahException;
import computah.task.Deadline;
import computah.task.Event;
import computah.task.Task;
import computah.task.ToDo;
import computah.util.DateTimeUtil;

/**
 * Loads and saves task and client data in separate files.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " \\| ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String CLIENT_TYPE = "C";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";
    private static final String LOAD_ERROR_MESSAGE = "I could not load the task list.";
    private static final String CLIENT_LOAD_ERROR_MESSAGE = "I could not load the client list.";

    private static final int PRESERVE_TRAILING_EMPTY_FIELDS = -1;
    private static final int TASK_TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int FIRST_DETAIL_INDEX = 3;
    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int CLIENT_FIELD_COUNT = 4;
    private static final int CLIENT_NAME_INDEX = 1;
    private static final int CLIENT_PHONE_INDEX = 2;
    private static final int CLIENT_EMAIL_INDEX = 3;

    private final String taskFilePath;
    private final String clientFilePath;

    /**
     * Creates a storage component for the given task and client files.
     *
     * @param taskFilePath path to the task save file.
     * @param clientFilePath path to the client save file.
     */
    public Storage(String taskFilePath, String clientFilePath) {
        this.taskFilePath = taskFilePath;
        this.clientFilePath = clientFilePath;
    }

    /**
     * Saves the given task list to the save file, overwriting previous contents.
     *
     * @param tasks tasks to save.
     * @throws ComputahException if the data directory or save file cannot be written.
     */
    public void saveTasks(ArrayList<Task> tasks) throws ComputahException {
        File dataFile = new File(taskFilePath);
        File dataDirectory = dataFile.getParentFile();
        if (dataDirectory != null && !dataDirectory.exists() && !dataDirectory.mkdirs()) {
            throw new ComputahException("I could not create the data directory.");
        }
        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new ComputahException("I could not save the task list.");
        }
    }

    /**
     * Loads tasks from the save file.
     *
     * @return saved tasks, or an empty list if the save file does not exist.
     * @throws ComputahException if the save file cannot be read or contains malformed task data.
     */
    public ArrayList<Task> loadTasks() throws ComputahException {
        ArrayList<Task> tasks = new ArrayList<>();
        File dataFile = new File(taskFilePath);
        if (!dataFile.exists()) {
            return tasks;
        }
        try (Scanner fileScanner = new Scanner(dataFile)) {
            while (fileScanner.hasNextLine()) {
                tasks.add(createTaskFromFileString(fileScanner.nextLine()));
            }
        } catch (IOException e) {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
        return tasks;
    }

    /**
     * Saves the given client list to the client save file.
     *
     * @param clients clients to save.
     * @throws ComputahException if the data directory or client file cannot be written.
     */
    public void saveClients(ArrayList<Client> clients) throws ComputahException {
        File dataFile = new File(clientFilePath);
        File dataDirectory = dataFile.getParentFile();
        if (dataDirectory != null && !dataDirectory.exists() && !dataDirectory.mkdirs()) {
            throw new ComputahException("I could not create the data directory.");
        }
        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Client client : clients) {
                writer.write(client.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new ComputahException("I could not save the client list.");
        }
    }

    /**
     * Loads clients from the client save file.
     *
     * @return saved clients, or an empty list if the client file does not exist.
     * @throws ComputahException if the client file cannot be read or contains malformed data.
     */
    public ArrayList<Client> loadClients() throws ComputahException {
        ArrayList<Client> clients = new ArrayList<>();
        File dataFile = new File(clientFilePath);
        if (!dataFile.exists()) {
            return clients;
        }
        try (Scanner fileScanner = new Scanner(dataFile)) {
            while (fileScanner.hasNextLine()) {
                clients.add(createClientFromFileString(fileScanner.nextLine()));
            }
        } catch (IOException e) {
            throw new ComputahException(CLIENT_LOAD_ERROR_MESSAGE);
        }
        return clients;
    }

    /**
     * Creates a client from one line in the client save file.
     *
     * @param line save-file line to parse.
     * @return client represented by the line.
     * @throws ComputahException if the line does not match the client save-file format.
     */
    private Client createClientFromFileString(String line) throws ComputahException {
        String[] parts = line.split(FIELD_SEPARATOR, PRESERVE_TRAILING_EMPTY_FIELDS);
        if (parts.length != CLIENT_FIELD_COUNT || !parts[TASK_TYPE_INDEX].equals(CLIENT_TYPE)) {
            throw new ComputahException(CLIENT_LOAD_ERROR_MESSAGE);
        }
        try {
            return new Client(parts[CLIENT_NAME_INDEX], parts[CLIENT_PHONE_INDEX], parts[CLIENT_EMAIL_INDEX]);
        } catch (ComputahException e) {
            throw new ComputahException(CLIENT_LOAD_ERROR_MESSAGE);
        }
    }

    /**
     * Creates a task from one line in the save file.
     *
     * @param line save-file line to parse.
     * @return task represented by the line.
     * @throws ComputahException if the line does not match the save-file format.
     */
    private Task createTaskFromFileString(String line) throws ComputahException {
        String[] parts = line.split(FIELD_SEPARATOR, PRESERVE_TRAILING_EMPTY_FIELDS);
        if (parts.length < MINIMUM_FIELD_COUNT) {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
        Task task;
        if (parts[TASK_TYPE_INDEX].equals(TODO_TYPE)) {
            validateSavedLine(parts, TODO_FIELD_COUNT);
            task = new ToDo(parts[DESCRIPTION_INDEX]);
        } else if (parts[TASK_TYPE_INDEX].equals(DEADLINE_TYPE)) {
            validateSavedLine(parts, DEADLINE_FIELD_COUNT);
            task = new Deadline(parts[DESCRIPTION_INDEX], parseSavedDateTime(parts[FIRST_DETAIL_INDEX]));
        } else if (parts[TASK_TYPE_INDEX].equals(EVENT_TYPE)) {
            validateSavedLine(parts, EVENT_FIELD_COUNT);
            task = new Event(parts[DESCRIPTION_INDEX], parseSavedDateTime(parts[FIRST_DETAIL_INDEX]),
                    parseSavedDateTime(parts[FIRST_DETAIL_INDEX + 1]));
        } else {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
        if (parts[STATUS_INDEX].equals(DONE_STATUS)) {
            task.markAsDone();
        } else if (!parts[STATUS_INDEX].equals(NOT_DONE_STATUS)) {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
        return task;
    }

    /**
     * Checks that a saved task line has the exact field count and no empty data fields.
     *
     * @param parts fields split from one save-file line.
     * @param expectedLength expected number of fields for the task type.
     * @throws ComputahException if the saved line is malformed.
     */
    private void validateSavedLine(String[] parts, int expectedLength) throws ComputahException {
        if (parts.length != expectedLength || parts[DESCRIPTION_INDEX].isEmpty()) {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
        for (int i = FIRST_DETAIL_INDEX; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                throw new ComputahException(LOAD_ERROR_MESSAGE);
            }
        }
    }

    /**
     * Parses a saved date/time while converting date parse failures into storage load failures.
     *
     * @param text saved date/time text.
     * @return parsed date/time.
     * @throws ComputahException if the saved date/time is invalid.
     */
    private LocalDateTime parseSavedDateTime(String text) throws ComputahException {
        try {
            return DateTimeUtil.parse(text);
        } catch (ComputahException e) {
            throw new ComputahException(LOAD_ERROR_MESSAGE);
        }
    }
}
