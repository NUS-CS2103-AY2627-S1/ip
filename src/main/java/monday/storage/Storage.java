package monday.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import monday.exception.MondayException;
import monday.task.Deadline;
import monday.task.Event;
import monday.task.Task;
import monday.task.TaskList;
import monday.task.Todo;
import monday.util.DateTimeFormat;

/**
 * Loads tasks from and saves tasks to Monday's data file.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "monday.txt");

    /**
     * Loads saved tasks. Returns an empty list when no save files exists yet.
     */
    public static ArrayList<Task> loadTask() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(FILE_PATH)) {
            return tasks;
        }

        List<String> lines;

        try {
            lines = Files.readAllLines(FILE_PATH);
        } catch (IOException e) {
            System.out.println("Sorry, I could not load your tasks.");
            return tasks;
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);

            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(createTask(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Ignoring invalid task on line " + (index + 1) + " of the save file.");
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks task list to save.
     */
    public static void saveTask(TaskList tasks) throws MondayException {
        ArrayList<String> savedTasks = new ArrayList<>();

        for (Task task : tasks) {
            savedTasks.add(convertToFileFormat(task));
        }

        try {
            Files.createDirectories(FILE_PATH.getParent());
            Path temporaryFile = Files.createTempFile(FILE_PATH.getParent(), "monday-", ".tmp");
            try {
                Files.write(temporaryFile, savedTasks);
                moveIntoPlace(temporaryFile);
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException e) {
            throw new MondayException("Sorry, I could not save your tasks. Your change was not applied.");
        }
    }

    /**
     * Replaces the save file with a fully written temporary file.
     *
     * @param temporaryFile file containing the complete replacement contents.
     * @throws IOException if the replacement cannot be completed.
     */
    private static void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, FILE_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one saved line into the appropriate Task object.
     */
    private static Task createTask(String line) {
        Task task;
        String[] parts = line.split("\\|", -1);
        String taskType = parts[0].trim();
        validatePartCount(taskType, parts);
        boolean isDone = parseCompletionStatus(parts[1].trim());
        String taskDescription = parts[2].trim();
        if (taskDescription.isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }

        switch (taskType) {
            case "T":
                task = new Todo(taskDescription, isDone, getNotes(parts, 3));
                break;
            case "D":
                LocalDate deadlineDate = LocalDate.parse(parts[3].trim());
                LocalTime deadlineTime = parseOptionalTime(parts[4].trim());
                task = new Deadline(taskDescription, isDone, deadlineDate, deadlineTime, getNotes(parts, 5));
                break;
            case "E":
                LocalDate eventDate1 = LocalDate.parse(parts[3].trim());
                LocalTime eventTime1 = parseOptionalTime(parts[4].trim());

                LocalDate eventDate2 = LocalDate.parse(parts[5].trim());
                LocalTime eventTime2 = parseOptionalTime(parts[6].trim());

                validateEventRange(eventDate1, eventTime1, eventDate2, eventTime2);
                task = new Event(taskDescription, isDone, eventDate1, eventTime1, eventDate2, eventTime2,
                        getNotes(parts, 7));
                break;
            default:
                throw new IllegalArgumentException("Invalid task type in save file.");
        }

        return task;
    }

    /**
     * Validates that a saved task type has its expected number of fields.
     */
    private static void validatePartCount(String taskType, String[] parts) {
        int expectedPartCount;
        switch (taskType) {
            case "T":
                expectedPartCount = 3;
                break;
            case "D":
                expectedPartCount = 5;
                break;
            case "E":
                expectedPartCount = 7;
                break;
            default:
                throw new IllegalArgumentException("Invalid task type in save file.");
        }

        if (parts.length != expectedPartCount && parts.length != expectedPartCount + 1) {
            throw new IllegalArgumentException("Incorrect number of fields in save file.");
        }
    }

    /**
     * Extracts notes when present in the extended save format.
     *
     * @param parts fields from one saved task.
     * @param legacyPartCount number of fields used before notes were supported.
     * @return saved notes, or an empty string for a legacy task.
     */
    private static String getNotes(String[] parts, int legacyPartCount) {
        return parts.length == legacyPartCount ? "" : parts[legacyPartCount].trim();
    }

    /**
     * Converts a saved completion value to a boolean after validating it.
     */
    private static boolean parseCompletionStatus(String status) {
        if (status.equals("1")) {
            return true;
        }
        if (status.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("Invalid completion status in save file.");
    }

    /**
     * Validates that an event's end is after its start.
     */
    private static void validateEventRange(LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime) {
        LocalTime effectiveStartTime = (startTime == null) ? LocalTime.MIN : startTime;
        LocalTime effectiveEndTime = (endTime == null) ? LocalTime.MIN : endTime;
        if (endDate.isBefore(startDate)
                || (endDate.equals(startDate) && !effectiveEndTime.isAfter(effectiveStartTime))) {
            throw new IllegalArgumentException("Event end must be after its start.");
        }
    }

    /**
     * Converts one Task object into a line suitable for the save file.
     */
    private static String convertToFileFormat(Task task) {
        String status = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription() + formatNotes(task);
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            String savedTime = deadline.getDeadlineTime() == null
                    ? ""
                    : deadline.getDeadlineTime().format(DateTimeFormat.INPUT_TIME.getFormatter());
            return "D | " + status + " | " + task.getDescription()
                    + " | " + deadline.getDeadlineDate() + " | " + savedTime + formatNotes(task);
        }

        assert task instanceof Event : "The task is not declared as a Todo, Deadline, or Event task.";
        Event event = (Event) task;
        String savedTime1 = event.getStartTime() == null
                ? ""
                : event.getStartTime().format(DateTimeFormat.INPUT_TIME.getFormatter());
        String savedTime2 = event.getEndTime() == null
                ? ""
                : event.getEndTime().format(DateTimeFormat.INPUT_TIME.getFormatter());
        return "E | " + status + " | " + task.getDescription()
                + " | " + event.getStartDate() + " | " + savedTime1
                + " | " + event.getEndDate() + " | " + savedTime2 + formatNotes(task);
    }

    /**
     * Formats non-empty task notes as an additional save-file field.
     */
    private static String formatNotes(Task task) {
        return task.getNotes().isEmpty() ? "" : " | " + task.getNotes();
    }

    /**
     * Parses an optional saved time.
     *
     * @param savedTime saved time text, which may be empty.
     * @return parsed time, or null if no time was saved.
     */
    private static LocalTime parseOptionalTime(String savedTime) {
        if (!savedTime.isEmpty()) {
            return LocalTime.parse(savedTime, DateTimeFormat.INPUT_TIME.getFormatter());
        }

        return null;
    }
}
