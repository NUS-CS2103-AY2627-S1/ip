package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orangutan.OrangutanException;
import orangutan.Storage;
import orangutan.chatlist.ChatList;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

public class LifecycleCommandTest {
    private static final String WELCOME_MESSAGE =
            "Greetings, I am Orangutan. In what manner may I be of service today?";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void run_initWithMissingFile_initializesEmptyList() throws OrangutanException {
        Context context = initialContext(temporaryFolder.resolve("missing.txt"));

        String result = new InitCommand().run(context);

        assertEquals(WELCOME_MESSAGE, result);
        assertTrue(context.isRun());
        assertEquals(0, context.getList().getLength());
    }

    @Test
    public void run_initWithValidFile_loadsSavedList() throws IOException, OrangutanException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "NONE\n[T],true,read book");
        Context context = initialContext(filePath);

        String result = new InitCommand().run(context);

        assertEquals(WELCOME_MESSAGE, result);
        assertTrue(context.isRun());
        assertEquals("1. [T] [X] read book", context.getList().toString());
    }

    @Test
    public void run_initWithUnreadablePath_throwsAndStopsContext() throws IOException {
        Path directoryPath = temporaryFolder.resolve("directory");
        Files.createDirectory(directoryPath);
        Context context = initialContext(directoryPath);

        OrangutanException exception = assertThrows(OrangutanException.class, () -> new InitCommand().run(context));

        assertTrue(exception.toString().startsWith("Alas! I could not retrieve the records preserved in"));
        assertFalse(context.isRun());
    }

    @Test
    public void run_initWithInvalidDate_throwsAndStopsContext() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "NONE\n[D],false,submit report,tomorrow");
        Context context = initialContext(filePath);

        OrangutanException exception = assertThrows(OrangutanException.class, () -> new InitCommand().run(context));

        assertTrue(exception.toString().contains("dates and times"));
        assertTrue(exception.toString().contains("yyyymmdd hhmm"));
        assertFalse(context.isRun());
    }

    @Test
    public void run_initWithUnknownSortMethod_throwsAndStopsContext() throws IOException {
        Path filePath = temporaryFolder.resolve("orangutan.txt");
        Files.writeString(filePath, "ALPHABETICAL\n");
        Context context = initialContext(filePath);

        OrangutanException exception = assertThrows(OrangutanException.class, () -> new InitCommand().run(context));

        assertTrue(exception.toString().contains("sorting method preserved in"));
        assertFalse(context.isRun());
    }

    @Test
    public void run_byeWithWritablePath_savesListAndStopsContext() throws IOException, OrangutanException {
        Path filePath = temporaryFolder.resolve("nested").resolve("orangutan.txt");
        ChatList list = new ChatList();
        list.addItem(new ListItem(ItemType.TODO, "read book", false));
        Context context = new Context(list, true, filePath, new Storage());

        String result = new ByeCommand().run(context);

        assertEquals("Fare thee well, and may we meet again.", result);
        assertFalse(context.isRun());
        assertEquals("NONE\n[T],false,read book", Files.readString(filePath));
    }

    @Test
    public void run_byeWithUnwritablePath_throwsAndKeepsContextRunning() throws IOException {
        Path fileInParentPosition = temporaryFolder.resolve("not-a-directory");
        Files.writeString(fileInParentPosition, "file");
        Path filePath = fileInParentPosition.resolve("orangutan.txt");
        Context context = new Context(new ChatList(), true, filePath, new Storage());

        OrangutanException exception = assertThrows(OrangutanException.class, () -> new ByeCommand().run(context));

        assertTrue(exception.toString().startsWith("Alas! I was unable to inscribe your list in"));
        assertTrue(context.isRun());
    }

    private Context initialContext(Path filePath) {
        return new Context(null, false, filePath, new Storage());
    }
}
