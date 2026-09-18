package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import herta.task.Task;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Provides focused fixtures shared by storage save and transaction tests.
 */
final class StorageTestSupport {
    private StorageTestSupport() {
        // Utility class; do not instantiate.
    }

    /** Creates the largest task list whose serialized form fits the requested byte size. */
    static TaskList createTasksWithSerializedSize(long expectedSerializedBytes) {
        long recordOverheadBytes = "T | 0 | ".getBytes(StandardCharsets.UTF_8).length
                + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
        long descriptionBytes = expectedSerializedBytes
                - (long) TaskList.MAXIMUM_TASK_COUNT * recordOverheadBytes;
        int commonDescriptionLength = Math.toIntExact(descriptionBytes / TaskList.MAXIMUM_TASK_COUNT);
        int finalDescriptionLength = commonDescriptionLength
                + Math.toIntExact(descriptionBytes % TaskList.MAXIMUM_TASK_COUNT);
        Todo commonTask = new Todo("a".repeat(commonDescriptionLength));
        List<Task> tasks = new ArrayList<>(TaskList.MAXIMUM_TASK_COUNT);
        for (int i = 0; i < TaskList.MAXIMUM_TASK_COUNT - 1; i++) {
            tasks.add(commonTask);
        }
        tasks.add(new Todo("a".repeat(finalDescriptionLength)));
        return new TaskList(tasks);
    }

    /** Confirms that a transaction leaves no owned temporary files behind. */
    static void assertNoTransactionFiles(Path directory) throws Exception {
        try (var paths = Files.list(directory)) {
            assertEquals(List.of(), paths.filter(path -> {
                String fileName = path.getFileName().toString();
                return fileName.startsWith(".herta-active-")
                        || fileName.startsWith(".herta-archive-")
                        || fileName.startsWith(".herta-transaction");
            }).toList());
        }
    }
}
