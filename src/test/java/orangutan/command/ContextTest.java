package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import orangutan.Storage;
import orangutan.chatlist.ChatList;

public class ContextTest {
    @Test
    public void defaultConstructor_createsInitialState() {
        Context context = new Context();

        assertNull(context.getList());
        assertFalse(context.isRun());
        assertNull(context.getFilePath());
        assertTrue(context.getStorage() instanceof Storage);
    }

    @Test
    public void constructorAndSetters_validValues_storeValues() {
        ChatList originalList = new ChatList();
        Storage storage = new Storage();
        Path originalPath = Path.of("original.txt");
        Context context = new Context(originalList, true, originalPath, storage);

        assertSame(originalList, context.getList());
        assertTrue(context.isRun());
        assertEquals(originalPath, context.getFilePath());
        assertSame(storage, context.getStorage());

        ChatList replacementList = new ChatList();
        Path replacementPath = Path.of("replacement.txt");
        context.setList(replacementList);
        context.setRun(false);
        context.setFilePath(replacementPath);

        assertSame(replacementList, context.getList());
        assertFalse(context.isRun());
        assertEquals(replacementPath, context.getFilePath());
    }
}
