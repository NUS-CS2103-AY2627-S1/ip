package shan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shan.exception.DataFileException;
import shan.task.ToDo;

class StorageErrorHandlingTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_controlCharacterInDescription_skipsMalformedEntry() throws Exception {
        Path dataFile = this.tempDirectory.resolve("shan.txt");
        Files.writeString(dataFile, "T | 0 | read\u0000book");

        Storage.LoadResult result = new Storage(dataFile).load();

        assertTrue(result.tasks().isEmpty());
        assertEquals(1, result.skippedTasks());
    }

    @Test
    void save_parentPathIsFile_exceptionThrownWithoutChangingParentFile() throws Exception {
        Path parentFile = this.tempDirectory.resolve("data");
        Files.writeString(parentFile, "keep this content");
        Storage storage = new Storage(parentFile.resolve("shan.txt"));

        assertThrows(DataFileException.class, () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("keep this content", Files.readString(parentFile));
    }

    @Test
    void save_successfulReplacement_leavesNoTemporaryFile() throws Exception {
        Path dataFile = this.tempDirectory.resolve("shan.txt");
        Files.writeString(dataFile, "old content");
        Storage storage = new Storage(dataFile);

        storage.save(List.of(new ToDo("read book")));

        assertEquals("T | 0 | read book" + System.lineSeparator(),
                Files.readString(dataFile));
        try (Stream<Path> files = Files.list(this.tempDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith(".shan-")));
        }
    }
}
