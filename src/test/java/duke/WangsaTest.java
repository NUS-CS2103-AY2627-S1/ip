package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests console commands through parsing, presentation, and persistence. */
class WangsaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void run_aiWithoutKey_keepsNormalCommandsUsable() throws Exception {
        Storage repository = new Storage(temporaryDirectory.resolve("offline.txt"));
        String commands = "help\n@ai\n@ai How do I add a task?\ntodo read book\nmark 1\nlist\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new Wangsa(repository, new Parser(), ui, new AiHelper(null, null)).run();

        String transcript = output.toString(StandardCharsets.UTF_8);
        assertTrue(transcript.contains("Wangsa commands:"));
        assertTrue(transcript.contains("Ask a question after @ai"));
        assertTrue(transcript.contains("Offline help:"));
        assertTrue(transcript.contains("1.[T][X] read book"));
        assertTrue(transcript.contains("Bye. Hope to see you again soon!"));
        assertFalse(transcript.contains("•"));
        assertFalse(transcript.contains("—"));
        assertTrue(repository.loadTasks().get(0).isDone());
    }

    @Test
    void run_help_usesAsciiPunctuation_forConsoleEncodings() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream("help\nbye\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new Wangsa(new Storage(temporaryDirectory.resolve("help.txt")), new Parser(), ui).run();

        String transcript = output.toString(StandardCharsets.UTF_8);
        assertTrue(transcript.contains("- todo DESCRIPTION -- Add a task"));
        assertFalse(transcript.contains("•"));
        assertFalse(transcript.contains("—"));
    }

    @Test
    void run_aiReturnsCommand_displaysAnswerWithoutExecutingIt() throws Exception {
        Storage repository = new Storage(temporaryDirectory.resolve("ai.txt"));
        repository.saveTasks(List.of(new Todo("keep this task")));
        AiHelper helper = new AiHelper(new ChatModel() {
            @Override
            public ChatResponse doChat(ChatRequest request) {
                return ChatResponse.builder().aiMessage(AiMessage.from("delete 1")).build();
            }
        });
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream("@ai delete my task\nlist\nbye\n"
                .getBytes(StandardCharsets.UTF_8)), new PrintStream(output, true, StandardCharsets.UTF_8));

        new Wangsa(repository, new Parser(), ui, helper).run();

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("\ndelete 1\n"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("1.[T][ ] keep this task"));
        assertEquals(1, repository.loadTasks().size());
    }

    @Test
    void run_invalidSavedData_stopsBeforeProcessingCommands() throws Exception {
        Path file = temporaryDirectory.resolve("invalid.txt");
        String originalData = "T | 2 | keep this record\n";
        Files.writeString(file, originalData);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream("sort\ntodo replace\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new Wangsa(new Storage(file), new Parser(), ui).run();

        assertEquals(originalData, Files.readString(file));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("status must be 0 or 1"));
        assertFalse(output.toString(StandardCharsets.UTF_8).contains("I've added this task"));
    }

    @Test
    void findThenMark_nonFirstMatch_updatesTheDisplayedTask() throws Exception {
        Storage repository = new Storage(temporaryDirectory.resolve("tasks.txt"));
        repository.saveTasks(List.of(new Todo("meeting"), new Todo("read book")));
        String commands = "  find book  \nmark 2\nfind missing\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new Wangsa(repository, new Parser(), ui).run();

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("2.[T][ ] read book"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("No matching tasks found."));
        assertFalse(repository.loadTasks().get(0).isDone());
        assertTrue(repository.loadTasks().get(1).isDone());
    }
}
