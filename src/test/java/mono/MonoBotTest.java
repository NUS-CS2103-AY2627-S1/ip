package mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mono.exception.NonExistentException;
import mono.task.ToDo;
import mono.testutil.StorageTestSupport;
import mono.ui.ConversationUi;

/** Tests that MonoBot coordinates task operations with conversation output. */
public class MonoBotTest {
    private ArrayList<String> responses;
    private MonoBot bot;

    @BeforeEach
    public void setUp() throws IOException {
        StorageTestSupport.resetStorage();
        this.responses = new ArrayList<>();
        this.bot = new MonoBot(new ConversationUi(this.responses::add));
    }

    @Test
    public void addListFindDeleteMarkAndUnmark_emitResponsesForStateChanges()
            throws NonExistentException {
        ToDo task = new ToDo("read book");
        this.bot.addTask(task);
        this.bot.listTasks();
        this.bot.findTasks("read");
        this.bot.markTaskDone(1);
        this.bot.unmarkTaskDone(1);
        this.bot.delete(1);

        assertEquals(6, this.responses.size());
        assertEquals(0, new mono.storage.Storage().readStorage().length);
    }

    @Test
    public void greetAndExit_emitExpectedMessages() {
        this.bot.greet();
        this.bot.exit();

        assertEquals(2, this.responses.size());
        assertEquals(true, this.responses.get(0).contains("Hello! I'm Mono."));
        assertEquals(true, this.responses.get(1).contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void invalidDelete_doesNotEmitResponse() {
        assertThrows(NonExistentException.class, () -> this.bot.delete(1));
        assertEquals(0, this.responses.size());
    }
}
