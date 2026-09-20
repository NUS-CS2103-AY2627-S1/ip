package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests loading and saving tasks through the on-disk storage format. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_mixedValidAndMalformedRecords_loadsValidRecordsAndCountsSkipped()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("nexus.txt");
        Files.writeString(dataFile, "T | 0 | read book\nbroken record\n"
                + "D | 0 | submit report | 2026-09-18\n");

        LoadResult result = new Storage(dataFile).load();

        assertEquals(2, result.tasks().size());
        assertEquals("[T][ ] read book", result.tasks().get(0).toString());
        assertEquals("[D][ ] submit report (by: Sep 18 2026)",
                result.tasks().get(1).toString());
        assertEquals(1, result.skippedRecords());
    }

    @Test
    void load_invalidDatesAndEventOrder_skipsBothRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nexus.txt");
        Files.writeString(dataFile, "D | 0 | impossible | 2026-02-30\n"
                + "E | 0 | backwards | 2026-09-20 | 2026-09-19\n");

        LoadResult result = new Storage(dataFile).load();

        assertEquals(0, result.tasks().size());
        assertEquals(2, result.skippedRecords());
    }

    @Test
    void load_invalidStatusesAndFieldCounts_skipsEveryMalformedRecord() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nexus.txt");
        Files.writeString(dataFile, "T | 2 | invalid status\n"
                + "T | true | text status\n"
                + "T | 0 | too many | fields\n"
                + "D | 0 | missing date\n"
                + "E | 0 | missing end | 2026-09-18\n");

        LoadResult result = new Storage(dataFile).load();

        assertEquals(0, result.tasks().size());
        assertEquals(5, result.skippedRecords());
    }
}
