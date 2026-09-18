package linus.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import linus.invalidtaskexception.InvalidTaskException;
import linus.task.Deadline;
import linus.task.Event;
import linus.task.Task;
import linus.task.ToDo;

/**
 * Represents the file stored on the local hard drive which corresponds
 * to the TaskList.
 */
public class Storage {
    private static final String CREATE_STORAGE_ERROR =
            "Here's a tech tip! I'm unable to create the storage for the tasklist file :-(";
    private static final String LOAD_FILE_ERROR = "Here's a tech tip! I'm unable to open and load the tasklist file.";
    private static final String LOAD_TASK_ERROR = "Here's a tech tip! I'm unable to load the invalid task from storage";
    private static final String LOAD_TODO_TASK_ERROR =
            "Here's a tech tip! I'm unable to load the invalid ToDo task from storage";
    private static final String LOAD_DEADLINE_TASK_ERROR =
            "Here's a tech tip! I'm unable to load the invalid deadline task from storage";
    private static final String LOAD_EVENT_TASK_ERROR =
            "Here's a tech tip! I'm unable to load the invalid Event task from storage";
    private static final String SAVE_FILE_ERROR = "Here's a tech tip! I'm unable to open and save the tasklist file.";

    private static final String TEXT_SPLIT_REGEX = "\\|";
    private static final String TODO_TASK_TYPE = "T";
    private static final String DEADLINE_TASK_TYPE = "D";
    private static final String EVENT_TASK_TYPE = "E";
    private static final String TASK_COMPLETED_STATUS = "X";

    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int TASK_DEADLINE_INDEX = 3;
    private static final int TASK_START_DATE_INDEX = 3;
    private static final int TASK_END_DATE_INDEX = 4;

    private static final int MIN_TEXT_SPLIT_PARTS = 3;
    private static final int TODO_SPLIT_PARTS = 3;
    private static final int DEADLINE_SPLIT_PARTS = 4;
    private static final int EVENT_SPLIT_PARTS = 5;
    private final File file;

    /**
     * Creates the file to store the TaskList. If the file already exists,
     * use the existing file.
     *
     * @param path The filepath of the file to be used as storage.
     * @throws IOException When the tasklist storage file cannot be created.
     */
    public Storage(String path) throws IOException {
        assert path != null : "No filepath provided to create storage.";
        this.file = new File(path);
        if (this.file.exists()) {
            return;
        }
        try {
            this.createStorage(this.file.toPath());
        } catch (IOException e) {
            throw new IOException(CREATE_STORAGE_ERROR, e);
        }
    }

    private void createStorage(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.createFile(path);
    }


    /**
     * Loads the tasks in the storage file into the TaskList.
     *
     * @return Tasklist of tasks corresponding to the local storage tasklist file.
     * @throws FileNotFoundException When there is an error opening and accessing the file to load the tasklist.
     * @throws InvalidTaskException When the constructor is unable to load the tasklist due to malformed task format.
     */
    public List<Task> loadFile() throws FileNotFoundException, InvalidTaskException {
        try (Scanner scanner = new Scanner(this.file)) {
            return createTasklist(scanner);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(LOAD_FILE_ERROR);
        }
    }

    private List<Task> createTasklist(Scanner scanner) throws InvalidTaskException {
        List<Task> taskList = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String text = scanner.nextLine();
            Task task = this.createTask(text);
            taskList.add(task);
        }
        return taskList;
    }

    private Task createTask(String text) throws InvalidTaskException {
        String[] parts = text.split(TEXT_SPLIT_REGEX, 3);
        if (parts.length < MIN_TEXT_SPLIT_PARTS) {
            throw new InvalidTaskException(LOAD_TASK_ERROR);
        }
        String taskType = parts[TASK_TYPE_INDEX].trim();
        String taskStatus = parts[TASK_STATUS_INDEX].trim();
        String remainder = parts[TASK_DESCRIPTION_INDEX].substring(1);
        boolean isDone = parseTaskStatus(taskStatus);
        switch (taskType) {
            case TODO_TASK_TYPE:
                return createToDoTask(isDone, remainder);
            case DEADLINE_TASK_TYPE:
                return createDeadlineTask(isDone, remainder);
            case EVENT_TASK_TYPE:
                return createEventTask(isDone, remainder);
            default:
                throw new InvalidTaskException(LOAD_TASK_ERROR);
        }
    }

    private boolean parseTaskStatus(String status) throws InvalidTaskException {
        if (status.equals(TASK_COMPLETED_STATUS)) {
            return true;
        } else if (status.isEmpty()) {
            return false;
        } else {
            throw new InvalidTaskException(LOAD_TASK_ERROR);
        }
    }

    private ToDo createToDoTask(boolean isDone, String description) throws InvalidTaskException {
        return new ToDo(isDone, description);
    }

    private Deadline createDeadlineTask(boolean isDone, String text)
            throws InvalidTaskException {
        int delimiterIndex = text.lastIndexOf('|');
        if (delimiterIndex < 0) {
            throw new InvalidTaskException(LOAD_DEADLINE_TASK_ERROR);
        }
        String description = text.substring(0, delimiterIndex - 1);
        String deadlineText = text.substring(delimiterIndex + 1).trim();
        try {
            LocalDate deadline = LocalDate.parse(deadlineText);
            return new Deadline(isDone, description, deadline);
        } catch (DateTimeParseException e) {
            throw new InvalidTaskException(LOAD_DEADLINE_TASK_ERROR);
        }
    }

    private Event createEventTask(boolean isDone, String text) throws InvalidTaskException {
        int endDelimiter = text.lastIndexOf('|');
        int startDelimiter = text.lastIndexOf('|', endDelimiter - 1);
        if (startDelimiter < 0 || endDelimiter < 0) {
            throw new InvalidTaskException(LOAD_EVENT_TASK_ERROR);
        }
        String description = text.substring(0, startDelimiter - 1);
        String startText = text.substring(startDelimiter + 1, endDelimiter).trim();
        String endText = text.substring(endDelimiter + 1).trim();
        try {
            LocalDate start = LocalDate.parse(startText);
            LocalDate end = LocalDate.parse(endText);
            return new Event(isDone, description, start, end);
        } catch (DateTimeParseException e) {
            throw new InvalidTaskException(LOAD_EVENT_TASK_ERROR);
        }
    }

    /**
     * Saves the tasks from the TaskList into the local storage tasklist.
     *
     * @param taskList The collection of Task objects.
     * @throws IOException When the tasklist storage file cannot be opened or accessed to save changes.
     */
    public void saveFile(List<Task> taskList) throws IOException {
        try (FileWriter fileWriter = new FileWriter(this.file)) {
            writeTasks(fileWriter, taskList);
        } catch (IOException e) {
            throw new IOException(SAVE_FILE_ERROR);
        }
    }

    private void writeTasks(FileWriter fileWriter, List<Task> taskList) throws IOException {
        for (Task task : taskList) {
            fileWriter.append(task.toFileFormat());
        }
    }
}
