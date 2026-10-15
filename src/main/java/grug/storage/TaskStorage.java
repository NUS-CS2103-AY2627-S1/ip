package grug.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import grug.storage.serde.TaskDeserializer;
import grug.storage.serde.TaskDeserializerException;
import grug.storage.serde.TaskSerializer;
import grug.task.Task;

/**
 * Utility class used to read tasks from and write tasks to a single file.
 */
public final class TaskStorage {
    private final String filepath;

    /**
     * Creates a new task storage that uses the {@code filepath} as a save file.
     *
     * @param filepath The path to the save file for the storage to read and write
     *                 from / to.
     */
    public TaskStorage(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return this.filepath;
    }

    /**
     * Renames the tasks file by inserting {@code .bak} before its extension.
     * i.e. {@code file.ext}'s backup is {@code file.bak.ext} and {@code file}'s
     * backup is {@code file.bak}
     *
     * @return The filepath of the renamed backup file.
     * @throws IOException                If the tasks file cannot be renamed.
     * @throws FileAlreadyExistsException If the backup file already exists.
     */
    public String moveTasksFileToBackup() throws IOException {
        Path sourcePath = Path.of(this.filepath);
        Path filenamePath = sourcePath.getFileName();
        if (filenamePath == null) {
            throw new IOException("Tasks filepath does not identify a file: " + this.filepath);
        }

        // create backup filename file.bak.ext or file.bak (if no extension)
        String filename = filenamePath.toString();
        int extensionIndex = filename.lastIndexOf('.');
        String backupFilename;
        if (extensionIndex > 0) {
            backupFilename = filename.substring(0, extensionIndex)
                    + ".bak"
                    + filename.substring(extensionIndex);
        } else {
            backupFilename = filename + ".bak";
        }

        Path backupPath = sourcePath.resolveSibling(backupFilename);
        Files.move(sourcePath, backupPath);
        return backupPath.toString();
    }

    /**
     * Saves the tasks to the file specified by the storage filepath
     * {@link #getFilepath()}.
     *
     * @throws IOException If the named file exists but is a directory rather than a
     *                     regular file, does not exist but cannot be created, or
     *                     cannot be opened or written to for any other reason.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        // the writer is automatically closed at the end of this try-resource block
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(this.filepath))) {
            // replace the contents of the entire file every save, rather than modify based
            // on some diffs (which is unnecessarily complex)
            String contents = tasks.stream()
                    .map(TaskSerializer::serialize)
                    .collect(Collectors.joining(System.lineSeparator()));

            bufferedWriter.write(contents);
        }
    }

    /**
     * Loads the tasks to the file specified by the storage filepath
     * {@link #getFilepath()}.
     *
     * @throws IOException               If the file does not exist, access is denied,
     *                                   or it cannot be opened or read for another
     *                                   reason.
     * @throws TaskDeserializerException If there was an error deserializing the
     *                                   saved tasks.
     */
    public List<Task> loadTasks() throws IOException, TaskDeserializerException {
        String serializedTasks;
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(this.filepath))) {
            serializedTasks = bufferedReader.readAllAsString();
        }

        return TaskDeserializer.deserializeMany(serializedTasks);
    }
}
