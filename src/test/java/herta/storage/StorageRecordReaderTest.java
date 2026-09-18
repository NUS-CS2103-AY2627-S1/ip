package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.task.TaskList;

/**
 * Tests strict decoding and bounded record reading.
 */
class StorageRecordReaderTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void read_allSupportedLineSeparatorsAndEmptyRecords_returnsRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("line-endings.txt");
        Files.writeString(dataFile, "first\n\rsecond\r\n\nthird",
                StandardCharsets.UTF_8);

        List<String> records = StorageRecordReader.read(dataFile);

        assertEquals(List.of("first", "", "second", "", "third"), records);
    }

    @Test
    void read_malformedUtf8AndOverlongRecord_rejectsInput() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        Files.write(dataFile, new byte[] {(byte) 0xc3, (byte) 0x28});

        assertThrows(IOException.class, () -> StorageRecordReader.read(dataFile));

        Files.writeString(dataFile, "a".repeat(StorageRecordReader.MAX_RECORD_LENGTH),
                StandardCharsets.UTF_8);
        assertEquals(List.of("a".repeat(StorageRecordReader.MAX_RECORD_LENGTH)),
                StorageRecordReader.read(dataFile));

        Files.writeString(dataFile, "a".repeat(StorageRecordReader.MAX_RECORD_LENGTH + 1),
                StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> StorageRecordReader.read(dataFile));
    }

    @Test
    void read_exactTaskCountLimit_acceptsBoundaryAndRejectsExcess() throws Exception {
        Path dataFile = temporaryDirectory.resolve("records.txt");
        String record = "T | 0 | task";
        List<String> maximumRecords = Collections.nCopies(TaskList.MAXIMUM_TASK_COUNT, record);
        Files.writeString(dataFile, String.join(System.lineSeparator(), maximumRecords),
                StandardCharsets.UTF_8);

        assertEquals(TaskList.MAXIMUM_TASK_COUNT, StorageRecordReader.read(dataFile).size());

        Files.writeString(dataFile, String.join(System.lineSeparator(),
                Collections.nCopies(TaskList.MAXIMUM_TASK_COUNT + 1, record)),
                StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> StorageRecordReader.read(dataFile));
    }
}
