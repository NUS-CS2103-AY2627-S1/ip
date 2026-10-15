package tuesday.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import tuesday.task.Task;
import tuesday.task.TaskList;

/**
 * Loads and saves Tuesday's task data.
 */
public final class Storage {

    /** Stores the path used for the task save file. */
    public static final String FILE_PATH = "data" + File.separator + "Tuesday.txt";

    private Storage() {
    }

    /**
     * Loads valid saved tasks from a file into a task list.
     *
     * @param filePath the path of the saved task file.
     * @param tasks the list to which loaded tasks are added.
     * @throws NoSuchFileException if the save file does not exist.
     * @throws IOException if opening, reading, or closing the save file fails.
     */
    public static void loadData(String filePath, TaskList tasks) throws IOException {
        loadData(tasks, () -> Files.newBufferedReader(Path.of(filePath), Charset.defaultCharset()));
    }

    /**
     * Stages loaded tasks until reading and closing succeed, leaving the caller unchanged on failure.
     */
    static void loadData(TaskList tasks, ReaderSource source) throws IOException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try (BufferedReader reader = source.open()) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = SavedTaskParser.parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        }
        for (Task task : loadedTasks) {
            tasks.add(task);
        }
    }

    /**
     * Appends serialized task data to the save file.
     *
     * @param filePath the path of the saved task file.
     * @param data the serialized task data to append.
     * @throws IOException if the data cannot be saved.
     */
    public static void saveNewData(String filePath, String data) throws IOException {
        Path file = Path.of(filePath);
        byte[] existing;
        try {
            existing = Files.readAllBytes(file);
        } catch (NoSuchFileException exception) {
            existing = new byte[0];
        }
        byte[] addition = (data + System.lineSeparator()).getBytes(Charset.defaultCharset());
        byte[] combined = Arrays.copyOf(existing, existing.length + addition.length);
        System.arraycopy(addition, 0, combined, existing.length, addition.length);
        new AtomicFileWriter().write(file, combined);
    }

    /**
     * Replaces the save file contents with the current tasks.
     *
     * @param filePath the path of the saved task file.
     * @param tasks the tasks to save.
     * @throws IOException if the data cannot be saved.
     */
    public static void modifyData(String filePath, TaskList tasks) throws IOException {
        String data = tasks.stream()
                .map(task -> task.toString() + System.lineSeparator())
                .collect(Collectors.joining());

        new AtomicFileWriter().write(Path.of(filePath), data.getBytes(Charset.defaultCharset()));
    }

    /**
     * Opens a reader, allowing tests to reproduce failures during reading and closing.
     */
    @FunctionalInterface
    interface ReaderSource {
        /**
         * Opens the input whose ownership transfers to the loader.
         *
         * @return the reader to consume and close.
         * @throws IOException if opening fails.
         */
        BufferedReader open() throws IOException;
    }
}
