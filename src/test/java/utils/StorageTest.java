package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import recordbase.exceptions.RecordException;
import recordbase.types.RecordList;
import recordbase.utils.ListParser;
import recordbase.utils.Storage;

/** Tests persistence of fields that are not present in legacy save records. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoad_scheduledToDo_preservesDateAndTime() {
        RecordList original = new RecordList();
        ListParser.parseToDo("todo Buy milk 20260115 14:30", original);
        Path saveFile = temporaryDirectory.resolve("tasks.txt");

        Storage.saveToFile(original, saveFile.toString());
        RecordList restored = new RecordList();
        Storage.loadFromFile(restored, saveFile.toString());

        assertTrue(restored.getItem(0).toString().contains("Scheduled: 15 Jan 2026, 2:30 PM"));
    }

    @Test
    void saveToFile_nestedMissingFolders_createsFoldersAndFile() {
        Path saveFile = temporaryDirectory.resolve("parent/nested/tasks.txt");
        RecordList list = new RecordList();
        list.addToDoItem("Read chapter");

        Storage.saveToFile(list, saveFile.toString());

        assertTrue(Files.isRegularFile(saveFile));
    }

    @Test
    void loadFromFile_corruptedSecondLine_doesNotPartiallyLoad() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "# Record save format v2\nT,0,3,\"Valid task\"\nnot valid\n");
        RecordList target = new RecordList();
        target.addToDoItem("Existing task");

        assertThrows(RecordException.class, () -> Storage.loadFromFile(target, saveFile.toString()));

        assertEquals(1, target.getItems().size());
        assertTrue(target.getItem(0).toString().contains("Existing task"));
    }

    @Test
    void saveAndLoad_descriptionWithCsvCharacters_preservesDescription() throws Exception {
        RecordList original = new RecordList();
        String description = "separator ', ' inside, called \"special\"";
        original.addToDoItem(description);
        Path saveFile = temporaryDirectory.resolve("tasks.txt");

        Storage.saveToFile(original, saveFile.toString());

        assertTrue(Files.readString(saveFile)
                .contains("\"separator ', ' inside, called \"\"special\"\"\""));
        RecordList restored = new RecordList();
        Storage.loadFromFile(restored, saveFile.toString());
        assertEquals(description, restored.getItem(0).getTaskDescription());
    }

    @Test
    void loadFromFile_manuallyWrittenCsv_loadsItem() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "# Record save format v2\nD,0,1,\"Submit report, with appendix\","
                + "2026-09-30T23:59\n");
        RecordList restored = new RecordList();

        Storage.loadFromFile(restored, saveFile.toString());

        assertEquals("Submit report, with appendix", restored.getItem(0).getTaskDescription());
        assertEquals(1, restored.getItems().size());
    }

    @Test
    void loadFromFile_legacyFormat_rejected() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "T, 0, 3, 'Legacy task'\n");

        assertThrows(RecordException.class, () ->
                Storage.loadFromFile(new RecordList(), saveFile.toString()));
    }
}
