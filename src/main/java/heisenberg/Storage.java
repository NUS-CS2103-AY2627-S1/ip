package heisenberg;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Loads and saves tasks in the local data file.
 */
public class Storage {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Path FILE_PATH = Path.of("data", "storage.txt");

    /**
     * Appends saved tasks to the given list if the data file exists.
     *
     * @param taskList List receiving the saved tasks.
     * @throws StorageException If the data file cannot be read.
     */
    public void loadTasks(TaskList taskList) {
        if (!Files.exists(FILE_PATH)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                if (line.isBlank()) {
                    continue;
                }

                String[] taskParts = line.split("\\|", -1);
                Task task = switch (taskParts[0]) {
                    case "T" -> new ToDo(taskParts[2]);
                    case "D" -> new Deadline(taskParts[2],
                            LocalDateTime.parse(taskParts[3], DATE_TIME_FORMAT));
                    case "E" -> new Event(taskParts[2],
                            LocalDateTime.parse(taskParts[3], DATE_TIME_FORMAT),
                            LocalDateTime.parse(taskParts[4], DATE_TIME_FORMAT));
                    default -> throw new IllegalArgumentException("Unknown stored task type.");
                };

                if (taskParts[1].equals("1")) {
                    task.mark();
                }
                taskList.addTask(task);
            }
        } catch (IOException e) {
            throw new StorageException("Unable to load saved tasks.", e);
        }
    }

    /**
     * Replaces the data file with the current tasks and completion statuses.
     *
     * @param taskList Tasks to save in their current order.
     * @throws StorageException If the data directory or file cannot be written.
     */
    public void saveTasks(TaskList taskList) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task task : taskList) {
                    String status = task.isDone() ? "1" : "0";
                    if (task instanceof Deadline deadline) {
                        writer.write("D|" + status + "|" + task.getDescription()
                                + "|" + deadline.getDeadlineDateTime().format(DATE_TIME_FORMAT));
                    } else if (task instanceof Event event) {
                        writer.write("E|" + status + "|" + task.getDescription()
                                + "|" + event.getStartDateTime().format(DATE_TIME_FORMAT)
                                + "|" + event.getEndDateTime().format(DATE_TIME_FORMAT));
                    } else {
                        writer.write("T|" + status + "|" + task.getDescription());
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Unable to save tasks.", e);
        }
    }
}
