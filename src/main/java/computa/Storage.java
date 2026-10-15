package computa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Handles the loading and saving of task data to a local file on the hard drive.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private final String filePath;
    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath The relative or absolute path where the data file is stored.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }
    /**
     * Loads the saved tasks from the data file into an ArrayList.
     * If the file does not exist, it creates a new empty file.
     *
     * @return An ArrayList containing the tasks parsed from the file.
     * @throws Computa.ComputaException If there is an issue reading the file or parsing its contents.
     */

    public List<Todo> load() throws Computa.ComputaException {
        List<Todo> tasks = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                return tasks;
            }
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String[] taskData = scanner.nextLine().split(FIELD_SEPARATOR_REGEX);
                
                    assert taskData.length >= 3 : "Each saved task needs a type, status, and description";
                    boolean isDone = Integer.parseInt(taskData[1]) != 0;
                    switch (taskData[0]) {
                        case "T" -> tasks.add(new Todo(taskData[2], isDone));
                        case "D" -> {
                            assert taskData.length >= 4 : "A deadline must include a due date";
                            tasks.add(new Deadline(taskData[2], taskData[3], isDone));
                        }
                        case "E" -> {
                            assert taskData.length >= 5 : "An event must include start and end dates";
                            tasks.add(new Event(taskData[2], taskData[3], taskData[4], isDone));
                        }
                        case "R" -> {
                            assert taskData.length >= 5 : "A recurring task needs a weekday";
                            DayOfWeek day = DayOfWeek.valueOf(taskData[3].toUpperCase());
                            if (taskData[4].startsWith("NONE:")) {
                                String date = taskData[4].substring(5);
                                tasks.add(new RecurringTask(taskData[2], day,
                                        "NONE".equals(date) ? null : LocalDate.parse(date)));
                            } else {
                                tasks.add(new RecurringTask(taskData[2], day,
                                        LocalDate.parse(taskData[4]), isDone));
                            }
                        }
                        default -> { }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new Computa.ComputaException("no saved tasks found girl, starting fresh!");
        } catch (IOException e) {
            throw new Computa.ComputaException("bruh something went wrong with the file: " + e.getMessage());
        }
        return tasks;
    }
    /**
     * Saves the current list of tasks from the application to the data file.
     *
     * @param taskList The TaskList object containing the current tasks to be saved.
     */
    public void saveTasks(TaskList taskList) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < taskList.getSize(); i++) {
                writer.write(taskList.getTask(i).toFileFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("bruh the file won't save: " + e.getMessage());
        }
    }
}
