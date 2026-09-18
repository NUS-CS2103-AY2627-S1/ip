package com.fireinyu.themyth.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fireinyu.themyth.exceptions.CorruptedTaskFileException;

/**
 * Unit tests for {@link CsvBackedList}.
 */
public class CsvBackedListTest {

    @TempDir
    Path tempDir;

    @Test
    public void close_specialCharacters_preservesFileFormat() throws IOException {
        Path filePath = tempDir.resolve("exact-format.csv");
        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        list.open(filePath);
        list.add(new SimpleItem("hello, world", "\"quoted\""));
        list.close();

        assertEquals(List.of("hello\",\" world, \"\"quoted\"\""), Files.readAllLines(filePath));
    }

    @Test
    public void open_corruptedFile_clearsListAndLeavesFileUntouched() throws IOException {
        Path filePath = tempDir.resolve("failed-load.csv");
        List<String> originalLines = List.of("valid, row", "invalid");
        Files.write(filePath, originalLines);
        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        list.add(new SimpleItem("previous", "entry"));

        assertThrows(CorruptedTaskFileException.class, () -> list.open(filePath));
        assertEquals(0, list.size());
        list.close();
        assertEquals(originalLines, Files.readAllLines(filePath));
    }

    private static class SimpleItem implements CsvSerializable {
        private final String col1;
        private final String col2;

        SimpleItem(String col1, String col2) {
            this.col1 = col1;
            this.col2 = col2;
        }

        @Override
        public List<String> extract() {
            return List.of(col1, col2);
        }

        public String getCol1() {
            return col1;
        }

        public String getCol2() {
            return col2;
        }
    }

    private static class ConcreteCsvBackedList extends CsvBackedList<SimpleItem> {
        @Override
        protected SimpleItem parse(String... item) throws CorruptedTaskFileException {
            if (item.length < 2) {
                throw new CorruptedTaskFileException("test");
            }
            return new SimpleItem(item[0], item[1]);
        }
    }

    /**
     * Tests unlinked behavior of {@link CsvBackedList}.
     */
    @Test
    public void unlinkedList_returnsNullPathAndIgnoresClose() {
        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        assertNull(list.getDescriptor());

        // close should safely do nothing when not linked
        list.close();
        assertNull(list.getDescriptor());
    }

    /**
     * Tests opening a file, adding items, closing to save, and reopening to fetch.
     */
    @Test
    public void openAndClose_roundtripPersistence() {
        Path filePath = tempDir.resolve("test_storage.csv");
        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        list.open(filePath);

        assertEquals(filePath.toString(), list.getDescriptor());
        list.add(new SimpleItem("apple", "red"));
        list.add(new SimpleItem("banana", "yellow"));
        list.close();

        ConcreteCsvBackedList reloadedList = new ConcreteCsvBackedList();
        reloadedList.open(filePath);

        assertEquals(2, reloadedList.size());
        assertEquals("apple", reloadedList.get(0).getCol1());
        assertEquals("red", reloadedList.get(0).getCol2());
        assertEquals("banana", reloadedList.get(1).getCol1());
        assertEquals("yellow", reloadedList.get(1).getCol2());
    }

    /**
     * Tests serialization and deserialization of fields with quotes, commas, and newlines.
     */
    @Test
    public void specialCharacters_escapedAndRestoredCorrectly() {
        Path filePath = tempDir.resolve("special_chars.csv");
        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        list.open(filePath);

        list.add(new SimpleItem("hello, world", "line1, line2"));
        list.add(new SimpleItem("\"quoted\"", "plain"));
        list.close();

        ConcreteCsvBackedList reloaded = new ConcreteCsvBackedList();
        reloaded.open(filePath);
        assertEquals(2, reloaded.size());
        assertEquals("hello, world", reloaded.get(0).getCol1());
        assertEquals("line1, line2", reloaded.get(0).getCol2());
        assertEquals("\"quoted\"", reloaded.get(1).getCol1());
        assertEquals("plain", reloaded.get(1).getCol2());
    }

    /**
     * Tests loading corrupted CSV rows triggers {@link CorruptedTaskFileException}.
     */
    @Test
    public void open_corruptedRow_throwsCorruptedTaskFileException() {
        Path filePath = tempDir.resolve("corrupted.csv");
        FileLinesDisk disk = new FileLinesDisk(filePath);
        disk.writeLines(java.util.stream.Stream.of("onlyOneColumn"));

        ConcreteCsvBackedList list = new ConcreteCsvBackedList();
        assertThrows(CorruptedTaskFileException.class, () -> list.open(filePath));
    }

    /**
     * Tests opening a CSV resource using classloader path.
     */
    @Test
    public void open_validResourceName_loadsSuccessfully() {
        QuizList quizList = new QuizList();
        quizList.open("data/questions.csv");

        assertNotNull(quizList.getDescriptor());
        assertFalse(quizList.isEmpty());
    }
}
