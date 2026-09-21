package melodie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import melodie.task.Deadline;
import melodie.task.Event;
import melodie.task.Task;
import melodie.task.TaskList;
import melodie.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void read_missingFile_returnsEmptyList() throws IOException {
        // Arrange
        Path filePath = this.tempDirectory.resolve("missing").resolve("Melodie.txt");
        Storage storage = new Storage(filePath);

        // Act
        ArrayList<Task> actualTasks = storage.read();

        // Assert
        assertTrue(actualTasks.isEmpty());
    }

    @Test
    public void writeAndRead_multipleTaskTypes_preservesTasks() throws IOException {
        // Arrange
        Path filePath = this.tempDirectory.resolve("visions").resolve("Melodie.txt");
        Storage storage = new Storage(filePath);
        TaskList expectedTasks = new TaskList();
        expectedTasks.add(new Todo("farm Primogems for Chasca"));
        expectedTasks.add(new Deadline(
                "reach Challenger before season ends",
                LocalDateTime.of(2027, 1, 8, 23, 59)));
        expectedTasks.add(new Event(
                "play Clash with the squad",
                LocalDateTime.of(2027, 1, 9, 18, 0),
                LocalDateTime.of(2027, 1, 9, 22, 0)));

        // Act
        storage.write(expectedTasks);
        ArrayList<Task> actualTasks = storage.read();

        // Assert
        assertEquals(3, actualTasks.size());
        assertInstanceOf(Todo.class, actualTasks.get(0));
        assertInstanceOf(Deadline.class, actualTasks.get(1));
        assertInstanceOf(Event.class, actualTasks.get(2));
        for (int i = 0; i < actualTasks.size(); i++) {
            assertEquals(
                    expectedTasks.get(i).toStorageString(),
                    actualTasks.get(i).toStorageString());
        }
    }

    @Test
    public void writeAndRead_markedTask_preservesStatus() throws IOException {
        // Arrange
        Path filePath = this.tempDirectory.resolve("marked").resolve("Melodie.txt");
        Storage storage = new Storage(filePath);
        TaskList expectedTasks = new TaskList();
        Todo wardBaronPit = new Todo("place a control ward at Baron pit");
        wardBaronPit.mark();
        expectedTasks.add(wardBaronPit);

        // Act
        storage.write(expectedTasks);
        ArrayList<Task> actualTasks = storage.read();

        // Assert
        assertEquals(1, actualTasks.size());
        assertTrue(actualTasks.get(0).isCompleted());
        assertFalse(actualTasks.get(0).getDescription().isBlank());
        assertEquals("T | 1 | place a control ward at Baron pit",
                actualTasks.get(0).toStorageString());
    }

    @Test
    public void writeAndRead_descriptionsContainingSeparator_preservesDescriptions()
            throws IOException {
        // Arrange
        Path filePath = this.tempDirectory.resolve("separators").resolve("Melodie.txt");
        Storage storage = new Storage(filePath);
        TaskList expectedTasks = new TaskList();
        expectedTasks.add(new Todo("compare A | B"));
        expectedTasks.add(new Deadline(
                "submit draft | final copy",
                LocalDateTime.of(2027, 1, 10, 18, 0)));
        expectedTasks.add(new Event(
                "rehearse verse | chorus",
                LocalDateTime.of(2027, 1, 11, 14, 0),
                LocalDateTime.of(2027, 1, 11, 16, 0)));

        // Act
        storage.write(expectedTasks);
        ArrayList<Task> actualTasks = storage.read();

        // Assert
        assertEquals(expectedTasks.size(), actualTasks.size());
        for (int i = 0; i < actualTasks.size(); i++) {
            assertEquals(expectedTasks.get(i).getDescription(), actualTasks.get(i).getDescription());
            assertEquals(expectedTasks.get(i).toStorageString(), actualTasks.get(i).toStorageString());
        }
    }

    @Test
    public void read_malformedKnownTask_throwsIoException() throws IOException {
        // Arrange
        Path filePath = this.tempDirectory.resolve("malformed").resolve("Melodie.txt");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "D | 0 | missing date");
        Storage storage = new Storage(filePath);

        // Act and assert
        assertThrows(IOException.class, storage::read);
    }
}
