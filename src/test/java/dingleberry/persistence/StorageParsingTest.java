package dingleberry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests record-validation order and exact escape recovery warnings. */
class StorageParsingTest {
    /** Provides isolated files for persisted record fixtures. */
    @TempDir
    private Path tempDir;

    @Test
    void loadWithReport_multipleProblems_preservesFirstFailure()
            throws Exception {
        List<String> records = List.of(
                "X | 2 | description",
                "X | 2 | ",
                "X | 0 | description",
                "D | 0 | description | invalid date | extra",
                "E | 0 | description | invalid date",
                "E | 0 | description | invalid date | invalid end",
                "E | 0 | description | 2026-09-16T15:00:00"
                        + " | 2026-09-16T15:00:00",
                "T | 0",
                "T | | description");
        List<String> reasons = List.of(
                "invalid completion flag",
                "blank description",
                "unknown task type",
                "expected 4 fields but found 5",
                "expected 5 fields but found 4",
                "invalid date/time",
                "event end must be after event start",
                "missing description",
                "blank completion flag");
        Path file = tempDir.resolve("invalid-records.txt");
        Files.writeString(file, String.join(System.lineSeparator(), records),
                StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(file.toString())
                .loadWithReport();

        assertEquals(0, result.getTasks().size());
        assertEquals(records.size(), result.getWarnings().size());
        for (int i = 0; i < records.size(); i++) {
            assertEquals("Skipping corrupted task record: " + records.get(i)
                    + " (" + reasons.get(i) + ")",
                    result.getWarnings().get(i));
        }
    }

    @Test
    void loadWithReport_invalidEscapes_warnsAndContinues() throws Exception {
        String unknownEscape = "T | 0 | bad\\q";
        String incompleteEscape = "T | 0 | trailing\\";
        Path file = tempDir.resolve("escape-recovery.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                unknownEscape, incompleteEscape, "T | 1 | recovered"),
                StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(file.toString())
                .loadWithReport();

        assertEquals(List.of(
                "Skipping corrupted task record: " + unknownEscape
                        + " (unknown escape sequence \\q)",
                "Skipping corrupted task record: " + incompleteEscape
                        + " (incomplete escape sequence)"),
                result.getWarnings());
        assertEquals(1, result.getTasks().size());
        assertEquals("T | 1 | recovered",
                result.getTasks().getFirst().toSaveFormat());
    }

    @Test
    void loadWithReport_adjacentEscapes_preservesDescription()
            throws Exception {
        Path file = tempDir.resolve("adjacent-escapes.txt");
        Files.writeString(file, "T | 0 | café\\|\\\\\\n\\rberry",
                StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(file.toString())
                .loadWithReport();

        assertEquals(List.of(), result.getWarnings());
        assertEquals(1, result.getTasks().size());
        assertEquals("café|\\\n\rberry",
                result.getTasks().getFirst().getDescription());
        assertEquals("T | 0 | café\\|\\\\\\n\\rberry",
                result.getTasks().getFirst().toSaveFormat());
    }
}
