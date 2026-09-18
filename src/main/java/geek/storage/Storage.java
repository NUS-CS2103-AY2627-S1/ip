package geek.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import geek.task.Task;

/**
 * Loads and saves tasks in Geek's line-based data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath Location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all valid, nonblank task records from the data file.
     *
     * Missing parent directories and the data file are created. Malformed
     * nonblank lines are skipped and reported using one-based line numbers.
     *
     * @return Loaded tasks and the line numbers of corrupted records.
     * @throws IOException If the data path cannot be created or read.
     */
    public LoadResult loadTasks() throws IOException {
        ensureDataFileExists();

        List<Task> tasks = new ArrayList<>();
        List<Integer> corruptedLineNumbers = new ArrayList<>();
        List<String> lines = Files.readAllLines(
                filePath,
                StandardCharsets.UTF_8
        );

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(Task.fromDataString(line));
            } catch (IllegalArgumentException e) {
                corruptedLineNumbers.add(i + 1);
            }
        }

        return new LoadResult(tasks, corruptedLineNumbers);
    }

    /**
     * Replaces the data file with the serialized form of the supplied tasks.
     *
     * Missing parent directories and the data file are created first.
     *
     * @param tasks Tasks to save in list order.
     * @throws IOException If the data path cannot be created or written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        ensureDataFileExists();

        List<String> lines = tasks.stream()
                .map(Task::toDataString)
                .toList();

        Files.write(
                filePath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Creates the parent directories and an empty data file when they are
     * absent.
     *
     * @throws IOException If a required directory or file cannot be created.
     */
    private void ensureDataFileExists() throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Groups loaded tasks with the corrupted lines skipped during loading.
     *
     * @param tasks Tasks loaded in file order.
     * @param corruptedLineNumbers One-based numbers of malformed nonblank
     *         lines.
     */
    public record LoadResult(
            List<Task> tasks,
            List<Integer> corruptedLineNumbers
    ) {
        /**
         * Creates a result whose list structures are defensive copies.
         *
         * @param tasks Tasks loaded in file order.
         * @param corruptedLineNumbers One-based numbers of malformed nonblank
         *         lines.
         */
        public LoadResult {
            tasks = List.copyOf(tasks);
            corruptedLineNumbers = List.copyOf(
                    corruptedLineNumbers
            );
        }
    }
}
