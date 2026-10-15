package orangutan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orangutan.chatlist.ChatList;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;
import orangutan.command.SortMethod;

public class StorageTest {
    @TempDir
    public Path temporaryFolder;

    private final Storage storage = new Storage();

    @Test
    public void readFromFile_missingFile_returnsEmptyList() throws IOException {
        ChatList result = storage.readFromFile(temporaryFolder.resolve("missing.txt"));

        assertEquals(0, result.getLength());
        assertEquals(SortMethod.NONE, result.getSortMethod());
    }

    @Test
    public void writeAndRead_allItemTypesAndSortMethod_roundTripsData() throws IOException {
        ChatList original = new ChatList();
        original.addItem(new ListItem(ItemType.TODO, "read book", true));
        original.addItem(new ListItem(ItemType.DEADLINE, "submit report", false, "20260901 1700"));
        original.addItem(new ListItem(ItemType.EVENT, "lecture", false, "20260831 0900", "20260831 1100"));
        original.setSorting(SortMethod.DATETIME);
        Path filePath = temporaryFolder.resolve("nested").resolve("orangutan.txt");

        storage.writeToFile(filePath, original);
        ChatList loaded = storage.readFromFile(filePath);

        assertTrue(Files.exists(filePath));
        assertEquals(SortMethod.DATETIME, loaded.getSortMethod());
        assertEquals(toFileLines(original), toFileLines(loaded));
    }

    @Test
    public void writeToFile_emptyList_writesSortMethodOnly() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");

        storage.writeToFile(filePath, new ChatList());

        assertEquals("NONE\n", Files.readString(filePath));
    }

    @Test
    public void writeToFile_existingFile_overwritesPreviousContents() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "obsolete contents");
        ChatList list = new ChatList();
        list.addItem(new ListItem(ItemType.TODO, "replacement", false));

        storage.writeToFile(filePath, list);

        assertEquals("NONE\n[T],false,replacement", Files.readString(filePath));
    }

    @Test
    public void readFromFile_unknownSortMethod_throwsIllegalArgumentException() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "UNKNOWN\n");

        assertThrows(IllegalArgumentException.class, () -> storage.readFromFile(filePath));
    }

    @Test
    public void readFromFile_invalidDateTime_throwsDateTimeParseException() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "NONE\n[D],false,submit report,tomorrow");

        assertThrows(DateTimeParseException.class, () -> storage.readFromFile(filePath));
    }

    private List<String> toFileLines(ChatList list) {
        return list.toStream().map(ListItem::toFile).toList();
    }
}
